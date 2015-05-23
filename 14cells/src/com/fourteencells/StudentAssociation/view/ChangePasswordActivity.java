package com.fourteencells.StudentAssociation.view;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.*;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * 页面功能：修改密码
 * 
 * @author 师春雷
 * 
 */
public class ChangePasswordActivity extends BaseActivity {

	private final static String TAG = "ChangePasswordActivity";

	/** 返回按钮 */
	private ImageView back;
	/** 标头 */
	private TextView titleText;
	/** 完成 */
	private TextView affirm;

	private ProgressDialog loading;

	private EditText old_password;
	private EditText new_password;
	private EditText new_password_to;

	private String token;

	private String password;
	private String password_to;
	private String password_old;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		findViews();
		setListener();
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		titleText = (TextView) findViewById(R.id.title_text);
		titleText.setText(R.string.change_password);
		affirm = (TextView) findViewById(R.id.title_right);
		affirm.setText(R.string.complete);

		old_password = (EditText) findViewById(R.id.in_old_pwd);
		new_password = (EditText) findViewById(R.id.in_new_pwd);
		new_password_to = (EditText) findViewById(R.id.in_new_pwd_to);

		token = (String) SPUtils.get(context, "token", "");
	}

	private void setListener() {

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		affirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				password_old = old_password.getText().toString().trim();
				password = new_password.getText().toString().trim();
				password_to = new_password_to.getText().toString().trim();

				if (null == password_old || password_old.equals("")) {
					showToast("原始密码不能为空！");
				} else if (null == password || password.equals("")) {
					showToast("新密码不能为空！");
				} else if (password.length() < 8) {
					showToast("密码长度不能小于8位！");
				} else if (!password.equals(password_to)) {
					showToast("两次输入的密码不一致，请重新输入！");
				} else {
					loading = ProgressDialog.show(context, "请稍后...");
					loading.show();
					changePassword(password, token);
				}
			}
		});
	}

	/**
	 * 修改密码
	 * 
	 * @param password
	 * @param token
	 */
	protected void changePassword(String password, String token) {
		AjaxParams params = new AjaxParams();
		params.put("password", password);
		params.put("auth_token", token);

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.put(HttpUtils.ROOT_URL + HttpUtils.CHANGE_PASSWORD, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();

						Log.i(TAG, str);

						Result result = (Result) JsonUtils.fromJson(str,
								Result.class);
						if (result.getResultcode() == 200) {
							showToast("修改成功！");
							finish();
						} else if (result.getResultcode() == 101) {
							showToast("修改失败，请稍候再试！");
						}
						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						loading.dismiss();
						showToast("网络请求失败，请稍候再试！");
					}
				});
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}
