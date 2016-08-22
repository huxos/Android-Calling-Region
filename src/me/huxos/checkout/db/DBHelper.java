package me.huxos.checkout.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import me.huxos.checkout.entity.PhoneArea;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * @author hy511
 *
 */
@SuppressLint("SdCardPath")
public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = "DATABASE";
	public static DBHelper instance;
	private SQLiteDatabase db = null;

	private static final Integer DB_VERSION = 1;
	private static final Integer DB_VERSION_SERVICE = DB_VERSION + 3;
	public static final String DB_PATH = "/data/data/me.huxos.checkout/databases/";
	private static final String DB_NAME = "location.db";

	public final static String TABLE_SERVICE = "phone_service";
	public final static String SERVICE_COLUMN_NAME = "service_name";
	public final static String SERVICE_COLUMN_NUMBER = "service_number";
	private final static String SQL_TABLE_SERVICE = "create table if not exists " + TABLE_SERVICE + " (" +
			SERVICE_COLUMN_NUMBER + " char(20)," +
			SERVICE_COLUMN_NAME + " char(128)" +
			")";

	private DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION_SERVICE);
		try {
			db = getWritableDatabase();
		} catch (Exception e) {
			db = getReadableDatabase();
		}
	}

	public static synchronized DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_TABLE_SERVICE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion == DB_VERSION_SERVICE) {
			Log.w("DBHelper", "onUpgrade:" + SQL_TABLE_SERVICE);
			db.execSQL("drop table " + TABLE_SERVICE);
			db.execSQL(SQL_TABLE_SERVICE);
		}
	}

	/**
	 * 复制数据库文件到软件目录
	 */
	public static void copyDB(Context context) {

		boolean dBExists = new File(DB_PATH + DB_NAME).exists();
		if (!dBExists) {
			Log.i(TAG, "DATABASE: NOT EXISTS ");
			File directory = new File(DB_PATH);
			if (!directory.exists())
				directory.mkdir();
			try {
				Log.i(TAG, "DATABASE: COPYING .. ");
				InputStream is = context.getAssets().open(DB_NAME);
				OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新或者保存
	 * @param phoneArea
	 * @return
	 */
	public boolean saveOrUpdatePhoneArea(PhoneArea phoneArea) {
		ContentValues addToDB = new ContentValues();
		addToDB.put("_id", phoneArea.get_id());
		addToDB.put("area", phoneArea.getArea());
		long count = db.update("phone_location", addToDB, "_id = ?",
				new String[] { phoneArea.get_id().toString() });
		if (count != 1)
			db.insert("phone_location", null, addToDB);
		return count == 1;

	}

}