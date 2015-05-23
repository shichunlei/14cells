package com.fourteencells.StudentAssociation.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.ResponseAdapter;
import com.fourteencells.StudentAssociation.customview.CustomDialog;
import com.fourteencells.StudentAssociation.customview.refresh.XListView;
import com.fourteencells.StudentAssociation.customview.refresh.XListView.HeaderListener;
import com.fourteencells.StudentAssociation.model.Messages;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.*;
import com.fourteencells.StudentAssociation.view.NewsFragmentActivity;
import com.fourteencells.StudentAssociation.view.PictureDetailsActivity;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * 回应碎片
 * 
 * 展示图片评论或赞的回应列表
 * 
 * 点击每一个回应列表项可进入图片详情也查看评论或赞
 * 
 * 长按每一个回应列表项可删除该条回应
 * 
 * @author 师春雷
 * 
 */
public class ResponseFragment extends BaseFragment implements HeaderListener {

	private static final String TAG = "ResponseFragment";

	Context context;

	private XListView mListView;

	private TextView no_response;

	private List<Messages> responseList = new ArrayList<Messages>();
	private List<Messages> response;

	private ResponseAdapter adapter;

	private String token;

	/** 图片ID */
	public String picid;

	/** 消息ID */
	private String mesid;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_response, container,
				false);

		init(view);

		ClickItem();

		return view;
	}

	/** 初始化方法 */
	private void init(View view) {
		context = getActivity();

		token = (String) SPUtils.get(getActivity(), "token", "");

		mListView = (XListView) view.findViewById(R.id.responseListView);

		mListView.setPullLoadEnable(false);
		mListView.setHeaderListener(this);

		no_response = (TextView) view.findViewById(R.id.tv_no_response);
		no_response.setVisibility(View.GONE);
	}

	/**
	 * 从服务器得到数据
	 * 
	 * @param token
	 */
	public void getResponseList(String token) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(HttpUtils.ROOT_URL + HttpUtils.GET_NEWS + "?auth_token=" + token,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);

						String result = t.toString();

						Log.i(TAG, result);

						response = (List<Messages>) JsonUtils.fromJson(result,
								new TypeToken<List<Messages>>() {
								});
						if (response.size() > 0) {
							setAdapter();
						} else {
							no_response.setVisibility(View.VISIBLE);
						}

						NewsFragmentActivity.dialog.dismiss();
						mListView.stopRefresh();
						mListView.setRefreshTime(TimeUtils.upDataTime());
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);

						NewsFragmentActivity.dialog.dismiss();
						mListView.stopRefresh();

						mListView.setRefreshTime(TimeUtils.upDataTime());
					}
				});
	}

	protected void setAdapter() {
		if (responseList != null && !responseList.isEmpty()) {
			responseList.clear();
			adapter.notifyDataSetChanged();
		}

		for (int i = 0; i < response.size(); i++) {
			if (response.get(i).getFather_type().equals("2")) {
				// 加入到列表中
				responseList.add(response.get(i));
			}
		}

		if (responseList.size() == 0) {
			no_response.setVisibility(View.VISIBLE);
		} else {
			no_response.setVisibility(View.GONE);
		}

		response.clear();
		adapter = new ResponseAdapter(context, responseList);
		adapter.notifyDataSetChanged();
		mListView.setAdapter(adapter);
	}

	/** Item点击 */
	private void ClickItem() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				mesid = responseList.get(position - 1).getId();
				picid = responseList.get(position - 1).getPhoto_id();

				openActivity(PictureDetailsActivity.class, "picture_id", picid);

				RemoveListPosition(position - 1);
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mesid = responseList.get(position - 1).getId();

				DialogCreate(position - 1);
				return true;
			}
		});
	}

	private void DialogCreate(final int position) {

		new CustomDialog.Builder(getActivity().getParent())
				.setTitle("删除")
				.setMessage("确认删除该条回应吗？")
				.setPositiveButton(R.string.determine,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface diaInterface,
									int i) {
								RemoveListPosition(position);
								diaInterface.dismiss();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface diaInterface,
									int i) {
								diaInterface.dismiss();
							}

						}).create().show();
	}

	private void RemoveListPosition(final int position) {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();

		params.put("auth_token", token);

		fh.configTimeout(HttpUtils.TIME_OUT);

		fh.put(HttpUtils.ROOT_URL + HttpUtils.NEWS_MESSAGE + mesid, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String result = t.toString();
						Result flag = (Result) JsonUtils.fromJson(result,
								Result.class);
						if (flag.getResultcode() == 200) {
							responseList.remove(position);
							adapter.notifyDataSetChanged();
							mListView.setAdapter(adapter);
						} else if (flag.getResultcode() == 101) {

						}
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
					}
				});
	}

	@Override
	public void onRefresh() {
		getResponseList(token);
	}

}
