package me.huxos.checkout;

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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BrockerListActivity extends Activity {
	private static final String TAG = "BrockerListActivity";
	static final int PICK_CONTACT_BLACKLIST = 0;
	static final int PICK_CONTACT_WHITELIST = 1;
	static final int PICK_CALL_BLACKLIST = 2;
	static final int PICK_CALL_WHITELIST = 3;
	boolean m_isWhite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brocker_list);
		Intent intent = this.getIntent();
		m_isWhite = intent.getStringExtra("isWhitelist").equals("true");
		TextView text = (TextView) findViewById(R.id.txtBrockerList);
		if (m_isWhite)
			text.setText(R.string.whitelist);
		else
			text.setText(R.string.blacklist);
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
		Intent intent=new Intent(Intent.ACTION_CALL_BUTTON);
		if (m_isWhite)
			startActivityForResult(intent, PICK_CALL_WHITELIST);
		else
			startActivityForResult(intent, PICK_CALL_BLACKLIST);
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
				String number = c.getString(0);
				// String name = c.getString(1);
				CBrockerlist list = new CBrockerlist(number, 1, 1);
				db.updateBrockerList(list, requestCode == 1 ? true : false);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
	}

	private void updateGirdView() {

	}

	class myAdapter extends BaseAdapter {
		private Context context;

		public myAdapter(Context context) {
			super();
			this.context = context;
			DBHelper db = DBHelper.getInstance(this.context);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
