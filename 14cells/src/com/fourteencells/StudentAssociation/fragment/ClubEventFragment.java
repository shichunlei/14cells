package com.fourteencells.StudentAssociation.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.EventAdapter;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnFooterRefreshListener;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnHeaderRefreshListener;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.ClubFragmentActivity;
import com.fourteencells.StudentAssociation.view.EventFragmentActivity;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class ClubEventFragment extends BaseFragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private final static String TAG = "ClubEventFragment";

	/** 活动视图ListView */
	private ListView listView;

	private TextView noEvent;

	private PullToRefreshView mPullToRefreshView;

	private List<Event> eventsList = new ArrayList<Event>();
	private List<Event> events;
	private EventAdapter adapter;

	/** 社团ID */
	private String id = null;
	/** 用户的token */
	private String token = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		token = (String) SPUtils.get(getActivity(), "token", "");
		id = (String) SPUtils.get(getActivity(), "type_id", "");

		View view = inflater.inflate(R.layout.fragment_club_event, null);

		mPullToRefreshView = (PullToRefreshView) view
				.findViewById(R.id.club_event_pull_refresh_view);

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		listView = (ListView) view.findViewById(R.id.club_event_list);

		noEvent = (TextView) view.findViewById(R.id.tv_no_club_event);
		noEvent.setVisibility(View.GONE);

		getEventsList(token, id);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				openActivity(EventFragmentActivity.class, "id",
						eventsList.get(arg2).getId());
			}
		});

		return view;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getEventsList(String token, String clubID) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(HttpUtils.ROOT_URL + HttpUtils.GET_CLUBS_EVENT_LIST
				+ "?club_id=" + clubID + "&auth_token=" + token,
				new AjaxCallBack() {
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

						if (events.size() == 0) {
							noEvent.setVisibility(View.VISIBLE);
						} else {
							setListAdapter();
							noEvent.setVisibility(View.GONE);
						}

						ClubFragmentActivity.loading.dismiss();
					}

					public void onFailure(Throwable t, int errorCode,
							String strMsg) {
						ClubFragmentActivity.loading.dismiss();
						if (t != null) {
							Log.i(TAG, t.toString());

							noEvent.setVisibility(View.VISIBLE);
						}
						super.onFailure(t, errorCode, strMsg);
					}
				});
	}

	private void setListAdapter() {

		if (eventsList != null && !eventsList.isEmpty()) {
			eventsList.clear();
			adapter.notifyDataSetChanged();
		}

		eventsList.addAll(events);
		events.clear();
		adapter = new EventAdapter(getActivity(), eventsList);
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter = new EventAdapter(getActivity(), eventsList);
				getEventsList(token, id);

				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				adapter = new EventAdapter(getActivity(), eventsList);
				getEventsList(token, id);

				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 2000);
	}

}
