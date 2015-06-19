package kanglinstudio.assistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kanglinstudio.assistant.db.DBHelper;
import kanglinstudio.assistant.entity.CSystemInformation;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 防火墙设置主界面
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class FirewallSettingActivity extends Activity implements
		OnItemClickListener {
	@SuppressWarnings("unused")
	private static final String TAG = "FirewallSettingActivity";
	List<Map<String, String>> m_List;
	ListView m_lstView;
	SimpleAdapter m_Adapter;
	List<onChickCalllInterface> m_chickCallInterface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firewall_setting);

		m_lstView = (ListView) findViewById(R.id.lvFirewallSettingListView);
		m_lstView.setOnItemClickListener(this);

		Button btnFirewall = (Button) findViewById(R.id.btnFirewallSettingOpen);
		DBHelper db = DBHelper.getInstance(this.getBaseContext());
		CSystemInformation info = db.getSystemInformation();
		if (info.getFirewallstatus().equals("0")) {
			btnFirewall.setText(R.string.open_firewall);
			m_lstView.setEnabled(false);

		} else {
			btnFirewall.setText(R.string.close_firewall);
			m_lstView.setEnabled(true);
		}
	}

	@Override
	protected void onResume() {

		initListView();
		if (null != m_List && null != m_lstView) {
			m_Adapter = new SimpleAdapter(this, m_List,
					android.R.layout.simple_list_item_1, // List 显示一行item1
					new String[] { "CONTENT" }, // "TITLE",
					new int[] { android.R.id.text1 });
			m_lstView.setAdapter(m_Adapter);
		}
		// m_Adapter.notifyDataSetChanged();

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.firewall_setting, menu);
		return true;
	}

	/**
	 * 初始化 listview
	 * 
	 * @return
	 */
	private boolean initListView() {
		if (null != m_chickCallInterface)
			m_chickCallInterface.clear();
		m_chickCallInterface = new ArrayList<onChickCalllInterface>();
		
		onSetInerceptMode inerceptMode = new onSetInerceptMode(this);
		m_chickCallInterface.add(inerceptMode);
		onSetWhitelist whitelist = new onSetWhitelist(this);
		m_chickCallInterface.add(whitelist);
		onSetBlacklist blacklist = new onSetBlacklist(this);
		m_chickCallInterface.add(blacklist);
		onSetSmsKeyWordWhitelist keywordWhitelist = new onSetSmsKeyWordWhitelist(
				this);
		m_chickCallInterface.add(keywordWhitelist);
		onSetSmsKeyWordBlacklist keywordBlacklist = new onSetSmsKeyWordBlacklist(
				this);
		m_chickCallInterface.add(keywordBlacklist);
		onBrockerPhoneLog phoneLog = new onBrockerPhoneLog(this);
		m_chickCallInterface.add(phoneLog);
		onBrockerSMSLog smsLog = new onBrockerSMSLog(this);
		m_chickCallInterface.add(smsLog);

		if (null != m_List)
			m_List.clear();
		m_List = new ArrayList<Map<String, String>>();
		
		Iterator<onChickCalllInterface> it = m_chickCallInterface.iterator();
		while (it.hasNext()) {
			onChickCalllInterface node = it.next();
			m_List.add(node.getItem());
		}
		return true;
	}

	/**
	 * 防火墙控制按钮事件
	 */
	public void onClickOpenFirewall(View source) {
		DBHelper db = DBHelper.getInstance(this.getBaseContext());
		CSystemInformation info = db.getSystemInformation();
		if (info.getFirewallstatus().equals("0"))
			info.setFirewallstatus("1");
		else
			info.setFirewallstatus("0");
		db.updateSystemInformation(info);
		Button btnFirewall = (Button) findViewById(R.id.btnFirewallSettingOpen);
		info = db.getSystemInformation();
		if (info.getFirewallstatus().equals("0")) {
			btnFirewall.setText(R.string.open_firewall);
			m_lstView.setEnabled(false);
		} else {
			btnFirewall.setText(R.string.close_firewall);
			m_lstView.setEnabled(true);
		}
	}

	/**
	 * listview item 点击事件接口
	 * 
	 * @author KangLin<kl222@126.com>
	 * 
	 */
	public interface onChickCalllInterface {
		// listview item 显示 map
		public Map<String, String> getItem();
		// listview item 点击事件
		public void onChick();
	}

	/**
	 * 设置拦截模式事件
	 * 
	 * @author KangLin<kl222@126.com>
	 */
	class onSetInerceptMode implements onChickCalllInterface {
		Activity m_activity;

		public onSetInerceptMode(Activity activity) {
			m_activity = activity;
		}

		@Override
		public void onChick() {
			Intent intent = new Intent();
			intent.setClass(m_activity, InterceptModeActivity.class);
			startActivity(intent);
		}

		@Override
		public Map<String, String> getItem() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("CONTENT",
					getString(R.string.title_activity_intercept_mode));
			return map;
		}
	}

	/**
	 * 设置白名单事件
	 */
	class onSetWhitelist implements onChickCalllInterface {
		Activity m_activity;

		public onSetWhitelist(Activity activity) {
			m_activity = activity;
		}

		@Override
		public void onChick() {
			Intent intent = new Intent();
			intent.putExtra("isWhitelist", "true");
			intent.setClass(m_activity, BrockerListActivity.class);
			startActivity(intent);
		}

		@Override
		public Map<String, String> getItem() {
			Map<String, String> mapWhitelist = new HashMap<String, String>();
			mapWhitelist.put("CONTENT", getString(R.string.whitelist));
			return mapWhitelist;
		}
	}

	/**
	 * 设置黑名单事件
	 */
	class onSetBlacklist implements onChickCalllInterface {
		Activity m_activity;

		public onSetBlacklist(Activity activity) {
			m_activity = activity;
		}

		@Override
		public void onChick() {
			Intent intent = new Intent();
			intent.putExtra("isWhitelist", "false");
			intent.setClass(m_activity, BrockerListActivity.class);
			startActivity(intent);
		}

		@Override
		public Map<String, String> getItem() {
			Map<String, String> mapBlacklist = new HashMap<String, String>();
			mapBlacklist.put("CONTENT", getString(R.string.blacklist));

			return mapBlacklist;
		}
	}

	/**
	 * 设置短信关键字白名单
	 */
	class onSetSmsKeyWordWhitelist implements onChickCalllInterface {
		Activity m_activity;

		public onSetSmsKeyWordWhitelist(Activity activity) {
			m_activity = activity;
		}

		@Override
		public void onChick() {
			Intent intent = new Intent();
			intent.putExtra("isWhitelist", "true");
			intent.setClass(m_activity, BrockerSmsKeyWordListActivity.class);
			startActivity(intent);
		}

		@Override
		public Map<String, String> getItem() {
			Map<String, String> mapSmsKeyWhitelist = new HashMap<String, String>();
			mapSmsKeyWhitelist.put("CONTENT",
					getString(R.string.brocker_sms_keyword_whitelist));
			return mapSmsKeyWhitelist;
		}
	}

	/**
	 * 设置短信关键字黑名单
	 */
	class onSetSmsKeyWordBlacklist implements onChickCalllInterface {
		Activity m_activity;

		public onSetSmsKeyWordBlacklist(Activity activity) {
			m_activity = activity;
		}

		@Override
		public void onChick() {
			Intent intent = new Intent();
			intent.putExtra("isWhitelist", "false");
			intent.setClass(m_activity, BrockerSmsKeyWordListActivity.class);
			startActivity(intent);
		}

		@Override
		public Map<String, String> getItem() {
			Map<String, String> mapSmsKeyBlacklist = new HashMap<String, String>();
			mapSmsKeyBlacklist.put("CONTENT",
					getString(R.string.brocker_sms_keyword_blacklist));
			return mapSmsKeyBlacklist;
		}
	}

	/**
	 * 查看电话拦截日志
	 */
	class onBrockerPhoneLog implements onChickCalllInterface {
		Activity m_activity;

		public onBrockerPhoneLog(Activity activity) {
			m_activity = activity;
		}

		@Override
		public void onChick() {
			Intent intent = new Intent();
			intent.setClass(m_activity, BrockerPhoneLogActivity.class);
			startActivity(intent);
		}

		@Override
		public Map<String, String> getItem() {
			DBHelper db = DBHelper.getInstance(getBaseContext());
			int[] unRead = db.getBlockerPhoneLogUnread();
			String szPhoneLog = getString(R.string.brocker_phone_log);
			Map<String, String> mapPhoneLog = new HashMap<String, String>();
			if (0 != unRead[0] /* || 0 != unRead[1] */)
				szPhoneLog += "(" + String.valueOf(unRead[0]) + "/"
						+ String.valueOf(unRead[1]) + ")";
			mapPhoneLog.put("CONTENT", szPhoneLog);
			return mapPhoneLog;
		}
	}

	/**
	 * 查看短信拦截日志
	 */
	class onBrockerSMSLog implements onChickCalllInterface {
		Activity m_activity;

		public onBrockerSMSLog(Activity activity) {
			m_activity = activity;
		}

		@Override
		public void onChick() {
			Intent intent = new Intent();
			intent.setClass(m_activity, BrockerSmsLogActivity.class);
			startActivity(intent);
		}

		@Override
		public Map<String, String> getItem() {
			DBHelper db = DBHelper.getInstance(getBaseContext());
			int[] unRead = db.getBlockerSmsLogUnreadCount();
			String szSmsLog = getString(R.string.brocker_sms_log);
			Map<String, String> mapSmsLog = new HashMap<String, String>();
			if (0 != unRead[0] /* || 0 != unRead[1] */)
				szSmsLog += "(" + String.valueOf(unRead[0]) + "/"
						+ String.valueOf(unRead[1]) + ")";
			mapSmsLog.put("CONTENT", szSmsLog);
			return mapSmsLog;
		}
	}

	/**
	 * 处理 listview 的 item 点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		onChickCalllInterface cf = m_chickCallInterface.get(position);
		if(null != cf)
			cf.onChick();
	}
}
