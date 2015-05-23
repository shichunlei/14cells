package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.RegistrationAdapter;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.*;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 页面功能：签到选择列表
 * 
 * @author 师春雷
 * 
 */
public class RegistrationActivity extends BaseActivity {

	private static final String TAG = "RegistrationActivity";

	private Context context;

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 签到活动选择列表 */
	private ListView listview;

	/** 用户的token */
	private String token = null;

	private List<Event> events;
	private List<Event> eventList = new ArrayList<Event>();

	/** 签到活动适配器 */
	private RegistrationAdapter adapter;

	/** 本地活动缓存 */
	private String event_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

		context = RegistrationActivity.this;

		token = (String) SPUtils.get(context, "token", "");
		Log.i(TAG, token);

		findViews();

		getEventsList(token);

		setListener();
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.chose_event);

		listview = (ListView) findViewById(R.id.lv_registration);
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openActivity(MainActivity.class, true);
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (eventList.get(arg2).getRole().equals("2")) {// role->2
					// 参加者身份,进入扫码界面
					openActivity(CaptureActivity.class, "flag", "registration",
							false);
				} else if (eventList.get(arg2).getRole().equals("1")) {// role->1
																		// 创建者身份,进入生成二维码界面
					Intent intent = new Intent();
					intent = new Intent(context, GenerateQRCodeActivity.class);
					intent.putExtra("event_id", eventList.get(arg2).getId());
					intent.putExtra("event_name", eventList.get(arg2).getName());
					startActivity(intent);
				}

			}
		});
	}

	/**
	 * 获得我的活动列表
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
		adapter = new RegistrationAdapter(context, eventList);
		adapter.notifyDataSetChanged();
		listview.setAdapter(adapter);
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openActivity(MainActivity.class, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
