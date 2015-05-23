package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.SearchPlaceAdapter;
import com.fourteencells.StudentAssociation.model.SearchPlace;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.Tools;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 创建活动
 * 
 * 活动地点（可选）
 * 
 * @author 师春雷
 * 
 */
public class CreateEventSecondActivity extends BaseActivity {

	private static final String TAG = "CreateEventSecondActivity";

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 跳过 */
	private Button next;

	/** 搜索 */
	private Button mSearch;
	/** 搜索关键字 */
	private EditText keyWord;

	private String keyWords;

	/** 搜索结果 */
	private String result;

	private ListView listView;

	/** 加载对话框 */
	private ProgressDialog dialog;

	/** 活动地点 */
	private String address;

	private double longitude = 0.0;// 经度
	private double latitude = 0.0;// 纬度

	private String lng = "0.0";
	private String lat = "0.0";

	private List<SearchPlace> searchList = new ArrayList<SearchPlace>();
	private List<SearchPlace> search;
	/** 搜索结果适配器 */
	private SearchPlaceAdapter searchAdapter;

	public LocationManager lManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event_second);

		findViews();
		setListener();
	}

	private void findViews() {
		address = (String) SPUtils.get(CreateEventSecondActivity.this,
				"create_event_address", "");

		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);

		title = (TextView) findViewById(R.id.title_text);
		title.setText("位置");

		next = (Button) findViewById(R.id.title_right);
		next.setText("跳过");

		lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		dialog = new ProgressDialog(this);

		mSearch = (Button) findViewById(R.id.tv_create_event_second_select);
		keyWord = (EditText) findViewById(R.id.et_create_event_second_keyword);

		listView = (ListView) findViewById(R.id.lv_create_event_second_list);

		if (address.length() > 0) {
			keyWord.setText(address);
		}
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				exitDialod();
			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				address = keyWord.getText().toString().trim();

				SPUtils.put(CreateEventSecondActivity.this,
						"create_event_address", address);

				openActivity(CreateEventThirdActivity.class, true);
			}
		});

		mSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				keyWords = keyWord.getText().toString().trim();
				if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					showToast("请开启GPS导航...");
					// 返回开启GPS导航设置界面
					Intent intent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 0);
					return;
				} else {
					if (keyWords.equals("") || null == keyWords) {
						showToast("请输入搜索关键字");
					} else {

						getLocation();
					}
				}
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				address = searchList.get(arg2).getName();
				lng = searchList.get(arg2).getLocation().getLng();
				lat = searchList.get(arg2).getLocation().getLat();

				Log.i(TAG, "地点：" + address);
				Log.i(TAG, lng + lat);

				SPUtils.put(CreateEventSecondActivity.this,
						"create_event_address", address);
				SPUtils.put(CreateEventSecondActivity.this, "create_event_lng",
						lng);
				SPUtils.put(CreateEventSecondActivity.this, "create_event_lat",
						lat);

				openActivity(CreateEventThirdActivity.class, true);
			}
		});

	}

	/**
	 * 得到当前位置经纬度
	 */
	private void getLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();

			} else {
				LocationListener locationListener = new LocationListener() {

					// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}

					// Provider被enable时触发此函数，比如GPS被打开
					@Override
					public void onProviderEnabled(String provider) {
					}

					// Provider被disable时触发此函数，比如GPS被关闭
					@Override
					public void onProviderDisabled(String provider) {
					}

					// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
					@Override
					public void onLocationChanged(Location location) {

						Log.i(TAG, location.toString());
						if (location != null) {
							Log.i("Map",
									"Location changed : Lat: "
											+ location.getLatitude() + " Lng: "
											+ location.getLongitude());
							latitude = location.getLatitude(); // 经度
							longitude = location.getLongitude(); // 纬度
						}
					}
				};
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListener);
				Location location0 = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location0 != null) {
					latitude = location0.getLatitude(); // 经度
					longitude = location0.getLongitude(); // 纬度
				}
			}
		}
		if (latitude == 0.0 && longitude == 0.0) {
			showToast("定位失败，检查是否开启GPS");
		} else {
			dialog.setMessage("正在获取搜索结果...");
			dialog.show();
			getSearchPlace(longitude, latitude, keyWords);
		}

	}

	/***
	 * 得到搜索位置列表
	 * 
	 * @param latitude
	 * @param longitude
	 * @param keyWords
	 */
	private void getSearchPlace(double longitude, double latitude,
			String keyWords) {
		String search_url = HttpUtils.ROOT_BAIDUMAP_URL
				+ HttpUtils.BAIDUMAP_SEARCH_PLACE + "?query=" + keyWords
				+ "&location=" + latitude + "," + longitude
				+ "&radius=2000&output=json&ak=" + AppConfig.BAIDU_MAP_KEY;

		AjaxParams params = new AjaxParams();

		FinalHttp fh = new FinalHttp();
		fh.post(search_url, params, new AjaxCallBack<Object>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				String str = t.toString();
				String str2 = Tools.replaceBlank(str);
				result = JsonUtils.ParserPlaceInfo("[" + str2 + "]");
				search = (List<SearchPlace>) JsonUtils.fromJson(result,
						new TypeToken<List<SearchPlace>>() {
						});

				if (search.size() == 0) {
					showToast("没有你想要的结果");
				} else {
					setSearchAdapter();
				}
			}

			protected void setSearchAdapter() {
				if (searchList != null && !searchList.isEmpty()) {
					searchList.clear();
					searchAdapter.notifyDataSetChanged();
				}

				searchList.addAll(search);
				search.clear();
				searchAdapter = new SearchPlaceAdapter(getApplicationContext(),
						searchList);
				searchAdapter.notifyDataSetChanged();
				listView.setAdapter(searchAdapter);
				dialog.dismiss();
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				dialog.dismiss();
				showToast("网络不稳定！");
			}
		});
	}

	private void exitDialod() {
		openActivity(CreateEventFirstActivity.class, true);
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitDialod();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
