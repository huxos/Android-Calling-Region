package kanglinstudio.assistant;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.format.Time;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityLocaleGps extends Activity {

	TextView m_txtStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_locale_gps);
		m_txtStatus = (TextView) this.findViewById(R.id.txtLocalGpsStatus);
		openGPSSettings();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_locale_gps, menu);
		return true;
	}

	private void openGPSSettings() {
		LocationManager alm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, getString(R.string.gps_is_ok),
					Toast.LENGTH_SHORT).show();
			getLocation();
			return;
		} else {
			Toast.makeText(this, getString(R.string.please_open_gps),
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Settings.ACTION_SETTINGS);
			startActivityForResult(intent,
					R.layout.activity_activity_locale_gps); // 此为设置完成后返回到获取界面
			getLocation();
			return;
		}
	}

	LocationManager locationManager;

	private void getLocation() {
		// 获取位置管理服务
		String serviceName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) this.getSystemService(serviceName);
		// 查找到服务信息
		// Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// // 高精度
		// criteria.setAltitudeRequired(false);
		// criteria.setBearingRequired(false);
		// criteria.setCostAllowed(true);
		// criteria.setPowerRequirement(Criteria.POWER_LOW);
		// // 低功耗
		// String provider = locationManager.getBestProvider(criteria, true);
		// 获取GPS信息
		String provider = LocationManager.GPS_PROVIDER;

		Location location = locationManager.getLastKnownLocation(provider);// 通过GPS获取位置

		if (location == null)
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		updateToNewLocation(location);
		// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000)或最小位移变化超过N米
		locationManager.requestLocationUpdates(provider, 1000, 0,
				locationListener);
		locationManager.addGpsStatusListener(statusListener); // 注册状态信息回调
	}

	private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号
	/**
	 * 卫星状态监听器
	 */
	private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
			GpsStatus status = locationManager.getGpsStatus(null); // 取当前状态
			updateGpsStatus(event, status);
		}
	};

	private void updateGpsStatus(int event, GpsStatus status) {
		if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
			int maxSatellites = status.getMaxSatellites();
			Iterator<GpsSatellite> it = status.getSatellites().iterator();
			numSatelliteList.clear();
			int count = 0;
			while (it.hasNext() && count <= maxSatellites) {
				GpsSatellite s = it.next();
				numSatelliteList.add(s);
				count++;
			}

		}
	}

	/**
	 * location.getAccuracy(); 精度 location.getAltitude(); 高度 : 海拔
	 * location.getBearing(); 导向 location.getSpeed(); 速度 location.getLatitude();
	 * 纬度 location.getLongitude(); 经度 location.getTime(); UTC时间 以毫秒计
	 */
	private void updateToNewLocation(Location location) {
		// 获取系统时间
		Time t = new Time();
		t.setToNow(); // 取得系统时间
		int year = t.year;
		int month = t.month + 1;
		int date = t.monthDay;
		int hour = t.hour; // 24小时制
		int minute = t.minute;
		int second = t.second;
		TextView tv1;
		tv1 = (TextView) this.findViewById(R.id.txtLocalGpsDisplay);
		if (location != null) {
			double latitude = location.getLatitude();// 经度
			double longitude = location.getLongitude();// 纬度
			double altitude = location.getAltitude(); // 海拔
			tv1.setText("搜索卫星个数：" + numSatelliteList.size() + "/n纬度："
					+ latitude + "/n经度：" + longitude + "/n海拔：" + altitude
					+ "/n时间：" + year + "年" + month + "月" + date + "日" + hour
					+ ":" + minute + ":" + second);

		} else {

			tv1.setText(getString(R.string.Unable_to_get_geographic_information));
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
			if (location != null) {
				updateToNewLocation(location);
				Toast.makeText(ActivityLocaleGps.this,
						getString(R.string.position_has_changed),
						Toast.LENGTH_SHORT).show();
			}
		}

		public void onProviderDisabled(String provider) {
			m_txtStatus.setText(getString(R.string.gps_is_disabled));
			// Provider被disable时触发此函数，比如GPS被关闭
			updateToNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
			m_txtStatus.setText(getString(R.string.gps_is_enabled));
			// Provider被enable时触发此函数，比如GPS被打开
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
			m_txtStatus.setText(getString(R.string.provider_changed) + ":" + provider);
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {// 拦截menu键事件
			// do something...
			finish();// 结束之后会将结果传回From

		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {// 拦截返回按钮事件
			// do something...
			finish();// 结束之后会将结果传回From
		}
		return true;
	}
}