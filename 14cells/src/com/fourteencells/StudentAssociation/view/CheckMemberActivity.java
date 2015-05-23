package com.fourteencells.StudentAssociation.view;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.model.User;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

public class CheckMemberActivity extends BaseActivity {

	private final static String TAG = "CheckMemberActivity";

	Context context;

	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 忽略 */
	private Button ignore;

	/** 新成员头像 */
	private MyCircleImageView newUserHeadPic;
	/** 新成员姓名 */
	private TextView newUserName;
	/** 新成员性别 */
	private TextView newUserSex;
	/** 新成员所在学校 */
	private TextView newUserSchool;
	/** 新成员所在学院 */
	private TextView newUserCollege;
	/** 新成员学号 */
	private TextView newUserSchoolNumber;
	/** 新成员入学时间 */
	private TextView newUserSchoolTime;
	/** 同意按钮 */
	private Button consent;
	/** 拒绝按钮 */
	private Button refuse;

	/** 用户token */
	private String token;
	/** 用户id */
	private String userID;
	/** 社团ID */
	private String clubID;
	/** 消息ID */
	private String messID;

	private User user = new User();

	private ProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_member);

		init();

		findViews();

		loading.show();
		getUserInfo();

		setListener();
	}

	private void init() {
		context = getApplicationContext();

		userID = getStringExtra("userID");
		clubID = getStringExtra("clubID");
		messID = getStringExtra("mess");

		token = (String) SPUtils.get(CheckMemberActivity.this, "token", "");

		loading = ProgressDialog.show(context, "请稍后...");
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageDrawable(getResources().getDrawable(R.drawable.back));

		title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.personal_info);

		ignore = (Button) findViewById(R.id.title_right);
		ignore.setText("忽略");

		newUserHeadPic = (MyCircleImageView) findViewById(R.id.new_user_photo);

		newUserName = (TextView) findViewById(R.id.new_user_name);
		newUserSex = (TextView) findViewById(R.id.new_user_sex);
		newUserSchool = (TextView) findViewById(R.id.new_user_school);
		newUserCollege = (TextView) findViewById(R.id.new_user_college);
		newUserSchoolNumber = (TextView) findViewById(R.id.new_user_number);
		newUserSchoolTime = (TextView) findViewById(R.id.new_user_school_time);

		consent = (Button) findViewById(R.id.button_consent);
		refuse = (Button) findViewById(R.id.button_refuse);
	}

	/**
	 * 按钮点击事件
	 */
	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ignore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		consent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loading.show();
				agreeOrRefuse(token, clubID, userID, true);
			}
		});

		// 拒绝
		refuse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loading.show();
				agreeOrRefuse(token, clubID, userID, false);
			}
		});
	}

	/**
	 * 获取新成员信息，并显示
	 */
	private void getUserInfo() {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put("auth_token", token);
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(HttpUtils.ROOT_URL + HttpUtils.GET_USER_INFO + userID, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);

						String result = t.toString();
						if (result != null) {
							Log.i(TAG, result);
							user = (User) JsonUtils
									.fromJson(result, User.class);
							if (user != null) {
								FinalBitmap fb = FinalBitmap.create(context);
								fb.configLoadingImage(R.drawable.default_image);
								fb.display(newUserHeadPic, user.getHeadpic()
										.getUrl());

								newUserName.setText(user.getName());
								if (user.getGender().equals("m")) {
									newUserSex.setText("男");
								} else if (user.getGender().equals("f")) {
									newUserSex.setText("女");
								}
								newUserSchool.setText(user.getSchool());
								newUserCollege.setText(user.getCollege());
								newUserSchoolNumber.setText(user.getSchool_no());
								newUserSchoolTime.setText(user.getEnrollment());
							}
						}
						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t, int errorCode,
							String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						loading.dismiss();
						showToast("发生意外错误，请稍候再试！");
					}
				});
	}

	/**
	 * 删除消息
	 * 
	 * @param token
	 * @param confirmed
	 */
	protected void deleteNotice(String token, String message_id,
			final boolean confirmed) {
		AjaxParams paramsa = new AjaxParams();
		paramsa.put("auth_token", token);
		FinalHttp fha = new FinalHttp();
		fha.configTimeout(HttpUtils.TIME_OUT);
		fha.put(HttpUtils.ROOT_URL + HttpUtils.NEWS_MESSAGE + message_id,
				paramsa, new AjaxCallBack<Object>() {
					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);

						String result = t.toString();

						Result flag = (Result) JsonUtils.fromJson(result,
								Result.class);

						if (flag.getResultcode() == 200) {
							if (confirmed) {
								showToast("已同意加入社团的申请！");
							} else {
								showToast("已拒绝加入社团的申请！");
							}

							finish();
						} else if (flag.getResultcode() == 101) {
							showToast("发生意外错误，请稍候再试！");
						}
						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t, int errorCode,
							String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						loading.dismiss();
					}
				});
	}

	/**
	 * 同意或拒绝加入社团的申请
	 * 
	 * @param token
	 * @param club_id
	 * @param user_id
	 * @param confirmed
	 */
	protected void agreeOrRefuse(final String token, String club_id,
			String user_id, final boolean confirmed) {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put("club_id", club_id);
		params.put("user_id", user_id);
		params.put("confirmed", confirmed + "");
		params.put("auth_token", token);
		fh.configTimeout(HttpUtils.TIME_OUT);

		fh.put(HttpUtils.ROOT_URL + HttpUtils.CHECK_NEW_MEMBER, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);

						String result = t.toString();

						Result flag = (Result) JsonUtils.fromJson(result,
								Result.class);

						if (flag.getResultcode() == 200) {

							deleteNotice(token, messID, confirmed);

						} else if (flag.getResultcode() == 101) {
							showToast("发生意外错误，请稍候再试！");
							loading.dismiss();
						}

					}

					@Override
					public void onFailure(Throwable t, int errorCode,
							String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						showToast("网络连接错误，请稍候再试！");
						loading.dismiss();
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
