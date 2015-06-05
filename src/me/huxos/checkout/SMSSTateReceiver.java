package me.huxos.checkout;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBlockerSMSLog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSSTateReceiver extends BroadcastReceiver {
	private static final String TAG = "BroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (!action.equals("android.provider.Telephony.SMS_RECEIVED")) {
			return;
		}

		Bundle bundle = intent.getExtras();
		if (null == bundle) {
			return;
		}
		Object[] pdus = (Object[]) bundle.get("pdus");
		for (Object pdu : pdus) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
			// 发送者
			String sender = message.getOriginatingAddress();
			Log.d(TAG, "sms sender:" + sender
					+ "\nmessage:" +  message.getMessageBody()
					+ "\ntime:" + String.valueOf(message.getTimestampMillis()));
			CFirewallOperate firewallOperate= new CFirewallOperate(context);
			if(firewallOperate.brockerSMS(sender))
			{
				//写入拦截日志
				CBlockerSMSLog log = new CBlockerSMSLog(sender, message.getMessageBody(),
						message.getTimestampMillis(), 0);
				DBHelper db = DBHelper.getInstance(context);
				db.insertBlockerSMSLog(log);
				//取消继续广播
				abortBroadcast();
			}
		}
	}
}
