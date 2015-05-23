package com.fourteencells.StudentAssociation.utils;

import com.fourteencells.StudentAssociation.view.MainActivity;

import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 判断网络工具类
 * 
 * @author 师春雷
 * 
 */
public class NetUtils {

	private static String TAG = "NetWorkConect";

	/**
	 * 网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetUseful() {
		if (NetUtils.isNetworkEnabled(MainActivity.context) == true
				&& NetUtils.isNetworkConnected(MainActivity.context) == true) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断Network是否开启(包括移动网络和wifi)
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isNetworkEnabled(Context mContext) {
		return (isNetEnabled(mContext) || isWIFIEnabled(mContext));
	}

	/**
	 * 判断Network是否连接成功(包括移动网络和wifi)
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isNetworkConnected(Context mContext) {
		return (isWifiContected(mContext) || isNetContected(mContext));
	}

	/**
	 * 判断移动网络是否开启
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetEnabled(Context context) {
		boolean enable = false;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null) {
			if (telephonyManager.getNetworkType() != TelephonyManager.NETWORK_TYPE_UNKNOWN) {
				enable = true;
				Log.i(TAG, "isNetEnabled");
			}
		}

		return enable;
	}

	/**
	 * 判断wifi是否开启
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWIFIEnabled(Context context) {
		boolean enable = false;
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			enable = true;
			Log.i(TAG, "isWifiEnabled");
		}

		Log.i(TAG, "isWifiDisabled");
		return enable;
	}

	/**
	 * 判断移动网络是否连接成功！
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetContected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetworkInfo == null)
			return false;
		if (mobileNetworkInfo.isConnected()) {

			Log.i(TAG, "isNetContected");
			return true;
		}
		Log.i(TAG, "isNetDisconnected");
		return false;

	}

	/**
	 * 判断wifi是否连接成功
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiContected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			Log.i(TAG, "isWifiContected");
			return true;
		}
		Log.i(TAG, "isWifiDisconnected");
		return false;

	}

	/**
	 * 打开设置网络界面
	 */
	public static void setNetworkMethod(final Context context) {
		// 提示对话框
		new Builder(context)
				.setTitle("网络设置提示")
				.setMessage("网络连接不可用,是否进行设置?")
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = null;
						// 判断手机系统的版本 即API大于10 就是3.0或以上版本
						if (android.os.Build.VERSION.SDK_INT > 10) {
							intent = new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						} else {
							intent = new Intent();
							ComponentName component = new ComponentName(
									"com.android.settings",
									"com.android.settings.WirelessSettings");
							intent.setComponent(component);
							intent.setAction("android.intent.action.VIEW");
						}
						context.startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						android.os.Process.killProcess(android.os.Process
								.myPid());
						dialog.dismiss();
					}
				}).show();

	}

	// Gps是否可用
	public static boolean isGpsEnable(Context context) {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

}
