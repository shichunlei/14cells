package com.fourteencells.StudentAssociation.view;

import java.io.File;
import java.io.FileOutputStream;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.utils.*;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 页面功能：创建社团第一步
 * 
 * 添加社团LOGO，实现拍照和相册选择相片，并展示在页面上，把社团LOGO地址传到下一步。
 * 
 * @author 师春雷
 * 
 */
public class CreateClubActivity extends BaseActivity {

	Context context = null;

	private PopupWindow mPopupWindow;
	/** 相册选择照片 */
	private TextView from_native;
	/** 拍照 */
	private TextView from_camera;
	/** 取消 */
	private TextView cancel;

	// 调用相机拍摄照片的名字(临时)
	private String takePicturePath;

	/** SDcard根目录 */
	private String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 下一步 */
	private Button next;
	/** 添加封面 */
	private ImageView add;

	/** logo */
	private ImageView logo;

	/** 封面图片名称（包括全路径） */
	private String coverName = null;

	private Boolean is_addpicture = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_club);
		context = getApplicationContext();
		findViews();
		setListener();
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.create_club);
		next = (Button) findViewById(R.id.title_right);
		next.setText(R.string.next);

		logo = (ImageView) findViewById(R.id.image_create_club_cover);
		add = (ImageView) findViewById(R.id.image_create_club_add);

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
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				exitDialog();
			}

		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (is_addpicture) {
					openActivity(CreateClubNextActivity.class, "path",
							coverName, true);
				} else {
					showToast(R.string.add_club_logo);
				}
			}
		});

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopupWindow != null) {
					mPopupWindow.setAnimationStyle(R.style.popupWindow);
					mPopupWindow.showAtLocation(logo, Gravity.BOTTOM
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
				takePicturePath = getIntent().getStringExtra("data");
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
				takePicturePath = IMAGE_FILE_PATH + "/temp.jpg";
				File image = new File(takePicturePath);
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
					File temp = new File(takePicturePath);
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
				logo.setImageBitmap(bitmap);
				add.setVisibility(View.GONE);
			}
		}
	}

	private void exitDialog() {
		new AlertDialog.Builder(CreateClubActivity.this)
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
