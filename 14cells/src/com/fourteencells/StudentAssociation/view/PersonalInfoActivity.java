package com.fourteencells.StudentAssociation.view;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.User;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * 个人资料
 * 
 * @author 师春雷
 * 
 */
public class PersonalInfoActivity extends BaseActivity {

	private static final String TAG = "PersonalInfoActivity";

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 编辑 */
	private Button editor;
	/** 用户ID */
	private String user_id;
	/** 姓名 */
	private TextView name;
	/** 用户姓名 */
	private String user_name;
	/** 学号 */
	private TextView schoolNo;
	/** 注册学号 */
	private String user_schoolNo;
	/** 入学时间 */
	private TextView enrollment;
	/** 入学时间 */
	private String user_enrollment;
	/** 性别 */
	private TextView sex;
	/** 性别 */
	private String user_sex;
	/** 学校 */
	private TextView school;
	/** 学校 */
	private String user_school;
	/** 学院 */
	private TextView college;
	/** 学院 */
	private String user_college;
	/** 头像 */
	private MyCircleImageView headerpic;
	/** 用户头像（网络地址） */
	private String user_headerpic;
	/** 用户ID */
	private String id;
	/** 用户token */
	private String token = null;

	private User user = new User();

	private ProgressDialog loading;

	String tag = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);

		id = getStringExtra("user_id");

		findViews();

		getSpInfo();

		setListener();
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);

		title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.personal_info);

		editor = (Button) findViewById(R.id.title_right);
		editor.setText(R.string.edit);
		editor.setVisibility(View.GONE);

		name = (TextView) findViewById(R.id.personal_info_name);
		sex = (TextView) findViewById(R.id.personal_info_sex);
		college = (TextView) findViewById(R.id.personal_info_college);
		school = (TextView) findViewById(R.id.personal_info_school);
		schoolNo = (TextView) findViewById(R.id.personal_info_schoolNo);
		enrollment = (TextView) findViewById(R.id.personal_info_time);
		headerpic = (MyCircleImageView) findViewById(R.id.img_headpic);
	}

	/***
	 * 赋值
	 */
	private void assignment() {
		name.setText(user_name);

		if (user_sex.equals("m")) {
			sex.setText(R.string.sex1);
		} else if (user_sex.equals("f")) {
			sex.setText(R.string.sex2);
		}
		school.setText(user_school);
		schoolNo.setText(user_schoolNo);
		enrollment.setText(user_enrollment);
		college.setText(user_college);

		FinalBitmap fb = FinalBitmap.create(getApplicationContext());
		fb.configLoadingImage(R.drawable.default_image);
		fb.display(headerpic, user_headerpic);
	}

	/**
	 * 得到本地缓存数据
	 * 
	 */
	private void getSpInfo() {

		user_id = (String) SPUtils.get(PersonalInfoActivity.this, "id", "");
		token = (String) SPUtils.get(PersonalInfoActivity.this, "token", "");
		user_name = (String) SPUtils.get(PersonalInfoActivity.this, "name", "");
		user_sex = (String) SPUtils.get(PersonalInfoActivity.this, "sex", "");
		user_school = (String) SPUtils.get(PersonalInfoActivity.this, "school",
				"");
		user_enrollment = (String) SPUtils.get(PersonalInfoActivity.this,
				"schooltime", "");
		user_college = (String) SPUtils.get(PersonalInfoActivity.this,
				"college", "");
		user_schoolNo = (String) SPUtils.get(PersonalInfoActivity.this,
				"schoolnum", "");
		user_headerpic = (String) SPUtils.get(PersonalInfoActivity.this,
				"intentpath", "");
		Log.i(TAG, "个人头像：" + user_headerpic);

		Log.i(TAG, "id：" + id);
		Log.i(TAG, "user_id：" + user_id);

		if (user_id.equals(id)) {// 用户自己 --> 请求本地数据
			editor.setVisibility(View.VISIBLE);

			assignment();
		} else {// 非用户自己 --> 请求网络数据
			editor.setVisibility(View.GONE);

			loading = ProgressDialog.show(PersonalInfoActivity.this, "请稍后...");
			loading.show();
			getPersonalInfo(token, id);
		}
	}

	/**
	 * 得到个人信息
	 * 
	 * @param token
	 * @param id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getPersonalInfo(String token, String id) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);

		String URL = HttpUtils.ROOT_URL + HttpUtils.GET_USER_INFO + id
				+ "?auth_token=" + token;

		Log.i(TAG, URL);

		fh.get(URL, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				String str = t.toString();
				Log.i("", "返回个人基本信息：" + str);
				user = (User) JsonUtils.fromJson(str, User.class);

				Log.i(TAG, user.toString());

				name.setText(user.getName());
				if (user.getGender().equals("m")) {
					sex.setText(R.string.sex1);
				} else if (user.getGender().equals("f")) {
					sex.setText(R.string.sex2);
				}
				school.setText(user.getSchool());
				schoolNo.setText(user.getSchool_no());
				enrollment.setText(user.getEnrollment());
				college.setText(user.getCollege());

				FinalBitmap fb = FinalBitmap.create(getApplicationContext());
				fb.configLoadingImage(R.drawable.img_default);
				fb.display(headerpic, user.getHeadpic().getUrl());

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

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		editor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openActivity(EditorPersonalInfoActivity.class, "regist",
						"info", true);
			}
		});

	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}
