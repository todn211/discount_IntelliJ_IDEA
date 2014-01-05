package com.zixingchen.discount.business;

import java.util.List;

import android.content.Context;

import com.zixingchen.discount.dao.GoodsTypeDao;
import com.zixingchen.discount.model.GoodsType;

/**
 * 商品类别逻辑处理类
 * @author 陈梓星
 */
public class GoodsTypeBusiness {
	private GoodsTypeDao dao;
	
	public GoodsTypeBusiness() {
		this.dao = new GoodsTypeDao();
	}
	
	/**
	 * 查询已关注的商品类别
	 * @return 已关注的商品类别集合
	 */
	public List<GoodsType> findFocusGoodsTypes(){
		return dao.findFocusGoodsTypes();
	}
	
	/**
	 * 查询商品类别
	 * @param filter 要过滤的条件
	 * @return 商品添加集合
	 */
	public List<GoodsType> findGoodsTypes(GoodsType goodsType){
		return dao.findGoodsTypes(goodsType);
	}
	
	/**
	 * 根据父ID查找商品类别集合
	 * @param 商品类别父ID
	 * @return 商品类别集合
	 */
	public List<GoodsType> findGoodsTypesByParentId(Long parentId){
		GoodsType goodsType = new GoodsType();
		goodsType.setParentId(parentId);
		return this.findGoodsTypes(goodsType);
	}
}
