package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.SearchClubAdapter;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnFooterRefreshListener;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnHeaderRefreshListener;
import com.fourteencells.StudentAssociation.model.Club;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 页面功能：搜索社团页面，列表展示搜索的社团
 * 
 * @author 师春雷
 * 
 */
public class SearchClubResultActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private static final String TAG = "SearchClubResultActivity";

	private ProgressDialog loading;

	/** 返回按钮 */
	private ImageView imgBack;
	/** 标头 */
	private TextView title;
	/** 无搜索结果时的显示界面 */
	private TextView result_null;
	/** 搜索结果列表 */
	private ListView listview;

	private List<Club> club;
	private List<Club> clubList = new ArrayList<Club>();

	/** 搜索结果适配器 */
	private SearchClubAdapter adapter;

	private int page = 1;

	/** 用户的token */
	private String token = null;
	/** 关键字 */
	private String keyword;

	private String type;

	private PullToRefreshView mPullToRefreshView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_club);

		token = (String) SPUtils
				.get(SearchClubResultActivity.this, "token", "");
		Log.i(TAG, token);
		keyword = getStringExtra("keyword");
		Log.i(TAG, keyword);

		findViews();
		// 隐藏显示
		result_null.setVisibility(View.GONE);

		type = "header";
		loading = ProgressDialog.show(SearchClubResultActivity.this, "请稍后...");
		loading.show();
		searchResult(keyword, token, page);

		setListener();
	}

	private void findViews() {
		imgBack = (ImageView) findViewById(R.id.title_back);
		imgBack.setImageResource(R.drawable.back);

		title = (TextView) findViewById(R.id.title_text);
		title.setText("搜索结果");

		result_null = (TextView) findViewById(R.id.search_result_null);
		listview = (ListView) findViewById(R.id.lv_search_list);

		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.search_pull_refresh_view);

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
	}

	private void setListener() {
		imgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				openActivity(ClubFragmentActivity.class, "club_id",
						clubList.get(arg2).getId(), false);
			}
		});
	}

	/**
	 * 按照搜索关键字搜索内容
	 * 
	 * @param keywords
	 * @param page
	 * @param token
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void searchResult(String keywords, String token, int page) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(HttpUtils.ROOT_URL + HttpUtils.FIND_CLUB + "?keyword="
				+ keywords + "&auth_token=" + token + "&page=" + page,
				new AjaxCallBack() {
					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);

						String str = t.toString();
						Log.i(TAG, "返回社团信息：" + str);
						club = (List<Club>) JsonUtils.fromJson(str,
								new TypeToken<List<Club>>() {
								});
						Log.i(TAG, "-=-" + club);

						if (club.size() > 0) {
							result_null.setVisibility(View.GONE);
							setAdapter();
						} else {
							if (clubList.size() > 0) {
								result_null.setVisibility(View.GONE);
							} else {
								result_null.setVisibility(View.VISIBLE);
							}
						}
						loading.dismiss();
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						Log.i(TAG, strMsg);
						Log.i(TAG, t.toString());
						result_null.setVisibility(View.VISIBLE);
						loading.dismiss();
						if (t != null) {
							loading.dismiss();
							showToast("请求数据失败，请稍后再试");
						}
					}
				});
	}

	private void setAdapter() {

		if (type.equals("header")) {
			if (clubList != null && !clubList.isEmpty()) {
				clubList.clear();
				adapter.notifyDataSetChanged();
			}

			clubList.addAll(club);
			adapter = new SearchClubAdapter(SearchClubResultActivity.this,
					clubList);
			adapter.notifyDataSetChanged();
			listview.setAdapter(adapter);
		} else if (type.equals("footer")) {
			result_null.setVisibility(View.GONE);
			clubList.addAll(club);
			adapter = new SearchClubAdapter(SearchClubResultActivity.this,
					clubList);
			adapter.notifyDataSetChanged();
			listview.setAdapter(adapter);
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page++;
				type = "footer";
				searchResult(keyword, token, page);
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page = 1;
				type = "header";

				searchResult(keyword, token, page);
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}
