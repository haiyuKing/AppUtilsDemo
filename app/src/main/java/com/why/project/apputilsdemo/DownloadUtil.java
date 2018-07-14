package com.why.project.apputilsdemo;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by HaiyuKing
 * Used 模拟下载文件的工具类：其实就是从assets目录复制到手机中
 */

public class DownloadUtil {

	/**
	 *  从assets目录中复制单个文件到SD卡中
	 *  @param  mContext  Context 使用CopyFiles类的Activity
	 *  @param  assetsFilePath  String  assets的相对路径(目录)  如："why/img/image.png"
	 *  @param  sdFilePath  String  复制到的sd路径（目录）  如：Environment.getExternalStorageDirectory() + "/why/img.png"
	 */
	public static boolean copyOneFileFromAssetsToSD(Context mContext, String assetsFilePath, String sdFilePath) {

		try {

			InputStream stream = mContext.getResources().getAssets().open(assetsFilePath);

			File file = new File(sdFilePath);

			OutputStream o = null;
			try {
				//创建父目录
				String parentPath = file.getAbsolutePath();
				String getFolderName = "";
				if(parentPath == null || parentPath.length() == 0){
					getFolderName = parentPath;
				}else{
					int filePosi = parentPath.lastIndexOf(File.separator);
					getFolderName = (filePosi == -1) ? "" : parentPath.substring(0, filePosi);
				}

				Boolean makeDirs = false;
				if(getFolderName == null || getFolderName.length() == 0){
					makeDirs = false;
				}else{
					File folder = new File(getFolderName);
					makeDirs = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
				}
				if(makeDirs){
					o = new FileOutputStream(file,false);
					byte data[] = new byte[1024];
					int length = -1;
					while ((length = stream.read(data)) != -1) {
						o.write(data, 0, length);
					}
					o.flush();
					return true;
				}

			} catch (FileNotFoundException e) {
				throw new RuntimeException("FileNotFoundException occurred. ", e);
			} catch (IOException e) {
				throw new RuntimeException("IOException occurred. ", e);
			} finally {
				if (o != null) {
					try {
						o.close();
						stream.close();
					} catch (IOException e) {
						throw new RuntimeException("IOException occurred. ", e);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception occurred. ", e);
		}
		return false;
	}


}
