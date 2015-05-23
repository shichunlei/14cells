package com.fourteencells.StudentAssociation.adpter;

import java.util.List;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.SearchPlace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchPlaceAdapter extends BaseAdapter {

	/** 定义环境变量 */
	private Context context = null;
	/** 定义数据存储的list列表 */
	private List<SearchPlace> list;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            环境变量
	 * @param list
	 *            存储数据的list
	 */
	public SearchPlaceAdapter(Context context, List<SearchPlace> list) {
		this.context = context;
		this.list = list;
	}

	public void setSearchList(List<SearchPlace> list) {
		this.list = list;
	}

	/**
	 * @return list
	 */
	public List<SearchPlace> getSearchList() {
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
					R.layout.item_search_place, null);

			viewholder.name = (TextView) convertView
					.findViewById(R.id.tv_search_place_name);
			viewholder.addr = (TextView) convertView
					.findViewById(R.id.tv_search_place_addr);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		viewholder.name.setText(list.get(position).getName());
		viewholder.addr.setText(list.get(position).getAddress());

		return convertView;
	}

	class ViewHolder {
		TextView name;
		TextView addr;
	}
}
