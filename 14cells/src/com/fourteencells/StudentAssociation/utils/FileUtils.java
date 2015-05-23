package com.fourteencells.StudentAssociation.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import android.os.Environment;
import android.util.Log;

/**
 * 文件操作工具类
 * 
 * @author 师春雷
 * 
 */
public class FileUtils {

	private static final String TAG = "FileUtils";

	/**
	 * 在SD卡下创建文件夹
	 * 
	 * @return
	 */
	public static File createFolders(String folder) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			File baseDir = Environment.getExternalStorageDirectory();

			File cellsFolder = new File(baseDir, folder);

			if (cellsFolder.exists())
				return cellsFolder;
			if (cellsFolder.mkdirs())
				return cellsFolder;

			return baseDir;
		}
		return null;
	}

	/**
	 * 读取SDCard 文件内容
	 * 
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String read(String fileName) {

		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// 获取SD卡对应存储目录
				File sdCardDir = Environment.getExternalStorageDirectory();
				// 获取指定文件对应的输出流
				FileInputStream fis = new FileInputStream(
						sdCardDir.getAbsolutePath() + "/Cells/" + fileName);
				// 将指定输入流包装成 BufferedReader
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis));
				StringBuilder sb = new StringBuilder("");
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				return sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 在 SDCard 写入文件
	 * 
	 * @param content
	 * @param fileName
	 * @return
	 */
	public static String write(String content, String fileName) {
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File sdCardDir = Environment.getExternalStorageDirectory();

				String sPath = sdCardDir.getAbsolutePath() + "/Cells/"
						+ fileName;
				File file = new File(sPath);
				// 判断文件是否存在
				if (!file.exists()) { // 不存在返回 false,创建文件
					File targetDir = new File(sPath);
					// 以指定文件创建对象 RandomAccessFile
					RandomAccessFile raf = new RandomAccessFile(targetDir, "rw");
					// 输出文件内容
					raf.write(content.getBytes());
					raf.close();
				} else {// 存在的话，先删除之前的文件，然后重新创建一个同名文件
					// 如果文件存在则删除文件
					DeleteFile(fileName);
					// 重新创建一个文件
					File targetDir = new File(sdCardDir.getAbsolutePath()
							+ "/Cells/" + fileName);
					// 以指定文件创建对象 RandomAccessFile
					RandomAccessFile raf = new RandomAccessFile(targetDir, "rw");
					// 输出文件内容
					raf.write(content.getBytes());
					raf.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 删除本地文件
	 * 
	 * @param fileName
	 */
	public static void DeleteFile(String fileName) {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdCardDir = Environment.getExternalStorageDirectory();
			String path = sdCardDir.getAbsolutePath() + "/Cells/" + fileName;

			File file = new File(path);
			// 判断目录或文件是否存在
			if (file.exists() && file.isFile()) { // 为文件且不为空则进行删除
				Log.i(TAG, "file.delete()");
				file.delete();
			}
		}
	}

}
