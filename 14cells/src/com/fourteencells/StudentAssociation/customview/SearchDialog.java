package com.fourteencells.StudentAssociation.customview;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.view.SearchClubResultActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SearchDialog extends Dialog {
	private EditText et_keyword;
	private ImageView image;
	private Context context;

	public SearchDialog(Context context) {
		super(context);
		this.context = context;
	}

	public SearchDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search_club_dialog);

		findViews();

		setListener();
	}

	// 初始化控件
	private void findViews() {
		image = (ImageView) findViewById(R.id.image_search);
		et_keyword = (EditText) findViewById(R.id.et_keyword_dialog);
		et_keyword.setFocusable(true); // 设置焦点
	}

	private void setListener() {
		// 显示键盘
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(et_keyword, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.SHOW_IMPLICIT);

		// 给et_keyword绑定焦点改变的监听器
		et_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (hasFocus) {// 如果有焦点就显示软件盘
					imm.showSoftInputFromInputMethod(
							et_keyword.getWindowToken(), 0);
				} else {// 否则就隐藏
					imm.hideSoftInputFromWindow(et_keyword.getWindowToken(), 0);
				}
			}
		});

		image.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String temp = et_keyword.getText().toString().trim();
				if (!temp.equals("") && temp != null && !temp.equals("请输入关键字")) {
					Intent i = new Intent();
					i.putExtra("keyword", temp);
					i.setClass(context, SearchClubResultActivity.class);
					context.startActivity(i);
					et_keyword.clearFocus();
					SearchDialog.this.dismiss();
				} else {
					Toast.makeText(getContext(), "请输入搜索关键字", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		setCanceledOnTouchOutside(true);
	}

	/**
	 * 当点击屏幕别的地方的时候，系统自动调用这个方法
	 */
	@Override
	public void cancel() {
		// 清空焦点
		et_keyword.clearFocus();
		super.cancel();
	}

	/**
	 * 根据屏幕来设置Dialog中EditText的宽度
	 * 
	 * @param pix
	 */
	public void setEditTextWidth(int pix) {
		et_keyword.setWidth(pix);
	}
}
