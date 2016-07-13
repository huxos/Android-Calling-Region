package me.huxos.checkout;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.PhoneArea;
import me.huxos.checkout.entity.Product;
import me.huxos.checkout.utils.PermissionUtils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static DBHelper helper;
	private TextView textView5;
	private TextView textView6;
	private TextView textView3;
	private ProgressBar progressBar1;
	private ProgressBar progressBar2;
	private MenuItem menuItem;
	private boolean using_network;
	private Toast toast;

	public MainActivity() {
	}

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
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		// 关于菜单
		case R.id.action_about:
			LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
			final View dialog_view = inflater.inflate(R.layout.dialog_view,
					null);
			new AlertDialog.Builder(this).setTitle(R.string.action_about)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(dialog_view).setPositiveButton("确定", null).show();
			break;
		// 更新到本地
		case R.id.action_save:
			PhoneArea phoneArea = new PhoneArea(Integer.parseInt(textView6
					.getText().toString()), textView5.getText().toString());
			if (helper.saveOrUpdatePhoneArea(phoneArea)) {
				toast = Toast.makeText(this, "更新到本地成功", Toast.LENGTH_SHORT);
				toast.show();
				textView3.setText(phoneArea.getArea());
			} else {
				toast = Toast.makeText(this, "更新到本地失败", Toast.LENGTH_SHORT);
				toast.show();
			}
			break;
		}
		return true;
	}

	/**
	 * 查询按钮触发事件
	 * 
	 * @param view
	 */
	public void query(View view) {

		EditText editText = (EditText) findViewById(R.id.editText1);
		String phoneNumber = editText.getText().toString();
		// 去掉非数字字符
		phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
		if (phoneNumber.length() >= 7 && phoneNumber.length() <= 11) {
			// 截取前面7个数字
			String phoneNumberShort = phoneNumber.substring(0, 7);

			// 重置保存按钮
			menuItem.setEnabled(false);

			progressBar1.setVisibility(ProgressBar.VISIBLE);
			textView3.setText(R.string.loading);

			textView6.setVisibility(View.VISIBLE);
			textView6.setText(phoneNumberShort);
			textView6.setTextSize(40);
			// 清空输入框
			editText.setText("");

			// 查询数据库
			PhoneArea phoneArea;
			if ((phoneArea = helper.findPhoneArea(new String[] { phoneNumberShort
					.toString() })) != null) {

				textView3.setText(phoneArea.getArea());

			} else
				textView3.setText(R.string.none_area);

			progressBar1.setVisibility(View.GONE);

			//使用网络查询
			if (using_network) {
				textView5.setText(R.string.loading);
				progressBar2.setVisibility(ProgressBar.VISIBLE);
				new RemoteHelper(this).execute(phoneNumber);
			}

		} else {
			toast = Toast.makeText(this, "输入7到11位手机号码", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	// 使用网络查询归属地数据库
	class RemoteHelper extends AsyncTask<String, Void, PhoneArea> {

		public RemoteHelper(Context context) {

		}

		@Override
		protected PhoneArea doInBackground(String... params) {
			PhoneArea phoneArea = null;
			// 财付通手机归属地api
			String path = "http://life.tenpay.com/cgi-bin/mobile/MobileQueryAttribution.cgi?chgmobile="
					+ params[0];
			Log.i("RemoteHelper", "url:" + path);
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(path);
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				if (is != null) {
					try {
						//解析XML 
						List<Product> products = parseXML(is);
						if (products.size() == 1) {
							Product product = products.get(0);
							String phonenum = product.getPhonenum();
							StringBuffer location = new StringBuffer(
									product.getLocation());
							
							phoneArea = new PhoneArea(Integer.parseInt(phonenum
									.substring(0, 7)), location.toString()
									.replaceAll(" ", ""));
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return phoneArea;
		}

		private List<Product> parseXML(InputStream inputStream)
				throws XmlPullParserException, IOException {
			List<Product> products = new ArrayList<Product>();
			Product product = null;
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inputStream, "GBK");
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				Log.d("RemoteHelper", "parser.getName:" + parser.getName());
				switch (event) {
				case XmlPullParser.START_TAG:
					if ("root".equals(parser.getName())) {
						product = new Product();
					} else if ("chgmobile".equals(parser.getName())) {
						product.setPhonenum(parser.nextText());
					} else if ("city".equals(parser.getName())) {
						product.setCity(parser.nextText());
						Log.d("RemoteHelper", "city:" + product.getLocation());
					} else if("province".equals(parser.getName())) {
						product.setProvince(parser.nextText());
						Log.d("RemoteHelper", "city:" + product.getLocation());
					} else if("supplier".equals(parser.getName())) {
						product.setSupplier(parser.nextText());
						Log.d("RemoteHelper", "city:" + product.getLocation());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("root".equals(parser.getName())) {
						products.add(product);
						product = null;
					}
					break;
				}
				event = parser.next();
			}
			return products;
		}

		/**
		 * 返回时调用
		 */
		@Override
		protected void onPostExecute(PhoneArea phoneArea) {
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
	}

}