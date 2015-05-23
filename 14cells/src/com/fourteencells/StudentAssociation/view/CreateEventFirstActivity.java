package com.fourteencells.StudentAssociation.view;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.TimeUtils;
import com.fourteencells.StudentAssociation.utils.datetime.ScreenInfo;
import com.fourteencells.StudentAssociation.utils.datetime.WheelMain;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 创建活动 第一步
 * 
 * 活动名称(必填项)
 * 
 * 活动时间（开始时间、结束时间）(必填项)
 * 
 * @author 师春雷
 * 
 */
public class CreateEventFirstActivity extends BaseActivity {

	private static final String TAG = "CreateEventFirstActivity";

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 下一步 */
	private Button next;

	/** 活动名称 */
	private EditText eventName;
	/** 开始时间 */
	private TextView startTime;
	/** 结束时间 */
	private TextView endTime;

	private String strName;
	private String strStartTime;
	private String strEndTime;

	WheelMain wheelMain;

	String start_time, end_time, now_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event_first);

		findViews();
		setListener();
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		title = (TextView) findViewById(R.id.title_text);
		title.setText("创建活动");
		next = (Button) findViewById(R.id.title_right);
		next.setText("下一步");

		eventName = (EditText) findViewById(R.id.et_create_event_name);
		startTime = (TextView) findViewById(R.id.tv_create_event_starttime);
		endTime = (TextView) findViewById(R.id.tv_create_event_endtime);

		strName = (String) SPUtils.get(CreateEventFirstActivity.this,
				"create_event_name", "");
		strStartTime = (String) SPUtils.get(CreateEventFirstActivity.this,
				"create_event_start_time", "");
		strEndTime = (String) SPUtils.get(CreateEventFirstActivity.this,
				"create_event_end_time", "");

		if (strName.length() > 0) {
			eventName.setText(strName);
		}

		if (strStartTime.length() <= 0) {
			startTime.setText(TimeUtils.nowTime2());
		} else {
			startTime.setText(strStartTime);
		}

		if (strEndTime.length() <= 0) {
			endTime.setText(TimeUtils.nowTime2());
		} else {
			endTime.setText(strEndTime);
		}
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				exitDialod();
			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strName = eventName.getText().toString().trim();
				strStartTime = startTime.getText().toString().trim();
				strEndTime = endTime.getText().toString().trim();

				if (null == strName || strName.equals("")) {
					showToast("活动名称不能为空");
				} else {
					SPUtils.put(CreateEventFirstActivity.this,
							"create_event_name", strName);
					SPUtils.put(CreateEventFirstActivity.this,
							"create_event_start_time", strStartTime);
					SPUtils.put(CreateEventFirstActivity.this,
							"create_event_end_time", strEndTime);

					Log.i(TAG, strStartTime);
					Log.i(TAG, strEndTime);

					openActivity(CreateEventSecondActivity.class, true);
				}
			}
		});

		startTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDateTime("start");
			}
		});

		endTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDateTime("end");
			}
		});
	}

	/**
	 * 得到创建时间
	 * 
	 * @param strDateTime
	 * 
	 */
	protected void getDateTime(final String strDateTime) {
		LayoutInflater inflater = LayoutInflater
				.from(CreateEventFirstActivity.this);
		final View timepickerview = inflater.inflate(
				R.layout.timepicker_dialog, null);
		ScreenInfo screenInfo = new ScreenInfo(CreateEventFirstActivity.this);

		wheelMain = new WheelMain(timepickerview, 0);
		wheelMain.screenheight = screenInfo.getHeight();

		wheelMain.initDateTimePicker();

		new AlertDialog.Builder(CreateEventFirstActivity.this).setTitle("选择时间")
				.setView(timepickerview)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (strDateTime == "start") {
							start_time = wheelMain.getTime();

							now_time = TimeUtils.nowTime2();

							if (TimeUtils.compare_date(now_time, start_time)) {
								startTime.setText(start_time);
							} else {
								showToast("开始时间不能小于当前时间");
								start_time = now_time;
								startTime.setText(start_time);
							}

						} else if (strDateTime == "end") {
							end_time = wheelMain.getTime();
							start_time = startTime.getText().toString().trim();
							if (TimeUtils.compare_date(start_time, end_time)) {
								endTime.setText(end_time);
							} else {
								showToast("结束时间不能小于开始时间");
								end_time = start_time;
								endTime.setText(end_time);
							}
						}

					}
				}).setNegativeButton("取消", null).show();
	}

	/**
	 * 退出返回
	 * 
	 */
	private void exitDialod() {
		new AlertDialog.Builder(CreateEventFirstActivity.this)
				.setTitle("提示")
				.setMessage("放弃已添加内容？")
				.setPositiveButton(R.string.determine,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								openActivity(PostEventActivity.class, true);
							}

						}).setNegativeButton(R.string.cancel, null).create()
				.show();
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitDialod();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
