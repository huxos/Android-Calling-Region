package kanglinstudio.assistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

/**
 * 接收网络连接广播
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
	private static final String TAG = "ConnectionChangeReceiver";

	public ConnectionChangeReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// State state = connManager.getActiveNetworkInfo().getState();
		// 获取WIFI网络连接状态
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		// 判断是否正在使用WIFI网络
		if (State.CONNECTED == state) {
			Log.d(TAG, "wifi connected");
		}
		// 获取GPRS网络连接状态
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		// 判断是否正在使用GPRS网络
		if (State.CONNECTED == state) {
			Log.d(TAG, "GPRS connected");
		} else if (State.DISCONNECTED == state) {
			Log.d(TAG, "GPRS disconnected");
		}

	}

}
