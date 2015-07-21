package kanglinstudio.assistant.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;

import kanglinstudio.assistant.R;

public class CPositionInfo {
	private Integer no;
	private String userid;
	private String deviceid;
	private long systime;// 系统时间
	private long gpstime;// gps 时间(utc)
	private float accuracy;// 精度（单位：米）
	private float bearing; // 方位（单位：角度）
	private float speed;// 速度（单位：米/秒）
	private double latitude;// 经度
	private double longitude;// 纬度
	private double altitude;// 海拔(单位:米)
	private Integer satellite_number;// 卫星个数
	private Integer state;// 状态:1：活动；0：禁止

	public CPositionInfo(double latitude, double longitude, long systime,
			long gpstime) {
		init();
		this.latitude = latitude;
		this.longitude = longitude;
		this.systime = systime;
		this.gpstime = gpstime;
	}

	public CPositionInfo() {
		init();
	}

	private void init() {
		userid = "";
		deviceid = "";
		systime = 0;
		gpstime = 0;
		accuracy = 0;
		bearing = 0;
		speed = 0;
		latitude = 0;
		longitude = 0;
		altitude = 0;
		satellite_number = 0;
		state = 1;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public long getSystime() {
		return systime;
	}

	public void setSystime(long systime) {
		this.systime = systime;
	}

	public long getGpstime() {
		return gpstime;
	}

	public void setGpstime(long gpstime) {
		this.gpstime = gpstime;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public float getBearing() {
		return bearing;
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public Integer getSatellite_number() {
		return satellite_number;
	}

	public void setSatellite_number(Integer satellite_number) {
		this.satellite_number = satellite_number;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String toString(Context context) {
		String szMsg = context.getString(R.string.systime)
				+ utc2Local(systime, "yyyy-MM-dd HH:mm:ss.SSSZ") + "\n"
				+ context.getString(R.string.gpstime)
				+ utc2Local(gpstime, "yyyy-MM-dd HH:mm:ss.SSSZ") + "\n"
				+ context.getString(R.string.satellite_number)
				+ satellite_number + "\n"
				+ context.getString(R.string.accuracy) + accuracy
				+ context.getString(R.string.meters) + "\n"
				+ context.getString(R.string.latitude) + latitude
				+ context.getString(R.string.degrees) + "\n"
				+ context.getString(R.string.longitude) + longitude
				+ context.getString(R.string.degrees) + "\n"
				+ context.getString(R.string.bearing) + bearing
				+ context.getString(R.string.degrees) + "\n"
				+ context.getString(R.string.altitude) + altitude
				+ context.getString(R.string.meters) + "\n"
				+ context.getString(R.string.speed) + speed
				+ context.getString(R.string.meters_second);
		return szMsg;
	}

	@SuppressLint("SimpleDateFormat")
	public static String utc2Local(long utcTime, String localTimePatten) {
		Date gpsUTCDate = new Date(utcTime);
		SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
		localFormater.setTimeZone(TimeZone.getDefault());
		String localTime = localFormater.format(gpsUTCDate.getTime());
		return localTime;
	}
}
