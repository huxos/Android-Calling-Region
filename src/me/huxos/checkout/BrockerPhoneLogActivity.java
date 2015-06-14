package me.huxos.checkout;

import java.util.List;
import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBlockerPhoneLog;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 拦截电话日志界面
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class BrockerPhoneLogActivity extends Activity {
	private static final String TAG = "BrockerPhoneLogActivity";
	private listAdapter m_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brocker_phone_log);
		// 设置listview数据适配器
		ListView listView = (ListView) findViewById(R.id.lstBrockerPhoneLog);
		m_adapter = new listAdapter(this.getBaseContext());
		listView.setAdapter(m_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.brocker_phone_log, menu);
		return true;
	}

	public final class ViewHolder {
		TextView m_Name;
		TextView m_Number;
		TextView m_Time;
	}

	class listAdapter extends BaseAdapter {
		private LayoutInflater m_Inflater;
		private Context m_context;
		private DBHelper m_db;
		private List<CBlockerPhoneLog> m_Brockerlist;

		public listAdapter(Context context) {
			super();
			this.m_context = context;
			this.m_Inflater = LayoutInflater.from(context);
			m_db = DBHelper.getInstance(this.m_context);
			m_Brockerlist = m_db.findBlockerPhoneLog(null);
			m_db.updateBlockerPhoneLogIsread();
		}

		// 更新数据
		public void UpdateDate() {
			m_Brockerlist = m_db.findBlockerPhoneLog(null);
			notifyDataSetChanged();
			return;
		}

		@Override
		public int getCount() {
			Log.d(TAG, "getCount:" + String.valueOf(m_Brockerlist.size()));
			return m_Brockerlist.size();
		}

		@Override
		public Object getItem(int position) {
			return m_Brockerlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = m_Inflater.inflate(R.layout.brocker_phone_log,
						null);
				holder.m_Name = (TextView) convertView
						.findViewById(R.id.txtBrockerPhoneLogName);
				holder.m_Number = (TextView) convertView
						.findViewById(R.id.txtBrockerPhoneLogNumber);
				holder.m_Time = (TextView) convertView
						.findViewById(R.id.txtBrockerPhoneLogTime);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 更新值
			CBlockerPhoneLog brockerList = m_Brockerlist.get(position);
			holder.m_Number.setText(CTool.getShowPhone(m_context,
					brockerList.getPhone_number()));
			String name = CTool.getNameFromPhone(m_context,
					brockerList.getPhone_number());
			holder.m_Name.setText(name);
			holder.m_Time.setText(CTool.formatTimeStampString(m_context,
					brockerList.getTime(), false));

			return convertView;
		}

	}
}
