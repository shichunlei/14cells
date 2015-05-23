package com.fourteencells.StudentAssociation.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.PictureAdapter;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnFooterRefreshListener;
import com.fourteencells.StudentAssociation.customview.PullToRefreshView.OnHeaderRefreshListener;
import com.fourteencells.StudentAssociation.model.PictureDetail;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.NetUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.ClubFragmentActivity;
import com.fourteencells.StudentAssociation.view.EventFragmentActivity;
import com.fourteencells.StudentAssociation.view.MineFragmentActivity;
import com.fourteencells.StudentAssociation.view.PictureDetailsActivity;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

/**
 * 页面功能：图片碎片
 * 
 * @author 师春雷
 * 
 */
public class PictureFragment extends BaseFragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private final static String TAG = "PictureFragment";

	private PullToRefreshView mPullToRefreshView;

	private GridView mGridView;

	private TextView noPicture;

	private List<PictureDetail> pictureList = new ArrayList<PictureDetail>();
	private List<PictureDetail> picture;

	private PictureAdapter adapter;

	/** 用户的token */
	private String token = null;
	private int page = 1;
	/** 从其他Activity中传过来的社团ID */
	private String id = null;

	private String type = "header";

	private String mURL;

	private String flog;

	public PictureFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_picture, null);

		findViews(view);

		init();

		if (NetUtils.isNetUseful()) {
			getPicture(page);
		} else {
			noPicture.setVisibility(View.VISIBLE);
		}

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				openActivity(PictureDetailsActivity.class, "picture_id",
						pictureList.get(position).getId());
			}
		});

		return view;
	}

	private void findViews(View view) {
		mPullToRefreshView = (PullToRefreshView) view
				.findViewById(R.id.picture_pull_refresh_view);

		mGridView = (GridView) view.findViewById(R.id.gv_picture_fragment);

		noPicture = (TextView) view.findViewById(R.id.tv_no_picture);
	}

	private void init() {
		token = (String) SPUtils.get(getActivity(), "token", "");
		flog = (String) SPUtils.get(getActivity(), "picture_type", "");

		if (flog.equals("mine")) {
			mURL = HttpUtils.ROOT_URL + HttpUtils.PUBLISH_PHOTO
					+ "?auth_token=" + token;
		} else {
			id = (String) SPUtils.get(getActivity(), "type_id", "");
			if (flog.equals("club")) {
				mURL = HttpUtils.ROOT_URL + HttpUtils.GET_CLUBS_ALBUM_LIST
						+ "?club_id=" + id + "&auth_token=" + token;
			} else if (flog.equals("event")) {
				mURL = HttpUtils.ROOT_URL + HttpUtils.GET_EVENTS_ALBUMS_LIST
						+ "?event_id=" + id + "&auth_token=" + token;
			}
		}

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
	}

	/**
	 * 得到发布的图片
	 * 
	 * @param page
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getPicture(int page) {

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(mURL + "&page=" + page, new AjaxCallBack() {

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);

				String result = t.toString();
				picture = (List<PictureDetail>) JsonUtils.fromJson(result,
						new TypeToken<List<PictureDetail>>() {
						});

				Log.i(TAG, picture.toString() + "\n" + picture.size());

				if (picture.size() > 0) {
					noPicture.setVisibility(View.GONE);
					setPictureAdapter();
				} else {
					if (pictureList.size() > 0) {
						noPicture.setVisibility(View.GONE);
					} else {
						noPicture.setVisibility(View.VISIBLE);
					}
				}

				if (flog.equals("mine")) {
					MineFragmentActivity.loading.dismiss();
				} else if (flog.equals("event")) {
					EventFragmentActivity.loading.dismiss();
				} else if (flog.equals("club")) {
					ClubFragmentActivity.loading.dismiss();
				}
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				noPicture.setVisibility(View.VISIBLE);
				if (flog.equals("mine")) {
					MineFragmentActivity.loading.dismiss();
				} else if (flog.equals("event")) {
					EventFragmentActivity.loading.dismiss();
				} else if (flog.equals("club")) {
					ClubFragmentActivity.loading.dismiss();
				}

				super.onFailure(t, errorCode, strMsg);
			}
		});
	}

	private void setPictureAdapter() {
		if (type.equals("header")) {
			if (pictureList != null && !pictureList.isEmpty()) {
				pictureList.clear();
				adapter.notifyDataSetChanged();
			}

			pictureList.addAll(picture);
			adapter = new PictureAdapter(getActivity(), pictureList);
			adapter.notifyDataSetChanged();
			mGridView.setAdapter(adapter);
		} else if (type.equals("footer")) {
			noPicture.setVisibility(View.GONE);
			pictureList.addAll(picture);
			adapter = new PictureAdapter(getActivity(), pictureList);
			adapter.notifyDataSetChanged();
			mGridView.setAdapter(adapter);
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page++;
				type = "footer";
				getPicture(page);
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page = 1;
				type = "header";

				getPicture(page);
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 2000);
	}
}