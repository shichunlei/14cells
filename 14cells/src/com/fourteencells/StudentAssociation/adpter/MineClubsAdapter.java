package com.fourteencells.StudentAssociation.adpter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Club;

/**
 * 我的社团 数据适配器
 * 
 * @author 师春雷
 * 
 */
public class MineClubsAdapter extends BaseAdapter {

	private final static String TAG = "MineClubsAdapter";

	private Context context;

	private List<Club> list;

	private LayoutInflater inflater;

	private int flog;

	public MineClubsAdapter(Context context, List<Club> list, int flog) {
		this.context = context;
		this.list = list;
		this.flog = flog;
		inflater = LayoutInflater.from(context);
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

		ViewHolder holder;

		if (convertView == null) {

			holder = new MineClubsAdapter.ViewHolder();

			convertView = inflater.inflate(R.layout.item_mineclub, null);

			holder.image = (ImageView) convertView
					.findViewById(R.id.image_club);

			holder.name = (TextView) convertView
					.findViewById(R.id.tv_club_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String name = list.get(position).getName();

		String str_image = null;
		if (flog == 1) {
			str_image = list.get(position).getUrl();
		} else if (flog == 0) {
			str_image = list.get(position).getLogo().getLogo().getUrl();
		}

		try {
			FinalBitmap fb = FinalBitmap.create(context);
			fb.configLoadingImage(R.drawable.img_default);
			fb.display(holder.image, str_image);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		holder.name.setText(name);

		return convertView;
	}

	class ViewHolder {
		ImageView image;
		TextView name;
	}

}
