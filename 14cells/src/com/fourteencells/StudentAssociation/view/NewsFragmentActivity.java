package com.fourteencells.StudentAssociation.view;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.fragment.NoticeFragment;
import com.fourteencells.StudentAssociation.fragment.ResponseFragment;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NewsFragmentActivity extends BaseActivity implements
		OnClickListener {

	/** 顶部Notice */
	private Button mTabNotice;
	/** 顶部Response */
	private Button mTabResponse;

	private String token;

	public static ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_fragment);

		dialog = ProgressDialog.show(this, "加载中...");

		initView();

	}

	/** 初始化控件 */
	private void initView() {
		token = (String) SPUtils.get(NewsFragmentActivity.this, "token", "");

		mTabNotice = (Button) findViewById(R.id.constact_notice);
		mTabResponse = (Button) findViewById(R.id.constact_respond);

		/** 设置Notice标签页点击事件 */
		mTabNotice.setOnClickListener(this);
		/** 设置Response标签页点击事件 */
		mTabResponse.setOnClickListener(this);

		mTabNotice.performClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.constact_notice:
			mTabNotice.setEnabled(false);
			mTabResponse.setEnabled(true);
			NoticeFragment notice = new NoticeFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.news_framelayout, notice, "fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

			dialog.show();
			notice.getNoticeList(token);

			break;
		case R.id.constact_respond:
			mTabResponse.setEnabled(false);
			mTabNotice.setEnabled(true);
			ResponseFragment response = new ResponseFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.news_framelayout, response, "fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
			dialog.show();
			response.getResponseList(token);

			break;
		default:
			break;
		}
	}
}
