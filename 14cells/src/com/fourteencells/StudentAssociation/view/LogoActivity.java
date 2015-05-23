package com.fourteencells.StudentAssociation.view;

import java.io.File;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.utils.*;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

/**
 * 
 * 页面功能：项目启动页面
 * 
 * @data 2014-04-11
 * 
 * @time 16:27
 * 
 * @author 师春雷
 * 
 */
public class LogoActivity extends BaseActivity {

	/** 项目 Logo */
	private ImageView img_logo;

	File mFolder;

	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_logo);

		token = (String) SPUtils.get(LogoActivity.this, "token", "");

		mFolder = FileUtils.createFolders(AppConfig.FOLDER_NAME);

		img_logo = (ImageView) this.findViewById(R.id.image_logo);

		Animation animation = AnimationUtils.loadAnimation(LogoActivity.this,
				R.anim.logo_alpha);
		animation.setFillAfter(true);
		img_logo.startAnimation(animation);

		animation.setAnimationListener(new AnimationListener() {

			public void onAnimationEnd(Animation animation) {
				if (token.equals("") || token == null) {
					openActivity(LoginActivity.class, true);
				} else {
					openActivity(MainActivity.class, true);
				}
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
	}
}
