package com.fourteencells.StudentAssociation.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.EventAdapter;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnFooterRefreshListener;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnHeaderRefreshListener;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.FileUtils;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.NetUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.EventFragmentActivity;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

@SuppressLint("ValidFragment")
public class MineEventFragment extends BaseFragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private final static String TAG = "MineEventFragment";

	/** 碎片中的ListView控件 */
	private ListView mListView;

	private TextView noEvent;

	private PullToRefreshView mPullToRefreshView;

	public List<Event> eventsList = new ArrayList<Event>();
	private List<Event> events;

	private EventAdapter adapter;

	/** 本地活动缓存 */
	private String event_list;

	/** 用户的token */
	private String token = null;

	public MineEventFragment() {

	}

	@SuppressLint("InlinedApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		token = (String) SPUtils.get(getActivity(), "token", "");

		View view = inflater.inflate(R.layout.fragment_event, null);

		mPullToRefreshView = (PullToRefreshView) view
				.findViewById(R.id.main_pull_refresh_view);

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		mListView = (ListView) view.findViewById(R.id.event_list);

		noEvent = (TextView) view.findViewById(R.id.tv_no_mine_event);
		noEvent.setVisibility(View.VISIBLE);

		getEventsList();

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				openActivity(EventFragmentActivity.class, "id",
						eventsList.get(position).getId());
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		adapter = new EventAdapter(getActivity(), eventsList);
		getEventsList();
		super.onResume();
	}

	/**
	 * 得到活动列表（本地数据）
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void getEventsList() {
		event_list = FileUtils.read(AppConfig.EVENT_FILE_NAME);

		if (null != event_list) {
			events = (List<Event>) JsonUtils.fromJson(event_list,
					new TypeToken<List<Event>>() {
					});
			if (events.size() <= 0) {
				noEvent.setVisibility(View.VISIBLE);
			} else {
				setListAdapter();
				noEvent.setVisibility(View.GONE);
			}
		}
	}

	private void setListAdapter() {

		if (eventsList != null && !eventsList.isEmpty()) {
			eventsList.clear();
			adapter.notifyDataSetChanged();
		}

		for (int i = 0; i < events.size(); i++) {
			if (!events.get(i).getRole().equals("3")) {
				// 加入到列表中
				eventsList.add(events.get(i));
			}
		}

		events.clear();
		adapter = new EventAdapter(getActivity(), eventsList);
		adapter.notifyDataSetChanged();
		mListView.setAdapter(adapter);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter = new EventAdapter(getActivity(), eventsList);

				if (NetUtils.isNetUseful()) {
					getNetEventsList(token);
				} else {
					getEventsList();
				}
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}

	/**
	 * 得到活动列表（网络数据）
	 * 
	 * @param token
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void getNetEventsList(String token) {
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

				// 将从网络得到的数据写到本地文件中
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

				if (events.size() <= 0) {
					noEvent.setVisibility(View.VISIBLE);
				} else {
					setListAdapter();
					noEvent.setVisibility(View.GONE);
				}

			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				if (t != null) {
				}
				super.onFailure(t, errorCode, strMsg);
			}
		});
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
}