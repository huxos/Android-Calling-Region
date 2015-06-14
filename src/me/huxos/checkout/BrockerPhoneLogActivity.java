package me.huxos.checkout;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBlockerPhoneLog;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 拦截电话日志界面
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class BrockerPhoneLogActivity extends Activity implements
		OnClickListener {
	private static final String TAG = "BrockerPhoneLogActivity";
	private listAdapter m_adapter;
	private List<CBlockerPhoneLog> m_Brockerlist;
	private Set<Integer> m_lstPostion;
	CheckBox m_cbDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brocker_phone_log);
		// 设置listview数据适配器
		ListView listView = (ListView) findViewById(R.id.lstBrockerPhoneLog);
		m_adapter = new listAdapter(this);
		listView.setAdapter(m_adapter);
		m_lstPostion = new HashSet<Integer>();
		m_cbDelete = (CheckBox) findViewById(R.id.cbBrockerPhoneLogDelete);
		m_cbDelete.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.brocker_phone_log, menu);
		return true;
	}

	/**
	 * 复选框点击改变事件
	 */
	@Override
	public void onClick(View arg0) {

		if (m_cbDelete.isChecked()) {
			for (int i = 0; i < m_Brockerlist.size(); i++) {
				m_lstPostion.add(i);
			}
		} else {
			m_lstPostion.clear();
		}
		m_adapter.notifyDataSetChanged();

	}

	/**
	 * 删除按钮事件
	 * 
	 * @param source
	 */
	public void onDeleteButtonOnclick(View source) {
		String condition = null;
		Iterator<Integer> it = m_lstPostion.iterator();
		while (it.hasNext()) {
			if(null != condition)
				condition += " or ";
			else 
				condition = "";
			condition += "no=" + m_Brockerlist.get(it.next()).getNo();
		}
		DBHelper db = DBHelper.getInstance(getBaseContext());
		db.deleteBlockerPhoneLog(condition);
		//注意下面顺序不能变
		m_lstPostion.clear();
		m_adapter.UpdateDate();
		
	}

	public final class ViewHolder {
		TextView m_Name;
		TextView m_Number;
		TextView m_Time;
		CheckBox m_cbDelete;
	}

	class listAdapter extends BaseAdapter {
		private LayoutInflater m_Inflater;
		private Context m_context;
		private DBHelper m_db;
		private BrockerPhoneLogActivity m_activity;

		public listAdapter(BrockerPhoneLogActivity activity) {
			super();
			m_activity = activity;
			this.m_context = activity.getBaseContext();
			this.m_Inflater = LayoutInflater.from(m_context);
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
			try {
				ViewHolder holder = null;

				if (convertView == null) {
					holder = new ViewHolder();
					convertView = m_Inflater.inflate(
							R.layout.brocker_phone_log, null);
					holder.m_Name = (TextView) convertView
							.findViewById(R.id.txtBrockerPhoneLogName);
					holder.m_Number = (TextView) convertView
							.findViewById(R.id.txtBrockerPhoneLogNumber);
					holder.m_Time = (TextView) convertView
							.findViewById(R.id.txtBrockerPhoneLogTime);
					holder.m_cbDelete = (CheckBox) convertView
							.findViewById(R.id.cbBrockerPhoneLogItemDelete);
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
				if (m_activity.m_lstPostion.contains(position)) {
					holder.m_cbDelete.setChecked(true);
				} else {
					holder.m_cbDelete.setChecked(false);
				}

				// 设置事件监听
				class CCheckedChangeListener implements OnCheckedChangeListener {
					private int m_position;
					private BrockerPhoneLogActivity m_activity;

					CCheckedChangeListener(BrockerPhoneLogActivity activity,
							int position) {
						super();
						this.m_position = position;
						m_activity = activity;
					}

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked)
							m_activity.m_lstPostion.add(m_position);
						else
							m_activity.m_lstPostion.remove(m_position);
						if (m_activity.m_lstPostion.size() > 0)
							m_activity.m_cbDelete.setChecked(true);
						else
							m_activity.m_cbDelete.setChecked(false);
					}
				}

				holder.m_cbDelete
						.setOnCheckedChangeListener(new CCheckedChangeListener(
								this.m_activity, position));
			} catch (Exception e) {
				Log.e(TAG, "getView exception", e);
			}
			return convertView;
		}

	}

}
