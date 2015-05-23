package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.StatisticsAdapter;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.model.User;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 页面功能：实况统计
 * 
 * @author 师春雷
 * 
 */
public class StatisticsActivity extends BaseActivity {

	private static final String TAG = "StatisticsActivity";

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 活动ID */
	private String id;
	/** 用户ID */
	private String user_id;
	/** 用户的token */
	private String token = null;

	private GridView gv_list;

	private List<User> user;
	private List<User> userList = new ArrayList<User>();

	/** 签到活动适配器 */
	private StatisticsAdapter adapter;

	private ProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);

		loading = ProgressDialog.show(StatisticsActivity.this, "请稍后...");

		id = getStringExtra("event_id");

		token = (String) SPUtils.get(StatisticsActivity.this, "token", "");
		user_id = (String) SPUtils.get(StatisticsActivity.this, "id", "");
		Log.i(TAG, token + "   user_id = " + user_id);

		findViews();

		loading.show();
		showRegistration(token, id);

		setListener();
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		title = (TextView) findViewById(R.id.title_text);

		gv_list = (GridView) findViewById(R.id.gv_statistics);
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		gv_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String userId = userList.get(arg2).getId();

				if (!userId.equals(user_id)) {
					loading.show();

					registration(token, userId, id);
				}
			}
		});
	}

	/**
	 * 活动创建者手动签到或解除签到
	 * 
	 * @param token
	 * @param user_id
	 * @param event_id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void registration(final String token, String user_id,
			String event_id) {
		String URL = HttpUtils.ROOT_URL + HttpUtils.REGISTRATION + "?event_id="
				+ event_id + "&user_id=" + user_id + "&auth_token=" + token;

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(URL, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				String str = t.toString();
				Result result = (Result) JsonUtils.fromJson(str, Result.class);
				if (result.getResultcode() == 200) {
					showRegistration(token, id);
				} else if (result.getResultcode() == 101) {
					showToast("失败，请稍候再试！");
				}
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				if (t != null) {
					showToast("网络环境不稳定，请稍后再试");
				}
				
			}
		});
	}

	/**
	 * 显示签到列表
	 * 
	 * @param token
	 * @param id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void showRegistration(String token, String id) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);

		String URL = HttpUtils.ROOT_URL + HttpUtils.STATISTIC + "?event_id="
				+ id + "&auth_token=" + token;

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
				Log.i(TAG, "返回签到统计信息：" + str);
				user = (List<User>) JsonUtils.fromJson(str,
						new TypeToken<List<User>>() {
						});

				// 已经签到人数
				int Molecular = 0;

				for (int i = 0; i < user.size(); i++) {
					if (user.get(i).getPresent().equals("1")) {
						Molecular++;
					}
				}

				// 活动总人数
				int Total = user.size();
				title.setText("参与人数(" + Molecular + "/" + Total + ")");

				setAdapter();
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				Log.i(TAG, strMsg);
				Log.i(TAG, t.toString());
				if (t != null) {
					loading.dismiss();
					showToast("网络环境不稳定，请稍后再试");
				}
			}
		});
	}

	protected void setAdapter() {
		if (userList != null && !userList.isEmpty()) {
			userList.clear();
			adapter.notifyDataSetChanged();
		}

		userList.addAll(user);
		user.clear();
		adapter = new StatisticsAdapter(this, userList);
		gv_list.setAdapter(adapter);
		loading.dismiss();
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}
