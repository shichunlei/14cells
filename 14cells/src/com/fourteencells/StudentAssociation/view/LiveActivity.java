package com.fourteencells.StudentAssociation.view;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import com.fourteencells.StudentAssociation.MyApplication;
import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.LiveAdapter;
import com.fourteencells.StudentAssociation.customview.waterfall.PLA_AdapterView;
import com.fourteencells.StudentAssociation.customview.waterfall.YListView;
import com.fourteencells.StudentAssociation.customview.waterfall.PLA_AdapterView.OnItemClickListener;
import com.fourteencells.StudentAssociation.customview.waterfall.YListView.IXListViewListener;
import com.fourteencells.StudentAssociation.model.Live;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.TimeUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * 直播页面
 * 
 * @author 师春雷
 * 
 */
public class LiveActivity extends BaseActivity implements IXListViewListener {

	private YListView xListview = null;
	private LiveAdapter adapter = null;
	private int currentPage;

	private ImageView image;
	private List<Live> live;
	/** 用户的token */

	private String token = null;

	private boolean isfirst = false;

	private LayoutParams params;

	private WindowManager wm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live);

		// 加到一个ActivityListView中
		MyApplication.getInstance().addActivity(this);

		token = (String) SPUtils.get(LiveActivity.this, "token", "");

		findViews();
		setListener();
	}

	@SuppressWarnings("deprecation")
	private void findViews() {
		image = (ImageView) findViewById(R.id.title_image_min);

		params = image.getLayoutParams();

		wm = this.getWindowManager();

		int ScreenW = wm.getDefaultDisplay().getWidth();
		int ScreenH = wm.getDefaultDisplay().getHeight();

		params.width = (int) (ScreenW * 0.47 * 1.25);
		params.height = (int) (ScreenH * 0.0475 * 1.25);

		image.setImageResource(R.drawable.slogan);
		image.setLayoutParams(params);

		xListview = (YListView) findViewById(R.id.list);

		xListview.setSelector(new ColorDrawable(Color.TRANSPARENT));

		xListview.setPullLoadEnable(true);
		xListview.setXListViewListener(this);

		adapter = new LiveAdapter(this, xListview);
	}

	private void setListener() {
		xListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(PLA_AdapterView<?> parent, View view,
					int position, long id) {
				openActivity(PictureDetailsActivity.class, "picture_id",
						((Live) adapter.getItem(position - 1)).getId(), false);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isfirst) {
			currentPage = 1;
			xListview.setAdapter(adapter);
			AddItemToContainer(currentPage, 2, token);
		}
		isfirst = true;
	}

	@Override
	public void onRefresh() {
		// adapter.clear();
		currentPage = 1;
		AddItemToContainer(currentPage, 1, token);
	}

	@Override
	public void onLoadMore() {
		AddItemToContainer(++currentPage, 2, token);
	}

	/**
	 * 添加内容
	 * 
	 * @param pageindex
	 * @param type
	 *            1为下拉刷新 2为加载更多
	 * @param token
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void AddItemToContainer(int pageindex, final int type, String token) {
		String url = HttpUtils.ROOT_URL + HttpUtils.FIND + "?page=" + pageindex
				+ "&auth_token=" + token;

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(url, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);

				String str = t.toString();

				live = (List<Live>) JsonUtils.fromJson(str,
						new TypeToken<List<Live>>() {
						});

				if (type == 1) {
					adapter.clear();
					adapter.addItemTop(live);
					adapter.notifyDataSetChanged();
					xListview.stopRefresh();
					xListview.setRefreshTime(TimeUtils.upDataTime());
				} else if (type == 2) {
					adapter.addItemLast(live);
					adapter.notifyDataSetChanged();
					xListview.stopLoadMore();
				}
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				if (t != null) {
					showToast("网络环境不稳定，请稍后再试");
					if (type == 2) {
						xListview.stopLoadMore();
					} else if (type == 1) {
						xListview.stopRefresh();
						xListview.setRefreshTime(TimeUtils.upDataTime());
					}
				}
			}
		});
	}

}