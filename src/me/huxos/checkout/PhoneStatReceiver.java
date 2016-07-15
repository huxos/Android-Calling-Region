package me.huxos.checkout;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.cabe.lib.cache.CacheSource;
import com.cabe.lib.cache.interactor.impl.SimpleViewPresenter;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.PhoneArea;
import me.huxos.checkout.usecase.DBLocationUseCase;

/**
 * 接收来去电广播
 * @author hy511
 *
 */
public class PhoneStatReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneStatReceiver";
	private static WindowManager wm;
	private static TextView tv;
	private static boolean incomingFlag = false;

	private static String incoming_number = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		//显示归属地
		boolean view_area = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("view_area", true);
		//去电时显示归属地
		boolean view_area_call_out = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("view_area_call_out", true);
		//来电时显示归属地
		boolean view_area_call_in = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("view_area_call_in", true);

		Log.w(TAG, "onReceive:" + intent.getAction());
		if (view_area && (view_area_call_in || view_area_call_out)) {
			//获取当前的界面
			if(wm == null) {
				wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			}

			// 拨打电话
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				incomingFlag = false;
				String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				// 去掉非数字字符
				phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
				Log.i(TAG, "view_area_call_out:" + String.valueOf(view_area_call_out) + "; CALL OUT: " + phoneNumber);
				if (view_area_call_out)
					showArea(context, phoneNumber);
			} else {
				// 来电
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

				switch (tm.getCallState()) {
				//电话等待接听
				case TelephonyManager.CALL_STATE_RINGING:
					incomingFlag = true;
					incoming_number = intent.getStringExtra("incoming_number");
					// 去掉非数字字符
					incoming_number = incoming_number.replaceAll("[^0-9]", "");
					Log.i(TAG,  "view_area_call_in:" + String.valueOf(view_area_call_in) + "; CALL IN RINGING :" + incoming_number);
					if (view_area_call_in)
						showArea(context, incoming_number);
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
						if (wm != null && tv != null) {
							wm.removeView(tv);
							tv = null;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	private void showArea(final Context context, String phoneNum) {
		if(tv == null) {
			tv = new TextView(context);
			tv.setPadding(30, 50, 50, 30);
			tv.setTextSize(20);
			tv.setTextColor(0xFFFFFFFF);
			tv.setBackgroundColor(0x66000000);
		}

		DBHelper helper = DBHelper.getInstance(context);
		DBLocationUseCase useCase = new DBLocationUseCase(helper, phoneNum);
		useCase.execute(new SimpleViewPresenter<PhoneArea>() {
			private PhoneArea phoneArea;
			@Override
			public void load(CacheSource from, PhoneArea data) {
				this.phoneArea = data;
			}
			@Override
			public void error(CacheSource from, int code, String info) {
				super.error(from, code, info);
				Log.w("MainActivity", "error:" + info);
			}
			@Override
			public void complete(CacheSource from) {
				if(tv == null) return;

				//构造显示参数
				WindowManager.LayoutParams params = new WindowManager.LayoutParams();

				//在所有窗体之上
				params.type = WindowManager.LayoutParams.TYPE_TOAST;
				params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				params.y = 150;

				//设置失去焦点，不能被点击
				params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
				//高度宽度
				params.width = WindowManager.LayoutParams.WRAP_CONTENT;
				params.height = WindowManager.LayoutParams.WRAP_CONTENT;
				//透明
				params.format = PixelFormat.RGBA_8888;
				//显示
				wm.addView(tv, params);

				String locationLocation = context.getString(R.string.none_area);
				if(phoneArea != null) {
					locationLocation = phoneArea.getArea();
				}
				tv.setText(locationLocation);
			}
		});
	}
}