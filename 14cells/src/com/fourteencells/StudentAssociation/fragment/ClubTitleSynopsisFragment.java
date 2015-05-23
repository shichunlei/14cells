package com.fourteencells.StudentAssociation.fragment;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Club;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;

public class ClubTitleSynopsisFragment extends BaseFragment {

	private TextView clubSynopsis;

	private String id;
	private String token;

	private Club clubs = new Club();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		id = bundle.getString("club_id");
		token = bundle.getString("token");

		View view = inflater.inflate(R.layout.fragment_title_synopsis, null);

		clubSynopsis = (TextView) view
				.findViewById(R.id.title_fragment_synopsis);

		getClubInfo(id);

		return view;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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

				clubSynopsis.setText(clubs.getDescription());
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
