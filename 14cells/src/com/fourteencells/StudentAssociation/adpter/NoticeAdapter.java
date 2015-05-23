package com.fourteencells.StudentAssociation.adpter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Messages;
import com.fourteencells.StudentAssociation.utils.TimeUtils;

public class NoticeAdapter extends BaseAdapter {

	private Context context = null;

	private List<Messages> list;

	public NoticeAdapter() {

	}

	public NoticeAdapter(Context context, List<Messages> list) {
		this.context = context;
		this.list = list;
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_notice, null);
			holder.content = (TextView) convertView
					.findViewById(R.id.notice_datails);
			holder.from = (TextView) convertView.findViewById(R.id.notice_from);
			holder.time = (TextView) convertView.findViewById(R.id.notice_time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.time.setText(TimeUtils.GetTimeDifference(list.get(position)
				.getCreated_at()));

		if (list.get(position).getFather_type().equals("1")) {
			holder.content.setText(list.get(position).getContent());
			if (list.get(position).getSub_type().equals("4")) {
				holder.from.setText("来自");
			} else {
				holder.from.setText("来自 " + list.get(position).getClub_name()
						+ " 社团");
			}
		}

		return convertView;
	}

	class ViewHolder {
		TextView content;
		TextView from;
		TextView time;
	}

}