package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

/**
 * 产品详情
 * 
 * @author ke.wei.quan
 * 
 */
public class ProductDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2247776688351543650L;
	private String shopName; // 店名
	private String img; // 图片
	private String productName; // 产品名
	private float rate; // 产品评分
	private int orderCount; // 产品预订人数
	private int stock; // 产品库存
	private int shopId; // 店铺id
	private int isPublic; // 产品是否可见(0:不可见,1:可见)
	private String introduction; // 产品介绍
	private int productStatus; // 产品销售状态 (1:出售中 2:删除 3:未出售,也指下架)
	private int shopStatus; // 店铺销售状态
							// ((0:审核中，1：正常开店，2：审核拒绝，3：审核通过未发布，4：冻结，5：暂停店铺))
	private String mobile; // 联系电话
	private String shareUrl; // 分享链接
	private String address; // 地址
	private String openTime; // 营业时间
	private String productType; // 产品类型
	private int collect; // 是否收藏
	private String price; // 产品价格
	private String sku; // 产品编号
	private String startDescription; // 出发
	private String endDescription; // 结束
	private List<DayList> dayList; // 线路节点列表
	private List<Product> partList; // 组合产品列表
	private String marketPrice; // 市场价
	private String priceColumnLeft; // 价格栏目左边“¥”
	private String priceColumnRight; // 价格栏目右边（“起/人”）
	private int tag; // 显示的标签页（0：默认，1,2,3）
	private String shopLogo; // 店铺图片

	private int isShowCalendar; // 是否显示日历

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

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
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

	public int getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(int productStatus) {
		this.productStatus = productStatus;
	}

	public int getShopStatus() {
		return shopStatus;
	}

	public void setShopStatus(int shopStatus) {
		this.shopStatus = shopStatus;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
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

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public int getCollect() {
		return collect;
	}

	public void setCollect(int collect) {
		this.collect = collect;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
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

	public String getPriceColumnLeft() {
		return priceColumnLeft;
	}

	public void setPriceColumnLeft(String priceColumnLeft) {
		this.priceColumnLeft = priceColumnLeft;
	}

	public String getPriceColumnRight() {
		return priceColumnRight;
	}

	public void setPriceColumnRight(String priceColumnRight) {
		this.priceColumnRight = priceColumnRight;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int isShowCalendar() {
		return isShowCalendar;
	}

	public void setShowCalendar(int isShowCalendar) {
		this.isShowCalendar = isShowCalendar;
	}

	public String getShopLogo() {
		return shopLogo;
	}

	public void setShopLogo(String shopLogo) {
		this.shopLogo = shopLogo;
	}

	public int getIsShowCalendar() {
		return isShowCalendar;
	}

	public void setIsShowCalendar(int isShowCalendar) {
		this.isShowCalendar = isShowCalendar;
	}

	@Override
	public String toString() {
		return "ProductDetail [shopName=" + shopName + ", img=" + img + ", productName=" + productName + ", rate=" + rate + ", orderCount=" + orderCount + ", stock=" + stock + ", shopId=" + shopId + ", isPublic=" + isPublic + ", introduction=" + introduction + ", productStatus=" + productStatus + ", shopStatus=" + shopStatus + ", mobile=" + mobile + ", shareUrl=" + shareUrl + ", address=" + address + ", openTime=" + openTime + ", productType=" + productType + ", collect=" + collect + ", price=" + price + ", sku=" + sku + ", startDescription=" + startDescription + ", endDescription=" + endDescription + ", dayList=" + dayList + ", partList=" + partList + ", marketPrice=" + marketPrice + ", priceColumnLeft=" + priceColumnLeft + ", priceColumnRight=" + priceColumnRight + ", tag=" + tag
				+ ", isShowCalendar=" + isShowCalendar + "]";
	}

}
