package com.fourteencells.StudentAssociation.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.MineClubsAdapter;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnFooterRefreshListener;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnHeaderRefreshListener;
import com.fourteencells.StudentAssociation.model.Club;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.FileUtils;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.NetUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.ClubFragmentActivity;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MineClubFragment extends BaseFragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private final static String TAG = "ClubFragment";

	private GridView mGridView;

	private TextView noClub;

	private PullToRefreshView mPullToRefreshView;

	private List<Club> clubsList = new ArrayList<Club>();
	private List<Club> clubs;
	private MineClubsAdapter clubsAdapter;

	private String club_list = null;

	/** 用户的token */
	private String token = null;

	public MineClubFragment() {
	}

	@SuppressLint("InlinedApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		token = (String) SPUtils.get(getActivity(), "token", "");

		View view = inflater.inflate(R.layout.fragment_club, null);

		mPullToRefreshView = (PullToRefreshView) view
				.findViewById(R.id.club_pull_refresh_view);

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		mGridView = (GridView) view.findViewById(R.id.gv_clubs);

		noClub = (TextView) view.findViewById(R.id.tv_no_mine_club);
		noClub.setVisibility(View.VISIBLE);

		getClubsList();

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				openActivity(ClubFragmentActivity.class, "club_id", clubsList
						.get(position).getId());
			}
		});

		return view;
	}

	/**
	 * 
	 * 得到社团列表(本地数据)
	 */
	@SuppressWarnings("unchecked")
	public void getClubsList() {

		club_list = FileUtils.read(AppConfig.CLUB_FILE_NAME);
		if (null != club_list) {
			clubs = (List<Club>) JsonUtils.fromJson(club_list,
					new TypeToken<List<Club>>() {
					});

			Log.i(TAG, "clubs.size() is : " + clubs.size());

			if (clubs.size() <= 0) {
				noClub.setVisibility(View.VISIBLE);
			} else {
				noClub.setVisibility(View.GONE);
				setClubAdapter(1);
			}
		}
	}

	private void setClubAdapter(int type) {
		if (clubsList != null && !clubsList.isEmpty()) {
			clubsList.clear();
			clubsAdapter.notifyDataSetChanged();
		}

		clubsList.addAll(clubs);
		clubs.clear();
		clubsAdapter = new MineClubsAdapter(getActivity(), clubsList, type);
		clubsAdapter.notifyDataSetChanged();
		mGridView.setAdapter(clubsAdapter);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (!NetUtils.isNetUseful()) {
					clubsAdapter = new MineClubsAdapter(getActivity(),
							clubsList, 1);
					getClubsList();
				} else {
					clubsAdapter = new MineClubsAdapter(getActivity(),
							clubsList, 0);
					getNetClubsList(token);
				}
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}

	/**
	 * 得到社团列表（网络数据）
	 * 
	 * @param token
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void getNetClubsList(String token) {
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

				// 将得到的网络数据写到本地文件中
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

				// 展示数据
				if (clubs.size() <= 0) {
					noClub.setVisibility(View.VISIBLE);
				} else {
					setClubAdapter(0);
					noClub.setVisibility(View.GONE);
				}
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				if (t != null) {
				}
				super.onFailure(t,errorCode, strMsg);
			}
		});
	}

	@Override
	public void onResume() {
		clubsAdapter = new MineClubsAdapter(getActivity(), clubsList, 1);

		getClubsList();
		super.onResume();
	}

}
