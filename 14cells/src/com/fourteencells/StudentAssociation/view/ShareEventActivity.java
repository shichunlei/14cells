package com.fourteencells.StudentAssociation.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.sharesdk.douban.Douban;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.TimeUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * 分享活动
 * 
 * 展示活动基本信息
 * 
 * 将活动的网络地址（接收前一个页面传过来的值）分享到公共平台上
 * 
 * 邮件发送、短信发送分享给好友
 * 
 * @author 师春雷
 * 
 */
public class ShareEventActivity extends BaseActivity implements
		PlatformActionListener, Callback {

	private static final String TAG = "ShareEventActivity";

	private Context context;

	/** 返回按钮 */
	private ImageView back;
	/** 标题 */
	private TextView title;

	/** 活动名称 */
	private TextView eventName;
	/** 活动分类Logo */
	private ImageView logo;
	/** 活动类型 */
	private TextView logoType;

	private ImageView sinaImage;
	private ImageView renrenImage;
	private ImageView tencentImage;
	private ImageView doubanImage;

	private LinearLayout sinaLayout;
	private LinearLayout renrenLayout;
	private LinearLayout tencentLayout;
	private LinearLayout doubanLayout;

	/** 活动地点 */
	private LinearLayout addressLayout;
	/** 活动费用 */
	private LinearLayout feeLayout;
	/** 活动限制最大人数 */
	private LinearLayout numberLayout;
	/** 活动简介 */
	private LinearLayout decLayout;

	/** 活动时间 */
	private TextView eventstartTime;
	/** 活动时间 */
	private TextView eventendTime;
	/** 活动地点 */
	private TextView eventAddress;
	/** 活动人数 */
	private TextView eventNumber;
	/** 活动费用 */
	private TextView eventCharge;
	/** 活动简介 */
	private TextView eventDec;

	/** 短信发送 */
	private TextView sms;
	/** 邮件发送 */
	private TextView email;

	/** 完成按钮 */
	private Button submit;

	private String url;

	/** 地址 */
	private String address;
	/** 活动名称 */
	private String name;
	/** 开始时间 */
	private String startTime;
	/** 结束时间 */
	private String endTime;

	private SimpleDateFormat formatter;
	private Date date = null;

	/** 活动描述 */
	private String dec;

	/** 是否收费 */
	private int charge;
	/** 活动费用 */
	private int fee;
	/** 是否有人数限制 */
	private int limit;
	/** 最大人数 */
	private int number;
	/** 活动类别 */
	private String type;

	/** 加载对话框 */
	private ProgressDialog dialog;

	private String flog = "";

	/** 新浪，腾讯，人人，豆瓣图标点亮状态标记位 */
	private boolean sinaFlag, tencentFlag, renrenFlag, doubanFlag = false;

	/** 新浪，腾讯，人人，豆瓣授权状态标记位 */
	private boolean sinaFlog, tencentFlog, renrenFlog, doubanFlog = false;

	private Platform weibo, renrenwang, tencentweibo, doubanwang;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_event);

		context = ShareEventActivity.this;

		ShareSDK.initSDK(context);

		url = getStringExtra("url");

		Log.i(TAG, url);

		getData();

		init();

		setListener();
	}

	/***
	 * 得到用户编辑的数据
	 * 
	 */
	private void getData() {

		address = (String) SPUtils.get(context, "create_event_address", "");
		name = (String) SPUtils.get(context, "create_event_name", "");
		startTime = (String) SPUtils
				.get(context, "create_event_start_time", "");
		endTime = (String) SPUtils.get(context, "create_event_end_time", "");
		dec = (String) SPUtils.get(context, "create_event_dec", "");
		type = (String) SPUtils.get(context, "create_event_type", "");// 类型
		limit = (Integer) SPUtils.get(context, "create_event_limit", "");// 是否有人数限制
		number = (Integer) SPUtils.get(context, "create_event_max_number", 0);// 最大人数
		charge = (Integer) SPUtils.get(context, "create_event_charge", 0);// 是否收费
		fee = (Integer) SPUtils.get(context, "create_event_fee", 0);// 收费金额
	}

	/**
	 * 控件初始化方法
	 */
	private void init() {

		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		title = (TextView) findViewById(R.id.title_text);
		title.setText("活动概览");

		eventName = (TextView) findViewById(R.id.create_event_v4_event_name);
		logo = (ImageView) findViewById(R.id.create_event_v4_event_logo);
		logoType = (TextView) findViewById(R.id.create_event_v4_type);

		sinaImage = (ImageView) findViewById(R.id.img_create_event_share_sina);
		renrenImage = (ImageView) findViewById(R.id.img_create_event_share_renren);
		tencentImage = (ImageView) findViewById(R.id.img_create_event_share_tencent);
		doubanImage = (ImageView) findViewById(R.id.img_create_event_share_douban);

		sinaLayout = (LinearLayout) findViewById(R.id.ll_create_event_share_sina);
		renrenLayout = (LinearLayout) findViewById(R.id.ll_create_event_share_renren);
		tencentLayout = (LinearLayout) findViewById(R.id.ll_create_event_share_tencent);
		doubanLayout = (LinearLayout) findViewById(R.id.ll_create_event_share_douban);

		weibo = ShareSDK.getPlatform(getApplicationContext(), SinaWeibo.NAME);
		renrenwang = ShareSDK.getPlatform(getApplicationContext(), Renren.NAME);
		tencentweibo = ShareSDK.getPlatform(getApplicationContext(),
				TencentWeibo.NAME);
		doubanwang = ShareSDK.getPlatform(getApplicationContext(), Douban.NAME);

		addressLayout = (LinearLayout) findViewById(R.id.ll_create_event_v4_address);
		feeLayout = (LinearLayout) findViewById(R.id.ll_create_event_v4_charge);
		numberLayout = (LinearLayout) findViewById(R.id.ll_create_event_v4_number);
		decLayout = (LinearLayout) findViewById(R.id.ll_create_event_v4_dec);

		eventstartTime = (TextView) findViewById(R.id.create_event_v4_starttime);
		eventendTime = (TextView) findViewById(R.id.create_event_v4_endtime);
		eventAddress = (TextView) findViewById(R.id.create_event_v4_address);
		eventNumber = (TextView) findViewById(R.id.create_event_v4_limit);
		eventCharge = (TextView) findViewById(R.id.create_event_v4_charge);
		eventDec = (TextView) findViewById(R.id.create_event_v4_dec);

		sms = (TextView) findViewById(R.id.tv_create_event_v4_sms);
		email = (TextView) findViewById(R.id.tv_create_event_v4_email);
		submit = (Button) findViewById(R.id.create_event_v4_submit);

		assignment();
	}

	/***
	 * 
	 * 赋值
	 * 
	 */
	private void assignment() {
		if (name.length() > 0) {
			eventName.setText(name);
		}

		if (type == "conference") {
			logo.setImageResource(R.drawable.btn_bg_event_shank);
			logoType.setText(R.string.conference);
		} else if (type == "sports") {
			logo.setImageResource(R.drawable.btn_bg_sports_shank);
			logoType.setText(R.string.sports);
		} else if (type == "party") {
			logo.setImageResource(R.drawable.btn_bg_party_shank);
			logoType.setText(R.string.party);
		} else if (type == "trip") {
			logo.setImageResource(R.drawable.btn_bg_travel_shank);
			logoType.setText(R.string.travel);
		}

		if (address.length() > 0) {
			eventAddress.setText(address);
		} else {
			addressLayout.setVisibility(View.GONE);
		}

		if (startTime.length() > 0) {
			eventstartTime.setText(timeConversion(startTime));
		}

		if (endTime.length() > 0) {
			eventendTime.setText(timeConversion(endTime));
		}

		if (charge == 0) {
			feeLayout.setVisibility(View.GONE);
		} else if (charge == 1) {
			eventCharge.setText(fee + " 元");
		}

		if (limit == 0) {
			numberLayout.setVisibility(View.GONE);
		} else if (limit == 1) {
			eventNumber.setText(number + " 人");
		}

		if (dec.length() > 0) {
			eventDec.setText(dec);
		} else {
			decLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 时间转换
	 * 
	 * @param time
	 * @return
	 */
	private String timeConversion(String time) {
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			date = formatter.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String strtime = TimeUtils.convertTime(date);

		return strtime;
	}

	/**
	 * 事件
	 * 
	 */
	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				exitDialod();
			}
		});

		sinaLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sinaFlag == false) {

					sinaFlag = true;
					weibo.setPlatformActionListener(ShareEventActivity.this);
					if (weibo.isValid()) {
						sinaFlog = true;
						sinaImage.setImageDrawable(getResources().getDrawable(
								R.drawable.icon_sina_light));
					} else {
						flog = "shouquan";
						weibo.authorize();
					}
				} else {
					sinaFlog = false;
					sinaImage.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_sina));
					sinaFlag = false;
					weibo.setPlatformActionListener(ShareEventActivity.this);
					// weibo.removeAccount();
					// Toast.makeText(getApplicationContext(), "取消授权！",
					// Toast.LENGTH_SHORT).show();
				}
			}
		});

		tencentLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tencentFlag == false) {
					tencentFlag = true;
					tencentweibo
							.setPlatformActionListener(ShareEventActivity.this);

					if (tencentweibo.isValid()) {
						tencentFlog = true;
						tencentImage.setImageDrawable(getResources()
								.getDrawable(R.drawable.icon_tencent_light));
					} else {
						flog = "shouquan";
						tencentweibo.authorize();
					}
				} else {
					tencentFlog = false;
					tencentImage.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_tencent));
					tencentFlag = false;
					tencentweibo
							.setPlatformActionListener(ShareEventActivity.this);
					// tencentweibo.removeAccount();
					// Toast.makeText(getApplicationContext(), "取消授权！",
					// Toast.LENGTH_SHORT).show();
				}
			}
		});

		renrenLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (renrenFlag == false) {
					renrenFlag = true;
					renrenwang
							.setPlatformActionListener(ShareEventActivity.this);

					if (renrenwang.isValid()) {
						renrenFlog = true;
						renrenImage.setImageDrawable(getResources()
								.getDrawable(R.drawable.icon_renren_light));
					} else {
						flog = "shouquan";
						renrenwang.authorize();
					}
				} else {
					renrenFlog = false;
					renrenImage.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_renren));
					renrenFlag = false;
					renrenwang
							.setPlatformActionListener(ShareEventActivity.this);
					// renrenwang.removeAccount();
					// Toast.makeText(getApplicationContext(), "取消授权！",
					// Toast.LENGTH_SHORT).show();
				}
			}
		});

		doubanLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (doubanFlag == false) {

					doubanFlag = true;
					if (doubanwang.isValid()) {
						doubanFlog = true;
						doubanImage.setImageDrawable(getResources()
								.getDrawable(R.drawable.icon_douban_light));
					} else {
						flog = "shouquan";
						doubanwang.authorize();
					}
				} else {
					doubanFlog = false;
					doubanImage.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_douban));
					doubanFlag = false;
					doubanwang
							.setPlatformActionListener(ShareEventActivity.this);
					// doubanwang.removeAccount();
					// Toast.makeText(getApplicationContext(), "取消授权！",
					// Toast.LENGTH_SHORT).show();
				}
			}
		});

		sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 发送信息到指定号码
				Uri uri = Uri.parse("smsto:");
				Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
				smsIntent.putExtra("sms_body", url);
				startActivity(smsIntent);
			}
		});

		email.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 创建Intent
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				// 设置内容类型
				emailIntent.setType("message/rfc822");
				// 设置收件人邮箱
				emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {});
				// 设置邮件默认标题
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, name);
				// 设置要默认发送的内容
				emailIntent.putExtra(Intent.EXTRA_TEXT, url);

				startActivity(Intent.createChooser(emailIntent, "请选择邮件发送软件"));
			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (sinaFlog || tencentFlog || renrenFlog || doubanFlog) {
					dialog = ProgressDialog.show(context, "请稍后...");
					dialog.show();
					SharePrepare();
				} else {
					showToast("请选择分享平台！");
				}

			}
		});
	}

	/**
	 * 退出返回
	 * 
	 */
	private void exitDialod() {
		openActivity(PostEventActivity.class, true);
	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitDialod();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.arg1) {
		case 1:
			if (flog.equals("shouquan")) {
				if (sinaFlag) {
					sinaFlog = true;
					sinaImage.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_sina_light));
					showToast("授权成功！");
				}

				if (tencentFlag) {
					tencentFlog = true;
					tencentImage.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_tencent_light));
					showToast("授权成功！");
				}

				if (renrenFlag) {
					renrenFlog = true;
					renrenImage.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_renren_light));
					showToast("授权成功！");
				}

				if (doubanFlag) {
					doubanFlog = true;
					doubanImage.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_douban_light));
					showToast("授权成功！");
				}

			} else if (flog.equals("share")) {
				showToast("分享成功！");
				dialog.dismiss();
			}
			break;
		case 2:
			if (flog.equals("shouquan")) {
				showToast("授权取消！");
				sinaFlag = tencentFlag = renrenFlag = doubanFlag = false;
			} else if (flog.equals("share")) {
				showToast("分享取消！");
				dialog.dismiss();
			}
			break;
		case 3:
			if (flog.equals("shouquan")) {
				showToast("授权失败！");
			} else if (flog.equals("share")) {
				showToast("分享失败！");
				dialog.dismiss();
			}
			break;
		default:
			break;
		}

		return false;
	}

	/**
	 * 分享图片到指定平台
	 */

	private void SharePrepare() {
		flog = "share";
		if (sinaFlog == true) {
			ShareParams sinaweibo = new ShareParams();
			sinaweibo.setText(url);
			weibo.share(sinaweibo);
			weibo.setPlatformActionListener(this);
		}

		if (renrenFlog == true) {
			ShareParams renren = new ShareParams();
			renren.setText(url);
			renrenwang.share(renren);
			renrenwang.setPlatformActionListener(this);
		}

		if (tencentFlog == true) {
			ShareParams txweibo = new ShareParams();
			txweibo.setText(url);
			tencentweibo.share(txweibo);
			tencentweibo.setPlatformActionListener(this);
		}

		if (doubanFlog == true) {
			ShareParams douban = new ShareParams();
			douban.setText(url);
			doubanwang.share(douban);
			doubanwang.setPlatformActionListener(this);
		}
	}

	@Override
	public void onComplete(Platform plat, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onCancel(Platform plat, int action) {
		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onError(Platform plat, int action, Throwable t) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(context);
		super.onDestroy();
	}

}
