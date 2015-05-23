package com.fourteencells.StudentAssociation.customview;

import com.fourteencells.StudentAssociation.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 加载中对话框
 * 
 * @author 师春雷
 * 
 */
public class ProgressDialog extends Dialog {

	private static ProgressDialog progressDialog;

	private ProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param cancelable
	 * 
	 * @return
	 */
	public static ProgressDialog show(Context context, String title,
			String message, boolean cancelable) {
		progressDialog = new ProgressDialog(context, R.style.progressDialog);
		progressDialog.setContentView(R.layout.progress_dialog);
		progressDialog.setCancelable(cancelable);
		progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		TextView tv_message = (TextView) progressDialog
				.findViewById(R.id.tv_message);
		if (null == message || "".equals(message))
			tv_message.setVisibility(View.GONE);
		else
			tv_message.setText(message);
		return progressDialog;
	}

	public static ProgressDialog show(Context context, String title,
			String message) {
		return show(context, title, message, false);
	}

	public static ProgressDialog show(Context context, String message) {
		return show(context, "", message, false);
	}

	public static ProgressDialog show(Context context) {
		return show(context, "");
	}

	@Override
	public void cancel() {
		super.cancel();
		progressDialog = null;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		progressDialog = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismiss();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}