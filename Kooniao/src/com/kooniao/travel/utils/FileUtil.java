package com.kooniao.travel.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.http.util.EncodingUtils;

import android.util.Log;

import com.kooniao.travel.constant.Define;

/**
 * 文件操作工具类
 * 
 * @author ke.wei.quan
 * 
 */
public class FileUtil {
	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			java.io.File myDelFile = new java.io.File(filePathAndName);
			myDelFile.delete();
		} catch (Exception e) {
			Log.i(Define.LOG_TAG, e.toString());
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			java.io.File myFilePath = new java.io.File(folderPath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			Log.i(Define.LOG_TAG, e.toString());
		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @throws Exception
	 */
	public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration<?> zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				Log.d(Define.LOG_TAG, "upZipFile , ze.getName() = " + ze.getName());
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				Log.d(Define.LOG_TAG, "upZipFile , str = " + dirstr);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			Log.d(Define.LOG_TAG, "upZipFile , ze.getName() = " + ze.getName());
			OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		Log.d(Define.LOG_TAG, "upZipFile , finish...");
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					// substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					Log.i(Define.LOG_TAG, e.toString());
				}
				ret = new File(ret, substr);

			}
			Log.d(Define.LOG_TAG, "upZipFile , 1ret = " + ret);
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				// substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d(Define.LOG_TAG, "upZipFile , substr = " + substr);
			} catch (UnsupportedEncodingException e) {
				Log.i(Define.LOG_TAG, e.toString());
			}

			ret = new File(ret, substr);
			Log.d(Define.LOG_TAG, "upZipFile , 2ret = " + ret);
			return ret;
		}
		return ret;
	}

	/**
	 * 读取SD卡中的文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFileFromSdcard(String fileName) {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			Log.i(Define.LOG_TAG, e.toString());
		}
		return res;
	}

	/**
	 * 保存SD卡中的文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static void saveFileToSdcard(String fileName, String res) {
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(fileName);
			fwriter.write(res);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
			} catch (IOException e) {
				Log.i(Define.LOG_TAG, e.toString());
			}
		}
	}

	/**
	 * 计算文件或者文件夹的大小 ，单位 MB
	 * 
	 * @param path
	 * @return 大小，单位：MB
	 */
	public static double getSize(String path) {
		File file = new File(path);
		// 判断文件是否存在
		if (file.exists()) {
			// 如果是目录则递归计算其内容的总大小，如果是文件则直接返回其大小
			if (!file.isFile()) {
				// 获取文件大小
				File[] fl = file.listFiles();
				double size = 0;
				for (File f : fl) {
					path = f.getAbsolutePath();
					size += getSize(path);
				}
				return size;

			} else {
				double size = (double) file.length() / 1024 / 1024;
				return size;

			}

		} else {
			return 0;
		}

	}

	/**
	 * 字符串转换unicode
	 */
	public static String string2Unicode(String string) {

		StringBuffer unicode = new StringBuffer();

		for (int i = 0; i < string.length(); i++) {

			// 取出每一个字符
			char c = string.charAt(i);

			// 转换为unicode
			unicode.append("\\u" + Integer.toHexString(c));
		}

		return unicode.toString();
	}

}
