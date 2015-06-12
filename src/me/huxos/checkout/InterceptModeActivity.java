package me.huxos.checkout;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.CSystemInformation;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * 拦截模式设置窗口
 * @author KangLin<kl222@126.com>
 *
 */
public class InterceptModeActivity extends Activity {
	private static final String TAG = "InterceptModeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intercept_mode);

		DBHelper db = DBHelper.getInstance(this.getBaseContext());
		CSystemInformation info = db.getSystemInformation();
		//初始化拦截方向按钮
		directionToRadioButton(info.getInterceptionDirection())
				.setChecked(true);
		//设置点击事件
		RadioGroup rbDirection = (RadioGroup) findViewById(R.id.rgInterceptModeInterceptDirection);
		rbDirection.setOnCheckedChangeListener(onDirection);
		
		typeToRadioButton(info.getInterceptionType()).setChecked(true);
		RadioGroup rbType = (RadioGroup) findViewById(R.id.rgInterceptModeInterceptType);
		rbType.setOnCheckedChangeListener(onType);
		
		conditionToRadioButton(info.getInterceptionCondition()).setChecked(true);
		RadioGroup rbCondition = (RadioGroup) findViewById(R.id.rgInterceptModeInterceptCondition);
		rbCondition.setOnCheckedChangeListener(onCondition);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.intercept_mode, menu);
		return true;
	}

	//从id转换成方向常量
	private int directionFromId(int id) {
		RadioButton rbIncoming = (RadioButton) findViewById(R.id.rgInterceptModeInterceptDirectionIncoming);
		if (rbIncoming.getId() == id)
			return CSystemInformation.InterceptionDirectionIncoming;
		RadioButton rbOutgoing = (RadioButton) findViewById(R.id.rgInterceptModeInterceptDirectionOutgoing);
		if (rbOutgoing.getId() == id)
			return CSystemInformation.InterceptionDirectionOutgoing;
		RadioButton rbDouble = (RadioButton) findViewById(R.id.rgInterceptModeInterceptDirectionDouble);
		if (rbDouble.getId() == id)
			return CSystemInformation.InterceptionDirectionDouble;

		return CSystemInformation.InterceptionDirectionIncoming;
	}
	//从常量找到按钮控件
	private RadioButton directionToRadioButton(int d) {
		switch (d) {
		case CSystemInformation.InterceptionDirectionIncoming:
			return (RadioButton) findViewById(R.id.rgInterceptModeInterceptDirectionIncoming);

		case CSystemInformation.InterceptionDirectionOutgoing:
			return (RadioButton) findViewById(R.id.rgInterceptModeInterceptDirectionOutgoing);
		case CSystemInformation.InterceptionDirectionDouble:
			return (RadioButton) findViewById(R.id.rgInterceptModeInterceptDirectionDouble);
		}
		return (RadioButton) findViewById(R.id.rgInterceptModeInterceptDirectionIncoming);
	}
	//点击事件
	private RadioGroup.OnCheckedChangeListener onDirection = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			Log.d(TAG, "checkedID:" + String.valueOf(checkedId));
			CSystemInformation info = new CSystemInformation();
			info.setInterceptionDirection(directionFromId(checkedId));
			DBHelper db = DBHelper.getInstance(getBaseContext());
			db.updateSystemInformation(info);
		}

	};

	private int typeFromId(int id) {
		RadioButton btnNormal = (RadioButton) findViewById(R.id.rgInterceptModeInterceptTypeNormal);
		if (btnNormal.getId() == id)
			return CSystemInformation.InterceptionTypeNormal;

		RadioButton btnPrompt = (RadioButton) findViewById(R.id.rgInterceptModeInterceptTypePrompt);
		if (btnPrompt.getId() == id)
			return CSystemInformation.InterceptioinTypePrompt;
		
		RadioButton btnNo = (RadioButton) findViewById(R.id.rgInterceptModelInterceptTypeNo);
		if (btnNo.getId() == id)
			return CSystemInformation.InterceptioinTypeNo;
		return CSystemInformation.InterceptionTypeNormal;
	}

	private RadioButton typeToRadioButton(int d) {
		switch (d) {
		case CSystemInformation.InterceptioinTypePrompt:
			return (RadioButton) findViewById(R.id.rgInterceptModeInterceptTypePrompt);
		case CSystemInformation.InterceptionTypeNormal:
		default:
			return (RadioButton) findViewById(R.id.rgInterceptModeInterceptTypeNormal);
		}
	}

	private RadioGroup.OnCheckedChangeListener onType = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			CSystemInformation info = new CSystemInformation();
			info.setInterceptionType(typeFromId(checkedId));
			DBHelper db = DBHelper.getInstance(getBaseContext());
			db.updateSystemInformation(info);

		}

	};
	
	private int conditionFromId(int id) {
		RadioButton btnNormal = (RadioButton) findViewById(R.id.rgInterceptModeInterceptConditionNormal);
		if (btnNormal.getId() == id)
			return CSystemInformation.InterceptionConditionNormal;

		RadioButton btnNoContact = (RadioButton) findViewById(R.id.rgInterceptModeInterceptConditionNoContact);
		if (btnNoContact.getId() == id)
			return CSystemInformation.InterceptionConditionNoContact;
		return CSystemInformation.InterceptionConditionNormal;
	}

	private RadioButton conditionToRadioButton(int d) {
		switch (d) {
		case CSystemInformation.InterceptionConditionNoContact:
			return (RadioButton) findViewById(R.id.rgInterceptModeInterceptConditionNoContact);
		case CSystemInformation.InterceptionConditionNormal:
		default:
			return (RadioButton) findViewById(R.id.rgInterceptModeInterceptConditionNormal);
		}
	}
	private RadioGroup.OnCheckedChangeListener onCondition = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			CSystemInformation info = new CSystemInformation();
			info.setInterceptionCondition(conditionFromId(checkedId));
			DBHelper db = DBHelper.getInstance(getBaseContext());
			db.updateSystemInformation(info);

		}

	};
}
