package me.huxos.checkout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CSystemInformation;
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
	List<Map<String, String>> m_List;
	ListView m_lstView;
	SimpleAdapter m_Adapter;

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
		m_Adapter = new SimpleAdapter(this, m_List,
				android.R.layout.simple_list_item_1, // List 显示一行item1
				new String[] { "CONTENT" }, // "TITLE",
				new int[] { android.R.id.text1 });
		m_lstView.setAdapter(m_Adapter);

		// m_Adapter.notifyDataSetChanged();

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.firewall_setting, menu);
		return true;
	}

	private boolean initListView() {
		DBHelper db = DBHelper.getInstance(this.getBaseContext());
		if (null != m_List)
			m_List.clear();
		m_List = new ArrayList<Map<String, String>>();
		Map<String, String> mapWhitelist = new HashMap<String, String>();
		mapWhitelist.put("CONTENT", this.getString(R.string.whitelist));
		m_List.add(mapWhitelist);
		Map<String, String> mapBlacklist = new HashMap<String, String>();
		mapBlacklist.put("CONTENT", getString(R.string.blacklist));
		m_List.add(mapBlacklist);
		Map<String, String> mapSmsKeyWhitelist = new HashMap<String, String>();
		mapSmsKeyWhitelist.put("CONTENT",
				getString(R.string.brocker_sms_keyword_whitelist));
		m_List.add(mapSmsKeyWhitelist);
		Map<String, String> mapSmsKeyBlacklist = new HashMap<String, String>();
		mapSmsKeyBlacklist.put("CONTENT",
				getString(R.string.brocker_sms_keyword_blacklist));
		m_List.add(mapSmsKeyBlacklist);
		int[] unRead = db.getBlockerPhoneLogUnread();
		String szPhoneLog = this.getString(R.string.brocker_phone_log);
		Map<String, String> mapPhoneLog = new HashMap<String, String>();
		if (0 != unRead[0] /*|| 0 != unRead[1]*/)
			szPhoneLog += "(" + String.valueOf(unRead[0]) + "/"
					+ String.valueOf(unRead[1]) + ")";
		mapPhoneLog.put("CONTENT", szPhoneLog);
		m_List.add(mapPhoneLog);
		unRead = db.getBlockerSmsLogUnreadCount();
		String szSmsLog = this.getString(R.string.brocker_sms_log);
		Map<String, String> mapSmsLog = new HashMap<String, String>();
		if (0 != unRead[0] /*|| 0 != unRead[1]*/)
			szSmsLog += "(" + String.valueOf(unRead[0]) + "/"
					+ String.valueOf(unRead[1]) + ")";
		mapSmsLog.put("CONTENT", szSmsLog);
		m_List.add(mapSmsLog);

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
	 * 设置白名单事件
	 * 
	 * @param source
	 */
	private void onSetWhitelist() {
		Intent intent = new Intent();
		// 用intent.putExtra(String name, String value);来传递参数。
		intent.putExtra("isWhitelist", "true");
		intent.setClass(this, BrockerListActivity.class);
		startActivity(intent);
	}

	/**
	 * 设置黑名单事件
	 * 
	 * @param source
	 */
	private void onSetBlackist() {
		Intent intent = new Intent();
		// 用intent.putExtra(String name, String value);来传递参数。
		intent.putExtra("isWhitelist", "false");
		intent.setClass(this, BrockerListActivity.class);
		startActivity(intent);
	}

	/**
	 * 设置短信关键字白名单
	 * 
	 * @param source
	 */
	private void onSetSmsKeyWordWhitelist() {
		Intent intent = new Intent();
		// 用intent.putExtra(String name, String value);来传递参数。
		intent.putExtra("isWhitelist", "true");
		intent.setClass(this, BrockerSmsKeyWordListActivity.class);
		startActivity(intent);
	}

	/**
	 * 设置短信关键字黑名单
	 * 
	 * @param source
	 */
	private void onSetSmsKeyWordBlacklist() {
		Intent intent = new Intent();
		// 用intent.putExtra(String name, String value);来传递参数。
		intent.putExtra("isWhitelist", "false");
		intent.setClass(this, BrockerSmsKeyWordListActivity.class);
		startActivity(intent);
	}

	/**
	 * 查看电话拦截日志
	 * 
	 * @param source
	 */
	private void onBrockerPhoneLog() {
		Intent intent = new Intent();
		intent.setClass(this, BrockerPhoneLogActivity.class);
		startActivity(intent);
	}

	/**
	 * 查看短信拦截日志
	 * 
	 * @param source
	 */
	private void onBrockerSMSLog() {
		Intent intent = new Intent();
		intent.setClass(this, BrockerSmsLogActivity.class);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		switch (position) {
		case 0:
			onSetWhitelist();
			break;
		case 1:
			onSetBlackist();
			break;
		case 2:
			onSetSmsKeyWordWhitelist();
			break;
		case 3:
			onSetSmsKeyWordBlacklist();
			break;
		case 4:
			onBrockerPhoneLog();
			break;
		case 5:
			onBrockerSMSLog();
			break;
		}

	}

}
