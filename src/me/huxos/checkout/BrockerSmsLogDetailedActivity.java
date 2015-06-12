package me.huxos.checkout;

import java.util.List;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBlockerSMSLog;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 拦截短信日志详情
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class BrockerSmsLogDetailedActivity extends Activity {
	private static final String TAG = "BrockerSmsLogDetailedActivity";
	private String m_number;
	private listAdapter m_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brocker_sms_log_detailed);
		Intent intent = this.getIntent();
		m_number = intent.getStringExtra("number");
		// 设置listview数据适配器
		ListView listView = (ListView) findViewById(R.id.lvBrockerSmsLogDetailedListView);
		m_adapter = new listAdapter(this, m_number);
		listView.setAdapter(m_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.brocker_sms_log_detailed, menu);
		return true;
	}

	public final class ViewHolder {
		TextView m_Name;
		TextView m_Text;
		TextView m_Time;
		Button m_Delete;
	}

	class listAdapter extends BaseAdapter {
		private LayoutInflater m_Inflater;
		private Context m_context;
		private DBHelper m_db;
		private List<CBlockerSMSLog> m_Brockerlist;
		private String m_number;

		public listAdapter(BrockerSmsLogDetailedActivity activity, String number) {
			super();
			this.m_context = activity.getBaseContext();
			this.m_Inflater = LayoutInflater.from(m_context);
			m_number = number;
			m_db = DBHelper.getInstance(this.m_context);
			m_Brockerlist = m_db.findBlockerSMSLog("where phone_number='"
					+ m_number + "'");
			m_db.updateBlockerSMSLogIsread(m_number);
		}

		// 更新数据
		public void UpdateDate() {
			m_Brockerlist = m_db.findBlockerSMSLog("where phone_number='"
					+ m_number +"'");
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
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = m_Inflater.inflate(
						R.layout.brocker_sms_log_detailed, null);
				holder.m_Name = (TextView) convertView
						.findViewById(R.id.txtBrockerSmsLogDetailedName);
				holder.m_Text = (TextView) convertView
						.findViewById(R.id.txtBrockerSmsLogDetailedContent);
				holder.m_Time = (TextView) convertView
						.findViewById(R.id.txtBrockerSmsLogDetailedTime);
				convertView.setTag(holder);
				holder.m_Delete = (Button) convertView
						.findViewById(R.id.btnBrockerSmsLogDetailedDelete);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 更新值
			CBlockerSMSLog brockerList = m_Brockerlist.get(position);
			holder.m_Name.setText(CTool.getNameFromPhone(m_context,
					brockerList.getPhone_number()));
			holder.m_Text.setText(brockerList.getContent());
			holder.m_Time.setText(CTool.formatTimeStampString(m_context,
					brockerList.getTime(), false));

			// 设置删除按钮事件
			class CDeleteClickListener implements View.OnClickListener {
				private int m_position;

				public CDeleteClickListener(int position) {
					super();
					this.m_position = position;
				}

				@Override
				public void onClick(View v) {
					CBlockerSMSLog brockerList = m_Brockerlist.get(m_position);
					m_db.deleteBlockerSMSLog("no=" + brockerList.getNo());
					UpdateDate();
				}

			}
			holder.m_Delete.setOnClickListener(new CDeleteClickListener(
					position));

			return convertView;
		}
	}
}
