package com.zixingchen.discount.model;

import java.io.Serializable;

/**
 * 商品
 * @author 陈梓星
 */
public class Goods implements Serializable{
	private static final long serialVersionUID = 1839147853139127631L;
	
	private Long id;
	private Long goodsTypeId;//所属分类ID
	private Float prePrice;//商品价格
	private Float currentPrice;//当前价格
	private String name;//商品名
	private String subTitle;//商品子标题
	private String descript;//商品说明
	private String icon;//图标
	private String href;//商品的URL
	private String priceCache;//价格缓存，为节省关注列表的网络请求
    private PriceState priceState;//价格状态

    /**
     * 价格状态
     */
    public static enum PriceState{
        /**
         * 升价
         */
        UP,
        /**
         * 降价
         */
        DOWN,

        /**
         * 不变
         */
        EQUATION
    }

	public Goods() {
	}
	
	public Goods(Long id) {
		this.id = id;
	}
	
	public Goods(String name) {
		this.setName(name);
	}

	public Long getGoodsTypeId() {
		return goodsTypeId;
	}

	public void setGoodsTypeId(Long goodsTypeId) {
		this.goodsTypeId = goodsTypeId;
	}

	public Float getPrePrice() {
		return prePrice;
	}

	public void setPrePrice(Float prePrice) {
		this.prePrice = prePrice;
	}

	public Float getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(Float currentPrice) {
		this.currentPrice = currentPrice;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getPriceCache() {
		return priceCache;
	}

	public void setPriceCache(String priceCache) {
		this.priceCache = priceCache;
	}

    public PriceState getPriceState() {
        return priceState;
    }

    public void setPriceState(PriceState priceState) {
        this.priceState = priceState;
    }
}
