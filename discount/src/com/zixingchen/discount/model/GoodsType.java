package com.zixingchen.discount.model;

import java.io.Serializable;
import java.util.List;

/**
 * 商品类型
 * @author 陈梓星
 */
public class GoodsType implements Serializable{
	private static final long serialVersionUID = 5689883900739188514L;
	
	public static final String YES = "Y";
	public static final String NO = "N";
	public static final long DEFAULT_ID = -1;
	
	private Long id;
	private Long parentId;//所属分类ID
	private String name;//类型名
	private String typeCode;//编码
	private String keyWord;//关键字，用于查询
	private String icon;//商品图标
	private String isLeaf;//是否为最后一级，N表示否，Y表示是
	private String isShow;//是否显示默认的分类(ID为-1)，N表示否，Y表示是
	private List<Goods> goodses;//相关的商品
	private boolean hasExpand;//该组是否被展开过
	
	public GoodsType() {
		
	}
	
	public GoodsType(Long id, Long parentId, String name, String typeCode, String keyWord,String isLeaf) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.typeCode = typeCode;
		this.keyWord = keyWord;
		this.isLeaf = isLeaf;
	}

	public String isLeaf() {
		return isLeaf;
	}
	public void setLeaf(String isLeaf) {
		this.isLeaf = isLeaf;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
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
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public List<Goods> getGoodses() {
		return goodses;
	}

	public void setGoodses(List<Goods> goodses) {
		this.goodses = goodses;
	}

	public String isShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public boolean getHasExpand() {
		return hasExpand;
	}

	public void setHasExpand(boolean hasExpand) {
		this.hasExpand = hasExpand;
	}
}
