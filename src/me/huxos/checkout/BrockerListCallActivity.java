package me.huxos.checkout;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.provider.CallLog;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BrockerListCallActivity extends Activity implements
		OnItemClickListener {
	private static final String TAG = "BrockerListCallActivity";
	private listAdapter m_Adapter;
	private List<CCallList> m_lstCall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brocker_list_call);

		m_lstCall = getCallList();

		ListView listView = (ListView) findViewById(R.id.lvBrockerListCallListView);
		listView.setOnItemClickListener(this);
		m_Adapter = new listAdapter(this);
		listView.setAdapter(m_Adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.brocker_list_call, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		CCallList call = m_lstCall.get(arg2);
		Intent intent = new Intent();
		intent.putExtra("name", call.name);
		intent.putExtra("number", call.number);
		setResult(Activity.RESULT_OK, intent);
		finish();// 结束之后会将结果传回From

	}

	public final class ViewHolder {
		TextView m_Name;
		TextView m_Number;
		TextView m_Time;
	}

	public final class CCallList {
		String name;
		String number;
		int type;
		long time;

		public CCallList(String name, String number, int type, long time) {
			super();
			this.name = name;
			this.number = number;
			this.type = type;
			this.time = time;
		}
	}

	private List<CCallList> getCallList() {
		List<CCallList> lstCall = new ArrayList<CCallList>();

		Cursor cursor = null;
		try {
			ContentResolver cr = getContentResolver();
			cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[] {
					CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
					CallLog.Calls.TYPE, CallLog.Calls.DATE }, null, null,
					CallLog.Calls.DEFAULT_SORT_ORDER);
			while (cursor.moveToNext()) {
				CCallList call = new CCallList(
						cursor.getString(cursor
								.getColumnIndex(CallLog.Calls.CACHED_NAME)),
						cursor.getString(cursor
								.getColumnIndex(CallLog.Calls.NUMBER)),
						cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)),
						cursor.getLong(cursor
								.getColumnIndex(CallLog.Calls.DATE)));
				lstCall.add(call);
			}
		} catch (Exception e) {
			Log.e(TAG, "findBlockerPhoneLog exception:" + e.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return lstCall;
	}

	class listAdapter extends BaseAdapter {
		private LayoutInflater m_Inflater;
		private BrockerListCallActivity m_activity;

		public listAdapter(BrockerListCallActivity activity) {
			super();
			m_activity = activity;
			this.m_Inflater = LayoutInflater.from(m_activity.getBaseContext());
		}

		@Override
		public int getCount() {
			Log.d(TAG,
					"getCount:" + String.valueOf(m_activity.m_lstCall.size()));
			return m_activity.m_lstCall.size();
		}

		@Override
		public Object getItem(int position) {
			CCallList call = m_activity.m_lstCall.get(position);
			return call;
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
				convertView = m_Inflater.inflate(R.layout.brocker_list_call,
						null);
				holder.m_Name = (TextView) convertView
						.findViewById(R.id.txtBrockerListCallName);
				holder.m_Number = (TextView) convertView
						.findViewById(R.id.txtBrockerListCallNumber);
				convertView.setTag(holder);
				holder.m_Time = (TextView) convertView
						.findViewById(R.id.txtBrockerListCallTiem);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 更新值
			CCallList call = m_activity.m_lstCall.get(position);
			holder.m_Name.setText(call.name);
			holder.m_Number.setText(call.number);
			holder.m_Time.setText(CTool.formatTimeStampString(
					m_activity.getBaseContext(), call.time, false));

			return convertView;
		}

	}

}
