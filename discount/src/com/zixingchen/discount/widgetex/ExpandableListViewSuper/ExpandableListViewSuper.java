package com.zixingchen.discount.widgetex.ExpandableListViewSuper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * 我的关注列表控件
 * @author 陈梓星
 */
public class ExpandableListViewSuper extends ExpandableListView {
	private OnChildOperationListener OnChildOperationListener;
	private MyFocusGoodsItemLoyout currentItem;

	private float xDistance;//X轴上相对上一次前移动了多少位置
	private float  yDistance;//Y轴上相对上一次前移动了多少位置
	private float  xPre;//X轴上一次按下时的位置
	private float  yPre;//Y轴上一次按下时的位置
	
	public ExpandableListViewSuper(Context context) {
		super(context);
	}

	public ExpandableListViewSuper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				xDistance = yDistance = 0f;
				xPre = event.getX();
				yPre = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float curX = event.getX();
				float curY = event.getY();
				
				xDistance = Math.abs(curX - xPre);
				yDistance = Math.abs(curY - yPre);
				
				//如果X轴移动的位置大于Y轴移动的位置，说明用户是在做横向滑动操作，这样就要返回false以便把事件传递给列表项视图
				if (xDistance > yDistance) {
					return false;
				}else if(currentItem != null){
					currentItem.resetViewPosition();
				}
				break;
		}

		return super.onInterceptTouchEvent(event);
	}

	public OnChildOperationListener getOnChildOperationListener() {
		return OnChildOperationListener;
	}

	public void setOnChildOperationListener(
			OnChildOperationListener onChildOperationListener) {
		OnChildOperationListener = onChildOperationListener;
	}

	public MyFocusGoodsItemLoyout getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(MyFocusGoodsItemLoyout currentItems) {
		this.currentItem = currentItems;
	}
}