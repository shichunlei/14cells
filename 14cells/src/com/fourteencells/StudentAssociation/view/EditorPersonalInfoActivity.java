package com.fourteencells.StudentAssociation.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.HttpHandler;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.model.User;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.TimeUtils;
import com.fourteencells.StudentAssociation.utils.Tools;
import com.fourteencells.StudentAssociation.utils.datetime.ScreenInfo;
import com.fourteencells.StudentAssociation.utils.datetime.WheelMain;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

public class EditorPersonalInfoActivity extends BaseActivity {

	private final static String TAG = "EditorPersonalInfoActivity";

	/** SDcard根目录 */
	private String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	private ProgressDialog loading;

	/** 返回按钮 */
	private ImageView back;
	/** 保存按钮 */
	private Button save;
	/** 标头 */
	private TextView title;
	/** 修改头像 */
	private ImageView edit_headpic;
	/** 头像 */
	private MyCircleImageView headpic;
	/** 输入姓名 */
	private EditText edit_name;
	/** 选择性别 */
	private TextView edit_sex;
	/** 输入学校 */
	private EditText edit_school;
	/** 输入学院 */
	private EditText edit_college;
	/** 输入学号 */
	private EditText edit_schoolNum;
	/** 选择入学时间 */
	private TextView edit_schoolTime;

	/** 头像(网络地址) */
	private String user_headerpic;
	/** 姓名 */
	private String user_name;
	/** 学号 */
	private String user_schoolNo;
	/** 入学时间 */
	private String user_enrollment;
	/** 性别 */
	private String user_sex;
	/** 学校 */
	private String user_school;
	/** 学院 */
	private String user_college;

	private String token = null;
	private String id = null;

	private User user = new User();

	WheelMain wheelMain;

	/** 临时路径 */
	private String strTemp = null;

	/** 图片名称（包括全路径） */
	private String coverName = null;
	/** 图像本地路径（如果不存在则为下载的头像） */
	private String downloadImage = null;
	/** 记录头像是否下载完成 */
	boolean flag = false;

	private PopupWindow mPopupWindow;

	/** 相册选择照片 */
	private TextView from_native;
	/** 拍照 */
	private TextView from_camera;
	/** 取消 */
	private TextView cancel;

	private String[] sex = new String[] { "男", "女" };

	private String regist = null;

	private Boolean is_photo = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor_personal);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init();

		regist = getStringExtra("regist");

		/** 从注册步骤跳过来 */
		if (regist.equals("regist")) {// 此页面相当于注册第三步
			showRegist();
		}

		/** 从查看个人资料页面跳转过来 */
		if (regist.equals("info")) {
			showInfo();
		}

		SelectOnClick();
	}

	/***
	 * 展示用户注册的信息
	 * 
	 */
	private void showRegist() {
		coverName = getStringExtra("imagepath");// 从上一个页面传过来的头像的本地地址
		user_name = getStringExtra("name");// 从上一个页面传过来的用户姓名
		user_sex = getStringExtra("sex");// 从上一个页面传过来的用户性别

		// 将上个页面传过来的值显示在相应的位置上
		headpic.setImageBitmap(Tools.getLocalImage(coverName));// 显示头像

		edit_name.setText(user_name);// 显示用户姓名
		// 显示用户性别
		if (user_sex.equals("m")) {
			edit_sex.setText("男");
		} else if (user_sex.equals("f")) {
			edit_sex.setText("女");
		}
	}

	/** 初始化方法 */
	private void init() {

		token = (String) SPUtils.get(EditorPersonalInfoActivity.this, "token",
				"");
		id = (String) SPUtils.get(EditorPersonalInfoActivity.this, "id", "");
		user_headerpic = (String) SPUtils.get(EditorPersonalInfoActivity.this,
				"intentpath", "");
		downloadImage = (String) SPUtils.get(EditorPersonalInfoActivity.this,
				"imagepath", "");
		// 检查头像图片是否存在本地文件夹内
		File file = new File(downloadImage);
		if (!file.exists()) {
			Log.i(TAG, "本地不存在图像");

			// 得到图片文件名
			// String fileName = downloadImage.substring(downloadImage
			// .lastIndexOf("/") + 1);

			downImage();
		}

		title = (TextView) findViewById(R.id.title_text);
		title.setText("修改个人信息");

		back = (ImageView) findViewById(R.id.title_back);
		back.setImageDrawable(getResources().getDrawable(R.drawable.back));

		save = (Button) findViewById(R.id.title_right);
		save.setText("保存");

		edit_headpic = (ImageView) findViewById(R.id.editor_img_headpic);
		headpic = (MyCircleImageView) findViewById(R.id.edit_head_pic);
		edit_name = (EditText) findViewById(R.id.editor_personal_info_name);
		edit_sex = (TextView) findViewById(R.id.editor_personal_info_sex);
		edit_school = (EditText) findViewById(R.id.editor_personal_info_school);
		edit_college = (EditText) findViewById(R.id.editor_personal_info_college);
		edit_schoolNum = (EditText) findViewById(R.id.editor_personal_info_schoolNo);
		edit_schoolTime = (TextView) findViewById(R.id.editor_personal_info_time);

		View view = LayoutInflater.from(this).inflate(
				R.layout.popup_choose_dialog, null);

		from_native = (TextView) view.findViewById(R.id.textview_dialog_album);
		from_camera = (TextView) view
				.findViewById(R.id.textview_dialog_take_picture);
		cancel = (TextView) view.findViewById(R.id.textview_dialog_cancel);

		mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#b0000000")));
		mPopupWindow.setOutsideTouchable(true);
	}

	private void downImage() {
		FinalHttp fh = new FinalHttp();
		HttpHandler<File> down_image = fh.download(user_headerpic,
				downloadImage, new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(File t) {
						if (t != null) {
							Log.i(TAG, "头像下载下载成功！");
							flag = true;
						}
					}
				});
		if (flag)
			down_image.stop();
	}

	/** 各个按钮点击事件 */
	private void SelectOnClick() {

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				user_name = edit_name.getText().toString().trim();
				user_sex = edit_sex.getText().toString().trim();
				user_school = edit_school.getText().toString().trim();
				user_college = edit_college.getText().toString().trim();
				user_schoolNo = edit_schoolNum.getText().toString().trim();
				user_enrollment = edit_schoolTime.getText().toString().trim();

				Boolean flog = true;

				if (!is_photo) {
					if (regist.equals("info")) {
						if (downloadImage != null || !downloadImage.equals("")
								|| downloadImage.length() > 0) {
							coverName = downloadImage;
							flog = true;
						} else {
							showToast("请重新选择你的头像！");
							flog = false;
						}
					}
				}

				if (flog) {
					if (null == user_name || user_name.equals("")) {
						showToast("请输入你的真实姓名！");
					} else if (null == user_sex || user_sex.equals("")) {
						showToast("请选择你的性别！");
					} else {
						loading = ProgressDialog.show(
								EditorPersonalInfoActivity.this, "请稍后...");
						loading.show();
						putPersonalInfo(user_name, coverName, user_school,
								user_college, user_schoolNo, user_sex,
								user_enrollment);
					}
				}
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitFun();
			}
		});

		// 选择头像
		edit_headpic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopupWindow != null) {
					mPopupWindow.setAnimationStyle(R.style.popupWindow);
					mPopupWindow.showAtLocation(edit_headpic, Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0);
				}
			}
		});

		// 相册选择照片
		from_native.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				strTemp = getIntent().getStringExtra("data");
				startActivityForResult(intent, 1);
				if (mPopupWindow != null && mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
			}
		});

		// 相机拍摄照片
		from_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 下面这句指定调用相机拍照后的照片存储的路径
				strTemp = IMAGE_FILE_PATH + "/temp.jpg";
				File image = new File(strTemp);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
				startActivityForResult(intent, 2);
				if (mPopupWindow != null && mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
			}
		});

		// 取消
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopupWindow != null && mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
			}
		});

		// 选择性别
		edit_sex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(EditorPersonalInfoActivity.this)
						.setTitle("请选择")
						.setItems(sex, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								edit_sex.setText(sex[which]);
							}
						}).create().show();
			}
		});

		// 选择入学年份
		edit_schoolTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSchoolYear();
			}
		});
	}

	protected void getSchoolYear() {
		final View timepickerview = LayoutInflater.from(this).inflate(
				R.layout.timepicker_dialog, null);
		ScreenInfo screenInfo = new ScreenInfo(EditorPersonalInfoActivity.this);

		wheelMain = new WheelMain(timepickerview, 2);
		wheelMain.screenheight = screenInfo.getHeight();

		wheelMain.initDateTimePicker();

		new AlertDialog.Builder(EditorPersonalInfoActivity.this)
				.setTitle("选择入学年份")
				.setView(timepickerview)
				.setPositiveButton(R.string.determine,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String year = wheelMain.getTime();

								String nowYear = TimeUtils.nowYear();

								if (Integer.valueOf(year) > Integer
										.valueOf(nowYear)) {
									showToast("入学年份不得大于现在");
									edit_schoolTime.setText(nowYear);
								} else {
									edit_schoolTime.setText(year);
								}
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	/**
	 * 修改个人信息
	 * 
	 * @param name
	 * @param path
	 * @param School
	 * @param college
	 * @param ShoolNumber
	 * @param sex
	 * @param enrollment
	 */
	protected void putPersonalInfo(String name, String path, String School,
			String college, String ShoolNumber, String sex, String enrollment) {

		AjaxParams params = new AjaxParams();
		params.put("name", name);
		try {
			params.put("photo", new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		params.put("school", School);
		params.put("college", college);
		params.put("school_no", ShoolNumber);
		if (sex.equals("男") || sex.equals("m")) {
			params.put("gender", "m");
		} else if (sex.equals("女") || sex.equals("f")) {
			params.put("gender", "f");
		}
		params.put("enrollment", enrollment);
		params.put("auth_token", token);

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.put(HttpUtils.ROOT_URL + HttpUtils.UPDATE_USER_INFO, params,
				new AjaxCallBack<Object>() {
					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();
						Result result = (Result) JsonUtils.fromJson(str,
								Result.class);

						if (result.getResultcode() == 101) {
							showToast("修改失败，请检查填写是否正确后重试！");
						} else if (result.getResultcode() == 200) {
							showToast("修改成功！");

							getPersonalInfo(id, token);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void getPersonalInfo(final String id, String token) {
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
				user = (User) JsonUtils.fromJson(str, User.class);

				/** 保存用户的头像 */
				SPUtils.put(EditorPersonalInfoActivity.this, "imagepath",
						coverName);
				/** 保存用户的头像 */
				SPUtils.put(EditorPersonalInfoActivity.this, "intentpath",
						user.getHeadpic().getUrl());
				/** 保存用户的姓名 */
				SPUtils.put(EditorPersonalInfoActivity.this, "name",
						user.getName());
				/** 保存用户的性别 */
				SPUtils.put(EditorPersonalInfoActivity.this, "sex",
						user.getGender());
				/** 保存用户所在的大学 */
				SPUtils.put(EditorPersonalInfoActivity.this, "school",
						user.getSchool());
				/** 保存用户所在学院 */
				SPUtils.put(EditorPersonalInfoActivity.this, "college",
						user.getCollege());
				/** 保存用户的学号 */
				SPUtils.put(EditorPersonalInfoActivity.this, "schoolnum",
						user.getSchool_no());
				/** 保存用户的入学时间 */
				SPUtils.put(EditorPersonalInfoActivity.this, "schooltime",
						user.getEnrollment());

				if (regist.equals("info")) {
					openActivity(PersonalInfoActivity.class, "user_id", id,
							true);
				}

				if (regist.equals("regist")) {
					openActivity(LoginActivity.class, true);
				}
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

	/** 展示从本地文件中读取出的信息 */
	private void showInfo() {
		// 从本地文件中取值
		user_name = (String) SPUtils.get(EditorPersonalInfoActivity.this,
				"name", "");
		user_sex = (String) SPUtils.get(EditorPersonalInfoActivity.this, "sex",
				"");
		user_school = (String) SPUtils.get(EditorPersonalInfoActivity.this,
				"school", "");
		user_college = (String) SPUtils.get(EditorPersonalInfoActivity.this,
				"college", "");
		user_enrollment = (String) SPUtils.get(EditorPersonalInfoActivity.this,
				"schooltime", "");
		user_schoolNo = (String) SPUtils.get(EditorPersonalInfoActivity.this,
				"schoolnum", "");

		// 将存在本地的信息显示出来
		// 显示用户头像
		if (user_headerpic != null) {
			FinalBitmap fb = FinalBitmap.create(getApplicationContext());
			fb.configLoadingImage(R.drawable.default_image);
			fb.display(headpic, user_headerpic);
		}

		edit_name.setText(user_name);// 显示用户姓名

		// 显示用户性别
		if (user_sex.equals("m")) {
			edit_sex.setText("男");
		} else if (user_sex.equals("f")) {
			edit_sex.setText("女");
		}

		edit_school.setText(user_school);// 显示用户学校
		edit_college.setText(user_college);// 显示用户学院
		edit_schoolNum.setText(user_schoolNo);// 显示用户学号
		edit_schoolTime.setText(user_enrollment);// 显示用户入学时间
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				// 如果是直接从相册获取
				case 1:
					startPhotoZoom(data.getData());
					break;
				// 如果是调用相机拍照
				case 2:
					File temp = new File(strTemp);
					startPhotoZoom(Uri.fromFile(temp));
					break;
				// 取得裁剪后的图片
				case 3:
					if (data != null) {
						setPicToView(data);
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 360);
		intent.putExtra("outputY", 360);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			FileOutputStream b = null;
			File file = new File(IMAGE_FILE_PATH);
			file.mkdirs();
			coverName = IMAGE_FILE_PATH + AppConfig.FOLDER_NAME + "cells_"
					+ TimeUtils.nowTime() + ".jpg";

			try {
				b = new FileOutputStream(coverName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				is_photo = true;
				headpic.setImageBitmap(bitmap);
			}
		}
	}

	/**
	 * 退出或返回
	 * 
	 */
	private void exitFun() {
		if (regist.equals("info")) {
			openActivity(PersonalInfoActivity.class, "user_id", id, true);
		} else if (regist.equals("regist")) {
			openActivity(RegistSecondActivity.class, true);
		}
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			exitFun();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
