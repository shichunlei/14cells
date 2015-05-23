package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MainViewPager;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.fragment.EventDetailsFragment;
import com.fourteencells.StudentAssociation.fragment.EventTitleNameFragment;
import com.fourteencells.StudentAssociation.fragment.EventTitleSynopsisFragment;
import com.fourteencells.StudentAssociation.fragment.MemberFragment;
import com.fourteencells.StudentAssociation.fragment.PictureFragment;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.FileUtils;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.NetUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.TimeUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

/**
 * 活动主页
 * 
 * @author 师春雷
 * 
 */
public class EventFragmentActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "EventFragmentActivity";

	/** 标头 */
	private TextView title;
	/** 返回 */
	private ImageView back;

	private Button exitEven;
	/** 用户加入活动状态 */
	private LinearLayout join;

	private TextView textJoin;
	private ImageView imgJoin;

	/** 是否加入该活动 */
	private boolean is_join = false;

	private boolean is_exit = false;

	public static ProgressDialog loading;

	private String id;
	/** 用户参与活动状态 */
	private String role;

	private String token = null;
	private Event event;

	private List<Event> events;

	private String result;
	private String application_id;
	private String event_list;
	private String nowTime, endTime;
	private String flog = "";

	/** 滑动的ViewPager */
	private MainViewPager eventTitleViewPager;
	/** 碎片集合 */
	private List<Fragment> fragmentList = new ArrayList<Fragment>();

	/** 碎片管理器 */
	private FragmentManager fragmentManager;

	/** 活动信息碎片1 */
	private EventTitleNameFragment etpFragment = new EventTitleNameFragment();
	/** 活动信息碎片2 */
	private EventTitleSynopsisFragment etsFragment = new EventTitleSynopsisFragment();

	/** 活动详情标签 */
	private Button eventDetailsTab;
	/** 活动图片标签 */
	private Button eventPictureTab;
	/** 活动成员图片 */
	private Button eventMemberTab;

	private ImageView[] dots = null;

	private ImageView dot1, dot2;

	private Bundle bundle = new Bundle();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_fragment);

		findViews();

		init();

		if (NetUtils.isNetUseful()) {
			loading.show();
			getEventInfo(id, token);
		} else {
			join.setVisibility(View.GONE);
		}

		setListener();
	}

	private void init() {
		loading = ProgressDialog.show(EventFragmentActivity.this, "请稍后...");

		id = getStringExtra("id");

		token = (String) SPUtils.get(EventFragmentActivity.this, "token", "");

		fragmentManager = getSupportFragmentManager();
	}

	private void setListener() {
		eventDetailsTab.setOnClickListener(this);
		eventPictureTab.setOnClickListener(this);
		eventMemberTab.setOnClickListener(this);

		eventDetailsTab.performClick();

		/** 退出活动 */
		exitEven.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (flog.equals("exit")) {
					loading.show();
					exitEvent(token, application_id);
				} else if (flog.equals("dissolve")) {

				}
				is_exit = true;
			}
		});

		join.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (role.equals("4")) {
					loading.show();
					JoinEvent(id, token);

					is_join = true;
				}
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void exitEvent(final String token, String application_id) {

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);

		String url = HttpUtils.ROOT_URL + HttpUtils.EXIT_EVENT + application_id
				+ "?auth_token=" + token;

		fh.delete(url, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				String str = t.toString();

				Result result = (Result) JsonUtils.fromJson(str, Result.class);
				if (result.getResultcode() == 200) {
					showToast("你已退出该活动");

					exitEven.setVisibility(View.GONE);
					join.setVisibility(View.VISIBLE);

					getMineEvent(token);
				} else if (result.getResultcode() == 101) {
					showToast("退出活动失败，请稍候再试！");
				}

				loading.dismiss();
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				if (t != null) {
					loading.dismiss();
					showToast("网络环境不稳定，请稍后再试");
				}
			}
		});
	}

	/**
	 * WARNING！！！！！！！！！！ 注意请求失败后将如何去做！！！！！！！！！！ WARNING！！！！！！！！！！！
	 * 
	 * 展示活动详情 活动图片 活动成员的方法
	 * 
	 * 该方法只有在网络请求成功后才能正确执行，不然会报空指针异常
	 */
	private void FragmentPerpare() {
		dots = new ImageView[] { dot1, dot2 };
		// 循环取得小点图片
		for (int i = 0; i < 2; i++) {
			dots[i].setEnabled(true);
			dots[i].setTag(i);
		}
		dots[0].setEnabled(false);

		bundle.putString("event_id", id);
		bundle.putString("token", token);
		bundle.putString("result", result);// 只有在网络请求成功后才能得到

		fragmentList.add(etpFragment);
		fragmentList.add(etsFragment);

		etpFragment.setArguments(bundle);
		etsFragment.setArguments(bundle);

		eventTitleViewPager.setOffscreenPageLimit(2);
		eventTitleViewPager
				.setOnPageChangeListener(new EventTitleOnPageChangeListener());
		eventTitleViewPager.setAdapter(new EventTitlePagerAdapter());
	}

	/**
	 * 得到活动基本信息
	 * 
	 * @param id
	 * @param token
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getEventInfo(String id, String token) {
		// 读取存在本地文件内的活动ID
		event_list = FileUtils.read(AppConfig.EVENT_FILE_NAME);

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);

		String url = HttpUtils.ROOT_URL + HttpUtils.GET_EVENTS_DETAILS + id
				+ "?auth_token=" + token;

		fh.get(url, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				result = t.toString();
				event = (Event) JsonUtils.fromJson(result, Event.class);

				nowTime = TimeUtils.nowTime2();
				endTime = event.getEnd_time();

				String end_Time = endTime.substring(0, 10) + " "
						+ endTime.substring(11, 16);

				if (TimeUtils.compare_date(end_Time, nowTime)) {
					join.setVisibility(View.GONE);
				} else {
					if (null != event_list) {// 我还没有活动或者还没有保存活动信息
						List<Event> mine_event;
						mine_event = (List<Event>) JsonUtils.fromJson(
								event_list, new TypeToken<List<Event>>() {
								});

						for (int i = 0; i < mine_event.size(); i++) {
							int activityID = Integer.valueOf(mine_event.get(i)
									.getId());
							int eventID = Integer.valueOf(event.getId());
							if (activityID == eventID) {

								role = mine_event.get(i).getRole();

								Log.i(TAG, "状态：" + role);

								if (role.equals("3")) {// role = 3 -> 排队中
									imgJoin.setVisibility(View.GONE);
									textJoin.setText(R.string.line_up);
								} else {
									join.setVisibility(View.GONE);

									application_id = mine_event.get(i)
											.getApplication_id();

									Log.i(TAG, "application_id："
											+ application_id);
									if (role.equals("2")) {// role = 2 -> 参与者
										exitEven.setVisibility(View.VISIBLE);
										exitEven.setText(R.string.exit_event);
										flog = "exit";
									} else if (role.equals("1")) {// role = 1 ->
																	// 创建者
										// exitEven.setVisibility(View.VISIBLE);
										exitEven.setText(R.string.dissolve_event);
										flog = "dissolve";
									}
								}

								break;
							} else {
								role = "4";
							}

						}
					}
				}
				loading.dismiss();
				if (!is_join && !is_exit) {
					FragmentPerpare();
				}
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				showToast("网络环境不稳定，请稍后再试");
			}
		});
	}

	private void findViews() {
		title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.event_details);

		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);

		exitEven = (Button) findViewById(R.id.title_right);
		// exitEven.setText("退出活动");
		exitEven.setVisibility(View.GONE);

		join = (LinearLayout) findViewById(R.id.ll_event_tab_join);
		textJoin = (TextView) findViewById(R.id.tv_event_tab_join);
		imgJoin = (ImageView) findViewById(R.id.img_event_tab_join);

		eventTitleViewPager = (MainViewPager) findViewById(R.id.EventTitleViewPager);

		eventDetailsTab = (Button) findViewById(R.id.event_details);
		eventPictureTab = (Button) findViewById(R.id.event_pic);
		eventMemberTab = (Button) findViewById(R.id.event_member);

		dot1 = (ImageView) findViewById(R.id.indicator1);
		dot2 = (ImageView) findViewById(R.id.indicator2);
	}

	/***
	 * 申请加入活动
	 * 
	 * @param id
	 * @param token
	 */
	protected void JoinEvent(final String id, final String token) {
		// 读取存在本地文件内的活动ID
		AjaxParams params = new AjaxParams();
		params.put("auth_token", token);// 用户的token
		params.put("event_id", id);// 社团ID

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.post(HttpUtils.ROOT_URL + HttpUtils.JOIN_EVENT, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();

						Result result = (Result) JsonUtils.fromJson(str,
								Result.class);
						if (result.getResultcode() == 200) {
							showToast("您已加入该活动！");

							getMineEvent(token);
						} else if (result.getResultcode() == 101) {
							if (result.getError().equals("open_to_error")) {

							}
							showToast("加入失败，请稍候再试！");
						}

						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							loading.dismiss();
							showToast("网络环境不稳定，请稍后再试");
						}
					}
				});
	}

	/**
	 * 得到我的活动列表
	 * 
	 * @param token
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void getMineEvent(final String token) {
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
					// 将“我的活动”所有ID装入一个Json数据里，并将其装换成字符串使用sp保存
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

				getEventInfo(id, token);
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				if (t != null) {
					showToast("亲,网络不给力,稍后再试！");
				}
			}
		});
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private class EventTitleOnPageChangeListener implements
			OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int position) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < dots.length; i++) {
				dots[i].setEnabled(true);
			}
			dots[position].setEnabled(false);
		}
	}

	// ****************************************************************************************************************************************************//
	private class EventTitlePagerAdapter extends PagerAdapter {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(fragmentList.get(position)
					.getView());
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = fragmentList.get(position);
			if (!fragment.isAdded()) { // 如果fragment还没有added
				FragmentTransaction ft = fragmentManager.beginTransaction();
				ft.add(fragment, fragment.getClass().getSimpleName());
				ft.commit();
				/**
				 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
				 * 会在进程的主线程中,用异步的方式来执行。 如果想要立即执行这个等待中的操作, 就要调用这个方法(只能在主线程中调用)。
				 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
				 */
				fragmentManager.executePendingTransactions();
			}

			if (fragment.getView().getParent() == null) {
				container.addView(fragment.getView()); // 为viewpager增加布局
			}
			return fragment.getView();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.event_details:
			eventDetailsTab.setEnabled(false);
			eventPictureTab.setEnabled(true);
			eventMemberTab.setEnabled(true);

			loading.show();

			EventDetailsFragment detailsFragment = new EventDetailsFragment();

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.event_framelayout, detailsFragment,
							"fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

			detailsFragment.getEventsDetails(id, token);

			break;
		case R.id.event_pic:
			eventDetailsTab.setEnabled(true);
			eventPictureTab.setEnabled(false);
			eventMemberTab.setEnabled(true);

			SPUtils.put(EventFragmentActivity.this, "picture_type", "event");
			SPUtils.put(EventFragmentActivity.this, "type_id", id);

			loading.show();

			PictureFragment pictureFragment = new PictureFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.event_framelayout, pictureFragment,
							"fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

			break;

		case R.id.event_member:
			eventDetailsTab.setEnabled(true);
			eventPictureTab.setEnabled(true);
			eventMemberTab.setEnabled(false);

			SPUtils.put(EventFragmentActivity.this, "picture_type", "event");
			SPUtils.put(EventFragmentActivity.this, "type_id", id);

			loading.show();

			MemberFragment memberFragment = new MemberFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.event_framelayout, memberFragment, "fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

			break;
		default:
			break;
		}
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}