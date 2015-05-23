package com.fourteencells.StudentAssociation.view;

import java.io.File;
import java.util.HashMap;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.HttpHandler;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.MyCircleImageView;
import com.fourteencells.StudentAssociation.customview.ProgressDialog;
import com.fourteencells.StudentAssociation.model.PictureDetail;
import com.fourteencells.StudentAssociation.model.Picture;
import com.fourteencells.StudentAssociation.model.Result;
import com.fourteencells.StudentAssociation.model.User;
import com.fourteencells.StudentAssociation.utils.AppConfig;
import com.fourteencells.StudentAssociation.utils.FileUtils;
import com.fourteencells.StudentAssociation.utils.HttpUtils;
import com.fourteencells.StudentAssociation.utils.JsonUtils;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.utils.TimeUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * 直播图片详情页面
 * 
 * @author 师春雷
 * 
 */

public class PictureDetailsActivity extends BaseActivity implements
		PlatformActionListener, Callback {

	private static final String TAG = "PictureDetailsActivity";

	/** 照片发起者名字 */
	private TextView initiator;
	/** 活动时间 */
	private TextView time;
	/** 距离发布照片时间 */
	private String strTime;
	/** 发布者头像 */
	private MyCircleImageView headpic;
	/** 照片 */
	private ImageView picture;
	/** 活动名称 */
	private TextView eventName;
	/**  */
	private LinearLayout ll_eventname;
	/** 社团名称 */
	private TextView clubName;
	/**  */
	private LinearLayout ll_clubname;
	/** 添加照片时的地址 */
	private TextView picAddress;
	/**  */
	private LinearLayout ll_pic_address;
	/** 照片简介 */
	private TextView description;

	/** 返回 */
	private LinearLayout back;
	/** 分享 */
	private LinearLayout share;
	/** 评论 */
	private RelativeLayout comment;
	/** 评论的数量 */
	private TextView commentNum;
	/** 赞 */
	private RelativeLayout praise;
	/** 赞的数量 */
	private TextView praiseNum;
	/** 赞的图片 */
	private ImageView imageLike;
	/** 赞的数量 */
	private int praise_num = 0;
	/** 是否赞过 */
	private Boolean is_like;

	Picture photo = new Picture();
	User user = new User();
	PictureDetail picture_ = new PictureDetail();

	private ProgressDialog loading;

	/** 图片ID */
	private String id;
	/** 用户的token */
	private String token = null;

	private HttpHandler<File> handler;

	/** 下载的图片的储存路径 */
	private String coverName = null;

	private String SDCard = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	private File download, savePicture;

	private WindowManager wm;

	/** 屏幕宽度 */
	private int ScreenWidth;

	private LayoutParams params;

	private boolean flag2 = false;

	private boolean flag12 = false;

	private OnekeyShare oks;

	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_details);

		ShareSDK.initSDK(PictureDetailsActivity.this);

		id = getStringExtra("picture_id");

		findViews();

		loading = ProgressDialog.show(PictureDetailsActivity.this, "请稍后...");
		loading.show();
		getPictureDetails(token, id);

		setListener();
	}

	@SuppressWarnings("deprecation")
	private void findViews() {
		token = (String) SPUtils.get(PictureDetailsActivity.this, "token", "");

		headpic = (MyCircleImageView) findViewById(R.id.img_picture_details_headpic);
		picture = (ImageView) findViewById(R.id.img_picture_details_picture);
		description = (TextView) findViewById(R.id.tv_picture_details_comment);
		picAddress = (TextView) findViewById(R.id.tv_picture_details_address);
		ll_pic_address = (LinearLayout) findViewById(R.id.ll_picture_details_address);
		initiator = (TextView) findViewById(R.id.tv_picture_details_initiator);
		time = (TextView) findViewById(R.id.tv_picture_details_time);
		eventName = (TextView) findViewById(R.id.tv_picture_details_eventname);
		ll_eventname = (LinearLayout) findViewById(R.id.ll_picture_details_eventname);
		clubName = (TextView) findViewById(R.id.tv_picture_details_clubname);
		ll_clubname = (LinearLayout) findViewById(R.id.ll_picture_details_clubname);
		comment = (RelativeLayout) findViewById(R.id.rl_picture_details_comment);
		commentNum = (TextView) findViewById(R.id.tv_picture_details_comment_num);
		praise = (RelativeLayout) findViewById(R.id.rl_picture_details_praise);
		imageLike = (ImageView) findViewById(R.id.iv_picture_details_like);
		praiseNum = (TextView) findViewById(R.id.tv_event_details_praise_num);
		back = (LinearLayout) findViewById(R.id.ll_picture_details_back);
		share = (LinearLayout) findViewById(R.id.ll_picture_details_share);

		if (download == null) {
			download = FileUtils.createFolders(AppConfig.DOWNLOAD_FOLDER_NAME);
		}
		if (savePicture == null) {
			savePicture = FileUtils.createFolders(AppConfig.SAVE_FOLDER_NAME);
		}

		// 图片保存到本地的路径
		coverName = SDCard + AppConfig.DOWNLOAD_FOLDER_NAME
				+ "temporary_image.jpg";

		wm = this.getWindowManager();

		ScreenWidth = wm.getDefaultDisplay().getWidth();

		params = picture.getLayoutParams();
	}

	private void setListener() {

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PictureDetailsActivity.this.finish();
			}
		});

		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				File file = new File(coverName);
				if (file != null) {
					file.delete();
				}

				if (picture_.getPhoto().getUrl() != null) {
					savPicture();
				}

				String name = (String) SPUtils.get(PictureDetailsActivity.this,
						"name", "");

				oks = new OnekeyShare();
				oks.setNotification(R.drawable.icon, "正在分享...");
				oks.setTitle(name + "分享的图片");
				oks.setText(name + "分享的图片，时间戳：" + TimeUtils.nowTime());
				oks.setTitleUrl("http://www.14cells.com/");
				oks.setComment("赞");
				oks.setImageUrl(picture_.getPhoto().getUrl());
				oks.setImagePath(coverName);
				new BitmapFactory();
				oks.setCustomerLogo(BitmapFactory.decodeResource(
						getResources(), R.drawable.save), "保存",
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								oks.finish();
								mySavePicture();
							}
						});
				oks.show(PictureDetailsActivity.this);
			}
		});

		comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openActivity(CommentActivity.class, "picture_id",
						picture_.getId(), true);
			}
		});

		praise.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				praise.setEnabled(false);
				String photo_id = picture_.getId();
				if (is_like) {
					clickDisLike(token, photo_id);
				} else {
					clickLike(token, photo_id);
				}
			}
		});
	}

	private void mySavePicture() {
		String fileParth = SDCard + AppConfig.SAVE_FOLDER_NAME
				+ System.currentTimeMillis() + ".jpg";
		FinalHttp fh = new FinalHttp();
		HttpHandler<File> picture = fh.download(picture_.getPhoto().getUrl(),
				fileParth, new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(File t) {
						if (t != null) {
							showToast("保存成功！");
							flag12 = true;
						}
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							Log.i(TAG, t.toString());
						}
						if (strMsg != null) {
							Log.i(TAG, strMsg);
						}
					}
				});
		if (flag12 == true) {
			picture.stop();
		}

	}

	private void savPicture() {
		// 保存到本地
		FinalHttp fh = new FinalHttp();
		handler = fh.download(picture_.getPhoto().getUrl(), coverName, true,
				new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(File t) {
						if (t != null) {
							flag2 = true;
						}
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							Log.i(TAG, t.toString());
						}
						if (strMsg != null) {
							Log.i(TAG, strMsg);
						}
					}
				});
		if (flag2 == true) {
			handler.stop();
		}
	}

	/**
	 * 点赞
	 * 
	 * @param token
	 * @param photo_id
	 */
	protected void clickLike(String token, String photo_id) {
		AjaxParams params = new AjaxParams();
		params.put("photo_id", photo_id);// 图片ID
		params.put("auth_token", token);// 用户的token

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.post(HttpUtils.ROOT_URL + HttpUtils.LIKE, params,
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
							showToast(R.string.islike);
							praise_num++;
							praiseNum.setText(praise_num + "");
							imageLike.setImageResource(R.drawable.is_like);
							is_like = true;
							praise.setEnabled(true);
						} else if (result.getResultcode() == 101) {
							showToast("点赞失败！");
						}
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							showToast("点赞失败！");
						}
					}
				});
	}

	/**
	 * 取消赞
	 * 
	 * @param token
	 * @param photo_id
	 */
	protected void clickDisLike(String token, String photo_id) {
		AjaxParams params = new AjaxParams();
		params.put("photo_id", photo_id);// 图片ID
		params.put("auth_token", token);// 用户的token

		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		fh.post(HttpUtils.ROOT_URL + HttpUtils.DISLIKE, params,
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
							showToast(R.string.dislike);
							praise_num--;
							praiseNum.setText(praise_num + "");
							imageLike.setImageResource(R.drawable.no_like);
							is_like = false;
							praise.setEnabled(true);
						} else if (result.getResultcode() == 101) {
							showToast("操作失败！");
						}
					}

					@Override
					public void onFailure(Throwable t,int errorCode, String strMsg) {
						super.onFailure(t, errorCode, strMsg);
						if (t != null) {
							showToast("操作失败！");
						}
					}
				});
	}

	/**
	 * 得到照片详情
	 * 
	 * @param token
	 * @param id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getPictureDetails(String token, String id) {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(HttpUtils.TIME_OUT);
		String URL = HttpUtils.ROOT_URL + HttpUtils.GET_ALBUMS_DETAILS + id
				+ "?auth_token=" + token;

		fh.get(URL, new AjaxCallBack() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);

				String str = t.toString();

				photo = JsonUtils.ParserPictureDetails("[" + str + "]");

				if (photo.getIslike().equals("0")) {
					is_like = false;
					imageLike.setImageResource(R.drawable.no_like);
				} else if (photo.getIslike().equals("1")) {
					is_like = true;
					imageLike.setImageResource(R.drawable.is_like);
				}

				user = (User) JsonUtils.fromJson(photo.getPublisher_info(),
						User.class);
				picture_ = (PictureDetail) JsonUtils.fromJson(
						photo.getPhoto_info(), PictureDetail.class);

				if (null != photo.getEvent_name()
						&& photo.getEvent_name().length() > 0) {
					ll_eventname.setVisibility(View.VISIBLE);
					eventName.setText(photo.getEvent_name());
				} else {
					ll_eventname.setVisibility(View.GONE);
				}

				if (null != photo.getClub_name()
						&& photo.getClub_name().length() > 0) {
					ll_clubname.setVisibility(View.VISIBLE);
					clubName.setText(photo.getClub_name());
				} else {
					ll_clubname.setVisibility(View.GONE);
				}

				initiator.setText(user.getName());
				if (user.getHeadpic() != null) {
					FinalBitmap fb = FinalBitmap
							.create(getApplicationContext());
					fb.configLoadingImage(R.drawable.default_image);
					fb.display(headpic, user.getHeadpic().getUrl());
				}

				try {
					FinalBitmap fb = FinalBitmap
							.create(getApplicationContext());
					fb.configLoadingImage(R.drawable.img_default);
					fb.display(picture, picture_.getPhoto().getUrl());

					params.width = ScreenWidth;
					params.height = ScreenWidth;
					picture.setLayoutParams(params);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}

				if (null != picture_.getPosition()
						&& picture_.getPosition().length() > 0) {
					ll_pic_address.setVisibility(View.VISIBLE);
					picAddress.setText(picture_.getPosition());
				} else {
					ll_pic_address.setVisibility(View.GONE);
				}

				if (null != picture_.getDescription()
						&& picture_.getDescription().length() > 0) {
					description.setText(picture_.getDescription());
					description.setVisibility(View.VISIBLE);
				} else {
					description.setVisibility(View.GONE);
				}

				strTime = picture_.getCreated_at();
				time.setText(TimeUtils.GetTimeDifference(strTime));

				commentNum.setText(picture_.getComment_count() + "");

				praise_num = picture_.getLike_count();
				praiseNum.setText(picture_.getLike_count() + "");

				loading.dismiss();
			}

			@Override
			public void onFailure(Throwable t,int errorCode, String strMsg) {
				super.onFailure(t, errorCode, strMsg);
				if (t != null) {
					loading.dismiss();
					showToast("请求数据失败，请稍后再试");
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void showNotification(long cancelTime, String text) {
		try {
			Context app = getApplicationContext();
			NotificationManager nm = (NotificationManager) app
					.getSystemService(Context.NOTIFICATION_SERVICE);

			final int id = Integer.MAX_VALUE / 13 + 1;
			nm.cancel(id);
			long when = System.currentTimeMillis();
			Notification notification = new Notification(R.drawable.icon, text,
					when);
			PendingIntent pi = PendingIntent.getActivity(app, 0, new Intent(),
					0);
			notification.setLatestEventInfo(app, "sharesdk test", text, pi);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(id, notification);

			if (cancelTime > 0) {
				Message msg = new Message();
				msg.what = MSG_CANCEL_NOTIFY;
				msg.obj = nm;
				msg.arg1 = id;
				UIHandler.sendMessageDelayed(msg, cancelTime, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 取消分享的回调方法 */
	@Override
	public void onCancel(Platform platform, int action) {
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = platform;

		UIHandler.sendMessage(msg, this);
	}

	/** 完成分享的回调方法 */
	@Override
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> arg2) {
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = platform;

		UIHandler.sendMessage(msg, this);
	}

	/** 分享出错的回调方法 */
	@Override
	public void onError(Platform platform, int action, Throwable t) {
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = t;

		UIHandler.sendMessage(msg, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(PictureDetailsActivity.this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_TOAST: {
			String text = String.valueOf(msg.obj);
			showToast(text);
		}
			break;
		case MSG_ACTION_CCALLBACK: {
			switch (msg.arg1) {
			case 1: // 成功后发送Notification
				showNotification(2000, "分享完成");
				break;
			case 2: // 失败后发送Notification
				showNotification(2000, "分享失败");
				break;
			case 3: // 取消
				showNotification(2000, "取消分享");
				break;
			}
		}
			break;
		case MSG_CANCEL_NOTIFY:
			NotificationManager nm = (NotificationManager) msg.obj;
			if (nm != null) {
				nm.cancel(msg.arg1);
			}
			break;
		}
		return false;
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}