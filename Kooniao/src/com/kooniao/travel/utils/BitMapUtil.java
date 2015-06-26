package com.kooniao.travel.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.kooniao.travel.constant.Define;

public class BitMapUtil {

	private static int count = 0;

	/**
	 * 获取相机照相图片
	 * 
	 * @param context
	 * @param imgpath
	 * @param adjustOritation
	 * @return
	 */
	public static Bitmap loadBitmapRotate(Context context, String imgpath, boolean adjustOritation, int width, int height) {
		if (!adjustOritation) {
			return loadBitmap(context, imgpath, width, height);
		} else {

			Bitmap bitmap = loadBitmap(context, imgpath, width, height);
			int rotate = 0;
			ExifInterface exif = null;
			try {
				exif = new ExifInterface(imgpath);
			} catch (IOException e) {
				e.printStackTrace();
				exif = null;
			}
			if (exif != null) {
				// 读取图片中相机方向信息
				int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
				// 计算旋转角度
				switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				default:
					rotate = 0;
					break;
				}
			}
			if (rotate != 0) {
				// 旋转图片
				Matrix matrix = new Matrix();
				matrix.postRotate(rotate);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
			}
			return bitmap;
		}
	}

	/**
	 * 加载bitmap
	 * 
	 * @param c
	 * @param fileName
	 * @return
	 */
	public static Bitmap loadBitmap(Context c, String fileName) {
		ParcelFileDescriptor pfd;
		try {
			pfd = c.getContentResolver().openFileDescriptor(Uri.parse("file://" + fileName), "r");
		} catch (IOException ex) {
			return null;
		}
		java.io.FileDescriptor fd = pfd.getFileDescriptor();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inTempStorage = new byte[10 * 1024];
		// 先指定原始大小
		options.inSampleSize = 1;
		// 只进行大小判断
		options.inJustDecodeBounds = true;
		// 调用此方法得到options得到图片的大小
		BitmapFactory.decodeFileDescriptor(fd, null, options);
		// 我们的目标是在400pixel的画面上显示。
		// 所以需要调用computeSampleSize得到图片缩放的比例
		options.inSampleSize = computeSampleSize(options, 400);
		// OK,我们得到了缩放的比例，现在开始正式读入BitMap数据
		options.inJustDecodeBounds = false;
		options.inDither = false;
		if (Define.DENSITY >= 1.5) {
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		} else {
			options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		}

		// 根据options参数，减少所需要的内存
		Bitmap sourceBitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
		if (count < 4) {
			count++;
		} else {
			count = 0;
			System.gc();
		}
		return sourceBitmap;
	}

	// 这个函数会对图片的大小进行判断，并得到合适的缩放比例，比如2即1/2,3即1/3
	public static int computeSampleSize(BitmapFactory.Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}
		return candidate;
	}

	/**
	 * 指定宽高加载bitmap
	 * 
	 * @param c
	 * @param fileName
	 * @param width
	 * @return
	 */
	public static Bitmap loadBitmap(Context c, String fileName, int width, int height) {
		int count = 0;
		ParcelFileDescriptor pfd;
		try {
			pfd = c.getContentResolver().openFileDescriptor(Uri.parse("file://" + fileName), "r");
		} catch (IOException ex) {
			return null;
		}
		java.io.FileDescriptor fd = pfd.getFileDescriptor();
		BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inTempStorage = new byte[10 * 1024];
		// 先指定原始大小
		options.inSampleSize = 1;
		// 只进行大小判断
		options.inJustDecodeBounds = true;
		// 调用此方法得到options得到图片的大小
		BitmapFactory.decodeFileDescriptor(fd, null, options);
		// 我们的目标是在400pixel的画面上显示。
		// 所以需要调用computeSampleSize得到图片缩放的比例
		options.inSampleSize = computeSampleSize(options, width, height);
		// OK,我们得到了缩放的比例，现在开始正式读入BitMap数据
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 根据options参数，减少所需要的内存
		try {
			Bitmap sourceBitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
			if (count < 4) {
				count++;
			} else {
				count = 0;
				System.gc();
			}
			return sourceBitmap;
		} catch (OutOfMemoryError e) {
			return loadBitmap(c, fileName, (int) (width * 0.5), (int) (height * 0.5));
		}
	}

	/**
	 * 用computeSampleSize得到图片缩放的比例
	 * 
	 * @param options
	 * @param target
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options, int width, int height) {
		int w = options.outWidth;
		int h = options.outHeight;
		float candidateW = w / width;
		float candidateH = h / height;
		float candidate = Math.max(candidateW, candidateH);
		candidate = (float) (candidate + 0.5);
		if (candidate < 1.0) {
			return 1;
		}
		return (int) candidate;
	}

	/**
	 * 保存图片
	 * 
	 * @param bitName
	 * @param bitmap
	 * @return
	 */
	public static boolean saveBitmap(String bitName, Bitmap bitmap) {
		if (bitmap != null) {
			try {
				File file = new File(bitName);
				try {
					if (file.exists()) {
						file.delete();
					}
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				FileOutputStream fOut = null;
				try {
					fOut = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
				}
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
				fOut.flush();
				fOut.close();
				return true;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * 压缩图片
	 * 
	 * @param image
	 * @param targetKB
	 * @return
	 */
	public static int compressImage(Bitmap image, int targetKB) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;
		Log.i(Define.LOG_TAG, "压缩前大小:" + baos.toByteArray().length / 1024 + "KB");
		while (baos.toByteArray().length / 1024 > targetKB) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;
		}
		Log.i(Define.LOG_TAG, "压缩后最终大小:" + baos.toByteArray().length / 1024 + "KB");
		return options;
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			left = 0;
			top = 0;
			right = width;
			bottom = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
		paint.setColor(color);

		// 以下有两种方法画圆,drawRounRect和drawCircle
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
		// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}

	/**
	 * 高斯模糊化
	 * 
	 * @param sentBitmap
	 * @param radius
	 *            模糊因子
	 * @param canReuseInBitmap
	 *            是否可以循环使用
	 * @return
	 */
	private static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

		Bitmap bitmap;
		if (canReuseInBitmap) {
			bitmap = sentBitmap;
		} else {
			bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
		}

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}

	/**
	 * 毛玻璃效果
	 * 
	 * @param bitmap
	 * @param view
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void blur(Bitmap bitmap, ImageView view) {
		float scaleFactor = 10; // 模糊因子，越大越模糊，花费时间越多
		float radius = 1;

		Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor), (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(bitmap, 0, 0, paint);

		overlay = BitMapUtil.doBlur(overlay, (int) radius, true);
		view.setImageBitmap(overlay);
	}

	/**
	 * 按照屏幕宽度缩放图片
	 * 
	 * @param oldBitmap
	 * @return
	 */
	public static Bitmap zoomBitmapByScreenWidth(Bitmap oldBitmap) {
		if (oldBitmap != null) {
			int oldBitmapWidth = oldBitmap.getWidth();
			int oldBitmapHeight = oldBitmap.getHeight();
			int phoneScreenWidth = Define.widthPx;
			float scaleWidth = ((float) phoneScreenWidth) / oldBitmapWidth;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleWidth);
			Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmapWidth, oldBitmapHeight, matrix, true);
			return newBitmap;
		} else {
			return oldBitmap;
		}
	}

	/**
	 * 根据size缩放图片
	 * 
	 * @param oldBitmap
	 * @param size
	 * @return
	 */
	public static Bitmap zoomBitmapBySize(Bitmap oldBitmap, int size) {
		if (oldBitmap != null) {
			int oldBitmapWidth = oldBitmap.getWidth();
			int oldBitmapHeight = oldBitmap.getHeight();

			float scaleHeight = ((float) size) / oldBitmapHeight;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleHeight, scaleHeight);
			Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmapWidth, oldBitmapHeight, matrix, true);
			return newBitmap;
		} else {
			return oldBitmap;
		}
	}

	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * 
	 * @param context
	 * @param imageUri
	 */
	@TargetApi(19)
	public static String getRealPathFromURI(Context context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

}
