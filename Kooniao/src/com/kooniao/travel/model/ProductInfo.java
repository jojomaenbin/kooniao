package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZZD on 2015/5/26.
 * last modify by ke.wei.quan
 */
public class ProductInfo implements Serializable {
	/**
     *
     */
	private static final long serialVersionUID = 1452760308856398258L;
	/**
     *
     */
	private String shopName; // 店名
	private String img; // 图片
	private String productName; // 产品名
	private float rate; // 产品评分
	private int stock; // 产品库存
	private int shopId; // 店铺id
	private String introduction; // 产品介绍
	private String mobile; // 联系电话
	private String address; // 地址
	private String openTime; // 营业时间
	private String startTime; // 上架时间
	private String endTime; // 下架时间
	private String unit; // 市场价单位
	private String sku; // 产品编号
	private String startDescription; // 出发城市
	private String endDescription; // 结束城市
	private List<DayList> dayList; // 线路节点列表
	private String dayTips; // 备注
	private String dayDate; // 时间
	private String dayTitle; // 标题
	private List<Product> partList; // 组合产品列表
	private String marketPrice; // 市场价
	private String price; // 价格
	private int grade; // 产品星级
	private int productClass_0; // 一级分类
	private int productClass_1; // 二级分类
	private int productClass_2; // 三级分类
	public String productClass_0Name; // 一级分类
	public String productClass_1Name; // 二级分类
	public String productClass_2Name; // 三级分类

	public List<Area> areaList;// 地区列表
	public List<String> imageList;

	private int isPublic; // 产品是否可见
	private int status; // 产品是否上下架
	private int webappDefault; // 默认展示页
	private int minBuy;// 最小起订量
	private int isBook;// 库存为0可预订
	private int isShowStock;// 是否显示库存
	private int returnStatus;// 退货处理(直接回退至库存是1，手动为0)
	private List<SubProduct> combination; // 组合子产品列表
	private List<ProductStandard> packageList; // 产品规格列表
	private List<ProductTag> tagList; // 产品标签列表
	private List<ProductCatalog> catalogList; // 产品目录列表

	private int affiliate_commission;// 是否分销
	private int is_percentage;// 是否按百分比
	private int affiliateStatus;// 分销金额
	private int affiliate_commission_percentage;// 佣金百分比
	private int commission_theme;// 佣金模板
	private int type;// 1:手动、2：模板、0：没有设置



	public class ProductTag implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8553031919625153172L;
		private int id; // 标签id
		private String name; // 标签名字

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public class SubProduct implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1749562839922592924L;
		private int id;
		private String logo;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

	}



	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getStartDescription() {
		return startDescription;
	}

	public void setStartDescription(String startDescription) {
		this.startDescription = startDescription;
	}

	public String getEndDescription() {
		return endDescription;
	}

	public void setEndDescription(String endDescription) {
		this.endDescription = endDescription;
	}

	public List<DayList> getDayList() {
		return dayList;
	}

	public void setDayList(List<DayList> dayList) {
		this.dayList = dayList;
	}

	public String getDayTips() {
		return dayTips;
	}

	public void setDayTips(String dayTips) {
		this.dayTips = dayTips;
	}

	public String getDayDate() {
		return dayDate;
	}

	public void setDayDate(String dayDate) {
		this.dayDate = dayDate;
	}

	public String getDayTitle() {
		return dayTitle;
	}

	public void setDayTitle(String dayTitle) {
		this.dayTitle = dayTitle;
	}

	public List<Product> getPartList() {
		return partList;
	}

	public void setPartList(List<Product> partList) {
		this.partList = partList;
	}

	public String getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(String marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getProductClass_0() {
		return productClass_0;
	}

	public void setProductClass_0(int productClass_0) {
		this.productClass_0 = productClass_0;
	}

	public int getProductClass_1() {
		return productClass_1;
	}

	public void setProductClass_1(int productClass_1) {
		this.productClass_1 = productClass_1;
	}

	public int getProductClass_2() {
		return productClass_2;
	}

	public void setProductClass_2(int productClass_2) {
		this.productClass_2 = productClass_2;
	}

	public String getProductClass_0Name() {
		return productClass_0Name;
	}

	public void setProductClass_0Name(String productClass_0Name) {
		this.productClass_0Name = productClass_0Name;
	}

	public String getProductClass_1Name() {
		return productClass_1Name;
	}

	public void setProductClass_1Name(String productClass_1Name) {
		this.productClass_1Name = productClass_1Name;
	}

	public String getProductClass_2Name() {
		return productClass_2Name;
	}

	public void setProductClass_2Name(String productClass_2Name) {
		this.productClass_2Name = productClass_2Name;
	}

	public List<Area> getAreaList() {
		return areaList;
	}

	public void setAreaList(List<Area> areaList) {
		this.areaList = areaList;
	}

	public List<String> getImageList() {
		return imageList;
	}

	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}

	public int getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getWebappDefault() {
		return webappDefault;
	}

	public void setWebappDefault(int webappDefault) {
		this.webappDefault = webappDefault;
	}

	public int getMinBuy() {
		return minBuy;
	}

	public void setMinBuy(int minBuy) {
		this.minBuy = minBuy;
	}

	public int getIsBook() {
		return isBook;
	}

	public void setIsBook(int isBook) {
		this.isBook = isBook;
	}

	public int getIsShowStock() {
		return isShowStock;
	}

	public void setIsShowStock(int isShowStock) {
		this.isShowStock = isShowStock;
	}

	public int getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(int returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<SubProduct> getCombination() {
		return combination;
	}

	public void setCombination(List<SubProduct> combination) {
		this.combination = combination;
	}

	public List<ProductStandard> getPackageList() {
		return packageList;
	}

	public void setPackageList(List<ProductStandard> packageList) {
		this.packageList = packageList;
	}

	public List<ProductTag> getTagList() {
		return tagList;
	}

	public void setTagList(List<ProductTag> tagList) {
		this.tagList = tagList;
	}

	public List<ProductCatalog> getCatalogList() {
		return catalogList;
	}

	public void setCatalogList(List<ProductCatalog> catalogList) {
		this.catalogList = catalogList;
	}

	public int getAffiliate_commission() {
		return affiliate_commission;
	}

	public void setAffiliate_commission(int affiliate_commission) {
		this.affiliate_commission = affiliate_commission;
	}

	public int getIs_percentage() {
		return is_percentage;
	}

	public void setIs_percentage(int is_percentage) {
		this.is_percentage = is_percentage;
	}

	public int getAffiliateStatus() {
		return affiliateStatus;
	}

	public void setAffiliateStatus(int affiliateStatus) {
		this.affiliateStatus = affiliateStatus;
	}

	public int getAffiliate_commission_percentage() {
		return affiliate_commission_percentage;
	}

	public void setAffiliate_commission_percentage(int affiliate_commission_percentage) {
		this.affiliate_commission_percentage = affiliate_commission_percentage;
	}

	public int getCommission_theme() {
		return commission_theme;
	}

	public void setCommission_theme(int commission_theme) {
		this.commission_theme = commission_theme;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
