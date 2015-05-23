package com.fourteencells.StudentAssociation.fragment;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

/**
 * 显活动的名称
 * 
 * 这是一个Fragment
 * 
 * @author LiuZhenYu
 * 
 */
public class EventTitleNameFragment extends BaseFragment {

	private final static String TAG = "EventTitleNameFragment";

	private TextView eventName;

	private List<Event> event;

	private String result;

	/** 活动ID */
	private String eventID;
	/** 用户Token */
	private String token;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		eventID = bundle.getString("event_id");
		token = bundle.getString("token");

		View view = inflater.inflate(R.layout.fragment_event_titlename, null);

		eventName = (TextView) view.findViewById(R.id.tv_name_eventtab);

		getEventInfo(eventID, token);

		return view;
	}

	/**
	 * 得到活动基本信息
	 * 
	 * @param id
	 * @param token
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getEventInfo(String id, String token) {

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
				result = "[" + t.toString() + "]";
				event = (List<Event>) JsonUtils.fromJson(result,
						new TypeToken<List<Event>>() {
						});

				eventName.setText(event.get(0).getName());
			}

			@Override
			public void onFailure(Throwable t, int errorCode, String strMsg) {
				if (strMsg != null) {
					Log.i(TAG, strMsg);
				}
				if (t != null) {
					Log.i(TAG, t.toString());
					showToast("网络环境不稳定，请稍后再试");
				}
				super.onFailure(t, errorCode, strMsg);
			}
		});
	}

}
