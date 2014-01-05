package com.zixingchen.discount.common;

import java.io.Serializable;
import java.util.List;

/**
 * 分页类
 * @author 陈梓星
 */
public class Page<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int pageSize = 10;//一页显示的行数
	private int pageNumber = 1;//当前页码
	private int totalPage;//一共有几多页
	private List<T> datas;//当前页的数据集合
	
	/**
	 * 克隆一个没有数据集合(datas为空 )的Page对象
	 * @return
	 */
	public Page<T> clonePageNotDatas(){
		Page<T> page = new Page<T>();
		page.setPageNumber(pageNumber);
		page.setPageSize(pageSize);
		page.setTotalPage(totalPage);
		return page;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
	public List<T> getDatas() {
		return datas;
	}
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
	
	/**
	 * 是否达到最后一页
	 * @return true时是最后一页
	 */
	public boolean isLastPage() {
		return !(pageNumber <= totalPage);
	}
	
	/**
	 * 从哪行开始获取数据
	 */
	public int getStartRecord(){
		return pageSize * (pageNumber-1);
	}
}