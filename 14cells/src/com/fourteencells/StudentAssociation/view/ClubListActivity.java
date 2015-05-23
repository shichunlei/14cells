package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import com.fourteencells.StudentAssociation.MyApplication;
import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.ClubAdapter;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnFooterRefreshListener;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnHeaderRefreshListener;
import com.fourteencells.StudentAssociation.model.Club;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.NetUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.SetSearch;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 页面功能：发现主页
 * 
 * @author 师春雷
 * 
 */
public class ClubListActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private static final String TAG = "ClubListActivity";
	/** 二维码扫描 */
	private ImageView dimension_code;

	Boolean isRefresh = false;
	/** 本页面最重要的一块 */
	private GridView gv_list;
	private List<Club> clubList = new ArrayList<Club>();
	private List<Club> clubs;
	/** 发现列表适配器 */
	public ClubAdapter adapter;

	private ProgressDialog loading;
	private TextView textView;

	private String school;

	boolean is_loading = false;

	/** 用户的token */
	private String token = null;

	private PullToRefreshView mPullToRefreshView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clublist);

		// 加到一个ActivityListView中
		MyApplication.getInstance().addActivity(this);

		token = (String) SPUtils.get(ClubListActivity.this, "token", "");
		school = (String) SPUtils.get(ClubListActivity.this, "school", "");

		findViews();

		if (NetUtils.isNetUseful()) {
			is_loading = true;

			loading = ProgressDialog.show(ClubListActivity.this, "请稍后...");
			loading.show();

			getClubList(school, token);
		} else {
			textView.setVisibility(View.VISIBLE);
			textView.setText(R.string.no_club);
		}

		setListener();

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (NetUtils.isNetUseful()) {
			if (!is_loading) {

				loading = ProgressDialog.show(ClubListActivity.this, "请稍后...");
				loading.show();

				getClubList(school, token);
			}
			is_loading = false;
		} else {
			textView.setVisibility(View.VISIBLE);
			textView.setText(R.string.no_club);
		}
	}

	@SuppressLint("InlinedApi")
	private void findViews() {
		dimension_code = (ImageView) findViewById(R.id.dimension_code);

		SetSearch.setSearch(ClubListActivity.this, R.id.ll_search);

		gv_list = (GridView) findViewById(R.id.gv_club);

		textView = (TextView) findViewById(R.id.tv_clublist_textview);

		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.club_pull_refresh);
	}

	private void setListener() {
		dimension_code.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openActivity(CaptureActivity.class, "flag", "club", false);
			}
		});

		gv_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				openActivity(ClubFragmentActivity.class, "club_id",
						clubList.get(arg2).getClub_id(), false);
			}
		});
	}

	/**
	 * 得到社团列表
	 * 
	 * @param school
	 * @param token
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getClubList(String school, String token) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(HttpUtils.ROOT_URL + HttpUtils.GET_CLUBS_LIST + "?school="
				+ school + "&auth_token=" + token, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);

				String str = t.toString();
				Log.i(TAG, "返回社团信息：" + str);
				clubs = (List<Club>) JsonUtils.fromJson(str,
						new TypeToken<List<Club>>() {
						});
				Log.i(TAG, "-=-" + clubs);

				if (clubs.size() == 0) {
					textView.setVisibility(View.VISIBLE);
					textView.setText(R.string.no_club);
				} else {
					setAdapter();
					loading.dismiss();
				}
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				if (t != null) {
					loading.dismiss();
					textView.setVisibility(View.VISIBLE);
					textView.setText(R.string.no_club);
					showToast("请求数据失败，请稍后再试");
				}
			}
		});
	}

	protected void setAdapter() {

		if (clubList != null) {
			clubList.clear();
		}

		clubList.addAll(clubs);
		clubs.clear();

		adapter = new ClubAdapter(this, clubList);
		gv_list.setAdapter(adapter);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {

		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {

				getClubList(school, token);
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 2000);
	}

}
