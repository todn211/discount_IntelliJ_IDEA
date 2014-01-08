package com.zixingchen.discount.widgetex.PopupWindowSuper;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.zixingchen.discount.R;
import com.zixingchen.discount.utils.PxAndDpUtil;

/**
 * 更多菜单弹出窗口，带指向箭头
 * @author 陈梓星
 */
public class PopupMenuWindow extends PopupWindow {
	private Context context;
	private ListView menus;//菜单项列表
	
	public PopupMenuWindow(Context context,List<Map<String, Object>> menuItems) {
		super(context);
		
		//初始化菜单项列表
		View menus = this.initMenus(context,menuItems);
		
		//初始化菜单容器
		View contentView = this.initContentView(context,menus);
		
		this.setContentView(contentView);
		this.setBackgroundDrawable(new BitmapDrawable());
		this.setFocusable(true);
        this.setAnimationStyle(android.R.style.Animation_InputMethod);
//		this.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//2.3没有效果
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	/**
	 * 初始化菜单容器
	 * @return 菜单容器
	 */
	private View initContentView(Context context,View child){
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.arrow_contain_top_right));
		linearLayout.setLayoutParams(params);
		linearLayout.addView(child);
		return linearLayout;
	}
	
	/**
	 * 初始化菜单项列表
	 */
	private View initMenus(Context context,List<Map<String, Object>> menuItems){
		String[] from = new String[]{"icon","title"};
		int[] to = new int[]{R.id.ivIcon,R.id.tvTitle};
		
		LayoutParams params = new LayoutParams((int)PxAndDpUtil.dip2px(context, 130f),LayoutParams.WRAP_CONTENT);
		SimpleAdapter adapter = new SimpleAdapter(context, menuItems, R.layout.popup_menu_window_item, from, to);
		menus = new ListView(context);
		menus.setLayoutParams(params);
		menus.setAdapter(adapter);
		menus.setDivider(new ColorDrawable(Color.GRAY));
		menus.setDividerHeight(1);
		return menus;
	}

	/**
	 * 菜单项点击事件回调
	 * @param onItemClickListener 菜单项点击事件监听者
	 */
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		menus.setOnItemClickListener(onItemClickListener);
	}
}