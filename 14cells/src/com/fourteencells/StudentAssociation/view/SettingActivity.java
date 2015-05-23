package com.fourteencells.StudentAssociation.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.onekeyshare.ShareCore;

import com.fourteencells.StudentAssociation.MyApplication;
import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.utils.*;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * @date 2014-04-14
 * 
 * @author 师春雷
 * 
 *         授权 @author 刘振宇
 * 
 *         注销 @author 刘振宇
 * 
 */
public class SettingActivity extends BaseActivity implements
		PlatformActionListener, Callback {

	private final static String TAG = "";

	private ImageView back;
	private TextView titleText;

	private RelativeLayout rlPassword;
	private RelativeLayout rlUsername;

	/** 需要授权的账户列表 */
	private ListView authorizationList;

	private AuthAdapter adapter;

	/** 账号切换 */
	private Button mButton;

	/** 窗口管理 */
	private WindowManager wm;

	/** 屏幕宽度 */
	private int ScreenWidth;
	/** 屏幕高度 */
	private int ScreenHeigth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ShareSDK.initSDK(this);

		init();

		setListener();

		authorizationList.setAdapter(adapter);

		authorizationList.setOnItemClickListener(adapter);
	}

	/**
	 * 初始化方法
	 */
	@SuppressWarnings("deprecation")
	private void init() {
		back = (ImageView) findViewById(R.id.title_back);
		back.setImageResource(R.drawable.back);
		titleText = (TextView) findViewById(R.id.title_text);
		titleText.setText(R.string.setting);

		rlPassword = (RelativeLayout) findViewById(R.id.rl_password);
		rlUsername = (RelativeLayout) findViewById(R.id.rl_username);

		mButton = (Button) findViewById(R.id.Handover_Button);

		authorizationList = (ListView) findViewById(R.id.authorizationList);

		adapter = new AuthAdapter(this);

		wm = getWindowManager();

		ScreenWidth = wm.getDefaultDisplay().getWidth();
		ScreenHeigth = wm.getDefaultDisplay().getHeight();

		Log.i(TAG, "屏幕宽度" + ScreenWidth + "，屏幕高度" + ScreenHeigth);

		mButton.setHeight((int) (ScreenWidth * 0.08));
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 删除本地文件
				FileUtils.DeleteFile(AppConfig.EVENT_FILE_NAME);
				FileUtils.DeleteFile(AppConfig.CLUB_FILE_NAME);

				SPUtils.remove(SettingActivity.this, "token");
				SPUtils.remove(SettingActivity.this, "name");
				SPUtils.remove(SettingActivity.this, "sex");
				SPUtils.remove(SettingActivity.this, "school");
				SPUtils.remove(SettingActivity.this, "college");
				SPUtils.remove(SettingActivity.this, "schoolnum");
				SPUtils.remove(SettingActivity.this, "schooltime");
				SPUtils.remove(SettingActivity.this, "intentpath");
				SPUtils.remove(SettingActivity.this, "state");
				SPUtils.remove(SettingActivity.this, "id");

				openActivity(LoginActivity.class, "tag", "", false);
				MyApplication.getInstance().exit();
			}
		});

		rlPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openActivity(ChangePasswordActivity.class, false);
			}
		});

		rlUsername.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String user_id = (String) SPUtils.get(SettingActivity.this,
						"id", "");

				openActivity(PersonalInfoActivity.class, "user_id", user_id,
						false);
			}
		});
	}

	@Override
	public void onCancel(Platform plat, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
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
	public void onError(Platform plat, int action, Throwable t) {
		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	/**
	 * 处理操作结果
	 * <p>
	 * 如果获取到用户的名称，则显示名称；否则如果已经授权，则显示 平台名称
	 */
	public boolean handleMessage(Message msg) {
		switch (msg.arg1) {
		case 1: {
			showToast("授权成功！");
		}
			break;
		case 2: {
			showToast("授权失败！");
			return false;
		}
		case 3: {
			showToast("授权取消！");
			return false;
		}
		}

		adapter.notifyDataSetChanged();
		return false;
	}

	/** 将action转换为String */
	public static String actionToString(int action) {
		switch (action) {
		case Platform.ACTION_AUTHORIZING:
			return "ACTION_AUTHORIZING";
		case Platform.ACTION_GETTING_FRIEND_LIST:
			return "ACTION_GETTING_FRIEND_LIST";
		case Platform.ACTION_FOLLOWING_USER:
			return "ACTION_FOLLOWING_USER";
		case Platform.ACTION_SENDING_DIRECT_MESSAGE:
			return "ACTION_SENDING_DIRECT_MESSAGE";
		case Platform.ACTION_TIMELINE:
			return "ACTION_TIMELINE";
		case Platform.ACTION_USER_INFOR:
			return "ACTION_USER_INFOR";
		case Platform.ACTION_SHARE:
			return "ACTION_SHARE";
		default: {
			return "UNKNOWN";
		}
		}
	}

	// ////////////////////////////////////////内部类/////////////////////////////////////////////////////
	private static class AuthAdapter extends BaseAdapter implements
			OnItemClickListener {

		private SettingActivity set;
		private ArrayList<Platform> platforms;

		public AuthAdapter(SettingActivity set) {
			this.set = set;

			// 获取平台列表
			Platform[] tmp = ShareSDK.getPlatformList(set.getBaseContext());
			platforms = new ArrayList<Platform>();
			if (tmp == null) {
				return;
			}

			for (Platform p : tmp) {
				String name = p.getName();
				if ((p instanceof CustomPlatform)
						|| !ShareCore.canAuthorize(p.getContext(), name)) {
					continue;
				}
				platforms.add(p);
			}

		}

		@Override
		public int getCount() {
			return platforms == null ? 0 : platforms.size();
		}

		@Override
		public Platform getItem(int position) {
			return platforms.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(set.getBaseContext(),
						R.layout.item_auth, null);
			}

			int count = getCount();
			View llItem = convertView.findViewById(R.id.llItem);
			int dp_10 = cn.sharesdk.framework.utils.R.dipToPx(
					parent.getContext(), 5);
			if (count == 1) {
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, dp_10, dp_10, dp_10);
			} else if (position == 0) {
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, dp_10, dp_10, 0);
			} else if (position == count - 1) {
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, 0, dp_10, dp_10);
			} else {
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, 0, dp_10, 0);
			}

			Platform plat = getItem(position);
			ImageView ivLogo = (ImageView) convertView
					.findViewById(R.id.ivLogo);
			Bitmap logo = getIcon(plat);
			if (logo != null && !logo.isRecycled()) {
				ivLogo.setImageBitmap(logo);
			}
			CheckedTextView ctvName = (CheckedTextView) convertView
					.findViewById(R.id.ctvName);
			ctvName.setChecked(plat.isValid());
			if (plat.isValid()) {
				String userName = plat.getDb().get("nickname");
				if (userName == null || userName.length() <= 0
						|| "null".equals(userName)) {
					// 如果平台已经授权却没有拿到帐号名称，则自动获取用户资料，以获取名称
					userName = getName(plat);
					plat.setPlatformActionListener(set);
					plat.showUser(null);
				}
				ctvName.setText(userName);
			} else {
				ctvName.setText(R.string.not_yet_authorized);
			}
			return convertView;
		}

		private Bitmap getIcon(Platform plat) {
			if (plat == null) {
				return null;
			}

			String name = plat.getName();
			if (name == null) {
				return null;
			}

			String resName = "logo_" + plat.getName();
			int resId = cn.sharesdk.framework.utils.R.getResId(
					R.drawable.class, resName);
			return BitmapFactory.decodeResource(set.getResources(), resId);
		}

		private String getName(Platform plat) {
			if (plat == null) {
				return "";
			}

			String name = plat.getName();
			if (name == null) {
				return "";
			}

			int resId = cn.sharesdk.framework.utils.R.getStringRes(
					set.getBaseContext(), plat.getName());
			return set.getBaseContext().getString(resId);
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Platform plat = getItem(position);
			CheckedTextView ctvName = (CheckedTextView) view
					.findViewById(R.id.ctvName);
			if (plat == null) {
				ctvName.setChecked(false);
				ctvName.setText(R.string.not_yet_authorized);
				return;
			}

			if (plat.isValid()) {
				plat.removeAccount();
				ctvName.setChecked(false);
				ctvName.setText(R.string.not_yet_authorized);
				return;
			}

			plat.setPlatformActionListener(set);
			plat.showUser(null);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}