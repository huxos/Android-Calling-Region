package kanglinstudio.assistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

	List<onChickCallInstance> m_chickCallInterface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assistant_main);

		m_chickCallInterface = new ArrayList<onChickCallInstance>();
		m_chickCallInterface.add(new onChickCallInstance(
				R.string.setting_query_title, QueryAreaActivity.class));
		m_chickCallInterface.add(new onChickCallInstance(
				R.string.firewallsetting, FirewallSettingActivity.class));
		m_chickCallInterface.add(new onChickCallInstance(
				R.string.title_activity_activity_locale_gps, ActivityLocaleGps.class));
		List<Map<String, String>> m_List = new ArrayList<Map<String, String>>();
		Iterator<onChickCallInstance> it = m_chickCallInterface.iterator();
		while (it.hasNext()) {
			onChickCallInstance node = it.next();
			m_List.add(node.getItem());
		}

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
		getMenuInflater().inflate(R.menu.assistant_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// 关于菜单
		case R.id.action_about:
			LayoutInflater inflater = LayoutInflater.from(this);
			final View dialog_view = inflater.inflate(R.layout.dialog_view,
					null);
			new AlertDialog.Builder(this).setTitle(R.string.action_about)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(dialog_view).setPositiveButton(R.string.ok, null)
					.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * listview item 点击事件接口
	 * 
	 * @author KangLin<kl222@126.com>
	 * 
	 */
	class onChickCallInstance {
		int m_IdRes;
		Class<?> m_Class;

		onChickCallInstance(int idRes, Class<?> cls) {
			m_IdRes = idRes;
			m_Class = cls;
		}
		// listview item 点击事件
		public void onChick() {
			Intent intent = new Intent();
			intent.setClass(AssistantMainActivity.this, m_Class);
			startActivity(intent);
		}
		// listview item 显示 map
		public Map<String, String> getItem() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("CONTENT", getString(m_IdRes));
			return map;
		}
	}

	/**
	 * listview item 点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		onChickCallInstance cf = m_chickCallInterface.get(position);
		if (null != cf)
			cf.onChick();
	}

}
