package me.huxos.checkout;

import java.util.Iterator;
import java.util.List;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBlockerSmsKeyword;
import me.huxos.checkout.entity.CBrockerlist;
import me.huxos.checkout.entity.CSystemInformation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * 防火墙拦截条件操作类
 * 
 * @author KangLin <kl222@126.com>
 * 
 */
public class CFirewallOperate {
	private static final String TAG = "CBrockerOperate";
	private Context context;

	public CFirewallOperate(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 拦截来电
	 * 
	 * @param number
	 *            ：来电号码
	 * @return 拦截成功返回true，不拦截返回false
	 */
	public boolean brockerPhone(String number) {
		DBHelper db = DBHelper.getInstance(context);
		// 检查是否打开防火墙
		CSystemInformation info = db.getSystemInformation();
		if (info.getFirewallstatus().equals("0"))
			return false;

		// 检查白名单
		CBrockerlist whitelist = db.findWhitelist(number);
		if (null != whitelist)
			if (0 != whitelist.getPhone_enable())
				return false;

		// 检查黑名单
		CBrockerlist blacklist = db.findBlacklist(number);
		if (null != blacklist && 1 == blacklist.getPhone_enable())
			return true;

		// 检查是否在通信薄中
		if (info.getInterceptionCondition() == CSystemInformation.InterceptionConditionNoContact) {

			// 检查通信薄
			if (!getContactNameFromPhoneBook(context, number))
				return true;
		}

		// 挂机
		return false;
	}

	/**
	 * 拦截短信
	 * 
	 * @param number
	 *            ：短信发送号码
	 * @return 拦截成功返回true，不拦截返回false
	 */
	public boolean brockerSMS(String number, String content) {
		DBHelper db = DBHelper.getInstance(context);
		// 检查是否打开防火墙
		CSystemInformation info = db.getSystemInformation();
		if (info.getFirewallstatus().equals("0"))
			return false;
		// 检查白名单
		CBrockerlist whitelist = db.findWhitelist(number);
		if (null != whitelist)
			if (0 != whitelist.getSms_enable())
				return false;
		// 检查关键字白名单
		if (checkKeywordList(number, content, true))
			return false;
		// 检查关键字黑名单
		if (checkKeywordList(number, content, false))
			return true;

		// 检查黑名单
		CBrockerlist blacklist = db.findBlacklist(number);
		if (null == blacklist)
			return false;
		if (0 == blacklist.getSms_enable())
			return false;
		return true;
	}

	/**
	 * 检查短信关键字拦截
	 * 
	 * @param number
	 *            ：手机号码
	 * @param content
	 *            ：短信内容
	 * @param isWhite
	 *            ：true：白名单；false：黑名单
	 * @return
	 */
	private boolean checkKeywordList(String number, String content,
			boolean isWhite) {
		boolean bRet = false;
		DBHelper db = DBHelper.getInstance(context);
		List<CBlockerSmsKeyword> keyword = db.getAllBrockerKeyWord(isWhite,
				true);
		Iterator<CBlockerSmsKeyword> it = keyword.iterator();
		while (it.hasNext()) {
			CBlockerSmsKeyword key = it.next();
			if (key.getEnable() == 0)
				continue;
			if (!content.contains(key.getKeyword()))
				continue;
			if (!key.getPhone_number().isEmpty()
					&& !key.getPhone_number().equals(number))
				continue;
			return true;
		}
		return bRet;
	}

	/**
	 * BUG:检查电话号码是否存在电话薄中
	 * 
	 * @param context
	 * @param phoneNum
	 *            ：要查询的号码
	 * @return：true：存在；false：不存在
	 */
	private boolean getContactNameFromPhoneBook(Context context, String phoneNum) {
		boolean bRet = false;
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
					new String[] { phoneNum }, null);
			if (cursor.moveToFirst()) {
				bRet = true;
			}
		} catch (Exception e) {
			Log.e(TAG, "getContactNameFromPhoneBook exception", e);
		} finally {
			if (null != cursor)
				cursor.close();
		}
		return bRet;
	}
}
