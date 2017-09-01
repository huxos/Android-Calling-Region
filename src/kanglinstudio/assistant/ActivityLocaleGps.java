package kanglinstudio.assistant;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import kanglinstudio.assistant.db.DBHelper;
import kanglinstudio.assistant.entity.CPositionInfo;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityLocaleGps extends Activity {
	private static final String TAG = "ActivityLocaleGps";
	private TextView m_txtStatus;
	private TextView m_txtDisplay;
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_locale_gps);
		m_txtStatus = (TextView) findViewById(R.id.txtLocalGpsStatus);
		m_txtStatus.setText("");
		m_txtDisplay = (TextView) findViewById(R.id.txtLocalGpsDisplay);
		m_txtDisplay.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_locale_gps, menu);
		return true;
	}

	public void onOpenClick(View source) {
		Button btn = (Button) this.findViewById(R.id.btnLocalGpsOpen);
		if (null == locationManager) {
			openGPSSettings();
			btn.setText(getString(R.string.close));
		} else {
			closeGps();
			btn.setText(getString(R.string.open));
			locationManager = null;
			m_txtDisplay.setText("");
		}
	}

	private void openGPSSettings() {
		LocationManager alm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			m_txtStatus.setText(getString(R.string.gps_is_ok));
			getLocation();
			return;
		} else {
			m_txtStatus.setText(getString(R.string.please_open_gps));
			Intent intent = new Intent(Settings.ACTION_SETTINGS);
			startActivityForResult(intent,
					R.layout.activity_activity_locale_gps); // 此为设置完成后返回到获取界面
			getLocation();
			return;
		}
	}

	private void closeGps() {
		if (null != locationManager)
			locationManager.removeGpsStatusListener(statusListener);
	}

	private void getLocation() {
		// 获取位置管理服务
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
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

		// 显示所有提供者
		// List<String> providers = locationManager.getAllProviders();
		// for(Iterator iterator = providers.iterator();iterator.hasNext();){
		// String provider = (String)iterator.next();
		// System.out.println(provider);
		// and do something you need
		// }

		String provider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);// 通过GPS获取位置
		if (location == null){
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			m_txtStatus.setText(getString(R.string.use_network_provider));
		}
		//updateToNewLocation(location);

		EditText minTime = (EditText) this
				.findViewById(R.id.edtLocalGpsMinTime);
		EditText minDistance = (EditText) this
				.findViewById(R.id.edtLocalGpsMinDistance);
		long nMinTime = Integer.parseInt(minTime.getText().toString());
		float nMinDistance = Float.parseFloat(minDistance.getText().toString());
		if (nMinTime < 1) {
			nMinTime = 1;
			minTime.setText(String.valueOf(nMinTime));
		}
		if (nMinDistance < 0) {
			nMinDistance = 0;
			minDistance.setText(String.valueOf(nMinDistance));
		}
		// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000)或最小位移变化超过N米
		locationManager.requestLocationUpdates(provider, nMinTime * 1000,
				nMinDistance, locationListener);
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

	private void updateToNewLocation(Location location) {
		// 获取系统时间
		TextView txtMsg;
		txtMsg = (TextView) this.findViewById(R.id.txtLocalGpsDisplay);
		if (null == location) {
			txtMsg.setText(getString(R.string.Unable_to_get_geographic_information));
			return;
		}
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		Date d = new Date();
		CPositionInfo pos = new CPositionInfo(latitude, longitude, d.getTime(),
				location.getTime());
		if (location.hasAccuracy())
			pos.setAccuracy(location.getAccuracy());// 精度（单位：米）
		if (location.hasAltitude())
			pos.setAltitude(location.getAltitude());// 海拔(单位:米)
		if (location.hasBearing())
			pos.setBearing(location.getBearing()); // 方位（单位：角度）
		if (location.hasSpeed())
			pos.setSpeed(location.getSpeed());// 速度（单位：米/秒）
		pos.setSatellite_number(numSatelliteList.size());
		txtMsg.setText(pos.toString(this.getBaseContext()));
		DBHelper db = DBHelper.getInstance(getBaseContext());
		db.insertPositionInfo(pos);
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
			if (location != null) {
				updateToNewLocation(location);
				m_txtStatus.setText(getString(R.string.position_has_changed));
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
			m_txtStatus.setText(getString(R.string.provider_changed) + ":"
					+ provider);
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