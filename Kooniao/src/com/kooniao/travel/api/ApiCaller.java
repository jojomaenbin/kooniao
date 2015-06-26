package com.kooniao.travel.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kooniao.travel.model.*;
import com.kooniao.travel.store.ProductCatalogActivity;
import com.kooniao.travel.store.ProductInfoActivity.QuoteInfo;
import com.kooniao.travel.store.ProductStarLevelActivity.starlist;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.EncryptUtil;
import com.kooniao.travel.utils.FileUtil;
import com.kooniao.travel.utils.JsonTools;
import com.kooniao.travel.utils.StringUtil;
import com.kooniao.travel.utils.UploadUtil;
import com.kooniao.travel.utils.UploadUtil.OnUploadProcessListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * API接口调用
 *
 * @author ke.wei.quan
 */
public class ApiCaller {

	ApiCaller() {
	}

	private static ApiCaller instance;

	public static ApiCaller getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new ApiCaller();
				}
			}
		}

		return instance;
	}

	/**
	 * 组拼请求链接
	 *
	 * @param string
	 * @return
	 */
	private String buildRequestURL(String uri) {
		return Define.BASE_URL + uri;
	}

	private final String KEY = "3273b102bba449325c2f15ef1f30db39";
	private final String INTERFACE_VERSION = "V105"; // 接口版本
	public static long TimeInService = Calendar.getInstance().getTimeInMillis() / 1000; // 接口版本

	/**
	 * 加入验证参数
	 *
	 * @param requsetParams
	 */
	private void authorizeRequestParameters(Map<String, String> requestParams) {
		requestParams.put("key", KEY);
		/**
		 * 加入用户Apikey & ApiKeySecret
		 */
		String apiKey = AppSetting.getInstance().getStringPreferencesByKey(Define.ApiKey);
		String apiKeySecret = AppSetting.getInstance().getStringPreferencesByKey(Define.ApiKeySecret);
		if (apiKey != null && apiKeySecret != null) {
			requestParams.put(Define.ApiKey, apiKey);
			requestParams.put(Define.ApiKeySecret, apiKeySecret);
		}
	}

	/**
	 * post请求
	 *
	 * @param requestURL
	 * @param params
	 */
	private void post(final String requestURL, final Map<String, String> params, final APIRequestCallBack callBack) {
		authorizeRequestParameters(params);
		RequestParams requestParams = new RequestParams(params);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setConnectTimeout(Define.REQUEST_TIMEOUT);
		httpClient.setResponseTimeout(Define.REQUEST_TIMEOUT);
		httpClient.post(requestURL, requestParams, new JsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				String errMsg = StringUtil.getStringFromR(R.string.net_err);
				callBack.onFailure(errMsg);
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					int recode = response.getInt("recode");
					if (recode == 7) {
						if (BaseActivity.frontActivity != null) {
							postlist.add(new Postmodel(requestURL, params, callBack));
							Intent loginIntent = new Intent(BaseActivity.frontActivity, LoginActivity_.class);
							loginIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							loginIntent.putExtra("relogin", true);
							BaseActivity.frontActivity.startActivity(loginIntent);
							Toast.makeText(BaseActivity.frontActivity, BaseActivity.frontActivity.getResources().getString(R.string.login_fail_tips), Toast.LENGTH_SHORT).show();
						}
					} else {
						callBack.onSuccess(response);
						super.onSuccess(statusCode, headers, response);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private List<Postmodel> postlist = new ArrayList<ApiCaller.Postmodel>();

	private class Postmodel {
		public Postmodel(String requestURL, Map<String, String> params, APIRequestCallBack callBack) {
			mRequestURL = requestURL;
			mParams = params;
			mCallBack = callBack;
		}

		public String mRequestURL;
		public Map<String, String> mParams;
		public APIRequestCallBack mCallBack;

	}

	/**
	 * 重新post请求
	 */
	public void repost() {
		for (final Postmodel mpost : postlist) {
			authorizeRequestParameters(mpost.mParams);
			RequestParams requestParams = new RequestParams(mpost.mParams);
			AsyncHttpClient httpClient = new AsyncHttpClient();
			httpClient.setConnectTimeout(Define.REQUEST_TIMEOUT);
			httpClient.setResponseTimeout(Define.REQUEST_TIMEOUT);
			httpClient.post(mpost.mRequestURL, requestParams, new JsonHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					String errMsg = StringUtil.getStringFromR(R.string.net_err);
					mpost.mCallBack.onFailure(errMsg);
					Log.e(Define.LOG_TAG, "请求出错了===》\n" + "状态码：" + statusCode + "\n错误信息：" + throwable.toString());
					super.onFailure(statusCode, headers, throwable, errorResponse);
					postlist.remove(mpost);
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					mpost.mCallBack.onSuccess(response);
					super.onSuccess(statusCode, headers, response);
					postlist.remove(mpost);
				}
			});
		}

	}

	/**
	 * 服务器请求回调接口
	 *
	 * @author ke.wei.quan
	 */
	private interface APIRequestCallBack {
		void onSuccess(JSONObject jsonObject);

		void onFailure(String errMsg);
	}

	/**
	 * *************************************************************************
	 * **********************************************
	 * <p/>
	 * 回调接口
	 * <p/>
	 * *************************************************************************
	 * ************************************************
	 */
	public interface ApiVersionResultCallback {
		void result(String errMsg, Version version);
	}

	public interface ApiUserInfoResultCallback {
		void result(String errMsg, UserInfo userInfo);
	}

	public interface APIStringResultCallback {
		void result(String errMsg);
	}

	public interface APIAgreementResultCallback {
		void result(String errMsg, String content);
	}

	public interface APIGetVerificationCodeResultCallback {
		void result(String errMsg, int resultCode);
	}

	public interface APIOpenStoreResultCallback {
		void result(String errMsg, int storeId);
	}

	public interface APIAdListResultCallback {
		void result(String errMsg, List<Ad> ads);
	}

	public interface APIProductListResultCallback {
		void result(String errMsg, List<Product> products, int pageCount);
	}

	public interface APICityListResultCallback {
		void result(String errMsg, List<City> hotCities);
	}

	public interface APIProductDetailResultCallback {
		void result(String errMsg, ProductDetail productDetail);
	}

	public interface APICommentListResultCallback {
		void result(String errMsg, List<Comment> comments, int commentCount);
	}

	public interface APIStoreResultCallback {
		void result(String errMsg, Store store);
	}

	public interface APIOrderListResultCallback {
		void result(String errMsg, List<Order> orders, int pageCount);
	}

	public interface APIMyOrderListResultCallback {
		void result(String errMsg, List<MyOrder> myOrders, int pageCount);
	}

	public interface APITravelListResultCallback {
		void result(String errMsg, List<Travel> travels, int pageCount);
	}

	public interface APIStoreAndProductListResultCallback {
		void result(String errMsg, Store store, List<Product> products, int pageCount);
	}

	public interface APIReserveProductResultCallback {
		void result(String errMsg, int orderId, int orderNum, int invoiceId, int isOffline, String shopLogo, String productLogo, List<Payment> payments);
	}

	public interface APIUserRegisterResultCallback {
		void result(String errMsg, String apiKey, String apiKeySecret, int uid);
	}

	public interface APIOrderDetailResultCallback {
		void result(String errMsg, OrderDetail orderDetail);
	}

	public interface APIStoreSalesStatisticsResultCallback {
		void result(String errMsg, StoreSalesStatistics storeSalesStatistics, int pageCount);
	}

	public interface APIProductSalesStatisticsResultCallback {
		void result(String errMsg, ProductSalesStatistics productSalesStatistics, int pageCount);
	}

	public interface APISelfLockingSalesStatisticsResultCallback {
		void result(String errMsg, SelfLockingSalesStatistics selfLockingSalesStatistics, int pageCount);
	}

	public interface APIStoreTotalSalesStatisticsResultCallback {
		void result(String errMsg, StoreTotalSalesStatistics storeTotalSalesStatistics, int pageCount);
	}

	public interface APIStoreDistributionSalesStatisticsResultCallback {
		void result(String errMsg, StoreDistributionSalesStatistics storeDistributionSalesStatistics, int pageCount);
	}

	public interface APIProductDistributionSalesStatisticsResultCallback {
		void result(String errMsg, ProductDistributionSalesStatistics productDistributionSalesStatistics, int pageCount);
	}

	public interface APICollectListResultCallback {
		void result(String errMsg, List<Collect> collects, int pageCount);
	}

	public interface APIStoreProductListResultCallback {
		void result(String errMsg, List<StoreProduct> storeProducts, int pageCount);
	}

	public interface APICommissionListResultCallback {
		void result(String errMsg, List<Commission> commissions, int pageCount);
	}

	public interface APICommissionDetailResultCallback {
		void result(String errMsg, CommissionDetail commissionDetail, int pageCount);
	}

	public interface APICustomerInfoListResultCallback {
		void result(String errMsg, List<CustomerInfo> customerInfos);
	}

	public interface APICustomerDetailResultCallback {
		void result(String errMsg, CustomerDetail customerDetail, int pageCount);
	}

	public interface APITravelDetailResultCallback {
		void result(String errMsg, TravelDetail travelDetail, int commentCount);
	}

	public interface APIStringListCallback {
		void result(String errMsg, List<String> imgList);
	}

	public interface APIAroundListResultCallback {
		void result(String errMsg, List<Around> arounds, int pageCount);
	}

	public interface APIScenicDetailResultCallback {
		void result(String errMsg, ScenicDetail scenicDetail);
	}

	public interface APICustomScenicDetailResultCallback {
		void result(String errMsg, CustomScenicDetail customScenicDetail);
	}

	public interface APIHotelDetailResultCallback {
		void result(String errMsg, HotelDetail hotelDetail);
	}

	public interface APICustomHotelDetailResultCallback {
		void result(String errMsg, CustomHotelDetail customHotelDetail);
	}

	public interface APIFoodDetailResultCallback {
		void result(String errMsg, FoodDetail foodDetail);
	}

	public interface APICustomFoodDetailResultCallback {
		void result(String errMsg, CustomFoodDetail customFoodDetail);
	}

	public interface APIShoppingDetailResultCallback {
		void result(String errMsg, ShoppingDetail shoppingDetail);
	}

	public interface APICustomAmusementAndShoppingDetailResultCallback {
		void result(String errMsg, CustomAmusementAndShopping customAmusementAndShopping);
	}

	public interface APICustomEventDetailResultCallback {
		void result(String errMsg, CustomEvent customEvent);
	}

	public interface APIAmusementDetailResultCallback {
		void result(String errMsg, AmusementDetail amusementDetail);
	}

	public interface APIRoleListResultCallback {
		void result(String errMsg, List<Role> roles);
	}

	public interface APIURLResolveResultCallback {
		void result(String errMsg, int id, String type, int distributorId, String commonType);
	}

	public interface APIGuideTravelListResultCallback {
		void result(String errMsg, Guide guideInfo, List<GuideTravel> travels, int totalCount);
	}

	public interface APIGetOfflineResultCallback {
		void result(String errMsg, String zipPath, String zipSize, long mTime);
	}

	public interface APIMyTravelResultCallback {
		void result(String errMsg, List<MyTravel> allTravels, List<MyTravel> teamTravels, int pageCount);
	}

	public interface APICurrentTravelResultCallback {
		void result(String errMsg, CurrentTravel currentTravel);
	}

	public interface APITravelFeedBackResultCallback {
		void result(String errMsg, float lineScore, float serviceScore, float repastScore, List<Comment> comments, int totalCount);
	}

	public interface APITeamCustomerResultCallback {
		void result(String errMsg, int teamId, List<TeamCustomer> teamCustomers);
	}

	public interface APIGroupInfoResultCallback {
		void result(String errMsg, GroupInfo groupInfo);
	}

	public interface APIMessageResultCallback {
		void result(String errMsg, List<Message> messages);
	}

	public interface APISubMessageResultCallback {
		void result(String errMsg, List<SubMessage> subMessages);
	}

	public interface APIOfflineUpdateInfoListResultCallback {
		void result(String errMsg, List<OfflineUpdateInfo> offlineUpdateInfos);
	}

	public interface APIRollCallListResultCallback {
		void result(String errMsg, List<RollCall> rollCalls, int pageCount);
	}

	public interface APIRollCallDetailResultCallback {
		void result(String errMsg, RollCallDetail rollCallDetail);
	}

	public interface APICurrentResultCallback {
		void result(String errMsg, int oldMtime, int newMtime);

	}

	public interface APIProductpackageResultCallback {
		void result(String errMsg, long serviceTime, int isShowStock, int isMinBuy, int minBuy, int isBook, List<ProductPackage> productPackages);

	}

	public interface APIProductCategoryResultCallback {
		void result(String errMsg, ProductCategory class_List);

	}

	public interface APIProductResourceListCallback {
		void result(String errMsg, List<ProductResource> productResourceList, int pageCount);
	}

	public interface APIProductInfoResultCallback {
		void result(String errMsg, ProductInfo productInfo);
	}

	public interface APIAddProductCatalogResultCallback {
		void result(String errMsg, int flag, int cid);
	}

	public interface APILoadProductCatalogResultCallback {
		void result(String errMsg, List<ProductCatalogActivity.Catalog> catalogs);
	}

	public interface APIDistributionTemplateListResultCallback {
		void result(String errMsg, List<DistributionTemplate> distributionTemplates);
	}

	public interface APIDistributionTemplateResultCallback {
		void result(String errMsg, int templateId);
	}

	public interface APIStarLevelResultCallback {
		void result(String errMsg, starlist starlevels);
	}

	public interface APISubmitProductInfoCallback {
		void result(String errMsg, int flag, int pid, int recommendCount);
	}

	public interface APISaveProductInfoCallback {
		void result(String errMsg, int flag);
	}

	public interface APIQuoteInfoCallback {
		void result(String errMsg, QuoteInfo quoteInfo);
	}

	public interface APIOrderPayResultCallback {
		void result(String errMsg, String payInfo);
	}

	public interface APIRemainPayResultCallback {
		void result(String errMsg, int orderId, int invoiceId, float price);
	}

	/**************************************************************************************************************************
	 *
	 * API接口请求
	 *
	 * *************************************************************************
	 * ************************************************
	 */

	/**
	 * 获取json数据中的data或者错误
	 *
	 * @param json
	 * @return 成功数据返回data，失败时候返回错误信息,[0]错误信息 ，[1]data obj
	 */
	private String[] getJsonObjDataOrErr(JSONObject jsonObject) {
		String[] datas = new String[2];
		try {
			int resultCode = jsonObject.getInt("recode");
			if (resultCode == 1) {
				JSONObject dataObject = jsonObject.getJSONObject("datas");
				datas[0] = null;
				datas[1] = dataObject.toString();
			} else {
				String errMsg = jsonObject.getString("msg");
				datas[0] = errMsg;
				datas[1] = null;
			}
		} catch (Exception e) {
			datas[0] = e.toString();
			datas[1] = null;
			Log.e(Define.LOG_TAG, "解析json obj获取datas出错了/n" + e.toString());
		}

		return datas;
	}

	/**
	 * 获取json数据中的data或者错误
	 *
	 * @param json
	 * @return 成功数据返回data，失败时候返回错误信息,[0]错误信息 ，[1]data array
	 */
	private String[] getJsonArrayDataOrErr(JSONObject jsonObject) {
		String[] datas = new String[2];
		try {
			int resultCode = jsonObject.getInt("recode");
			if (resultCode == 1) {
				JSONArray dataArray = jsonObject.getJSONArray("datas");
				datas[0] = null;
				datas[1] = dataArray.toString();
			} else {
				String errMsg = jsonObject.getString("msg");
				datas[0] = errMsg;
				datas[1] = null;
			}
		} catch (Exception e) {
			datas[0] = e.toString();
			datas[1] = null;
			Log.e(Define.LOG_TAG, "解析json array获取datas出错了/n" + e.toString());
		}

		return datas;
	}

	/**
	 * 检查新版本
	 *
	 * @param callback
	 */
	public void checkLastVersion(final ApiVersionResultCallback callback) {
		String subURL = "mod=mobileUpdate&act=versionControl";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					Version version = JsonTools.jsonObj(jsonData, Version.class);
					callback.result(null, version);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 用户登录
	 *
	 * @param uid
	 * @param passwd
	 * @param callback
	 */
	public void userLogin(String loginName, String passwd, final ApiUserInfoResultCallback callback) {
		String subURL = "mod=mobile" + INTERFACE_VERSION + "&act=authorize";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("loginName", loginName);
		passwd = EncryptUtil.generatePassword(passwd);
		paramsMaps.put("passwd", passwd);
		paramsMaps.put("isIphone", String.valueOf(1));
		String mark = JPushInterface.getRegistrationID(KooniaoApplication.getInstance());
		paramsMaps.put("mark", mark);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					UserInfo userInfo = JsonTools.jsonObj(jsonData, UserInfo.class);
					callback.result(null, userInfo);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});

	}

	/**
	 * 退出登录
	 *
	 * @param type
	 *            操作类型（1、登陆，2、退出）
	 * @param callback
	 */
	public void userLogout(final APIStringResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=deviceMark";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		String mark = JPushInterface.getRegistrationID(KooniaoApplication.getInstance());
		paramsMaps.put("mark", mark);
		paramsMaps.put("type", String.valueOf(2));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.logout_fail);
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "退出登录错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.logout_fail);
					callback.result(errMsg);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取用户详细信息
	 *
	 * @param callback
	 */
	public void loadUserDetailInfo(final ApiUserInfoResultCallback callback) {
		String subURL = "mod=MobileUser" + INTERFACE_VERSION + "&act=RddGetUserDetail";
		String requestURL = buildRequestURL(subURL);
		Map<String, String> paramsMaps = new HashMap<String, String>();
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					UserInfo userInfo = JsonTools.jsonObj(jsonData, UserInfo.class);
					callback.result(null, userInfo);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取手机验证码
	 *
	 * @param mobile
	 * @param module
	 *            验证类别 sms_module 发送短信类型模块(register:注册 system:系统
	 *            find_password:找回密码 modify_mobile:修改手机号 validate_mobile:验证原手机号)
	 * @param callback
	 */
	public void getPhoneVerificationCode(String mobile, String module, final APIGetVerificationCodeResultCallback callback) {
		String subURL = "mod=mobileSms" + INTERFACE_VERSION + "&act=send";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("mobile", mobile);
		paramsMaps.put("sms_module", module);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null, 0);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg, resultCode);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, resultCode);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.commit_comment_fail);
					callback.result(errMsg, 0);
					Log.e(Define.LOG_TAG, "获取手机验证码错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0);
			}
		});
	}

	/**
	 * 更新用户性别
	 *
	 * @param sex
	 * @param callback
	 */
	public void updateSex(int sex, final APIStringResultCallback callback) {
		updateUserInfo(null, sex, callback);
	}

	/**
	 * 更新用户昵称
	 *
	 * @param nickName
	 * @param sex
	 * @param callback
	 */
	public void updateNickName(String nickName, int sex, final APIStringResultCallback callback) {
		updateUserInfo(nickName, sex, callback);
	}

	/**
	 * 更新用户信息(性别、昵称)
	 *
	 * @param nickName
	 * @param sex
	 * @param callback
	 */
	private void updateUserInfo(String nickName, int sex, final APIStringResultCallback callback) {
		String subURL = "mod=mobileUser" + INTERFACE_VERSION + "&act=RddUpdateUserProfile";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		if (nickName != null) {
			paramsMaps.put("nickname", nickName);
		}
		paramsMaps.put("sex", String.valueOf(sex));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "更改用户信息错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.update_user_info_err);
					callback.result(errMsg);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 用户注册
	 *
	 * @param type
	 *            注册类型（邮箱:0;手机:1）
	 * @param email
	 *            邮箱
	 * @param nickname
	 *            昵称
	 * @param password
	 *            密码
	 * @param mobile
	 *            电话
	 * @param validateNum
	 *            验证码
	 * @param callback
	 */
	public void userRegister(int type, String email, String nickname, String password, String mobile, String validateNum, final APIUserRegisterResultCallback callback) {
		String subURL = "mod=mobile" + INTERFACE_VERSION + "&act=RddRegister";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("type", String.valueOf(type));
		if (type == 0) {
			paramsMaps.put("email", email);
		} else {
			paramsMaps.put("mobile", mobile);
			paramsMaps.put("verifyCode", validateNum);
		}
		paramsMaps.put("nickname", nickname);
		paramsMaps.put("password", password);
		String mark = JPushInterface.getRegistrationID(KooniaoApplication.getInstance());
		paramsMaps.put("mark", mark);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							String apiKey = dataObject.getString("ApiKey");
							String apiKeySecret = dataObject.getString("ApiKeySecret");
							int uid = dataObject.getInt("uid");
							callback.result(null, apiKey, apiKeySecret, uid);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg, null, null, 0);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null, null, 0);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.register_fail);
					callback.result(errMsg, null, null, 0);
					Log.e(Define.LOG_TAG, "用户注册错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, null, 0);
			}
		});
	}

	/**
	 * 角色申请
	 *
	 * @param realName
	 * @param certificate
	 * @param tel
	 * @param roleId
	 * @param callback
	 */
	public void roleApply(String realName, String certificate, String tel, int roleId, final APIStringResultCallback callback) {
		String subURL = "mod=mobileUser" + INTERFACE_VERSION + "&act=RddRoleAppli";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("realName", realName);
		paramsMaps.put("certificate", certificate);
		paramsMaps.put("tel", tel);
		paramsMaps.put("groupid_str", String.valueOf(roleId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "角色申请错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取角色列表
	 *
	 * @param callback
	 */
	public void getRoleList(final APIRoleListResultCallback callback) {
		String subURL = "mod=mobileUser" + INTERFACE_VERSION + "&act=RddGetUserGroupAppliList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonArrayDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					try {
						List<Role> roles = JsonTools.jsonObjArray(jsonArray, Role.class);
						callback.result(null, roles);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_role_list_fail);
						callback.result(errMsg, null);
					}

				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 邮箱重设密码
	 *
	 * @param email
	 * @param callback
	 */
	public void forgetPasswordByEmail(String email, final APIStringResultCallback callback) {
		String subURL = "mod=mobileWhite" + INTERFACE_VERSION + "&act=RddForgetPassword";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("email", email);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "邮箱重设密码错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 绑定邮箱
	 *
	 * @param email
	 * @param callback
	 */
	public void bindingEmail(String email, final APIStringResultCallback callback) {
		String subURL = "mod=mobileUser" + INTERFACE_VERSION + "&act=RddSendEmail";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("email", email);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "绑定邮箱错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 手机重设密码
	 *
	 * @param phoneNum
	 * @param validateNum
	 * @param password
	 * @param callback
	 */
	public void forgetPasswordByPhone(String phoneNum, String validateNum, String password, final APIStringResultCallback callback) {
		String subURL = "mod=mobileWhite" + INTERFACE_VERSION + "&act=RddForgetPassword";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("phoneNum", phoneNum);
		paramsMaps.put("validateNum", validateNum);
		paramsMaps.put("password", password);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "手机重设密码错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 绑定手机
	 *
	 * @param phoneNum
	 * @param validateNum
	 * @param callback
	 */
	public void bindingPhone(String phoneNum, String validateNum, final APIStringResultCallback callback) {
		String subURL = "mod=mobileUser" + INTERFACE_VERSION + "&act=RddModifyMobile";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("mobile", phoneNum);
		paramsMaps.put("verifyCode", validateNum);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "绑定手机错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取首页广告列表
	 *
	 * @param cid
	 * @param callback
	 */
	public void loadAdList(int cid, final APIAdListResultCallback callback) {
		String subURL = "mod=MobileAdvert" + INTERFACE_VERSION + "&act=homeAdvert";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("id", String.valueOf(cid));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonArrayDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					List<Ad> ads = JsonTools.jsonObjArray(jsonArray, Ad.class);
					callback.result(null, ads);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取产品列表
	 *
	 * @param cid
	 * @param callback
	 */
	public void loadProductList(int cid, int type, int pageNum, final APIProductListResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("id", String.valueOf(cid));
		paramsMaps.put("type", String.valueOf(type));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					JSONArray productJsonArray = null;
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						pageCount = dataObject.getInt("pageCount");
						productJsonArray = dataObject.getJSONArray("productList");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取产品列表错误\n" + e.toString());
					}

					List<Product> products = null;
					if (productJsonArray != null) {
						products = JsonTools.jsonObjArray(productJsonArray, Product.class);
					}
					callback.result(null, products, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取产品分类列表
	 *
	 * @param callback
	 */
	public void loadProductCategoryList(final APIProductCategoryResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productCategory";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					JSONObject dataObject;
					try {
						dataObject = new JSONObject(jsonData);
						TimeInService = dataObject.getInt("serviceTime");
					} catch (JSONException e) {
					}

					ProductCategory class_List = JsonTools.jsonObj(jsonData, ProductCategory.class);
					callback.result(null, class_List);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取产品详情页
	 *
	 * @param pid
	 *            产品id
	 * @param uid
	 *            用户id
	 * @param cid
	 *            C店id
	 * @param callback
	 */
	public void loadProductDetail(int pid, int uid, int cid, final APIProductDetailResultCallback callback) {
		String subURL = "mod=mobileProduct" + INTERFACE_VERSION + "&act=productDetail";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pid", String.valueOf(pid));
		if (uid != 0) {
			paramsMaps.put("uid", String.valueOf(uid));
		}
		if (cid != 0) {
			paramsMaps.put("cid", String.valueOf(cid));
		}
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					ProductDetail productDetail = JsonTools.jsonObj(jsonData, ProductDetail.class);
					callback.result(null, productDetail);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取产品编辑信息
	 *
	 * @param pid
	 *            产品id
	 * @param callback
	 */
	public void loadProductInfo(int pid, final APIProductInfoResultCallback callback) {
		String subURL = "mod=mobileProduct" + INTERFACE_VERSION + "&act=resourcesproductInfo";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pid", String.valueOf(pid));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					ProductInfo productInfo = JsonTools.jsonObj(jsonData, ProductInfo.class);
					callback.result(null, productInfo);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 上传产品图片
	 *
	 * @param filePath
	 * @param callback
	 */
	public void uploadTempImage(String filePath, final APIAgreementResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=uploadTempImage";
		String requestURL = buildRequestURL(subURL);

		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(new OnUploadProcessListener() {

			@Override
			public void onUploadProcess(int uploadSize) {
				Log.e(Define.LOG_TAG, "上传图片+++" + uploadSize);
			}

			@Override
			public void onUploadDone(int responseCode, String message) {
				try {
					JSONObject jsonObject = new JSONObject(message);
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						String flag = dataObject.getString("imageUrl");
						if (flag != null) {
							callback.result(null, flag);
						} else {
							callback.result("上传图片失败", "");
						}

					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, "");
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "上传图片出错'n" + e.toString());
				}
			}

			@Override
			public void initUpload(int fileSize) {
				Log.e(Define.LOG_TAG, "上传图片+++" + fileSize);
			}
		});

		Map<String, String> params = new HashMap<String, String>();
		params.put("key", KEY);
		String apiKey = AppSetting.getInstance().getStringPreferencesByKey(Define.ApiKey);
		String apiKeySecret = AppSetting.getInstance().getStringPreferencesByKey(Define.ApiKeySecret);
		params.put(Define.ApiKey, apiKey);
		params.put(Define.ApiKeySecret, apiKeySecret);
		uploadUtil.uploadFile(true, filePath.replace("file://", ""), "image", requestURL, params);
	}

	/**
	 * 添加产品目录组
	 *
	 * @param title
	 *            目录名称
	 * @param isShow
	 *            是否显示
	 * @param remark
	 *            备注
	 * @param callback
	 */
	public void addProductCatalog(String title, int isShow, String remark, int maxsort, final APIAddProductCatalogResultCallback callback) {
		String subURL = "mod=mobileProduct" + INTERFACE_VERSION + "&act=addProductCatalog";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("title", title);
		paramsMaps.put("isShow", String.valueOf(isShow));
		paramsMaps.put("sort", String.valueOf(maxsort));
		if (remark != null)
			paramsMaps.put("remark", remark);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					JSONObject dataObject = null;
					try {
						dataObject = new JSONObject(jsonData);
						int flag = dataObject.getInt("flag");
						int cid = dataObject.getInt("cid");
						callback.result(null, flag, cid);
					} catch (JSONException e) {
						callback.result("添加目录组失败", 0, 0);
						Log.e(Define.LOG_TAG, "获取产品评论列表错误\n" + e.toString());
					}

				} else {
					callback.result(errMsg, 0, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0, 0);
			}
		});
	}

	/**
	 * 获取产品目录组
	 *
	 * @param callback
	 */
	public void loadProductCatalog(final APILoadProductCatalogResultCallback callback) {
		String subURL = "mod=mobileProduct" + INTERFACE_VERSION + "&act=productCatalog";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						JSONArray catalogArray = dataObject.getJSONArray("catalogList");
						List<ProductCatalogActivity.Catalog> Catalogs = JsonTools.jsonObjArray(catalogArray, ProductCatalogActivity.Catalog.class);
						callback.result(null, Catalogs);
					} catch (Exception e) {
						callback.result("获取产品目录失败", null);
						Log.e(Define.LOG_TAG, "获取产品目录失败\n" + e.toString());
					}
				} else {
					callback.result(errMsg, null);
				}

			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取产品评论列表
	 *
	 * @param cid
	 * @param notificationCallback
	 */
	public void loadProductCommentList(int type, int pid, int pageNum, final APICommentListResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productReview";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("type", String.valueOf(type));
		paramsMaps.put("pid", String.valueOf(pid));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						int pageCount = dataObject.getInt("pageCount");
						JSONArray commentArray = dataObject.getJSONArray("commentList");
						List<Comment> comments = JsonTools.jsonObjArray(commentArray, Comment.class);
						callback.result(null, comments, pageCount);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_product_comments_fail);
						callback.result(errMsg, null, 0);
						Log.e(Define.LOG_TAG, "获取产品评论列表错误\n" + e.toString());
					}
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取店铺产品列表(产品管理的产品列表数据)
	 *
	 * @param productType
	 *            产品类型： 全部：0 线路：4 组合：2 酒店：5 美食：8 娱乐：7
	 * @param shopType
	 *            店铺类型（a、c）
	 * @param shopId
	 * @param statusType
	 *            产品状态（0：全部、1:出售中、3:未出售、4、售罄）
	 * @param recommend
	 *            推荐（0：没推荐,1：推荐，2:全部）
	 * @param affiliateStatus
	 *            分销（0：不是，1:是，2：全部）
	 * @param pageNum
	 * @param callback
	 */
	public void loadShopProductList(String productType, String shopType, int shopId, int statusType, int recommend, int affiliateStatus, int pageNum, final APIStoreProductListResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=shopProductList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("productType", String.valueOf(productType));
		paramsMaps.put("shopType", shopType);
		paramsMaps.put("shopId", String.valueOf(shopId));
		paramsMaps.put("statusType", String.valueOf(statusType));
		paramsMaps.put("recommend", String.valueOf(recommend));
		paramsMaps.put("affiliateStatus", String.valueOf(affiliateStatus));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						int pageCount = dataObject.getInt("pageCount");
						JSONArray storeProductArray = dataObject.getJSONArray("productList");
						List<StoreProduct> storeProducts = JsonTools.jsonObjArray(storeProductArray, StoreProduct.class);
						callback.result(null, storeProducts, pageCount);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_product_lists_fail);
						callback.result(errMsg, null, 0);
						Log.e(Define.LOG_TAG, "获取店铺产品列表错误\n" + e.toString());
					}
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 产品管理操作（上下架和删除）
	 *
	 * @param pid
	 *            产品id
	 * @param sid
	 *            店铺id
	 * @param shopType
	 *            产品类型
	 * @param operateType
	 *            操作类型(删除:delete、上架：added、下架：shelves)
	 * @param callback
	 */
	public void productOperate(int pid, int sid, String shopType, String operateType, final APIStringResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productManagement";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pid", String.valueOf(pid));
		paramsMaps.put("sid", String.valueOf(sid));
		paramsMaps.put("shopType", shopType);
		paramsMaps.put("operateType", operateType);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "产品管理操作错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 产品搜索
	 *
	 * @param cid
	 * @param callback
	 */
	public void productSearch(int cid, String keyWord, int type, int pageNum, final APIProductListResultCallback callback) {
		String subURL = "mod=MobileSearch" + INTERFACE_VERSION + "&act=homeProductSearch";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("id", String.valueOf(cid));
		paramsMaps.put("keyWord", keyWord);
		paramsMaps.put("type", String.valueOf(type));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					JSONArray productJsonArray = null;
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						pageCount = dataObject.getInt("pageCount");
						productJsonArray = dataObject.getJSONArray("productList");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取产品列表错误\n" + e.toString());
					}

					List<Product> products = null;
					if (productJsonArray != null) {
						products = JsonTools.jsonObjArray(productJsonArray, Product.class);
					}
					callback.result(null, products, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取热门城市列表数据
	 *
	 * @param callback
	 */
	public void loadHotCities(final APICityListResultCallback callback) {
		String subURL = "mod=mobileWhite" + INTERFACE_VERSION + "&act=getHotCityList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					JSONArray jsonArray = null;
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						jsonArray = dataObject.getJSONArray("hotCityList");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取热门城市列表错误\n" + e.toString());
					}

					List<City> hotCities = null;
					if (jsonArray != null) {
						hotCities = JsonTools.jsonObjArray(jsonArray, City.class);
					}

					callback.result(null, hotCities);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 添加或取消产品到我的收藏列表
	 *
	 * @param likeId
	 * @param isKeep
	 * @param likeType
	 *            行程：plan 景点：location 酒店：hotel 美食：lifestyle_food
	 *            娱乐：lifestyle_funny 购物：lifestyle_shopping 产品酒店：product_hotel
	 *            产品娱乐：product_lifestyle_entertainm
	 *            产品购物：product_lifestyle_shopping
	 *            产品门票：product_location_ticket_type 产品行程：product_plan
	 * @param likeSubType
	 * @param fromId
	 *            产品填店铺id，其他传0
	 * @param callback
	 */
	public void addOrCancelToMyCollect(int likeId, int isKeep, String likeType, String likeSubType, int fromId, final APIStringResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=addFavorite";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("likeId", String.valueOf(likeId));
		paramsMaps.put("isKeep", String.valueOf(isKeep));
		paramsMaps.put("likeType", likeType);
		paramsMaps.put("fromId", String.valueOf(fromId));
		if (likeSubType != null) {
			paramsMaps.put("likeSubType", likeSubType);
		}
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "添加收藏错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取店铺信息
	 *
	 * @param sid
	 *            店铺id
	 * @param type
	 *            （a/c）
	 * @param callback
	 */
	public void loadStoreInfo(int sid, String type, final APIStoreResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=shopInfo";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("sid", String.valueOf(sid));
		paramsMaps.put("type", type);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					Store store = JsonTools.jsonObj(jsonData, Store.class);
					callback.result(null, store);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 店铺订单管理
	 *
	 * @param type
	 * @param pagNum
	 * @param callback
	 */
	public void loadOrderList(String type, int pageNum, final APIOrderListResultCallback callback) {
		String subURL = "mod=MobileOrder" + INTERFACE_VERSION + "&act=shopOrderList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("type", type);
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					JSONArray orderArray = null;
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						pageCount = dataObject.getInt("pageCount");
						orderArray = dataObject.getJSONArray("orderList");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取订单列表错误\n" + e.toString());
					}

					List<Order> orders = null;
					if (orderArray != null) {
						orders = JsonTools.jsonObjArray(orderArray, Order.class);
					}
					callback.result(null, orders, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取行程列表
	 *
	 * @param uid
	 * @param cityId
	 * @param pageNum
	 * @param callback
	 */
	public void loadTravelList(int uid, final int cityId, int pageNum, final APITravelListResultCallback callback) {
		String subURL = "mod=mobileWhite" + INTERFACE_VERSION + "&act=getRecommendationTravelList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("cityId", String.valueOf(cityId));
		int pageSize = 15; // 默认一页15条数据
		paramsMaps.put("pageSize", String.valueOf(pageSize));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		if (uid != 0) {
			paramsMaps.put("uid", String.valueOf(uid));
		}
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					JSONArray travelArray = null;
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						travelArray = dataObject.getJSONArray("recommendationList");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取行程列表错误\n" + e.toString());
					}

					List<Travel> travels = null;
					if (travelArray != null) {
						travels = JsonTools.jsonObjArray(travelArray, Travel.class);
					}
					callback.result(null, travels, Integer.MAX_VALUE);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取我的行程列表
	 *
	 * @param pageNum
	 * @param callback
	 */
	public void loadMyTravelList(int pageNum, final APIMyTravelResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=getSelfPlanList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		int pageSize = 15; // 默认一页15条数据
		paramsMaps.put("pageSize", String.valueOf(pageSize));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String dataJson = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(dataJson);
						JSONArray teamTravelArray = dataObject.getJSONArray("teamList");
						List<MyTravel> teamTravels = JsonTools.jsonObjArray(teamTravelArray, MyTravel.class);
						JSONArray allTravelArray = dataObject.getJSONArray("selfPlanList");
						List<MyTravel> allTravels = JsonTools.jsonObjArray(allTravelArray, MyTravel.class);
						int pageCount = dataObject.getInt("pageCount");
						callback.result(null, allTravels, teamTravels, pageCount);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_mine_travel_list_fail);
						callback.result(errMsg, null, null, 0);
					}

				} else {
					callback.result(errMsg, null, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, null, 0);
			}
		});
	}

	/**
	 * 获取三天行程列表
	 *
	 * @param pageNum
	 * @param callback
	 */
	public void loadThreeDaysTravelList(final APIMyTravelResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=soonAllPlan";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String dataJson = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(dataJson);
						JSONArray allTravelArray = dataObject.getJSONArray("selfPlanList");
						List<MyTravel> allTravels = JsonTools.jsonObjArray(allTravelArray, MyTravel.class);
						callback.result(null, allTravels, null, 0);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_mine_travel_list_fail);
						callback.result(errMsg, null, null, 0);
					}

				} else {
					callback.result(errMsg, null, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, null, 0);
			}
		});
	}

	/**
	 * 获取导游行程列表
	 *
	 * @param guideId
	 * @param pageNum
	 * @param callback
	 */
	public void loadGuideTravelList(int guideId, int pageNum, final APIGuideTravelListResultCallback callback) {
		String subURL = "mod=mobileWhite" + INTERFACE_VERSION + "&act=getGuidePlan";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("guideId", String.valueOf(guideId));
		int pageSize = 15; // 默认一页15条数据
		paramsMaps.put("pageSize", String.valueOf(pageSize));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					JSONArray travelArray = null;
					JSONObject guideInfoObject = null;
					int totalCount = 0; // 总数据条数
					Guide guideInfo = null;
					List<GuideTravel> travels = null;
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						travelArray = dataObject.getJSONArray("planList");
						travels = JsonTools.jsonObjArray(travelArray, GuideTravel.class);
						totalCount = dataObject.getInt("pageCount");
						guideInfoObject = dataObject.getJSONObject("guideList");
						guideInfo = JsonTools.jsonObj(guideInfoObject.toString(), Guide.class);
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取导游行程列表错误\n" + e.toString());
					}
					callback.result(null, guideInfo, travels, totalCount);
				} else {
					callback.result(errMsg, null, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, null, 0);
			}
		});
	}

	/**
	 * 获取行程详情
	 *
	 * @param isOffline
	 * @param uid
	 * @param planId
	 *            行程ID
	 * @param groupID
	 *            团ID
	 * @param callback
	 */
	public void loadTravelDetail(boolean isOffline, int uid, final int planId, int groupID, final APITravelDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + planId + "/source/" + planId + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
			parseOfflineTravelDetail(jsonObject, callback);
		} else {
			String subURL = "mod=mobileWhite" + INTERFACE_VERSION + "&act=getTravelDetails";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("uid", String.valueOf(uid));
			paramsMaps.put("planId", String.valueOf(planId));
			if (groupID != -1) {
				paramsMaps.put("groupID", String.valueOf(groupID));
			}
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					parseTravelDetail(callback, jsonObject);
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null, 0);
				}
			});
		}
	}

	/**
	 * 保存行程详情
	 *
	 * @param travelId
	 * @param travelDetail
	 */
	public void saveTravelDetail(int travelId, TravelDetail travelDetail) {
		String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/" + travelId + ".txt";
		FileUtil.saveFileToSdcard(travelDetailPath, JsonTools.getGson().toJson(travelDetail));
	}

	/**
	 * 解析行程详情
	 *
	 * @param callback
	 * @param json
	 */
	private void parseTravelDetail(final APITravelDetailResultCallback callback, JSONObject jsonObject) {
		String[] dataArray = getJsonObjDataOrErr(jsonObject);
		String errMsg = dataArray[0];
		if (errMsg == null) {
			String jsonData = dataArray[1];
			JSONObject dataObject;
			TravelDetail travelDetail = null;
			int commentCount = 0;
			try {
				dataObject = new JSONObject(jsonData);
				commentCount = dataObject.getInt("commentCount");
				travelDetail = JsonTools.jsonObj(dataObject.toString(), TravelDetail.class);
			} catch (Exception e) {
				errMsg = StringUtil.getStringFromR(R.string.load_travel_detail_fail);
			}
			callback.result(errMsg, travelDetail, commentCount);
		} else {
			callback.result(errMsg, null, 0);
		}
	}

	/**
	 * 解析离线行程详情
	 *
	 * @param callback
	 * @param json
	 */
	private void parseOfflineTravelDetail(JSONObject dataObject, final APITravelDetailResultCallback callback) {
		TravelDetail travelDetail = null;
		int commentCount = 0;
		String errMsg = null;
		try {
			commentCount = dataObject.getInt("commentCount");
			travelDetail = JsonTools.jsonObj(dataObject.toString(), TravelDetail.class);
		} catch (Exception e) {
			errMsg = StringUtil.getStringFromR(R.string.load_travel_detail_fail);
		}
		callback.result(errMsg, travelDetail, commentCount);
	}

	/**
	 * 获取行程大图列表
	 *
	 * @param isOffline
	 * @param planId
	 * @param callback
	 */
	public void loadTravelLargeImageList(final boolean isOffline, final int planId, final APIStringListCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + planId + "/source/bigImage/plan.txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
			parseTravelLargeImageList(isOffline, planId, callback, jsonObject);
		} else {
			String subURL = "mod=mobileWhite" + INTERFACE_VERSION + "&act=getPlanImages";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("planId", String.valueOf(planId));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					parseTravelLargeImageList(isOffline, planId, callback, jsonObject);
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 解析行程大图列表数据
	 *
	 * @param callback
	 * @param jsonObject
	 */
	private void parseTravelLargeImageList(boolean isOffline, int planId, final APIStringListCallback callback, JSONObject jsonObject) {
		String[] dataArray = getJsonObjDataOrErr(jsonObject);
		String errMsg = dataArray[0];
		if (errMsg == null) {
			String jsonData = dataArray[1];
			JSONObject dataObject = null;
			List<String> imgList = new ArrayList<String>();
			try {
				dataObject = new JSONObject(jsonData);
				JSONArray imgArray = dataObject.getJSONArray("imagesList");
				for (int i = 0; i < imgArray.length(); i++) {
					String imgPath = imgArray.getString(i);
					if (isOffline) {
						imgPath = "file://" + KooniaoApplication.getInstance().getDownloadDir() + planId + "/source" + imgPath;
					}
					imgList.add(imgPath);
				}
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, "获取行程大图列表错误\n" + e.toString());
			}

			callback.result(null, imgList);
		} else {
			callback.result(errMsg, null);
		}
	}

	/**
	 * 获取产品大图列表
	 *
	 * @param pid
	 * @param callback
	 */
	public void loadProductLargeImageList(int pid, final APIStringListCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productPhotoGallery";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pid", String.valueOf(pid));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					JSONObject dataObject = null;
					List<String> imgList = new ArrayList<String>();
					try {
						dataObject = new JSONObject(jsonData);
						JSONArray imgArray = dataObject.getJSONArray("imagesList");
						for (int i = 0; i < imgArray.length(); i++) {
							imgList.add(imgArray.getString(i));
						}
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取产品大图列表错误\n" + e.toString());
					}

					callback.result(null, imgList);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取附近资讯大图列表
	 *
	 * @param isOffline
	 * @param type
	 *            类型：景点 location 酒店 hotel 饮食 lifestyle_food 购物
	 *            lifestyle_shopping 娱乐 lifestyle_funny
	 * @param planId
	 * @param id
	 * @param callback
	 */
	public void loadAroundLargeImageList(boolean isOffline, String type, int planId, int id, final APIStringListCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + planId + "/source/bigImage/" + type + "_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			List<String> imgList = new ArrayList<String>();
			try {
				jsonObject = new JSONObject(json);
				JSONObject dataObject = jsonObject.getJSONObject("datas");
				JSONArray imgArray = dataObject.getJSONArray("imagesList");
				for (int i = 0; i < imgArray.length(); i++) {
					String imgPath = imgArray.getString(i);
					imgPath = "file://" + KooniaoApplication.getInstance().getDownloadDir() + planId + "/source" + imgPath;
					imgList.add(imgPath);
				}
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
			callback.result(null, imgList);
		} else {
			String subURL = "mod=mobile" + INTERFACE_VERSION + "&act=RddGetBigImg";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("type", type);
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						JSONObject dataObject = null;
						List<String> imgList = new ArrayList<String>();
						try {
							dataObject = new JSONObject(jsonData);
							JSONArray imgArray = dataObject.getJSONArray("imagesList");
							for (int i = 0; i < imgArray.length(); i++) {
								imgList.add(imgArray.getString(i));
							}
						} catch (Exception e) {
							Log.e(Define.LOG_TAG, "获取附近资讯大图列表错误\n" + e.toString());
						}

						callback.result(null, imgList);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 添加行程
	 *
	 * @param planId
	 * @param callback
	 */
	public void addToMyTravelList(int planId, final APIStringResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=copyPlanToPlan";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planId", String.valueOf(planId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "添加收藏错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 删除我的行程
	 *
	 * @param planId
	 * @param callback
	 */
	public void deleteMyTravel(int planId, final APIStringResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=delPlan";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planId", String.valueOf(planId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.delete_mine_travel_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "删除我的行程错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 更新我的行程时间
	 *
	 * @param startDate
	 * @param planId
	 * @param callback
	 */
	public void updateMyTravelDate(long startDate, int planId, final APIStringResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=changePlanDate";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("startDate", String.valueOf(startDate));
		paramsMaps.put("planId", String.valueOf(planId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.set_mine_travel_date_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "设置行程时间错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 设置当前行程
	 *
	 * @param planId
	 * @param callback
	 */
	public void setCurrentTravel(int planId, final APICurrentResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=setCurrentPlan";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planId", String.valueOf(planId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						int oldMtime = dataObject.getInt("oldMtime");
						int newMtime = dataObject.getInt("newMtime");
						if (flag == 1) {
							callback.result(null, oldMtime, newMtime);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg, 0, 0);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0, 0);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.set_current_travel_fail);
					callback.result(errMsg, 0, 0);
					Log.e(Define.LOG_TAG, "设置当前行程错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0, 0);
			}
		});
	}

	/**
	 * 取消当前行程
	 *
	 * @param planId
	 * @param callback
	 */
	public void cancelCurrentTravel(int planId, final APIStringResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=cancelCurrentPlan";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planId", String.valueOf(planId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.cancel_current_travel_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "取消当前行程错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 设置行程以后出发
	 *
	 * @param planId
	 * @param callback
	 */
	public void setAfterCurrentPlan(int planId, final APIStringResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=setAfterCurrentPlan";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planId", String.valueOf(planId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.setting_travel_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "设置行程以后出发行程错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取当前行程
	 *
	 * @param callback
	 */
	public void getCurrentTravel(final APICurrentTravelResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=getCurrentPlan";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					try {
						CurrentTravel currentTravel = JsonTools.jsonObj(jsonData, CurrentTravel.class);
						callback.result(null, currentTravel);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_current_travel_fail);
						callback.result(errMsg, null);
						Log.e(Define.LOG_TAG, "获取当前行程错误\n" + e.toString());
					}
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取行程评论列表
	 *
	 * @param id
	 * @param module
	 *            评论类别；location:景点 hotel:酒店 plan:计划 record:record表
	 *            lifestyle:吃喝玩乐 team:团单
	 * @param pageNum
	 * @param pageSize
	 * @param callback
	 */
	public void loadCommentList(int id, String module, int pageNum, int pageSize, final APICommentListResultCallback callback) {
		String subURL = "mod=mobileWhite" + INTERFACE_VERSION + "&act=getComment";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("id", String.valueOf(id));
		paramsMaps.put("module", module);
		paramsMaps.put("pageSize", String.valueOf(pageSize));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						int commentCount = dataObject.getInt("commentCount");
						JSONArray commentArray = dataObject.getJSONArray("commentList");
						List<Comment> comments = JsonTools.jsonObjArray(commentArray, Comment.class);
						callback.result(null, comments, commentCount);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_product_comments_fail);
						callback.result(errMsg, null, 0);
						Log.e(Define.LOG_TAG, "获取行程评论列表错误\n" + e.toString());
					}
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 提交评论
	 *
	 * @param id
	 *            评论目标id
	 * @param content
	 *            评论的内容
	 * @param rank
	 *            综合评分
	 * @param pid
	 *            评论父id（0代表第一次评论）
	 * @param module
	 *            行程：plan，景点：location，酒店hotel，美食：lifestyle_food，娱乐：
	 *            lifestyle_funny，购物:lifestyle_shopping
	 * @param callback
	 */
	public void commitComment(int id, String content, float rank, int pid, String module, final APIStringResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=saveComment";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("id", String.valueOf(id));
		paramsMaps.put("content", content);
		paramsMaps.put("rank", String.valueOf(rank));
		paramsMaps.put("pid", String.valueOf(pid));
		paramsMaps.put("module", module);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.commit_comment_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "发表评论错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取附近列表
	 *
	 * @param cityId
	 * @param type
	 *            类型：景点 location 酒店 hotel 饮食 lifestyle_food 购物
	 *            lifestyle_shopping 娱乐 lifestyle_funny
	 * @param pageNum
	 */
	public void loadAroundList(double lon, double lat, int cityId, String type, int pageNum, final APIAroundListResultCallback callback) {
		String subURL = "mod=mobile" + INTERFACE_VERSION + "&act=RddGetInfoList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		if (cityId != 0) {
			paramsMaps.put("city_id", String.valueOf(cityId));
		}
		paramsMaps.put("type", type);
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		if (lon != 0) {
			paramsMaps.put("lon", String.valueOf(lon));
		}
		if (lat != 0) {
			paramsMaps.put("lat", String.valueOf(lat));
		}

		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						int pageCount = dataObject.getInt("total_num");
						JSONArray aroundArray = dataObject.getJSONArray("infoList");
						List<Around> arounds = JsonTools.jsonObjArray(aroundArray, Around.class);
						callback.result(null, arounds, pageCount);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_product_comments_fail);
						callback.result(errMsg, null, 0);
						Log.e(Define.LOG_TAG, "获取附近列表错误\n" + e.toString());
					}
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取附近景点详情页
	 *
	 * @param isOffline
	 * @param uid
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadScenicDetail(boolean isOffline, int uid, int travelId, int id, final APIScenicDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/location_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				ScenicDetail scenicDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), ScenicDetail.class);
				callback.result(null, scenicDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=mobile" + INTERFACE_VERSION + "&act=RddGetInfoDetail";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("uid", String.valueOf(uid));
			paramsMaps.put("type", Define.LOCATION);
			paramsMaps.put("map_type", String.valueOf(0)); // 坐标类型：0：百度，1：谷歌
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						ScenicDetail scenicDetail = JsonTools.jsonObj(jsonData, ScenicDetail.class);
						callback.result(null, scenicDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取自定义景点详情页
	 *
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadCustomScenicDetail(boolean isOffline, int travelId, int id, final APICustomScenicDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/custom_location_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				CustomScenicDetail customScenicDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), CustomScenicDetail.class);
				callback.result(null, customScenicDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=Mobile" + INTERFACE_VERSION + "&act=getNodeCustom";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						CustomScenicDetail customScenicDetail = JsonTools.jsonObj(jsonData, CustomScenicDetail.class);
						callback.result(null, customScenicDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取附近酒店详情页
	 *
	 * @param isOffline
	 * @param uid
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadHotelDetail(boolean isOffline, int uid, int travelId, int id, final APIHotelDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/hotel_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				HotelDetail hotelDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), HotelDetail.class);
				callback.result(null, hotelDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=mobile" + INTERFACE_VERSION + "&act=RddGetInfoDetail";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("uid", String.valueOf(uid));
			paramsMaps.put("type", Define.HOTEL);
			paramsMaps.put("map_type", String.valueOf(0)); // 坐标类型：0：百度，1：谷歌
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						HotelDetail hotelDetail = JsonTools.jsonObj(jsonData, HotelDetail.class);
						callback.result(null, hotelDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取自定义酒店详情页
	 *
	 * @param isOffline
	 *            是否离线
	 * @param id
	 *            类型对应的ID
	 */
	public void loadCustomHotelDetail(boolean isOffline, int travelId, int id, final APICustomHotelDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/custom_hotel_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				CustomHotelDetail customHotelDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), CustomHotelDetail.class);
				callback.result(null, customHotelDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=Mobile" + INTERFACE_VERSION + "&act=getNodeCustom";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						CustomHotelDetail customHotelDetail = JsonTools.jsonObj(jsonData, CustomHotelDetail.class);
						callback.result(null, customHotelDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取附近美食详情页
	 *
	 * @param isOffline
	 * @param uid
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadFoodDetail(boolean isOffline, int uid, int travelId, int id, final APIFoodDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/lifestyle_food_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				FoodDetail foodDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), FoodDetail.class);
				callback.result(null, foodDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=mobile" + INTERFACE_VERSION + "&act=RddGetInfoDetail";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("uid", String.valueOf(uid));
			paramsMaps.put("type", Define.FOOD);
			paramsMaps.put("map_type", String.valueOf(0)); // 坐标类型：0：百度，1：谷歌
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						FoodDetail foodDetail = JsonTools.jsonObj(jsonData, FoodDetail.class);
						callback.result(null, foodDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取自定义美食详情页
	 *
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadCustomFoodDetail(boolean isOffline, int travelId, int id, final APICustomFoodDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/custom_food_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				CustomFoodDetail customFoodDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), CustomFoodDetail.class);
				callback.result(null, customFoodDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=Mobile" + INTERFACE_VERSION + "&act=getNodeCustom";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						CustomFoodDetail customFoodDetail = JsonTools.jsonObj(jsonData, CustomFoodDetail.class);
						callback.result(null, customFoodDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取附近购物详情页
	 *
	 * @param isOffline
	 *            是否离线
	 * @param uid
	 *            用户id
	 * @param id
	 *            类型对应的ID
	 */
	public void loadShoppingDetail(boolean isOffline, int uid, int travelId, int id, final APIShoppingDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/lifestyle_shopping_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				ShoppingDetail shoppingDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), ShoppingDetail.class);
				callback.result(null, shoppingDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=mobile" + INTERFACE_VERSION + "&act=RddGetInfoDetail";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("uid", String.valueOf(uid));
			paramsMaps.put("type", Define.SHOPPING);
			paramsMaps.put("map_type", String.valueOf(0)); // 坐标类型：0：百度，1：谷歌
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						ShoppingDetail shoppingDetail = JsonTools.jsonObj(jsonData, ShoppingDetail.class);
						callback.result(null, shoppingDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取自定义购物详情页
	 *
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadCustomShoppingDetail(boolean isOffline, int travelId, int id, final APICustomAmusementAndShoppingDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/custom_shopping_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				CustomAmusementAndShopping customShoppingDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), CustomAmusementAndShopping.class);
				callback.result(null, customShoppingDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=Mobile" + INTERFACE_VERSION + "&act=getNodeCustom";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						CustomAmusementAndShopping customShoppingDetail = JsonTools.jsonObj(jsonData, CustomAmusementAndShopping.class);
						callback.result(null, customShoppingDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取附近娱乐详情页
	 *
	 * @param isOffline
	 *            是否离线
	 * @param uid
	 *            用户id
	 * @param id
	 *            类型对应的ID
	 */
	public void loadAmusementDetail(boolean isOffline, int uid, int travelId, int id, final APIAmusementDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/lifestyle_funny_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				AmusementDetail amusementDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), AmusementDetail.class);
				callback.result(null, amusementDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=mobile" + INTERFACE_VERSION + "&act=RddGetInfoDetail";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("uid", String.valueOf(uid));
			paramsMaps.put("type", Define.AMUSEMENT);
			paramsMaps.put("map_type", String.valueOf(0)); // 坐标类型：0：百度，1：谷歌
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						AmusementDetail amusementDetail = JsonTools.jsonObj(jsonData, AmusementDetail.class);
						callback.result(null, amusementDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取自定义娱乐详情页
	 *
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadCustomAmusementDetail(boolean isOffline, int travelId, int id, final APICustomAmusementAndShoppingDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/custom_entertainment_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				CustomAmusementAndShopping customAmusementDetail = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), CustomAmusementAndShopping.class);
				callback.result(null, customAmusementDetail);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=Mobile" + INTERFACE_VERSION + "&act=getNodeCustom";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						CustomAmusementAndShopping customAmusementDetail = JsonTools.jsonObj(jsonData, CustomAmusementAndShopping.class);
						callback.result(null, customAmusementDetail);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 获取其他自定义详情页
	 *
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadOtherCustomDetail(boolean isOffline, String type, int travelId, int id, final APICustomEventDetailResultCallback callback) {
		if (isOffline) {
			String travelDetailPath = KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/node/" + type + "_" + id + ".txt";
			String json = FileUtil.readFileFromSdcard(travelDetailPath);
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
				CustomEvent customEvent = JsonTools.jsonObj(jsonObject.getJSONObject("datas").toString(), CustomEvent.class);
				callback.result(null, customEvent);
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		} else {
			String subURL = "mod=Mobile" + INTERFACE_VERSION + "&act=getNodeCustom";
			String requestURL = buildRequestURL(subURL);
			HashMap<String, String> paramsMaps = new HashMap<String, String>();
			paramsMaps.put("id", String.valueOf(id));
			post(requestURL, paramsMaps, new APIRequestCallBack() {

				@Override
				public void onSuccess(JSONObject jsonObject) {
					String[] dataArray = getJsonObjDataOrErr(jsonObject);
					String errMsg = dataArray[0];
					if (errMsg == null) {
						String jsonData = dataArray[1];
						CustomEvent customEvent = JsonTools.jsonObj(jsonData, CustomEvent.class);
						callback.result(null, customEvent);
					} else {
						callback.result(errMsg, null);
					}
				}

				@Override
				public void onFailure(String errMsg) {
					callback.result(errMsg, null);
				}
			});
		}
	}

	/**
	 * 上传头像
	 *
	 * @param filePath
	 * @param callback
	 */
	public void uploadUserAvatar(String filePath, final APIStringResultCallback callback) {
		String subURL = "mod=mobileUser" + INTERFACE_VERSION + "&act=RddUpdateUserFace";
		String requestURL = buildRequestURL(subURL);

		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(new OnUploadProcessListener() {

			@Override
			public void onUploadProcess(int uploadSize) {
			}

			@Override
			public void onUploadDone(int responseCode, String message) {
				try {
					JSONObject jsonObject = new JSONObject(message);
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							callback.result("上传头像失败");
						}

					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "上传头像出错'n" + e.toString());
				}
			}

			@Override
			public void initUpload(int fileSize) {
			}
		});

		Map<String, String> params = new HashMap<String, String>();
		params.put("key", KEY);
		String apiKey = AppSetting.getInstance().getStringPreferencesByKey(Define.ApiKey);
		String apiKeySecret = AppSetting.getInstance().getStringPreferencesByKey(Define.ApiKeySecret);
		params.put(Define.ApiKey, apiKey);
		params.put(Define.ApiKeySecret, apiKeySecret);
		uploadUtil.uploadFile(true, filePath, "Filedata", requestURL, params);
	}

	/**
	 * 上传店铺logo或店铺背景
	 *
	 * @param sid
	 *            店铺id
	 * @param shopType
	 *            店铺类型（a、c）
	 * @param imgType
	 *            图片类型（logo、bg_image）
	 * @param filePath
	 *            图片路径
	 * @param callback
	 */
	public void uploadStoreImage(int sid, String shopType, String imgType, String filePath, final APIStringResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=uploadShopImage";
		String requestURL = buildRequestURL(subURL);

		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(new OnUploadProcessListener() {

			@Override
			public void onUploadProcess(int uploadSize) {
			}

			@Override
			public void onUploadDone(int responseCode, String message) {
				try {
					JSONObject jsonObject = new JSONObject(message);
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							callback.result("上传店铺图片失败");
						}

					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "上传店铺图片出错\n" + e.toString());
				}
			}

			@Override
			public void initUpload(int fileSize) {
			}
		});

		Map<String, String> params = new HashMap<String, String>();
		params.put("key", KEY);
		String apiKey = AppSetting.getInstance().getStringPreferencesByKey(Define.ApiKey);
		String apiKeySecret = AppSetting.getInstance().getStringPreferencesByKey(Define.ApiKeySecret);
		params.put(Define.ApiKey, apiKey);
		params.put(Define.ApiKeySecret, apiKeySecret);
		params.put("sid", String.valueOf(sid));
		params.put("shopType", shopType);
		params.put("imgType", imgType);
		uploadUtil.uploadFile(true, filePath, "img", requestURL, params);
	}

	/**
	 * 上传日志文件
	 *
	 * @param filePath
	 * @param callback
	 */
	public void uploadLogFile(String filePath, final APIStringResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=collapseLog";
		String requestURL = buildRequestURL(subURL);

		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(new OnUploadProcessListener() {

			@Override
			public void onUploadProcess(int uploadSize) {
			}

			@Override
			public void onUploadDone(int responseCode, String message) {
				try {
					JSONObject jsonObject = new JSONObject(message);
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							callback.result("上传日志失败");
						}

					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "上传日志出错'n" + e.toString());
				}
			}

			@Override
			public void initUpload(int fileSize) {
			}
		});

		Map<String, String> params = new HashMap<String, String>();
		params.put("key", KEY);
		params.put("type", "android");
		uploadUtil.uploadFile(false, filePath, "Filedata", requestURL, params);
	}

	/**
	 * 更改店铺信息
	 *
	 * @param sid
	 * @param type
	 * @param name
	 * @param mobile
	 * @param callback
	 */
	public void updateStoreInfo(int sid, String type, String name, String mobile, final APIStringResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=editorShopInfo";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("sid", String.valueOf(sid));
		paramsMaps.put("type", type);
		if (name != null) {
			paramsMaps.put("name", name);
		}
		if (mobile != null) {
			paramsMaps.put("mobile", mobile);
		}
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.update_store_info_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "更改店铺信息错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 更改订单状态
	 *
	 * @param id
	 * @param status
	 * @param callback
	 */
	public void updateOrderStatus(int id, int status, final APIStringResultCallback callback) {
		String subURL = "mod=MobileOrder" + INTERFACE_VERSION + "&act=changeOrderStatus";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("id", String.valueOf(id));
		paramsMaps.put("status", String.valueOf(status));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "更改订单状态错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 取消订单
	 *
	 * @param id
	 * @param status
	 * @param callback
	 */
	public void cancelOrder(int id, String reason, final APIStringResultCallback callback) {
		String subURL = "mod=MobileOrder" + INTERFACE_VERSION + "&act=buyCancel";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("order_id", String.valueOf(id));
		paramsMaps.put("reason", reason);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					int flag = dataObject.getInt("flag");
					if (flag == 1) {
						callback.result(null);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.collect_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "取消订单失败\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 提交订单点评
	 *
	 * @param orderId
	 * @param mark1
	 * @param mark2
	 * @param mark3
	 * @param content
	 * @param callback
	 */
	public void commitOrderReview(int orderId, int mark1, int mark2, int mark3, String content, final APIStringResultCallback callback) {
		String subURL = "mod=MobileOrder" + INTERFACE_VERSION + "&act=orderReview";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("orderId", String.valueOf(orderId));
		paramsMaps.put("mark1", String.valueOf(mark1));
		if (mark2 != 0) {
			paramsMaps.put("mark2", String.valueOf(mark2));
		}
		if (mark3 != 0) {
			paramsMaps.put("mark3", String.valueOf(mark3));
		}
		paramsMaps.put("content", content);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.commit_order_review_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "提交订单点评错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取店铺
	 *
	 * @param pageNum
	 * @param shopId
	 * @param shopType
	 * @param productType
	 *            "类型： 全部：0 ，线路：4， 组合：2， 酒店：5， 美食：8， 娱乐：7"
	 * @param callback
	 */
	public void loadStore(int pageNum, boolean isMyStore, int shopId, String shopType, int productType, final APIStoreAndProductListResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=myShop";
		if (!isMyStore) { // 他人的店铺
			subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=othersShop";
		}
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("shopId", String.valueOf(shopId));
		paramsMaps.put("shopType", shopType);
		paramsMaps.put("productType", String.valueOf(productType));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						// 页数
						int pageCount = dataObject.getInt("pageCount");
						// 店铺信息
						JSONObject storeObject = dataObject.getJSONObject("shopInfo");
						Store store = JsonTools.jsonObj(storeObject.toString(), Store.class);
						// 产品列表
						JSONArray productArray = dataObject.getJSONArray("productList");
						List<Product> products = JsonTools.jsonObjArray(productArray, Product.class);

						callback.result(null, store, products, pageCount);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_store_fail);
						callback.result(errMsg, null, null, 0);
					}

				} else {
					callback.result(errMsg, null, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, null, 0);
			}
		});
	}

	/**
	 * 提交产品预订订单
	 *
	 * @param adult
	 * @param child
	 * @param startTime
	 * @param conractName
	 * @param conractMobile
	 * @param conractEmail
	 * @param joiner
	 * @param shopType
	 * @param shopId
	 * @param productId
	 * @param salePrice
	 * @param callback
	 */
	public void reserveProduct(String message, int priceId, String title, int isPayDeposit, float totalPrice, int adult, int child, long startTime, String conractName, String conractMobile, String conractEmail, String joiner, String shopType, int shopId, int productId, String salePrice, final APIReserveProductResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=planProductReserve";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("adult", String.valueOf(adult));
		paramsMaps.put("child", String.valueOf(child));
		paramsMaps.put("startTime", String.valueOf(startTime));
		paramsMaps.put("conractName", conractName);
		paramsMaps.put("conractMobile", conractMobile);
		paramsMaps.put("conractEmail", conractEmail);
		paramsMaps.put("joiner", joiner);
		paramsMaps.put("shopType", shopType);
		paramsMaps.put("shopId", String.valueOf(shopId));
		paramsMaps.put("productId", String.valueOf(productId));
		paramsMaps.put("salePrice", salePrice);
		paramsMaps.put("message", message);
		paramsMaps.put("packageId", String.valueOf(priceId));
		paramsMaps.put("packageTitle", title);
		paramsMaps.put("isPayDeposit", String.valueOf(isPayDeposit));
		paramsMaps.put("depositNum", String.valueOf(totalPrice));
		paramsMaps.put("mobileSystem", "android");
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							int orderId = dataObject.getInt("id");
							int orderNum = dataObject.getInt("sn");
							// 订单对应的凭证id
							int invoiceId = dataObject.getInt("invoice_id");
							// 支付平台标识(1:店铺只允许线下支付 0:店铺只允许线上支付)
							int isOffline = dataObject.getInt("isOffline");
							// 店铺logo
							String shopLogo = dataObject.getString("shop_logo");
							// 产品logo
							String productLogo = dataObject.getString("product_logo");
							JSONArray paymentsArray = dataObject.getJSONArray("pay_method");
							// 支付方式列表
							List<Payment> payments = JsonTools.jsonObjArray(paymentsArray, Payment.class);

							callback.result(null, orderId, orderNum, invoiceId, isOffline, shopLogo, productLogo, payments);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg, 0, 0, 0, 0, null, null, null);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0, 0, 0, 0, null, null, null);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.submit_order_fail);
					callback.result(errMsg, 0, 0, 0, 0, null, null, null);
					Log.e(Define.LOG_TAG, "提交产品预订订单错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0, 0, 0, 0, null, null, null);
			}
		});
	}

	/**
	 * 加载产品套餐列表
	 *
	 * @param pid
	 * @param callback
	 */
	public void loadProductPackageList(int pid, final APIProductpackageResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productPackage";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pid", String.valueOf(pid));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						long serviceTime = dataObject.getLong("serviceTime");
						int isShowStock = dataObject.getInt("isShowStock");
						int isBook = dataObject.getInt("isBook");
						int minBuy = dataObject.getInt("minBuy");
						int isMinBuy = dataObject.getInt("isMinBuy");
						JSONArray jsonArray = dataObject.getJSONArray("packageList");
						List<ProductPackage> productPackages = JsonTools.jsonObjArray(jsonArray, ProductPackage.class);
						callback.result(null, serviceTime, isShowStock, isMinBuy, minBuy, isBook, productPackages);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0, 0, 0, 0, 0, null);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.load_product_package_fail);
					callback.result(errMsg, 0, 0, 0, 0, 0, null);
					Log.e(Define.LOG_TAG, "获取产品套餐错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0, 0, 0, 0, 0, null);
			}
		});
	}

	/**
	 * 获取我的订单列表
	 *
	 * @param pageNum
	 * @param callback
	 */
	public void loadMyOrderList(int pageNum, final APIMyOrderListResultCallback callback) {
		String subURL = "mod=MobileOrder" + INTERFACE_VERSION + "&act=selfPurchaseOrder";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					JSONArray orderArray = null;
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						pageCount = dataObject.getInt("pageCount");
						orderArray = dataObject.getJSONArray("orderList");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取订单列表错误\n" + e.toString());
					}

					List<MyOrder> myOrders = null;
					if (orderArray != null) {
						myOrders = JsonTools.jsonObjArray(orderArray, MyOrder.class);
					}
					callback.result(null, myOrders, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取订单详情页
	 *
	 * @param orderId
	 * @param callback
	 */
	public void loadOrderDetail(int orderId, final APIOrderDetailResultCallback callback) {
		String subURL = "mod=MobileOrder" + INTERFACE_VERSION + "&act=selfOrderDetailed";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("orderId", String.valueOf(orderId));
		paramsMaps.put("pageSize", String.valueOf(Integer.MAX_VALUE));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					OrderDetail orderDetail = JsonTools.jsonObj(jsonData, OrderDetail.class);
					callback.result(null, orderDetail);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取C店店铺销售统计
	 *
	 * @param pageNum
	 * @param day
	 *            天数(0:全部 1:今天 7：一个星期 30:一个月)
	 */
	public void loadStoreSalesStatistics(int pageNum, int day, final APIStoreSalesStatisticsResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=salesStatisticsC";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("salesType", "shop");
		paramsMaps.put("day", String.valueOf(day));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						pageCount = dataObject.getInt("pageCount");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取店铺销售统计错误\n" + e.toString());
					}
					StoreSalesStatistics storeSalesStatistics = JsonTools.jsonObj(jsonData, StoreSalesStatistics.class);
					callback.result(errMsg, storeSalesStatistics, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取C店产品销售统计
	 *
	 * @param pageNum
	 * @param productType
	 *            产品类型： 全部：0 ，线路：4， 组合：2， 酒店：5， 美食：8， 娱乐：7
	 * @param day
	 *            天数(0:全部 1:今天 7：一个星期 30:一个月)
	 */
	public void loadProductSalesStatistics(int pageNum, int productType, int day, final APIProductSalesStatisticsResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=salesStatisticsC";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("productType", String.valueOf(productType));
		paramsMaps.put("salesType", "product");
		paramsMaps.put("day", String.valueOf(day));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						pageCount = dataObject.getInt("pageCount");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取产品销售统计错误\n" + e.toString());
					}
					ProductSalesStatistics productSalesStatistics = JsonTools.jsonObj(jsonData, ProductSalesStatistics.class);
					callback.result(errMsg, productSalesStatistics, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取A店自销统计
	 *
	 * @param pageNum
	 * @param productType
	 *            产品类型： 全部：0 ，线路：4， 组合：2， 酒店：5， 美食：8， 娱乐：7
	 * @param day
	 *            天数(0:全部 1:今天 7：一个星期 30:一个月)
	 */
	public void loadSelfLockingSalesStatistics(int pageNum, int productType, int day, final APISelfLockingSalesStatisticsResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=salesStatisticsA";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("productType", String.valueOf(productType));
		paramsMaps.put("salesType", "self");
		paramsMaps.put("day", String.valueOf(day));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						pageCount = dataObject.getInt("pageCount");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取店铺自销统计错误\n" + e.toString());
					}
					SelfLockingSalesStatistics selfLockingSalesStatistics = JsonTools.jsonObj(jsonData, SelfLockingSalesStatistics.class);
					callback.result(errMsg, selfLockingSalesStatistics, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取A店总销统计
	 *
	 * @param pageNum
	 * @param productType
	 *            产品类型： 全部：0 ，线路：4， 组合：2， 酒店：5， 美食：8， 娱乐：7
	 * @param day
	 *            天数(0:全部 1:今天 7：一个星期 30:一个月)
	 */
	public void loadStoreTotalSalesStatistics(int pageNum, int productType, int day, final APIStoreTotalSalesStatisticsResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=salesStatisticsA";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("productType", String.valueOf(productType));
		paramsMaps.put("salesType", "shop");
		paramsMaps.put("day", String.valueOf(day));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						pageCount = dataObject.getInt("pageCount");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取店铺总销统计错误\n" + e.toString());
					}
					StoreTotalSalesStatistics storeTotalSalesStatistics = JsonTools.jsonObj(jsonData, StoreTotalSalesStatistics.class);
					callback.result(errMsg, storeTotalSalesStatistics, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取A店分销店铺统计
	 *
	 * @param pageNum
	 * @param productType
	 *            产品类型： 全部：0 ，线路：4， 组合：2， 酒店：5， 美食：8， 娱乐：7
	 * @param day
	 *            天数(0:全部 1:今天 7：一个星期 30:一个月)
	 */
	public void loadStoreDistributionSalesStatistics(int pageNum, int productType, int day, final APIStoreDistributionSalesStatisticsResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=salesStatisticsA";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("productType", String.valueOf(productType));
		paramsMaps.put("salesType", "othersShop");
		paramsMaps.put("day", String.valueOf(day));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						pageCount = dataObject.getInt("pageCount");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取分销店铺统计错误\n" + e.toString());
					}
					StoreDistributionSalesStatistics storeDistributionSalesStatistics = JsonTools.jsonObj(jsonData, StoreDistributionSalesStatistics.class);
					callback.result(errMsg, storeDistributionSalesStatistics, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取A店分销产品统计
	 *
	 * @param pageNum
	 * @param productType
	 *            产品类型： 全部：0 ，线路：4， 组合：2， 酒店：5， 美食：8， 娱乐：7
	 * @param day
	 *            天数(0:全部 1:今天 7：一个星期 30:一个月)
	 */
	public void loadProductDistributionSalesStatistics(int pageNum, int productType, int day, final APIProductDistributionSalesStatisticsResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=salesStatisticsA";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("productType", String.valueOf(productType));
		paramsMaps.put("salesType", "othersProduct");
		paramsMaps.put("day", String.valueOf(day));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						pageCount = dataObject.getInt("pageCount");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取分销产品统计错误\n" + e.toString());
					}
					ProductDistributionSalesStatistics productDistributionSalesStatistics = JsonTools.jsonObj(jsonData, ProductDistributionSalesStatistics.class);
					callback.result(errMsg, productDistributionSalesStatistics, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取我的收藏列表
	 *
	 * @param pageNum
	 * @param callback
	 */
	public void loadMyCollects(int pageNum, final APICollectListResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=getFavoriteList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					JSONArray collectArray = null;
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						pageCount = dataObject.getInt("pageCount");
						collectArray = dataObject.getJSONArray("favoriteList");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取我的收藏列表错误\n" + e.toString());
					}

					List<Collect> collects = null;
					if (collectArray != null) {
						collects = JsonTools.jsonObjArray(collectArray, Collect.class);
					}
					callback.result(null, collects, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 产品库
	 *
	 * @param type
	 * @param keyWord
	 * @param pageNum
	 */
	public void loadProductLibrary(int type, String keyWord, int pageNum, final APIProductListResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productWareroom";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("type", String.valueOf(type));
		if (keyWord != null && !"".equals(keyWord)) {
			paramsMaps.put("keyWord", keyWord);
		}

		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					JSONArray productJsonArray = null;
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						pageCount = dataObject.getInt("pageCount");
						productJsonArray = dataObject.getJSONArray("productList");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取产品库产品列表错误\n" + e.toString());
					}

					List<Product> products = null;
					if (productJsonArray != null) {
						products = JsonTools.jsonObjArray(productJsonArray, Product.class);
					}
					callback.result(null, products, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 添加产品
	 *
	 * @param productId
	 *            json格式
	 * @param callback
	 */
	public void addProduct(String productId, final APIStringResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=addAffiliateProduct";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("productId", productId);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.logout_fail);
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "添加产品错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.logout_fail);
					callback.result(errMsg);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取佣金列表
	 *
	 * @param type
	 *            店铺类型（a、c）
	 * @param pageNum
	 * @param callback
	 */
	public void loadCommissionList(String type, int pageNum, final APICommissionListResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=brokerageManagement";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("type", type);
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						// 页数
						int pageCount = dataObject.getInt("pageCount");
						// 佣金列表
						JSONArray brokerageArray = dataObject.getJSONArray("brokerageList");
						List<Commission> commissions = JsonTools.jsonObjArray(brokerageArray, Commission.class);
						callback.result(null, commissions, pageCount);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_commission_fail);
						callback.result(errMsg, null, 0);
					}

				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 佣金支付
	 *
	 * @param selfShopId
	 *            自己店铺id（A店）
	 * @param othersShopId
	 *            他人店铺id（C店）
	 * @param defrayType
	 *            支付方式 （1线下支付，2系统入账)
	 * @param defrayMoney
	 *            支付金额
	 * @param unDefrayMoney
	 *            待支付佣金
	 * @param defrayRemark
	 *            支付备注
	 * @param defrayTime
	 *            时间戳
	 * @param callback
	 */
	public void brokerageDefray(int selfShopId, int othersShopId, int defrayType, float defrayMoney, float unDefrayMoney, String defrayRemark, long defrayTime, final APIStringResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=brokerageDefray";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("selfShopId", String.valueOf(selfShopId));
		paramsMaps.put("othersShopId", String.valueOf(othersShopId));
		paramsMaps.put("defrayType", String.valueOf(defrayType));
		paramsMaps.put("defrayMoney", String.valueOf(defrayMoney));
		paramsMaps.put("unDefrayMoney", String.valueOf(unDefrayMoney));
		if (!"".equals(defrayRemark)) {
			paramsMaps.put("defrayRemark", defrayRemark);
		}
		paramsMaps.put("defrayTime", String.valueOf(defrayTime));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.logout_fail);
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "佣金支付错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.payment_fail);
					callback.result(errMsg);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 加载佣金详细页
	 *
	 * @param othersShopId
	 *            他人店铺id
	 * @param selfShopId
	 *            自己店铺id
	 * @param shopType
	 *            店铺类型（a、c）
	 * @param pageNum
	 *            第几页(默认为1）
	 * @param callback
	 */
	public void loadCommissionDetail(int pageNum, int othersShopId, int selfShopId, String shopType, final APICommissionDetailResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=brokerageDetailed";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("othersShopId", String.valueOf(othersShopId));
		paramsMaps.put("selfShopId", String.valueOf(selfShopId));
		paramsMaps.put("shopType", shopType);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						pageCount = dataObject.getInt("pageCount");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取佣金详细错误\n" + e.toString());
					}
					CommissionDetail commissionDetail = JsonTools.jsonObj(jsonData, CommissionDetail.class);
					callback.result(errMsg, commissionDetail, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取客户列表
	 *
	 * @param type
	 *            类型（客户：client、分销商：sell）
	 * @param callback
	 */
	public void loadClientList(String type, final APICustomerInfoListResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=clientManagement";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("type", type);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonArray = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonArray);
						// 佣金列表
						JSONArray brokerageArray = dataObject.getJSONArray("contactList");
						List<CustomerInfo> customerInfos = JsonTools.jsonObjArray(brokerageArray, CustomerInfo.class);
						callback.result(null, customerInfos);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_client_fail);
						callback.result(errMsg, null);
					}

				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取客户详细
	 *
	 * @param pageNum
	 * @param clientId
	 * @param callback
	 */
	public void loadStoreClientDetail(int pageNum, int clientId, final APICustomerDetailResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=clientDetails";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("clientId", String.valueOf(clientId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					int pageCount = 0; // 总共有多少页的数据
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						pageCount = dataObject.getInt("pageCount");
					} catch (Exception e) {
						Log.e(Define.LOG_TAG, "获取客户详细错误\n" + e.toString());
					}
					CustomerDetail customerDetail = JsonTools.jsonObj(jsonData, CustomerDetail.class);
					callback.result(errMsg, customerDetail, pageCount);
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 开店
	 *
	 * @param storeType
	 * @param othersShopId
	 * @param name
	 * @param mobile
	 * @param callback
	 */
	public void openAStore(String storeType, int othersShopId, String name, String mobile, final APIOpenStoreResultCallback callback) {
		String subURL = "mod=MobileShop" + INTERFACE_VERSION + "&act=establishShopC";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		if (othersShopId != 0) {
			paramsMaps.put("othersShopId", String.valueOf(othersShopId));
		}
		paramsMaps.put("name", name);
		paramsMaps.put("mobile", mobile);
		if (storeType != null) {
			paramsMaps.put("shopType", storeType);
		}
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						int storeId = dataObject.getInt("id");
						if (flag == 1) {
							callback.result(null, storeId);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg, 0);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "开店错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.update_user_info_err);
					callback.result(errMsg, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0);
			}
		});
	}

	/**
	 * 二维码链接解码
	 *
	 * @param url
	 * @param callback
	 */
	public void urlResolve(String url, final APIURLResolveResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=urlResolve";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("url", url);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int id = dataObject.getInt("id");
						String type = dataObject.getString("type");
						int distributorId = dataObject.getInt("distributorId");
						String commonType = dataObject.getString("commonType");
						callback.result(null, id, type, distributorId, commonType);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0, null, 0, null);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "二维码扫描解码错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.qrcode_scan_request_fail);
					callback.result(errMsg, 0, null, 0, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0, null, 0, null);
			}
		});
	}

	/**
	 * 获取离线包
	 *
	 * @param planId
	 * @param again
	 *            是否重新打包(0：否，1：是)
	 * @param callback
	 */
	public void getOfflineZip(int planId, int again, final APIGetOfflineResultCallback callback) {
		String subURL = "mod=mobileWhite" + INTERFACE_VERSION + "&act=offlineDataPacket";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planId", String.valueOf(planId));
		paramsMaps.put("again", String.valueOf(again));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						String zipPath = dataObject.getString("zipPath");
						double zipSize = dataObject.getDouble("zipSize");
						long mTime = dataObject.getLong("mtime");
						String zipSizeString;
						zipSize = zipSize / 1024; // kb
						zipSizeString = ((int) zipSize) + "kb";
						if (zipSize >= 1024) {
							zipSizeString = "";
							zipSize = zipSize / 1024; // MB
							zipSizeString = ((int) zipSize) + "MB";
						}
						callback.result(null, zipPath, zipSizeString, mTime);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null, null, 0);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "生成离线数据包错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.download_offline_fail);
					callback.result(errMsg, null, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, null, 0);
			}
		});
	}

	/**
	 * 检查离线更新
	 *
	 * @param offlineIdJsonArray
	 * @param callback
	 */
	public void checkOfflineUpdate(String offlineIdJsonArray, final APIOfflineUpdateInfoListResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=packetMtime";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planIdArr", String.valueOf(offlineIdJsonArray));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						JSONArray timeList = dataObject.getJSONArray("timeList");
						List<OfflineUpdateInfo> offlineUpdateInfos = JsonTools.jsonObjArray(timeList, OfflineUpdateInfo.class);
						callback.result(null, offlineUpdateInfos);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "检查离线更新错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.check_offline_update_fail);
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 用户行程反馈
	 *
	 * @param recordId
	 * @param lineScore
	 * @param serviceScore
	 * @param repastScore
	 * @param content
	 * @param callback
	 */
	public void commitTravelFeedback(int travelId, float lineScore, float serviceScore, float repastScore, String content, final APIStringResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=feedback";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("recordId", String.valueOf(travelId));
		paramsMaps.put("lineScore", String.valueOf(lineScore));
		paramsMaps.put("serviceScore", String.valueOf(serviceScore));
		paramsMaps.put("repastScore", String.valueOf(repastScore));
		paramsMaps.put("content", content);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.logout_fail);
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.commit_travel_feed_back_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "提交行程反馈错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取行程反馈（用户反馈）
	 *
	 * @param travelId
	 * @param pageNum
	 * @param callback
	 */
	public void loadTravelFeedback(int travelId, int pageNum, final APITravelFeedBackResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=getCommentList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planId", String.valueOf(travelId));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int totalCount = dataObject.getInt("commentCount");
						float lineScore = (float) dataObject.getDouble("lineScore");
						float serviceScore = (float) dataObject.getDouble("serviceScore");
						float repastScore = (float) dataObject.getDouble("repastScore");
						List<Comment> comments = JsonTools.jsonObjArray(dataObject.getJSONArray("commentList"), Comment.class);
						callback.result(null, lineScore, serviceScore, repastScore, comments, totalCount);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0, 0, 0, null, 0);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.load_travel_feed_back_list_fail);
					callback.result(errMsg, 0, 0, 0, null, 0);
					Log.e(Define.LOG_TAG, "加载行程反馈列表错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0, 0, 0, null, 0);
			}
		});
	}

	/**
	 * 获取团单联系人列表
	 *
	 * @param planId
	 * @param callback
	 */
	public void getTeamCustomerList(int planId, final APITeamCustomerResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=getPlanTeamList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planId", String.valueOf(planId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int teamId = dataObject.getInt("teamID");
						List<TeamCustomer> teamCustomers = JsonTools.jsonObjArray(dataObject.getJSONArray("customerList"), TeamCustomer.class);
						callback.result(null, teamId, teamCustomers);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0, null);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.load_team_customer_list_fail);
					callback.result(errMsg, 0, null);
					Log.e(Define.LOG_TAG, "获取团队联系人列表错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0, null);
			}
		});
	}

	/**
	 * 删除联系人
	 *
	 * @param teamId
	 * @param name
	 * @param tel
	 * @param callback
	 */
	public void deleteTeamCustomer(int teamId, String name, String tel, final APIStringResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=delTeamCustomer";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("teamId", String.valueOf(teamId));
		paramsMaps.put("name", String.valueOf(name));
		paramsMaps.put("tel", String.valueOf(tel));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.logout_fail);
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "删除联系人错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.delete_user_fail);
					callback.result(errMsg);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取团信息
	 *
	 * @param planId
	 * @param callback
	 */
	public void loadGroupInfo(int planId, final APIGroupInfoResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=getPlanTeamDetail";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("planId", String.valueOf(planId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						GroupInfo groupInfo = JsonTools.jsonObj(dataObject.toString(), GroupInfo.class);
						callback.result(null, groupInfo);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.load_group_info_fail);
					callback.result(errMsg, null);
					Log.e(Define.LOG_TAG, "获取团信息错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取消息一级列表
	 *
	 * @param type
	 * @param callback
	 */
	public void loadMessageList(String type, final APIMessageResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=categoryNotify";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("type", type);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						JSONArray notifyList = dataObject.getJSONArray("notifyList");
						List<Message> messages = JsonTools.jsonObjArray(notifyList, Message.class);
						callback.result(null, messages);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.load_message_list_fail);
					callback.result(errMsg, null);
					Log.e(Define.LOG_TAG, "获取信息列表错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取消息二级列表
	 *
	 * @param ptype
	 *            一级分类
	 * @param mtype
	 *            二级分类
	 * @param pageNum
	 *            当前页码
	 * @param callback
	 */
	public void loadSubMessageList(String ptype, String mtype, int pageNum, final APISubMessageResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=notifyList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("ptype", ptype);
		paramsMaps.put("mtype", mtype);
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("pageSize", String.valueOf(15)); // 一页默认条数
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						JSONArray notifyList = dataObject.getJSONArray("notifyList");
						List<SubMessage> subMessages = JsonTools.jsonObjArray(notifyList, SubMessage.class);
						callback.result(null, subMessages);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.load_message_list_fail);
					callback.result(errMsg, null);
					Log.e(Define.LOG_TAG, "获取消息列表错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 是否接受指派导游
	 *
	 * @param notifyId
	 * @param isAccept
	 * @param teamId
	 * @param callback
	 */
	public void acceptGuideAppoint(int notifyId, int isAccept, int teamId, final APIStringResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=acceptGuideAppoint";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("notifyId", String.valueOf(notifyId));
		paramsMaps.put("isAccept", String.valueOf(isAccept));
		paramsMaps.put("teamId", String.valueOf(teamId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.operate_fail);
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "接收拒绝指派导游错误错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.operate_fail);
					callback.result(errMsg);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取协议
	 *
	 * @param type
	 * @param callback
	 */
	public void loadAgreement(int type, final APIAgreementResultCallback callback) {
		String subURL = "mod=mobileSystem" + INTERFACE_VERSION + "&act=agreement";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("type", String.valueOf(type));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						String content = dataObject.getString("content");
						callback.result(null, content);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "获取协议错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.load_agreement_fail);
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 获取点名列表
	 *
	 * @param pageNum
	 * @param teamId
	 * @param callback
	 */
	public void loadRollCallList(int pageNum, int teamId, final APIRollCallListResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=getRollCallList";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		int pageSize = 15; // 一页多少条数据
		paramsMaps.put("pageSize", String.valueOf(pageSize));
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("teamId", String.valueOf(teamId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int pageCount = dataObject.getInt("pageCount");
						JSONArray rollCallArray = dataObject.getJSONArray("rollCallList");
						List<RollCall> rollCalls = JsonTools.jsonObjArray(rollCallArray, RollCall.class);
						callback.result(null, rollCalls, pageCount);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null, 0);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "获取点名列表错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.load_rollcall_list_fail);
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 删除点名名单
	 *
	 * @param rollCallID
	 * @param callback
	 */
	public void deleteRollCall(int rollCallID, final APIStringResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=deleteRollCall";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("rollCallID", String.valueOf(rollCallID));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.logout_fail);
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "删除点名名单错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.delete_rollcall_fail);
					callback.result(errMsg);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 修改或添加点名名单
	 *
	 * @param touristListJson
	 * @param rollCallTime
	 * @param teamId
	 * @param rollCallID
	 *            不为零则表示修改，为零则表示新增
	 * @param callback
	 */
	public void editOrAddRollCall(String touristListJson, long rollCallTime, int teamId, int rollCallID, final APIStringResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=editRollCall";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("touristList", touristListJson);
		paramsMaps.put("rollCallTime", String.valueOf(rollCallTime));
		paramsMaps.put("teamId", String.valueOf(teamId));
		if (rollCallID != 0) {
			paramsMaps.put("rollCallID", String.valueOf(rollCallID));
		}
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.rollcall_operation_fail);
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "操作点名名单错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.delete_user_fail);
					callback.result(errMsg);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 根据点名表id获取点名详细
	 *
	 * @param rollCallID
	 * @param callback
	 */
	public void loadRollCallDetail(int rollCallID, final APIRollCallDetailResultCallback callback) {
		String subURL = "mod=mobilePlan" + INTERFACE_VERSION + "&act=getRollCallDetail";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("rollCallID", String.valueOf(rollCallID));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						RollCallDetail rollCallDetail = JsonTools.jsonObj(dataObject.toString(), RollCallDetail.class);
						callback.result(null, rollCallDetail);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.load_rollcall_detail_fail);
					callback.result(errMsg, null);
					Log.e(Define.LOG_TAG, "获取点名名单详情错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 产品推荐
	 *
	 * @param pid
	 * @param recommend
	 *            推荐 (1：是，0：否）
	 * @param callback
	 */
	public void productRecommend(int pid, int recommend, final APIStringResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productRecommend";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pid", String.valueOf(pid));
		paramsMaps.put("recommend", String.valueOf(recommend));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.operate_fail);
					callback.result(errMsg);
					Log.e(Define.LOG_TAG, "产品推荐错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取引用列表数据
	 *
	 * @param cityId
	 *            城市id
	 * @param referenceType
	 *            引用的类型(线路：line, 酒店：hotel, 景点：scenic, 吃喝玩乐：lifestyle)
	 * @param type
	 *            线路引用的type(个人：self, 公共：public)
	 * @param pageNum
	 *            第几页
	 * @param callback
	 */
	public void loadReferenceList(int cityId, final String referenceType, String type, int pageNum, final APIAroundListResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=";
		if ("line".equals(referenceType)) { // 线路
			subURL += "productPlanQuote";
		} else if ("hotel".equals(referenceType)) { // 酒店
			subURL += "hotelNode";
		} else if ("scenic".equals(referenceType)) { // 景点
			subURL += "locationNode";
		} else { // 吃喝玩乐
			subURL += "lifestyleNode";
		}

		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("city", String.valueOf(cityId));
		if (type != null) {
			paramsMaps.put("type", type);
		}
		paramsMaps.put("pageNum", String.valueOf(pageNum));

		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						int pageCount = dataObject.getInt("pageCount");
						JSONArray aroundArray = null;
						if ("line".equals(referenceType)) { // 线路
							aroundArray = dataObject.getJSONArray("planList");
						} else if ("hotel".equals(referenceType)) { // 酒店
							aroundArray = dataObject.getJSONArray("hotelList");
						} else if ("scenic".equals(referenceType)) { // 景点
							aroundArray = dataObject.getJSONArray("locationList");
						} else { // 吃喝玩乐
							aroundArray = dataObject.getJSONArray("lifestyleList");
						}
						List<Around> arounds = JsonTools.jsonObjArray(aroundArray, Around.class);
						callback.result(null, arounds, pageCount);
					} catch (Exception e) {
						errMsg = StringUtil.getStringFromR(R.string.load_reference_list_fail);
						callback.result(errMsg, null, 0);
						Log.e(Define.LOG_TAG, "获取引用资源列表错误\n" + e.toString());
					}
				} else {
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 加载产品资源库
	 *
	 * @param pageNum
	 *            页码
	 * @param sort
	 *            排序 desc、asc
	 * @param city
	 *            城市id
	 * @param productType
	 *            产品类型 全部：0 ，线路：4，酒店：5，景点：6， 娱乐：7，美食：8，购物：9
	 * @param minPrice
	 * @param maxPrice
	 * @param callback
	 */
	public void loadProductResource(int pageNum, String sort, int city, int productType, float minPrice, float maxPrice, final APIProductResourceListCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productResourcesWareroom";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("sort", sort);
		paramsMaps.put("city", String.valueOf(city));
		paramsMaps.put("productType", String.valueOf(productType));
		paramsMaps.put("minPrice", String.valueOf(minPrice));
		paramsMaps.put("maxPrice", String.valueOf(maxPrice));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int pageCount = dataObject.getInt("pageCount");
						JSONArray productResourceArray = dataObject.getJSONArray("productList");
						List<ProductResource> productResources = JsonTools.jsonObjArray(productResourceArray, ProductResource.class);
						callback.result(null, productResources, pageCount);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null, 0);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "获取产品资源列表错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.load_resource_list_fail);
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取分销模板列表
	 * 
	 * @param callback
	 */
	public void loadDistributionTemplateList(final APIDistributionTemplateListResultCallback callback)

	{
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=affiliateCommissionTheme";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						JSONArray distributionArray = dataObject.getJSONArray("theme");
						List<DistributionTemplate> distributionTemplates = JsonTools.jsonObjArray(distributionArray, DistributionTemplate.class);
						callback.result(null, distributionTemplates);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "获取分销模板列表错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR

					(R.string.load_template_list_fail);
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 添加分销模板
	 * 
	 * @param title
	 *            标题
	 * @param type
	 *            佣金类型
	 * @param value
	 *            百分比/金额
	 * @param defaultTemplate
	 *            默认 模板(0：否，1：是)
	 * @param remark
	 *            备注
	 * @param callback
	 */
	public void addDistributionTemplate(String title, int type, String value, int defaultTemplate,

	String remark, final APIDistributionTemplateResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=addAffiliateCommissionTheme";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("title", title);
		paramsMaps.put("type", String.valueOf(type));
		paramsMaps.put("value", value);
		paramsMaps.put("default", String.valueOf(defaultTemplate));
		paramsMaps.put("remark", remark);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						int templateId = dataObject.getInt("themeId");
						if (flag == 1) {
							callback.result(null, templateId);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.add_distribution_faile);
							callback.result(errMsg, 0);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "添加分销模板错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR

					(R.string.add_distribution_faile);
					callback.result(errMsg, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0);
			}
		});
	}

	/**
	 * 更改产品分销设置
	 * 
	 * @param pid
	 *            产品id
	 * @param siteType
	 *            操作类型 （手动：manually、模板：template）
	 * @param isPercentage
	 *            是否按百分比支付佣金 (0：否、1：是)
	 * @param affiliateCommission
	 *            金额或百分比
	 * @param themeId
	 *            模板id
	 * @param callback
	 */
	public void updateDistributionSetting(int pid, String siteType, int isPercentage, String

	affiliateCommission, int themeId, final APIStringResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=saveProductAffiliate";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pid", String.valueOf(pid));
		paramsMaps.put("siteType", siteType);
		paramsMaps.put("isPercentage", String.valueOf(isPercentage));
		paramsMaps.put("affiliateCommission", affiliateCommission);
		if (themeId != 0) {
			paramsMaps.put("themeId", String.valueOf(themeId));
		}
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null);
						} else {
							String errMsg = StringUtil.getStringFromR(R.string.update_distribution_set_faile);
							callback.result(errMsg);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "更新分销设置错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.add_distribution_faile);
					callback.result(errMsg);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 根据类型获取产品星级
	 *
	 * @param type
	 * @param callback
	 */
	public void loadStarLevelDetail(String type, final APIStarLevelResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productGrade";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("type", type);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						starlist gradeList = JsonTools.jsonObj(dataObject.toString(), starlist.class);
						callback.result(null, gradeList);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null);
					}
				} catch (Exception e) {
					callback.result("获取产品星级详情错误", null);
					Log.e(Define.LOG_TAG, "获取产品星级详情错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 提交产品信息
	 *
	 * @param paramsMaps
	 * @param callback
	 */
	public void submitProductInfo(HashMap<String, String> paramsMaps, final APISubmitProductInfoCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=submitProductInfo";
		String requestURL = buildRequestURL(subURL);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						int pid = dataObject.getInt("pid");
						int recommendCount = dataObject.getInt("recommendCount");
						if (flag == 1) {
							callback.result(null, flag, pid, recommendCount);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg, 0, 0, 0);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0, 0, 0);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "提交产品信息失败\n" + e.toString());
					callback.result("提交产品信息失败\n", 0, 0, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0, 0, 0);
			}
		});
	}

	/**
	 * 获取引用信息
	 *
	 * @param pid
	 *            产品id
	 * @param callback
	 */
	public void loadQuoteInfo(int pid, String type, final APIQuoteInfoCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=tidingsInfo";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("id", String.valueOf(pid));
		paramsMaps.put("type", type);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					QuoteInfo quoteInfo = JsonTools.jsonObj(jsonData, QuoteInfo.class);
					callback.result(null, quoteInfo);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 加载组合产品列表
	 *
	 * @param pageNum
	 *            页码
	 * @param sort
	 *            排序 desc、asc
	 * @param city
	 *            城市id
	 * @param productType
	 *            产品类型 全部：0 ，线路：4，酒店：5，景点：6， 娱乐：7，美食：8，购物：9
	 * @param minPrice
	 * @param maxPrice
	 * @param callback
	 */
	public void loadProductSimpleResource(int pageNum, String sort, int city, int productType, float minPrice, float maxPrice, final APIProductResourceListCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=mySimpleProduct";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("pageNum", String.valueOf(pageNum));
		paramsMaps.put("sort", sort);
		paramsMaps.put("city", String.valueOf(city));
		paramsMaps.put("productType", String.valueOf(productType));
		paramsMaps.put("minPrice", String.valueOf(minPrice));
		paramsMaps.put("maxPrice", String.valueOf(maxPrice));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int pageCount = dataObject.getInt("pageCount");
						JSONArray productResourceArray = dataObject.getJSONArray("productList");
						List<ProductResource> productResources = JsonTools.jsonObjArray(productResourceArray, ProductResource.class);
						callback.result(null, productResources, pageCount);
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null, 0);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "获取组合资源列表错误\n" + e.toString());
					String errMsg = StringUtil.getStringFromR(R.string.load_resource_list_fail);
					callback.result(errMsg, null, 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null, 0);
			}
		});
	}

	/**
	 * 获取编辑产品的详细信息
	 * 
	 * @param pid
	 * @param callback
	 */
	public void loadProductDetailInfo(int pid, final APIProductInfoResultCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=productDetailToEditor";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("pid", String.valueOf(pid));
		post(requestURL, params, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				String[] dataArray = getJsonObjDataOrErr(jsonObject);
				String errMsg = dataArray[0];
				if (errMsg == null) {
					String jsonData = dataArray[1];
					try {
						JSONObject dataObject = new JSONObject(jsonData);
						TimeInService = dataObject.getInt("serviceTime");
					} catch (JSONException e) {
					}
					ProductInfo productInfo = JsonTools.jsonObj(jsonData, ProductInfo.class);
					callback.result(null, productInfo);
				} else {
					callback.result(errMsg, null);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 保存产品信息
	 *
	 * @param paramsMaps
	 * @param callback
	 */
	public void saveProductInfo(HashMap<String, String> paramsMaps, final APISaveProductInfoCallback callback) {
		String subURL = "mod=MobileProduct" + INTERFACE_VERSION + "&act=saveProductDetail";
		String requestURL = buildRequestURL(subURL);
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					if (resultCode == 1) {
						JSONObject dataObject = jsonObject.getJSONObject("datas");
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							callback.result(null, flag);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg, 0);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0);
					}
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, "保存产品信息失败\n" + e.toString());
					callback.result("保存产品信息失败\n", 0);
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0);
			}
		});
	}

	/**
	 * 订单支付
	 * 
	 * @param orderId
	 *            订单id
	 * @param invoiceId
	 *            凭证id
	 * @param payType
	 *            支付方式id
	 * @param callback
	 */
	public void orderPay(int orderId, int invoiceId, int payType, final APIOrderPayResultCallback callback) {
		String subURL = "mod=MobilePaypal" + INTERFACE_VERSION + "&act=pay";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("order_id", String.valueOf(orderId));
		paramsMaps.put("invoice_id", String.valueOf(invoiceId));
		paramsMaps.put("pay_type", String.valueOf(payType));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							String payInfo = dataObject.getString("payInfo");
							callback.result(null, payInfo);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg, null);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, null);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.order_pay_fail);
					callback.result(errMsg, null);
					Log.e(Define.LOG_TAG, "订单支付错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, null);
			}
		});
	}

	/**
	 * 尾款支付
	 * 
	 * @param orderId
	 *            订单id
	 * @param callback
	 */
	public void remainPay(int orderId, final APIRemainPayResultCallback callback) {
		String subURL = "mod=MobilePaypal" + INTERFACE_VERSION + "&act=remainPay";
		String requestURL = buildRequestURL(subURL);
		HashMap<String, String> paramsMaps = new HashMap<String, String>();
		paramsMaps.put("order_id", String.valueOf(orderId));
		post(requestURL, paramsMaps, new APIRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
					int resultCode = jsonObject.getInt("recode");
					JSONObject dataObject = jsonObject.getJSONObject("datas");
					if (resultCode == 1) {
						int flag = dataObject.getInt("flag");
						if (flag == 1) {
							int orderId = dataObject.getInt("order_id");
							int invoiceId = dataObject.getInt("invoice_id");
							float price = (float) dataObject.getDouble("price");
							callback.result(null, orderId, invoiceId, price);
						} else {
							String errMsg = jsonObject.getString("msg");
							callback.result(errMsg, 0, 0, 0);
						}
					} else {
						String errMsg = jsonObject.getString("msg");
						callback.result(errMsg, 0, 0, 0);
					}
				} catch (Exception e) {
					String errMsg = StringUtil.getStringFromR(R.string.order_pay_fail);
					callback.result(errMsg, 0, 0, 0);
					Log.e(Define.LOG_TAG, "订单尾款支付错误\n" + e.toString());
				}
			}

			@Override
			public void onFailure(String errMsg) {
				callback.result(errMsg, 0, 0, 0);
			}
		});
	}

}
