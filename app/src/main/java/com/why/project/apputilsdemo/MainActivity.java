package com.why.project.apputilsdemo;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.why.project.apputilsdemo.util.AppDir;
import com.why.project.apputilsdemo.util.AppUtils;

import java.io.File;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private Button btn_show;
	private Button btn_install;
	private TextView tv_show;

	String apkFileSavePath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		onePermission();//申请运行时权限（读写文件的权限）

		initViews();
		initEvents();
	}

	private void initViews() {
		btn_show = findViewById(R.id.btn_show);
		btn_install = findViewById(R.id.btn_install);
		tv_show = findViewById(R.id.tv_show);
	}

	private void initEvents() {
		btn_show.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String DeviceVersion = AppUtils.getDeviceVersion();
				String DeviceName = AppUtils.getDeviceName();
				int VersionCode = AppUtils.getVersionCode(MainActivity.this);
				String VersionName = AppUtils.getVersionName(MainActivity.this);
				String PackageName = AppUtils.getPackageName(MainActivity.this);
				boolean isUpdate = AppUtils.getVersionUpdate(MainActivity.this,"2.0");

				String showText = "设备的系统版本号：" + DeviceVersion +
						"\n设备的型号：" + DeviceName +
						"\n应用版本号code值：" + VersionCode +
						"\n应用版本号name值：" + VersionName +
						"\n包名：" + PackageName +
						"\n是否更新(服务器版本号name值是2.0)：" + isUpdate;

				tv_show.setText(showText);
			}
		});

		btn_install.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				checkOreo();
			}
		});
	}

	/**
	 * 检测版本8.0
	 */
	public void checkOreo() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
			//判断是否可以直接安装
			boolean canInstall = getPackageManager().canRequestPackageInstalls();
			if (!canInstall) {
				RxPermissions rxPermissions = new RxPermissions(MainActivity.this); // where this is an Activity instance
				rxPermissions.request(Manifest.permission.REQUEST_INSTALL_PACKAGES) //权限名称，多个权限之间逗号分隔开
						.subscribe(new Consumer<Boolean>() {
							@Override
							public void accept(Boolean granted) throws Exception {
								Log.e(TAG, "{accept}granted=" + granted);//执行顺序——1【多个权限的情况，只有所有的权限均允许的情况下granted==true】
								if (granted) { // 在android 6.0之前会默认返回true
									//安装APP
									AppUtils.installApk(MainActivity.this, apkFileSavePath);
								} else {
									Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
									startActivityForResult(intent, 1000);
								}
							}
						}, new Consumer<Throwable>() {
							@Override
							public void accept(Throwable throwable) throws Exception {
								Log.e(TAG,"{accept}");//可能是授权异常的情况下的处理
							}
						}, new Action() {
							@Override
							public void run() throws Exception {
								Log.e(TAG,"{run}");//执行顺序——2
							}
						});
			} else {
				//安装APP
				AppUtils.installApk(MainActivity.this,apkFileSavePath);
			}
		} else {
			//安装APP
			AppUtils.installApk(MainActivity.this,apkFileSavePath);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 1000:
				checkOreo();
				break;
		}
	}


	/**只有一个运行时权限申请的情况*/
	private void onePermission(){
		RxPermissions rxPermissions = new RxPermissions(MainActivity.this); // where this is an Activity instance
		rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE) //权限名称，多个权限之间逗号分隔开
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean granted) throws Exception {
						Log.e(TAG, "{accept}granted=" + granted);//执行顺序——1【多个权限的情况，只有所有的权限均允许的情况下granted==true】
						if (granted) { // 在android 6.0之前会默认返回true
							// 已经获取权限
							Toast.makeText(MainActivity.this, "已经获取权限", Toast.LENGTH_SHORT).show();
							downloadApkFile();
						} else {
							// 未获取权限
							Toast.makeText(MainActivity.this, "您没有授权该权限，请在设置中打开授权", Toast.LENGTH_SHORT).show();
						}
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						Log.e(TAG,"{accept}");//可能是授权异常的情况下的处理
					}
				}, new Action() {
					@Override
					public void run() throws Exception {
						Log.e(TAG,"{run}");//执行顺序——2
					}
				});
	}

	/**模拟下载文件到手机本地目录下*/
	private void downloadApkFile(){
		String assetsFilePath = "wanandroid.apk";
		apkFileSavePath = AppDir.getInstance(MainActivity.this).DOWNLOAD + File.separator + "wanandroid.apk";
		DownloadUtil.copyOneFileFromAssetsToSD(MainActivity.this,assetsFilePath,apkFileSavePath);
	}

}
