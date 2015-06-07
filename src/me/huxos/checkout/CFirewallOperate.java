package me.huxos.checkout;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBrockerlist;
import me.huxos.checkout.entity.CSystemInformation;
import android.content.Context;

/**
 * 防火墙操作类
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
	 * @param number：来电号码
	 * @return 拦截成功返回true，不拦截返回false
	 */
	public boolean brockerPhone(String number) {
		DBHelper db = DBHelper.getInstance(context);
		//检查是否打开防火墙 
		CSystemInformation info = db.getSystemInformation();
		if(info.getFirewallstatus().equals("0"))
			return false;
		//检查白名单
		CBrockerlist whitelist = db.findWhitelist(number);
		if(null != whitelist)
			if(0 != whitelist.getPhone_enable())
				return false;
		//检查黑名单
		CBrockerlist blacklist = db.findBlacklist(number);
		if(null == blacklist)
			return false;
		if(0 == blacklist.getPhone_enable())
			return false;
		//挂机
		return true;
	}

	/**
	 * 拦截短信
	 * @param number：短信发送号码
	 * @return 拦截成功返回true，不拦截返回false
	 */
	public boolean brockerSMS(String number) {
		DBHelper db = DBHelper.getInstance(context);
		//检查是否打开防火墙 
		CSystemInformation info = db.getSystemInformation();
		if(info.getFirewallstatus().equals("0"))
			return false;
		//检查白名单
		CBrockerlist whitelist = db.findWhitelist(number);
		if(null != whitelist)
			if(0 != whitelist.getSms_enable())
				return false;
		//检查黑名单
		CBrockerlist blacklist = db.findBlacklist(number);
		if(null == blacklist)
			return false;
		if(0 == blacklist.getSms_enable())
			return false;
		return true;
	}

}
