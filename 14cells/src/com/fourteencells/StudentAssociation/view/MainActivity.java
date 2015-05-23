package com.fourteencells.StudentAssociation.view;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.Toast;

import com.fourteencells.StudentAssociation.MyApplication;
import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Club;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.FileUtils;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.NetUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.google.gson.reflect.TypeToken;

/**
 * @data 2014-04-11 16:27
 * 
 * @author 师春雷
 * 
 *         主页：分为直播、社团、添加、我的、消息等五个版块
 * 
 *         1、直播板块（瀑布流展示图片）
 * 
 *         2、社团板块
 * 
 *         3、添加板块（popupWindow实现）：分为拍照、签到、创建社团(动画效果出现)
 * 
 *         4、我的板块：分为我的社团、我的活动、我的照片（我的社团、我的活动数据加载后缓存在本地文件内）
 * 
 *         5、消息板块：分为通知、回应
 * 
 */

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	public static Context context;

	private PopupWindow mPopupWindow;

	private View mPopView;

	/** 拍照 */
	private ImageView createPhoto;
	/** 签名 */
	private ImageView registration;
	/** 创建社团 */
	private ImageView createClub;

	/** 拍照Layout */
	private LinearLayout photoLayout;
	/** 签名Layout */
	private LinearLayout regisLayout;
	/** 创建社团Layout */
	private LinearLayout clubLayout;

	/** 拍照动画 */
	private Animation photoAnim;
	/** 签名动画 */
	private Animation regisAnim;
	/** 创建社团动画 */
	private Animation clubAnim;

	/** 添加按钮 */
	private Button add;

	public TabHost tab_host;

	private View tabLive, tabMine, tabNews, tabCells;

	/** 退出时点击返回键之间的时间间隔 */
	private long mExitTime;

	private List<Event> events;
	private List<Club> clubs;

	/** 用户的token */
	private String token = null;

	Intent intent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		context = MainActivity.this;
		// 加到一个ActivityListView中
		MyApplication.getInstance().addActivity(this);

		init();

		// 初始化TabHost
		initialTabHost();

		// 判断联网状态
		if (NetUtils.isNetUseful()) {
			getEventsList(token);
			getClubList(token);
		}

		setListener();

	}

	/**
	 * 初始化
	 * 
	 */
	private void init() {
		token = (String) SPUtils.get(context, "token", "");

		tab_host = getTabHost();

		add = (Button) findViewById(R.id.btn_main_add);

		mPopView = LayoutInflater.from(context).inflate(
				R.layout.popup_add_dialog, null);

		createPhoto = (ImageView) mPopView.findViewById(R.id.image_add_photo);
		registration = (ImageView) mPopView
				.findViewById(R.id.image_add_registration);
		createClub = (ImageView) mPopView.findViewById(R.id.image_add_club);

		photoLayout = (LinearLayout) mPopView.findViewById(R.id.photoLayout);
		regisLayout = (LinearLayout) mPopView.findViewById(R.id.regisLayout);
		clubLayout = (LinearLayout) mPopView.findViewById(R.id.clubLayout);

		mPopupWindow = new PopupWindow(mPopView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#b0000000")));
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
	}

	/**
	 * 初始化TabHost
	 */
	@SuppressLint("NewApi")
	private void initialTabHost() {
		tabLive = LayoutInflater.from(context).inflate(R.layout.tab_live, null);
		tab_host.addTab(tab_host.newTabSpec(getString(R.string.live))
				.setIndicator(tabLive)
				.setContent(new Intent(context, LiveActivity.class)));

		tabCells = LayoutInflater.from(context)
				.inflate(R.layout.tab_club, null);
		tab_host.addTab(tab_host.newTabSpec(getString(R.string.discovery))
				.setIndicator(tabCells)
				.setContent(new Intent(context, ClubListActivity.class)));

		tabMine = LayoutInflater.from(context).inflate(R.layout.tab_mine, null);
		tab_host.addTab(tab_host.newTabSpec(getString(R.string.mine))
				.setIndicator(tabMine)
				.setContent(new Intent(context, MineFragmentActivity.class)));

		tabNews = LayoutInflater.from(context).inflate(R.layout.tab_news, null);
		tab_host.addTab(tab_host.newTabSpec(getString(R.string.news))
				.setIndicator(tabNews)
				.setContent(new Intent(context, NewsFragmentActivity.class)));
	}

	/**
	 * 事件监听器
	 * 
	 */
	private void setListener() {
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 设置窗口出现样式
				mPopupWindow.setAnimationStyle(R.style.popupWindow);
				// 显示窗口
				mPopupWindow.showAtLocation(add, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);
				// 显示三个动画
				anima(context);
			}
		});

		createPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(context, PhotoGraphActivity.class);
				startActivity(intent);
				mPopupWindow.dismiss();
			}
		});

		registration.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(context, RegistrationActivity.class);
				startActivity(intent);
				mPopupWindow.dismiss();
			}
		});

		createClub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(context, CreateClubActivity.class);
				startActivity(intent);
				mPopupWindow.dismiss();
			}
		});
	}

	/**
	 * 设置三个动画
	 * 
	 * @param context
	 */
	private void anima(Context context) {
		photoAnim = AnimationUtils.loadAnimation(context,
				R.anim.down_to_up_anim);
		photoAnim.setDuration(500);
		photoAnim.setFillAfter(true);
		photoLayout.startAnimation(photoAnim);

		regisAnim = AnimationUtils.loadAnimation(context,
				R.anim.down_to_up_anim);
		regisAnim.setDuration(800);
		regisAnim.setFillAfter(true);
		regisLayout.startAnimation(regisAnim);

		clubAnim = AnimationUtils
				.loadAnimation(context, R.anim.down_to_up_anim);
		clubAnim.setDuration(1100);
		clubAnim.setFillAfter(true);
		clubLayout.startAnimation(clubAnim);
	}

	/**
	 * 
	 * 得到我的社团列表
	 * 
	 * @param token
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getClubList(String token) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(HttpUtils.ROOT_URL + HttpUtils.MY_PARTICIPATED_CLUB
				+ "?auth_token=" + token, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);

				String str = t.toString();
				clubs = (List<Club>) JsonUtils.fromJson(str,
						new TypeToken<List<Club>>() {
						});
				String club_list = null;

				if (clubs != null) {

					JSONArray object = new JSONArray();
					for (int i = 0; i < clubs.size(); i++) {
						JSONObject jo = new JSONObject();
						try {
							jo.put("id", clubs.get(i).getId());
							jo.put("url", clubs.get(i).getLogo().getLogo()
									.getUrl());
							jo.put("name", clubs.get(i).getName());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						object.put(jo);
					}
					club_list = object.toString();
				}

				FileUtils.write(club_list, AppConfig.CLUB_FILE_NAME);
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
			}
		});
	}

	/**
	 * 得到我的活动列表
	 * 
	 * @param token
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getEventsList(String token) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(HttpUtils.ROOT_URL + HttpUtils.MY_PARTICIPATED_EVENT
				+ "?auth_token=" + token, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				String str = t.toString();
				events = (List<Event>) JsonUtils.fromJson(str,
						new TypeToken<List<Event>>() {
						});
				String event_list = null;
				if (events != null) {

					JSONArray object = new JSONArray();
					for (int i = 0; i < events.size(); i++) {
						JSONObject jo = new JSONObject();
						try {
							jo.put("id", events.get(i).getId());
							jo.put("start_time", events.get(i).getStart_time());
							jo.put("end_time", events.get(i).getEnd_time());
							jo.put("club_name", events.get(i).getClub_name());
							jo.put("count", events.get(i).getCount());
							jo.put("name", events.get(i).getName());
							jo.put("role", events.get(i).getRole());
							jo.put("application_id", events.get(i)
									.getApplication_id());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						object.put(jo);
					}
					event_list = object.toString();
				}

				FileUtils.write(event_list, AppConfig.EVENT_FILE_NAME);
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
			}
		});
	}

	/**
	 * 重写返回键
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// 判断两次点击的时间间隔（默认设置为2秒）
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				System.exit(0);
				finish();
			}

			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
