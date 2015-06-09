package me.huxos.checkout.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.huxos.checkout.entity.CBlockerPhoneLog;
import me.huxos.checkout.entity.CBlockerSMSLog;
import me.huxos.checkout.entity.CBlockerSMSLogs;
import me.huxos.checkout.entity.CBlockerSmsKeyword;
import me.huxos.checkout.entity.CBrockerlist;
import me.huxos.checkout.entity.CSystemInformation;
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
	 * 
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
			// 初次使用将准备好的数据库文件考入到系统目录共程序使用
			DBHelper.copyDB(context);
			instance = new DBHelper(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate");
		createDatabase(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade");
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		Log.d(TAG, "onOpen");

		createDatabase(db);

		super.onOpen(db);
	}

	private boolean createDatabase(SQLiteDatabase db) {
		try {
			// 电话区域查询表
			db.execSQL("create table  if not exists phone_location (_id INTEGER primary key,location varchar(32) not null)");
			// 白名单
			db.execSQL("create table  if not exists whitelist(phone_number text primary key, name text, phone_enable integer, sms_enable integer)");
			// 黑名单
			db.execSQL("create table  if not exists blacklist(phone_number text primary key, name text, phone_enable integer, sms_enable integer)");
			/* 短信关键字白名单 */
			db.execSQL("create table  if not exists sms_keyword_whitelist(no integer primary key, keyword text, phone_number text, enable integer)");
			/* 短信关键字黑名单 */
			db.execSQL("create table  if not exists sms_keyword_blacklist(no integer primary key, keyword text, phone_number text, enable integer)");
			// 拦截电话日志
			db.execSQL("create table  if not exists blocker_phone_log(no integer primary key, phone_number text, time integer)");
			// 拦截短信日志
			db.execSQL("create table  if not exists blocker_sms_log(no integer primary key, phone_number text, time integer, content text, isread integer)");
			// 系统信息
			db.execSQL("create table  if not exists system_infomation(key text primary key, value text)");
		} catch (Exception e) {
			Log.e(TAG, "createDatabase Exception:" + e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * 判断指定的表是否已经存在在数据库中
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param tableName
	 *            ：需要判断的表名
	 * @return 存在返回true；否则返回false
	 */
	/*
	 * private boolean tabbleIsExist(String tableName) { boolean result = false;
	 * if (tableName == null) { return false; } // SQLiteDatabase db = null;
	 * Cursor cursor = null; try { // db = this.getReadableDatabase(); String
	 * sql =
	 * "select count(*) as c from sqlite_master where type ='table' and name ='"
	 * + tableName.trim() + "' "; cursor = db.rawQuery(sql, null); if
	 * (cursor.moveToNext()) { int count = cursor.getInt(0); if (count > 0) {
	 * result = true; } }
	 * 
	 * } catch (Exception e) { Log.e(TAG, "onOpen Exception:" + e.getMessage(),
	 * e); } ; return result; }
	 */

	/**
	 * 复制数据库文件到软件目录
	 * 
	 * @param context
	 */
	private static void copyDB(Context context) {
		Log.d(TAG, "copyDB");
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
	 * 
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
				Log.d(TAG, "find:" + args[0] + ";area:" + area);
			} else {
				Log.e(TAG, "Don't find:" + args[0]);
			}
		} catch (Exception e) {
			Log.e(TAG, "findPhoneArea exception:" + e.getMessage());
		} finally {
			if (c != null)
				c.close();
		}
		return phoneArea;

	}

	/**
	 * 更新或者保存
	 * 
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

	/**
	 * 查询拦截名单
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param number
	 *            :要查询的号码
	 * @param isWhite
	 *            :true,在白名单中查询,false,在黑名单中查询
	 * @return CBrockerlist 实例
	 */
	private CBrockerlist findBrockerList(String number, boolean isWhite) {
		CBrockerlist brockerList = null;
		String szTable = "blacklist";
		if (isWhite)
			szTable = "whitelist";
		String szSql = "select * from " + szTable + " where phone_number = "
				+ number;
		Cursor c = null;
		try {
			c = db.rawQuery(szSql, null);
			if (c.getCount() == 1) {
				c.moveToNext();
				brockerList = new CBrockerlist(c.getString(c
						.getColumnIndex("phone_number")), c.getString(c
						.getColumnIndex("name")), c.getInt(c
						.getColumnIndex("phone_enable")), c.getInt(c
						.getColumnIndex("sms_enable")));
			} else {
				String szMsg = "Don't find " + number;
				if (isWhite)
					szMsg += " in whitelist";
				else
					szMsg += " in blacklist";
				szMsg += "; count:" + String.valueOf(c.getCount());
				Log.d(TAG, szMsg);
			}
		} catch (Exception e) {
			Log.e(TAG, "findBrockerList exception:" + e.getMessage());
		} finally {
			if (null != c)
				c.close();
		}
		return brockerList;
	}

	/**
	 * 查询白名单
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param number
	 *            :需要查询的电话号码
	 * @return CBrockerlist 实例
	 */
	public CBrockerlist findWhitelist(String number) {
		return findBrockerList(number, true);
	}

	/**
	 * 查询黑名单
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param number
	 *            :需要查询的电话号码
	 * @return CBrockerlist 实例
	 */
	public CBrockerlist findBlacklist(String number) {
		return findBrockerList(number, false);
	}

	/**
	 * 查询所有拦截名单
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param isWhite
	 *            :true,在白名单中查询,false,在黑名单中查询
	 * @return
	 */
	public List<CBrockerlist> getAllBrockerList(boolean isWhite) {
		List<CBrockerlist> lstRet = new ArrayList<CBrockerlist>();
		String szTable = "blacklist";
		if (isWhite)
			szTable = "whitelist";
		String szSql = "select * from " + szTable;
		Cursor c = null;
		try {
			c = db.rawQuery(szSql, null);
			while (c.moveToNext()) {
				CBrockerlist brockerlist = new CBrockerlist(c.getString(c
						.getColumnIndex("phone_number")), c.getString(c
						.getColumnIndex("name")), c.getInt(c
						.getColumnIndex("phone_enable")), c.getInt(c
						.getColumnIndex("sms_enable")));
				lstRet.add(brockerlist);
				Log.d(TAG,
						"findAllBrockerList:name:"
								+ c.getString(c.getColumnIndex("name"))
								+ ";number:"
								+ c.getString(c.getColumnIndex("phone_number")));
			}
		} catch (Exception e) {
			Log.e(TAG, "findAllBrockerList exception:" + e.getMessage());
		} finally {
			if (null != c)
				c.close();
		}
		return lstRet;
	}

	/**
	 * 更新拦截名单
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param brockerList
	 *            ：要拦截的实体
	 * @param isWhite
	 *            ：true，白名单；false，黑名单
	 * @return
	 */
	public boolean updateBrockerList(CBrockerlist brockerList, boolean isWhite) {
		boolean bRet = false;
		String szTable = "blacklist";
		if (isWhite)
			szTable = "whitelist";
		try {
			// 更新
			ContentValues cv = new ContentValues();
			cv.put("name", brockerList.getName());
			cv.put("phone_number", brockerList.getPhone_number());
			cv.put("phone_enable", brockerList.getPhone_enable());
			cv.put("sms_enable", brockerList.getSms_enable());
			Log.d(TAG,
					"updateBrockerList:number:" + brockerList.getPhone_number()
							+ ";name:" + brockerList.getName()
							+ ";phone_enable:"
							+ String.valueOf(brockerList.getPhone_enable())
							+ ";sms_enable:"
							+ String.valueOf(brockerList.getSms_enable()));
			long nCount = db.update(szTable, cv, "phone_number=?",
					new String[] { brockerList.getPhone_number() });
			if (1 != nCount) {
				// 插入
				nCount = db.insert(szTable, null, cv);
			}
			if (0 <= nCount) {
				bRet = true;
			} else
				Log.d(TAG,
						"UpdateBrockerList fail:"
								+ brockerList.getPhone_number() + ";count:"
								+ String.valueOf(nCount));
		} catch (Exception e) {
			Log.e(TAG, "UpdateBrockerList exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 删除名单中指定的项
	 * 
	 * @param brockerList
	 *            ：要删除的实体
	 * @param isWhite
	 *            ：true，白名单；false，黑名单
	 * @return
	 */
	public boolean deleteBrockerlist(CBrockerlist brockerList, boolean isWhite) {
		boolean bRet = false;
		String szTable = "blacklist";
		if (isWhite)
			szTable = "whitelist";
		try {
			db.delete(szTable, "phone_number=?",
					new String[] { brockerList.getPhone_number() });
			bRet = true;
		} catch (Exception e) {
			Log.e(TAG, "DeleteBrockerlist exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 查询系统信息
	 * 
	 * @author KangLin <kl222@126.com>
	 * @return CSystemInformation 实例
	 */
	public CSystemInformation getSystemInformation() {
		CSystemInformation info = new CSystemInformation();
		String szSql = "select * from system_infomation";
		Cursor c = null;
		try {
			c = db.rawQuery(szSql, null);
			while (c.moveToNext()) {
				String key = c.getString(c.getColumnIndex("key"));
				String value = c.getString(c.getColumnIndex("value"));
				Log.d(TAG, "getSystemInformation:key:" + key + ";value:"
						+ value);
				if (key.equals("firewallstatus")) {
					info.setFirewallstatus(value);
				} else if (key.equals("user_name")) {
					info.setUser_name(value);
				} else if (key.equals("user_password")) {
					info.setUser_password(value);
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "findSystemInformation exception:" + e.getMessage());
		} finally {
			if (c != null)
				c.close();
		}
		return info;
	}

	/**
	 * 更新系统信息
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param info
	 * @return
	 */
	public boolean updateSystemInformation(CSystemInformation info) {
		boolean bRet = false;
		try {
			String szTable = "system_infomation";
			// 更新
			ContentValues cv = new ContentValues();
			cv.put("key", "firewallstatus");
			cv.put("value", info.getFirewallstatus());
			long count = db.update(szTable, cv, "key=?",
					new String[] { "firewallstatus" });
			if (1 != count) {
				db.insert(szTable, null, cv);
			}
			cv.clear();
			cv.put("key", "user_name");
			cv.put("value", info.getUser_name());
			count = db.update(szTable, cv, "key=?",
					new String[] { "user_name" });
			if (1 != count) {
				db.insert(szTable, null, cv);
			}
			cv.clear();
			cv.put("key", "user_password");
			cv.put("value", info.getUser_password());
			count = db.update(szTable, cv, "key=?",
					new String[] { "user_password" });
			if (1 != count) {
				db.insert(szTable, null, cv);
			}
			bRet = true;
		} catch (Exception e) {
			Log.e(TAG, "updateSystemInformation exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 插入电话拦截日志
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param log
	 *            ：拦截日志
	 * @return 成功返回true；失败返回false
	 */
	public boolean insertBlockerPhoneLog(CBlockerPhoneLog log) {
		boolean bRet = false;
		try {
			// 更新
			ContentValues cv = new ContentValues();
			cv.put("phone_number", log.getPhone_number());
			cv.put("time", log.getTime());
			Log.d(TAG,
					"insertBlockerPhoneLog:phone_number:"
							+ log.getPhone_number());
			long count = db.insert("blocker_phone_log", null, cv);
			if (-1 == count)
				Log.e(TAG,
						"insertBlockerPhoneLog fail:" + log.getPhone_number());
			else
				bRet = true;

		} catch (Exception e) {
			Log.e(TAG, "insertBlockerPhoneLog exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 查询拦截电话日志
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param arg
	 *            :查询条件
	 * @return 返回拦截日志列表
	 */
	public List<CBlockerPhoneLog> findBlockerPhoneLog(String[] condition) {
		String szSql = "select * from blocker_phone_log ";
		if (condition != null && condition.length > 0)
			szSql += " where ?";
		szSql += " ORDER BY  time  DESC";
		List<CBlockerPhoneLog> blockerPhoneLog = new ArrayList<CBlockerPhoneLog>();
		Cursor c = null;
		try {
			c = db.rawQuery(szSql, condition);
			while (c.moveToNext()) {
				Integer no = c.getInt(c.getColumnIndex("no"));
				String number = c.getString(c.getColumnIndex("phone_number"));
				long time = c.getLong(c.getColumnIndex("time"));
				Date d = new Date(time);
				Log.d(TAG, "findBlockerPhoneLog:no:" + String.valueOf(no)
						+ ";number:" + number + ";time:" + d.toString());
				CBlockerPhoneLog log = new CBlockerPhoneLog(no, number, time);
				blockerPhoneLog.add(log);
			}
		} catch (Exception e) {
			Log.e(TAG, "findBlockerPhoneLog exception:" + e.getMessage());
		} finally {
			if (c != null)
				c.close();
		}
		return blockerPhoneLog;
	}

	/**
	 * 插入拦截短信日志
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param log
	 *            ：拦截的短信日志
	 * @return 成功返回true；失败返回false
	 */
	public boolean insertBlockerSMSLog(CBlockerSMSLog log) {
		boolean bRet = false;
		try {
			// 更新
			ContentValues cv = new ContentValues();
			cv.put("phone_number", log.getPhone_number());
			cv.put("time", log.getTime());
			cv.put("content", log.getContent());
			cv.put("isread", 0);
			long count = db.insert("blocker_sms_log", null, cv);
			if (-1 == count)
				Log.e(TAG, "insertBlockerSMSLog fail:" + log.getPhone_number());
			else
				bRet = true;

		} catch (Exception e) {
			Log.e(TAG, "insertBlockerSMSLog exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 更新短信已讀狀態
	 * @param number：手機號碼
	 * @return
	 */
	public boolean updateBlockerSMSLogIsread(String number) {
		boolean bRet = false;
		try {
			// 更新
			ContentValues cv = new ContentValues();
			cv.put("isread", 1);
			long count = db.update("blocker_sms_log", cv, "phone_number=?",
					new String[] { number });
			if (-1 == count)
				Log.e(TAG, "insertBlockerSMSLog fail:" + number);
			else
				bRet = true;

		} catch (Exception e) {
			Log.e(TAG, "insertBlockerSMSLog exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 查询拦截短信
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param arg
	 *            :查询条件
	 * @return 返回拦截日志列表
	 */
	public List<CBlockerSMSLog> findBlockerSMSLog(String condition) {
		String szSql = "select * from blocker_sms_log ";
		if (condition != null)
			szSql += condition;
		szSql += " ORDER BY  time  DESC";
		List<CBlockerSMSLog> blockerSMSLog = new ArrayList<CBlockerSMSLog>();
		Cursor c = null;
		try {
			c = db.rawQuery(szSql, null);
			while (c.moveToNext()) {
				Integer no = c.getInt(c.getColumnIndex("no"));
				String number = c.getString(c.getColumnIndex("phone_number"));
				String content = c.getString(c.getColumnIndex("content"));
				long time = c.getLong(c.getColumnIndex("time"));
				Integer isRead = c.getInt(c.getColumnIndex("isread"));
				Date d = new Date(time);
				Log.d(TAG, "findBlockerSMSLog:no:" + String.valueOf(no)
						+ ";number:" + number + ";time:" + d.toString()
						+ ";content:" + content);
				CBlockerSMSLog log = new CBlockerSMSLog(no, number, content,
						time, isRead);
				blockerSMSLog.add(log);
			}
		} catch (Exception e) {
			Log.e(TAG, "findBlockerSMSLog exception:" + e.getMessage());
		} finally {
			if (c != null)
				c.close();
		}
		return blockerSMSLog;
	}

	public List<CBlockerSMSLogs> findBlockerSMSLogGroup(String condition) {
		String szSql = "select * , count(content) as c, sum(isread) as read_count from blocker_sms_log ";
		if (condition != null)
			szSql += condition;
		szSql += "  GROUP BY phone_number order by time desc";
		List<CBlockerSMSLogs> blockerSMSLogs = new ArrayList<CBlockerSMSLogs>();
		Cursor c = null;
		try {
			c = db.rawQuery(szSql, null);
			while (c.moveToNext()) {
				Integer no = c.getInt(c.getColumnIndex("no"));
				String number = c.getString(c.getColumnIndex("phone_number"));
				String content = c.getString(c.getColumnIndex("content"));
				long time = c.getLong(c.getColumnIndex("time"));
				Integer isRead = c.getInt(c.getColumnIndex("isread"));
				Integer count = c.getInt(c.getColumnIndex("c"));
				Integer unread_count = count
						- c.getInt(c.getColumnIndex("read_count"));
				Date d = new Date(time);
				Log.d(TAG,
						"findBlockerSMSLog:no:" + String.valueOf(no)
								+ ";number:" + number + ";time:" + d.toString()
								+ ";content:" + content + ";count:"
								+ String.valueOf(unread_count) + "/"
								+ String.valueOf(count));
				CBlockerSMSLogs log = new CBlockerSMSLogs(no, number, content,
						time, isRead, count, unread_count);
				blockerSMSLogs.add(log);
			}
		} catch (Exception e) {
			Log.e(TAG, "findBlockerSMSLog exception:" + e.getMessage());
		} finally {
			if (c != null)
				c.close();
		}
		return blockerSMSLogs;
	}

	/**
	 * 刪除短信攔截日志
	 * 
	 * @param condition
	 * @return
	 */
	public boolean deleteBlockerSMSLog(String condition) {
		boolean bRet = false;
		String szTable = "blocker_sms_log";

		try {
			db.delete(szTable, condition, null);
			bRet = true;
		} catch (Exception e) {
			Log.e(TAG, "deleteBlockerSMSLog exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 更新短信关键字名单
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param key
	 *            ：关键字实体
	 * @param isWhite
	 *            ：true：短信关键字白名单；false：短信关键字黑名单
	 * @return
	 */
	public boolean updateBrockerKeyWord(CBlockerSmsKeyword key, boolean isWhite) {
		boolean bRet = false;
		String szTable = "sms_keyword_blacklist";
		if (isWhite)
			szTable = "sms_keyword_whitelist";
		try {
			// 更新
			ContentValues cv = new ContentValues();
			cv.put("keyword", key.getKeyword());
			cv.put("phone_number", key.getPhone_number());
			cv.put("enable", key.getEnable());
			Log.d(TAG,
					"updateBrockerKeyWord:number:" + key.getPhone_number()
							+ ";keyword:" + key.getKeyword() + ";enable:"
							+ key.getEnable());
			long nCount = db.update(szTable, cv, "no=?",
					new String[] { String.valueOf(key.getNo()) });
			if (0 <= nCount) {
				bRet = true;
			} else
				Log.d(TAG, "updateBrockerKeyWord fail:" + key.getPhone_number()
						+ ";count:" + String.valueOf(nCount));
		} catch (Exception e) {
			Log.e(TAG, "updateBrockerKeyWord exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 插入短信关键字名单
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param key
	 *            ：关键字实体
	 * @param isWhite
	 *            ：true：短信关键字白名单；false：短信关键字黑名单
	 * @return
	 */
	public boolean insertBrockerKeyWord(CBlockerSmsKeyword key, boolean isWhite) {
		boolean bRet = false;
		String szTable = "sms_keyword_blacklist";
		if (isWhite)
			szTable = "sms_keyword_whitelist";
		try {
			// 更新
			ContentValues cv = new ContentValues();
			cv.put("keyword", key.getKeyword());
			cv.put("phone_number", key.getPhone_number());
			cv.put("enable", key.getEnable());
			Log.d(TAG,
					"insertBrockerKeyWord:number:" + key.getPhone_number()
							+ ";keyword:" + key.getKeyword() + ";enable:"
							+ key.getEnable());
			long nCount = db.insert(szTable, null, cv);
			if (-1 == nCount) {
				bRet = false;
				Log.d(TAG, "insertBrockerKeyWord fail:key:" + key.getKeyword());
			} else
				bRet = true;
		} catch (Exception e) {
			Log.e(TAG, "insertBrockerKeyWord exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 删除指定的关键字
	 * 
	 * @author KangLin <kl222@126.com>
	 * @param keyword
	 *            ：关键字
	 * @param isWhite
	 *            ：true：短信关键字白名单；false：短信关键字黑名单
	 * @return
	 */
	public boolean deleteBrockerKeyWord(CBlockerSmsKeyword keyword,
			boolean isWhite) {
		boolean bRet = false;
		String szTable = "sms_keyword_blacklist";
		if (isWhite)
			szTable = "sms_keyword_whitelist";
		try {
			db.delete(szTable, "no=?",
					new String[] { String.valueOf(keyword.getNo()) });
			bRet = true;
		} catch (Exception e) {
			Log.e(TAG, "DeleteBrockerKeyWord exception:" + e.getMessage());
		} finally {
			;
		}
		return bRet;
	}

	/**
	 * 得到所有关键字
	 * 
	 * @param isWhite
	 *            ：true：短信关键字白名单；false：短信关键字黑名单
	 * @param isEnable
	 *            :true:只得到enable项；false：得到所有的项
	 * @return
	 */
	public List<CBlockerSmsKeyword> getAllBrockerKeyWord(boolean isWhite,
			boolean isEnable) {
		List<CBlockerSmsKeyword> lstRet = new ArrayList<CBlockerSmsKeyword>();
		String szTable = "sms_keyword_blacklist";
		if (isWhite)
			szTable = "sms_keyword_whitelist";
		String szSql = "select * from " + szTable;
		if (isEnable)
			szSql += " where enable=1";
		Cursor c = null;
		try {
			c = db.rawQuery(szSql, null);
			while (c.moveToNext()) {
				CBlockerSmsKeyword brockerlist = new CBlockerSmsKeyword(
						c.getInt(c.getColumnIndex("no")), c.getString(c
								.getColumnIndex("keyword")), c.getString(c
								.getColumnIndex("phone_number")), c.getInt(c
								.getColumnIndex("enable")));
				lstRet.add(brockerlist);

				Log.d(TAG,
						"getAllBrockerKeyWord:keyword:"
								+ c.getString(c.getColumnIndex("keyword"))
								+ ";number:"
								+ c.getString(c.getColumnIndex("phone_number")));
			}
		} catch (Exception e) {
			Log.e(TAG, "getAllBrockerKeyWord exception:" + e.getMessage());
		} finally {
			if (null != c)
				c.close();
		}
		return lstRet;
	}
}