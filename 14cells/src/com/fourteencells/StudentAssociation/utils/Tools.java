package com.fourteencells.StudentAssociation.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 工具类，一些全局公用的工具类
 * 
 * @author 师春雷
 * 
 */
public class Tools {

	/**
	 * dp转化成px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * px转换成dp
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int Px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/**
	 * px转sp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int Px2Sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * sp转px
	 * 
	 * @param context
	 * @param spValue
	 * @return
	 */
	public static int Sp2Px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 邮箱格式验证
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 去除字符串中的空格与换行符
	 * 
	 * @param string
	 * @return
	 */
	public static String replaceBlank(String string) {
		String dest = "";
		if (string != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(string);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 将本地图片路径转换为Bitmap形式展示到ImageView内
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getLocalImage(String path) {
		Bitmap bitmap = null;

		BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 1;
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(path, options);// 获取尺寸信息

		// 获取比例大小
		int wRatio = (int) Math.ceil(options.outWidth / 500);
		int hRatio = (int) Math.ceil(options.outHeight / 500);
		// 如果超出指定大小，则缩小相应的比例
		if (wRatio > 1 && hRatio > 1) {
			if (wRatio > hRatio) {
				options.inSampleSize = wRatio;
			} else {
				options.inSampleSize = hRatio;
			}
		}
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(path, options);// 获取尺寸信息
		return bitmap;

	}
}
