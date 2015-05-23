package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.ChooseEventAdapter;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.*;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 选择活动
 * 
 * @author 师春雷
 * 
 */
public class ChooseEventActivity extends BaseActivity {

	private static final String TAG = "ChooseEventActivity";

	/** 标头 */
	private TextView title;
	/** 返回 */
	private ImageView back;
	/** 活动列表 */
	private ListView listview;

	private List<Event> events;
	private List<Event> eventList = new ArrayList<Event>();

	/** 搜索结果适配器 */
	private ChooseEventAdapter adapter;

	/** 活动名称 */
	private String event_name = "";
	/** 活动ID */
	private String event_id = "";

	/** 用户的token */
	private String token = "";

	/** 加载对话框 */
	private ProgressDialog dialog;

	/** 本地活动缓存 */
	private String event_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_event);

		token = (String) SPUtils.get(context, "token", "");
		Log.i(TAG, token);

		findViews();

		dialog.setMessage(getString(R.string.loading));
		dialog.show();
		getEventsList(token);

		setListener();
	}

	private void findViews() {
		title = (TextView) findViewById(R.id.title_text);
		title.setText("选择活动");

		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);

		dialog = new ProgressDialog(this);

		listview = (ListView) findViewById(R.id.lv_choose_event_list);
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitDialog();
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				event_name = eventList.get(arg2).getName();
				event_id = eventList.get(arg2).getId();

				// 将选择的活动名称、ID保存到SharedPreferences里
				SPUtils.put(context, "choose_event_name", event_name);
				SPUtils.put(context, "choose_event_id", event_id);
				exitDialog();
			}
		});
	}

	/**
	 * 获得活动列表
	 * 
	 * @param token
	 */
	@SuppressWarnings("unchecked")
	private void getEventsList(String token) {

		event_list = FileUtils.read(AppConfig.EVENT_FILE_NAME);

		if (null != event_list) {
			events = (List<Event>) JsonUtils.fromJson(event_list,
					new TypeToken<List<Event>>() {
					});
			if (events == null) {
				showToast("亲，您暂时还没有参加任何活动！");
			} else {
				setAdapter();
			}
		}

	}

	private void setAdapter() {
		if (eventList != null && !eventList.isEmpty()) {
			eventList.clear();
			adapter.notifyDataSetChanged();
		}

		for (int i = 0; i < events.size(); i++) {
			if (!events.get(i).getRole().equals("3")) {
				// 加入到列表中
				eventList.add(events.get(i));
			}
		}

		events.clear();
		adapter = new ChooseEventAdapter(context, eventList);
		adapter.notifyDataSetChanged();
		listview.setAdapter(adapter);
		dialog.dismiss();
	}

	/**
	 * 确定返回上一级对话框
	 * 
	 */
	private void exitDialog() {
		openActivity(CreatePictureActivity.class, true);
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
