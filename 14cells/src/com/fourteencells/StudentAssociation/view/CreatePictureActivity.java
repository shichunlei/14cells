package com.fourteencells.StudentAssociation.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.douban.Douban;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.Tools;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * 照片创建：照片简介、选择活动、照片定位
 * 
 * @author 师春雷
 * 
 *         分享到社交网络上 @author liuzhengyu
 * 
 */
public class CreatePictureActivity extends BaseActivity implements
		PlatformActionListener, Callback {

	private static final String TAG = "CreatePictureActivity";

	Context context;

	/** 标头 */
	private TextView title;
	/** 返回 */
	private ImageView back;
	/** 创建 */
	private Button create;
	/** 图片 */
	private ImageView image;
	/** 图片描述 */
	private EditText description;

	private String str_dec = "";
	/** 位置 */
	private TextView address;
	/** 照片地理位置 */
	private String photo_address = "";
	/** 活动 */
	private TextView event;

	/** 活动名称 */
	private String event_name = "";
	/** 活动ID */
	private String event_id = "";
	/** 经度 */
	private String longitude = "";
	/** 纬度 */
	private String latitude = "";
	/** 照片文件地址路径 */
	private String path = "";

	/** 选择地理位置 */
	private LinearLayout ll_address;
	/** 选择活动 */
	private LinearLayout ll_event;

	/** 用户的token */
	private String token = null;

	private ProgressDialog loading;

	public LocationManager lManager;

	/** 新浪 */
	private RelativeLayout sina;
	/** 腾讯 */
	private RelativeLayout tencent;
	/** 人人 */
	private RelativeLayout renren;
	/** 豆瓣 */
	private RelativeLayout douban;

	/** 新浪图标 */
	private ImageView sinaImage;
	/** 人人图标 */
	private ImageView renrenImage;
	/** 腾讯图标 */
	private ImageView tencentImage;
	/** 豆瓣图标 */
	private ImageView doubanImage;

	/** 新浪，腾讯，人人，豆瓣图标点亮状态标记位 */
	private boolean sinaFlag, tencentFlag, renrenFlag, doubanFlag = false;

	/** 新浪，腾讯，人人，豆瓣授权状态标记位 */
	private boolean sinaFlog, tencentFlog, renrenFlog, doubanFlog = false;

	private Platform weibo, renrenwang, tencentweibo, doubanwang;

	private String flog = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_picture);

		init();

		setListener();
	}

	/**
	 * 分享图片到指定平台
	 */

	private void SharePrepare() {

		if (sinaFlog == true) {
			flog = "share";
			ShareParams sinaweibo = new ShareParams();
			sinaweibo.setImagePath(path);

			weibo.share(sinaweibo);
			weibo.setPlatformActionListener(this);
		}

		if (renrenFlog == true) {
			flog = "share";

			ShareParams renren = new ShareParams();
			renren.setImagePath(path);

			renrenwang.share(renren);
			renrenwang.setPlatformActionListener(this);
		}

		if (tencentFlog == true) {
			flog = "share";
			ShareParams txweibo = new ShareParams();
			txweibo.setImagePath(path);

			tencentweibo.share(txweibo);
			tencentweibo.setPlatformActionListener(this);
		}

		if (doubanFlog == true) {
			flog = "share";
			ShareParams douban = new ShareParams();
			douban.setImagePath(path);

			doubanwang.share(douban);
			doubanwang.setPlatformActionListener(this);
		}
	}

	private void init() {
		context = CreatePictureActivity.this;

		ShareSDK.initSDK(context);

		SPUtils.put(context, "token", "");
		SPUtils.put(context, "choose_event_name", "");
		SPUtils.put(context, "choose_event_id", "");
		SPUtils.put(context, "choose_location_lng", "");
		SPUtils.put(context, "choose_location_lat", "");
		SPUtils.put(context, "choose_location_address_name", "");
		SPUtils.put(context, "choose_image_path", "");
		SPUtils.put(context, "choose_dec", "");

		title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.share_photo);
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);

		create = (Button) findViewById(R.id.btn_create_picture);

		image = (ImageView) findViewById(R.id.img_create_picture);
		description = (EditText) findViewById(R.id.et_create_picture);
		address = (TextView) findViewById(R.id.tv_create_picture_address);
		event = (TextView) findViewById(R.id.tv_create_picture_event);

		ll_address = (LinearLayout) findViewById(R.id.ll_create_picture_address);
		ll_event = (LinearLayout) findViewById(R.id.ll_create_picture_event);

		weibo = ShareSDK.getPlatform(getApplicationContext(), SinaWeibo.NAME);
		renrenwang = ShareSDK.getPlatform(getApplicationContext(), Renren.NAME);
		tencentweibo = ShareSDK.getPlatform(getApplicationContext(),
				TencentWeibo.NAME);
		doubanwang = ShareSDK.getPlatform(getApplicationContext(), Douban.NAME);

		sina = (RelativeLayout) findViewById(R.id.shareLayout1);
		tencent = (RelativeLayout) findViewById(R.id.shareLayout3);
		renren = (RelativeLayout) findViewById(R.id.shareLayout2);
		douban = (RelativeLayout) findViewById(R.id.shareLayout4);

		sinaImage = (ImageView) findViewById(R.id.sinaImage);
		renrenImage = (ImageView) findViewById(R.id.renrenImage);
		tencentImage = (ImageView) findViewById(R.id.tencentImage);
		doubanImage = (ImageView) findViewById(R.id.doubanImage);

		lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		image.setImageBitmap(Tools.getLocalImage(path));

		if (photo_address.length() > 0) {
			address.setText(photo_address);
		}

		if (event_name.length() > 0) {
			event.setText(event_name);
		}

		if (str_dec.length() > 0) {
			description.setText(str_dec);
		}

	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitDialog();
			}
		});

		ll_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					showToast("请开启GPS导航...");
					// 返回开启GPS导航设置界面
					Intent intent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 0);
					return;
				} else {
					str_dec = description.getText().toString().trim();
					SPUtils.put(context, "choose_dec", str_dec);

					openActivity(LocationActivity.class, true);
				}
			}
		});

		ll_event.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				str_dec = description.getText().toString().trim();
				SPUtils.put(context, "choose_dec", str_dec);

				openActivity(ChooseEventActivity.class, true);
			}
		});

		sina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sinaFlag == false) {

					sinaFlag = true;
					weibo.setPlatformActionListener(CreatePictureActivity.this);
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
					weibo.setPlatformActionListener(CreatePictureActivity.this);
					// weibo.removeAccount();
					// showToast("取消授权！");
				}
			}
		});

		tencent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tencentFlag == false) {
					tencentFlag = true;
					tencentweibo
							.setPlatformActionListener(CreatePictureActivity.this);

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
							.setPlatformActionListener(CreatePictureActivity.this);
					// tencentweibo.removeAccount();
					// showToast("取消授权！");
				}
			}
		});

		renren.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (renrenFlag == false) {
					renrenFlag = true;
					renrenwang
							.setPlatformActionListener(CreatePictureActivity.this);

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
							.setPlatformActionListener(CreatePictureActivity.this);
					// renrenwang.removeAccount();
					// showToast("取消授权！");
				}
			}
		});

		douban.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (doubanFlag == false) {

					doubanFlag = true;
					doubanwang
							.setPlatformActionListener(CreatePictureActivity.this);
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
							.setPlatformActionListener(CreatePictureActivity.this);
					// doubanwang.removeAccount();
					// showToast("取消授权！");
				}
			}
		});

		create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				photo_address = address.getText().toString().trim();
				str_dec = description.getText().toString().trim();

				loading = ProgressDialog.show(context, "请稍后...");
				loading.show();
				CreatePhoto(str_dec, photo_address, event_id, token, latitude,
						longitude, path);
			}
		});
	}

	/**
	 * 创建照片
	 * 
	 * @param longitude
	 * @param latitude
	 * @param token
	 * @param event_id
	 * @param position
	 * @param description
	 * @param photo
	 * 
	 */
	private void CreatePhoto(String description, String position,
			String event_id, String token, String latitude, String longitude,
			String photo) {

		AjaxParams params = new AjaxParams();
		params.put("position", position);// 照片地点
		params.put("description", description);// 照片说明
		params.put("event_id", event_id);// 所属活动ID
		params.put("latitude", latitude);//
		params.put("longitude", longitude);//
		params.put("auth_token", token);// 用户的token

		try {
			params.put("photo", new File(photo));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} // 上传文件

		if (params != null) {
			Log.i(TAG, "" + params.toString());
		}
		FinalHttp fh = new FinalHttp();
		fh.post(HttpUtils.ROOT_URL + HttpUtils.CREATE_PHOTO, params,
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
							// showToast("上传成功！");
							SharePrepare();

							finish();
						} else if (result.getResultcode() == 101) {
							showToast("上传失败，请稍候再试！");
						}
						loading.dismiss();

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

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 确定返回上一级对话框
	 * 
	 */
	private void exitDialog() {
		new AlertDialog.Builder(context)
				.setTitle("温馨提示")
				.setMessage("放弃创建照片？")
				.setPositiveButton(R.string.determine,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setNegativeButton(R.string.cancel, null).create()
				.show();
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
			}
			break;
		case 2:
			if (flog.equals("shouquan")) {
				showToast("授权取消！");
				sinaFlag = tencentFlag = renrenFlag = doubanFlag = false;
			} else if (flog.equals("share")) {
				showToast("分享取消！");
				finish();
				loading.dismiss();
			}
			break;
		case 3:
			if (flog.equals("shouquan")) {
				showToast("授权失败！");
			} else if (flog.equals("share")) {
				showToast("分享失败！");
				finish();
				loading.dismiss();
			}
			break;
		default:
			break;
		}

		return false;
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

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}
