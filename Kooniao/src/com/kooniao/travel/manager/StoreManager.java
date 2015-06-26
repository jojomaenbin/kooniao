package com.kooniao.travel.manager;

import java.util.List;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APIDistributionTemplateResultCallback;
import com.kooniao.travel.api.ApiCaller.APICommissionDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APICommissionListResultCallback;
import com.kooniao.travel.api.ApiCaller.APICustomerInfoListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIDistributionTemplateListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIOpenStoreResultCallback;
import com.kooniao.travel.api.ApiCaller.APIProductDistributionSalesStatisticsResultCallback;
import com.kooniao.travel.api.ApiCaller.APIProductSalesStatisticsResultCallback;
import com.kooniao.travel.api.ApiCaller.APIReserveProductResultCallback;
import com.kooniao.travel.api.ApiCaller.APISelfLockingSalesStatisticsResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStoreAndProductListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStoreDistributionSalesStatisticsResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStoreProductListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStoreResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStoreSalesStatisticsResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStoreTotalSalesStatisticsResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStringResultCallback;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.Commission;
import com.kooniao.travel.model.CommissionDetail;
import com.kooniao.travel.model.CustomerInfo;
import com.kooniao.travel.model.DistributionTemplate;
import com.kooniao.travel.model.Payment;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.model.ProductDistributionSalesStatistics;
import com.kooniao.travel.model.ProductSalesStatistics;
import com.kooniao.travel.model.SelfLockingSalesStatistics;
import com.kooniao.travel.model.Store;
import com.kooniao.travel.model.StoreDistributionSalesStatistics;
import com.kooniao.travel.model.StoreProduct;
import com.kooniao.travel.model.StoreSalesStatistics;
import com.kooniao.travel.model.StoreTotalSalesStatistics;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.utils.AppSetting;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

/**
 * 店铺管理
 * 
 * @author ke.wei.quan
 * 
 */
public class StoreManager {

	StoreManager() {
	}

	private static StoreManager instance;

	public static StoreManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new StoreManager();
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

	public interface StoreResultCallback {
		void result(String errMsg, Store store);
	}

	public interface StringResultCallback {
		void result(String errMsg);
	}

	public interface OpenStoreResultCallback {
		void result(String errMsg, int storeId);
	}

	public interface StoreAndProductListCallback {
		void result(String errMsg, Store store, List<Product> products, int pageCount);
	}

	public interface ReserveLineProductResultCallback {
		void result(String errMsg, int orderId, int orderNum, int invoiceId, int isOffline, String shopLogo, String productLogo, List<Payment> payments);
	}

	public interface StoreSalesStatisticsResultCallback {
		void result(String errMsg, StoreSalesStatistics storeSalesStatistics, int pageCount);
	}

	public interface ProductSalesStatisticsResultCallback {
		void result(String errMsg, ProductSalesStatistics productSalesStatistics, int pageCount);
	}

	public interface SelfLockingSalesStatisticsResultCallback {
		void result(String errMsg, SelfLockingSalesStatistics selfLockingSalesStatistics, int pageCount);
	}

	public interface StoreTotalSalesStatisticsResultCallback {
		void result(String errMsg, StoreTotalSalesStatistics storeTotalSalesStatistics, int pageCount);
	}

	public interface StoreDistributionSalesStatisticsResultCallback {
		void result(String errMsg, StoreDistributionSalesStatistics storeDistributionSalesStatistics, int pageCount);
	}

	public interface ProductDistributionSalesStatisticsResultCallback {
		void result(String errMsg, ProductDistributionSalesStatistics productDistributionSalesStatistics, int pageCount);
	}

	public interface StoreProductListResultCallback {
		void result(String errMsg, List<StoreProduct> storeProducts, int pageCount);
	}

	public interface CommissionListResultCallback {
		void result(String errMsg, List<Commission> commissions, int pageCount);
	}

	public interface CommissionDetailResultCallback {
		void result(String errMsg, CommissionDetail commissionDetail, int pageCount);
	}

	public interface CustomerInfoListResultCallback {
		void result(String errMsg, List<CustomerInfo> customerInfos);
	}

	public interface DistributionTemplateListResultCallback {
		void result(String errMsg, List<DistributionTemplate> distributionTemplates);
	}

	public interface DistributionTemplateResultCallback {
		void result(String errMsg, int templateId);
	}

	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	private boolean isFirstTimeLoadStoreInfo = true;

	/**
	 * 获取店铺信息
	 * 
	 * @param sid
	 *            店铺id
	 * @param type
	 *            （a/c）
	 * @param callback
	 */
	public void loadStoreInfo(int sid, String type, final StoreResultCallback callback) {
		if (isFirstTimeLoadStoreInfo) {
			isFirstTimeLoadStoreInfo = false;
			Store localStore = getCurrentStore();
			if (localStore != null) {
				callback.result(null, localStore);
			}
		}

		ApiCaller.getInstance().loadStoreInfo(sid, type, new APIStoreResultCallback() {

			@Override
			public void result(String errMsg, Store store) {
				if (store != null) {
					int sid = AppSetting.getInstance().getIntPreferencesByKey(Define.SID);
					store.setId(sid);
					saveOrUpdateStore(store);
				}
				callback.result(errMsg, store);
			}
		});
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
	public void uploadStoreImage(int sid, String shopType, String imgType, String filePath, final StringResultCallback callback) {
		ApiCaller.getInstance().uploadStoreImage(sid, shopType, imgType, filePath, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 更改店铺名称
	 * 
	 * @param sid
	 * @param type
	 * @param name
	 * @param callback
	 */
	public void updateStoreName(int sid, String type, String name, final StringResultCallback callback) {
		ApiCaller.getInstance().updateStoreInfo(sid, type, name, null, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 更改店铺联系电话
	 * 
	 * @param sid
	 * @param type
	 * @param mobile
	 * @param callback
	 */
	public void updateStoreContactPhone(int sid, String type, String mobile, final StringResultCallback callback) {
		ApiCaller.getInstance().updateStoreInfo(sid, type, null, mobile, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
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
		ApiCaller.getInstance().updateOrderStatus(id, status, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取店铺
	 * 
	 * @param pageNum
	 * @param isMyStore
	 * @param shopId
	 * @param shopType
	 * @param productType
	 *            "类型： 全部：0 ，线路：4， 组合：2， 酒店：5， 美食：8， 娱乐：7"
	 * @param callback
	 */
	public void loadStore(int pageNum, boolean isMyStore, int shopId, String shopType, int productType, final StoreAndProductListCallback callback) {
		ApiCaller.getInstance().loadStore(pageNum, isMyStore, shopId, shopType, productType, new APIStoreAndProductListResultCallback() {

			@Override
			public void result(String errMsg, Store store, List<Product> products, int pageCount) {
				callback.result(errMsg, store, products, pageCount);
			}
		});
	}

	/**
	 * 提交线路产品预订订单
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
	public void reserveProduct(String message, int priceId, String title, int isPayDeposit, float totalPrice, int adult, int child, long startTime, String conractName, String conractMobile, String conractEmail, String joiner, String shopType, int shopId, int productId, String salePrice, final ReserveLineProductResultCallback callback) {
		ApiCaller.getInstance().reserveProduct(message, priceId, title, isPayDeposit, totalPrice, adult, child, startTime, conractName, conractMobile, conractEmail, joiner, shopType, shopId, productId, salePrice, new APIReserveProductResultCallback() {

			@Override
			public void result(String errMsg, int orderId, int orderNum, int invoiceId, int isOffline, String shopLogo, String productLogo, List<Payment> payments) {
				callback.result(errMsg, orderId, orderNum, invoiceId, isOffline, shopLogo, productLogo, payments);
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
	public void loadStoreSalesStatistics(int pageNum, int day, final StoreSalesStatisticsResultCallback callback) {
		ApiCaller.getInstance().loadStoreSalesStatistics(pageNum, day, new APIStoreSalesStatisticsResultCallback() {

			@Override
			public void result(String errMsg, StoreSalesStatistics storeSalesStatistics, int pageCount) {
				callback.result(errMsg, storeSalesStatistics, pageCount);
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
	public void loadProductSalesStatistics(int pageNum, int productType, int day, final ProductSalesStatisticsResultCallback callback) {
		ApiCaller.getInstance().loadProductSalesStatistics(pageNum, productType, day, new APIProductSalesStatisticsResultCallback() {

			@Override
			public void result(String errMsg, ProductSalesStatistics productSalesStatistics, int pageCount) {
				callback.result(errMsg, productSalesStatistics, pageCount);
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
	public void loadSelfLockingSalesStatistics(int pageNum, int productType, int day, final SelfLockingSalesStatisticsResultCallback callback) {
		ApiCaller.getInstance().loadSelfLockingSalesStatistics(pageNum, productType, day, new APISelfLockingSalesStatisticsResultCallback() {

			@Override
			public void result(String errMsg, SelfLockingSalesStatistics selfLockingSalesStatistics, int pageCount) {
				callback.result(errMsg, selfLockingSalesStatistics, pageCount);
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
	public void loadStoreTotalSalesStatistics(int pageNum, int productType, int day, final StoreTotalSalesStatisticsResultCallback callback) {
		ApiCaller.getInstance().loadStoreTotalSalesStatistics(pageNum, productType, day, new APIStoreTotalSalesStatisticsResultCallback() {

			@Override
			public void result(String errMsg, StoreTotalSalesStatistics storeTotalSalesStatistics, int pageCount) {
				callback.result(errMsg, storeTotalSalesStatistics, pageCount);
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
	public void loadStoreDistributionSalesStatistics(int pageNum, int productType, int day, final StoreDistributionSalesStatisticsResultCallback callback) {
		ApiCaller.getInstance().loadStoreDistributionSalesStatistics(pageNum, productType, day, new APIStoreDistributionSalesStatisticsResultCallback() {

			@Override
			public void result(String errMsg, StoreDistributionSalesStatistics storeDistributionSalesStatistics, int pageCount) {
				callback.result(errMsg, storeDistributionSalesStatistics, pageCount);
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
	public void loadProductDistributionSalesStatistics(int pageNum, int productType, int day, final ProductDistributionSalesStatisticsResultCallback callback) {
		ApiCaller.getInstance().loadProductDistributionSalesStatistics(pageNum, productType, day, new APIProductDistributionSalesStatisticsResultCallback() {

			@Override
			public void result(String errMsg, ProductDistributionSalesStatistics productDistributionSalesStatistics, int pageCount) {
				callback.result(errMsg, productDistributionSalesStatistics, pageCount);
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
	public void loadShopProductList(String productType, String shopType, int shopId, int statusType, int recommend, int affiliateStatus, int pageNum, final StoreProductListResultCallback callback) {
		ApiCaller.getInstance().loadShopProductList(productType, shopType, shopId, statusType, recommend, affiliateStatus, pageNum, new APIStoreProductListResultCallback() {

			@Override
			public void result(String errMsg, List<StoreProduct> storeProducts, int pageCount) {
				callback.result(errMsg, storeProducts, pageCount);
			}
		});
	}

	/**
	 * 删除产品
	 * 
	 * @param pid
	 * @param sid
	 * @param shopType
	 * @param callback
	 */
	public void deleteProduct(int pid, int sid, String shopType, final StringResultCallback callback) {
		productOperate(pid, sid, shopType, "delete", callback);
	}

	/**
	 * 产品上架
	 * 
	 * @param pid
	 * @param sid
	 * @param shopType
	 * @param callback
	 */
	public void addedProduct(int pid, int sid, String shopType, final StringResultCallback callback) {
		productOperate(pid, sid, shopType, "added", callback);
	}

	/**
	 * 产品下架
	 * 
	 * @param pid
	 * @param sid
	 * @param shopType
	 * @param callback
	 */
	public void shelvesProduct(int pid, int sid, String shopType, final StringResultCallback callback) {
		productOperate(pid, sid, shopType, "shelves", callback);
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
	private void productOperate(int pid, int sid, String shopType, String operateType, final StringResultCallback callback) {
		ApiCaller.getInstance().productOperate(pid, sid, shopType, operateType, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
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
	public void loadCommissionList(String type, int pageNum, final CommissionListResultCallback callback) {
		ApiCaller.getInstance().loadCommissionList(type, pageNum, new APICommissionListResultCallback() {

			@Override
			public void result(String errMsg, List<Commission> commissions, int pageCount) {
				callback.result(errMsg, commissions, pageCount);
			}
		});
	}

	/**
	 * 佣金支付
	 * 
	 * @param othersShopId
	 *            他人店铺id（C店）
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
	public void brokerageDefray(int othersShopId, float defrayMoney, float unDefrayMoney, String defrayRemark, long defrayTime, final StringResultCallback callback) {
		int selfShopId = AppSetting.getInstance().getIntPreferencesByKey(Define.SID);
		int defrayType = 1;

		ApiCaller.getInstance().brokerageDefray(selfShopId, othersShopId, defrayType, defrayMoney, unDefrayMoney, defrayRemark, defrayTime, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 加载佣金详细页
	 * 
	 * @param othersShopId
	 *            他人店铺id
	 * @param shopType
	 *            店铺类型（a、c）
	 * @param pageNum
	 *            第几页(默认为1）
	 * @param callback
	 */
	public void loadCommissionDetail(int pageNum, int othersShopId, String shopType, final CommissionDetailResultCallback callback) {
		int selfShopId = AppSetting.getInstance().getIntPreferencesByKey(Define.SID);

		ApiCaller.getInstance().loadCommissionDetail(pageNum, othersShopId, selfShopId, shopType, new APICommissionDetailResultCallback() {

			@Override
			public void result(String errMsg, CommissionDetail commissionDetail, int pageCount) {
				callback.result(errMsg, commissionDetail, pageCount);
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
	public void loadClientList(String type, final CustomerInfoListResultCallback callback) {
		ApiCaller.getInstance().loadClientList(type, new APICustomerInfoListResultCallback() {

			@Override
			public void result(String errMsg, List<CustomerInfo> customerInfos) {
				callback.result(errMsg, customerInfos);
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
	public void openAStore(String storeType, int othersShopId, String name, String mobile, final StringResultCallback callback) {
		ApiCaller.getInstance().openAStore(storeType, othersShopId, name, mobile, new APIOpenStoreResultCallback() {

			@Override
			public void result(String errMsg, int storeId) {
				// 更新本地用户信息
				UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
				if (localUserInfo != null) {
					localUserInfo.setShopC(storeId);
					UserManager.getInstance().updateUserInfo(localUserInfo);
				}

				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取分销模板列表
	 * 
	 * @param callback
	 */
	public void loadDistributionTemplateList(final DistributionTemplateListResultCallback callback) {
		ApiCaller.getInstance().loadDistributionTemplateList(new APIDistributionTemplateListResultCallback() {

			@Override
			public void result(String errMsg, List<DistributionTemplate> distributionTemplates) {
				callback.result(errMsg, distributionTemplates);
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
	public void addDistributionTemplate(String title, int type, String value, int defaultTemplate, String remark, final DistributionTemplateResultCallback callback) {
		ApiCaller.getInstance().addDistributionTemplate(title, type, value, defaultTemplate, remark, new APIDistributionTemplateResultCallback() {

			@Override
			public void result(String errMsg, int templateId) {
				callback.result(errMsg, templateId);
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
	public void updateDistributionSetting(int pid, String siteType, int isPercentage, String affiliateCommission, int themeId, final StringResultCallback callback) {
		ApiCaller.getInstance().updateDistributionSetting(pid, siteType, isPercentage, affiliateCommission, themeId, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
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
	 * 保存或更新店铺
	 * 
	 * @param store
	 */
	public void saveOrUpdateStore(Store store) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		db.save(store);
	}

	/**
	 * 获取当前店铺
	 * 
	 * @return
	 */
	public Store getCurrentStore() {
		int storeId = AppSetting.getInstance().getIntPreferencesByKey(Define.SID);
		Store store = getStoreBySid(storeId);
		return store;
	}

	/**
	 * 根据店铺id获取店铺
	 * 
	 * @param sid
	 * @return
	 */
	public Store getStoreBySid(int sid) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		Store store = db.queryById(sid, Store.class);
		return store;
	}

}
