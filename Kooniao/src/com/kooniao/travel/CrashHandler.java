package com.kooniao.travel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.AppManager;
import com.kooniao.travel.utils.AppSetting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 捕获全局异常，写入日志文件
 * 
 * @author ke.wei.quan
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler defaultHandler;
	private static CrashHandler instance = new CrashHandler();
	private String logPath = ""; // 日志打印路径
	private Context context;
	/**
	 * 用来存储设备信息和异常信息
	 */
	private Map<String, String> logInfo = new HashMap<String, String>();
	/**
	 * 用于格式化日期,作为日志文件名的一部分
	 */
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

	/**
	 * Creates a new instance of CrashHandler.
	 */
	private CrashHandler() {
	}

	/**
	 * getInstance:{获取CrashHandler实例 ,单例模式 }
	 */
	public static CrashHandler getInstance() {
		return instance;
	}

	/**
	 * 初始化
	 * 
	 * @param paramContext
	 * @param logPath
	 *            日志打印路径
	 */
	public void init(Context paramContext, String logPath) {
		context = paramContext;
		this.logPath = logPath;
		// 获取系统默认的UncaughtException处理器
		defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
		if (!handleException(paramThrowable) && defaultHandler != null) {
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			defaultHandler.uncaughtException(paramThread, paramThrowable);
		} else {
			try {
				// 如果处理了，让程序继续运行1秒再退出，保证文件保存并上传到服务器
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

	/**
	 * handleException:{自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.}
	 * 
	 * @param paramThrowable
	 * @return true:如果处理了该异常信息;否则返回false.
	 * @throws
	 */
	public boolean handleException(Throwable paramThrowable) {
		if (paramThrowable == null)
			return false;
		new Thread() {
			public void run() {
				Looper.prepare();
				Toast.makeText(context, "非常抱歉,酷鸟行程出现了异常,即将退出", Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		// 获取设备参数信息
		getDeviceInfo(context);
		// 保存日志文件
		saveCrashLogToFile(paramThrowable);
		return true;
	}

	/**
	 * getDeviceInfo:{获取设备参数信息}
	 * 
	 * @param paramContext
	 */
	public void getDeviceInfo(Context paramContext) {
		logInfo.put("versionCode", AppManager.getInstance().getCurrentAppVersion());
		// 反射机制
		Field[] mFields = Build.class.getDeclaredFields();
		// 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
		for (Field field : mFields) {
			try {
				field.setAccessible(true);
				logInfo.put(field.getName(), field.get("").toString());
				Log.d(Define.LOG_TAG, field.getName() + ":" + field.get(""));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * saveCrashLogToFile:{将崩溃的Log保存到本地} 
	 * 
	 * @param paramThrowable
	 * @return FileName
	 */
	private String saveCrashLogToFile(Throwable paramThrowable) {
		StringBuffer mStringBuffer = new StringBuffer();
		for (Map.Entry<String, String> entry : logInfo.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			mStringBuffer.append(key + "=" + value + "\r\n");
		}
		Writer mWriter = new StringWriter();
		PrintWriter mPrintWriter = new PrintWriter(mWriter);
		paramThrowable.printStackTrace(mPrintWriter);
		paramThrowable.printStackTrace();
		Throwable mThrowable = paramThrowable.getCause();
		// 迭代栈队列把所有的异常信息写入writer中
		while (mThrowable != null) {
			mThrowable.printStackTrace(mPrintWriter);
			// 换行 每个个异常栈之间换行
			mPrintWriter.append("\r\n");
			mThrowable = mThrowable.getCause();
		}
		// 记得关闭
		mPrintWriter.close();
		String mResult = mWriter.toString();
		mStringBuffer.append(mResult);
		// 保存文件，设置文件名
		String mTime = mSimpleDateFormat.format(new Date());
		String mFileName = "KooniaoCrashLog-" + mTime + ".txt";
		AppSetting.getInstance().saveStringPreferencesByKey(Define.LAST_LOG_NAME, mFileName); 
		try {
			File directory = new File(logPath);
			Log.v(Define.LOG_TAG, directory.toString());
			if (!directory.exists())
				directory.mkdir();
			FileOutputStream mFileOutputStream = new FileOutputStream(directory + "/" + mFileName);
			mFileOutputStream.write(mStringBuffer.toString().getBytes());
			mFileOutputStream.close();
			return mFileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
