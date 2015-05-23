package com.fourteencells.StudentAssociation.view;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.fourteencells.StudentAssociation.MyApplication;
import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.Tools;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 页面功能：用户进行注册（第一步）
 * 
 * @author 师春雷
 * 
 */
public class RegistActivity extends BaseActivity {

	private static final String TAG = "RegistActivity";

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 进行下一步 */
	private Button regist;
	/** 邮箱输入框 */
	private EditText email;
	/** 邮箱 */
	private String str_email;
	/** 密码输入框 */
	private EditText password;
	/** 密码 */
	private String str_password;

	/** 解析结果 */
	private Result result;

	private ProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);

		MyApplication.getInstance().addActivity(this);

		findViews();
		setListener();
	}

	private void findViews() {

		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.regist);

		email = (EditText) findViewById(R.id.et_regist_email);
		password = (EditText) findViewById(R.id.et_regist_password);
		regist = (Button) findViewById(R.id.btn_regist);
	}

	private void setListener() {
		regist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				str_email = email.getText().toString();
				str_password = password.getText().toString();

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
							.show(RegistActivity.this, "请稍后...");
					loading.show();

					HostToServer();
				}
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openActivity(LoginActivity.class, true);
			}
		});
	}

	/**
	 * 初步注册
	 */
	private void HostToServer() {
		AjaxParams params = new AjaxParams();
		params.put("email", str_email);
		params.put("password", str_password);
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.post(HttpUtils.ROOT_URL + HttpUtils.REGIST, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();
						Log.i(TAG, "注册返回结果" + str);
						result = (Result) JsonUtils
								.fromJson(str, Result.class);

						if (result.getResultcode() == 101) {
							if (result.getError().equals("email_exist")) {
								showToast("邮箱已被占用，请重新输入！");
							}
						} else if (result.getResultcode() == 200) {
							SPUtils.put(RegistActivity.this, "token", result
									.getUser().getAuthentication_token());
							SPUtils.put(RegistActivity.this, "email", str_email);
							SPUtils.put(RegistActivity.this, "id", result
									.getUser().getId());
							Log.i(TAG, result.getUser()
									.getAuthentication_token());

							openActivity(RegistSecondActivity.class, "email",
									str_email, true);
						}

						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						showToast("请求数据失败，请稍候再试");
						loading.dismiss();
					}
				});
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openActivity(LoginActivity.class, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
