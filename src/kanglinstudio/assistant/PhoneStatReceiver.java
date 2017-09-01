package kanglinstudio.assistant;

import java.lang.reflect.Method;
import java.util.Date;

import com.android.internal.telephony.ITelephony;

import kanglinstudio.assistant.db.DBHelper;
import kanglinstudio.assistant.entity.CBlockerPhoneLog;
import kanglinstudio.assistant.entity.CSystemInformation;
import kanglinstudio.assistant.entity.PhoneArea;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 接收来去电广播
 * 
 * @author hy511
 * 
 */
public class PhoneStatReceiver extends BroadcastReceiver {
	private static final String TAG = "PhoneStatReceiver";
	private static WindowManager m_wm;
	private static TextView m_tv;
	private boolean view_area;
	private boolean view_area_call_in;
	private boolean view_area_call_out;
	private static boolean incomingFlag = false;
	private static String incoming_number = null;
	private CSystemInformation m_info;
	private DBHelper m_db;

	@Override
	public void onReceive(Context context, Intent intent) {

		// 获取当前的界面
		if (null == m_wm)
			m_wm = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
		// 设置显示文本框
		if (null == m_tv) {
			m_tv = new TextView(context);
			m_tv.setTextSize(25);
			m_tv.setVisibility(TextView.INVISIBLE);
		}

		// 显示归属地
		view_area = PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("view_area", true);
		// 去电时显示归属地
		view_area_call_out = PreferenceManager.getDefaultSharedPreferences(
				context).getBoolean("view_area_call_out", true);
		// 来电时显示归属地
		view_area_call_in = PreferenceManager.getDefaultSharedPreferences(
				context).getBoolean("view_area_call_in", true);

		// 得到数据库
		if (null == m_db)
			m_db = DBHelper.getInstance(context);
		m_info = m_db.getSystemInformation();

		if (view_area && (view_area_call_in || view_area_call_out)) {
			// 拨打电话
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				incomingFlag = false;
				String phoneNumber = intent
						.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				// 去掉非数字字符
				phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
				Log.i(TAG,
						"view_area_call_out:"
								+ String.valueOf(view_area_call_out)
								+ "; CALL OUT: " + phoneNumber);

				// 拦截
				if (m_info.getInterceptionDirection() == CSystemInformation.InterceptionDirectionOutgoing
						|| m_info.getInterceptionDirection() == CSystemInformation.InterceptionDirectionDouble) {
					CFirewallOperate firewallOperate = new CFirewallOperate(
							context);
					if (firewallOperate.brockerPhone(phoneNumber)) {
						brockerPhone(context, phoneNumber, false);
						return;
					}
				}

				if (view_area_call_out)
					new ShowArea(context).execute(phoneNumber);

			} else {
				// 来电
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);

				switch (tm.getCallState()) {
				case TelephonyManager.CALL_STATE_RINGING: // 电话等待接听
					incomingFlag = true;
					incoming_number = intent
							.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
					// 去掉非数字字符（可能会包含空格）
					incoming_number = incoming_number.replaceAll("[^0-9]", "");
					Log.i(TAG,
							"view_area_call_in:"
									+ String.valueOf(view_area_call_in)
									+ "; CALL IN RINGING :" + incoming_number);

					// 区域显示
					if (view_area_call_in)
						new ShowArea(context).execute(incoming_number);

					if ((m_info.getInterceptionDirection() == CSystemInformation.InterceptionDirectionIncoming || m_info
							.getInterceptionDirection() == CSystemInformation.InterceptionDirectionDouble)
							&& m_info.getInterceptionType() != CSystemInformation.InterceptioinTypeNo) {
						// 来电拦截
						CFirewallOperate firewallOperate = new CFirewallOperate(
								context);
						if (firewallOperate.brockerPhone(incoming_number)) {
							AudioManager mAudioManager = (AudioManager) context
									.getSystemService(Context.AUDIO_SERVICE);
							mAudioManager
									.setRingerMode(AudioManager.RINGER_MODE_SILENT);// 静音处理

							brockerPhone(context, incoming_number, true);
						}
					}
					break;

				case TelephonyManager.CALL_STATE_OFFHOOK: // 电话摘机
					if (incomingFlag) {
						Log.i(TAG, "CALL IN ACCEPT :" + incoming_number);
					}
					break;

				case TelephonyManager.CALL_STATE_IDLE: // 电话挂机
					Log.i(TAG, "CALL IDLE");
					// 再恢复正常铃声
					AudioManager mAudioManager = (AudioManager) context
							.getSystemService(Context.AUDIO_SERVICE);
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					try {
						if (null != m_wm && null != m_tv) {
							m_wm.removeView(m_tv);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						m_tv = null;
					}
					break;
				}
			}
		}
	}

	/**
	 * 拦截电话操作
	 * 
	 * @param context
	 */
	private void brockerPhone(Context context, String number, boolean bIncoming) {
		if (m_info.getInterceptionType() == CSystemInformation.InterceptionTypeNormal) {
			// 挂机
			if (bIncoming)
				endCall(context);
			else
				setResultData(null); // 清除电话，广播被传给系统的接收者后，因为电话为null，取消电话拔打
		}
		// 写入拦截日志
		Date d = new Date();
		CBlockerPhoneLog log = new CBlockerPhoneLog(number, d.getTime());
		m_db.insertBlockerPhoneLog(log);
	}

	/**
	 * 挂机
	 * 
	 * @return
	 */
	private boolean endCall(Context context) {
		boolean bRet = false;

		try {
			TelephonyManager telMgr = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);
			Class<TelephonyManager> c = TelephonyManager.class;
			Method getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
			ITelephony iTelephony = null;
			Log.d(TAG, "End call.");

			iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr,
					(Object[]) null);
			bRet = iTelephony.endCall();
		} catch (Exception e) {
			Log.e(TAG, "Fail to answer ring call.", e);
		} finally {

		}
		return bRet;
	}

	/**
	 * 异步任务
	 * 
	 * @author hy511
	 * 
	 */
	class ShowArea extends AsyncTask<String, Void, TextView> {

		public ShowArea(Context context) {
		}

		@Override
		protected TextView doInBackground(String... param) {
			if (null == m_tv)
				return null;
			try {
				// 构建显示内容
				String incomingNumber = param[0];
				Log.d(TAG, "Number:" + incomingNumber);
				PhoneArea phoneArea;
				if ((incomingNumber != null && incomingNumber.length() >= 7)
						&& ((phoneArea = m_db.findPhoneArea((incomingNumber)
								.substring(0, 7))) != null)) {
					m_tv.setText(phoneArea.getArea());
				} else {
					m_tv.setText(R.string.none_area);
				}
			} catch (Exception e) {
				Log.e(TAG, "doInBackground exception", e);
			}
			return m_tv;
		}

		@Override
		protected void onPostExecute(TextView textView) {

			// 构造显示参数
			WindowManager.LayoutParams params = new WindowManager.LayoutParams();

			// 在所有窗体之上
			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

			// 设置失去焦点，不能被点击
			params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			// 高度宽度
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			// 透明
			params.format = PixelFormat.RGBA_8888;
			// 显示
			if (null != m_wm && null != m_tv) {
				m_tv.setVisibility(TextView.VISIBLE);
				m_wm.addView(m_tv, params);

			}
		}
	}
}
