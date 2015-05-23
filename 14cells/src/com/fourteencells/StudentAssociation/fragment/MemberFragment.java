package com.fourteencells.StudentAssociation.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.MemberAdapter;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnFooterRefreshListener;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnHeaderRefreshListener;
import com.fourteencells.StudentAssociation.model.Member;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.ClubFragmentActivity;
import com.fourteencells.StudentAssociation.view.EventFragmentActivity;
import com.fourteencells.StudentAssociation.view.PersonalInfoActivity;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

public class MemberFragment extends BaseFragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private final static String TAG = "MemberFragment";

	private List<Member> peopleList = new ArrayList<Member>();
	private List<Member> people;

	private MemberAdapter adapter;

	private PullToRefreshView mPullToRefreshView;

	Boolean isRefresh = false;

	private String id;
	private String token;

	private String URL;
	private ListView listView;
	/** 社团成员数 */
	private TextView memNum;
	private String flog = "";

	private String text;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		token = (String) SPUtils.get(getActivity(), "token", "");
		flog = (String) SPUtils.get(getActivity(), "picture_type", "");
		id = (String) SPUtils.get(getActivity(), "type_id", "");

		View view = inflater.inflate(R.layout.fragment_member, null);

		mPullToRefreshView = (PullToRefreshView) view
				.findViewById(R.id.member_pull_refresh_view);

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		listView = (ListView) view.findViewById(R.id.MemberList);
		memNum = (TextView) view.findViewById(R.id.MemberNum);

		if (flog.equals("event")) {
			URL = HttpUtils.ROOT_URL + HttpUtils.GET_EVENTS_MEMBERS_LIST
					+ "?event_id=" + id + "&auth_token=" + token;

			text = "活动成员：";
		} else if (flog.equals("club")) {
			URL = HttpUtils.ROOT_URL + HttpUtils.GET_CLUBS_MEMBER_LIST
					+ "?club_id=" + id + "&auth_token=" + token;

			text = "社团成员：";
		}

		getUsersList();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				openActivity(PersonalInfoActivity.class, "user_id", peopleList
						.get(arg2).getUser_info().getId());
			}
		});

		return view;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getUsersList() {

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);

		fh.get(URL, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				String str = t.toString();
				// people = JsonParser.ParserPeopleList(str);
				people = (List<Member>) JsonUtils.fromJson(str,
						new TypeToken<ArrayList<Member>>() {
						});

				Log.i(TAG, "成员信息：" + people.toString());

				memNum.setText(text + people.size() + " 人");
				setUserAdapter();

				if (flog.equals("event")) {
					EventFragmentActivity.loading.dismiss();
				} else if (flog.equals("club")) {
					ClubFragmentActivity.loading.dismiss();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorCode, String strMsg) {
				if (flog.equals("event")) {
					EventFragmentActivity.loading.dismiss();
				} else if (flog.equals("club")) {
					ClubFragmentActivity.loading.dismiss();
				}

				super.onFailure(t, errorCode, strMsg);
			}
		});
	}

	private void setUserAdapter() {
		if (peopleList != null && !peopleList.isEmpty()) {
			peopleList.clear();
			adapter.notifyDataSetChanged();
		}

		peopleList.addAll(people);
		people.clear();
		adapter = new MemberAdapter(getActivity(), peopleList);
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter = new MemberAdapter(getActivity(), peopleList);
				getUsersList();

				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter = new MemberAdapter(getActivity(), peopleList);
				getUsersList();

				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 2000);
	}

}
