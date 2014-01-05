package com.zixingchen.discount.widgetex.ExpandableListViewSuper;

import android.view.View;
import android.widget.ExpandableListView;


/**
 * ExpandableListViewEx元素操作事件监听器接口
 * @author 陈梓星
 */
public interface OnChildOperationListener {
	/**
	 * 子元素点击事件
	 * @param parent 当前视图的父对象
	 * @param v 当前视图对象
	 * @param groupPosition 元素所在的组索引
	 * @param childPosition 元素索引
	 */
	public void onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition);
	
	/**
	 * 子元素删除事件
	 * @param parent 当前视图的父对象
	 * @param v 当前视图对象
	 * @param groupPosition 元素所在的组索引
	 * @param childPosition 元素索引
	 */
	public void onChildDelete(ExpandableListView parent, View v, int groupPosition, int childPosition);
}