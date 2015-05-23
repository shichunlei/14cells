package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.adpter.LocationAdapter;
import com.fourteencells.StudentAssociation.adpter.SearchPlaceAdapter;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Place;
import com.fourteencells.StudentAssociation.model.Point;
import com.fourteencells.StudentAssociation.model.SearchPlace;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.Tools;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
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
 * 图片定位:当前位置附近热点、搜索当前位置附近热点
 * 
 * @author 师春雷
 * 
 */
public class LocationActivity extends BaseActivity {

	private static final String TAG = "LocationActivity";

	/** 标头 */
	private TextView title;
	/** 当前位置 */
	private ImageView CurrentPosition;
	/** 取消 */
	private Button cancel;
	/** 结果列表 */
	private ListView listView;
	/** 搜索关键字 */
	private EditText searchKeyWords;
	private String keyWords;
	/** 搜索 */
	private Button Search;
	/** 搜索结果 */
	private String result;

	private boolean is = true;

	double longitude = 0.0;// 经度
	private String lng;
	double latitude = 0.0;// 纬度
	private String lat;

	private String address_name;

	private String x, y;

	private List<Place> placeList = new ArrayList<Place>();
	private List<Place> place;
	/** 定位结果适配器 */
	private LocationAdapter locationAdapter;

	private List<SearchPlace> searchList = new ArrayList<SearchPlace>();
	private List<SearchPlace> search;
	/** 搜索结果适配器 */
	private SearchPlaceAdapter searchAdapter;

	Point point = new Point();

	private ProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_location);

		loading = ProgressDialog.show(LocationActivity.this, "请稍后...");

		findViews();
		getLocation();
		setListener();
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

			loading.show();
			// getLocationList(longitude, latitude);

			CorrectCoordinates(longitude, latitude);
		}

	}

	/***
	 * 纠偏经纬度
	 * 
	 * @param longitude
	 * @param latitude
	 */
	private void CorrectCoordinates(double longitude, double latitude) {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		// 添加参数
		params.put("ak", AppConfig.BAIDU_MAP_KEY);
		params.put("callback", "renderReverse");
		params.put("coords", longitude + "," + latitude);
		params.put("output", "json");
		params.put("from", "1");
		params.put("to", "5");
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.post(HttpUtils.ROOT_BAIDUMAP_URL + HttpUtils.BAIDUMAP_CORRECT,
				params, new AjaxCallBack<Object>() {

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();
						Log.i(TAG, "纠偏结果：" + str);

						point = JsonUtils.ParserCorrectCoordinates(str);

						Log.i(TAG, point.toString());

						x = point.getX();
						y = point.getY();

						getLocationList(y, x);
					}

					@Override
					public void onFailure(Throwable t, int errorCode,
							String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						loading.dismiss();
					}
				});
	}

	private void findViews() {
		title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.location);
		CurrentPosition = (ImageView) findViewById(R.id.title_back);
		CurrentPosition.setImageResource(R.drawable.location);
		cancel = (Button) findViewById(R.id.title_right);
		cancel.setText(R.string.cancel);

		listView = (ListView) findViewById(R.id.lv_select_location_list);
		searchKeyWords = (EditText) findViewById(R.id.search_place_edit);
		Search = (Button) findViewById(R.id.search_place_text);
	}

	private void setListener() {
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openActivity(CreatePictureActivity.class, true);
			}
		});

		CurrentPosition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 清空屏幕
				if (is) {
					if (null != placeList) {
						placeList.clear();
						locationAdapter.notifyDataSetChanged();
						Log.i(TAG, "清空当前位置热点位置信息");
					}
				} else {
					if (null != searchList) {
						searchList.clear();
						searchAdapter.notifyDataSetChanged();
						Log.i(TAG, "清空搜索位置信息");
					}
				}

				is = true;
				loading.show();
				getLocationList(y, x);
			}
		});

		Search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				keyWords = searchKeyWords.getText().toString().trim();

				if (null == keyWords || keyWords.equals("")) {
					showToast("请输入搜索位置！");
				} else {
					// 清空屏幕
					if (is) {
						if (null != placeList) {
							placeList.clear();
							// locationAdapter.notifyDataSetChanged();
							Log.i(TAG, "清空当前位置热点位置信息");
						}
					} else {
						if (null != searchList) {
							searchList.clear();
							// searchAdapter.notifyDataSetChanged();
							Log.i(TAG, "清空搜索位置信息");
						}
					}
					is = false;
					loading.show();
					getSearchPlace(longitude, latitude, keyWords);
				}
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (is) {
					address_name = placeList.get(arg2).getName();
					lng = placeList.get(arg2).getPoint().getX();
					lat = placeList.get(arg2).getPoint().getY();
				} else {
					address_name = searchList.get(arg2).getName();
					lng = searchList.get(arg2).getLocation().getLng();
					lat = searchList.get(arg2).getLocation().getLat();
				}

				SPUtils.put(LocationActivity.this,
						"choose_location_address_name", address_name);
				SPUtils.put(LocationActivity.this, "choose_location_lng", lng);
				SPUtils.put(LocationActivity.this, "choose_location_lat", lat);

				openActivity(CreatePictureActivity.class, true);
			}
		});
	}

	/***
	 * 得到搜索位置列表
	 * 
	 * @param longitude
	 * @param latitude
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

			@Override
			public void onFailure(Throwable t, int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				loading.dismiss();
				showToast("网络不稳定！");
			}
		});
	}

	protected void setSearchAdapter() {
		if (searchList != null && !searchList.isEmpty()) {
			searchList.clear();
			searchAdapter.notifyDataSetChanged();
		}

		searchList.addAll(search);
		search.clear();
		searchAdapter = new SearchPlaceAdapter(LocationActivity.this,
				searchList);
		searchAdapter.notifyDataSetChanged();
		listView.setAdapter(searchAdapter);
		loading.dismiss();
	}

	/**
	 * 得到当前位置热点列表
	 * 
	 * @param y
	 * @param x
	 */
	private void getLocationList(String y, String x) {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		// 添加参数
		params.put("ak", AppConfig.BAIDU_MAP_KEY);
		params.put("callback", "renderReverse");
		params.put("location", y + "," + x);
		params.put("output", "json");
		params.put("pois", "1");
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.post(HttpUtils.ROOT_BAIDUMAP_URL + HttpUtils.BAIDUMAP_LOCATION,
				params, new AjaxCallBack<Object>() {

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();
						if (str != null) {
							Log.i(TAG, str);
						}
						place = JsonUtils.ParserPlaceList(str);
						if (place != null) {
							Log.i(TAG, place.toString());
						}
						setLocationAdapter();
					}

					@Override
					public void onFailure(Throwable t, int errorCode,
							String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						loading.dismiss();
						showToast("亲，网络不给力！");
					}
				});
	}

	protected void setLocationAdapter() {
		if (placeList != null && !placeList.isEmpty()) {
			placeList.clear();
			locationAdapter.notifyDataSetChanged();
		}
		if (place != null) {
			placeList.addAll(place);
			place.clear();
		}
		locationAdapter = new LocationAdapter(LocationActivity.this, placeList);
		locationAdapter.notifyDataSetChanged();
		listView.setAdapter(locationAdapter);
		loading.dismiss();
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openActivity(CreatePictureActivity.class, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
