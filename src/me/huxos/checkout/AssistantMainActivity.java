package me.huxos.checkout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * android 助手主界面
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class AssistantMainActivity extends Activity implements
		OnItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assistant_main);

		List<Map<String, String>> m_List = new ArrayList<Map<String, String>>();
		Map<String, String> mapArea = new HashMap<String, String>();
		mapArea.put("CONTENT", this.getString(R.string.setting_query_title));
		m_List.add(mapArea);
		Map<String, String> mapFirewall = new HashMap<String, String>();
		mapFirewall.put("CONTENT", getString(R.string.firewallsetting));
		m_List.add(mapFirewall);

		SimpleAdapter adapter = new SimpleAdapter(this, m_List,
				android.R.layout.simple_list_item_1, // List 显示一行item1
				new String[] { "CONTENT" }, // "TITLE",
				new int[] { android.R.id.text1 });
		ListView listView = (ListView) findViewById(R.id.lvAssistantMainListView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.assistant_main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent();
		switch (position) {
		case 0:// 区域查询界面
			intent.setClass(this, QueryAreaActivity.class);
			break;
		case 1:// 防火墙设置界面
			intent.setClass(this, FirewallSettingActivity.class);
			break;
		}
		startActivity(intent);
	}

}
