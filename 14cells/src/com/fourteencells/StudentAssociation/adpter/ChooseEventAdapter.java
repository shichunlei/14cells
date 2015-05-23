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

public class ChooseEventAdapter extends BaseAdapter {

	private static String TAG = "ChooseEventAdapter";

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
	public ChooseEventAdapter(Context context, List<Event> eventList) {
		this.context = context;
		this.list = eventList;
	}

	public void setDishesList(List<Event> eventList) {
		this.list = eventList;
	}

	/**
	 * @return list
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

			viewholder.imageView = (ImageView) convertView
					.findViewById(R.id.iv_registration_edit);
			viewholder.imageView.setVisibility(View.GONE);
			viewholder.event_name = (TextView) convertView
					.findViewById(R.id.tv_registration_event_name);
			viewholder.club_name = (TextView) convertView
					.findViewById(R.id.tv_registration_club_name);

			viewholder.eventdate = (TextView) convertView
					.findViewById(R.id.tv_registration_event_time);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		String name = list.get(position).getName();

		Log.i(TAG, "名称：" + name);
		viewholder.event_name.setText(name);
		viewholder.club_name.setText(list.get(position).getClub_name());

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

		viewholder.eventdate.setText(date);

		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView event_name;
		TextView club_name;
		TextView eventdate;
	}
}
