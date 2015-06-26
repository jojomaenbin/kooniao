package com.kooniao.travel.store;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APISaveProductInfoCallback;
import com.kooniao.travel.api.ApiCaller.APISubmitProductInfoCallback;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoDatePickerDialog;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.ProductInfoResultCallback;
import com.kooniao.travel.model.ProductCatalog;
import com.kooniao.travel.model.ProductInfo;
import com.kooniao.travel.model.ProductResource;
import com.kooniao.travel.model.Area;
import com.kooniao.travel.store.DistributionSetActivity.ParamsSetting;
import com.kooniao.travel.store.DistributionSetActivity.SettingWay;
import com.kooniao.travel.utils.BitMapUtil;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.JsonTools;
import com.kooniao.travel.view.materialdesign.DeleteImageView;
import com.kooniao.travel.view.materialdesign.SwitchButton;

/**
 * 类说明
 * 
 * @author zheng.zong.di
 * @date 2015年6月10日
 * @version 1.0
 *
 */
@EActivity(R.layout.activity_product_edit)
public class ProductEditActivity extends BaseActivity {
	@ViewById(R.id.et_product_name)
	EditText productnameEditText; //
	@ViewById(R.id.et_product_phonenumb)
	EditText phonenumberEditText; //
	@ViewById(R.id.tv_product_introdruce)
	TextView introduceText; //
	@ViewById(R.id.tv_product_catalog)
	TextView productcatalogText; //
	@ViewById(R.id.sb_seeable)
	SwitchButton seeableSwitch; //
	@ViewById(R.id.sb_putawayable)
	SwitchButton putawayableSwitch; //
	@ViewById(R.id.tv_starttime)
	TextView startimeText; //
	@ViewById(R.id.tv_endtime)
	TextView endtimeText; //
	@ViewById(R.id.et_number)
	EditText numberEdit; //
	@ViewById(R.id.et_opentime)
	EditText opentimeEdit; //
	@ViewById(R.id.tv_edit_tabs)
	TextView tabsText; //
	@ViewById(R.id.tv_catalog)
	TextView catalogText; //
	@ViewById(R.id.tv_next_step)
	View next_stepView; //
	@ViewById(R.id.ll_other_all)
	LinearLayout otherallLayout; // 其他信息栏
	@ViewById(R.id.ll_group_product)
	LinearLayout groupLayout; // 组合产品栏
	@ViewById(R.id.scene_starlevel_layout)
	LinearLayout starlevelLayout; // 景点星级
	@ViewById(R.id.tv_starlevel)
	TextView starlevelText; // 景点或酒店星级
	@ViewById(R.id.ll_default_show)
	LinearLayout defaultshowLayout; // 默认展示页
	@ViewById(R.id.tv_level)
	TextView levelsetText; // 星级
	@ViewById(R.id.tv_product_type_layout)
	LinearLayout typeLayout; // 产品类型
	@ViewById(R.id.tv_editaddress)
	EditText adddressText; //
	@ViewById(R.id.tv_address_num)
	TextView adddressnumText; //
	@ViewById(R.id.tv_standardnum)
	TextView standardnumText; //
	@ViewById(R.id.tv_distribute)
	TextView disributenumText; //
	@ViewById(R.id.image_scroll_shadow)
	ImageView imageshadowImage; //
	@ViewById(R.id.resource_scroll_shadow)
	ImageView resourceshadowImage; //
	@ViewById(R.id.tv_other_all)
	TextView otherTextView; // 其他信息

	@AfterViews
	void init() {
		initData();
	}

	private int pid;
	protected String mProductIntroduceString;
	private ProductInfo productInfo;
	public List<Area> areaList;
	private List<String> mTabs;
	private List<ProductCatalog> catalogList; // 产品目录列表

	public void initData() {
		next_stepView.setBackgroundResource(R.color.vd0d0d0);
		next_stepView.setClickable(false);
		Intent intent = getIntent();
		if (intent != null) {
			pid = intent.getIntExtra(Define.PID, 0);
			showProgressDialog();
			loadproductInfo(pid);
		}
		pictureLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (pictureLayout.getWidth() > hs.getWidth() && addTextView1.getVisibility() == View.VISIBLE) {

					pictureLayout.post(new Runnable() {

						@Override
						public void run() {
							addTextView1.setVisibility(View.GONE);
							taddTextView2.setVisibility(View.VISIBLE);
							imageshadowImage.setVisibility(View.VISIBLE);
							if (checkNextClickable()) {
								next_stepView.setClickable(true);
								next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
							} else {
								next_stepView.setClickable(false);
								next_stepView.setBackgroundResource(R.color.vd0d0d0);
							}
						}
					});

				} else if (pictureLayout.getWidth() <= hs.getWidth()) {
					pictureLayout.post(new Runnable() {

						@Override
						public void run() {
							addTextView1.setVisibility(View.VISIBLE);
							taddTextView2.setVisibility(View.GONE);
							imageshadowImage.setVisibility(View.GONE);
							if (checkNextClickable()) {
								next_stepView.setClickable(true);
								next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
							} else {
								next_stepView.setClickable(false);
								next_stepView.setBackgroundResource(R.color.vd0d0d0);
							}
						}
					});
				}
			}
		});
		resource_layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (resource_layout.getWidth() > add_resource_layout.getWidth() && add_resource_in.getVisibility() == View.VISIBLE) {

					resource_layout.post(new Runnable() {

						@Override
						public void run() {
							add_resource_in.setVisibility(View.GONE);
							add_resource.setVisibility(View.VISIBLE);
							resourceshadowImage.setVisibility(View.VISIBLE);
							if (checkNextClickable()) {
								next_stepView.setClickable(true);
								next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
							} else {
								next_stepView.setClickable(false);
								next_stepView.setBackgroundResource(R.color.vd0d0d0);
							}
						}
					});

				} else if (resource_layout.getWidth() <= add_resource_layout.getWidth()) {
					resource_layout.post(new Runnable() {

						@Override
						public void run() {
							add_resource_in.setVisibility(View.VISIBLE);
							add_resource.setVisibility(View.GONE);
							resourceshadowImage.setVisibility(View.GONE);
							if (checkNextClickable()) {
								next_stepView.setClickable(true);
								next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
							} else {
								next_stepView.setClickable(false);
								next_stepView.setBackgroundResource(R.color.vd0d0d0);
							}
						}
					});
				}
			}
		});
		putawayableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (isChecked) {
						String dateString = DateUtil.timestampToStr(ApiCaller.TimeInService, "yyyy-MM-dd");
						startimeText.setText(dateString);
					}
					startimeText.setClickable(false);
				} else {
					// endtimeText.setText("结束时间");
					// startimeText.setText("起始时间");
					long time = DateUtil.strToTimestamp(productInfo.getEndTime(), Define.FORMAT_YMD);
					if (time > 0) {
						endtimeText.setText(DateUtil.timestampToStr(time, "yyyy-MM-dd"));
					}
					time = DateUtil.strToTimestamp(productInfo.getStartTime(), Define.FORMAT_YMD);
					if (time > 0) {
						startimeText.setText(DateUtil.timestampToStr(time, "yyyy-MM-dd"));
					}
					startimeText.setClickable(true);
				}
			}
		});
		putawayableSwitch.setChecked(false);
		productnameEditText.addTextChangedListener(mTextWatcher);
		productnameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (productnameEditText.getText().length() < 3) {
						showToast("产品名称长度必须大于3！");
					}
				}
			}
		});
		phonenumberEditText.addTextChangedListener(mTextWatcher);
		startimeText.addTextChangedListener(mTextWatcher);
	}

	@ViewById(R.id.add_1)
	TextView addTextView1; //
	@ViewById(R.id.add_2)
	TextView taddTextView2; //
	@ViewById(R.id.picture_layout)
	LinearLayout pictureLayout; //
	@ViewById(R.id.scroll_layout)
	HorizontalScrollView hs; //
	@ViewById(R.id.add_resource)
	TextView add_resource; //
	@ViewById(R.id.add_resource_in)
	TextView add_resource_in; //
	@ViewById(R.id.resource_layout)
	LinearLayout resource_layout; //
	@ViewById(R.id.add_resource_layout)
	HorizontalScrollView add_resource_layout; //

	public void initView() {
		submitAreas = new ArrayList<>();
		productnameEditText.setText(productInfo.getProductName());
		phonenumberEditText.setText(productInfo.getMobile());
		mProductIntroduceString = productInfo.getIntroduction();
		// startimeText.setText(DateUtil.timestampToStr(Long.valueOf(productInfo.getStartTime()),
		// "yyyy-MM-dd"));
		// endtimeText.setText(DateUtil.timestampToStr(Long.valueOf(productInfo.getEndTime()),
		// "yyyy-MM-dd"));
		long time = DateUtil.strToTimestamp(productInfo.getEndTime(), Define.FORMAT_YMD);
		if (time > 0) {
			endtimeText.setText(DateUtil.timestampToStr(time, "yyyy-MM-dd"));
		}
		time = DateUtil.strToTimestamp(productInfo.getStartTime(), Define.FORMAT_YMD);
		if (time > 0) {
			startimeText.setText(DateUtil.timestampToStr(time, "yyyy-MM-dd"));
		}
		seeableSwitch.setChecked(productInfo.getIsPublic() == 1 ? true : false);
		putawayableSwitch.setChecked(productInfo.getStatus() == 1 ? true : false);
		numberEdit.setText(productInfo.getProductName());
		opentimeEdit.setText(productInfo.getOpenTime());
		adddressText.setText(productInfo.getAddress());
		levelsetText.setText(productInfo.getGrade() + "星");
		imageList = productInfo.getImageList();
		areaList = productInfo.areaList;
		for (String imageString : productInfo.getImageList()) {
			DeleteImageView imageView = new DeleteImageView(ProductEditActivity.this);
			imageView.showProgress();
			imageView.setLayoutParams(new LinearLayout.LayoutParams(pictureLayout.getHeight(), pictureLayout.getHeight()));
			imageView.setImagePath(imageString);
			imageView.setObjectList(imageList);
			imageView.setObject(imageString);
			imageView.dissProgress();
			pictureLayout.addView(imageView, 0);
		}
		mTabs = new ArrayList<String>();
		if (productInfo.getTagList() != null) {

			for (int i = 0; i < productInfo.getTagList().size(); i++) {
				String tabString = productInfo.getTagList().get(i).getName();
				mTabs.add(tabString);
			}
			String cpbqstring = "";
			if (mTabs.size() > 0) {
				for (String t : mTabs) {
					cpbqstring = cpbqstring + t + "，";
				}
				tabsText.setText(cpbqstring.substring(0, cpbqstring.length() - 1));
				tabsText.setTextColor(Color.parseColor("#16B8EB"));
			} else {
				tabsText.setText("请输入产品标签");
				tabsText.setTextColor(Color.parseColor("#d0d0d0"));
			}
		}
		addedProductResourceList = new ArrayList<ProductResource>();
		if (productInfo.getCombination() != null) {
			for (int i = 0; i < productInfo.getCombination().size(); i++) {
				ProductResource productResource = new ProductResource();
				productResource.setLogo(productInfo.getCombination().get(i).getLogo());
				productResource.setPid(productInfo.getCombination().get(i).getId());
				DeleteImageView resourceImage = new DeleteImageView(ProductEditActivity.this);
				resourceImage.setObject(productResource);
				if (addedProductResourceList.size() < 20) {
					addedProductResourceList.add(productResource);
					resourceImage.setProductResourceList(addedProductResourceList);
					resourceImage.setLayoutParams(new LinearLayout.LayoutParams(resource_layout.getHeight(), resource_layout.getHeight() + 15));
					resourceImage.setImagePath(productResource.getLogo());
					resource_layout.addView(resourceImage, resource_layout.getChildCount() - 1);
				} else {
					showToast("产品组合最多搭配20个产品！");
					break;
				}
			}
		}
		introduceText.setText("浏览");
		introduceText.setTextColor(Color.parseColor("#16B8EB"));
		catalogList = productInfo.getCatalogList();
		if (catalogList != null) {
			if (productInfo.getCatalogList().size() > 0) {
				catalogText.setText(listCatalogTitletoString());
				catalogText.setTextColor(Color.parseColor("#16B8EB"));
			}
		}
		resetCountNum();
		// 分销
		if (productInfo.getAffiliateStatus() != 0) {
			disributenumText.setText("已设置");
			if (productInfo.getType() == 1) {
				settingWay = SettingWay.MANUAL.type;
			} else {
				settingWay = SettingWay.TEMPLATE.type;
			}
			if (productInfo.getIs_percentage() == 0) {
				paramsSetting = ParamsSetting.SUM.type;
				paramsSettingSum = productInfo.getAffiliate_commission_percentage() + "";
			} else {
				paramsSetting = ParamsSetting.PERCENT.type;
				paramsSettingSum = productInfo.getAffiliate_commission_percentage() + "";
			}
			selectTemplateId = productInfo.getCommission_theme();
		} else {
			disributenumText.setText("未设置");
		}

		if (productInfo.getWebappDefault() == 2) {
			findViewById(R.id.radio_product_context).performClick();
		}
		if (productInfo.getWebappDefault() == 3) {
			findViewById(R.id.radio_product_evaluate).performClick();
		}

		if (checkNextClickable()) {
			next_stepView.setClickable(true);
			next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
		} else {
			next_stepView.setClickable(false);
			next_stepView.setBackgroundResource(R.color.vd0d0d0);
		}

		paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pid", pid + "");
		paramsMaps.put("name", productnameEditText.getText().toString());
		paramsMaps.put("mobile", phonenumberEditText.getText().toString());
		paramsMaps.put("introduction", mProductIntroduceString);
		paramsMaps.put("class_0", productInfo.getProductClass_0() + "");
		paramsMaps.put("class_1", productInfo.getProductClass_1() + "");
		paramsMaps.put("class_2", productInfo.getProductClass_2() + "");
		paramsMaps.put("isShow", seeableSwitch.isChecked() ? "1" : "0");
		paramsMaps.put("status", putawayableSwitch.isChecked() ? "1" : "3");
		paramsMaps.put("startTime", String.valueOf(DateUtil.strToTimestamp(startimeText.getText().toString(), "yyyy-MM-dd")));
		paramsMaps.put("endTime", String.valueOf(DateUtil.strToTimestamp(endtimeText.getText().toString(), "yyyy-MM-dd")));
		paramsMaps.put("sku", numberEdit.getText().toString());
		paramsMaps.put("openTime", opentimeEdit.getText().toString());
		paramsMaps.put("address", adddressText.getText().toString());
		paramsMaps.put("webappDefault", "0");
		paramsMaps.put("copyProductId", "0");
		paramsMaps.put("grade", levelsetText.getText().equals("未编辑") ? "0" : String.valueOf(levelsetText.getTag()));
		paramsMaps.put("catalog", listCatalogIdtoString());
		paramsMaps.put("imageList", JsonTools.listToJson(imageList));
		paramsMaps.put("tag", mTabs.size() == 0 ? null : JsonTools.listToJson(mTabs));
		paramsMaps.put("combineList", listResourcetoString());
		submitAreas = new ArrayList<>();
		for (int i = 0; i < areaList.size(); i++) {
			SubmitArea area = new SubmitArea();
			area.city_id = areaList.get(i).city_id.id;
			area.country_id = areaList.get(i).country_id.id;
			submitAreas.add(area);
		}
		paramsMaps.put("areaList", JsonTools.listToJson(submitAreas));
		paramsMaps.put("stock", productInfo.getStock() + "");
		paramsMaps.put("marketPrice", productInfo.getMarketPrice());
		paramsMaps.put("unit", productInfo.getUnit());
		paramsMaps.put("minBuy", productInfo.getMinBuy() + "");
		paramsMaps.put("isBook", productInfo.getIsBook() + "");
		paramsMaps.put("isShowStock", productInfo.getIsShowStock() + "");
		paramsMaps.put("returnStatus", productInfo.getReturnStatus() + "");
		paramsMaps.put("affiliateStatus", productInfo.getAffiliateStatus() + "");
		paramsMaps.put("pricePackage", JsonTools.listToJson(productInfo.getPackageList()));
		String commission_string = "{  \"type\": " + productInfo.getType() + ",    \"affiliate_commission\": " + productInfo.getAffiliate_commission() + ",    \"is_percentage\":" + productInfo.getIs_percentage() + ",     \"affiliate_commission_percentage\": " + productInfo.getAffiliate_commission_percentage() + ",     \"commission_theme\":" + productInfo.getCommission_theme() + "  }";
		paramsMaps.put("commission", commission_string);

	}

	public void changeVisibility() {
		if (productInfo.getProductClass_0Name().equals("组合产品")) {
			starlevelLayout.setVisibility(View.GONE);
			groupLayout.setVisibility(View.VISIBLE);
			defaultshowLayout.setVisibility(View.GONE);
			typeLayout.setVisibility(View.GONE);
			productcatalogText.setText(productInfo.getProductClass_0Name());
		}
		if (productInfo.getProductClass_0Name().equals("线路产品")) {
			starlevelLayout.setVisibility(View.GONE);
			groupLayout.setVisibility(View.GONE);
			if (productInfo.getWebappDefault() == 0) {
				defaultshowLayout.setVisibility(View.GONE);
			} else {
				defaultshowLayout.setVisibility(View.VISIBLE);
			}
			typeLayout.setVisibility(View.VISIBLE);
			productcatalogText.setText(productInfo.getProductClass_0Name() + "-" + productInfo.getProductClass_1Name() + "-" + productInfo.getProductClass_2Name());
		}
		if (productInfo.getProductClass_0Name().equals("单一产品")) {
			starlevelLayout.setVisibility(View.VISIBLE);
			groupLayout.setVisibility(View.GONE);
			defaultshowLayout.setVisibility(View.GONE);
			typeLayout.setVisibility(View.VISIBLE);
			if (productInfo.getProductClass_1Name().equals("酒店")) {
				starlevelText.setText("酒店星级");
			} else if (productInfo.getProductClass_1Name().equals("景点门票")) {
				starlevelText.setText("景点星级");
			} else {
				starlevelLayout.setVisibility(View.GONE);
			}

			productcatalogText.setText(productInfo.getProductClass_0Name() + "-" + productInfo.getProductClass_1Name() + "-" + productInfo.getProductClass_2Name());
		}

	}

	/**
	 * 获取产品详情
	 */
	private void loadproductInfo(int pid) {
		ProductManager.getInstance().loadProductDetailInfo(pid, new ProductInfoResultCallback() {
			@Override
			public void result(String errMsg, ProductInfo productInfo) {
				loadproductInfoComplete(errMsg, productInfo);
			}
		});
	}

	/**
	 * 获取产品详情完成
	 *
	 * @param errMsg
	 * @param productInfo
	 */
	@UiThread
	void loadproductInfoComplete(String errMsg, ProductInfo productInfo) {
		dissmissProgressDialog();

		if (errMsg == null) {
			this.productInfo = productInfo;
			changeVisibility();
			initView();
		} else {
			Toast.makeText(ProductEditActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 图片添加点击
	 */
	@Click(R.id.add_1)
	void onAddPictrue1Click() {
		popupAvatarSelectView();
	}

	/**
	 * 图片添加点击
	 */
	@Click(R.id.add_2)
	void onAddPictrue2Click() {
		popupAvatarSelectView();
	}

	/**
	 * 弹出图片选择界面
	 */
	@SuppressLint("InflateParams")
	private void popupAvatarSelectView() {
		if (pictureLayout.getChildCount() >= 11) {
			showToast("产品图片最多10张！");
			return;
		}
		View selectPhotoView = LayoutInflater.from(ProductEditActivity.this).inflate(R.layout.popupwindow_avatar_select, null);
		TextView t = (TextView) selectPhotoView.findViewById(R.id.tv_picture_title);
		t.setText("选择添加类型");
		final PopupWindow popupWindow = new PopupWindow(selectPhotoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		popupWindow.setFocusable(false);
		popupWindow.setAnimationStyle(R.style.PopupAnimationFromBottom);
		// 关闭按钮
		selectPhotoView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});

		// 选择拍照
		selectPhotoView.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PHOTO);
			}
		});

		// 选择从相册选择
		selectPhotoView.findViewById(R.id.tv_select_photo_from_gallery).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, REQUEST_CODE_GALLERY_PHOTO);
			}
		});

		popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}

	final int REQUEST_CODE_TAKE_PHOTO = 11; // 照相
	final int REQUEST_CODE_GALLERY_PHOTO = 12; // 图库
	final int REQUEST_CODE_POINT = 14; // 节点
	final int REQUEST_CODE_TABS = 15; // 标签
	final int REQUEST_CODE_CATALOG = 16; // 目录
	final int REQUEST_CODE_ADDRESOURCR = 17; // 组合添加
	final int REQUEST_CODE_SELECT_CITY = 18;// 地址修改
	final int REQUEST_CODE_ADD_CITY = 19;// 地址添加
	final int REQUEST_CODE_PRODUCT_CATEGORY = 21;// 产品类型
	final int REQUEST_CODE_PRODUCT_GRADE = 22;// 产品星级
	final int REQUEST_CODE_PRODUCT_INTRODUCE = 23;// 产品介绍
	final int REQUEST_CODE_PRODUCT_STANDARD = 24;// 商品规格
	final int REQUEST_CODE_PRODUCT_DISTRIBUT = 25;// 分销

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)
			switch (requestCode) {
			case REQUEST_CODE_TAKE_PHOTO: // 拍照
				String imgPath = null;
				if (data.getData() != null) {
					Uri originalUri = data.getData(); // 获得图片的uri
					imgPath = "file://" + BitMapUtil.getRealPathFromURI(ProductEditActivity.this, originalUri);
				} else if (data.getExtras() != null && data.getExtras().get("data") != null) {
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						Bitmap photoBitmap = (Bitmap) bundle.get("data");
						String savapathString = KooniaoApplication.getInstance().getPicDir() + System.currentTimeMillis() + ".jpg";
						if (saveImage(photoBitmap, savapathString)) {
							imgPath = "file://" + savapathString;
						}
					}
				}
				if (imgPath != null) {
					final String uploadPath = imgPath;
					ApiCaller.getInstance().uploadTempImage(uploadPath, new ApiCaller.APIAgreementResultCallback() {
						@Override
						public void result(String errMsg, String content) {
							if (errMsg == null) {
								for (int i = 0; i < pictureLayout.getChildCount(); i++) {
									DeleteImageView image = (DeleteImageView) pictureLayout.getChildAt(i);
									if (image.getImagePath().equals(uploadPath)) {
										imageList.add(content);
										image.setObjectList(imageList);
										image.setObject(content);
										image.dissProgress();
									}
								}
							} else {
								Toast.makeText(ProductEditActivity.this, errMsg, Toast.LENGTH_SHORT).show();
							}
						}
					});

					DeleteImageView imageView = new DeleteImageView(ProductEditActivity.this);
					imageView.showProgress();
					imageView.setLayoutParams(new LinearLayout.LayoutParams(pictureLayout.getHeight(), pictureLayout.getHeight()));
					imageView.setImagePath(imgPath);
					pictureLayout.addView(imageView, pictureLayout.getChildCount() - 1);
					pictureLayout.post(new Runnable() {

						@Override
						public void run() {
							hs.scrollTo(pictureLayout.getWidth(), 0);
						}
					});
				}

				break;
			case REQUEST_CODE_GALLERY_PHOTO: // 图库选择
				if (data != null) {
					Uri originalUri = data.getData(); // 获得图片的uri
					final String imgPath1 = "file://" + BitMapUtil.getRealPathFromURI(ProductEditActivity.this, originalUri);

					// Bitmap bitmap =
					// BitMapUtil.loadBitmap(ProductEditActivity.this, imgPath);
					ApiCaller.getInstance().uploadTempImage(imgPath1, new ApiCaller.APIAgreementResultCallback() {
						@Override
						public void result(String errMsg, String content) {
							if (errMsg == null) {
								for (int i = 0; i < pictureLayout.getChildCount() - 1; i++) {
									DeleteImageView image = (DeleteImageView) pictureLayout.getChildAt(i);
									if (image.getImagePath().equals(imgPath1)) {

										imageList.add(content);
										image.setObjectList(imageList);
										image.setObject(content);
										image.dissProgress();
									}
								}
							} else {
								Toast.makeText(ProductEditActivity.this, errMsg, Toast.LENGTH_SHORT).show();
							}
						}
					});
					DeleteImageView imageView = new DeleteImageView(ProductEditActivity.this);
					imageView.showProgress();
					imageView.setLayoutParams(new LinearLayout.LayoutParams(pictureLayout.getHeight(), pictureLayout.getHeight()));
					// imageView.setImageBitmap(bitmap);
					imageView.setImagePath(imgPath1);
					pictureLayout.addView(imageView, pictureLayout.getChildCount() - 1);
					pictureLayout.post(new Runnable() {

						@Override
						public void run() {
							hs.scrollTo(pictureLayout.getWidth(), 0);
						}
					});
				}
				break;
			case REQUEST_CODE_TABS:// 标签
				mTabs = (List<String>) data.getSerializableExtra(Define.DATA);
				String cpbqstring = "";
				if (mTabs.size() > 0) {
					for (String t : mTabs) {
						cpbqstring = cpbqstring + t + "，";
					}
					tabsText.setText(cpbqstring.substring(0, cpbqstring.length() - 1));
					tabsText.setTextColor(Color.parseColor("#16B8EB"));
				} else {
					tabsText.setText("请输入产品标签");
					tabsText.setTextColor(Color.parseColor("#d0d0d0"));
				}
				break;
			case REQUEST_CODE_CATALOG:// 目录
				productInfo.setCatalogList((List<ProductCatalog>) data.getSerializableExtra(Define.DATA));
				if (productInfo.getCatalogList().size() > 0) {
					catalogText.setText(listCatalogTitletoString());
					catalogText.setTextColor(Color.parseColor("#16B8EB"));
				} else {
					catalogText.setText("请选择产品所属目录");
					catalogText.setTextColor(Color.parseColor("#d0d0d0"));
				}
				break;
			case REQUEST_CODE_ADDRESOURCR:// 组合添加
				List<ProductResource> newAddResources = (List<ProductResource>) data.getSerializableExtra(Define.DATA);
				// resource_layout.removeViews(0,
				// resource_layout.getChildCount() - 1);
				for (ProductResource productResource : newAddResources) {
					DeleteImageView resourceImage = new DeleteImageView(ProductEditActivity.this);
					resourceImage.setObject(productResource);
					if (addedProductResourceList.size() < 20) {
						addedProductResourceList.add(productResource);
						resourceImage.setProductResourceList(addedProductResourceList);
						resourceImage.setLayoutParams(new LinearLayout.LayoutParams(resource_layout.getHeight(), resource_layout.getHeight() + 15));
						resourceImage.setImagePath(productResource.getLogo());
						resource_layout.addView(resourceImage, resource_layout.getChildCount() - 1);
					} else {
						showToast("产品组合最多搭配20个产品！");
						break;
					}
				}
				break;
			case REQUEST_CODE_SELECT_CITY:// 地址修改
				if (resultCode == RESULT_OK && data != null) {
					areaList = (List<Area>) data.getSerializableExtra(Define.DATA);
					resetCountNum();
				}
				break;
			case REQUEST_CODE_PRODUCT_GRADE:// 星级修改
				levelsetText.setText(data.getStringExtra(Define.DATA));
				levelsetText.setTag(data.getIntExtra(Define.ID, 0));
				changeVisibility();
				break;
			case REQUEST_CODE_PRODUCT_INTRODUCE:// 介绍修改
				mProductIntroduceString = data.getStringExtra(Define.DATA);
				break;
			case REQUEST_CODE_PRODUCT_STANDARD:// 商品规格
				paramsMaps = (HashMap<String, String>) data.getSerializableExtra(Define.DATA);
				productInfo = (ProductInfo) data.getSerializableExtra(Define.PRODUCT_INFO);
				resetCountNum();
				break;
			case REQUEST_CODE_PRODUCT_DISTRIBUT:// 分销
				if (resultCode == RESULT_OK && data != null) {
					disributenumText.setText("已设置");
					settingWay = data.getStringExtra(Define.SETTING_WAY);
					String paramsString = "0";
					productInfo.setAffiliateStatus(1);
					if (data.hasExtra(Define.PARAMS_SETTING))
						paramsSetting = data.getStringExtra(Define.PARAMS_SETTING);
					if (data.hasExtra(Define.PARAMS_SETTING_SUM))
						paramsSettingSum = data.getStringExtra(Define.PARAMS_SETTING_SUM);
					if (data.hasExtra(Define.TEMPLATE_ID))
						selectTemplateId = data.getIntExtra(Define.TEMPLATE_ID, 0);
					if (settingWay != null) {
						String typeString = settingWay.equals(SettingWay.MANUAL.type) ? "1" : "2";
						if (typeString.equals("1"))
							paramsString = paramsSetting.equals(ParamsSetting.SUM.type) ? "0" : "1";
						String commission_string = "{  \"type\": " + typeString + ",    \"affiliate_commission\": " + paramsSettingSum + ",    \"is_percentage\":" + paramsString + ",     \"affiliate_commission_percentage\": " + paramsSettingSum + ",     \"commission_theme\":" + selectTemplateId + "  }";
						paramsMaps.put("commission", commission_string);
					}
				}
				break;
			}
		if (checkNextClickable()) {
			next_stepView.setClickable(true);
			next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
		} else {
			next_stepView.setClickable(false);
			next_stepView.setBackgroundResource(R.color.vd0d0d0);
		}
	}

	String settingWay;// 设置方式，（手动、模板）
	String paramsSetting; // 手动设置的何种方式（按金额、按比例）
	String paramsSettingSum; // 手动设置方式的输入的金额
	int selectTemplateId;// 模板id
	private List<String> imageList = new ArrayList<>();

	public void addpictrue(Bitmap bitmap) {
		DeleteImageView imageView = new DeleteImageView(ProductEditActivity.this);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(pictureLayout.getHeight(), pictureLayout.getHeight()));
		imageView.setImageBitmap(bitmap);
		pictureLayout.addView(imageView, pictureLayout.getChildCount() - 1);
		pictureLayout.post(new Runnable() {

			@Override
			public void run() {
				hs.scrollTo(pictureLayout.getWidth(), 0);
			}
		});

	}

	/**
	 * 其他信息
	 */
	@SuppressLint("NewApi")
	@Click(R.id.ll_other_info)
	void onQtxxClick() {
		if (otherallLayout.getVisibility() == View.GONE) {
			otherTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.list_up_detail, 0);
			otherallLayout.setVisibility(View.VISIBLE);
		} else {
			otherTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.list_down_detail, 0);
			otherallLayout.setVisibility(View.GONE);
		}
	}

	private KooniaoDatePickerDialog datePickerDialog;

	/**
	 * 起始时间
	 */
	@Click(R.id.tv_starttime)
	void onQssjClick() {
		Date dt = new Date(ApiCaller.TimeInService * 1000);
		if (!startimeText.getText().toString().equals("起始时间")) {
			dt = new Date(DateUtil.strToTimestamp(startimeText.getText().toString(), "yyyy-MM-dd") * 1000);
		}
		datePickerDialog = new KooniaoDatePickerDialog(ProductEditActivity.this, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				String timeString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
				startimeText.setText(timeString);
			}
		}, dt.getYear() + 1900, dt.getMonth(), dt.getDate());
		datePickerDialog.getDatePicker().setMinDate(ApiCaller.TimeInService * 1000);

		datePickerDialog.show();
	}

	/**
	 * 结束时间
	 */
	@Click(R.id.tv_endtime)
	void onJssjClick() {
		if (!startimeText.getText().toString().equals("起始时间")) {
			Date dt = new Date(DateUtil.strToTimestamp(startimeText.getText().toString(), "yyyy-MM-dd") * 1000);
			if (!endtimeText.getText().toString().equals("结束时间")) {
				dt = new Date(DateUtil.strToTimestamp(endtimeText.getText().toString(), "yyyy-MM-dd") * 1000);
			}
			datePickerDialog = new KooniaoDatePickerDialog(ProductEditActivity.this, new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					String timeString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
					endtimeText.setText(timeString);
				}
			}, dt.getYear() + 1900, dt.getMonth(), dt.getDate());
			datePickerDialog.getDatePicker().setMinDate(ApiCaller.TimeInService * 1000);
			datePickerDialog.show();
		} else {
			showToast("请您选择自动上架的起始时间！");
		}
	}

	/**
	 * 产品标签
	 */
	@Click(R.id.ll_prodcut_tab)
	void onCpbqClick() {
		Intent intent = new Intent(ProductEditActivity.this, ProductTabActivity_.class);
		if (mTabs != null)
			intent.putExtra(Define.DATA, (Serializable) mTabs);
		startActivityForResult(intent, REQUEST_CODE_TABS);
	}

	/**
	 * 产品目录
	 */
	@Click(R.id.ll_product_catalog)
	void onCpmlClick() {
		Intent intent = new Intent(ProductEditActivity.this, ProductCatalogActivity_.class);
		intent.putExtra(Define.DATA, (Serializable) productInfo.getCatalogList());
		startActivityForResult(intent, REQUEST_CODE_CATALOG);
	}

	private List<ProductResource> addedProductResourceList = new ArrayList<>(); // 已添加列表数据

	/**
	 * 组合产品添加点击
	 */
	@Click(R.id.add_resource)
	void onAddResource1Click() {
		if (addedProductResourceList.size() >= 20) {
			showToast("产品组合最多搭配20个产品！");
		} else {
			Intent intent = new Intent(ProductEditActivity.this, ProductAddSelectActivity_.class);
			startActivityForResult(intent, REQUEST_CODE_ADDRESOURCR);
		}

	}

	/**
	 * 组合产品添加点击
	 */
	@Click(R.id.add_resource_in)
	void onAddResource2Click() {
		Intent intent = new Intent(ProductEditActivity.this, ProductAddSelectActivity_.class);
		intent.putExtra(Define.DATA, (Serializable) addedProductResourceList);
		startActivityForResult(intent, REQUEST_CODE_ADDRESOURCR);
	}

	private List<SubmitArea> submitAreas = new ArrayList<>();

	public class SubmitArea {
		public int country_id;
		public int province_id = 0;
		public int city_id;
	}

	/**
	 * 编辑产品地址点击
	 */
	@Click(R.id.ll_address_edit)
	void onPutAddressClick() {
		Intent intent = new Intent(ProductEditActivity.this, ProductAreaEditActivity_.class);

		intent.putExtra(Define.DATA, (Serializable) areaList);
		startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);

	}

	/**
	 * 商品规格点击
	 */
	@Click(R.id.ll_standard_edit)
	void onSetStandardClick() {
		Intent intent = new Intent(ProductEditActivity.this, InventoryPricingActivity_.class);
		intent.putExtra(Define.PRODUCT_INFO, productInfo);
		intent.putExtra(Define.MODE, "editProduct");
		intent.putExtra(Define.DATA, paramsMaps);
		intent.putExtra(Define.TIME_STAMP, ApiCaller.TimeInService);
		startActivityForResult(intent, REQUEST_CODE_PRODUCT_STANDARD);

	}

	/**
	 * 分销设置点击
	 */
	@Click(R.id.ll_distribution_edit)
	void onSetDistributionClick() {
		Intent intent = new Intent(ProductEditActivity.this, DistributionSetActivity_.class);
		intent.putExtra(Define.PID, pid);
		if (productInfo.getAffiliateStatus() != 0) {
			String typeString = settingWay.equals(SettingWay.MANUAL.type) ? "1" : "2";
			String paramsString = "0";
			if (typeString.equals("1"))
				paramsString = paramsSetting.equals(ParamsSetting.SUM.type) ? "0" : "1";
			intent.putExtra(Define.SETTING_WAY, typeString.equals("1") ? SettingWay.MANUAL.type : SettingWay.TEMPLATE.type);
			intent.putExtra(Define.PARAMS_SETTING, paramsString.equals("1") ? ParamsSetting.PERCENT.type : ParamsSetting.SUM.type);
			intent.putExtra(Define.PARAMS_SETTING_SUM, paramsSettingSum);
			intent.putExtra(Define.TEMPLATE_ID, selectTemplateId);
		}
		intent.putExtra(Define.MODE, "edit");
		startActivityForResult(intent, REQUEST_CODE_PRODUCT_DISTRIBUT);

	}

	/**
	 * 景点星级点击
	 */
	@Click(R.id.scene_starlevel_layout)
	void onJdxjClick() {
		Intent intent = new Intent(ProductEditActivity.this, ProductStarLevelActivity_.class);
		intent.putExtra(Define.DATA, starlevelText.getText().toString().replace("星级", ""));
		startActivityForResult(intent, REQUEST_CODE_PRODUCT_GRADE);
	}

	/**
	 * 产品介绍点击
	 */
	@Click(R.id.tv_product_introdruce)
	void onCpjsClick() {
		Intent intent = new Intent(ProductEditActivity.this, ProductIntroduceBrowserActivity_.class);
		intent.putExtra(Define.DATA, productInfo.getIntroduction());
		startActivity(intent);
	}

	private int webappDefault = 0;

	/**
	 * 线路点击
	 */
	@Click(R.id.radio_product_line)
	void onLineClick() {
		webappDefault = 1;
	}

	/**
	 * 详情点击
	 */
	@Click(R.id.radio_product_context)
	void onContextClick() {
		webappDefault = 2;
	}

	/**
	 * 评价点击
	 */
	@Click(R.id.radio_product_evaluate)
	void onEvaluateClick() {
		webappDefault = 3;
	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (checkNextClickable()) {
				next_stepView.setClickable(true);
				next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
			} else {
				next_stepView.setClickable(false);
				next_stepView.setBackgroundResource(R.color.vd0d0d0);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	public boolean checkNextClickable() {
		if (imageList.size() == 0) {
			return false;
		}
		if (checkTextView(productnameEditText)) {
			return false;
		}

		if (checkTextView(phonenumberEditText)) {
			return false;
		}
		if (productInfo.getProductClass_0Name().equals("组合产品")) {
			if (addedProductResourceList.size() < 2) {
				return false;
			}
		}
		return true;
	}

	public boolean checkTextView(TextView t) {
		String s = t.getText().toString();
		s = s.replace(" ", "");
		if (!s.equals(""))
			return false;
		else
			return true;
	}

	private HashMap<String, String> paramsMaps;

	/**
	 * 点击下一步
	 */
	@Click(R.id.tv_next_step)
	void onNextClick() {
		if (productnameEditText.getText().toString().length() < 3) {
			showToast("产品名称长度不得小于3！");
			return;
		}
		if (!startimeText.getText().toString().equals("起始时间")) {
			if (ApiCaller.TimeInService - DateUtil.strToTimestamp(startimeText.getText().toString(), "yyyy-MM-dd") > 60 * 60 * 24) {
				showToast("上架时间不得小于今天！");
				return;
			}
		}
		if (!endtimeText.getText().toString().equals("结束时间")) {
			long endtime = DateUtil.strToTimestamp(endtimeText.getText().toString(), "yyyy-MM-dd");
			long starttime = DateUtil.strToTimestamp(startimeText.getText().toString(), "yyyy-MM-dd");
			if (endtime - starttime < 0) {
				showToast("下架时必须大过上架时间！");
				return;
			}
		}
		if (mProductIntroduceString == null || mProductIntroduceString.equals("")) {
			showToast("产品介绍不得为空！");
			return;
		}
		paramsMaps.put("stock", productInfo.getStock() + "");
		paramsMaps.put("name", productnameEditText.getText().toString());
		paramsMaps.put("mobile", phonenumberEditText.getText().toString());
		paramsMaps.put("introduction", mProductIntroduceString);
		paramsMaps.put("class_0", productInfo.getProductClass_0() + "");
		paramsMaps.put("class_1", productInfo.getProductClass_1() + "");
		paramsMaps.put("class_2", productInfo.getProductClass_2() + "");
		paramsMaps.put("isShow", seeableSwitch.isChecked() ? "1" : "0");
		paramsMaps.put("status", putawayableSwitch.isChecked() ? "1" : "3");
		paramsMaps.put("startTime", String.valueOf(DateUtil.strToTimestamp(startimeText.getText().toString(), "yyyy-MM-dd")));
		paramsMaps.put("endTime", String.valueOf(DateUtil.strToTimestamp(endtimeText.getText().toString(), "yyyy-MM-dd")));
		paramsMaps.put("sku", numberEdit.getText().toString());
		paramsMaps.put("openTime", opentimeEdit.getText().toString());
		paramsMaps.put("address", adddressText.getText().toString());
		paramsMaps.put("webappDefault", webappDefault + "");
		paramsMaps.put("copyProductId", "0");
		paramsMaps.put("grade", levelsetText.getText().equals("未编辑") ? "0" : String.valueOf(levelsetText.getTag()));
		paramsMaps.put("catalog", listCatalogIdtoString());
		paramsMaps.put("imageList", JsonTools.listToJson(imageList));
		paramsMaps.put("tag", mTabs.size() == 0 ? null : JsonTools.listToJson(mTabs));
		paramsMaps.put("combineList", listResourcetoString());
		submitAreas = new ArrayList<>();
		for (int i = 0; i < areaList.size(); i++) {
			SubmitArea area = new SubmitArea();
			area.city_id = areaList.get(i).city_id.id;
			area.country_id = areaList.get(i).country_id.id;
			submitAreas.add(area);
		}
		paramsMaps.put("areaList", JsonTools.listToJson(submitAreas));

		showProgressDialog();
		ApiCaller.getInstance().saveProductInfo(paramsMaps, new APISaveProductInfoCallback() {
			@Override
			public void result(String errMsg, int flag) {
				dissmissProgressDialog();
				if (errMsg == null) {
					if (flag == 0)
						showToast("保存产品失败");
					else {
						showToast("保存成功");
						setResult(RESULT_OK);
						finish();
					}
				} else {
					showToast(errMsg);
				}
			}
		});
	}

	public void resetCountNum() {
		adddressnumText.setText(areaList.size() + "");
		standardnumText.setText(productInfo.getPackageList().size() + "");
	}

	public String listResourcetoString() {
		String s = "";
		if (addedProductResourceList != null && addedProductResourceList.size() > 0) {
			s = "[";
			for (ProductResource pr : addedProductResourceList) {
				s += pr.getPid();
				s += ",";
			}
			s = s.substring(0, s.length() - 1);
			s += "]";
		}
		return s;
	}

	public String listCatalogIdtoString() {
		String s = "";
		if (productInfo.getCatalogList() != null && productInfo.getCatalogList().size() > 0) {
			s = "[";
			for (ProductCatalog pc : productInfo.getCatalogList()) {
				s += pc.getId();
				s += ",";
			}
			s = s.substring(0, s.length() - 1);
			s += "]";
		}
		return s;
	}

	public String listCatalogTitletoString() {
		String s = "";
		for (ProductCatalog pc : productInfo.getCatalogList()) {
			s += "\"";
			s += pc.getTitle();
			s += "\"";
			s += ",";
		}
		s = s.substring(0, s.length() - 1);
		return s;
	}

	public static boolean saveImage(Bitmap photo, String spath) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(spath, false));
			photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();

	}

}
