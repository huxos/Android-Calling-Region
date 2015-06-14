package me.huxos.checkout;

import java.util.List;
import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CBlockerSmsKeyword;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 增加短信关键字列表界面
 * 
 * @author KangLin<kl222@126.com>
 * 
 */
public class BrockerSmsKeyWordListActivity extends Activity {
	private static final String TAG = "BrockerSmsKeyWordListActivity";
	boolean m_isWhite;
	private listAdapter m_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brocker_sms_key_word_list);
		Intent intent = this.getIntent();
		m_isWhite = intent.getStringExtra("isWhitelist").equals("true");
		if (m_isWhite)
			this.setTitle(R.string.brocker_sms_keyword_whitelist);
		else
			this.setTitle(R.string.brocker_sms_keyword_blacklist);

		// 设置listview数据适配器
		ListView listView = (ListView) findViewById(R.id.lvBrockerSmsKeyWordlistView);
		m_adapter = new listAdapter(this.getBaseContext(), m_isWhite);
		listView.setAdapter(m_adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.brocker_sms_key_word_list, menu);
		return true;
	}

	// 增加按钮事件
	public void onAdd(View source) {
		String keyword, number;
		TextView text = (TextView) findViewById(R.id.edtBrockerSmsKeyWordKeyWord);
		keyword = text.getText().toString();
		text = (TextView) findViewById(R.id.edtBrockerSmsKeyWordPhone);
		number = text.getText().toString();
		if (null == keyword || keyword.isEmpty())
			return;
		CBlockerSmsKeyword key = new CBlockerSmsKeyword(keyword, number);
		DBHelper db = DBHelper.getInstance(this.getBaseContext());
		db.insertBrockerKeyWord(key, m_isWhite);
		m_adapter.UpdateDate();

	}

	public final class ViewHolder {
		TextView m_Keyword;
		TextView m_Number;
		CheckBox m_Enable;
		Button m_Delete;
	}

	class listAdapter extends BaseAdapter {
		private LayoutInflater m_Inflater;
		private Context m_context;
		private DBHelper m_db;
		private boolean m_isWhite;
		private List<CBlockerSmsKeyword> m_Brockerlist;

		public listAdapter(Context context, boolean isWhite) {
			super();
			this.m_context = context;
			this.m_Inflater = LayoutInflater.from(context);
			m_isWhite = isWhite;
			m_db = DBHelper.getInstance(this.m_context);
			m_Brockerlist = m_db.getAllBrockerKeyWord(m_isWhite, false);
		}

		// 更新数据
		public void UpdateDate() {
			m_Brockerlist = m_db.getAllBrockerKeyWord(m_isWhite, false);
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
				convertView = m_Inflater.inflate(R.layout.brocker_sms_key_word,
						null);
				holder.m_Keyword = (TextView) convertView
						.findViewById(R.id.txtBrockerSmsKeyWordlistViewKeyWord);
				holder.m_Number = (TextView) convertView
						.findViewById(R.id.txtBrockerSmsKeyWordlistviewNumber);
				holder.m_Enable = (CheckBox) convertView
						.findViewById(R.id.ckbBrockerSmsKeyWordlistViewEnable);
				holder.m_Delete = (Button) convertView
						.findViewById(R.id.btnBrockerSmsKeyWordDelete);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 更新值
			CBlockerSmsKeyword brockerList = m_Brockerlist.get(position);
			holder.m_Keyword.setText(brockerList.getKeyword());
			holder.m_Number.setText(CTool.getShowPhone(m_context,
					brockerList.getPhone_number()));
			holder.m_Enable.setChecked(brockerList.getEnable() == 1 ? true
					: false);

			// 设置事件监听
			class CCheckedChangeListener implements OnCheckedChangeListener {
				private int m_position;
				private listAdapter m_this;

				CCheckedChangeListener(listAdapter listAdapter, int position) {
					super();
					this.m_position = position;
					m_this = listAdapter;
				}

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					CBlockerSmsKeyword keyword = m_this.m_Brockerlist
							.get(m_position);
					if (isChecked)
						keyword.setEnable(1);
					else
						keyword.setEnable(0);
					DBHelper db = DBHelper.getInstance(m_this.m_context);
					db.updateBrockerKeyWord(keyword, m_this.m_isWhite);
				}
			}

			holder.m_Enable
					.setOnCheckedChangeListener(new CCheckedChangeListener(
							this, position));

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
					CBlockerSmsKeyword brockerlist = m_this.m_Brockerlist
							.get(m_position);
					DBHelper db = DBHelper.getInstance(m_this.m_context);
					db.deleteBrockerKeyWord(brockerlist, m_this.m_isWhite);
					UpdateDate();
				}

			}
			holder.m_Delete.setOnClickListener(new CClickListener(this,
					position));

			return convertView;
		}

	}
}
