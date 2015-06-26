package com.kooniao.travel.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.widget.*;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.apache.commons.lang.StringUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView.OnScrollListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.citylist.AreaListActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.CityManager;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.ProductResourceListCallback;
import com.kooniao.travel.model.AreaParent;
import com.kooniao.travel.model.ProductResource;
import com.kooniao.travel.model.SubArea;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.InputMethodUtils;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 组合产品
 *
 * @author zheng.zong.di
 * @date 2015年5月22日
 * @description 返回数据List<ProductResource> 参数(Define.Data)
 */
@SuppressLint({"HandlerLeak", "InflateParams"})
@EActivity(R.layout.activity_resource_lib)
public class ProductAddSelectActivity extends BaseActivity {

    @ViewById(R.id.tv_comprehensive_ranking)
    TextView comprehensiveRankingTextView; // 价格排序
    @ViewById(R.id.tv_advanced_ranking)
    TextView advancedRankingTextView; // 高级排序
    @ViewById(R.id.swipe_refresh_layout)
    PtrFrameLayout refreshLayout; // 下拉刷新布局
    @ViewById(R.id.lv_product)
    ListView listView;
    @ViewById(R.id.layout_no_data)
    View noDataLayout; // 无数据布局
    @ViewById(R.id.ll_ranking)
    View rankingLayout; // 筛选布局
    @ViewById(R.id.title)
    TextView titleTextView; // 标题

    @AfterViews
    void init() {
        initBaiDuLocationService();
        initData();
        initView();
        handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
        saveSubAreaList();
    }

    private ImageLoader imageLoader;
    private ProductResourceAdapter adapter;
    String locatingFailTips; // 定位失败提示信息
    String locatingTips; // 正在定位提示信息

    /**
     * 初始化数据
     */
	private void initData() {
        imageLoader = ImageLoader.getInstance();
        adapter = new ProductResourceAdapter();
        locatingFailTips = getResources().getString(R.string.locating_fail);
        locatingTips = getResources().getString(R.string.locating);
    }

    /**
     * 初始化界面
     */
    private void initView() {
    	titleTextView.setText("我的产品");
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
        listView.setOnItemClickListener(new OnItemClickListener() {

            int lastClickPosition = -1; // 上次点击的位置

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                position = position - listView.getHeaderViewsCount();
                ProductResource resource = productResources.get(position);
                resource.setAdded(true);
                if (lastClickPosition != -1) {
                    ProductResource lastAddedResource = productResources.get(lastClickPosition);
                    lastAddedResource.setAdded(false);
                }
                if (resource.isAdded()) {
                    resource.setAdded(false);
                    for (int i = 0; i < addedProductResourceList.size(); i++) {
                        ProductResource productResource = addedProductResourceList.get(i);
                        if (productResource.getPid() == resource.getPid()) {
                            addedProductResourceList.remove(productResource);
                        }
                    }
                } else {
                    resource.setAdded(true);
                    addedProductResourceList.add(resource);
                }
                adapter.notifyDataSetChanged();
                lastClickPosition = position;
            }
        });
        // 下拉刷新配置
        MaterialHeader materialHeader = new MaterialHeader(ProductAddSelectActivity.this);
        materialHeader.setPadding(0, ViewUtils.dpToPx(15, getResources()), 0, ViewUtils.dpToPx(15, getResources()));
        refreshLayout.setHeaderView(materialHeader);
        refreshLayout.addPtrUIHandler(materialHeader);
        refreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, listView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                onRefresh();
            }
        });
    }

    // 滑动监听
    private class ListViewScrollListener implements OnScrollListener {
        @Override
        public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }

        @Override
        public void onScrollStateChanged(AbsListView listview, int scrollState) {
            switch (scrollState) {
                // 当不滚动时
                case OnScrollListener.SCROLL_STATE_IDLE:
                    // 判断滚动到底部
                    if (!isLoadingMore) {
                        if (listview.getLastVisiblePosition() == (listview.getCount() - 1)) {
                            if (currentPageNum < pageCount) {
                                currentPageNum++;
                                isLoadingMore = true;
                                loadProductList();
                            } else {
                                handler.sendEmptyMessageDelayed(NORE_MORE_DATA, 100);
                            }
                        }
                    }
                    break;
            }
        }
    }

    @StringRes(R.string.no_more_data)
    String noreMoreDataTips; // 没有更多数据的提示语

    final int REFRESH_DATA = 1; // 刷新数据
    final int NORE_MORE_DATA = 2; // 没有更多数据

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_DATA: // 刷新数据
                    refreshLayout.autoRefresh(true);
                    break;

                case NORE_MORE_DATA: // 没有更多数据
                    if (isNeedToShowNoMoreTips) {
                        isNeedToShowNoMoreTips = false;
                        showToast(noreMoreDataTips);
                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private int currentPageNum = 1; // 当前页码
    private boolean isNeedToShowNoMoreTips = true; // 是否需要提示没有更多数据了

    /**
     * 下拉刷新
     */
    public void onRefresh() {
        currentPageNum = 1;
        isNeedToShowNoMoreTips = true;
        loadProductList();
    }

    private boolean isLoadingMore; // 是否正在加载更多
    private int pageCount = 0; // 总共的页数
    String sort = "desc"; // 排序(desc, asc)
    int cityId = 0; // 默认全部
    int productType = 0; // 产品类型
    float minPrice = 0; // 最小价格
    float maxPrice = Float.MAX_VALUE; // 最大价格

    /**
     * 加载产品列表
     */
    private void loadProductList() {
        if (currentPageNum > 1) {
            isLoadingMore = false;
        }

        ProductManager.getInstance().loadProductSimpleResource(currentPageNum, sort, cityId, productType, minPrice, maxPrice, new ProductResourceListCallback() {

            @Override
            public void result(String errMsg, List<ProductResource> productResourceList, int pageCount) {
                loadProductResourceComplete(errMsg, productResourceList, pageCount);
            }
        });
    }

    private List<ProductResource> productResources = new ArrayList<>(); // 列表数据
    private List<ProductResource> addedProductResourceList = new ArrayList<>(); // 已添加列表数据


    /*
     * 加载产品资源列表完成
     */
    protected void loadProductResourceComplete(String errMsg, List<ProductResource> productResourceList, int pageCount) {
        isLoadingMore = false;
        refreshLayout.refreshComplete();
        this.pageCount = pageCount;
        if (errMsg == null && productResourceList != null) {
            noDataLayout.setVisibility(View.GONE);
            if (currentPageNum == 1) {
                // 是否展示无数据布局
                if (productResourceList.isEmpty()) {
                    noDataLayout.setVisibility(View.VISIBLE);
                } else {
                    noDataLayout.setVisibility(View.GONE);
                }

                if (addedProductResourceList != null && addedProductResourceList.size() > 0)
                    for (ProductResource p : productResourceList) {
                        for (ProductResource addP : addedProductResourceList) {
                            if (p.getPid() == addP.getPid())
                                p.setAdded(true);
                        }
                    }
                // 填数据
                productResources.clear();
                productResources = productResourceList;
            } else {
                productResources.addAll(productResourceList);
            }

            adapter.notifyDataSetChanged();
        } else {
        	showToast(errMsg);
        }
    }

    CountDownTimer countDownTimer;

    /**
     * 开始定位
     */
    @UiThread
    void startLocating() {
        initLocation();
        locationClient.start();
        /**
         * 定位倒计时，10s后停止定位
         */
        countDownTimer = new CountDownTimer(10 * 1000, 1 * 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (locationClient != null) {
                    locationClient.stop();
                }

                String locateString = locationTextView.getText().toString();
                if (locateString.equals(locatingTips)) {
                    // 设置定位失败
                    locationTextView.setText(locatingFailTips);
                    reLocatingImageView.clearAnimation(); // 停止动画
                }
            }
        };
        countDownTimer.start();
    }

    /**
     * 定位参数
     */
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";
    public LocationClient locationClient; // 百度定位
    public KooniaoLocationListener locationListener; // 定位监听

    /**
     * 初始化定位参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);// 设置定位模式
        option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true);
        option.setTimeOut(10 * 1000); // 定位超时
        locationClient.setLocOption(option);
    }

    /**
     * 初始化百度定位服务
     */
    private void initBaiDuLocationService() {
        locationClient = new LocationClient(this.getApplicationContext());
        locationListener = new KooniaoLocationListener();
        locationClient.registerLocationListener(locationListener);
    }

    int locatingCityId = 0; // 定位城市id
    
    /**
     * 实现实位回调监听
     */
    public class KooniaoLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            String city = location.getCity();
            if (city.length() > 1) {
                city = city.substring(0, city.length() - 1);
                SubArea subArea = CityManager.getInstance().getSubAreaByName(city);
                if (subArea != null) {
                	locatingCityId = Integer.parseInt(subArea.getArea_id());
                    String cityName = subArea.getArea_name();
                    locationTextView.setText(cityName);
                    String selectedCityName = destinationTextView.getText().toString();
                    String selectCityTips = getResources().getString(R.string.please_choice_city);
                    if (selectedCityName.equals(selectCityTips)) {
                        destinationTextView.setText(cityName);
                    }
                }
            }
            onLocateFinish();
        }
    }

    /**
     * 定位结束
     */
    @UiThread
    void onLocateFinish() {
        if (locationClient != null) {
            locationClient.stop();
        }
        reLocatingImageView.clearAnimation(); // 停止动画
    }

    /**
     * 保存所有子地区
     */
    private void saveSubAreaList() {
        boolean isFirstTimeSave = AppSetting.getInstance().getBooleanPreferencesByKey(Define.IS_FIRST_TIME_SAVE_SUB_AREA, true);
        if (isFirstTimeSave) {
            AppSetting.getInstance().saveBooleanPreferencesByKey(Define.IS_FIRST_TIME_SAVE_SUB_AREA, false);
            List<AreaParent> areaParents = CityManager.getInstance().getAreaList();
            if (areaParents != null) {
                List<SubArea> subAreas = new ArrayList<>();
                for (AreaParent areaParent : areaParents) {
                    List<SubArea> subAreasTemp = areaParent.getCityList();
                    for (SubArea subArea : subAreasTemp) {
                        subArea.setParent_id(areaParent.getArea_id());
                        subArea.setParent_name(areaParent.getArea_name());
                    }
                    subAreas.addAll(subAreasTemp);
                }

                CityManager.getInstance().saveSubAreaList(subAreas);
            }
        }
    }

    /**
     * 点击后退
     */
    @Click(R.id.iv_go_back)
    void onGoBackClick() {
        Intent intent = new Intent();
        intent.putExtra(Define.DATA, (Serializable) addedProductResourceList);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 点击价格排序
     */
    @Click(R.id.ll_comprehensive_ranking)
    void onComprehensiveRankingClick() {
        if (sort.equals("desc")) { // 目前是降序
            sort = "asc";
            comprehensiveRankingTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.double_triangle_up, 0);
        } else {
            sort = "desc";
            comprehensiveRankingTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.double_triangle_down, 0);
        }

        comprehensiveRankingTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
        advancedRankingTextView.setTextColor(getResources().getColor(R.color.v909090));
        if (advanceRankingPopupLayout != null && advanceRankingPopupLayout.getVisibility() == View.VISIBLE) {
            advanceRankingPopupLayout.setVisibility(View.GONE);
        }
        handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
    }

    View advanceRankingPopupLayout; // 高级筛选弹出框

    /**
     * 点击高级筛选
     */
    @Click(R.id.ll_advanced_ranking)
    void onAdvancedRankingClick() {
        if (advanceRankingPopupLayout == null) {
            initAdvanceRankingPopupLayout();
        }

        comprehensiveRankingTextView.setTextColor(getResources().getColor(R.color.v909090));
        advancedRankingTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
        if (advanceRankingPopupLayout.getVisibility() == View.VISIBLE) {
            advanceRankingPopupLayout.setVisibility(View.GONE);
            advancedRankingTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_black_triangle_down, 0);
        } else {
            advanceRankingPopupLayout.setVisibility(View.VISIBLE);
            advancedRankingTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_blue_triangle_up, 0);
        }
    }

    TextView lineTextView;
    TextView hotelTextView;
    TextView scenicTextView;
    TextView amusementTextView;
    TextView foodTextView;
    TextView shoppingTextView;
    TextView productTypeTextView;
    TextView destinationTextView;
    TextView locationTextView;
    ImageView reLocatingImageView;

    /**
     * 初始化高级筛选弹出框
     */
    private void initAdvanceRankingPopupLayout() {
        advanceRankingPopupLayout = findViewById(R.id.view_filter);
        // 最低价格
        final EditText minPriceEditText = (EditText) advanceRankingPopupLayout.findViewById(R.id.et_min_price);
        // 最大价格
        final EditText maxPriceEditText = (EditText) advanceRankingPopupLayout.findViewById(R.id.et_max_price);
        // 目的地
        destinationTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_destination);
        destinationTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductAddSelectActivity.this, AreaListActivity_.class);
                intent.putExtra(Define.FROM, ProductAddSelectActivity.class.getSimpleName());
                startActivityForResult(intent, REQUESTCODE_SELECT_CITY);
            }
        });
        // 定位城市
        locationTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_locating_area);
        locationTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String locationString = locationTextView.getText().toString();
                if (!locationString.equals(locatingTips) && !locationString.equals(locatingFailTips)) {
                	cityIdTemp = locatingCityId;
                	destinationTextView.setText(locationTextView.getText());
                }
            }
        });
        // 旋转动画
        final RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(Integer.MAX_VALUE);
        // 重新定位图标
        reLocatingImageView = (ImageView) advanceRankingPopupLayout.findViewById(R.id.iv_relocating);
        // 重新定位布局
        TextView reLocatingTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_relocating);
        reLocatingTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                locationTextView.setText(locatingTips);
                reLocatingImageView.clearAnimation();
                reLocatingImageView.startAnimation(animation);
                startLocating();
            }
        });
        reLocatingTextView.performClick();
        // 产品类型
        productTypeTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_product_type);
        // 线路
        lineTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_line);
        lineTextView.setOnClickListener(new ProductTypeItemClickListener());
        // 酒店
        hotelTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_hotel);
        hotelTextView.setOnClickListener(new ProductTypeItemClickListener());
        // 景点
        scenicTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_scenic);
        scenicTextView.setOnClickListener(new ProductTypeItemClickListener());
        // 娱乐
        amusementTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_amusement);
        amusementTextView.setOnClickListener(new ProductTypeItemClickListener());
        // 美食
        foodTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_food);
        foodTextView.setOnClickListener(new ProductTypeItemClickListener());
        // 购物
        shoppingTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_shopping);
        shoppingTextView.setOnClickListener(new ProductTypeItemClickListener());
        // 确认按钮
        TextView confirmTextView = (TextView) advanceRankingPopupLayout.findViewById(R.id.tv_confirm);
        confirmTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cityId = cityIdTemp;

                String minString = minPriceEditText.getText().toString();
                if (StringUtils.isNotEmpty(minString)) {
                    minPrice = Float.parseFloat(minString);
                }
                String maxString = maxPriceEditText.getText().toString();
                if (StringUtils.isNotEmpty(maxString)) {
                    maxPrice = Float.parseFloat(maxString);
                }

                if (minPrice > maxPrice) {
                    showToast(R.string.please_input_correct_price);
                } else {
                    advanceRankingPopupLayout.setVisibility(View.GONE);
                    InputMethodUtils.closeInputKeyboard(ProductAddSelectActivity.this);
                    advancedRankingTextView.setTextColor(getResources().getColor(R.color.v909090));
                    advancedRankingTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_black_triangle_down, 0);
                    handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (advanceRankingPopupLayout != null && advanceRankingPopupLayout.getVisibility() == View.VISIBLE) {
            advanceRankingPopupLayout.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    class ProductTypeItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {// 线路：4，酒店：5，景点：6， 娱乐：7，美食：8，购物：9
                case R.id.tv_line: // 线路
                    changeSelectProductType(true, false, false, false, false, false);
                    productType = 4;
                    productTypeTextView.setText(lineTextView.getText());
                    break;

                case R.id.tv_hotel: // 酒店
                    changeSelectProductType(false, true, false, false, false, false);
                    productType = 5;
                    productTypeTextView.setText(hotelTextView.getText());
                    break;

                case R.id.tv_scenic: // 景点
                    changeSelectProductType(false, false, true, false, false, false);
                    productType = 6;
                    productTypeTextView.setText(scenicTextView.getText());
                    break;

                case R.id.tv_amusement: // 娱乐
                    changeSelectProductType(false, false, false, true, false, false);
                    productType = 7;
                    productTypeTextView.setText(amusementTextView.getText());
                    break;

                case R.id.tv_food: // 美食
                    changeSelectProductType(false, false, false, false, true, false);
                    productType = 8;
                    productTypeTextView.setText(foodTextView.getText());
                    break;

                case R.id.tv_shopping: // 购物
                    changeSelectProductType(false, false, false, false, false, true);
                    productType = 9;
                    productTypeTextView.setText(shoppingTextView.getText());
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 更改选择的产品类型
     *
     * @param isSelectLine
     * @param isSelectHotel
     * @param isSelectScenic
     * @param isSelectamusement
     * @param isSelectFood
     * @param isSelectShopping
     */
    private void changeSelectProductType(boolean isSelectLine, boolean isSelectHotel, boolean isSelectScenic, boolean isSelectamusement, boolean isSelectFood, boolean isSelectShopping) {
        lineTextView.setSelected(isSelectLine);
        hotelTextView.setSelected(isSelectHotel);
        scenicTextView.setSelected(isSelectScenic);
        amusementTextView.setSelected(isSelectamusement);
        foodTextView.setSelected(isSelectFood);
        shoppingTextView.setSelected(isSelectShopping);
    }

    class ProductResourceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return productResources.size();
        }

        @Override
        public Object getItem(int position) {
            return productResources.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(ProductAddSelectActivity.this).inflate(R.layout.item_product_add_select, null);
                viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_product_cover_img);
//				viewHolder.addedCoverImageView = (ImageView) convertView.findViewById(R.id.iv_cover);
                viewHolder.productNameTextView = (TextView) convertView.findViewById(R.id.tv_product_name);
                viewHolder.productTypeTextView = (TextView) convertView.findViewById(R.id.tv_product_type);
                viewHolder.addToggleButton = (ToggleButton) convertView.findViewById(R.id.iv_add);
                viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.tv_product_price);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ProductResource resource = productResources.get(position);
            viewHolder.addToggleButton.setTag(position);
            viewHolder.addToggleButton.setOnCheckedChangeListener(null);
            // 产品名
            String productName = resource.getTitle();
            viewHolder.productNameTextView.setText(productName);
            // 产品类型
            int productType = resource.getProductType();
            String type = KooniaoTypeUtil.getStringByType(productType);
            viewHolder.productTypeTextView.setText(type);
            String price = resource.getProductPrice();
            viewHolder.priceTextView.setText(price);
            // 加载图片
            String imgUrl = resource.getLogo();
            ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, viewHolder.coverImageView);
            boolean isAdded = resource.isAdded();
            if (isAdded) {
                viewHolder.addToggleButton.setChecked(false);
            } else {
                viewHolder.addToggleButton.setChecked(true);
            }
            viewHolder.addToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ProductResource resource = productResources.get((Integer) buttonView.getTag());
                    if (!isChecked) {
                        resource.setAdded(true);
                        addedProductResourceList.add(resource);
                    } else {
                        resource.setAdded(false);
                        for (int i = 0; i < addedProductResourceList.size(); i++) {
                            ProductResource productResource = addedProductResourceList.get(i);
                            if (productResource.getPid() == resource.getPid()) {
                                addedProductResourceList.remove(productResource);
                            }
                        }
                    }
                }
            });
            return convertView;
        }

    }

    static class ViewHolder {
        //		ImageView addedCoverImageView; // 已添加的覆盖层
        ImageView coverImageView; // 封面
        TextView productNameTextView; // 产品名
        TextView productTypeTextView; // 产品类型
        ToggleButton addToggleButton; // 店铺名
        TextView priceTextView; // 产品价格
    }

    int cityIdTemp;
    final int REQUESTCODE_SELECT_CITY = 1; // 选择城市

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_SELECT_CITY:
                if (resultCode == RESULT_OK && data != null) {
                    String cityName = data.getStringExtra(Define.SELECTED_AREA_STRING);
                    destinationTextView.setText(cityName);
                    cityIdTemp = data.getIntExtra(Define.SELECTED_SUB_AREA_ID, 0);
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
