package com.fourteencells.StudentAssociation.view;

import java.io.File;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.Tools;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends BaseActivity {

	private static final String TAG = "LoginActivity";

	/** 登录按钮 */
	private ImageView login;
	/** 注册 */
	private ImageView register;
	private MyCircleImageView herderpic;
	/** 登录邮箱 */
	private EditText email;
	private String str_email;
	/** 登录密码 */
	private EditText password;
	private String str_password;

	private Result result;

	/** 头像本地文件路径 */
	private String imagePath, imagePathInternet;

	private ProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViews();

		setListener();
	}

	/**
	 * 初始化控件
	 */
	private void findViews() {
		imagePath = (String) SPUtils.get(LoginActivity.this, "imagepath", "");
		imagePathInternet = (String) SPUtils.get(LoginActivity.this,
				"intentpath", "");

		email = (EditText) findViewById(R.id.et_login_email);
		password = (EditText) findViewById(R.id.et_login_password);

		register = (ImageView) findViewById(R.id.img_login_register);
		login = (ImageView) findViewById(R.id.img_login_login);

		herderpic = (MyCircleImageView) findViewById(R.id.img_login_headerpic);

		FinalBitmap fb = FinalBitmap.create(getApplicationContext());

		if (imagePathInternet.length() > 0) {
			fb.display(herderpic, imagePathInternet);
		} else if (imagePath.length() > 0) {
			File file = new File(imagePath);
			if (file.exists()) {
				Bitmap bitmap = Tools.getLocalImage(imagePath);
				herderpic.setImageBitmap(bitmap);
			}
		}

		str_email = (String) SPUtils.get(LoginActivity.this, "email", "");
		email.setText(str_email);
	}

	/**
	 * 本页面按钮功能
	 */
	private void setListener() {
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				str_email = email.getText().toString().trim();
				str_password = password.getText().toString().trim();

				if (null == str_email || str_email.equals("")) {
					showToast("邮箱不能为空！");
				} else if (null == str_password || str_password.equals("")) {
					showToast("密码不能为空！");
				} else if (str_password.length() < 8) {
					showToast("密码长度不能小于8位！");
				} else if (!Tools.isEmail(str_email)) {
					showToast("邮箱格式错误！");
				} else {
					loading = ProgressDialog
							.show(LoginActivity.this, "正在登录...");
					loading.show();
					LoginPost(str_email, str_password);
				}
			}
		});

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openActivity(RegistActivity.class, true);
			}
		});
	}

	/**
	 * 登录
	 * 
	 * @param email
	 * @param password
	 */
	protected void LoginPost(final String email, final String password) {
		FinalHttp fh = new FinalHttp();

		AjaxParams params = new AjaxParams();

		params.put("email", email);
		params.put("password", password);
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.post(HttpUtils.ROOT_URL + HttpUtils.LOGIN, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();
						Log.i(TAG, "返回结果：" + str);

						result = (Result) JsonUtils
								.fromJson(str, Result.class);

						if (result.getResultcode() == 101) {
							if (result.getError().equals("password_error")) {
								showToast("密码错误！");
							} else if (result.getError().equals("username_error")) {
								showToast("邮箱错误！");
							}
						} else if (result.getResultcode() == 200) {
							showToast("登录成功！");

							SPUtils.put(LoginActivity.this, "email", email);
							SPUtils.put(LoginActivity.this, "password",
									password);
							SPUtils.put(LoginActivity.this, "id",
									result.getUser().getId());
							SPUtils.put(LoginActivity.this, "name",
									result.getUser().getName());
							SPUtils.put(LoginActivity.this, "sex",
									result.getUser().getGender());
							SPUtils.put(LoginActivity.this, "school",
									result.getUser().getSchool());
							SPUtils.put(LoginActivity.this, "college",
									result.getUser().getCollege());
							SPUtils.put(LoginActivity.this, "schooltime",
									result.getUser().getEnrollment());
							SPUtils.put(LoginActivity.this, "schoolnum",
									result.getUser().getSchool_no());
							SPUtils.put(LoginActivity.this, "token",
									result.getUser().getAuthentication_token());
							SPUtils.put(LoginActivity.this, "intentpath",
									result.getUser().getHeadpic().getUrl());

							openActivity(MainActivity.class, true);
						}

						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							loading.dismiss();
							showToast("网络环境不稳定，请稍后再试");
						}
					}
				});
	}
}
