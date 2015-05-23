package com.fourteencells.StudentAssociation.adpter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.TimeUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EventAdapter extends BaseAdapter {

	/** 定义环境变量 */
	private Context context = null;
	/** 定义数据存储的list列表 */
	private List<Event> list;
	ViewHolder viewHolder = null;

	SimpleDateFormat formatter;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            环境变量
	 * @param list
	 *            存储数据的list
	 */
	public EventAdapter(Context context, List<Event> list) {
		this.context = context;
		this.list = list;
	}

	public void setEventList(List<Event> list) {
		this.list = list;
	}

	/**
	 * @return list
	 */
	public List<Event> getEventList() {
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
	public View getView(int position, View rowView, ViewGroup parent) {
		if (rowView == null) {
			viewHolder = new ViewHolder();
			rowView = LayoutInflater.from(context).inflate(R.layout.item_event,
					null);

			viewHolder.date = (TextView) rowView
					.findViewById(R.id.tv_club_event_startDate);

			viewHolder.time = (TextView) rowView
					.findViewById(R.id.tv_club_event_startTime);

			viewHolder.name = (TextView) rowView
					.findViewById(R.id.tv_club_event_name);

			viewHolder.number = (TextView) rowView
					.findViewById(R.id.tv_club_event_number);

			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (list.get(position).getStart_time() != null) {
			String tempTime1 = list.get(position).getStart_time();
			String time1 = tempTime1.substring(0, 10) + " "
					+ tempTime1.substring(11, 19);
			Date date1 = null;
			try {
				date1 = formatter.parse(time1);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String startTime = TimeUtils.convertTime(date1);

			String str_date = startTime.substring(0, 6);

			viewHolder.date.setText(str_date);

			String str_time = startTime.substring(6, 15);

			viewHolder.time.setText(str_time);
		}

		viewHolder.name.setText(list.get(position).getName());
		viewHolder.number.setText(list.get(position).getCount() + "人参与");

		return rowView;
	}

	class ViewHolder {
		TextView name;
		TextView number;
		TextView date;
		TextView time;
	}
}
