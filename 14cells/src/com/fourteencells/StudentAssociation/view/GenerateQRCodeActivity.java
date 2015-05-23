package com.fourteencells.StudentAssociation.view;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.utils.Tools;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 生成二维码
 * 
 * @author 师春雷
 * 
 */
public class GenerateQRCodeActivity extends BaseActivity {

	private static final String TAG = "GenerateQRCodeActivity";

	/** 标头 */
	private TextView title;
	/** 返回 */
	private ImageView back;
	/** 二维码图片框 */
	private ImageView qrCodeImage;
	/** 统计 */
	private TextView statistics;
	/** 活动ID */
	private String event_id;
	/** 活动名称 */
	private String event_name;
	/** 屏幕宽度 */
	private int width;
	/** 生成二维码位图 */
	Bitmap qrCodeBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_qr_code);

		event_id = getStringExtra("event_id");
		event_name = getStringExtra("event_name");

		Log.i(TAG, event_id + "活动名称：" + event_name);

		findViews();

		setListener();

		// 得到屏幕的宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels - Tools.Dp2Px(GenerateQRCodeActivity.this, 10);

		Log.i(TAG, width + "");

		/**
		 * 将生成的二维码位图放到界面上
		 */
		try {
			qrCodeBitmap = EncodingHandler.createQRCode(event_id, width);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		qrCodeImage.setImageBitmap(qrCodeBitmap);

	}

	private void findViews() {
		title = (TextView) findViewById(R.id.title_text);
		title.setText(event_name);
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);

		qrCodeImage = (ImageView) findViewById(R.id.img_generate_qrcode);
		statistics = (TextView) findViewById(R.id.tv_generate_qrcode_statistics);
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		statistics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openActivity(StatisticsActivity.class, "event_id", event_id,
						false);
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}
