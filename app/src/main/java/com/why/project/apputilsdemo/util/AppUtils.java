package com.why.project.apputilsdemo.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

/**
 * 获取手机的信息和应用版本号、安装apk
 */
public class AppUtils {

    /**
     * 获取设备的系统版本号
     */
    public static String getDeviceVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取设备的型号
     */
    public static String getDeviceName() {
        String model = android.os.Build.MODEL;
        return model;
    }

    /**
     * 应用版本号code值
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }
    /**
     * 应用版本号name值
     */
    public static String getVersionName(Context context){
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    //获取包名
    public static String getPackageName(Context context){
        return context.getPackageName();
    }

    //是否更新，根据versionName值进行判断
    public static boolean getVersionUpdate(Context context, String versionNameServer){
        //versionNameServer = "3.1";
        String versionNameLocal = getVersionName(context);
        if(!TextUtils.isEmpty(versionNameLocal) && !TextUtils.isEmpty(versionNameServer)){
            String[] splitLocal = versionNameLocal.split("\\.");
            String[] splitServer = versionNameServer.split("\\.");
            if(splitLocal.length == splitServer.length){
                for(int i=0;i<splitLocal.length;i++){
                    int localInt = Integer.parseInt(splitLocal[i]);
                    int serverInt = Integer.parseInt(splitServer[i]);
                    if(serverInt > localInt){
                        return true;
                    }else if(serverInt==localInt){
                    }
                    else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**返回安装apk的Intent*/
    public static Intent getFileIntent(Context mContext,String fileSavePath) {
        File apkfile = new File(fileSavePath);
        if (!apkfile.exists()) {
            return null;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        Uri uri;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = mContext.getApplicationInfo().packageName + ".provider";
            uri = FileProvider.getUriForFile(mContext.getApplicationContext(), authority, apkfile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件【很重要】
        } else {
            uri = Uri.fromFile(apkfile);
        }
        intent.setDataAndType(uri, getMIMEType(apkfile));
        return intent;
    }

    public static String getMIMEType(File file) {
        String type = null;
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
        if (suffix.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            // /*如果无法直接打开，就跳出软件列表给用户选择 */
            type = "*/*";
        }
        return type;
    }

    /**
     * 安装apk【如果项目中需要使用这个方法的话，需要申请运行时权限（读写文件的权限）、需要特出处理Android8.0的请求未知来源权限】
     */
    public static void installApk(Context mContext,String fileSavePath) {
        Intent intent = getFileIntent(mContext,fileSavePath);
        if(intent != null){
            mContext.startActivity(intent);
        }
    }
}
