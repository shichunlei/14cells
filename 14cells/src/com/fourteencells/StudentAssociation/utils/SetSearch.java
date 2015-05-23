package com.fourteencells.StudentAssociation.utils;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.SearchDialog;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class SetSearch {

	/**
	 * 
	 * @param context
	 *            上下文环境
	 * @param resId
	 *            搜索按钮所对应的的id
	 */
	public static void setSearch(final Context context, int resId) {
		LinearLayout llSearch = (LinearLayout) ((Activity) context)
				.findViewById(resId);
		llSearch.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				SearchDialog dialog = new SearchDialog(context,
						R.style.searchDialog);
				dialog.show();
				// 根据不同屏幕的宽度，来设置不同的EditText宽度
				dialog.setEditTextWidth(((Activity) context).getWindowManager()
						.getDefaultDisplay().getWidth() - 95);
				// 设置整个Dialog的位置以及宽度
				WindowManager.LayoutParams params = dialog.getWindow()
						.getAttributes();
				params.width = ((Activity) context).getWindowManager()
						.getDefaultDisplay().getWidth();
				params.height = WindowManager.LayoutParams.WRAP_CONTENT;
				dialog.getWindow().setAttributes(params);
				Window window = dialog.getWindow();
				window.setGravity(Gravity.TOP);
			}
		});
	}

}
