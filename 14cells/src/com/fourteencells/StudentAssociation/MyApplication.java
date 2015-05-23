package com.fourteencells.StudentAssociation;

import android.app.Application;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

import android.os.Build;
import android.os.StrictMode;

public class MyApplication extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();
	private static MyApplication instance;

	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	@SuppressWarnings("unused")
	public void onCreate() {
		if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyDeath().build());
		}

		super.onCreate();

	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

}
