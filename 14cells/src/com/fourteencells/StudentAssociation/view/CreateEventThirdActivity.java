package com.fourteencells.StudentAssociation.view;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Event;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.FileUtils;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;
import com.google.gson.reflect.TypeToken;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 创建活动 第三步
 * 
 * 是否收费
 * 
 * 人数限制
 * 
 * 活动简介（可选项）
 * 
 * 下一步，即创建活动完成提交
 * 
 * 得到服务器返回的活动网址（需要将此网址传到下一页面进行分享）
 * 
 * 创建完成后，访问我的活动接口得到我的活动列表，将我的活动列表保存到本地文件内
 * 
 * @author 师春雷
 * 
 */
public class CreateEventThirdActivity extends BaseActivity {

	private static final String TAG = "CreateEventThirdActivity";

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;
	/** 确定按钮 */
	private Button next;

	private ProgressDialog loading;

	/** 收费金额 */
	private EditText money;
	private LinearLayout ll_money;
	/** 是否收费 */
	private CheckBox cb_money;
	/** 是否收费（默认不收费） */
	public int charge;
	/** 限制人数 */
	private EditText number;
	private LinearLayout ll_number;
	/** 是否限制人数 */
	private CheckBox cb_number;
	/** 人数限制（默认不限制） */
	public int limit;
	/** 活动简介 */
	private TextView explanation;

	/** 活动收费金额 */
	private int fee;
	/** 活动最大人数 */
	private int max_number;
	/** 活动简介 */
	private String description;
	/** 用户token */
	private String token;
	/** 地址 */
	private String address;
	/** 经度 */
	private String longitude = "0.0";
	/** 纬度 */
	private String latitude = "0.0";
	/** 活动名称 */
	private String name;
	/** 开始时间 */
	private String startTime;
	/** 结束时间 */
	private String endTime;
	/** 社团ID */
	private String club_id;
	/** 活动类别 */
	private String type;

	private String url;

	private List<Event> events;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event_third);

		findViews();
		setListener();
	}

	private void findViews() {

		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		title = (TextView) findViewById(R.id.title_text);
		title.setText("创建活动");
		next = (Button) findViewById(R.id.title_right);
		next.setText("下一步");

		explanation = (EditText) findViewById(R.id.et_create_event_next_briefintr);

		ll_money = (LinearLayout) findViewById(R.id.ll_create_event_next_money);
		money = (EditText) findViewById(R.id.et_create_event_next_money);
		cb_money = (CheckBox) findViewById(R.id.checkbox_money);
		ll_number = (LinearLayout) findViewById(R.id.ll_create_event_next_number);
		number = (EditText) findViewById(R.id.et_create_event_next_number);
		cb_number = (CheckBox) findViewById(R.id.checkbox_number);

		charge = (Integer) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_charge", 0);
		limit = (Integer) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_limit", 0);
		fee = (Integer) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_fee", "");
		max_number = (Integer) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_max_number", "");
		description = (String) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_dec", "");

		if (charge == 1) {
			cb_money.isChecked();
			cb_money.setChecked(true);
			ll_money.setVisibility(View.VISIBLE);

			if (fee != 0) {
				money.setText(fee);
			}
		} else if (charge == 0) {
			money.setText("");
			cb_money.setChecked(false);
			ll_money.setVisibility(View.GONE);
		}

		if (limit == 1) {
			cb_number.isChecked();
			cb_number.setChecked(true);
			ll_number.setVisibility(View.VISIBLE);

			if (max_number != 0) {
				number.setText(max_number);
			}
		} else if (limit == 0) {
			cb_number.setChecked(false);
			number.setText("");
			ll_number.setVisibility(View.GONE);
		}

		if (description.length() > 0) {
			explanation.setText(description);
		}

	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				exitDialod();
			}
		});

		cb_money.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (cb_money.isChecked()) {
					charge = 1;
					ll_money.setVisibility(View.VISIBLE);
				} else {
					charge = 0;
					money.setText("");
					ll_money.setVisibility(View.GONE);
				}
			}
		});

		cb_number.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (cb_number.isChecked()) {
					limit = 1;
					ll_number.setVisibility(View.VISIBLE);
				} else {
					limit = 0;
					number.setText("");
					ll_number.setVisibility(View.GONE);
				}
			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				description = explanation.getText().toString().trim();

				Boolean flog = false;

				if (charge == 0) {
					fee = 0;
					flog = true;
				} else if (charge == 1) {
					fee = Integer.valueOf(money.getText().toString().trim());

					if (0 == fee) {
						showToast("请输入收费金额");
					} else {
						flog = true;
					}
				}

				if (flog) {
					if (limit == 0) {
						max_number = 0;
						saveData();
					} else if (limit == 1) {
						max_number = Integer.valueOf(number.getText()
								.toString().trim());
						if (0 == max_number) {
							showToast("请输入限制人数");
						} else {
							saveData();
						}
					}
				}
			}
		});

	}

	/***
	 * 保存数据
	 * 
	 */
	protected void saveData() {
		Log.i(TAG, "简介：" + description);
		Log.i(TAG, "人数：" + limit + "---" + max_number);
		Log.i(TAG, "收费：" + charge + "===" + fee);

		SPUtils.put(CreateEventThirdActivity.this, "create_event_dec",
				description);// 活动简介
		SPUtils.put(CreateEventThirdActivity.this, "create_event_limit", limit);// 是否有人数限制
		SPUtils.put(CreateEventThirdActivity.this, "create_event_max_number",
				max_number);// 最大人数
		SPUtils.put(CreateEventThirdActivity.this, "create_event_charge",
				charge);// 是否收费
		SPUtils.put(CreateEventThirdActivity.this, "create_event_fee", fee);// 收费金额

		getDate();
	}

	/***
	 * 得到数据
	 * 
	 */
	private void getDate() {
		token = (String) SPUtils
				.get(CreateEventThirdActivity.this, "token", "");
		address = (String) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_address", "");
		longitude = (String) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_lng", "");
		latitude = (String) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_lat", "");
		name = (String) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_name", "");
		startTime = (String) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_start_time", "");
		endTime = (String) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_end_time", "");
		club_id = (String) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_club_id", "");
		type = (String) SPUtils.get(CreateEventThirdActivity.this,
				"create_event_type", "");

		Log.i(TAG, latitude + "-------" + longitude);

		loading = ProgressDialog.show(CreateEventThirdActivity.this, "请稍后...");
		loading.show();
		putEventInfo(token, club_id, name, address, startTime, endTime,
				description, type, limit, charge, max_number, fee, latitude,
				longitude);
	}

	/**
	 * 创建活动
	 * 
	 * @param club_id
	 *            社团ID
	 * @param token
	 *            用户Token
	 * @param name
	 *            活动名称
	 * @param category
	 *            活动类型
	 * @param description
	 *            活动简介
	 * @param end_time
	 *            结束时间
	 * @param start_time
	 *            开始时间
	 * @param address
	 *            活动地址
	 * @param fee
	 *            费用
	 * @param max_number
	 *            最大限制人数
	 * @param charge
	 *            是否收费
	 * @param limit
	 *            是否有人数限制
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * 
	 */
	protected void putEventInfo(final String token, String club_id,
			String name, String address, String start_time, String end_time,
			String description, String category, int limit, int charge,
			int max_number, int fee, String latitude, String longitude) {

		AjaxParams params = new AjaxParams();
		params.put("name", name);// 活动名称
		params.put("start_time", start_time);// 开始时间
		params.put("end_time", end_time);// 结束时间
		params.put("category", category);// 活动类型
		params.put("club_id", club_id);// 社团ID
		params.put("auth_token", token);// 用户的token

		if (address.length() > 0) {
			params.put("address", address);// 活动地点
		}

		// params.put("latitude", latitude);// 活动地点纬度
		// params.put("longitude", longitude);// 活动地点经度

		if (description.length() > 0) {
			params.put("description", description);// 活动描述
		}

		params.put("limit", limit + "");// 是否有人数限制
		params.put("max_number", max_number + "");// 参加人数上限
		params.put("charge", charge + "");// 是否收费
		params.put("fee", fee + "");

		if (params != null) {
			Log.i(TAG, "上传参数：" + params.toString());
		}
		FinalHttp fh = new FinalHttp();
		fh.post(HttpUtils.ROOT_URL + HttpUtils.CREATE_EVENT, params,
				new AjaxCallBack<Object>() {

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String str = t.toString();
						Result result = (Result) JsonUtils.fromJson(str,
								Result.class);
						if (result.getResultcode() == 200) {
							// 访问网络获得对应活动的url
							url = "http://www.baidu.com";

							getMineEventList(token);
						} else if (result.getResultcode() == 101) {
							showToast("创建失败，请稍候再试！");
							loading.dismiss();
						}

					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							loading.dismiss();

							showToast("网络环境不稳定，请稍后再试");
						}
					}
				});
	}

	/***
	 * 得到我的活动列表
	 * 
	 * @param token
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void getMineEventList(String token) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.get(HttpUtils.ROOT_URL + HttpUtils.MY_PARTICIPATED_EVENT
				+ "?auth_token=" + token, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);

				String str = t.toString();
				events = (List<Event>) JsonUtils.fromJson(str,
						new TypeToken<List<Event>>() {
						});
				String event_list = null;

				if (events != null) {
					Log.i(TAG, "返回" + events.size() + "条数据");

					JSONArray object = new JSONArray();
					for (int i = 0; i < events.size(); i++) {
						JSONObject jo = new JSONObject();
						try {
							jo.put("id", events.get(i).getId());
							jo.put("start_time", events.get(i).getStart_time());
							jo.put("end_time", events.get(i).getEnd_time());
							jo.put("club_name", events.get(i).getClub_name());
							jo.put("count", events.get(i).getCount());
							jo.put("name", events.get(i).getName());
							jo.put("role", events.get(i).getRole());
							jo.put("application_id", events.get(i)
									.getApplication_id());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						object.put(jo);
					}
					event_list = object.toString();
				}
				FileUtils.write(event_list, AppConfig.EVENT_FILE_NAME);

				loading.dismiss();

				openActivity(ShareEventActivity.class, "url", url, true);
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

	private void exitDialod() {
		openActivity(CreateEventSecondActivity.class, true);
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
