package me.huxos.checkout;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBrockerlist;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.format.DateUtils;
import android.text.format.Time;

/**
 * 公用函数
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class CTool {

	/**
	 * 从电话号码得到相应的名字
	 * 
	 * @author KangLin<kl222@126.com>
	 * @param context
	 * @param phone
	 * @return
	 */
	static public String getNameFromPhone(Context context, String phone) {
		// 从通信薄中得到
		String szName = "";
		szName = getContactNameFromPhoneBook(context, phone);
		if (szName.isEmpty()) {
			// 从黑名单中得到
			DBHelper db = DBHelper.getInstance(context);
			CBrockerlist brockerlist = db.findBlacklist(phone);
			if (null != brockerlist)
				szName = brockerlist.getName();
		}
		return szName;
	}

	/**
	 * 从通信录中得到相关号码的名字
	 * 
	 * @author KangLin<kl222@126.com>
	 * @param number
	 * @return
	 */
	static public String getContactNameFromPhoneBook(Context context,
			String phoneNum) {
		String contactName = "";
		ContentResolver cr = context.getContentResolver();
		Cursor pCur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
				new String[] { phoneNum }, null);
		if (pCur.moveToFirst()) {
			contactName = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			pCur.close();
		}
		return contactName;
	}

	/**
	 * 根据当前的时间为比较的依据来决定显示的时间格式：
	 * 1.如果当前的短信时间中年份跟手机当前的年份不一致，则显示年月日，不显示具体的几点几分，如：2010-6-30；
	 * 2.如果短信的时间跟手机当前时间在同一年，但不是同一天，则只显示月日，如：6月29日；
	 * 3.如果是当天的短信，则会计算是上午还是下午的短信，同时显示几点几分记录的该短信，如：下午 12:55；
	 * 
	 * @author KangLin<kl222@126.com>
	 * @param context
	 * @param when
	 * @param fullFormat
	 * @return
	 */
	public static String formatTimeStampString(Context context, long when,
			boolean fullFormat) {
		Time then = new Time();
		then.set(when);
		Time now = new Time();
		now.setToNow();

		// Basic settings for formatDateTime() we want for all cases.
		int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM;

		// If the message is from a different year, show the date and year.
		if (then.year != now.year) {
			format_flags |= DateUtils.FORMAT_SHOW_YEAR
					| DateUtils.FORMAT_SHOW_DATE;
		} else if (then.yearDay != now.yearDay) {
			// If it is from a different day than today, show only the date.
			format_flags |= DateUtils.FORMAT_SHOW_DATE;
		} else {
			// Otherwise, if the message is from today, show the time.
			format_flags |= DateUtils.FORMAT_SHOW_TIME;
		}

		// If the caller has asked for full details, make sure to show the date
		// and time no matter what we've determined above (but still make
		// showing
		// the year only happen if it is a different year from today).
		if (fullFormat) {
			format_flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
		}

		return DateUtils.formatDateTime(context, when, format_flags);
	}
}
