package com.fourteencells.StudentAssociation.fragment;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.model.Club;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;

public class ClubTitlePicFragment extends BaseFragment {

	private final static String TAG = "ClubTitlePicFragment";

	private MyCircleImageView headPic;
	private TextView clubNanme;
	private String id;
	private String token;

	private Club clubs = new Club();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		id = bundle.getString("club_id");
		token = bundle.getString("token");

		View view = inflater.inflate(R.layout.fragment_club_title_pic, null);

		headPic = (MyCircleImageView) view
				.findViewById(R.id.img_headpic_clubtab);
		clubNanme = (TextView) view.findViewById(R.id.club_name);

		getClubInfo(id);
		return view;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getClubInfo(String id) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);

		String url = HttpUtils.ROOT_URL + HttpUtils.GET_CLUBS_DETAILS + id
				+ "?auth_token=" + token;

		fh.get(url, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);

				String str = t.toString();

				clubs = (Club) JsonUtils.fromJson(str, Club.class);

				clubNanme.setText(clubs.getName());

				try {
					FinalBitmap fb = FinalBitmap.create(getActivity());
					fb.configLoadingImage(R.drawable.default_image);
					fb.display(headPic, clubs.getLogo().getLogo().getUrl());
				} catch (Exception e) {
					if (e != null) {
						Log.e(TAG, e.toString());
					}
				}
			}

			@Override
			public void onFailure(Throwable t, int errorCode, String strMsg) {
				if (t != null) {
				}
				super.onFailure(t, errorCode, strMsg);
			}
		});
	}

}
