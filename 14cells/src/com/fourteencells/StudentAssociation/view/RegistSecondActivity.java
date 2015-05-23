package com.fourteencells.StudentAssociation.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.MyApplication;
import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.TimeUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * 注册流程第二步
 * 
 * @author 师春雷
 * 
 */
public class RegistSecondActivity extends BaseActivity {

	private final static String TAG = "RegistSecondActivity";

	private Context context;

	private PopupWindow mPopupWindow;
	/** 相册选择照片 */
	private TextView from_native;
	/** 拍照 */
	private TextView from_camera;
	/** 取消 */
	private TextView cancel;

	/** 下一步按钮 */
	private Button next;
	/** 跳过 */
	private Button skip;
	/** 本Activity标题 */
	private TextView title;

	/** 头像选择 */
	private ImageView select_photo;
	/** 头像显示 */
	private MyCircleImageView affirm_photo;
	/** 输入姓名 */
	private EditText in_user_name;
	/** 保存姓名 */
	private String tempName;
	/** 选择男性 */
	private Button man;
	/** 选择女性 */
	private Button woman;
	/** 暂存性别 */
	private String tempSex = "";

	/** 图片名称（包括全路径） */
	private String coverName = null;
	/** 临时路径 */
	private String strTemp = null;
	/** SDcard根目录 */
	private String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	/** 用来判断是否选择头像 */
	private Boolean is_addpicture = false;

	private String token;

	private ProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist_second);

		context = RegistSecondActivity.this;
		MyApplication.getInstance().addActivity(this);
		init();
		setListener();
	}

	/**
	 * 初始化方法
	 */
	private void init() {
		title = (TextView) findViewById(R.id.title_text);
		title.setText("完善资料");

		select_photo = (ImageView) findViewById(R.id.select_photo);
		affirm_photo = (MyCircleImageView) findViewById(R.id.affirm_photo);
		man = (Button) findViewById(R.id.in_user_sex1);
		woman = (Button) findViewById(R.id.in_user_sex2);
		in_user_name = (EditText) findViewById(R.id.in_user_name);

		next = (Button) findViewById(R.id.in_next_activity);
		skip = (Button) findViewById(R.id.in_skip_activity);

		View view = LayoutInflater.from(this).inflate(
				R.layout.popup_choose_dialog, null);

		from_native = (TextView) view.findViewById(R.id.textview_dialog_album);
		from_camera = (TextView) view
				.findViewById(R.id.textview_dialog_take_picture);
		cancel = (TextView) view.findViewById(R.id.textview_dialog_cancel);

		mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#b0000000")));
		mPopupWindow.setOutsideTouchable(true);

		token = (String) SPUtils.get(context, "token", "");

	}

	private void setListener() {
		select_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopupWindow != null) {
					mPopupWindow.setAnimationStyle(R.style.popupWindow);
					mPopupWindow.showAtLocation(affirm_photo, Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0);
				}
			}

		});

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

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopupWindow != null && mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
			}
		});

		man.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tempSex = "m";
				man.setBackground(getResources().getDrawable(
						R.drawable.btn_agree));
				woman.setBackground(getResources().getDrawable(
						R.drawable.btn_refuse));
			}
		});

		woman.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tempSex = "f";
				woman.setBackground(getResources().getDrawable(
						R.drawable.btn_agree));
				man.setBackground(getResources().getDrawable(
						R.drawable.btn_refuse));
			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tempName = in_user_name.getText().toString();

				if (!is_addpicture) {
					showToast("请选择一张图片！");
				} else if (null == tempName || tempName.equals("")) {
					showToast("请输入姓名！");
				} else if (null == tempSex || tempSex.equals("")) {
					showToast("请选择您的性别！");
				} else {
					Intent intent = new Intent();
					// 为后续修改做准备
					intent.putExtra("regist", "regist");
					intent.putExtra("name", tempName);// 用户姓名
					intent.putExtra("sex", tempSex);// 用户性别
					intent.putExtra("imagepath", coverName);// 用户头像本地地址

					Log.i(TAG, "本地头像地址：" + coverName);

					SPUtils.put(context, "imagepath", coverName);
					SPUtils.put(context, "name", tempName);
					SPUtils.put(context, "sex", tempSex);

					intent.setClass(context, EditorPersonalInfoActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});

		skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tempName = in_user_name.getText().toString().trim();
				if (!is_addpicture) {
					showToast("请上传您的头像！");
				} else if (tempName == null || tempName.equals("")) {
					showToast("请输入您的真实姓名！");
				} else if (tempSex == null || tempSex.equals("")) {
					showToast("请选择您的性别！");
				} else {
					loading = ProgressDialog.show(context, "请稍后...");
					loading.show();
					putPersonalInfo(tempName, tempSex, token, coverName);
				}
			}
		});
	}

	/***
	 * 跳过时提交数据
	 * 
	 * @param tempName
	 * @param tempSex
	 * @param token
	 * @param coverName
	 */
	protected void putPersonalInfo(final String name, final String sex,
			String token, final String path) {
		FinalHttp fh = new FinalHttp();

		AjaxParams params = new AjaxParams();

		try {
			params.put("photo", new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		params.put("name", name);
		params.put("sex", sex);
		params.put("auth_token", token);

		Log.i(TAG, params.toString());

		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.put(HttpUtils.ROOT_URL + HttpUtils.UPDATE_USER_INFO, params,
				new AjaxCallBack<Object>() {
					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {

						String str = t.toString();

						Result result = (Result) JsonUtils.fromJson(str,
								Result.class);
						if (result.getResultcode() == 101) {
							showToast("发生意外错误，请检查填写是否正确后重试！");
						} else if (result.getResultcode() == 200) {
							SPUtils.put(context, "imagepath", path);
							SPUtils.put(context, "name", name);
							SPUtils.put(context, "sex", sex);

							openActivity(LoginActivity.class, true);
						}
						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						loading.dismiss();
					}
				});
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
				default:
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
				is_addpicture = true;
				affirm_photo.setImageBitmap(bitmap);
			}
		}
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(getApplicationContext())
					.setTitle("提示")
					.setMessage("放弃注册？")
					.setPositiveButton(R.string.determine,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									openActivity(LoginActivity.class, true);
								}
							}).setNegativeButton(R.string.cancel, null)
					.create().show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
