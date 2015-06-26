package com.kooniao.travel.store;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import android.view.ViewGroup;
import android.widget.*;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.citylist.AreaListActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoDatePickerDialog;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.model.ProductCatalog;
import com.kooniao.travel.model.ProductCategory;
import com.kooniao.travel.model.ProductCategory.class_0;
import com.kooniao.travel.model.ProductCategory.class_1;
import com.kooniao.travel.model.ProductCategory.class_2;
import com.kooniao.travel.model.ProductInfo;
import com.kooniao.travel.model.ProductResource;
import com.kooniao.travel.utils.BitMapUtil;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.JsonTools;
import com.kooniao.travel.view.materialdesign.DeleteImageView;
import com.kooniao.travel.view.materialdesign.SwitchButton;

import org.androidannotations.annotations.*;

/**
 * Created by ZZD on 2015/5/24.
 */
@SuppressLint({ "ResourceAsColor", "InflateParams" })
@EActivity(R.layout.activity_product_info)
public class ProductInfoActivity extends BaseActivity {
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
	@ViewById(R.id.tv_editaddress)
	EditText adddressText; //
	// @ViewById(R.id.tv_choose_address)
	// TextView chooseaddressText; //
	@ViewById(R.id.ll_addaddress)
	View addaddressLayout; //
	@ViewById(R.id.tv_next_step)
	View next_stepView; //
	@ViewById(R.id.tv_quote)
	TextView quoteView; //
	@ViewById(R.id.quote_layout)
	TextView quoteText; //
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
	TextView levelsetText; // 默认展示页
	@ViewById(R.id.tv_product_type_layout)
	LinearLayout typeLayout; // 产品类型
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

	private ProductResource mResource;
	private ProductCategory productCategory;
	protected String mProductIntroduceString;
	private ProductInfo productInfo = new ProductInfo();
	private boolean editIntroduceable = true;

	public void initData() {
		next_stepView.setBackgroundResource(R.color.vd0d0d0);
		next_stepView.setClickable(false);
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.getSerializableExtra(Define.CATEGORY) != null) {
				productCategory = (ProductCategory) intent.getSerializableExtra(Define.CATEGORY);
			}
			if (intent.getSerializableExtra(Define.DATA) != null) {
				mResource = (ProductResource) intent.getSerializableExtra(Define.DATA);
				loadproductInfo(mResource.getPid());
				quoteView.setText("");
				quoteView.setBackgroundResource(R.drawable.quote);
				quoteText.setVisibility(View.VISIBLE);
				quoteText.setText(mResource.getName());
			} else {
				changeVisibility();
			}
		}
		picturelayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (picturelayout.getWidth() > hs.getWidth() && t1.getVisibility() == View.VISIBLE) {

					picturelayout.post(new Runnable() {

						@Override
						public void run() {
							t1.setVisibility(View.GONE);
							t2.setVisibility(View.VISIBLE);
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

				} else if (picturelayout.getWidth() <= hs.getWidth()) {
					picturelayout.post(new Runnable() {

						@Override
						public void run() {
							t1.setVisibility(View.VISIBLE);
							t2.setVisibility(View.GONE);
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
					startimeText.setText(DateUtil.timestampToStr(ApiCaller.TimeInService, "yyyy-MM-dd"));
					startimeText.setClickable(false);
				} else {
					endtimeText.setText("结束时间");
					startimeText.setText("起始时间");
					startimeText.setClickable(true);
				}
			}
		});
		putawayableSwitch.setChecked(false);
		productnameEditText.addTextChangedListener(mTextWatcher);
		phonenumberEditText.addTextChangedListener(mTextWatcher);
		startimeText.addTextChangedListener(mTextWatcher);
		final LinearLayout firstaddress = (LinearLayout) getLayoutInflater().inflate(R.layout.item_address_layout, null);
		firstaddress.findViewById(R.id.delete_address).setOnClickListener(new View.OnClickListener() {
			@SuppressLint("InflateParams")
			@Override
			public void onClick(View v) {
				if (((LinearLayout) firstaddress.getParent()).getChildCount() > 1) {
					((LinearLayout) firstaddress.getParent()).removeView(firstaddress);
					if (checkNextClickable()) {
						next_stepView.setClickable(true);
						next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
					} else {
						next_stepView.setClickable(false);
						next_stepView.setBackgroundResource(R.color.vd0d0d0);
					}
				}
			}
		});
		address_layout.addView(firstaddress);
		firstaddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickAddText = (TextView) firstaddress.findViewById(R.id.new_address);
				Intent intent = new Intent(ProductInfoActivity.this, AreaListActivity_.class);
				startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);
			}
		});
	}

	@ViewById(R.id.add_1)
	TextView t1; //
	@ViewById(R.id.add_2)
	TextView t2; //
	@ViewById(R.id.picture_layout)
	LinearLayout picturelayout; //
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
		productnameEditText.setText(productInfo.getProductName());
		phonenumberEditText.setText(productInfo.getMobile());
		mProductIntroduceString = productInfo.getIntroduction();
		editIntroduceable = false;
		long time = DateUtil.strToTimestamp(productInfo.getEndTime(), Define.FORMAT_YMD);
		if (time != 0) {
			endtimeText.setText(DateUtil.timestampToStr(time, "yyyy-MM-dd"));
		}
		time = DateUtil.strToTimestamp(productInfo.getStartTime(), Define.FORMAT_YMD);
		if (time != 0) {
			startimeText.setText(DateUtil.timestampToStr(time, "yyyy-MM-dd"));
		}
		endtimeText.setText(DateUtil.timestampToStr(time, "yyyy-MM-dd"));
		seeableSwitch.setChecked(productInfo.getIsPublic() == 1 ? true : false);
		putawayableSwitch.setChecked(productInfo.getStatus() == 1 ? true : false);
		if (productInfo.getStartTime() != null && productInfo.getStartTime().equals("")) {

		}
		numberEdit.setText(productInfo.getProductName());
		opentimeEdit.setText(productInfo.getOpenTime());
		adddressText.setText(productInfo.getAddress());
		levelsetText.setText(productInfo.getGrade() + "星");
		imageList = productInfo.getImageList();
		for (String imageString : productInfo.getImageList()) {
			DeleteImageView imageView = new DeleteImageView(ProductInfoActivity.this);
			imageView.showProgress();
			imageView.setLayoutParams(new LinearLayout.LayoutParams(picturelayout.getHeight(), picturelayout.getHeight()));
			imageView.setImagePath(imageString);
			imageView.setObjectList(imageList);
			imageView.setObject(imageString);
			imageView.dissProgress();
			picturelayout.addView(imageView, picturelayout.getChildCount() - 1);
		}

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
				DeleteImageView resourceImage = new DeleteImageView(ProductInfoActivity.this);
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
		List<ProductCatalog> catalogList = productInfo.getCatalogList();
		if (catalogList != null) {
			if (productInfo.getCatalogList().size() > 0) {
				catalogText.setText(listCatalogTitletoString());
				catalogText.setTextColor(Color.parseColor("#16B8EB"));
			}
		}

		if (productInfo.getAreaList() != null) {
			address_layout.removeAllViews();
			for (int i = 0; i < productInfo.getAreaList().size(); i++) {
				final LinearLayout initaddress = (LinearLayout) getLayoutInflater().inflate(R.layout.item_address_layout, null);
				initaddress.findViewById(R.id.delete_address).setOnClickListener(new View.OnClickListener() {
					@SuppressLint("InflateParams")
					@Override
					public void onClick(View v) {
						if (((LinearLayout) initaddress.getParent()).getChildCount() > 1) {
							((LinearLayout) initaddress.getParent()).removeView(initaddress);
							if (checkNextClickable()) {
								next_stepView.setClickable(true);
								next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
							} else {
								next_stepView.setClickable(false);
								next_stepView.setBackgroundResource(R.color.vd0d0d0);
							}
						}
					}
				});
				address_layout.addView(initaddress);
				initaddress.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						clickAddText = (TextView) initaddress.findViewById(R.id.new_address);
						Intent intent = new Intent(ProductInfoActivity.this, AreaListActivity_.class);
						startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);
					}
				});
				if (productInfo.getAreaList().get(i).country_id.bname.equals("")) {
					productInfo.getAreaList().get(i).country_id.bname = "中国";
					productInfo.getAreaList().get(i).country_id.id = 3237;
				}
				TextView initaddTextView = (TextView) initaddress.findViewById(R.id.new_address);
				initaddTextView.setTextColor(getResources().getColor(R.color.v323232));
				initaddTextView.setText(productInfo.getAreaList().get(i).country_id.bname + " " + productInfo.getAreaList().get(i).city_id.bname);
				initaddress.setTag(R.id.lv_city, productInfo.getAreaList().get(i).city_id.id);
				initaddress.setTag(R.id.lv_country, productInfo.getAreaList().get(i).country_id.id);
			}
		}

		introduceText.setText("浏览");
		introduceText.setTextColor(Color.parseColor("#16B8EB"));

		if (checkNextClickable()) {
			next_stepView.setClickable(true);
			next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
		} else {
			next_stepView.setClickable(false);
			next_stepView.setBackgroundResource(R.color.vd0d0d0);
		}

	}

	public void changeVisibility() {
		if (productCategory.cl0 != null) {
			if (productCategory.cl0.bname.equals("组合产品")) {
				starlevelLayout.setVisibility(View.GONE);
				groupLayout.setVisibility(View.VISIBLE);
				defaultshowLayout.setVisibility(View.GONE);
				typeLayout.setVisibility(View.GONE);
				productcatalogText.setText(productCategory.cl0.bname);
				if (mResource != null) {
					quoteView.setVisibility(View.VISIBLE);
				} else {
					quoteView.setVisibility(View.GONE);
				}

			}
			if (productCategory.cl0.bname.equals("线路产品")) {
				starlevelLayout.setVisibility(View.GONE);
				quoteView.setVisibility(View.VISIBLE);
				groupLayout.setVisibility(View.GONE);
				defaultshowLayout.setVisibility(View.GONE);
				typeLayout.setVisibility(View.VISIBLE);
				productcatalogText.setText(productCategory.cl0.bname + "-" + productCategory.cl1.bname + "-" + productCategory.cl2.bname);
			}
			if (productCategory.cl0.bname.equals("单一产品")) {
				starlevelLayout.setVisibility(View.VISIBLE);
				groupLayout.setVisibility(View.GONE);
				quoteView.setVisibility(View.VISIBLE);
				defaultshowLayout.setVisibility(View.GONE);
				typeLayout.setVisibility(View.VISIBLE);
				if (productCategory.cl1.bname.equals("酒店")) {
					starlevelText.setText("酒店星级");
				} else if (productCategory.cl1.bname.equals("景点门票")) {
					starlevelText.setText("景点星级");
				} else {
					starlevelLayout.setVisibility(View.GONE);
				}

				productcatalogText.setText(productCategory.cl0.bname + "-" + productCategory.cl1.bname + "-" + productCategory.cl2.bname);
			}
		}

	}

	/**
	 * 获取产品详情
	 */
	private void loadproductInfo(int pid) {
		showProgressDialog();
		ProductManager.getInstance().loadProductInfo(pid, new ApiCaller.APIProductInfoResultCallback() {
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
			for (class_0 c_0 : productCategory.classList.class_0) {
				if (c_0.id.equals(productInfo.getProductClass_0() + ""))
					productCategory.cl0 = c_0;
				for (class_1 c_1 : c_0.class_1) {
					if (c_1.equals(productInfo.getProductClass_1() + ""))
						productCategory.cl1 = c_1;
					for (class_2 c_2 : c_1.class_2) {
						if (c_2.equals(productInfo.getProductClass_2() + ""))
							productCategory.cl2 = c_2;
					}
				}
			}
			if (productCategory.cl1 == null) {
				productCategory.cl1 = new ProductCategory().new class_1();
				productCategory.cl1.id = productInfo.getProductClass_1() + "";
				productCategory.cl1.bname = productInfo.getProductClass_1Name();
			}
			if (productCategory.cl2 == null) {
				productCategory.cl2 = new ProductCategory().new class_2();
				productCategory.cl2.id = productInfo.getProductClass_2() + "";
				productCategory.cl2.bname = productInfo.getProductClass_2Name();
			}
			changeVisibility();
			initView();
		} else {
			Toast.makeText(ProductInfoActivity.this, errMsg, Toast.LENGTH_SHORT).show();
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
	private void popupAvatarSelectView() {
		if (picturelayout.getChildCount() >= 11) {
			showToast("产品图片最多10张！");
			return;
		}
		View selectPhotoView = LayoutInflater.from(ProductInfoActivity.this).inflate(R.layout.popupwindow_avatar_select, null);
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
	final int REQUEST_CODE_AROUD = 13; // 线路
	final int REQUEST_CODE_POINT = 14; // 节点
	final int REQUEST_CODE_TABS = 15; // 标签
	final int REQUEST_CODE_CATALOG = 16; // 目录
	final int REQUEST_CODE_ADDRESOURCR = 17; // 组合添加
	final int REQUEST_CODE_SELECT_CITY = 18;// 地址修改
	final int REQUEST_CODE_ADD_CITY = 19;// 地址添加
	final int REQUEST_CODE_PRODUCT_CATEGORY = 21;// 产品类型
	final int REQUEST_CODE_PRODUCT_GRADE = 22;// 产品星级
	final int REQUEST_CODE_PRODUCT_INTRODUCE = 23;// 产品介绍
	final int REQUEST_CODE_PRODUCT_ADDSUCCESS = 24;// 产品添加完成

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)
			switch (requestCode) {
			case REQUEST_CODE_TAKE_PHOTO: // 拍照
				String imgPath = null;
				if (data.getData() != null) {
					Uri originalUri = data.getData(); // 获得图片的uri
					imgPath = "file://" + BitMapUtil.getRealPathFromURI(ProductInfoActivity.this, originalUri);
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
								for (int i = 0; i < picturelayout.getChildCount(); i++) {
									DeleteImageView image = (DeleteImageView) picturelayout.getChildAt(i);
									if (image.getImagePath().equals(uploadPath)) {
										imageList.add(content);
										image.setObjectList(imageList);
										image.setObject(content);
										image.dissProgress();
									}
								}
							} else {
								Toast.makeText(ProductInfoActivity.this, errMsg, Toast.LENGTH_SHORT).show();
							}
						}
					});

					DeleteImageView imageView = new DeleteImageView(ProductInfoActivity.this);
					imageView.showProgress();
					imageView.setLayoutParams(new LinearLayout.LayoutParams(picturelayout.getHeight(), picturelayout.getHeight()));
					// imageView.setImageBitmap(bitmap);
					imageView.setImagePath(imgPath);
					picturelayout.addView(imageView, picturelayout.getChildCount() - 1);
					picturelayout.post(new Runnable() {

						@Override
						public void run() {
							hs.scrollTo(picturelayout.getWidth(), 0);
						}
					});
					// addpictrue(bitmap);
				}

				break;
			case REQUEST_CODE_GALLERY_PHOTO: // 图库选择
				if (data != null) {
					Uri originalUri = data.getData(); // 获得图片的uri
					final String imgPath1 = "file://" + BitMapUtil.getRealPathFromURI(ProductInfoActivity.this, originalUri);

					// Bitmap bitmap =
					// BitMapUtil.loadBitmap(ProductInfoActivity.this, imgPath);
					ApiCaller.getInstance().uploadTempImage(imgPath1, new ApiCaller.APIAgreementResultCallback() {
						@Override
						public void result(String errMsg, String content) {
							if (errMsg == null) {
								for (int i = 0; i < picturelayout.getChildCount() - 1; i++) {
									DeleteImageView image = (DeleteImageView) picturelayout.getChildAt(i);
									if (image.getImagePath().equals(imgPath1)) {

										imageList.add(content);
										image.setObjectList(imageList);
										image.setObject(content);
										image.dissProgress();
									}
								}
							} else {
								Toast.makeText(ProductInfoActivity.this, errMsg, Toast.LENGTH_SHORT).show();
							}
						}
					});
					DeleteImageView imageView = new DeleteImageView(ProductInfoActivity.this);
					imageView.showProgress();
					imageView.setLayoutParams(new LinearLayout.LayoutParams(picturelayout.getHeight(), picturelayout.getHeight()));
					// imageView.setImageBitmap(bitmap);
					imageView.setImagePath(imgPath1);
					picturelayout.addView(imageView, picturelayout.getChildCount() - 1);
					picturelayout.post(new Runnable() {

						@Override
						public void run() {
							hs.scrollTo(picturelayout.getWidth(), 0);
						}
					});
				}
				break;
			case REQUEST_CODE_AROUD:// 线路引用
				seleceedAround = (Around) data.getSerializableExtra(Define.DATA);
				defaultshowLayout.setVisibility(View.VISIBLE);
				setQuoteView();
				break;
			case REQUEST_CODE_POINT:// 节点引用
				seleceedAround = (Around) data.getSerializableExtra(Define.DATA);
				setQuoteView();
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
					DeleteImageView resourceImage = new DeleteImageView(ProductInfoActivity.this);
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
					String cityName = data.getStringExtra(Define.SELECTED_AREA_STRING);
					Area area = new Area();
					area.country_id = data.getIntExtra(Define.SELECTED_AREA_ID, 0);
					area.city_id = data.getIntExtra(Define.SELECTED_SUB_AREA_ID, 0);
					((LinearLayout) ((LinearLayout) clickAddText.getParent()).getParent()).setTag(R.id.lv_city, area.city_id);
					((LinearLayout) ((LinearLayout) clickAddText.getParent()).getParent()).setTag(R.id.lv_country, area.country_id);
					clickAddText.setText(cityName);
					clickAddText.setTextColor(getResources().getColor(R.color.v323232));
				}
				break;
			case REQUEST_CODE_PRODUCT_CATEGORY:// 分类修改
				productCategory.cl2 = (ProductCategory.class_2) data.getSerializableExtra(Define.CLASS2);
				// levelsetText.setText("未编辑 ");
				// levelsetText.setTag(null);
				productcatalogText.setText(productCategory.cl0.bname + "-" + productCategory.cl1.bname + "-" + productCategory.cl2.bname);
				break;
			case REQUEST_CODE_PRODUCT_GRADE:// 星级修改
				levelsetText.setText(data.getStringExtra(Define.DATA));
				levelsetText.setTag(data.getIntExtra(Define.ID, 0));
				break;
			case REQUEST_CODE_PRODUCT_INTRODUCE:// 介绍修改
				mProductIntroduceString = data.getStringExtra(Define.DATA);
				break;
			case REQUEST_CODE_PRODUCT_ADDSUCCESS:// 添加成功
				if (data.hasExtra(Define.PRODUCT_INFO)) {
					productInfo=(ProductInfo) data.getSerializableExtra(Define.PRODUCT_INFO);
				}
				else {
					setResult(RESULT_OK,data);
				finish();
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

	private List<String> imageList = new ArrayList<>();

	public void addpictrue(Bitmap bitmap) {
		DeleteImageView imageView = new DeleteImageView(ProductInfoActivity.this);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(picturelayout.getHeight(), picturelayout.getHeight()));
		imageView.setImageBitmap(bitmap);
		picturelayout.addView(imageView, picturelayout.getChildCount() - 1);
		picturelayout.post(new Runnable() {

			@Override
			public void run() {
				hs.scrollTo(picturelayout.getWidth(), 0);
			}
		});

	}

	private Around seleceedAround;
	private QuoteInfo mQuoteInfo;

	public void setQuoteView() {
		if (seleceedAround != null) {
			quoteView.setText("");
			quoteView.setBackgroundResource(R.drawable.quote);
			quoteText.setVisibility(View.GONE);
			quoteText.setText(seleceedAround.getName());
		}
		locationNode(seleceedAround.getId(), seleceedAround.getType());
	}

	/**
	 * 获取引用信息
	 */
	private void locationNode(int pid, String type) {
		showProgressDialog();
		String typeString;
		if (type.contains("线路")) {
			typeString = "plan";
		} else if (type.contains("酒店")) {
			typeString = "hotel";
		} else if (type.contains("景点")) {
			typeString = "location";
		} else {
			typeString = "lifestyle";
		}
		ApiCaller.getInstance().loadQuoteInfo(pid, typeString, new ApiCaller.APIQuoteInfoCallback() {
			@Override
			public void result(String errMsg, QuoteInfo quoteInfo) {
				dissmissProgressDialog();
				if (errMsg == null) {
					mQuoteInfo = quoteInfo;
					mProductIntroduceString = quoteInfo.introduction;
					if (mProductIntroduceString != null && !mProductIntroduceString.equals("")) {
						introduceText.setText("浏览");
						introduceText.setTextColor(Color.parseColor("#16B8EB"));
						editIntroduceable = false;
					}
					webappDefault = 1;
					productnameEditText.setText(quoteInfo.name);

					imageList = quoteInfo.imageList;
					for (String imagepath : imageList) {
						if (picturelayout.getChildCount() >= 11) {
							break;
						}
						DeleteImageView imageView = new DeleteImageView(ProductInfoActivity.this);
						imageView.showProgress();
						imageView.setLayoutParams(new LinearLayout.LayoutParams(picturelayout.getHeight(), picturelayout.getHeight()));
						// imageView.setImageBitmap(bitmap);
						imageView.setImagePath(imagepath);
						imageView.dissProgress();
						picturelayout.addView(imageView, picturelayout.getChildCount() - 1);
						picturelayout.post(new Runnable() {

							@Override
							public void run() {
								hs.scrollTo(picturelayout.getWidth(), 0);
							}
						});
					}

				} else {
					Toast.makeText(ProductInfoActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public class QuoteInfo {
		public List<String> imageList;
		public String name;
		public String introduction;
	}

	/**
	 * 引用点击
	 */
	@Click(R.id.tv_quote)
	void onQuoteClick() {
		if (quoteView.getText().equals("+引用")) {
			if (productCategory != null) {
				if (productCategory.cl0.bname.equals("线路产品")) {
					Intent intent = new Intent(ProductInfoActivity.this, LineReferenceActivity_.class);
					startActivityForResult(intent, REQUEST_CODE_AROUD);
				} else {
					String typeString;
					Intent intent = new Intent(ProductInfoActivity.this, PublicReferenceActivity_.class);
					if (productCategory.cl1.bname.contains("酒店")) {
						typeString = "hotel";
					} else if (productCategory.cl1.bname.contains("景点")) {
						typeString = "scenic";
					} else {
						typeString = "lifestyle";
					}
					intent.putExtra(Define.REFERENCE_TYPE, typeString);
					startActivityForResult(intent, REQUEST_CODE_POINT);
				}
			}
		} else {
			if (quoteText.getVisibility() == View.VISIBLE) {
				quoteText.setVisibility(View.GONE);
			} else {
				quoteText.setVisibility(View.VISIBLE);
			}
		}
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
		datePickerDialog = new KooniaoDatePickerDialog(ProductInfoActivity.this, new DatePickerDialog.OnDateSetListener() {

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
			datePickerDialog = new KooniaoDatePickerDialog(ProductInfoActivity.this, new DatePickerDialog.OnDateSetListener() {

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

	private List<String> mTabs = new ArrayList<String>();;

	/**
	 * 产品标签
	 */
	@Click(R.id.ll_prodcut_tab)
	void onCpbqClick() {
		Intent intent = new Intent(ProductInfoActivity.this, ProductTabActivity_.class);
		if (mTabs != null)
			intent.putExtra(Define.DATA, (Serializable) mTabs);
		startActivityForResult(intent, REQUEST_CODE_TABS);
	}

	private String catalog_titile;// 产品目录名称
	private int catalog_cid;// 产品目录id

	/**
	 * 产品目录
	 */
	@Click(R.id.ll_product_catalog)
	void onCpmlClick() {
		Intent intent = new Intent(ProductInfoActivity.this, ProductCatalogActivity_.class);
		intent.putExtra(Define.TITLE, catalog_titile);
		intent.putExtra(Define.CID, catalog_cid);
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
			Intent intent = new Intent(ProductInfoActivity.this, ProductAddSelectActivity_.class);
			startActivityForResult(intent, REQUEST_CODE_ADDRESOURCR);
		}

	}

	/**
	 * 组合产品添加点击
	 */
	@Click(R.id.add_resource_in)
	void onAddResource2Click() {
		Intent intent = new Intent(ProductInfoActivity.this, ProductAddSelectActivity_.class);
		intent.putExtra(Define.DATA, (Serializable) addedProductResourceList);
		startActivityForResult(intent, REQUEST_CODE_ADDRESOURCR);
	}

	private TextView clickAddText;

	@ViewById(R.id.address_layout)
	LinearLayout address_layout;

	private List<Area> areas = new ArrayList<>();

	public class Area {
		public int country_id;
		public int province_id = 0;
		public int city_id;
	}

	/**
	 * 添加产品地址点击
	 */
	@Click(R.id.ll_addaddress)
	void onAddAddressClick() {
		boolean add = true;
		for (int i = 0; i < address_layout.getChildCount(); i++) {
			if (address_layout.getChildAt(i).getTag(R.id.lv_city) == null) {
				add = false;
			}
		}
		if (add) {

			final LinearLayout newaddress = (LinearLayout) getLayoutInflater().inflate(R.layout.item_address_layout, null);
			newaddress.findViewById(R.id.delete_address).setOnClickListener(new View.OnClickListener() {
				@SuppressLint("InflateParams")
				@Override
				public void onClick(View v) {
					if (((LinearLayout) newaddress.getParent()).getChildCount() > 1) {
						((LinearLayout) newaddress.getParent()).removeView(newaddress);
						if (checkNextClickable()) {
							next_stepView.setClickable(true);
							next_stepView.setBackgroundResource(R.drawable.blue_retancle_button_selector);
						} else {
							next_stepView.setClickable(false);
							next_stepView.setBackgroundResource(R.color.vd0d0d0);
						}
					}
				}
			});
			address_layout.addView(newaddress);
			newaddress.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clickAddText = (TextView) newaddress.findViewById(R.id.new_address);
					Intent intent = new Intent(ProductInfoActivity.this, AreaListActivity_.class);
					startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);
				}
			});
		} else {
			showToast("请选择一个目的地");
		}

	}

	/**
	 * 产品类型点击
	 */
	@Click(R.id.tv_product_type)
	void onProductTypeClick()

	{
		Intent intent = new Intent(ProductInfoActivity.this, CategoryActivity_.class);
		intent.putExtra(Define.CLASS2, productCategory.cl1);
		intent.putExtra(Define.CLASS3, productCategory.cl2);
		startActivityForResult(intent, REQUEST_CODE_PRODUCT_CATEGORY);
	}

	/**
	 * 景点星级点击
	 */
	@Click(R.id.scene_starlevel_layout)
	void onJdxjClick() {
		Intent intent = new Intent(ProductInfoActivity.this, ProductStarLevelActivity_.class);
		intent.putExtra(Define.DATA, productCategory.cl1.bname);
		startActivityForResult(intent, REQUEST_CODE_PRODUCT_GRADE);
	}

	/**
	 * 产品介绍点击
	 */
	@Click(R.id.tv_product_introdruce)
	void onCpjsClick() {
		if (!editIntroduceable) {
			Intent intent = new Intent(ProductInfoActivity.this, ProductIntroduceBrowserActivity_.class);
			intent.putExtra(Define.DATA, mProductIntroduceString);
			startActivity(intent);
		} else {
			Intent intent = new Intent(ProductInfoActivity.this, ProductIntroduceActivity_.class);
			intent.putExtra(Define.DATA, mProductIntroduceString);
			startActivityForResult(intent, REQUEST_CODE_PRODUCT_INTRODUCE);
		}

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
		if (editIntroduceable) {
			if (mProductIntroduceString == null || mProductIntroduceString.equals("")) {
				introduceText.setText("未编辑");
				introduceText.setTextColor(Color.parseColor("#d0d0d0"));
				return false;
			} else {
				introduceText.setText("已编辑");
				introduceText.setTextColor(Color.parseColor("#16B8EB"));
			}
		}

		if (imageList.size() == 0)
			return false;
		if (checkTextView(productnameEditText))
			return false;
		if (checkTextView(phonenumberEditText))
			return false;
		if (productCategory.cl0.bname.equals("组合产品")) {
			if (addedProductResourceList.size() < 2)
				return false;
		}
		if (productCategory.cl0.bname.equals("单一产品")) {
			if (levelsetText.getText().equals("未编辑"))
				return false;
		}
		if (address_layout.getChildAt(0).getTag(R.id.lv_city) == null) {
			return false;
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
			if (!endtimeText.getText().toString().equals("结束时间")) {
				long endtime = DateUtil.strToTimestamp(endtimeText.getText().toString(), "yyyy-MM-dd");
				long starttime = DateUtil.strToTimestamp(startimeText.getText().toString(), "yyyy-MM-dd");
				if (endtime - starttime < 0) {
					showToast("下架时必须大过上架时间！");
					return;
				}
			}
		}
		if (mProductIntroduceString==null||mProductIntroduceString.equals("")) {
			showToast("产品介绍不得为空！");
			return;
		}
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("relativedId", seleceedAround == null ? "0" : seleceedAround.getId() + "");
		paramsMaps.put("name", productnameEditText.getText().toString());
		paramsMaps.put("mobile", phonenumberEditText.getText().toString());
		paramsMaps.put("introduction", mProductIntroduceString);
		paramsMaps.put("class_0", productCategory.cl0.id);
		paramsMaps.put("class_1", productCategory.cl1 == null ? "2" : productCategory.cl1.id);
		paramsMaps.put("class_2", productCategory.cl2 == null ? "2" : productCategory.cl2.id);
		paramsMaps.put("isShow", seeableSwitch.isChecked() ? "1" : "0");
		paramsMaps.put("status", putawayableSwitch.isChecked() ? "1" : "3");
		paramsMaps.put("startTime", String.valueOf(DateUtil.strToTimestamp(startimeText.getText().toString(), "yyyy-MM-dd")));
		paramsMaps.put("endTime", String.valueOf(DateUtil.strToTimestamp(endtimeText.getText().toString(), "yyyy-MM-dd")));
		paramsMaps.put("sku", numberEdit.getText().toString());
		paramsMaps.put("openTime", opentimeEdit.getText().toString());
		paramsMaps.put("address", adddressText.getText().toString());
		paramsMaps.put("webappDefault", webappDefault + "");
		paramsMaps.put("copyProductId", mResource == null ? "0" : mResource.getPid() + "");
		paramsMaps.put("grade", levelsetText.getText().equals("未编辑") ? "0" : String.valueOf(levelsetText.getTag()));
		paramsMaps.put("catalog", listCatalogIdtoString());
		paramsMaps.put("imageList", JsonTools.listToJson(imageList));
		paramsMaps.put("tag", mTabs.size() == 0 ? null : JsonTools.listToJson(mTabs));
		paramsMaps.put("combineList", listResourcetoString());
		areas = new ArrayList<>();
		for (int i = 0; i < address_layout.getChildCount(); i++) {
			if (address_layout.getChildAt(i).getTag(R.id.lv_city) != null) {
				Area area = new Area();
				area.city_id = (int) address_layout.getChildAt(i).getTag(R.id.lv_city);
				area.country_id = (int) address_layout.getChildAt(i).getTag(R.id.lv_country);
				areas.add(area);
			}
		}
		paramsMaps.put("areaList", JsonTools.listToJson(areas));

		Intent intent = new Intent(ProductInfoActivity.this, InventoryPricingActivity_.class);
		intent.putExtra(Define.DATA, (Serializable) paramsMaps);
		intent.putExtra(Define.PRODUCT_PRICE, productInfo.getMarketPrice());
		intent.putExtra(Define.PRODUCT_UNIT, productInfo.getUnit());
		intent.putExtra(Define.TIME_STAMP, productCategory.serviceTime);
		intent.putExtra(Define.PRODUCT_INFO, productInfo);
		startActivityForResult(intent, REQUEST_CODE_PRODUCT_ADDSUCCESS);
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
		if (productInfo.getCatalogList() != null) {

			for (ProductCatalog pc : productInfo.getCatalogList()) {
				s += pc.getTitle();
				s += ",";
			}
			s = s.substring(0, s.length() - 1);
		}
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
