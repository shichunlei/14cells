package com.fourteencells.StudentAssociation.adpter;

import java.util.List;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {

	private Context context;
	private List<Comment> list;
	private ViewEvalute viewevalute;

	public CommentAdapter(Context context, List<Comment> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewevalute = new ViewEvalute();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_comment, null);
			viewevalute.comment = (TextView) convertView
					.findViewById(R.id.tv_picture_comment_content);
			viewevalute.name = (TextView) convertView
					.findViewById(R.id.tv_picture_comment_name);
			convertView.setTag(viewevalute);
		} else {
			viewevalute = (ViewEvalute) convertView.getTag();
		}

		String content = list.get(position).getBody();
		String username = list.get(position).getUser().getName();

		viewevalute.comment.setText(content);
		viewevalute.name.setText(username);

		return convertView;
	}

	class ViewEvalute {
		TextView name;// 用户名
		TextView comment;// 评价内容
	}
}
