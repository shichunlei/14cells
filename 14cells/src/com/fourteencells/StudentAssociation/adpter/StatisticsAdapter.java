package com.fourteencells.StudentAssociation.adpter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.model.User;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

public class StatisticsAdapter extends BaseAdapter {

	private static final String TAG = "StatisticsAdapter";

	private Context context;
	private List<User> list = new ArrayList<User>();
	ViewHolder holder;

	/** AFinal框架的图片加载 */
	private FinalBitmap fb;

	public StatisticsAdapter(Context context, List<User> list) {
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
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();

			fb = FinalBitmap.create(context);

			convertView = View.inflate(context, R.layout.item_statistics, null);
			holder.ivImage = (MyCircleImageView) convertView
					.findViewById(R.id.image_statistic);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_statistic_name);
			holder.yet_no_come = (TextView) convertView
					.findViewById(R.id.tv_statistic_yet_to_come);
			holder.mask = (MyCircleImageView) convertView
					.findViewById(R.id.v_statistic_mask);
			holder.mask.setImageResource(R.drawable.header_shank);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		fb.display(holder.ivImage, list.get(position).getHeadpic().getUrl());

		holder.tvName.setText(list.get(position).getName());

		String present = list.get(position).getPresent();

		Log.i(TAG, "签到状态：" + present);

		if (present.equals("1")) {
			holder.yet_no_come.setVisibility(View.GONE);
			holder.mask.setVisibility(View.GONE);
		} else if (present.equals("0")) {
			holder.yet_no_come.setVisibility(View.VISIBLE);
			holder.mask.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	class ViewHolder {
		private MyCircleImageView ivImage;
		private TextView tvName;
		private MyCircleImageView mask;
		private TextView yet_no_come;
	}
}
