package com.fourteencells.StudentAssociation.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.*;
import com.fourteencells.StudentAssociation.view.EventFragmentActivity;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventDetailsFragment extends BaseFragment {

	private final static String TAG = "EventDetailsFragment";

	/** 时间 */
	private TextView tv_time;
	/** 地点 */
	private TextView tv_address;
	private LinearLayout ll_address;
	/** 发起者 */
	private TextView tv_author;
	/** 人数限制 */
	private TextView tv_people_limit;
	/** 收费限制 */
	private TextView tv_charge;

	private String result;

	private Event event;

	SimpleDateFormat formatter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		formatter = new SimpleDateFormat("yyyy-MM-dd");

		View view = inflater.inflate(R.layout.fragment_event_details, null);

		tv_time = (TextView) view.findViewById(R.id.tv_event_details_time);
		tv_address = (TextView) view
				.findViewById(R.id.tv_event_details_address);
		ll_address = (LinearLayout) view
				.findViewById(R.id.ll_event_details_address);
		tv_author = (TextView) view.findViewById(R.id.tv_event_details_author);
		tv_people_limit = (TextView) view
				.findViewById(R.id.tv_event_people_limit);
		tv_charge = (TextView) view.findViewById(R.id.tv_event_charge);

		return view;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getEventsDetails(String id, String token) {
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

				String start = event.getStart_time();
				String end = event.getEnd_time();
				Log.i(TAG, start + "----------" + end);

				String time1 = start.substring(0, 10);
				String time2 = end.substring(0, 10);

				Date start_date = null, end_date = null;

				try {
					start_date = formatter.parse(time1);
					end_date = formatter.parse(time2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String startDate = TimeUtils.convertDate(start_date);
				String endtDate = TimeUtils.convertDate(end_date);

				String date = startDate + " -- " + endtDate;

				Log.i(TAG, date);

				tv_time.setText(date);

				if (null == event.getAddress() || event.getAddress().equals("")) {
					ll_address.setVisibility(View.GONE);
				} else {
					tv_address.setText(event.getAddress());
				}

				tv_author.setText(event.getPublisher_name());

				if (!event.isLimit()) {
					tv_people_limit.setText("无限制");
				} else {
					tv_people_limit.setText(event.getMax_number() + "人");
				}

				if (!event.isCharge()) {
					tv_charge.setText("不收费");
				} else {
					tv_charge.setText(event.getFee() + "元");
				}
				EventFragmentActivity.loading.dismiss();
			}

			@Override
			public void onFailure(Throwable t, int errorCode, String strMsg) {
				if (t != null) {
					EventFragmentActivity.loading.dismiss();
					showToast("网络环境不稳定，请稍后再试");
				}
				super.onFailure(t, errorCode, strMsg);
			}
		});
	}

}
