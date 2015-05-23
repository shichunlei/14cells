package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.CommentAdapter;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Comment;
import com.fourteencells.StudentAssociation.model.Picture;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 照片评论
 * 
 * 1、获得照片的评论列表
 * 
 * 2、对照片进行评论
 * 
 * 3、对评论进行回复
 * 
 * @author 师春雷
 * 
 */
public class CommentActivity extends BaseActivity {

	private static final String TAG = "CommentActivity";

	private TextView title;
	private ImageView back;

	private ListView listview;
	private Button submit;
	private EditText comment;

	private String photo_id;
	private String review;
	/** 用户的token */
	private String token = null;
	/** 我的ID */
	private String userId;
	/** 评论者ID */
	private String user_id;
	/** 评论者姓名 */
	private String user_name;
	/** 回复的评论ID */
	private String reply_to;

	private Boolean replyTo = false;

	private ProgressDialog loading;

	private List<Comment> contentList = new ArrayList<Comment>();
	private List<Comment> content;

	private CommentAdapter adapter;

	Picture photo = new Picture();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);

		loading = ProgressDialog.show(CommentActivity.this, "请稍后...");

		photo_id = getStringExtra("picture_id");

		token = (String) SPUtils.get(CommentActivity.this, "token", "");
		userId = (String) SPUtils.get(CommentActivity.this, "id", "");

		Log.i(TAG, "photo_id = " + photo_id);

		findViews();

		loading.show();
		getCommentList(token, photo_id);

		setListener();
	}

	private void findViews() {
		submit = (Button) findViewById(R.id.btn_picture_comment);
		comment = (EditText) findViewById(R.id.et_picture_comment);
		listview = (ListView) findViewById(R.id.lv_picture_comment);

		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		title = (TextView) findViewById(R.id.title_text);
		title.setText("照片评论");
	}

	private void setListener() {
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				review = comment.getText().toString();
				if (null == review || review.equals("")) {
					showToast("评论内容不能为空！");
				} else {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					loading.show();
					if (replyTo) {
						review = "@" + user_name + "  " + review;
					}
					saveComment(token, photo_id, review, reply_to);
				}
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openActivity(PictureDetailsActivity.class, "picture_id",
						photo_id, true);
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				user_id = contentList.get(arg2).getUser().getId();
				user_name = contentList.get(arg2).getUser().getName();
				reply_to = contentList.get(arg2).getId();

				if (!user_id.equals(userId)) {
					replyTo = true;
					submit.setText("回复");

					comment.setHint("回复" + user_name);
					// 弹出键盘（相当于点击输入框操作）
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
	}

	/**
	 * 得到图片评论列表
	 * 
	 * @param photo_id
	 * @param token
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getCommentList(String token, String photo_id) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		String URL = HttpUtils.ROOT_URL + HttpUtils.GET_ALBUMS_DETAILS
				+ photo_id + "?auth_token=" + token;
		Log.i(TAG, URL);
		fh.get(URL, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);

				String str = t.toString();
				Log.i(TAG, "返回图片信息：" + str);

				photo = JsonUtils.ParserPictureDetails("[" + str + "]");
				Log.i(TAG, photo.toString());
				Log.i(TAG, "返回照片评论列表：" + photo.getComments());

				content = (List<Comment>) JsonUtils.fromJson(
						photo.getComments(), new TypeToken<List<Comment>>() {
						});
				Log.i(TAG, "评论列表：" + content.toString());
				if (content.size() == 0) {
					showToast("暂无评论信息！");
				}

				setListAdapter();

				loading.dismiss();
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				if (t != null) {
					loading.dismiss();
					showToast("网络环境不稳定，请稍后再试");
				}
			}
		});

	}

	private void setListAdapter() {
		if (contentList != null && !contentList.isEmpty()) {
			contentList.clear();
			adapter.notifyDataSetChanged();
		}

		contentList.addAll(content);
		content.clear();
		adapter = new CommentAdapter(CommentActivity.this, contentList);
		adapter.notifyDataSetChanged();
		listview.setAdapter(adapter);
		loading.dismiss();
	}

	/**
	 * 
	 * 保存评论内容
	 * 
	 * @param comment
	 * @param photo_id
	 * @param auth_token
	 * @param reply_to
	 */
	protected void saveComment(String auth_token, String id, String review,
			String reply_to) {
		AjaxParams params = new AjaxParams();
		params.put("auth_token", auth_token);
		params.put("photo_id", id);// 照片ID
		params.put("comment", review);// 评论内容
		if (replyTo) {
			params.put("reply_to", reply_to);// (可选)回复的评论id
		}

		Log.i(TAG, "参数" + params.toString());

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.post(HttpUtils.ROOT_URL + HttpUtils.LEAVE_COMMENT, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();
						Log.i(TAG, "返回结果：" + str);

						Result result = (Result) JsonUtils.fromJson(str,
								Result.class);
						if (result.getResultcode() == 200) {
							comment.setText("");
							contentList.clear();
							if (replyTo) {
								showToast("回复成功！");
							} else {
								showToast(R.string.comments_success);
							}
							submit.setText("评论");
							comment.setHint("");
							replyTo = false;

							getCommentList(token, photo_id);
						} else if (result.getResultcode() == 101) {
							if (replyTo) {
								showToast("回复失败！");
							} else {
								showToast(R.string.comments_fail);
							}
						}
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							loading.dismiss();
							showToast("网络环境不稳定，请稍后再试");
						}
					}
				});
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openActivity(PictureDetailsActivity.class, "picture_id", photo_id,
					true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
