package com.fourteencells.StudentAssociation.adpter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Club;

import android.content.Context;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 主页社团版块适配器
 * 
 * @author 师春雷
 * 
 */
public class ClubAdapter extends BaseAdapter {

	private static final String TAG = "ClubAdapter";

	private Context context;
	private List<Club> list = new ArrayList<Club>();
	ViewHolder holder;

	public ClubAdapter(Context context, List<Club> list) {
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_clublist, null);
			holder.ivImage = (ImageView) convertView
					.findViewById(R.id.newclub_image);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.newclub_name);
			holder.tvTime = (TextView) convertView
					.findViewById(R.id.newclub_time);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			final FinalBitmap fb = FinalBitmap.create(context);
			fb.configLoadingImage(R.drawable.img_default);
			fb.display(holder.ivImage, list.get(position).getLogo().getLogo()
					.getUrl());
		} catch (Exception e) {
			Log.e(TAG, "图片加载异常");
		}

		holder.tvName.setText(list.get(position).getName());

		String time = list.get(position).getLatest();
		String str_time = null;

		if (null != time) {
			if (time.length() > 0) {
				str_time = list.get(position).getLatest().substring(5, 7) + "月"
						+ list.get(position).getLatest().substring(8, 10) + "日";
			}
		}

		holder.tvTime.setText(str_time);

		return convertView;
	}

	class ViewHolder {
		private ImageView ivImage;
		private TextView tvName;
		private TextView tvTime;
	}
}
