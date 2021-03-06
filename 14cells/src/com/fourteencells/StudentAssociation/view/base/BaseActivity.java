package com.fourteencells.StudentAssociation.view.base;

import java.io.Serializable;

import com.fourteencells.StudentAssociation.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {

	public Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * 接收前一个页面传递的String值
	 * 
	 * @param key
	 * @return
	 */
	protected String getStringExtra(String key) {
		Intent receive = getIntent();
		String flag;

		flag = receive.getStringExtra(key);

		return flag;
	}

	/**
	 * 接收前一个页面传递的Integer值
	 * 
	 * @param key
	 * @return
	 */
	protected Integer getIntExtra(String key) {
		Intent receive = getIntent();
		int flag;

		flag = receive.getIntExtra(key, 0);

		return flag;
	}

	/**
	 * 接收前一个页面传递的Boolean值
	 * 
	 * @param key
	 * @return
	 */
	protected boolean getBooleanExtra(String key) {
		Intent receive = getIntent();
		boolean flag;

		flag = receive.getBooleanExtra(key, false);

		return flag;
	}

	/**
	 * 接收前一个页面传递的Float值
	 * 
	 * @param key
	 * @return
	 */
	protected Float getFloatExtra(String key) {
		Intent receive = getIntent();
		Float flag;

		flag = receive.getFloatExtra(key, 0f);

		return flag;
	}

	/**
	 * 接收前一个页面传递的Long值
	 * 
	 * @param key
	 * @return
	 */
	protected Long getLongExtra(String key) {
		Intent receive = getIntent();
		Long flag;

		flag = receive.getLongExtra(key, 0L);

		return flag;
	}

	/**
	 * 接收前一个页面传递的Double值
	 * 
	 * @param key
	 * @return
	 */
	protected Double getDoubleExtra(String key) {
		Intent receive = getIntent();
		Double flag;

		flag = receive.getDoubleExtra(key, 0.0);

		return flag;
	}

	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 */
	protected void openActivity(Class<?> pClass) {
		Intent intent = new Intent(context, pClass);
		startActivity(intent);
	}

	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 * @param isfinish
	 */
	protected void openActivity(Class<?> pClass, boolean isfinish) {
		Intent intent = new Intent(context, pClass);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		if (isfinish) {
			this.finish();
		}
	}

	/**
	 * 通过类名启动Activity，并且携带数据
	 * 
	 * @param pClass
	 * @param key
	 * @param value
	 */
	protected void openActivity(Class<?> pClass, String key, Serializable value) {
		Intent intent = new Intent(context, pClass);
		intent.putExtra(key, value);
		startActivity(intent);
	}

	/**
	 * 通过类名启动Activity，并且携带数据
	 * 
	 * @param pClass
	 * @param key
	 * @param value
	 * @param isfinish
	 */
	protected void openActivity(Class<?> pClass, String key,
			Serializable value, boolean isfinish) {
		Intent intent = new Intent(context, pClass);
		intent.putExtra(key, value);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		if (isfinish) {
			this.finish();
		}
	}

	/**
	 * Toast提示
	 * 
	 * @param message
	 */
	protected void showToast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast提示
	 * 
	 * @param id
	 */
	protected void showToast(int id) {
		Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
