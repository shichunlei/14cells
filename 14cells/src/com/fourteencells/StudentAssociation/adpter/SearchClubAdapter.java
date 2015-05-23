package com.fourteencells.StudentAssociation.adpter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Club;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchClubAdapter extends BaseAdapter {

	private static String TAG = "SearchClubAdapter";

	/** 定义环境变量 */
	private Context context = null;
	/** 定义数据存储的list列表 */
	private List<Club> list;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            环境变量
	 * @param clubList
	 *            存储数据的list
	 */
	public SearchClubAdapter(Context context, List<Club> clubList) {
		this.context = context;
		this.list = clubList;
	}

	public void setDishesList(List<Club> clubList) {
		this.list = clubList;
	}

	/**
	 * @return dishesList
	 */
	public List<Club> getClubList() {
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

		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_search_club_result, null);
			viewholder.imageView = (ImageView) convertView
					.findViewById(R.id.img_search);
			viewholder.name = (TextView) convertView
					.findViewById(R.id.tv_search_name);
			viewholder.description = (TextView) convertView
					.findViewById(R.id.tv_search);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		String name = list.get(position).getName();
		String image = list.get(position).getLogo().getUrl();

		Log.i(TAG, "名称：" + name + "\n缩略图：" + image);

		try {
			FinalBitmap fb = FinalBitmap.create(context);
			fb.configLoadingImage(R.drawable.default_image);
			fb.display(viewholder.imageView, image, 200, 200);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		viewholder.name.setText(name);
		viewholder.description.setText(list.get(position).getDescription());

		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView name;
		TextView description;
	}
}
