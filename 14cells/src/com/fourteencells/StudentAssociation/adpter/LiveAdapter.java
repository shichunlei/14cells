package com.fourteencells.StudentAssociation.adpter;

import java.util.LinkedList;
import java.util.List;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.waterfall.YListView;
import com.fourteencells.StudentAssociation.model.Live;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LiveAdapter extends BaseAdapter {

	private LinkedList<Live> live;
	private Context context = null;

	ViewHolder holder;

	public LiveAdapter(Context context, YListView xListView) {
		live = new LinkedList<Live>();
		this.context = context;
	}

	@Override
	public int getCount() {
		return live.size();
	}

	public LinkedList<Live> getmInfos() {
		return live;
	}

	public void setmInfos(LinkedList<Live> live) {
		this.live = live;
	}

	@Override
	public Object getItem(int position) {
		return live.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addItemLast(List<Live> datas) {
		for (int i = 0; i < datas.size(); i++) {
			live.addLast(datas.get(i));
		}
	}

	public void addItemTop(List<Live> datas) {
		for (int i = datas.size() - 1; i >= 0; i--) {
			live.addFirst(datas.get(i));
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Live duitangInfo = live.get(position);

		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(parent
					.getContext());
			convertView = layoutInflator.inflate(R.layout.item_live, null);
			holder = new ViewHolder();

			holder.imageView = (ImageView) convertView
					.findViewById(R.id.news_pic);
			// holder.contentView = (TextView) convertView
			// .findViewById(R.id.news_title);
			holder.timeView = (TextView) convertView
					.findViewById(R.id.news_title2);
			holder.comment = (TextView) convertView
					.findViewById(R.id.comment_num);
			holder.isLike = (TextView) convertView
					.findViewById(R.id.islike_num);

			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		// holder.contentView.setText(duitangInfo.description);
		if (duitangInfo.getDescription() != null
				&& !duitangInfo.getDescription().equals("")) {
			holder.timeView.setVisibility(View.VISIBLE);
			holder.timeView.setText(duitangInfo.getDescription());
		} else {
			holder.timeView.setVisibility(View.GONE);
		}

		if (duitangInfo.getComment_count() != null) {
			holder.comment.setText(duitangInfo.getComment_count());
		} else {
			holder.comment.setText("0");
		}

		if (duitangInfo.getLike_count() != null) {
			holder.isLike.setText(duitangInfo.getLike_count());
		} else {
			holder.isLike.setText("0");
		}

		FinalBitmap fb = FinalBitmap.create(context);
		fb.configLoadingImage(R.drawable.img_default);
		fb.display(holder.imageView, duitangInfo.getPhoto().getUrl(), 400, 400);

		return convertView;
	}

	public void clear() {
		live.clear();
	}

	class ViewHolder {
		ImageView imageView;
		TextView contentView;
		TextView timeView;
		TextView comment;
		TextView isLike;
	}

}
