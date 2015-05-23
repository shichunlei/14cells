package com.fourteencells.StudentAssociation.utils;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.fourteencells.StudentAssociation.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * JSON数据解析辅助类
 * 
 * @author 师春雷
 * 
 */
public class JsonUtils {

	private static final String TAG = "JsonParser";

	private static Gson gson;

	private static String results;

	/**
	 * 得到当前位置附近热点的列表
	 * 
	 * @param str
	 * @return
	 */
	public static List<Place> ParserPlaceList(String str) {
		JSONObject jobg;
		Gson gson = new Gson();
		try {
			jobg = new JSONObject(str);

			JSONObject result = jobg.getJSONObject("result");
			List<Place> place = gson.fromJson(result.getString("pois"),
					new TypeToken<List<Place>>() {
					}.getType());
			return place;
		} catch (Exception e) {
			Log.e(TAG, "ParserPlaceList error");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到搜索的热点结果
	 * 
	 * @param result
	 * @return
	 */
	public static String ParserPlaceInfo(String result) {
		try {
			JSONArray obj = new JSONArray(result);
			JSONObject o = obj.getJSONObject(0);

			results = o.getString("results");
			return results;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "ParserPlaceInfo error");
			return null;
		}
	}

	/***
	 * 得到纠偏后的经纬度
	 * 
	 * @param string
	 * @return
	 */
	public static Point ParserCorrectCoordinates(String string) {
		Point point = new Point();

		// try {
		// JSONArray obj = new JSONArray(string);
		// JSONObject o = obj.getJSONObject(0);
		//
		// JSONArray obj1 = new JSONArray(o.getString("result"));
		//
		// JSONObject jsonObject = obj1.getJSONObject(0);
		// point.x = jsonObject.getString("x");
		// point.y = jsonObject.getString("y");
		//
		// return point;
		// } catch (JSONException e) {
		// e.printStackTrace();
		// Log.e(TAG, "ParserCorrectCoordinates error");
		// return null;
		// }

		try {

			JSONObject o = new JSONObject(string);

			JSONArray obj1 = new JSONArray(o.getString("result"));

			JSONObject jsonObject = obj1.getJSONObject(0);
			point.x = jsonObject.getString("x");
			point.y = jsonObject.getString("y");

			return point;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "ParserCorrectCoordinates error");
			return null;
		}
	}

	/**
	 * 得到照片详情
	 * 
	 * @param string
	 * @return
	 */
	public static Picture ParserPictureDetails(String string) {
		try {
			JSONArray obj = new JSONArray(string);
			JSONObject o = obj.getJSONObject(0);
			Picture photo = new Picture();
			photo.photo_info = o.getString("photo_info");
			photo.comments = o.getString("comments");
			photo.publisher_info = o.getString("publisher_info");
			photo.event_name = o.getString("event_name");
			photo.islike = o.getString("islike");
			photo.likes = o.getString("likes");
			photo.club_name = o.getString("club_name");

			return photo;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "ParserPictureDetails error");
			return null;
		}
	}

	/**
	 * 
	 * 描述：将对象转化为json.
	 * 
	 * @param list
	 * @return
	 */
	public static String toJson(Object src) {
		String json = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			gson = gsonb.create();
			json = gson.toJson(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 
	 * 描述：将列表转化为json.
	 * 
	 * @param list
	 * @return
	 */
	public static String toJson(List<?> list) {
		String json = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			gson = gsonb.create();
			json = gson.toJson(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 
	 * 描述：将json转化为列表.
	 * 
	 * @param json
	 * @param typeToken
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<?> fromJson(String json, TypeToken typeToken) {
		List<?> list = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			gson = gsonb.create();
			Type type = typeToken.getType();
			list = gson.fromJson(json, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 
	 * 描述：将json转化为对象.
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static Object fromJson(String json, Class<?> clazz) {
		Object obj = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			gson = gsonb.create();
			obj = gson.fromJson(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

}
