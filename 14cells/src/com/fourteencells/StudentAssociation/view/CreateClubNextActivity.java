package com.fourteencells.StudentAssociation.view;

import java.io.File;
import java.io.FileNotFoundException;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/***
 * 创建社团第二步
 * 
 * 添加社团名称、社团简介
 * 
 * 实现创建社团
 * 
 * @author 师春雷
 * 
 */
public class CreateClubNextActivity extends BaseActivity {

	private static final String TAG = "CreateClubNextActivity";

	Context context = null;

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 确定按钮 */
	private Button determine;
	/** 社团简介 */
	private EditText description;
	String strdes;
	/** 社团名称 */
	private EditText clubName;
	String strname;

	private String token = null;

	private String path;

	private ProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_club_next);

		context = CreateClubNextActivity.this;

		path = getStringExtra("path");

		token = (String) SPUtils.get(context, "token", "");

		findViews();
		setListener();
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		title = (TextView) findViewById(R.id.title_text);
		title.setText("创建社团");
		determine = (Button) findViewById(R.id.title_right);
		determine.setText("完成");

		clubName = (EditText) findViewById(R.id.et_create_club_next_name);
		description = (EditText) findViewById(R.id.et_create_club_next_briefintr);
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				exitDialog();
			}
		});

		determine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				strname = clubName.getText().toString().trim();
				strdes = description.getText().toString().trim();

				if (strname.equals("") || null == strname) {
					showToast(R.string.club_name_cannot_be_empty);
				} else if (strdes.equals("") || null == strdes) {
					showToast(R.string.club_introduction_cannot_be_empty);
				} else {
					loading = ProgressDialog.show(CreateClubNextActivity.this,
							"请稍后...");
					loading.show();
					CreateClub(strname, strdes, token, path);
				}
			}
		});
	}

	/**
	 * 创建社团
	 * 
	 * @param name
	 * @param des
	 * @param token
	 * @param path
	 */
	protected void CreateClub(String name, String des, String token, String path) {
		AjaxParams params = new AjaxParams();
		params.put("name", name);// 社团名称
		params.put("description", des);// 社团简介
		params.put("club_type", "0");// 社团类型
		params.put("auth_token", token);// 用户的token

		try {
			params.put("logo", new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} // 上传文件

		Log.i(TAG, "-------------" + params.toString());

		FinalHttp fh = new FinalHttp();
		fh.post(HttpUtils.ROOT_URL + HttpUtils.CREATE_CLUB, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();
						Log.i(TAG, "333333333333" + str.toString());

						Result result = (Result) JsonUtils.fromJson(str,
								Result.class);
						if (result.getResultcode() == 200) {
							showToast(R.string.creating_successful);

							openActivity(MainActivity.class, true);
						} else if (result.getResultcode() == 101) {
							showToast(R.string.failed_to_create);
						}
						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							loading.dismiss();
							showToast(R.string.network_access_failure);
						}
					}
				});
	}

	private void exitDialog() {
		new AlertDialog.Builder(CreateClubNextActivity.this)
				.setTitle(R.string.prompt)
				.setMessage(R.string.create_a_club_to_give_up)
				.setPositiveButton(R.string.determine,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								openActivity(MainActivity.class, true);
							}
						}).setNegativeButton(R.string.cancel, null).create()
				.show();
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
