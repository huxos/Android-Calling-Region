package me.huxos.checkout;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBrockerlist;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class CTool {
	
	/**
	 * 从电话号码得到相应的名字
	 * @param context
	 * @param phone
	 * @return
	 */
	static public String getNameFromPhone(Context context, String phone){
		//从通信薄中得到
		String szName = "";
		szName = getContactNameFromPhoneBook(context, phone);
		if(szName.isEmpty()) {
			//从黑名单中得到
			DBHelper db = DBHelper.getInstance(context);
			CBrockerlist brockerlist = db.findBlacklist(phone);
			if(null != brockerlist)
				szName = brockerlist.getName();
		}
		return szName;		
	}
	

	/**
	 * 从通信录中得到相关号码的名字
	 * 
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

}
