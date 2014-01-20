package com.zixingchen.discount.widgetex.ExpandableListViewSuper;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.*;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zixingchen.discount.R;

/**
 * 我的关注列表子元素视图
 * @author 陈梓星
 */
public class MyFocusGoodsItemLoyout extends RelativeLayout {

	private GestureDetector gestureDetector;
	private Context context;
	private ImageButton btDelete;
	private boolean isInterceptEevntToChild = true;//是否拦截事件传递到子控件
	private ViewGroup moveTarget;//位于顶层的，需要向左移动的容器
	private int btDeleteWidth;//删除按键的宽度
	private int groupPosition;//分组位置
	private int childPosition;//组下面元素子位置
    private ImageView dragItem;

	public MyFocusGoodsItemLoyout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setLongClickable(true);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		btDeleteWidth = this.findViewById(R.id.btDelete).getMeasuredWidth();
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//如果顶层视图没有被拖到最左边，就把拦截传递到子视图的事件标记为true
		if(moveTarget != null && moveTarget.getLeft() != -btDeleteWidth){
			isInterceptEevntToChild = true;
		}else{
			isInterceptEevntToChild = false;
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		//发布删除事件
		btDelete = (ImageButton) this.findViewById(R.id.btDelete);
		btDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ExpandableListViewSuper expandableListView = (ExpandableListViewSuper) MyFocusGoodsItemLoyout.this.getParent();
				expandableListView.getOnChildOperationListener()
									.onChildDelete(expandableListView, MyFocusGoodsItemLoyout.this, groupPosition, childPosition);
			}
		});
		
		//监听触摸事件
		gestureDetector = new GestureDetector(context, new MyGestureListener());
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(MotionEvent.ACTION_UP == event.getAction()){
					//如果顶层视图没有被拖到最左边，就把其还原到原来的位置
					if(moveTarget.getLeft() != -btDeleteWidth){
						resetViewPosition();
					}

                    //删除WindowManager
                    if (dragItem != null){
                        WindowManager wm = (WindowManager)MyFocusGoodsItemLoyout.this.getContext().getSystemService(Context.WINDOW_SERVICE);
                        wm.removeView(dragItem);
                        dragItem = null;
                    }
				}

                if (MotionEvent.ACTION_MOVE == event.getAction()){
                    WindowManager wm = (WindowManager)MyFocusGoodsItemLoyout.this.getContext().getSystemService(Context.WINDOW_SERVICE);
//                    wm.
                }
				return gestureDetector.onTouchEvent(event);
			}
		});
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		super.onInterceptTouchEvent(ev);
		return isInterceptEevntToChild;
	}
	
	/**
	 * 重置视图位置
	 */
	public void resetViewPosition(){
		LayoutParams params = (LayoutParams) moveTarget.getLayoutParams();
		params.leftMargin = 0;
		params.topMargin = 0;
		moveTarget.setLayoutParams(params);
	}
	
	/**
	 * 手势识别对象
	 */
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		public MyGestureListener() {
			moveTarget = (ViewGroup) MyFocusGoodsItemLoyout.this.getChildAt(1);
		}

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            MyFocusGoodsItemLoyout.this.setDrawingCacheEnabled(true);
            dragItem = new ImageView(MyFocusGoodsItemLoyout.this.getContext());
            dragItem.setImageBitmap(MyFocusGoodsItemLoyout.this.getDrawingCache());
            WindowManager wm = (WindowManager)MyFocusGoodsItemLoyout.this.getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            layoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
//            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            layoutParams.y = (int)(e.getRawY()-MyFocusGoodsItemLoyout.this.getMeasuredHeight()/2);
            wm.addView(dragItem,layoutParams);
        }

        /**
		 * 按下事件，在ExpandableListViewSuper中记录当前对象，以便在向上、下滚动时如果当前位置有变动则复位
		 */
		@Override
		public boolean onDown(MotionEvent e) {
			ExpandableListViewSuper expandableListView = (ExpandableListViewSuper) MyFocusGoodsItemLoyout.this.getParent();
			
			if(expandableListView.getCurrentItem() != null && expandableListView.getCurrentItem() != MyFocusGoodsItemLoyout.this){
				expandableListView.getCurrentItem().resetViewPosition();
			}
			
			expandableListView.setCurrentItem(MyFocusGoodsItemLoyout.this);
			
			return super.onDown(e);
		}
		
		/**
		 * 我的关注元素点击事件，把当前点击事件包装再发布出去
		 */
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			super.onSingleTapUp(e);
			ExpandableListViewSuper expandableListView = (ExpandableListViewSuper) MyFocusGoodsItemLoyout.this.getParent();
			expandableListView.getOnChildOperationListener()
								.onChildClick(expandableListView, MyFocusGoodsItemLoyout.this, groupPosition, childPosition);
			return true;
		}
		
		/**
		 * 顶层视图向左滑动，显示出删除按钮
		 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			int targetX = moveTarget.getLeft() - (int)distanceX;
			if(targetX > 0)
				targetX = 0;
			else if(targetX < -btDeleteWidth)
				targetX = -btDeleteWidth;
			
			LayoutParams params = (LayoutParams) moveTarget.getLayoutParams();
			params.leftMargin = targetX;
			params.width = MyFocusGoodsItemLoyout.this.getMeasuredWidth();
			moveTarget.setLayoutParams(params);
			
			//去掉点击产生的背景色
			moveTarget.getBackground().setState(EMPTY_STATE_SET);
			return true;
		}
	}

	public int getGroupPosition() {
		return groupPosition;
	}

	public void setGroupPosition(int groupPosition) {
		this.groupPosition = groupPosition;
	}

	public int getChildPosition() {
		return childPosition;
	}

	public void setChildPosition(int childPosition) {
		this.childPosition = childPosition;
	}
}
