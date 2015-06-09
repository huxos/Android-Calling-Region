package me.huxos.checkout;

import java.util.List;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBrockerlist;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class BrockerListActivity extends Activity {
	private static final String TAG = "BrockerListActivity";
	static final int PICK_CONTACT_BLACKLIST = 0;
	static final int PICK_CONTACT_WHITELIST = 1;
	static final int PICK_CALL_BLACKLIST = 2;
	static final int PICK_CALL_WHITELIST = 3;
	boolean m_isWhite;
	private listAdapter m_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brocker_list);
		Intent intent = this.getIntent();
		m_isWhite = intent.getStringExtra("isWhitelist").equals("true");
		TextView text = (TextView) findViewById(R.id.txtBrocklistBrockerList);
		if (m_isWhite)
			text.setText(R.string.whitelist);
		else
			text.setText(R.string.blacklist);

		// 设置listview数据适配器
		ListView listView = (ListView) findViewById(R.id.lstBrockerlistView);
		LayoutInflater layout = LayoutInflater.from(this.getBaseContext());
		/*listView.addHeaderView(layout.inflate(R.layout.brocker_list_view_head,
				null));*/
		m_adapter = new listAdapter(this.getBaseContext(), m_isWhite);
		listView.setAdapter(m_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.brocker_list, menu);
		return true;
	}

	/**
	 * 从通信薄加载按钮事件
	 * 
	 * @param source
	 */
	public void onFromContact(View source) {
		// 打开通信薄界面
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		if (m_isWhite)
			startActivityForResult(intent, PICK_CONTACT_WHITELIST);
		else
			startActivityForResult(intent, PICK_CONTACT_BLACKLIST);
	}

	/**
	 * 从最近通话记录加载按钮事件
	 * 
	 * @param source
	 */
	public void onFromCallRecords(View source) {
		Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
		if (m_isWhite)
			startActivityForResult(intent, PICK_CALL_WHITELIST);
		else
			startActivityForResult(intent, PICK_CALL_BLACKLIST);
	}

	public void onAdd(View source) {
		TextView text = (TextView) findViewById(R.id.edtBrockerName);
		String name = text.getText().toString();
		text = (TextView) findViewById(R.id.edtBrockerNumber);
		String number = text.getText().toString();
		Log.d(TAG, "onAdd:name:" + name + ";number:" + number);
		CBrockerlist brockerlist = new CBrockerlist(number, name, 1, 1);
		DBHelper db = DBHelper.getInstance(this.getBaseContext());
		db.updateBrockerList(brockerlist, m_isWhite);
		m_adapter.UpdateDate();
	}

	// Handle result from the contact picker
	@SuppressLint("InlinedApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		// Define our database manager
		DBHelper db = DBHelper.getInstance(this.getBaseContext());
		if (null == data)
			return;
		Uri uri = data.getData();
		if (null == uri)
			return;
		Cursor c = null;
		try {
			c = getContentResolver()
					.query(uri,
							new String[] {
									ContactsContract.CommonDataKinds.Phone.NUMBER,
									ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME },
							null, null, null);

			if (c != null && c.moveToFirst()) {
				String number = c
						.getString(c
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				String name = c
						.getString(c
								.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME));
				CBrockerlist list = new CBrockerlist(number, name, 1, 1);
				db.updateBrockerList(list, requestCode == 1 ? true : false);
			}
		} catch (Exception e) {
			Log.e(TAG, "onActivityResult Exception:" + e.getMessage(), e);
		} finally {
			if (c != null) {
				c.close();
			}
		}
		m_adapter.UpdateDate();
	}

	public final class ViewHolder {
		TextView m_Name;
		TextView m_Number;
		CheckBox m_PhoneEnable;
		CheckBox m_SmsEnable;
		Button m_Delete;
	}

	class listAdapter extends BaseAdapter {
		private LayoutInflater m_Inflater;
		private Context m_context;
		private boolean m_isWhite;
		private DBHelper m_db;
		private List<CBrockerlist> m_Brockerlist;

		public listAdapter(Context context, boolean isWhite) {
			super();
			this.m_context = context;
			this.m_Inflater = LayoutInflater.from(context);
			m_isWhite = isWhite;
			m_db = DBHelper.getInstance(this.m_context);
			m_Brockerlist = m_db.findAllBrockerList(isWhite);
		}

		// 更新数据
		public void UpdateDate() {
			m_Brockerlist = m_db.findAllBrockerList(m_isWhite);
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
				convertView = m_Inflater.inflate(R.layout.brocker_list_view,
						null);
				holder.m_Name = (TextView) convertView
						.findViewById(R.id.txtBrockerListViewName);
				holder.m_Number = (TextView) convertView
						.findViewById(R.id.txtBrockerListViewPhone);
				holder.m_PhoneEnable = (CheckBox) convertView
						.findViewById(R.id.cbBrockerListViewPhoneEnable);
				holder.m_SmsEnable = (CheckBox) convertView
						.findViewById(R.id.cbBrockerListViewSMSEnable);
				holder.m_Delete = (Button) convertView
						.findViewById(R.id.btnBrockerListViewDelete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 更新值
			CBrockerlist brockerList = m_Brockerlist.get(position);
			holder.m_Number.setText(brockerList.getPhone_number());
			String name = CTool.getNameFromPhone(m_context,
					brockerList.getPhone_number());
			holder.m_Name.setText(name);
			holder.m_PhoneEnable
					.setChecked(brockerList.getPhone_enable() != 0 ? true
							: false);
			holder.m_SmsEnable
					.setChecked(brockerList.getSms_enable() != 0 ? true : false);

			// 设置事件监听
			class CCheckedChangeListener implements OnCheckedChangeListener {
				private int m_position;
				private listAdapter m_this;
				private boolean m_isPhone;

				CCheckedChangeListener(listAdapter listAdapter, int position,
						boolean isPhone) {
					super();
					this.m_position = position;
					m_this = listAdapter;
					m_isPhone = isPhone;
				}

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					CBrockerlist brockerlist = m_this.m_Brockerlist
							.get(m_position);
					if (m_isPhone)
						brockerlist.setPhone_enable(isChecked ? 1 : 0);
					else
						brockerlist.setSms_enable(isChecked ? 1 : 0);
					DBHelper db = DBHelper.getInstance(m_this.m_context);
					db.updateBrockerList(brockerlist, m_this.m_isWhite);
				}
			}

			holder.m_PhoneEnable
					.setOnCheckedChangeListener(new CCheckedChangeListener(
							this, position, true));
			holder.m_SmsEnable
					.setOnCheckedChangeListener(new CCheckedChangeListener(
							this, position, false));

			class CClickListener implements View.OnClickListener {
				private int m_position;
				private listAdapter m_this;

				public CClickListener(listAdapter listAdapter, int position) {
					super();
					this.m_position = position;
					m_this = listAdapter;
				}

				@Override
				public void onClick(View v) {
					CBrockerlist brockerlist = m_this.m_Brockerlist
							.get(m_position);
					DBHelper db = DBHelper.getInstance(m_this.m_context);
					db.DeleteBrockerlist(brockerlist, m_this.m_isWhite);
					UpdateDate();
				}

			}
			holder.m_Delete.setOnClickListener(new CClickListener(this,
					position));

			return convertView;
		}

	}
}
