package com.fourteencells.StudentAssociation.adpter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.TimeUtils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 签到页面，选择签到活动的列表的适配器
 * 
 * @author 师春雷
 * 
 */

public class RegistrationAdapter extends BaseAdapter {

	private final static String TAG = "RegistrationAdapter";

	/** 定义环境变量 */
	private Context context = null;
	/** 定义数据存储的list列表 */
	private List<Event> list;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            环境变量
	 * @param eventList
	 *            存储数据的list
	 */
	public RegistrationAdapter(Context context, List<Event> eventList) {
		this.context = context;
		this.list = eventList;
	}

	public void setDishesList(List<Event> eventList) {
		this.list = eventList;
	}

	/**
	 * @return dishesList
	 */
	public List<Event> getDishesList() {
		return list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_choose_event, null);
			viewholder.imageRight = (ImageView) convertView
					.findViewById(R.id.iv_registration_edit);
			viewholder.event_name = (TextView) convertView
					.findViewById(R.id.tv_registration_event_name);
			viewholder.club_name = (TextView) convertView
					.findViewById(R.id.tv_registration_club_name);
			viewholder.event_date = (TextView) convertView
					.findViewById(R.id.tv_registration_event_time);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		String eventName = list.get(position).getName();
		String clubtName = list.get(position).getClub_name();

		Log.i(TAG, "活动名称：" + eventName);
		viewholder.event_name.setText(eventName);
		viewholder.club_name.setText(clubtName);

		String start = list.get(position).getStart_time();
		String end = list.get(position).getEnd_time();
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

		String date = startDate + "--" + endtDate;

		Log.i(TAG, date);

		viewholder.event_date.setText(date);

		String status = list.get(position).getRole();
		if (status.equals("1")) {
			viewholder.imageRight
					.setBackgroundResource(R.drawable.icon_rollcall);
		} else if (status.equals("2")) {
			viewholder.imageRight.setBackgroundResource(R.drawable.icon_signin);
		}

		return convertView;
	}

	class ViewHolder {
		ImageView imageRight;
		TextView event_name;
		TextView club_name;
		TextView event_date;
	}
}
