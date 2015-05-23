package com.fourteencells.StudentAssociation.adpter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.PictureDetail;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 图片列表 数据适配器
 * 
 * @author 师春雷
 * 
 */
public class PictureAdapter extends BaseAdapter {

	private static final String TAG = "PictureAdapter";

	/** 定义环境变量 */
	private Context context = null;
	/** 定义数据存储的list列表 */
	private List<PictureDetail> list;
	ViewHolder viewHolder = null;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            环境变量
	 * @param list
	 *            存储数据的list
	 */
	public PictureAdapter(Context context, List<PictureDetail> list) {
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
	public View getView(int position, View rowView, ViewGroup parent) {
		if (rowView == null) {
			viewHolder = new ViewHolder();
			rowView = LayoutInflater.from(context).inflate(
					R.layout.item_picture, null);
			viewHolder.image = (ImageView) rowView
					.findViewById(R.id.img_picture);

			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		try {
			FinalBitmap fb = FinalBitmap.create(context);
			fb.configLoadingImage(R.drawable.img_default);
			fb.display(viewHolder.image, list.get(position).getPhoto().getUrl());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return rowView;
	}

	class ViewHolder {
		ImageView image;
	}

}
