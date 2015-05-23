package com.fourteencells.StudentAssociation.view;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 页面功能：创建活动
 * 
 * @author 师春雷
 * 
 */
public class PostEventActivity extends BaseActivity {

	private Context context;

	/** 取消按钮 */
	private Button cancel;

	/** 会议 */
	private Button conference;
	/** 体育 */
	private Button sports;
	/** 聚餐 */
	private Button party;
	/** 旅行 */
	private Button travel;
	/** 社团ID */
	private String club_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_events);
		context = PostEventActivity.this;
		club_id = getStringExtra("club_id");

		init();
		setListener();
	}

	private void init() {
		cancel = (Button) findViewById(R.id.cancel);

		conference = (Button) findViewById(R.id.img_create_event_conference);
		sports = (Button) findViewById(R.id.img_create_event_sports);
		party = (Button) findViewById(R.id.img_create_event_party);
		travel = (Button) findViewById(R.id.img_create_event_travel);

		SPUtils.put(context, "create_event_name", "");// 活动名称
		SPUtils.put(context, "create_event_address", "");// 活动地址
		SPUtils.put(context, "create_event_lat", "0.0");// 纬度
		SPUtils.put(context, "create_event_lng", "0.0");// 经度
		SPUtils.put(context, "create_event_start_time", "");// 活动开始时间
		SPUtils.put(context, "create_event_end_time", "");// 活动结束时间
		SPUtils.put(context, "create_event_dec", "");// 活动简介
		SPUtils.put(context, "create_event_club_id", club_id);// 活动所属社团
		SPUtils.put(context, "create_event_type", "");// 类型
		SPUtils.put(context, "create_event_limit", 0);// 是否有人数限制
		SPUtils.put(context, "create_event_max_number", 0);// 最大人数
		SPUtils.put(context, "create_event_charge", 0);// 是否收费
		SPUtils.put(context, "create_event_fee", 0);// 收费金额
	}

	private void setListener() {
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		conference.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SPUtils.put(context, "create_event_type", "conference");

				openActivity(CreateEventFirstActivity.class, true);
			}
		});

		sports.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SPUtils.put(context, "create_event_type", "sports");

				openActivity(CreateEventFirstActivity.class, true);
			}
		});

		party.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SPUtils.put(context, "create_event_type", "party");

				openActivity(CreateEventFirstActivity.class, true);
			}
		});

		travel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SPUtils.put(context, "create_event_type", "trip");

				openActivity(CreateEventFirstActivity.class, true);
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
