package com.kooniao.travel.model;

/**
 * 店铺产品
 * 
 * @author ke.wei.quan
 * 
 */
public class StoreProduct {
	private String img;// 图片
	private String productName;// 产品名称
	private float productPrice;// 价格
	private int productType;// 类型
	private int productStatus;// 产品状态（0：全部、1:出售中、3:未出售、4、售罄）
	private int affiliateStatus;// 产品是否分销(0:不分销,1:分销)
	private int productId;// 产品id
	private String shopName;// 店铺名称
	private int brokerageType; // 返佣类型(0：不按百分比，1：按百分比)
	private float brokerage;// 返佣
	private int percentage; // 返佣百分比
	private String affiliate_comment; // 返佣备注
	private int productCommissionTheme; // 产品分销模板id

	/*
	 * A店新增加字段
	 */
	private String productStock; // 产品库存
	private String productPriceAmount; // 产品套餐数
	private String productScope; // 产品价格范围
	private String productShareUrl; // 产品分享链接
	private int productRecommend; // 产品是否推荐 (0:默认,1：已推荐)

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

	public float getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(float productPrice) {
		this.productPrice = productPrice;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public int getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(int productStatus) {
		this.productStatus = productStatus;
	}

	public int getAffiliateStatus() {
		return affiliateStatus;
	}

	public void setAffiliateStatus(int affiliateStatus) {
		this.affiliateStatus = affiliateStatus;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public int getBrokerageType() {
		return brokerageType;
	}

	public void setBrokerageType(int brokerageType) {
		this.brokerageType = brokerageType;
	}

	public float getBrokerage() {
		return brokerage;
	}

	public void setBrokerage(float brokerage) {
		this.brokerage = brokerage;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public String getAffiliate_comment() {
		return affiliate_comment;
	}

	public void setAffiliate_comment(String affiliate_comment) {
		this.affiliate_comment = affiliate_comment;
	}

	public int getProductCommissionTheme() {
		return productCommissionTheme;
	}

	public void setProductCommissionTheme(int productCommissionTheme) {
		this.productCommissionTheme = productCommissionTheme;
	}

	public String getProductStock() {
		return productStock;
	}

	public void setProductStock(String productStock) {
		this.productStock = productStock;
	}

	public String getProductPriceAmount() {
		return productPriceAmount;
	}

	public void setProductPriceAmount(String productPriceAmount) {
		this.productPriceAmount = productPriceAmount;
	}

	public String getProductScope() {
		return productScope;
	}

	public void setProductScope(String productScope) {
		this.productScope = productScope;
	}

	public String getProductShareUrl() {
		return productShareUrl;
	}

	public void setProductShareUrl(String productShareUrl) {
		this.productShareUrl = productShareUrl;
	}

	public int getProductRecommend() {
		return productRecommend;
	}

	public void setProductRecommend(int productRecommend) {
		this.productRecommend = productRecommend;
	}

	@Override
	public String toString() {
		return "StoreProduct [img=" + img + ", productName=" + productName + ", productPrice=" + productPrice + ", productType=" + productType + ", productStatus=" + productStatus + ", affiliateStatus=" + affiliateStatus + ", productId=" + productId + ", shopName=" + shopName + ", brokerageType=" + brokerageType + ", brokerage=" + brokerage + ", percentage=" + percentage + ", affiliate_comment=" + affiliate_comment + ", productCommissionTheme=" + productCommissionTheme + ", productStock=" + productStock + ", productPriceAmount=" + productPriceAmount + ", productScope=" + productScope + ", productShareUrl=" + productShareUrl + ", productRecommend=" + productRecommend + "]";
	}

}
