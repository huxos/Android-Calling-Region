package me.huxos.checkout.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import me.huxos.checkout.entity.PhoneArea;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	public static final String DB_PATH = "/data/data/me.huxos.checkout/databases/";
	private static final String DB_NAME = "location.db";

	/**
	 * 单例模式
	 * @param context
	 */
	private DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		try {
			db = getWritableDatabase();
		} catch (Exception e) {
			db = getReadableDatabase();
		}

	}

	/**
	 * @param context
	 * @return
	 */
	public static synchronized DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// db.execSQL("create table phone_location (_id INTEGER primary key,location varchar(32) not null)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * 复制数据库文件到软件目录
	 * @param context
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
	 * 查询号码归属地
	 * @param args
	 * @return
	 */
	public PhoneArea findPhoneArea(String... args) {

		Cursor c = null;
		PhoneArea phoneArea = null;
		try {
			c = db.rawQuery("select * from phone_location where rowid = ?",
					args);
			if (c.getCount() == 1) {
				c.moveToNext();
				Integer id = c.getInt(c.getColumnIndex("_id"));
				String area = c.getString(c.getColumnIndex("area"));
				phoneArea = new PhoneArea(id, area);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (c != null)
				c.close();
		}
		return phoneArea;

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