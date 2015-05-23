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

/**
 * 回应-适配器
 * 
 * @author LiuZhenYu
 * 
 */
public class ResponseAdapter extends BaseAdapter {

	/** 定义环境变量 */
	private Context context = null;
	/** 定义数据存储的list列表 */
	private List<Messages> list;

	public ResponseAdapter() {

	}

	public ResponseAdapter(Context context, List<Messages> list) {
		this.list = list;
		this.context = context;
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_response, null);
			holder.content = (TextView) convertView
					.findViewById(R.id.response_datails);
			holder.time = (TextView) convertView
					.findViewById(R.id.response_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (list.get(position).getFather_type().equals("2")) {
			holder.content.setText(list.get(position).getContent());
			holder.time.setText(TimeUtils.GetTimeDifference(list.get(position)
					.getCreated_at()));
		}

		return convertView;

	}

	class ViewHolder {
		TextView content;
		TextView time;
	}

}
