package com.fourteencells.StudentAssociation.adpter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.model.Member;

/**
 * 成员列表适配器
 * 
 * @author 师春雷
 * 
 */
public class MemberAdapter extends BaseAdapter {

	private static final String TAG = "MemberAdapter";

	/** 定义环境变量 */
	private Context context = null;
	/** 定义数据存储的list列表 */
	private List<Member> list;
	ViewHolder viewHolder = null;

	public MemberAdapter(Context context, List<Member> list) {
		this.context = context;
		this.list = list;
	}

	public void setPeopleList(List<Member> list) {
		this.list = list;
	}

	/**
	 * @return list
	 */
	public List<Member> getPeopleList() {
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

			rowView = LayoutInflater.from(context).inflate(
					R.layout.item_member, null);

			viewHolder.image = (MyCircleImageView) rowView
					.findViewById(R.id.img_member_image);

			viewHolder.name = (TextView) rowView
					.findViewById(R.id.tv_member_name);

			viewHolder.dec = (TextView) rowView
					.findViewById(R.id.tv_member_school);

			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		String str_name = list.get(position).getUser_info().getName();
		String str_image = list.get(position).getUser_info().getHeadpic()
				.getUrl();
		String str_school = list.get(position).getUser_info().getSchool();

		try {
			FinalBitmap fb = FinalBitmap.create(context);
			fb.configLoadingImage(R.drawable.default_image);
			fb.display(viewHolder.image, str_image);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		viewHolder.name.setText(str_name);
		viewHolder.dec.setText(str_school);

		return rowView;
	}

	class ViewHolder {
		MyCircleImageView image;
		TextView name;
		TextView dec;
	}

}