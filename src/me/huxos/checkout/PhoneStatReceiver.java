package me.huxos.checkout;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.PhoneArea;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 接收来去电广播
 * @author hy511
 *
 */
public class PhoneStatReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneStatReceiver";
	private static WindowManager wm;
	private static DBHelper helper;
	private static TextView tv;
	private boolean view_area;
	private boolean view_area_call_in;
	private boolean view_area_call_out;
	private static boolean incomingFlag = false;

	private static String incoming_number = null;

	@Override
	public void onReceive(Context context, Intent intent) {

		//显示归属地
		view_area = PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("view_area", true);
		//去电时显示归属地
		view_area_call_out = PreferenceManager.getDefaultSharedPreferences(
				context).getBoolean("view_area_call_out", true);
		//来电时显示归属地
		view_area_call_in = PreferenceManager.getDefaultSharedPreferences(
				context).getBoolean("view_area_call_in", true);
		if (view_area && (view_area_call_in || view_area_call_out)) {
			// 拨打电话
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				incomingFlag = false;
				String phoneNumber = intent
						.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				Log.i(TAG, "CALL OUT: " + phoneNumber);
				if (view_area_call_out)
					new ShowArea(context).execute(phoneNumber);
			} else {
				// 来电
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);

				switch (tm.getCallState()) {
				//电话等待接听
				case TelephonyManager.CALL_STATE_RINGING:
					incomingFlag = true;
					incoming_number = intent.getStringExtra("incoming_number");
					Log.i(TAG, "CALL IN RINGING :" + incoming_number);
					if (view_area_call_in)
						new ShowArea(context).execute(incoming_number);
					break;
				//电话摘机
				case TelephonyManager.CALL_STATE_OFFHOOK:
					if (incomingFlag) {
						Log.i(TAG, "CALL IN ACCEPT :" + incoming_number);
					}
					break;
				//电话挂机
				case TelephonyManager.CALL_STATE_IDLE:
					Log.i(TAG, "CALL IDLE");
					try {
						if (wm != null)
							wm.removeView(tv);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	/**
	 * 异步任务
	 * @author hy511
	 *
	 */
	class ShowArea extends AsyncTask<String, Void, TextView> {

		private Context context;

		public ShowArea(Context context) {
			this.context = context;
		}

		@Override
		protected TextView doInBackground(String... param) {
			//构建显示内容
			tv = new TextView(context);
			tv.setTextSize(25);
			//得到连接
			helper = DBHelper.getInstance(context);
			String incomingNumber = param[0];
			PhoneArea phoneArea;
			if ((incomingNumber != null && incomingNumber.length() >= 7)
					&& ((phoneArea = helper.findPhoneArea((incomingNumber)
							.substring(0, 7))) != null)) {
				tv.setText(phoneArea.getArea());
			} else {
				tv.setText(R.string.none_area);
			}
			return tv;
		}

		@Override
		protected void onPostExecute(TextView textView) {
			//获取当前的界面
			wm = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
			//构造显示参数
			WindowManager.LayoutParams params = new WindowManager.LayoutParams();
			
			//在所有窗体之上
			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
			
			//设置失去焦点，不能被点击
			params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			//高度宽度
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			//透明
			params.format = PixelFormat.RGBA_8888;
			//显示
			wm.addView(tv, params);
		}
	}

}
