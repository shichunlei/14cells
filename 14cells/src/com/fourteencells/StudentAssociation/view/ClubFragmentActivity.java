package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
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
import com.fourteencells.StudentAssociation.fragment.ClubEventFragment;
import com.fourteencells.StudentAssociation.fragment.ClubTitlePicFragment;
import com.fourteencells.StudentAssociation.fragment.ClubTitleSynopsisFragment;
import com.fourteencells.StudentAssociation.fragment.MemberFragment;
import com.fourteencells.StudentAssociation.fragment.PictureFragment;
import com.fourteencells.StudentAssociation.model.Club;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.NetUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * 社团主页
 * 
 * @author 师春雷
 * 
 */
public class ClubFragmentActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "ClubFragmentActivity";

	/** 返回 */
	private ImageView back;
	/** 标头 */
	private TextView title;
	/** 发起活动 */
	private Button postevent;
	/** 用户加入社团状态 */
	private TextView role;
	private LinearLayout join;

	public static ProgressDialog loading;

	/** 碎片管理器 */
	private FragmentManager fragmentManager;
	/** 碎片集合 */
	private List<Fragment> fragmentList = new ArrayList<Fragment>();

	private String id;
	private String token = null;

	private Club clubs = new Club();

	private MainViewPager mViewPager;

	private ImageView[] dots = null;

	private ImageView iv1, iv2;

	/** 社团Title碎片1 */
	private ClubTitlePicFragment cdpFragment;
	/** 社团Title碎片2 */
	private ClubTitleSynopsisFragment cdiFragment;

	/** 社团活动 */
	private TextView club_event;
	/** 社团图片 */
	private TextView club_picture;
	/** 社团成员 */
	private TextView club_member;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_club_fragment);

		findViews();

		init();

		slidelayout();

		if (NetUtils.isNetUseful()) {
			loading.show();
			getClubInfo(id);
		}

		setListener();
	}

	private void init() {
		id = getStringExtra("club_id");

		token = (String) SPUtils.get(ClubFragmentActivity.this, "token", "");

		loading = ProgressDialog.show(ClubFragmentActivity.this, "请稍后...");

		club_event.setOnClickListener(this);
		club_picture.setOnClickListener(this);
		club_member.setOnClickListener(this);

		club_event.performClick();

		Bundle bundle = new Bundle();
		bundle.putString("club_id", id);// 社团ID
		bundle.putString("token", token);

		cdpFragment.setArguments(bundle);
		cdiFragment.setArguments(bundle);
	}

	private void slidelayout() {
		fragmentList.add(cdpFragment);
		fragmentList.add(cdiFragment);

		dots = new ImageView[] { iv1, iv2 };
		// 循环取得小点图片
		for (int i = 0; i < 2; i++) {
			dots[i].setEnabled(true);
			dots[i].setTag(i);
		}
		dots[0].setEnabled(false);

		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setOnPageChangeListener(new TitleOnPageChangeListener());
		mViewPager.setAdapter(new TitlePagerAdapter());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getClubInfo(String id) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);

		String url = HttpUtils.ROOT_URL + HttpUtils.GET_CLUBS_DETAILS + id
				+ "?auth_token=" + token;

		fh.get(url, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);

				String str = t.toString();

				clubs = (Club) JsonUtils.fromJson(str, Club.class);

				if (clubs.getRole() == 0) {// role = 0 ->
											// 跟社团无关，需要申请加入该社团，无权创建活动
					join.setVisibility(View.VISIBLE);
					role.setText(R.string.join_club);
				} else if (clubs.getRole() == 1) {// role = 1 ->
													// 社长，即社团创建者，有权创建活动
					join.setVisibility(View.GONE);
					postevent.setText(R.string.post_event);
				} else if (clubs.getRole() == 2) {// role = 2 -> 管理员，有权创建活动
					join.setVisibility(View.GONE);
					postevent.setText(R.string.post_event);
				} else if (clubs.getRole() == 3) {// role = 3 ->
													// 普通社员，已经加入该社团，无权创建活动
					join.setVisibility(View.GONE);
					postevent.setText(R.string.post_event);
				} else if (clubs.getRole() == 4) {// role = 4 -> 审核中的社员，无权创建活动
					join.setVisibility(View.VISIBLE);
					role.setText(R.string.audit);
				}
				loading.dismiss();
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				loading.dismiss();
				if (t != null) {
					showToast("网络环境不稳定，请稍后再试");
				}
			}
		});
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);

		title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.club_details);

		postevent = (Button) findViewById(R.id.title_right);

		role = (TextView) findViewById(R.id.tv_club_tab_role);
		join = (LinearLayout) findViewById(R.id.ll_join);

		fragmentManager = getSupportFragmentManager();

		mViewPager = (MainViewPager) findViewById(R.id.viewPager_main);

		iv1 = (ImageView) findViewById(R.id.iView1);
		iv2 = (ImageView) findViewById(R.id.iView2);

		cdpFragment = new ClubTitlePicFragment();

		cdiFragment = new ClubTitleSynopsisFragment();

		club_event = (TextView) findViewById(R.id.club_event);
		club_picture = (TextView) findViewById(R.id.club_pic);
		club_member = (TextView) findViewById(R.id.club_mem);
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		postevent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openActivity(PostEventActivity.class, "club_id", id, false);
			}
		});

		join.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (clubs.getRole() == 0) {
					loading.show();

					JoinClub(id, token);
				}
			}
		});

	}

	/**
	 * 申请加入社团
	 * 
	 * @param id
	 * @param token
	 */
	private void JoinClub(String id, String token) {
		AjaxParams params = new AjaxParams();
		params.put("auth_token", token);// 用户的token
		params.put("club_id", id);// 社团ID

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.post(HttpUtils.ROOT_URL + HttpUtils.JOIN_CLUB, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();

						Log.i(TAG, "申请入团返回结果：" + str);

						Result result = (Result) JsonUtils.fromJson(str,
								Result.class);
						if (result.getResultcode() == 200) {
							showToast("申请成功，请等候团长审核");
							role.setText(R.string.audit);
							clubs.setRole(4);
						} else if (result.getResultcode() == 101) {
							showToast("申请失败，请稍候再试！");
						}

						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							loading.dismiss();
							if (t != null) {
								Log.i(TAG, t.toString());
							}
							if (strMsg != null) {
								Log.i(TAG, strMsg);
							}
							showToast("网络环境不稳定，请稍后再试");
						}
					}
				});
	}

	// ******************************************************************************************************************//
	public class ClubOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			club_event.setTextColor(getResources().getColor(R.color.dw));
			club_picture.setTextColor(getResources().getColor(R.color.dw));
			club_member.setTextColor(getResources().getColor(R.color.dw));

			switch (arg0) {
			case 0:
				club_event.setTextColor(getResources().getColor(R.color.white));
				break;
			case 1:
				club_picture.setTextColor(getResources()
						.getColor(R.color.white));
				break;
			case 2:
				club_member
						.setTextColor(getResources().getColor(R.color.white));
				break;
			}
		}

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public class TitleOnPageChangeListener implements OnPageChangeListener {

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

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public class TitlePagerAdapter extends PagerAdapter {

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
		case R.id.club_event:
			club_event.setEnabled(false);
			club_picture.setEnabled(true);
			club_member.setEnabled(true);

			SPUtils.put(ClubFragmentActivity.this, "type_id", id);

			loading.show();

			ClubEventFragment eventFragment = new ClubEventFragment();

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.club_framelayout, eventFragment, "fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

			break;
		case R.id.club_pic:
			club_event.setEnabled(true);
			club_picture.setEnabled(false);
			club_member.setEnabled(true);

			SPUtils.put(ClubFragmentActivity.this, "picture_type", "club");
			SPUtils.put(ClubFragmentActivity.this, "type_id", id);

			loading.show();

			PictureFragment pictureFragment = new PictureFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.club_framelayout, pictureFragment, "fragment")
					.setTransitionStyle(
							FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

			break;

		case R.id.club_mem:
			club_event.setEnabled(true);
			club_picture.setEnabled(true);
			club_member.setEnabled(false);

			SPUtils.put(ClubFragmentActivity.this, "picture_type", "club");
			SPUtils.put(ClubFragmentActivity.this, "type_id", id);

			loading.show();

			MemberFragment memberFragment = new MemberFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.club_framelayout, memberFragment, "fragment")
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
