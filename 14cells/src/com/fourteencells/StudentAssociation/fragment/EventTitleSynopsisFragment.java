package com.fourteencells.StudentAssociation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.view.base.BaseFragment;

/**
 * 显示活动简介的碎片
 * 
 * @author LiuZhenYu
 * 
 */
public class EventTitleSynopsisFragment extends BaseFragment {

	private TextView eventSynopsis;

	private String result;

	private Event event;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = getArguments();

		result = bundle.getString("result");

		View view = inflater.inflate(R.layout.fragment_title_synopsis, null);
		eventSynopsis = (TextView) view
				.findViewById(R.id.title_fragment_synopsis);

		if (result != null) {
			event = (Event) JsonUtils.fromJson(result, Event.class);

			eventSynopsis.setText(event.getDescription());
		}

		return view;
	}

}
