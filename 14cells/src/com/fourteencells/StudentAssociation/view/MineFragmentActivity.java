package com.fourteencells.StudentAssociation.view;

import net.tsz.afinal.FinalBitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.MyApplication;
import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.fragment.MineClubFragment;
import com.fourteencells.StudentAssociation.fragment.MineEventFragment;
import com.fourteencells.StudentAssociation.fragment.PictureFragment;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.Tools;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * 页面功能：我的页面。包括活动、社团、图片三部分
 * 
 * @author shichunlei
 * 
 */
public class MineFragmentActivity extends BaseActivity implements
		OnClickListener {

	private final static String TAG = "MineFragmentActivity";

	private TextView title;

	/** 设置按钮 */
	private ImageView setting;
	/** 社团 */
	private Button tvClub;
	/** 活动 */
	private Button tvEvent;
	/** 发布的图片 */
	private Button tvImage;

	public static ProgressDialog loading;

	/** 我的页面展示用户的姓名 */
	private TextView mName;
	/** 用户的头像 */
	private MyCircleImageView mHeadPic;
	/** 用户所在的大学 */
	private TextView mSchool;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine_fragment);

		// 加到一个ActivityListView中
		MyApplication.getInstance().addActivity(this);

		init();

		ShowMe();

		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openActivity(SettingActivity.class, false);
			}
		});
	}

	/**
	 * 初始化方法
	 */
	private void init() {
		loading = ProgressDialog.show(MineFragmentActivity.this, "请稍后...");

		title = (TextView) findViewById(R.id.title_text);
		title.setText("我的主页");

		setting = (ImageView) findViewById(R.id.title_image_right);
		setting.setImageResource(R.drawable.title_setting);

		tvEvent = (Button) findViewById(R.id.tv_event);
		tvClub = (Button) findViewById(R.id.tv_club);
		tvImage = (Button) findViewById(R.id.tv_image);

		mName = (TextView) findViewById(R.id.tv_username);
		mHeadPic = (MyCircleImageView) findViewById(R.id.mine_img_headpic);
		mSchool = (TextView) findViewById(R.id.user_school);

		tvClub.setOnClickListener(this);
		tvEvent.setOnClickListener(this);
		tvImage.setOnClickListener(this);

		tvEvent.performClick();
	}

	/**
	 * 展示用户的一些基本信息
	 */
	private void ShowMe() {
		// 显示用户的姓名s
		String name = (String) SPUtils.get(MineFragmentActivity.this, "name",
				"");
		mName.setText(name);
		String path = null;
		path = (String) SPUtils.get(MineFragmentActivity.this, "imagepath", "");
		String url = null;
		url = (String) SPUtils.get(MineFragmentActivity.this, "intentpath", "");
		if (url != null) {
			// 显示用户头像
			FinalBitmap fb = FinalBitmap.create(getApplicationContext());
			fb.configLoadingImage(R.drawable.default_image);
			fb.display(mHeadPic, url);

		} else if (path != null) {
			Log.i(TAG, path);
			mHeadPic.setImageBitmap(Tools.getLocalImage(path));
		}

		String school = (String) SPUtils.get(MineFragmentActivity.this,
				"school", "");
		mSchool.setText(school);
	}

	@Override
	protected void onResume() {
		ShowMe();
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_event:
			tvEvent.setEnabled(false);
			tvClub.setEnabled(true);
			tvImage.setEnabled(true);

			/** 活动碎片 */
			MineEventFragment eventFragment = new MineEventFragment();

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.mine_framelayout, eventFragment, "fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

			break;
		case R.id.tv_club:
			tvEvent.setEnabled(true);
			tvClub.setEnabled(false);
			tvImage.setEnabled(true);

			MineClubFragment clubFragment = new MineClubFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.mine_framelayout, clubFragment, "fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

			break;

		case R.id.tv_image:
			tvEvent.setEnabled(true);
			tvClub.setEnabled(true);
			tvImage.setEnabled(false);

			SPUtils.put(MineFragmentActivity.this, "picture_type", "mine");

			loading.show();

			PictureFragment pictureFragment = new PictureFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.mine_framelayout, pictureFragment, "fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

			break;
		default:
			break;
		}
	}

}