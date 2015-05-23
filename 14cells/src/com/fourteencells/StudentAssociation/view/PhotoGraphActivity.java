package com.fourteencells.StudentAssociation.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.common.utils.SDKUtils;
import com.aviary.android.feather.common.utils.StringUtils;
import com.aviary.android.feather.library.Constants;
import com.fourteencells.StudentAssociation.R;
import com.fourteencells.StudentAssociation.customview.Preview;
import com.fourteencells.StudentAssociation.utils.SPUtils;
import com.fourteencells.StudentAssociation.view.base.BaseActivity;

/**
 * 页面功能：拍照页面
 * 
 * 拍照
 * 
 * （本地相册）选择照片
 * 
 * 处理照片
 * 
 * 处理完的照片保存到本地文件夹内
 * 
 * 完成后跳转到分享照片页面
 * 
 * @author 师春雷
 * 
 */
public class PhotoGraphActivity extends BaseActivity {

	public static final String LOG_TAG = "PhotoGraphActivity";

	private Context context;

	private String mApiKey;

	// your aviary secret key
	private static final String API_SECRET = "ddb9b300695015be";

	/** 输出照片路径 */
	private String mOutputFilePath;

	/** 屏幕宽度 */
	private int imageWidth;

	/** session id for the hi-res post processing */
	private String mSessionId;

	private Camera camera;
	private Preview preview;

	private Uri imageUri;

	private FrameLayout layout;
	private String path = "/sdcard/Cells/image/";

	/** 拍照 */
	private ImageView from_camera;
	/** 选择照片 */
	private ImageView from_native;
	/** 返回 */
	private ImageView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photograph);
		context = PhotoGraphActivity.this;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		imageWidth = (int) ((float) metrics.widthPixels);

		init();

		setListener();

		new ApiKeyReader().execute();
	}

	private void setListener() {
		preview.setKeepScreenOn(true);
		from_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					takeFocusedPicture();
				} catch (Exception e) {
				}
			}
		});

		from_native.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				openAlbumIntent.setType("image/*");
				startActivityForResult(openAlbumIntent, 2);
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
								}).setNegativeButton(R.string.cancel, null)
						.create().show();
			}
		});
	}

	private void init() {
		from_camera = (ImageView) findViewById(R.id.from_camera);
		from_native = (ImageView) findViewById(R.id.from_native);

		back = (ImageView) findViewById(R.id.img_photograph_back);

		preview = new Preview(context,
				(SurfaceView) findViewById(R.id.KutCameraFragment));
		layout = (FrameLayout) findViewById(R.id.preview);

		layout.addView(preview);

		if (camera == null) {
			camera = Camera.open();
			camera.startPreview();
			camera.getParameters();
			camera.setErrorCallback(new ErrorCallback() {
				public void onError(int error, Camera mcamera) {
					camera.release();
					camera = Camera.open();
					Log.d("Camera died", "error camera");
				}
			});
		}

		if (camera != null) {
			if (Build.VERSION.SDK_INT >= 14)
				setCameraDisplayOrientation(this,
						CameraInfo.CAMERA_FACING_BACK, camera);
			preview.setCamera(camera);
		}

		File videoDirectory = new File(path);
		if (!videoDirectory.exists()) {
			videoDirectory.mkdirs();
		}

		// 获取系统时间 然后将裁剪后的图片保存至指定的文件夹
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		String address = sDateFormat.format(new java.util.Date());
		mOutputFilePath = path + address + ".jpg";
		imageUri = Uri.fromFile(new File(mOutputFilePath));
	}

	private void setCameraDisplayOrientation(Activity activity, int cameraId,
			android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			camera.takePicture(mShutterCallback, null, jpegCallback);
		}
	};

	Camera.ShutterCallback mShutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};

	public void takeFocusedPicture() {
		// 自动对焦
		camera.autoFocus(mAutoFocusCallback);
	}

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(LOG_TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {

			FileOutputStream outStream = null;

			Bitmap realImage;
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 5;

			options.inPurgeable = true; // Tell to gc that whether it needs free
										// memory, the Bitmap can be cleared

			options.inInputShareable = true; // Which kind of reference will be
												// used to recover the Bitmap
												// data after being clear, when
												// it will be used in the future

			realImage = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			int width = realImage.getWidth();
			int height = realImage.getHeight();
			Log.i(LOG_TAG, "image width is " + width + "image height is "
					+ height);
			int distance = Math.abs(width - height);
			if (width >= height) {
				realImage = Bitmap.createBitmap(realImage, distance, 0, height,
						height);
			} else {
				realImage = Bitmap.createBitmap(realImage, 0, distance, width,
						width);
			}

			ExifInterface exif = null;
			try {
				exif = new ExifInterface(mOutputFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Log.d("EXIF value",
					exif.getAttribute(ExifInterface.TAG_ORIENTATION));
			if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
					.equalsIgnoreCase("1")
					|| exif.getAttribute(ExifInterface.TAG_ORIENTATION)
							.equalsIgnoreCase("8")
					|| exif.getAttribute(ExifInterface.TAG_ORIENTATION)
							.equalsIgnoreCase("3")
					|| exif.getAttribute(ExifInterface.TAG_ORIENTATION)
							.equalsIgnoreCase("0")) {
				realImage = rotate(realImage);
			}

			try {
				// Write to SD Card
				outStream = new FileOutputStream(mOutputFilePath);
				realImage.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			startFeather(imageUri);
		}
	};

	public static Bitmap rotate(Bitmap source) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case 2:
			startPhotoZoom(data.getData());
			break;
		case 3:
			startFeather(imageUri);
			break;
		case 4:
			SPUtils.put(context, "choose_image_path", "");
			SPUtils.put(context, "choose_location_lng", "0.0");
			SPUtils.put(context, "choose_location_lat", "0.0");
			SPUtils.put(context, "choose_event_id", "");
			SPUtils.put(context, "choose_event_name", "");
			SPUtils.put(context, "choose_location_address_name", "");
			SPUtils.put(context, "choose_dec", "");

			openActivity(CreatePictureActivity.class, "path", mOutputFilePath,
					true);
			break;
		}
	}

	/**
	 * Load the incoming Image
	 * 
	 * @param uri
	 */
	private void startPhotoZoom(final Uri uri) {
		final Intent intent = new Intent("com.android.camera.action.CROP");

		// 照片URI地址
		intent.setDataAndType(uri, "image/*");

		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", imageWidth);
		intent.putExtra("outputY", imageWidth);
		// 输出路径
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		// 输出格式
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

		// 不启用人脸识别
		intent.putExtra("noFaceDetection", false);
		intent.putExtra("return-data", false);
		startActivityForResult(intent, 3);
	}

	/**
	 * 一旦你选择一个图像就可以启动feather activity
	 * 
	 * @param uri
	 */
	private void startFeather(Uri uri) {
		Intent newIntent = new Intent(this, FeatherActivity.class);
		newIntent.setData(uri);
		newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, API_SECRET);
		newIntent.putExtra(Constants.EXTRA_OUTPUT,
				Uri.parse("file://" + mOutputFilePath));
		newIntent.putExtra(Constants.EXTRA_OUTPUT_FORMAT,
				Bitmap.CompressFormat.JPEG.name());
		newIntent.putExtra(Constants.EXTRA_OUTPUT_QUALITY, 90);
		newIntent.putExtra(Constants.EXTRA_WHITELABEL, true);
		final DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int max_size = Math.max(metrics.widthPixels, metrics.heightPixels);
		max_size = (int) ((float) max_size / 1.2f);
		newIntent.putExtra(Constants.EXTRA_MAX_IMAGE_SIZE, max_size);
		mSessionId = StringUtils
				.getSha256(System.currentTimeMillis() + mApiKey);
		Log.d(LOG_TAG,
				"session: " + mSessionId + ", size: " + mSessionId.length());
		newIntent.putExtra(Constants.EXTRA_OUTPUT_HIRES_SESSION_ID, mSessionId);
		newIntent.putExtra(Constants.EXTRA_IN_SAVE_ON_NO_CHANGES, true);
		startActivityForResult(newIntent, 4);
	}

	private void setApiKey(String value) {
		Log.i(LOG_TAG, "api-key: " + value);
		mApiKey = value;

		if (null == value) {
			String message = SDKUtils.MISSING_APIKEY_MESSAGE;
			new AlertDialog.Builder(this).setTitle("API-KEY Missing!")
					.setMessage(message).show();
		}
	}

	class ApiKeyReader extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			return SDKUtils.getApiKey(getBaseContext());
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			setApiKey(result);
		}

	}

	// 监听手机上的BACK键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
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
							}).setNegativeButton(R.string.cancel, null)
					.create().show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left,
				R.anim.back_out_right);
	}
}
