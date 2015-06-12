package me.huxos.checkout;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBlockerSMSLog;
import me.huxos.checkout.entity.CSystemInformation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 短信接收界面
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class SMSSTateReceiver extends BroadcastReceiver {
	private static final String TAG = "BroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
			processRecived(context, intent);
		}

	}

	private void processRecived(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (null == bundle) {
			return;
		}
		Object[] pdus = (Object[]) bundle.get("pdus");
		for (Object pdu : pdus) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
			// 发送者
			String sender = message.getOriginatingAddress();
			Log.d(TAG,
					"sms sender:" + sender + "\nmessage:"
							+ message.getMessageBody() + "\ntime:"
							+ String.valueOf(message.getTimestampMillis()));

			DBHelper db = DBHelper.getInstance(context);
			CSystemInformation info = db.getSystemInformation();
			CFirewallOperate firewallOperate = new CFirewallOperate(context);
			if ((info.getInterceptionDirection() == CSystemInformation.InterceptionDirectionIncoming || info
					.getInterceptionDirection() == CSystemInformation.InterceptionDirectionDouble)
					&& info.getInterceptionType() != CSystemInformation.InterceptioinTypeNo) {
				if (firewallOperate
						.brockerSMS(sender, message.getMessageBody())) {
					// 写入拦截日志
					CBlockerSMSLog log = new CBlockerSMSLog(sender,
							message.getMessageBody(),
							message.getTimestampMillis(), 0);
					db.insertBlockerSMSLog(log);
					// 取消继续广播
					abortBroadcast();
				}
			}

		}
	}
}
