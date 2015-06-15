package kanglinstudio.assistant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.net.Uri;
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

/**
 * 呼叫列表界面
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class BrockerListCallActivity extends Activity implements
		OnItemClickListener {
	private static final String TAG = "BrockerListCallActivity";
	private listAdapter m_Adapter;
	private List<CCall> m_lstCall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brocker_list_call);

		m_lstCall = getList();

		ListView listView = (ListView) findViewById(R.id.lvBrockerListCallListView);
		listView.setOnItemClickListener(this);
		m_Adapter = new listAdapter(this);
		listView.setAdapter(m_Adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.brocker_list_call, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		CCall call = m_lstCall.get(arg2);
		Intent intent = new Intent();
		//intent.putExtra("name", call.name);
		intent.putExtra("number", call.number);
		setResult(Activity.RESULT_OK, intent);
		finish();// 结束之后会将结果传回From

	}

	public final class ViewHolder {
		TextView m_Name;
		TextView m_Number;
		TextView m_Time;
		TextView m_Type;
	}

	/**
	 * 呼叫对象
	 * 
	 * @author KangLin<kl222@126.com>
	 * 
	 */
	public class CCall implements Comparable<CCall> {
		String name;
		String number;
		int type;
		long time;

		public CCall(String name, String number, int type, long time) {
			super();
			this.name = name;
			this.number = number;
			this.type = type;
			this.time = time;
		}

		public String getType() {
			if (CallLog.Calls.OUTGOING_TYPE == type)
				return getString(R.string.outgoing_type);
			if (CallLog.Calls.INCOMING_TYPE == type)
				return getString(R.string.incoming_type);
			if(0 == type)
				return getString(R.string.sms);
			Log.d(TAG, "getType:" + String.valueOf(type));
			return "";
		}

		// 用于去重
		public boolean equals(Object obj) {
			if (obj instanceof CCall) {
				CCall r = (CCall) obj;
				if (r.number.equals(this.number)) {
					return true;
				}
			}
			return false;
		}

		// 用于去重
		public int hashCode() {
			return this.number.hashCode();
		}

		// 用于排序
		@Override
		public int compareTo(CCall another) {
			return this.time < another.time ? 1
					: (this.time == another.time ? 0 : -1);

		}

	}

	/**
	 * 得到最近呼叫与短信列表
	 *
	 */
	private List<CCall> getList(){
		Set<CCall> setCall = getCallList();
		setCall.addAll(getAllSms());
		// TreeSet排序
		Set<CCall> treeset = new TreeSet<CCall>(setCall);
		return new ArrayList<CCall>(treeset);
	}
	
	/**
	 * 得到呼叫列表
	 * 
	 * @return
	 */
	private Set<CCall> getCallList() {
		// HashSet去重
		Set<CCall> setCall = new HashSet<CCall>();

		Cursor cursor = null;
		try {
			ContentResolver cr = getContentResolver();
			cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[] {
					CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
					CallLog.Calls.TYPE, CallLog.Calls.DATE }, null, null,
					CallLog.Calls.DEFAULT_SORT_ORDER);
			while (cursor.moveToNext()) {
				CCall call = new CCall(
						cursor.getString(cursor
								.getColumnIndex(CallLog.Calls.CACHED_NAME)),
						cursor.getString(cursor
								.getColumnIndex(CallLog.Calls.NUMBER)),
						cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)),
						cursor.getLong(cursor
								.getColumnIndex(CallLog.Calls.DATE)));
				if (!setCall.contains(call))
					setCall.add(call);
			}

		} catch (Exception e) {
			Log.e(TAG, "findBlockerPhoneLog exception:" + e.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return setCall;
	}

	private Set<CCall> getAllSms() {
		Set<CCall> setCall = new HashSet<CCall>();
		ContentResolver cr = this.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		String[] projection = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		Cursor c = null;
		try {
			c = cr.query(uri, projection, null, null, "date desc");
			while (c.moveToNext()) {
				CCall msg = new CCall(c.getString(c.getColumnIndex("person")),
						c.getString(c.getColumnIndex("address")), 0,//短信
						c.getLong(c.getColumnIndex("date")));
				setCall.add(msg);
			}
		} catch (Exception e) {
			Log.e(TAG, "getAllSms exception", e);
		} finally {
			if (c != null)
				c.close();
		}
		return setCall;

	}

	class listAdapter extends BaseAdapter {
		private LayoutInflater m_Inflater;
		private BrockerListCallActivity m_activity;
		private Context m_context;
		
		public listAdapter(BrockerListCallActivity activity) {
			super();
			m_activity = activity;
			m_context = activity.getBaseContext();
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
			CCall call = m_activity.m_lstCall.get(position);
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
				holder.m_Type = (TextView) convertView
						.findViewById(R.id.txtBrockerListCallType);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 更新值
			CCall call = m_activity.m_lstCall.get(position);
			holder.m_Name.setText(CTool.getNameFromPhone(m_context, call.number));
			holder.m_Number.setText(CTool.getShowPhone(m_context, call.number));
			holder.m_Type.setText(call.getType());
			holder.m_Time.setText(CTool.formatTimeStampString(
					m_activity.getBaseContext(), call.time, false));

			return convertView;
		}

	}

}
