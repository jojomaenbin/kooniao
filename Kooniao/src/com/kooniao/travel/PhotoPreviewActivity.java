package com.kooniao.travel;

import java.io.File;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.RectClipView;
import com.kooniao.travel.customwidget.RoundClipView;
import com.kooniao.travel.customwidget.ZoomImageView;
import com.kooniao.travel.utils.BitMapUtil;
import com.kooniao.travel.utils.FileUtil;

/**
 * 图片预览界面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_photo_preview)
public class PhotoPreviewActivity extends BaseActivity {

	public static enum Type {
		ROUND_CLIP(0), // 圆形裁剪
		RECT_CLIP(1); // 矩形裁剪

		public int type;

		Type(int type) {
			this.type = type;
		}
	}

	@ViewById(R.id.iv_go_back)
	ImageView backwardImageView; // 后退按钮
	@ViewById(R.id.tv_accept)
	TextView ensureTextView; // 确定按钮
	@ViewById(R.id.ziv_preview_image)
	ZoomImageView previewImageView; // 预览的图片
	@ViewById(R.id.cv_round)
	RoundClipView roundClipView; // 圆形裁剪
	@ViewById(R.id.cv_rect)
	RectClipView rectClipView; // 矩形裁剪(16:9)

	private View currentClipView;

	@AfterViews
	void init() {
		initData();
	}

	private int type = Type.ROUND_CLIP.type; // 什么类型的裁剪(默认圆形)
	private Bitmap avatar; // 预览的图片

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent data = getIntent();
		if (data != null) {
			String path = KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL;
			double fileSize = FileUtil.getSize(path);
			if (fileSize <= 5) {
				File file = new File(path);
				if (file.exists()) {
					avatar = BitMapUtil.loadBitmapRotate(PhotoPreviewActivity.this, file.getAbsolutePath(), false, Define.widthPx, Define.widthPx);
					avatar = BitMapUtil.zoomBitmapByScreenWidth(avatar);
				}
				
				if (data.hasExtra(Define.TYPE)) {
					type = data.getIntExtra(Define.TYPE, Type.ROUND_CLIP.type);
				}
			} else {
				finish();
				Toast.makeText(PhotoPreviewActivity.this, "请选择小于5M的图片", Toast.LENGTH_SHORT).show(); 
			}
		}

		if (type == Type.RECT_CLIP.type) {
			currentClipView = rectClipView;
			rectClipView.setVisibility(View.VISIBLE);
			roundClipView.setVisibility(View.GONE);
		} else {
			currentClipView = roundClipView;
			rectClipView.setVisibility(View.GONE);
			roundClipView.setVisibility(View.VISIBLE);
		}

		/**
		 * 设置图片
		 */
		if (avatar != null) {
			previewImageView.setImageBitmap(avatar);
		}
	}

	/**
	 * 后退按钮点击
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 确定按钮点击
	 */
	@Click(R.id.tv_accept)
	void onAcceptClick() {
		int width = currentClipView.getWidth();
		int height = currentClipView.getHeight();
		int topY = (height - width * 9 / 16) / 2; // 距离顶部高度
		int bitmapHeight = height - 2 * topY; // 高度
		/**
		 * 保存
		 */
		Bitmap avatar = null;
		if (type == Type.ROUND_CLIP.type) {
			avatar = Bitmap.createBitmap(takeScreenShot(), (width - height / 2) / 2, height / 4 + getBarHeight(), height / 2, height / 2);
		} else {
			avatar = Bitmap.createBitmap(takeScreenShot(), 0, topY + getBarHeight(), width, bitmapHeight);
		}
		BitMapUtil.saveBitmap(KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL, avatar);
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @return
	 */
	private int getBarHeight() {
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int contenttop = this.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		return contenttop;
	}

	/**
	 * 获取Activity的截屏
	 * 
	 * @return
	 */
	private Bitmap takeScreenShot() {
		View view = previewImageView;
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		return view.getDrawingCache();
	}

}
