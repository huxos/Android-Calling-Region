package me.huxos.checkout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cabe.lib.cache.CacheSource;
import com.cabe.lib.cache.interactor.impl.SimpleViewPresenter;

import java.util.List;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.PhoneArea;
import me.huxos.checkout.entity.PhoneService;
import me.huxos.checkout.usecase.DBLocationUseCase;
import me.huxos.checkout.usecase.ServiceUpdateUseCase;
import me.huxos.checkout.usecase.WebLocationUseCase;
import me.huxos.checkout.utils.PermissionUtils;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends Activity {

	private static DBHelper helper;
	private TextView textView5;
	private TextView textView6;
	private TextView textView3;
	private ProgressBar progressBar1;
	private ProgressBar progressBar2;
	private MenuItem menuItem;
	private boolean using_network;

	private CompositeSubscription cs = new CompositeSubscription();

	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView5 = (TextView) findViewById(R.id.textView5);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView6 = (TextView) findViewById(R.id.textView6);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar2 = (ProgressBar) findViewById(R.id.ProgressBar2);

		// 初次使用将准备好的数据库文件考入到系统目录共程序使用
		DBHelper.copyDB(getBaseContext());
		// 获得数据库连接
		helper = DBHelper.getInstance(this);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PermissionUtils.checkAppPermissions(this, true);
		}
	}

	/**
	 * 返回到界面时调用
	 */
	@Override
	protected void onResume() {
		super.onResume();
		using_network = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("using_network", false);
		if (using_network) {
			if (textView5.getText().toString().equals("[未开启网络查询]"))
				textView5.setText(R.string.input_phone_number);
		} else
			textView5.setText(R.string.without_using_network);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cs.unsubscribe();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menuItem = menu.findItem(R.id.action_save);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// 设置菜单
		case R.id.action_settings:
			actionSetting();
			break;
		// 关于菜单
		case R.id.action_about:
			actionAbout();
			break;
		// 更新到本地
		case R.id.action_save:
			actionSavePhone();
			break;
		case R.id.action_service_update:
			actionUpdateService();
			break;
		}
		return true;
	}

	private void actionSetting() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void actionSavePhone() {
		PhoneArea phoneArea = new PhoneArea(Integer.parseInt(textView6.getText().toString()), textView5.getText().toString());
		if (helper.saveOrUpdatePhoneArea(phoneArea)) {
			Toast.makeText(this, "更新到本地成功", Toast.LENGTH_SHORT).show();
			textView3.setText(phoneArea.getArea());
		} else {
			Toast.makeText(this, "更新到本地失败", Toast.LENGTH_SHORT).show();
		}
	}

	private void actionUpdateService() {
		ServiceUpdateUseCase useCase = new ServiceUpdateUseCase();
		Subscription sc = useCase.execute(new SimpleViewPresenter<List<PhoneService>>(){
			@Override
			public void load(CacheSource from, List<PhoneService> data) {
				super.load(from, data);
			}
		});
		cs.add(sc);
	}

	private void actionAbout() {
		LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		final View dialog_view = inflater.inflate(R.layout.dialog_view, null);
		new AlertDialog.Builder(this).setTitle(R.string.action_about)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(dialog_view).setPositiveButton("确定", null).show();
	}

	/**
	 * 查询按钮触发事件
	 */
	public void query(View view) {
		EditText editText = (EditText) findViewById(R.id.editText1);
		String phoneNumber = editText.getText().toString();
		if (phoneNumber.length() >= 7 && phoneNumber.length() <= 11) {
			// 重置保存按钮
			menuItem.setEnabled(false);

			progressBar1.setVisibility(ProgressBar.VISIBLE);
			textView3.setText(R.string.loading);

			DBLocationUseCase useCase = new DBLocationUseCase(helper, phoneNumber);
			Subscription sc = useCase.execute(new SimpleViewPresenter<PhoneArea>() {
				private PhoneArea phoneArea;
				@Override
				public void load(CacheSource from, PhoneArea data) {
					this.phoneArea = data;
				}
				@Override
				public void error(CacheSource from, int code, String info) {
					super.error(from, code, info);
					Log.w("MainActivity", "error:" + info);
				}
				@Override
				public void complete(CacheSource from) {
					if (phoneArea != null) {
						textView3.setText(phoneArea.getArea());
					} else {
						textView3.setText(R.string.none_area);
					}
					progressBar1.setVisibility(View.GONE);
				}
			});
			cs.add(sc);

			textView6.setVisibility(View.VISIBLE);
			textView6.setText(phoneNumber.substring(0, 7));
			textView6.setTextSize(40);
			// 清空输入框
			editText.setText("");

			//使用网络查询
			if (using_network) {
				getLocationFromWeb(phoneNumber);
			}
		} else {
			Toast.makeText(this, "输入7到11位手机号码", Toast.LENGTH_SHORT).show();
		}
	}

	private void getLocationFromWeb(String phoneNum) {
		textView5.setText(R.string.loading);
		progressBar2.setVisibility(ProgressBar.VISIBLE);

		WebLocationUseCase useCase = new WebLocationUseCase(phoneNum);
		Subscription sc = useCase.execute(new SimpleViewPresenter<PhoneArea>(){
			private PhoneArea phoneArea;
			@Override
			public void load(CacheSource from, PhoneArea data) {
				this.phoneArea = data;
			}
			@Override
			public void complete(CacheSource from) {
				if (phoneArea != null && phoneArea.getArea() != null) {
					String area = phoneArea.getArea();
					textView5.setText(area);
					//网络查询结果与本地不一致是，将「更新到本地」菜单设置为可以点击
					if (!area.equals(textView3.getText().toString())) {
						menuItem.setEnabled(true);
					}
				} else
					textView5.setText(R.string.none_area);
				progressBar2.setVisibility(ProgressBar.GONE);
			}
		});
		cs.add(sc);
	}

}