package kanglinstudio.assistant;

import kanglinstudio.assistant.db.DBHelper;
import kanglinstudio.assistant.entity.CBlockerSMSLog;
import kanglinstudio.assistant.entity.CSystemInformation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 短信广播接收
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
		String sender = "", szMsg = "";
		long time = 0;
		Object[] pdus = (Object[]) bundle.get("pdus");
		for (Object pdu : pdus) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
			// 发送者
			sender = message.getOriginatingAddress();
			szMsg += message.getMessageBody();
			time = message.getTimestampMillis();
			Log.d(TAG,
					"sms sender:" + sender + ";message:"
							+ message.getMessageBody() + ";time:"
							+ String.valueOf(time));

		}

		DBHelper db = DBHelper.getInstance(context);
		CSystemInformation info = db.getSystemInformation();
		CFirewallOperate firewallOperate = new CFirewallOperate(context);
		if ((info.getInterceptionDirection() == CSystemInformation.InterceptionDirectionIncoming || info
				.getInterceptionDirection() == CSystemInformation.InterceptionDirectionDouble)
				&& info.getInterceptionType() != CSystemInformation.InterceptioinTypeNo) {
			if (firewallOperate.brockerSMS(sender, szMsg)) {
				// 写入拦截日志
				CBlockerSMSLog log = new CBlockerSMSLog(sender, szMsg, time, 0);
				db.insertBlockerSMSLog(log);
				// 取消继续广播
				abortBroadcast();
			}
		}

	}
}
