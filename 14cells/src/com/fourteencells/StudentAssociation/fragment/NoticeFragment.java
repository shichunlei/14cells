package com.fourteencells.StudentAssociation.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.NoticeAdapter;
import com.fourteencells.StudentAssociation.customview.CustomDialog;
import com.fourteencells.StudentAssociation.customview.refresh.XListView;
import com.fourteencells.StudentAssociation.customview.refresh.XListView.HeaderListener;
import com.fourteencells.StudentAssociation.model.Messages;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.*;
import com.fourteencells.StudentAssociation.view.CheckMemberActivity;
import com.fourteencells.StudentAssociation.view.NewsFragmentActivity;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
 * 通知碎片
 * 
 * 展示加入社团和同意加入社团的通知列表
 * 
 * 点击每一个加入社团通知列表项可进入个人信息界面操作（同意或拒绝加入）
 * 
 * 在个人界面操作完毕后返回该界面，重新访问网络数据，对应操作的通知不在新的列表中
 * 
 * 长按每一个通知列表项可删除该条通知
 * 
 * @author 师春雷
 * 
 */
public class NoticeFragment extends BaseFragment implements HeaderListener {

	private String TAG = "NoticeFragment";

	Context context;

	private XListView mListView;

	private TextView no_notice;

	private NoticeAdapter adapter;

	private List<Messages> notice;
	private List<Messages> noticeList = new ArrayList<Messages>();

	private String token;

	private String mesid;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_notice, container, false);
		init(view);

		ClickItem();

		return view;
	}

	/**
	 * 初始化方法
	 */
	private void init(View view) {
		context = getActivity();

		token = (String) SPUtils.get(getActivity(), "token", "");

		mListView = (XListView) view.findViewById(R.id.noticeListView);

		mListView.setPullLoadEnable(false);
		mListView.setHeaderListener(this);

		no_notice = (TextView) view.findViewById(R.id.tv_no_notice);
	}

	/**
	 * 得到数据
	 * 
	 * @param token
	 */
	public void getNoticeList(String token) {

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

						notice = (List<Messages>) JsonUtils.fromJson(result,
								new TypeToken<List<Messages>>() {
								});
						if (notice.size() > 0) {
							setAdapter();
						} else {
							no_notice.setVisibility(View.VISIBLE);
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
		if (noticeList != null && !noticeList.isEmpty()) {
			noticeList.clear();
			adapter.notifyDataSetChanged();
		}

		for (int i = 0; i < notice.size(); i++) {
			if (notice.get(i).getFather_type().equals("1")) {
				// 加入到列表中
				noticeList.add(notice.get(i));
			}
		}

		if (noticeList.size() == 0) {
			no_notice.setVisibility(View.VISIBLE);
		} else {
			no_notice.setVisibility(View.GONE);
		}

		notice.clear();
		adapter = new NoticeAdapter(context, noticeList);
		adapter.notifyDataSetChanged();
		mListView.setAdapter(adapter);
	}

	private void DialogCreate(final int position) {

		new CustomDialog.Builder(getActivity().getParent())
				.setTitle("删除")
				.setMessage("确认删除该条通知吗？")
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

	/**
	 * 删除选定的某一条数据
	 * 
	 * @param position
	 */
	private void RemoveListPosition(final int position) {

		AjaxParams params = new AjaxParams();
		params.put("auth_token", token);

		FinalHttp fh = new FinalHttp();
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
							Log.i(TAG, flag + result);
							noticeList.remove(position);
							adapter.notifyDataSetChanged();
							mListView.setAdapter(adapter);
						} else if (flag.getResultcode() == 101) {
							Log.i(TAG, result + flag);
						}
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
					}

				});
	}

	/**
	 * ListView的Item点击
	 * 
	 * 分为单击和长按
	 */
	private void ClickItem() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				mesid = noticeList.get(position - 1).getId();

				if (noticeList.get(position - 1).getSub_type().equals("1")) {
					Intent intent = new Intent();

					intent.putExtra("userID", noticeList.get(position - 1)
							.getUser_id());
					intent.putExtra("clubID", noticeList.get(position - 1)
							.getClub_id());
					intent.putExtra("mess", mesid);

					intent.setClass(context, CheckMemberActivity.class);

					startActivity(intent);
				}
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				mesid = noticeList.get(position - 1).getId();
				DialogCreate(position - 1);

				return true;
			}
		});
	}

	@Override
	public void onRefresh() {
		getNoticeList(token);
	}
}
