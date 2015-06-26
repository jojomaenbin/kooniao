package com.zbar.lib;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.discovery.TravelDetailActivity_;
import com.kooniao.travel.home.CombineProductDetailActivity_;
import com.kooniao.travel.home.LineProductDetailActivity_;
import com.kooniao.travel.home.NonLineProductDetailActivity_;
import com.kooniao.travel.manager.QRCodeManager;
import com.kooniao.travel.manager.QRCodeManager.URLResolveResultCallback;
import com.kooniao.travel.store.StoreActivity_;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

public class CaptureActivity extends Activity implements Callback {

	final HashMap<String, String> code = new HashMap<String, String>();

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private ImageView closeImageView;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_qr_scan);
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
		closeImageView = (ImageView) findViewById(R.id.iv_qrcode_scan_close);

		closeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(1200);
		mQrLineView.startAnimation(animation);

		/**
		 * 初始化code编码表
		 */
		code.put("a", "0");
		code.put("b", "1");
		code.put("c", "2");
		code.put("d", "3");
		code.put("e", "4");
		code.put("f", "5");
		code.put("g", "6");
		code.put("h", "7");
		code.put("i", "8");
		code.put("j", "9");
	}

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();

		if (result.contains("kooniao.com") || result.contains("dev.com") || result.contains("demo.com")) {
			QRCodeManager.getInstance().urlResolve(result, new URLResolveResultCallback() {

				@Override
				public void result(String errMsg, int id, String type, int distributorId, String commonType) {
					if (errMsg == null) {
						if ("store".equals(type)) {
							// 店铺
							Intent intent = new Intent(CaptureActivity.this, StoreActivity_.class);
							intent.putExtra(Define.SID, id);
							intent.putExtra(Define.STORE_TYPE, commonType);
							startActivity(intent);
						} else if ("product".equals(type)) {
							// 产品
							Intent productDetailIntent = null;
							int productType = Integer.parseInt(commonType);
							if (productType == 2) {
								// 组合产品详情
								productDetailIntent = new Intent(CaptureActivity.this, CombineProductDetailActivity_.class);
							} else {
								if (productType == 3) {
									// 线路产品详情
									productDetailIntent = new Intent(CaptureActivity.this, LineProductDetailActivity_.class);
								} else {
									// 非线路产品详情
									productDetailIntent = new Intent(CaptureActivity.this, NonLineProductDetailActivity_.class);
								}
							}

							if (productDetailIntent != null) {
								productDetailIntent.putExtra(Define.PID, id);
								if (distributorId == 0) {
									productDetailIntent.putExtra(Define.STORE_TYPE, "a");
								} else {
									productDetailIntent.putExtra(Define.STORE_TYPE, "c");
								}
								productDetailIntent.putExtra(Define.SID, distributorId);
								startActivity(productDetailIntent);
							}

						} else if ("plan".equals(type)) {
							// 行程
							Intent intent = new Intent(CaptureActivity.this, TravelDetailActivity_.class);
							intent.putExtra(Define.PID, id);
							startActivity(intent);
						}
					} else {
						Toast.makeText(CaptureActivity.this, errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			startWebBrowser(result);
		}

		// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
		handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
	}

	private void startWebBrowser(String result) {
		Uri webUri = Uri.parse(result);
		Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
		startActivity(webIntent);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

}