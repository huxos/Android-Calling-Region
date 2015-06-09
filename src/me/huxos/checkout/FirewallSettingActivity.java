package me.huxos.checkout;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CSystemInformation;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class FirewallSettingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firewall_setting);
		Button btnFirewall = (Button) findViewById(R.id.btnOpen);
		DBHelper db = DBHelper.getInstance(this.getBaseContext());
		CSystemInformation info = db.getSystemInformation();
		if (info.getFirewallstatus().equals("0")) {
			btnFirewall.setText(R.string.open_firewall);
			enableSetButton(false);
		} else {
			btnFirewall.setText(R.string.close_firewall);
			enableSetButton(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.firewall_setting, menu);
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
		Button btnFirewall = (Button) findViewById(R.id.btnOpen);
		info = db.getSystemInformation();
		if (info.getFirewallstatus().equals("0")) {
			btnFirewall.setText(R.string.open_firewall);
			enableSetButton(false);
		} else {
			btnFirewall.setText(R.string.close_firewall);
			enableSetButton(true);
		}
	}

	/**
	 * 允许/禁止控件
	 * 
	 * @param enbale
	 *            :true,允许控件;false,禁止控件
	 */
	private void enableSetButton(boolean enbale) {
		Button button = (Button) findViewById(R.id.btnSetWhitelist);
		button.setEnabled(enbale);
		button = (Button) findViewById(R.id.btnSetBlacklist);
		button.setEnabled(enbale);
		button = (Button) findViewById(R.id.btnSetSmsKeyWordWhitelist);
		button.setEnabled(enbale);
		button = (Button) findViewById(R.id.btnSetSmsKeyWordBlacklist);
		button.setEnabled(enbale);
		button = (Button) findViewById(R.id.btnSetBrockerPhoneLog);
		button.setEnabled(enbale);
		button = (Button) findViewById(R.id.btnSetBrockerSMSLog);
		button.setEnabled(enbale);
	}

	/**
	 * 设置白名单事件
	 * 
	 * @param source
	 */
	public void onSetWhitelist(View source) {
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
	public void onSetBlackist(View source) {
		Intent intent = new Intent();
		// 用intent.putExtra(String name, String value);来传递参数。
		intent.putExtra("isWhitelist", "false");
		intent.setClass(this, BrockerListActivity.class);
		startActivity(intent);
	}

	/**
	 * 设置短信关键字白名单
	 * @param source
	 */
	public void onSetSmsKeyWordWhitelist(View source){
		Intent intent = new Intent();
		// 用intent.putExtra(String name, String value);来传递参数。
		intent.putExtra("isWhitelist", "true");
		intent.setClass(this, BrockerSmsKeyWordListActivity.class);
		startActivity(intent);
	}
	
	/**
	 *  设置短信关键字黑名单
	 * @param source
	 */
	public void onSetSmsKeyWordBlacklist(View source){
		Intent intent = new Intent();
		// 用intent.putExtra(String name, String value);来传递参数。
		intent.putExtra("isWhitelist", "false");
		intent.setClass(this, BrockerSmsKeyWordListActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 查看电话拦截日志
	 * @param source
	 */
	public void onBrockerPhoneLog(View source) {
		Intent intent = new Intent();
		intent.setClass(this, BrockerPhoneLogActivity.class);
		startActivity(intent);
	}

	/**
	 * 查看短信拦截日志
	 * @param source
	 */
	public void onBrockerSMSLog(View source) {
		Intent intent = new Intent();
		intent.setClass(this, BrockerSmsLogActivity.class);
		startActivity(intent);
	}

}
