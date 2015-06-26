package com.kooniao.travel.manager;

import java.util.List;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APICommentListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIProductCategoryResultCallback;
import com.kooniao.travel.api.ApiCaller.APIProductDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIProductInfoResultCallback;
import com.kooniao.travel.api.ApiCaller.APIProductListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIProductResourceListCallback;
import com.kooniao.travel.api.ApiCaller.APIProductpackageResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStringListCallback;
import com.kooniao.travel.api.ApiCaller.APIStringResultCallback;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.*;
import com.kooniao.travel.utils.AppSetting;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

/**
 * 产品管理
 * 
 * @author ke.wei.quan
 * 
 */
public class ProductManager {
	ProductManager() {
	}

	private static ProductManager instance;

	public static ProductManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new ProductManager();
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

	public interface ProductListResultCallback {
		void result(String errMsg, List<Product> products, int pageCount);
	}

	public interface ProductDetailResultCallback {
		void result(String errMsg, ProductDetail productDetail);
	}

	public interface CommentListResultCallback {
		void result(String errMsg, List<Comment> comments, int pageCount);
	}

	public interface StringResultCallback {
		void result(String errMsg);
	}

	public interface StringListCallback {
		void result(String errMsg, List<String> imgList);
	}

	public interface ProductResourceListCallback {
		void result(String errMsg, List<ProductResource> productResourceList, int pageCount);
	}

	public interface ProductpackageResultCallback {
		void result(String errMsg, long serviceTime, int isShowStock, int isMinBuy, int minBuy, int isBook, List<ProductPackage> productPackages);

	}

	public interface ProductCategoryResultCallback {
		void result(String errMsg, ProductCategory class_List);

	}

	public interface ProductInfoResultCallback {
		void result(String errMsg, ProductInfo productDetail);
	}

	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	// private boolean isFirstTimeLoadProductList = true;

	/**
	 * 获取产品列表
	 * 
	 * @param cid
	 * @param type
	 * @param pageNum
	 * @param callback
	 */
	public void loadProductList(int cid, int type, int pageNum, final ProductListResultCallback callback) {
		ApiCaller.getInstance().loadProductList(cid, type, pageNum, new APIProductListResultCallback() {

			@Override
			public void result(String errMsg, List<Product> products, int pageCount) {
				if (products != null) {
					saveOrUpdateProductList(products);
				}

				callback.result(errMsg, products, pageCount);
			}
		});
	}

	/**
	 * 搜索产品
	 * 
	 * @param cid
	 * @param keyWord
	 * @param type
	 *            全部：0 线路：4 组合：2 酒店：5 美食：8 娱乐：7
	 * @param pageNum
	 * @param callback
	 */
	public void productSearch(int cid, String keyWord, int type, int pageNum, final ProductListResultCallback callback) {
		ApiCaller.getInstance().productSearch(cid, keyWord, type, pageNum, new APIProductListResultCallback() {

			@Override
			public void result(String errMsg, List<Product> products, int pageCount) {
				callback.result(errMsg, products, pageCount);
			}
		});
	}

	/**
	 * 获取产品详情
	 * 
	 * @param pid
	 * @param cid
	 * @param callback
	 */
	public void loadProductDetail(int pid, int cid, final ProductDetailResultCallback callback) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		ApiCaller.getInstance().loadProductDetail(pid, uid, cid, new APIProductDetailResultCallback() {

			@Override
			public void result(String errMsg, ProductDetail productDetail) {
				callback.result(errMsg, productDetail);
			}
		});
	}

	/**
	 * 获取产品编辑信息
	 *
	 * @param pid
	 * @param callback
	 */
	public void loadProductInfo(int pid, final ApiCaller.APIProductInfoResultCallback callback) {
		ApiCaller.getInstance().loadProductInfo(pid, new ApiCaller.APIProductInfoResultCallback() {

			@Override
			public void result(String errMsg, ProductInfo productDetail) {
				callback.result(errMsg, productDetail);
			}
		});
	}

	/**
	 * 获取产品评论列表
	 * 
	 * @param type
	 * @param pid
	 * @param pageNum
	 * @param callback
	 */
	public void loadProductCommentList(int type, int pid, int pageNum, final CommentListResultCallback callback) {
		ApiCaller.getInstance().loadProductCommentList(type, pid, pageNum, new APICommentListResultCallback() {

			@Override
			public void result(String errMsg, List<Comment> comments, int pageCount) {
				callback.result(errMsg, comments, pageCount);
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
	 * @param callback
	 */
	public void addOrCancelToMyCollect(int likeId, int isKeep, String likeType, String likeSubType, int fromId, final StringResultCallback callback) {
		ApiCaller.getInstance().addOrCancelToMyCollect(likeId, isKeep, likeType, likeSubType, fromId, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
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
	public void loadProductLibrary(int type, String keyWord, int pageNum, final ProductListResultCallback callback) {
		ApiCaller.getInstance().loadProductLibrary(type, keyWord, pageNum, new APIProductListResultCallback() {

			@Override
			public void result(String errMsg, List<Product> products, int pageCount) {
				callback.result(errMsg, products, pageCount);
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
	public void addProduct(String productId, final StringResultCallback callback) {
		ApiCaller.getInstance().addProduct(productId, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取产品大图列表
	 * 
	 * @param pid
	 * @param callback
	 */
	public void loadProductLargeImageList(int pid, final StringListCallback callback) {
		ApiCaller.getInstance().loadProductLargeImageList(pid, new APIStringListCallback() {

			@Override
			public void result(String errMsg, List<String> imgList) {
				callback.result(errMsg, imgList);
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

	public List<Product> getProducts() {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		List<Product> products = db.queryAll(Product.class);
		return products;
	}

	/**
	 * 保存或更新产品列表
	 * 
	 * @param products
	 */
	public void saveOrUpdateProductList(List<Product> products) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		long count = db.queryCount(Product.class);
		if (count > 0) {
			db.deleteAll(Product.class);
		}
		db.save(products);
	}

	/**
	 * 保存或更新产品
	 * 
	 * @param product
	 */
	public void saveOrUpdateProduct(Product product) {
		if (product != null) {
			DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
			Product localProduct = db.queryById(product.getProductId(), Product.class);
			if (localProduct != null) {
				db.update(product);
			} else {
				db.save(product);
			}
		}
	}

	/**
	 * 加载产品套餐列表
	 * 
	 * @param pid
	 * @param callback
	 */
	public void loadProductPackageList(int pid, final ProductpackageResultCallback callback) {
		ApiCaller.getInstance().loadProductPackageList(pid, new APIProductpackageResultCallback() {

			@Override
			public void result(String errMsg, long serviceTime, int isShowStock, int isMinBuy, int minBuy, int isBook, List<ProductPackage> productPackages) {
				callback.result(errMsg, serviceTime, isShowStock, isMinBuy, minBuy, isBook, productPackages);
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
	public void productRecommend(int pid, int recommend, final StringResultCallback callback) {
		ApiCaller.getInstance().productRecommend(pid, recommend, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取产品分类列表
	 * 
	 * @param cid
	 * @param type
	 * @param pageNum
	 * @param callback
	 */
	public void loadProductCategory(final ProductCategoryResultCallback callback) {
		ApiCaller.getInstance().loadProductCategoryList(new APIProductCategoryResultCallback() {

			@Override
			public void result(String errMsg, ProductCategory class_List) {

				callback.result(errMsg, class_List);
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
	public void loadProductResource(int pageNum, String sort, int city, int productType, float minPrice, float maxPrice, final ProductResourceListCallback callback) {
		ApiCaller.getInstance().loadProductResource(pageNum, sort, city, productType, minPrice, maxPrice, new APIProductResourceListCallback() {

			@Override
			public void result(String errMsg, List<ProductResource> productResourceList, int pageCount) {
				callback.result(errMsg, productResourceList, pageCount);
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
	public void loadProductSimpleResource(int pageNum, String sort, int city, int productType, float minPrice, float maxPrice, final ProductResourceListCallback callback) {
		ApiCaller.getInstance().loadProductSimpleResource(pageNum, sort, city, productType, minPrice, maxPrice, new APIProductResourceListCallback() {

			@Override
			public void result(String errMsg, List<ProductResource> productResourceList, int pageCount) {
				callback.result(errMsg, productResourceList, pageCount);
			}
		});
	}

	/**
	 * 获取编辑产品的详细信息
	 * 
	 * @param pid
	 * @param callback
	 */
	public void loadProductDetailInfo(int pid, final ProductInfoResultCallback callback) {
		ApiCaller.getInstance().loadProductDetailInfo(pid, new APIProductInfoResultCallback() {
			
			@Override
			public void result(String errMsg, ProductInfo productInfo) {
				callback.result(errMsg, productInfo); 
			}
		});
	}

}
