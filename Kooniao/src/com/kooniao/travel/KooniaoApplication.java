package com.kooniao.travel;

import java.io.File;

import android.app.Application;
import android.app.Notification;
import android.graphics.Bitmap;
import android.os.Environment;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.kooniao.travel.constant.Define;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class KooniaoApplication extends Application {

	private static KooniaoApplication application;

	public static KooniaoApplication getInstance() {
		return application;
	}

	@Override
	public void onCreate() {
		application = this;
		initJpush();
		initBaiduSDK();
		initOrCreateAppFileDir();
		initCrashHandler();
		initImageLoader();
		super.onCreate();
	}

	/**
	 * 初始化jpush
	 */
	private void initJpush() {
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(application);
		builder.statusBarDrawable = R.drawable.app_logo;
		// 设置为自动消失
		builder.notificationDefaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
		// 设置为铃声与震动都要
		JPushInterface.setPushNotificationBuilder(1, builder);
	}

	/**
	 * 初始化百度地图SDK
	 */
	private void initBaiduSDK() {
		SDKInitializer.initialize(application);
	}

	private String appRootDir;// app的根文件夹
	private String picPath;// 图片缓存文件夹
	private String logPath;// 日志打印文件夹
	private String downloadPath;// 下载文件夹

	/**
	 * 初始化app的文件夹
	 */
	private void initOrCreateAppFileDir() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			appRootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Kooniao";
		} else {
			appRootDir = getApplicationContext().getFilesDir().getAbsolutePath() + "/Kooniao";
		}

		picPath = appRootDir + "/pic/";
		logPath = appRootDir + "/.log/";
		downloadPath = appRootDir + "/download/";

		createRootDir();
		createSubDir();
	}

	/**
	 * 创建根目录
	 */
	private void createRootDir() {
		File rootFile = new File(appRootDir);
		if (!rootFile.exists() && !rootFile.isDirectory()) {
			rootFile.mkdir();

		}
	}

	/**
	 * 创建子目录
	 */
	private void createSubDir() {
		File picFile = new File(picPath);
		if (!picFile.exists() && !picFile.isDirectory()) {
			picFile.mkdir();

		}

		File logFile = new File(logPath);
		if (!logFile.exists() && !logFile.isDirectory()) {
			logFile.mkdir();

		}

		File downloadFile = new File(downloadPath);
		if (!downloadFile.exists() && !downloadFile.isDirectory()) {
			downloadFile.mkdir();

		}
	}

	/**
	 * 获取app根目录
	 * 
	 * @return
	 */
	public String getRootDir() {
		return appRootDir;
	}

	/**
	 * 获取图片缓存目录
	 * 
	 * @return
	 */
	public String getPicDir() {
		return picPath;
	}

	/**
	 * 获取日志打印目录
	 * 
	 * @return
	 */
	public String getLogDir() {
		return logPath;
	}

	/**
	 * 获取下载目录
	 * 
	 * @return
	 */
	public String getDownloadDir() {
		return downloadPath;
	}

	/**
	 * 初始化全局捕获异常
	 */
	private void initCrashHandler() {
		if (Define.CRASHHANDLER_OPEN) {
			CrashHandler crashHandler = CrashHandler.getInstance();
			crashHandler.init(application, logPath);
		}
	}

	/*
	 * 初始化图片加载器
	 */
	private void initImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions//
		.Builder()//
				.cacheInMemory(true)//
				.cacheOnDisc(true)//
				.showImageOnFail(R.drawable.list_default_cover)//
				.showImageForEmptyUri(R.drawable.list_default_cover)//
				.bitmapConfig(Bitmap.Config.RGB_565)//
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration//
		.Builder(getInstance())//
				.discCache(new UnlimitedDiscCache(new File(getPicDir())))//
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//
				.memoryCacheSize(2 * 1024 * 1024)//
				.denyCacheImageMultipleSizesInMemory()//
				.tasksProcessingOrder(QueueProcessingType.LIFO)//
				.defaultDisplayImageOptions(options)//
				.build();

		ImageLoader.getInstance().init(config);

	}

}
