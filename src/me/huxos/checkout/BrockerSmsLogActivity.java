package me.huxos.checkout;

import java.util.List;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBlockerSMSLogs;
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
 * 短信拦截日志界面
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class BrockerSmsLogActivity extends Activity {
	private static final String TAG = "BrockerSmsLogActivity";
	private listAdapter m_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brocker_sms_log);
	}

	@Override
	protected void onResume() {
		// 设置listview数据适配器
		ListView listView = (ListView) findViewById(R.id.lstBrockerSmsLogView);
		m_adapter = new listAdapter(this);
		listView.setAdapter(m_adapter);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.brocker_sms_log, menu);
		return true;
	}

	public final class ViewHolder {
		TextView m_Name;
		TextView m_Text;
		TextView m_Time;
		TextView m_Count;
		Button m_btnDelete;
		Button m_btnBrower;
	}

	class listAdapter extends BaseAdapter {
		private LayoutInflater m_Inflater;
		private Context m_context;
		private DBHelper m_db;
		private List<CBlockerSMSLogs> m_Brockerlist;
		private BrockerSmsLogActivity m_activity;

		public listAdapter(BrockerSmsLogActivity activity) {
			super();
			this.m_activity = activity;
			this.m_context = activity.getBaseContext();
			this.m_Inflater = LayoutInflater.from(m_context);
			m_db = DBHelper.getInstance(this.m_context);
			m_Brockerlist = m_db.findBlockerSMSLogGroup(null);
		}

		// 更新数据
		public void UpdateDate() {
			m_Brockerlist = m_db.findBlockerSMSLogGroup(null);
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
				convertView = m_Inflater
						.inflate(R.layout.brocker_sms_log, null);
				holder.m_Name = (TextView) convertView
						.findViewById(R.id.txtBrockerSmsLogName);
				holder.m_Text = (TextView) convertView
						.findViewById(R.id.txtBrockerSmsLogText);
				holder.m_Time = (TextView) convertView
						.findViewById(R.id.txtBrockerSmsLogTime);
				holder.m_Count = (TextView) convertView
						.findViewById(R.id.txtBrockerSmsLogCount);
				holder.m_btnDelete = (Button) convertView
						.findViewById(R.id.btnBrockerSmsLogDelete);
				holder.m_btnBrower = (Button) convertView
						.findViewById(R.id.btnBrockerSmsLogBrower);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 更新值
			CBlockerSMSLogs brockerList = m_Brockerlist.get(position);
			String name = CTool.getNameFromPhone(m_context,
					brockerList.getPhone_number());
			holder.m_Name.setText(name);
			holder.m_Text.setText(brockerList.getContent());
			holder.m_Time.setText(CTool.formatTimeStampString(m_context,
					brockerList.getTime(), false));
			holder.m_Count.setText("("
					+ String.valueOf(brockerList.getUnread_count()) + "/"
					+ String.valueOf(brockerList.getCount()) + ")");

			// 设置删除按钮事件
			class CDeleteClickListener implements View.OnClickListener {
				private int m_position;

				public CDeleteClickListener(listAdapter listAdapter,
						int position) {
					super();
					this.m_position = position;
				}

				@Override
				public void onClick(View v) {
					CBlockerSMSLogs brockerList = m_Brockerlist.get(m_position);
					m_db.deleteBlockerSMSLog("phone_number="
							+ brockerList.getPhone_number());
					UpdateDate();
				}

			}
			holder.m_btnDelete.setOnClickListener(new CDeleteClickListener(
					this, position));

			// 设置浏览按钮事件
			class CBrowerClickListener implements View.OnClickListener {
				private int m_position;
				private BrockerSmsLogActivity m_activity;

				public CBrowerClickListener(BrockerSmsLogActivity activity,
						listAdapter listAdapter, int position) {
					super();
					this.m_position = position;
					m_activity = activity;
				}

				@Override
				public void onClick(View v) {
					CBlockerSMSLogs brockerList = m_Brockerlist.get(m_position);
					Intent intent = new Intent();
					// 用intent.putExtra(String name, String value);来传递参数。
					intent.putExtra("number", brockerList.getPhone_number());
					intent.setClass(m_activity,
							BrockerSmsLogDetailedActivity.class);
					startActivity(intent);
				}

			}
			holder.m_btnBrower.setOnClickListener(new CBrowerClickListener(
					m_activity, this, position));

			return convertView;
		}
	}
}
