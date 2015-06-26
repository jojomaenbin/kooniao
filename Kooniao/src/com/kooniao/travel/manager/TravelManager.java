package com.kooniao.travel.manager;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APIAmusementDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIAroundListResultCallback;
import com.kooniao.travel.api.ApiCaller.APICommentListResultCallback;
import com.kooniao.travel.api.ApiCaller.APICurrentResultCallback;
import com.kooniao.travel.api.ApiCaller.APICurrentTravelResultCallback;
import com.kooniao.travel.api.ApiCaller.APICustomAmusementAndShoppingDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APICustomEventDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APICustomFoodDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APICustomHotelDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APICustomScenicDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIFoodDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIGetOfflineResultCallback;
import com.kooniao.travel.api.ApiCaller.APIGroupInfoResultCallback;
import com.kooniao.travel.api.ApiCaller.APIGuideTravelListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIHotelDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIMyTravelResultCallback;
import com.kooniao.travel.api.ApiCaller.APIOfflineUpdateInfoListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIScenicDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIShoppingDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStringListCallback;
import com.kooniao.travel.api.ApiCaller.APIStringResultCallback;
import com.kooniao.travel.api.ApiCaller.APITeamCustomerResultCallback;
import com.kooniao.travel.api.ApiCaller.APITravelDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APITravelFeedBackResultCallback;
import com.kooniao.travel.api.ApiCaller.APITravelListResultCallback;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.AmusementDetail;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.model.Comment;
import com.kooniao.travel.model.CurrentTravel;
import com.kooniao.travel.model.CustomAmusementAndShopping;
import com.kooniao.travel.model.CustomEvent;
import com.kooniao.travel.model.CustomFoodDetail;
import com.kooniao.travel.model.CustomHotelDetail;
import com.kooniao.travel.model.CustomScenicDetail;
import com.kooniao.travel.model.FoodDetail;
import com.kooniao.travel.model.GroupInfo;
import com.kooniao.travel.model.Guide;
import com.kooniao.travel.model.GuideTravel;
import com.kooniao.travel.model.HotelDetail;
import com.kooniao.travel.model.MessageTemplate;
import com.kooniao.travel.model.MyTravel;
import com.kooniao.travel.model.OfflineUpdateInfo;
import com.kooniao.travel.model.ScenicDetail;
import com.kooniao.travel.model.ShoppingDetail;
import com.kooniao.travel.model.TeamCustomer;
import com.kooniao.travel.model.Travel;
import com.kooniao.travel.model.TravelDetail;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.DateUtil;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.assit.QueryBuilder;

/**
 * 行程管理
 * 
 * @author ke.wei.quan
 * 
 */
public class TravelManager {

	TravelManager() {
	}

	private static TravelManager instance;

	public static TravelManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new TravelManager();
				}
			}
		}

		return instance;
	}

	/**************************************************************************************************************************
	 * 
	 * 回调接口
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	public interface TravelListCallback {
		void result(String errMsg, List<Travel> travels, int pageCount);
	}

	public interface TravelDetailResultCallback {
		void result(String errMsg, TravelDetail travelDetail, int commentCount);
	}

	public interface StringListCallback {
		void result(String errMsg, List<String> imgList);
	}

	public interface StringResultCallback {
		void result(String errMsg);
	}

	public interface CurrentTravelResultCallback {
		void result(String errMsg, CurrentTravel currentTravel);
	}

	public interface CommentListResultCallback {
		void result(String errMsg, List<Comment> comments, int commentCount);
	}

	public interface AroundListResultCallback {
		void result(String errMsg, List<Around> arounds, int pageCount);
	}

	public interface ScenicDetailResultCallback {
		void result(String errMsg, ScenicDetail scenicDetail);
	}

	public interface CustomScenicDetailResultCallback {
		void result(String errMsg, CustomScenicDetail customScenicDetail);
	}

	public interface HotelDetailResultCallback {
		void result(String errMsg, HotelDetail hotelDetail);
	}

	public interface CustomHotelDetailResultCallback {
		void result(String errMsg, CustomHotelDetail customHotelDetail);
	}

	public interface FoodDetailResultCallback {
		void result(String errMsg, FoodDetail foodDetail);
	}

	public interface CustomFoodDetailResultCallback {
		void result(String errMsg, CustomFoodDetail customFoodDetail);
	}

	public interface ShoppingDetailResultCallback {
		void result(String errMsg, ShoppingDetail shoppingDetail);
	}

	public interface AmusementDetailResultCallback {
		void result(String errMsg, AmusementDetail amusementDetail);
	}

	public interface CustomAmusementAndShoppingDetailResultCallback {
		void result(String errMsg, CustomAmusementAndShopping customAmusementAndShopping);
	}

	public interface CustomEventDetailResultCallback {
		void result(String errMsg, CustomEvent customEvent);
	}

	public interface GuideTravelListResultCallback {
		void result(String errMsg, Guide guideInfo, List<GuideTravel> travels, int totalCount);
	}

	public interface GetOfflineResultCallback {
		void result(String errMsg, String zipPath, String zipSize, long mTime);
	}

	public interface MyTravelResultCallback {
		void result(String errMsg, List<MyTravel> allTravels, List<MyTravel> teamTravels, int pageCount);
	}

	public interface TravelFeedBackResultCallback {
		void result(String errMsg, float lineScore, float serviceScore, float repastScore, List<Comment> comments, int totalCount);
	}

	public interface TeamCustomerResultCallback {
		void result(String errMsg, int teamId, List<TeamCustomer> teamCustomers);
	}

	public interface GroupInfoResultCallback {
		void result(String errMsg, GroupInfo groupInfo);
	}

	public interface OfflineUpdateInfoListResultCallback {
		void result(String errMsg, List<OfflineUpdateInfo> offlineUpdateInfos);
	}

	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	/**
	 * 获取行程列表
	 * 
	 * @param uid
	 * @param cityId
	 * @param pageNum
	 * @param callback
	 */
	public void loadTravelList(int cityId, int pageNum, final TravelListCallback callback) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		ApiCaller.getInstance().loadTravelList(uid, cityId, pageNum, new APITravelListResultCallback() {

			@Override
			public void result(String errMsg, List<Travel> travels, int pageCount) {
				callback.result(errMsg, travels, pageCount);
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
	public void loadTravelDetail(boolean isOffline, final int planId, final TravelDetailResultCallback callback) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		ApiCaller.getInstance().loadTravelDetail(isOffline, uid, planId, -1, new APITravelDetailResultCallback() {

			@Override
			public void result(String errMsg, TravelDetail travelDetail, int commentCount) {
				callback.result(errMsg, travelDetail, commentCount);
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
	public void loadTravelDetail(boolean isOffline, final int planId, int groupID, final TravelDetailResultCallback callback) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		ApiCaller.getInstance().loadTravelDetail(isOffline, uid, planId, groupID, new APITravelDetailResultCallback() {

			@Override
			public void result(String errMsg, TravelDetail travelDetail, int commentCount) {
				callback.result(errMsg, travelDetail, commentCount);
			}
		});
	}

	/**
	 * 获取行程大图列表
	 * 
	 * @param planId
	 * @param isOffline
	 * @param callback
	 */
	public void loadTravelLargeImageList(boolean isOffline, int planId, final StringListCallback callback) {
		ApiCaller.getInstance().loadTravelLargeImageList(isOffline, planId, new APIStringListCallback() {

			@Override
			public void result(String errMsg, List<String> imgList) {
				callback.result(errMsg, imgList);
			}
		});
	}

	/**
	 * 添加行程
	 * 
	 * @param planId
	 * @param callback
	 */
	public void addToMyTravelList(int planId, final StringResultCallback callback) {
		ApiCaller.getInstance().addToMyTravelList(planId, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
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
	public void deleteMyTravel(int planId, final StringResultCallback callback) {
		ApiCaller.getInstance().deleteMyTravel(planId, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 设置为当前行程
	 * 
	 * @param planId
	 * @param callback
	 */
	public void setCurrentTravel(int planId, final APICurrentResultCallback callback) {
		ApiCaller.getInstance().setCurrentTravel(planId, new APICurrentResultCallback() {

			@Override
			public void result(String errMsg, int oldMtime, int newMtime) {
				callback.result(errMsg, oldMtime, newMtime);
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
	public void updateMyTravelDate(long startDate, int planId, final StringResultCallback callback) {
		ApiCaller.getInstance().updateMyTravelDate(startDate, planId, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取行程评论列表
	 * 
	 * @param id
	 *            行程id
	 * @param module
	 *            评论类别；location:景点 hotel:酒店 plan:计划 record:record表
	 *            lifestyle:吃喝玩乐 team:团单
	 * @param pageNum
	 * @param pageSize
	 * @param callback
	 */
	public void loadCommentList(int id, String module, int pageNum, int pageSize, final CommentListResultCallback callback) {
		ApiCaller.getInstance().loadCommentList(id, module, pageNum, pageSize, new APICommentListResultCallback() {

			@Override
			public void result(String errMsg, List<Comment> comments, int commentCount) {
				callback.result(errMsg, comments, commentCount);
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
	public void commitComment(int id, String content, float rank, String module, final StringResultCallback callback) {
		ApiCaller.getInstance().commitComment(id, content, rank, 0, module, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 回复评论
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
	public void replyComment(int id, String content, float rank, int pid, String module, final StringResultCallback callback) {
		ApiCaller.getInstance().commitComment(id, content, rank, pid, module, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
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
	public void loadAroundList(double lon, double lat, int cityId, String type, int pageNum, final AroundListResultCallback callback) {
		ApiCaller.getInstance().loadAroundList(lon, lat, cityId, type, pageNum, new APIAroundListResultCallback() {

			@Override
			public void result(String errMsg, List<Around> arounds, int pageCount) {
				callback.result(errMsg, arounds, pageCount);
			}
		});
	}

	/**
	 * 获取附近景点详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadScenicDetail(boolean isOffline, int travelId, int id, final ScenicDetailResultCallback callback) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		ApiCaller.getInstance().loadScenicDetail(isOffline, uid, travelId, id, new APIScenicDetailResultCallback() {

			@Override
			public void result(String errMsg, ScenicDetail scenicDetail) {
				callback.result(errMsg, scenicDetail);
			}
		});
	}

	/**
	 * 获取自定义景点详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadCustomScenicDetail(boolean isOffline, int travelId, int id, final CustomScenicDetailResultCallback callback) {
		ApiCaller.getInstance().loadCustomScenicDetail(isOffline, travelId, id, new APICustomScenicDetailResultCallback() {

			@Override
			public void result(String errMsg, CustomScenicDetail customScenicDetail) {
				callback.result(errMsg, customScenicDetail);
			}
		});
	}

	/**
	 * 获取附近酒店详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadHotelDetail(boolean isOffline, int travelId, int id, final HotelDetailResultCallback callback) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		ApiCaller.getInstance().loadHotelDetail(isOffline, uid, travelId, id, new APIHotelDetailResultCallback() {

			@Override
			public void result(String errMsg, HotelDetail hotelDetail) {
				callback.result(errMsg, hotelDetail);
			}
		});
	}

	/**
	 * 获取自定义酒店详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadCustomHotelDetail(boolean isOffline, int travelId, int id, final CustomHotelDetailResultCallback callback) {
		ApiCaller.getInstance().loadCustomHotelDetail(isOffline, travelId, id, new APICustomHotelDetailResultCallback() {

			@Override
			public void result(String errMsg, CustomHotelDetail customHotelDetail) {
				callback.result(errMsg, customHotelDetail);
			}
		});
	}

	/**
	 * 获取附近美食详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadFoodDetail(boolean isOffline, int travelId, int id, final FoodDetailResultCallback callback) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		ApiCaller.getInstance().loadFoodDetail(isOffline, uid, travelId, id, new APIFoodDetailResultCallback() {

			@Override
			public void result(String errMsg, FoodDetail foodDetail) {
				callback.result(errMsg, foodDetail);
			}
		});
	}

	/**
	 * 获取自定义美食详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadCustomFoodDetail(boolean isOffline, int travelId, int id, final CustomFoodDetailResultCallback callback) {
		ApiCaller.getInstance().loadCustomFoodDetail(isOffline, travelId, id, new APICustomFoodDetailResultCallback() {

			@Override
			public void result(String errMsg, CustomFoodDetail customFoodDetail) {
				callback.result(errMsg, customFoodDetail);
			}
		});
	}

	/**
	 * 获取附近购物详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadShoppingDetail(boolean isOffline, int travelId, int id, final ShoppingDetailResultCallback callback) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		ApiCaller.getInstance().loadShoppingDetail(isOffline, uid, travelId, id, new APIShoppingDetailResultCallback() {

			@Override
			public void result(String errMsg, ShoppingDetail shoppingDetail) {
				callback.result(errMsg, shoppingDetail);
			}
		});
	}

	/**
	 * 获取自定义购物详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadCustomShoppingDetail(boolean isOffline, int travelId, int id, final CustomAmusementAndShoppingDetailResultCallback callback) {
		ApiCaller.getInstance().loadCustomShoppingDetail(isOffline, travelId, id, new APICustomAmusementAndShoppingDetailResultCallback() {

			@Override
			public void result(String errMsg, CustomAmusementAndShopping customAmusementAndShopping) {
				callback.result(errMsg, customAmusementAndShopping);
			}
		});
	}

	/**
	 * 获取附近娱乐详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadAmusementDetail(boolean isOffline, int travelId, int id, final AmusementDetailResultCallback callback) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		ApiCaller.getInstance().loadAmusementDetail(isOffline, uid, travelId, id, new APIAmusementDetailResultCallback() {

			@Override
			public void result(String errMsg, AmusementDetail amusementDetail) {
				callback.result(errMsg, amusementDetail);
			}
		});
	}

	/**
	 * 获取自定义娱乐详情页
	 * 
	 * @param isOffline
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadCustomAmusementDetail(boolean isOffline, int travelId, int id, final CustomAmusementAndShoppingDetailResultCallback callback) {
		ApiCaller.getInstance().loadCustomAmusementDetail(isOffline, travelId, id, new APICustomAmusementAndShoppingDetailResultCallback() {

			@Override
			public void result(String errMsg, CustomAmusementAndShopping customAmusementAndShopping) {
				callback.result(errMsg, customAmusementAndShopping);
			}
		});
	}

	/**
	 * 获取其他自定义详情页
	 * 
	 * @param isOffline
	 * @param type
	 * @param travelId
	 * @param id
	 * @param callback
	 */
	public void loadOtherCustomDetail(boolean isOffline, String type, int travelId, int id, final CustomEventDetailResultCallback callback) {
		ApiCaller.getInstance().loadOtherCustomDetail(isOffline, type, travelId, id, new APICustomEventDetailResultCallback() {

			@Override
			public void result(String errMsg, CustomEvent customEvent) {
				callback.result(errMsg, customEvent);
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
	public void loadAroundLargeImageList(boolean isOffline, String type, int planId, int id, final StringListCallback callback) {
		ApiCaller.getInstance().loadAroundLargeImageList(isOffline, type, planId, id, new APIStringListCallback() {

			@Override
			public void result(String errMsg, List<String> imgList) {
				callback.result(errMsg, imgList);
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
	public void loadGuideTravelList(int guideId, int pageNum, final GuideTravelListResultCallback callback) {
		ApiCaller.getInstance().loadGuideTravelList(guideId, pageNum, new APIGuideTravelListResultCallback() {

			@Override
			public void result(String errMsg, Guide guideInfo, List<GuideTravel> travels, int totalCount) {
				callback.result(errMsg, guideInfo, travels, totalCount);
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
	public void getOfflineZip(int planId, int again, final GetOfflineResultCallback callback) {
		ApiCaller.getInstance().getOfflineZip(planId, again, new APIGetOfflineResultCallback() {

			@Override
			public void result(String errMsg, String zipPath, String zipSize, long mTime) {
				callback.result(errMsg, zipPath, zipSize, mTime);
			}
		});
	}

	/**
	 * 检查离线更新
	 * 
	 * @param offlineIdJsonArray
	 * @param callback
	 */
	public void checkOfflineUpdate(String offlineIdJsonArray, final OfflineUpdateInfoListResultCallback callback) {
		ApiCaller.getInstance().checkOfflineUpdate(offlineIdJsonArray, new APIOfflineUpdateInfoListResultCallback() {

			@Override
			public void result(String errMsg, List<OfflineUpdateInfo> offlineUpdateInfos) {
				callback.result(errMsg, offlineUpdateInfos);
			}
		});
	}

	/**
	 * 获取我的行程列表
	 * 
	 * @param pageNum
	 * @param callback
	 */
	public void loadMyTravelList(int pageNum, final MyTravelResultCallback callback) {
		ApiCaller.getInstance().loadMyTravelList(pageNum, new APIMyTravelResultCallback() {

			@Override
			public void result(String errMsg, List<MyTravel> allTravels, List<MyTravel> teamTravels, int pageCount) {
				if (allTravels != null && !allTravels.isEmpty()) {
					for (MyTravel myTravel : allTravels) {
						if (myTravel.getCurrent() == 1) {
							allTravels.remove(myTravel);
							allTravels.add(0, myTravel);
							break;
						}
					}
				}
				callback.result(errMsg, allTravels, teamTravels, pageCount);
			}
		});
	}

	/**
	 * 获取三天内行程列表
	 * 
	 * @param callback
	 */
	public void loadThreeDaysTravelList(final MyTravelResultCallback callback) {
		ApiCaller.getInstance().loadThreeDaysTravelList(new APIMyTravelResultCallback() {

			@Override
			public void result(String errMsg, List<MyTravel> allTravels, List<MyTravel> teamTravels, int pageCount) {
				callback.result(errMsg, allTravels, teamTravels, pageCount);
			}
		});
	}

	/**
	 * 取消当前行程
	 * 
	 * @param planId
	 * @param callback
	 */
	public void cancelCurrentTravel(int planId, final StringResultCallback callback) {
		ApiCaller.getInstance().cancelCurrentTravel(planId, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg == null) {
					AppSetting.getInstance().saveLongPreferencesByKey(Define.CURRENT_TRAVEL_LAST_DATE, 0);
				}
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取当前行程
	 * 
	 * @param callback
	 */
	public void getCurrentTravel(final CurrentTravelResultCallback callback) {
		long lastDateStamp = AppSetting.getInstance().getLongPreferencesByKey(Define.CURRENT_TRAVEL_LAST_DATE);
		long currentStamp = System.currentTimeMillis() / 1000;
		if (lastDateStamp < currentStamp) {
			callback.result("", null);
		}
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (uid != 0) {
			ApiCaller.getInstance().getCurrentTravel(new APICurrentTravelResultCallback() {

				@Override
				public void result(String errMsg, CurrentTravel currentTravel) {
					if (currentTravel != null) {
						long unixtime = currentTravel.getTime();
						String firstDate = DateUtil.timeDistanceString(unixtime, Define.FORMAT_YMDHM);
						long firstDateStamp = DateUtil.strToTimestamp(firstDate, Define.FORMAT_YMD);
						firstDate = DateUtil.timeDistanceString(firstDateStamp, Define.FORMAT_YMD) + " 03:00";
						firstDateStamp = DateUtil.strToTimestamp(firstDate, Define.FORMAT_YMDHM);
						int daySize = currentTravel.getDayList().size();
						long lastDateStamp = firstDateStamp + 24 * 60 * 60 * daySize;
						AppSetting.getInstance().saveLongPreferencesByKey(Define.CURRENT_TRAVEL_LAST_DATE, lastDateStamp);
					}
					callback.result(errMsg, currentTravel);
				}
			});
		} else {
			callback.result(null, null);
		}
	}

	/**
	 * 设置行程以后出发
	 * 
	 * @param planId
	 * @param callback
	 */
	public void setAfterCurrentPlan(int planId, final StringResultCallback callback) {
		ApiCaller.getInstance().setAfterCurrentPlan(planId, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 用户行程反馈
	 * 
	 * @param travelId
	 * @param lineScore
	 * @param serviceScore
	 * @param repastScore
	 * @param content
	 * @param callback
	 */
	public void commitTravelFeedback(int travelId, float lineScore, float serviceScore, float repastScore, String content, final StringResultCallback callback) {
		ApiCaller.getInstance().commitTravelFeedback(travelId, lineScore, serviceScore, repastScore, content, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
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
	public void loadTravelFeedback(int travelId, int pageNum, final TravelFeedBackResultCallback callback) {
		ApiCaller.getInstance().loadTravelFeedback(travelId, pageNum, new APITravelFeedBackResultCallback() {

			@Override
			public void result(String errMsg, float lineScore, float serviceScore, float repastScore, List<Comment> comments, int totalCount) {
				callback.result(errMsg, lineScore, serviceScore, repastScore, comments, totalCount);
			}
		});
	}

	/**
	 * 获取团单联系人列表
	 * 
	 * @param planId
	 * @param callback
	 */
	public void loadTeamCustomerList(final int planId, final APITeamCustomerResultCallback callback) {
		ApiCaller.getInstance().getTeamCustomerList(planId, new APITeamCustomerResultCallback() {

			@Override
			public void result(String errMsg, int teamId, List<TeamCustomer> teamCustomers) {
				if (teamCustomers != null) {
					for (TeamCustomer teamCustomer : teamCustomers) {
						teamCustomer.setTravelId(planId);
						saveTeamCustomer(teamCustomer);
					}
				}
				callback.result(errMsg, teamId, teamCustomers);
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
	public void deleteTeamCustomer(int teamId, final String name, final String tel, final StringResultCallback callback) {
		ApiCaller.getInstance().deleteTeamCustomer(teamId, name, tel, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				deleteTeamCustomerByNameAndTel(name, tel);
				callback.result(errMsg);
			}
		});
	}

	public List<MessageTemplate> getMessageTemplates() {
		boolean isNotFirstTime = AppSetting.getInstance().getBooleanPreferencesByKey(Define.IS_NOT_FIRST_TIME_GET_MESSAGE_TEMPLATE);
		List<MessageTemplate> messageTemplates = getLocalMessageTemplates();
		messageTemplates = messageTemplates == null ? new ArrayList<MessageTemplate>() : messageTemplates;
		if (!isNotFirstTime && messageTemplates.isEmpty()) {
			AppSetting.getInstance().saveBooleanPreferencesByKey(Define.IS_NOT_FIRST_TIME_GET_MESSAGE_TEMPLATE, true);
			MessageTemplate messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("尊敬的游客，您参与10月10日的大理丽江4天3夜行程，请于早上7点在机场二楼集合，导游：刘欣芳，13800138000。");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("温馨提示：当地气温0℃~-12℃，建议携带羽绒服等防寒装备，请自备常用药品，携带身份证等重要证件。");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("出行工具：飞机，时间：10月10日8：20-12:20，航班号：BT8738。");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("出行工具：火车，时间：10月12日12：10-15:50，车次:A3232。");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("出行工具：汽车，时间：10月12日6：10，车牌：粤A 2393，司机：李师傅，13800138000。");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("XXX景区游玩，结束请于14:00景区门口集合，请注意游玩时间。");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("温馨提示：游玩时间快到了，请留意集合时间。");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("感谢亲们这些天来对我工作的支持与配合，祝亲们生活愉快，合家安康！");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("下次出游，记得联系13800138000优惠多多！");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("结束愉快旅程！");
			messageTemplates.add(messageTemplate);
			// //////////////////////////////////////////////
			messageTemplate = new MessageTemplate();
			messageTemplate.setMessage("紧急联系人：李小春：13800138000。中国应急电话:110——匪警电话、119——火警电话、120——急救电话、999——红十字会的急救电话和122——交通报警电话");
			messageTemplates.add(messageTemplate);
			// 保存至数据库
			saveMessageTemplates(messageTemplates);
		}

		return messageTemplates;
	}

	/**
	 * 获取团信息
	 * 
	 * @param planId
	 * @param callback
	 */
	public void loadGroupInfo(int planId, final GroupInfoResultCallback callback) {
		ApiCaller.getInstance().loadGroupInfo(planId, new APIGroupInfoResultCallback() {

			@Override
			public void result(String errMsg, GroupInfo groupInfo) {
				callback.result(errMsg, groupInfo);
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
	public void commitOrderReview(int orderId, int mark1, int mark2, int mark3, String content, final StringResultCallback callback) {
		ApiCaller.getInstance().commitOrderReview(orderId, mark1, mark2, mark3, content, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
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
	public void loadReferenceList(int cityId, final String referenceType, String type, int pageNum, final AroundListResultCallback callback) {
		ApiCaller.getInstance().loadReferenceList(cityId, referenceType, type, pageNum, new APIAroundListResultCallback() {
			
			@Override
			public void result(String errMsg, List<Around> arounds, int pageCount) {
				callback.result(errMsg, arounds, pageCount); 
			}
		});
	}

	/**************************************************************************************************************************
	 * 
	 * 数据库操作
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	/**
	 * 保存点名人员名单到本地
	 * 
	 */
	public void saveTeamCustomer(TeamCustomer teamCustomer) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		db.save(teamCustomer);
	}

	/**
	 * 根据teamId获取人员
	 * 
	 * @param teamId
	 * @return
	 */
	public List<TeamCustomer> getTeamCustomerListByTravelId(int teamId) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		List<TeamCustomer> teamCustomers = db.query(new QueryBuilder(TeamCustomer.class).where("travelId" + " = ?", new String[] { String.valueOf(teamId) }));
		return teamCustomers;
	}

	/**
	 * 删除点名人员名单
	 * 
	 */
	public void deleteTeamCustomerByNameAndTel(String name, String tel) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		List<TeamCustomer> teamCustomers = db.query(new QueryBuilder(TeamCustomer.class).where("name" + " = ?", new String[] { name })//
				.where("tel" + " = ?", new String[] { tel }));
		db.delete(teamCustomers);
	}

	/**
	 * 保存模板到本地
	 * 
	 * @param messageTemplates
	 */
	public void saveMessageTemplates(List<MessageTemplate> messageTemplates) {
		for (MessageTemplate messageTemplate : messageTemplates) {
			saveMessageTemplate(messageTemplate);
		}
	}

	/**
	 * 保存模板到本地
	 * 
	 * @param messageTemplate
	 */
	public void saveMessageTemplate(MessageTemplate messageTemplate) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		db.save(messageTemplate);
	}

	/**
	 * 获取短信模板
	 * 
	 * @return
	 */
	private List<MessageTemplate> getLocalMessageTemplates() {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		List<MessageTemplate> messageTemplates = db.query(new QueryBuilder(MessageTemplate.class).where("uid" + " = ?", new String[] { String.valueOf(uid) }));
		return messageTemplates;
	}
}
