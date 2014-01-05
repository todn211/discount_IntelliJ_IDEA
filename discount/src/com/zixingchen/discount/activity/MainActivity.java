package com.zixingchen.discount.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zixingchen.discount.R;
import com.zixingchen.discount.business.GoodsBusiness;
import com.zixingchen.discount.business.GoodsTypeBusiness;
import com.zixingchen.discount.common.Contexts;
import com.zixingchen.discount.common.Page;
import com.zixingchen.discount.model.Goods;
import com.zixingchen.discount.model.GoodsType;
import com.zixingchen.discount.utils.ImageLoaderUtils;
import com.zixingchen.discount.widgetex.ExpandableListViewSuper.ExpandableListViewSuper;
import com.zixingchen.discount.widgetex.ExpandableListViewSuper.MyFocusGoodsItemLoyout;
import com.zixingchen.discount.widgetex.ExpandableListViewSuper.OnChildOperationListener;
import com.zixingchen.discount.widgetex.PopupWindowSuper.PopupMenuWindow;

/**
 * 主页
 * @author 陈梓星
 */
public class MainActivity extends Activity implements OnGroupExpandListener,OnChildOperationListener{
	
	private ExpandableListViewSuper lvMyFocus;//关注的列表
	private List<GoodsType> goodsTypes;//关注的商品类型集合
	private GoodsTypeBusiness goodsTypeBusiness;
	private GoodsBusiness goodsBusiness;
	private PopupMenuWindow popupMenuWindow;//更新菜单弹出窗口
	private EditText etSearch;//搜索框
	private Button btSearchOrBack;//搜索或者返回按钮
	private InputMethodManager imm;//输入法管理者
	public static final List<GoodsType> readyGoodsTypes = new ArrayList<GoodsType>();//准备添加到列表的类型集合，集合的元素由其它页面填充，例如GoodsListActivity的商品关注时填充
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_activity);
		
		goodsTypeBusiness = new GoodsTypeBusiness();
		goodsBusiness = new GoodsBusiness();
		
		//初化//输入法管理者
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		//初始化关注列表 
		initLvlvMyFocus();		
		
		//初始化更多弹出菜单
		this.initPopupMenuWindow();
		
		//初始化搜索框
		this.initEtSearch();
	}
	
	/**
	 * 我的关注列表数据是否有更改，如果有就更新列表
	 */
	@Override
	protected void onStart() {
		super.onStart();

        Log.i("MainActivity","***********");

		//我的关注列表数据是否有更改
		SharedPreferences sp = this.getSharedPreferences(Contexts.SYSTEM_CACHE, MODE_PRIVATE);
		if(sp.getBoolean(Contexts.HAS_ADD_FOCUS_GOODS, false)){
			List<Integer> expandGroupPos = new ArrayList<Integer>();//需要展开的分组索引
			boolean isHas = false;
			for (GoodsType newGoodsType : readyGoodsTypes) {
				for (GoodsType goodsType : goodsTypes) {
					if(newGoodsType.getId().longValue() == goodsType.getId().longValue()){
						isHas = true;
						goodsType.getGoodses().addAll(newGoodsType.getGoodses());
						expandGroupPos.add(goodsTypes.indexOf(goodsType));
						break;
					}
				}
				
				if(!isHas){
					goodsTypes.add(newGoodsType);
					expandGroupPos.add(goodsTypes.indexOf(newGoodsType));
				}
				
				isHas = false;
			}
			
			lvMyFocusAdapter adapter = (lvMyFocusAdapter)lvMyFocus.getExpandableListAdapter();
			adapter.notifyDataSetChanged();
			
			//展开有新商品加入的分组
			for (Integer groupPos : expandGroupPos) {
				lvMyFocus.expandGroup(groupPos);
			}
			
			readyGoodsTypes.clear();
			
			//重置列表数据更改状态
			Editor editor = sp.edit();
			editor.putBoolean(Contexts.HAS_ADD_FOCUS_GOODS,false);
			editor.commit();
		}
	}
	
	/**
	 * 初始化搜索框
	 */
	private void initEtSearch(){
		btSearchOrBack = (Button) this.findViewById(R.id.btSearchOrBack);
		etSearch = (EditText) this.findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {
				//如果搜索框的内容为空，就把搜索按钮切换成回返按钮，否则为搜索按钮
				if(TextUtils.isEmpty(text)){
					String back = MainActivity.this.getResources().getString(R.string.back);
					btSearchOrBack.setText(back);
					btSearchOrBack.setTag(back);
					Drawable backDrawable = MainActivity.this.getResources().getDrawable(R.drawable.back_icon);
					btSearchOrBack.setCompoundDrawablesWithIntrinsicBounds(backDrawable, null, null, null);
				}else if(!btSearchOrBack.equals(MainActivity.this.getResources().getString(R.string.search))){
					String search = MainActivity.this.getResources().getString(R.string.search);
					btSearchOrBack.setText(search);
					btSearchOrBack.setTag(search);
					
					Drawable searchDrawable = MainActivity.this.getResources().getDrawable(R.drawable.search_icon);
					btSearchOrBack.setCompoundDrawablesWithIntrinsicBounds(searchDrawable, null, null, null);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	/**
	 * 搜索/返回按钮点击监听事件
	 * @param view
	 */
	public void onSearchOrBackClick(View view){
		String tag = view.getTag().toString();
		LinearLayout toolbarContainer = (LinearLayout) this.findViewById(R.id.toolbarContainer);
		LinearLayout searchContainer = (LinearLayout) this.findViewById(R.id.searchContainer);
		if(tag.equals(this.getResources().getString(R.string.back))){
			//工具条从左边进入
			toolbarContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
			
			//搜索栏从右边退出
			searchContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_right));
		}else{
			//搜索商品
			String goodsName = etSearch.getText().toString();
			GoodsType goodsType = new GoodsType();
			goodsType.setKeyWord(goodsName);
			goodsType.setId(GoodsType.DEFAULT_ID);
			goodsType.setName(goodsName);
			goodsType.setGoodses(new ArrayList<Goods>());
			Intent intent = new Intent(this,GoodsListActivity.class);
			intent.putExtra("goodsType", goodsType);
			this.startActivity(intent);
		}
		
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//隐藏键盘
		toolbarContainer.setVisibility(View.VISIBLE);
		searchContainer.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 更多按钮点击事件监听器，用于弹出更多菜单窗口
	 * @param view
	 */
	public void onMoreClick(View view){
		//获取弹出窗口的坐标
		int[] point = new int[2];
		view.getLocationOnScreen(point);
		int x = point[0] + view.getWidth() / 2;
		int y = point[1] + view.getHeight();
		
		
		popupMenuWindow.showAtLocation(findViewById(android.R.id.content) , Gravity.LEFT | Gravity.TOP, x, y);
	}
	
	/**
	 * 初始化更多菜单窗口
	 */
	public void initPopupMenuWindow(){
		List<Map<String, Object>> menuItems = new ArrayList<Map<String,Object>>();
		Map<String, Object> addMenuItem = new HashMap<String, Object>();
		addMenuItem.put("icon", R.drawable.add_icon);
		addMenuItem.put("title", MainActivity.this.getResources().getString(R.string.add));
		
		Map<String, Object> searchMenuItem = new HashMap<String, Object>();
		searchMenuItem.put("icon",Integer.valueOf(R.drawable.search_icon));
		searchMenuItem.put("title", MainActivity.this.getResources().getString(R.string.search));
		
		Map<String, Object> settingsMenuItem = new HashMap<String, Object>();
		settingsMenuItem.put("icon", Integer.valueOf(R.drawable.settings_icon));
		settingsMenuItem.put("title", MainActivity.this.getResources().getString(R.string.settings));
		
		Map<String, Object> exitMenuItem = new HashMap<String, Object>();
		exitMenuItem.put("icon", Integer.valueOf(R.drawable.exit_icon));
		exitMenuItem.put("title", MainActivity.this.getResources().getString(R.string.exit));
		
		menuItems.add(addMenuItem);
		menuItems.add(searchMenuItem);
		menuItems.add(settingsMenuItem);
		menuItems.add(exitMenuItem);
		popupMenuWindow = new PopupMenuWindow(this,menuItems);
		
		popupMenuWindow.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				switch(position){
					case 0:
						showGoodsTypeActivity();
						break;
					case 1:
						ShowSearchBar();
						break;
					case 2:
						showSettingsActivity();
						break;
					case 3:
						finish();
						break;
				}
			}
		});
	}
	
	/**
	 * 刷新关注的列表
	 * @param view
	 */
	public void onBtRefreshClick(View view){
		goodsTypes = goodsTypeBusiness.findFocusGoodsTypes();
		
		lvMyFocusAdapter adapter = (lvMyFocusAdapter)lvMyFocus.getExpandableListAdapter();
		adapter.notifyDataSetChanged();
		
		//重新展开
		for (int i = 0; i < goodsTypes.size(); i++) {
			if(lvMyFocus.isGroupExpanded(i)){
				lvMyFocus.collapseGroup(i);
			}
		}
		
		if(goodsTypes.size() > 0)
			lvMyFocus.expandGroup(0);
	}
	
	/**
	 * 显示搜索工具条
	 */
	private void ShowSearchBar(){
		popupMenuWindow.dismiss();
		
		//工具条从左边退出
		LinearLayout toolbarContainer = (LinearLayout) this.findViewById(R.id.toolbarContainer);
		toolbarContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_left));
		toolbarContainer.setVisibility(View.INVISIBLE);
		
		//搜索栏从右边进入
		LinearLayout searchContainer = (LinearLayout) this.findViewById(R.id.searchContainer);
		searchContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
		searchContainer.setVisibility(View.VISIBLE);
		
		etSearch.setText("");
	}
	
	/**
	 * 显示软件设置界面
	 */
	private void showSettingsActivity(){
		this.startActivity(new Intent(this,SettingsActivity.class));
		popupMenuWindow.dismiss();
	}
	
	/**
	 * 跳转到添加商品关注的界面
     */
	private void showGoodsTypeActivity(){
		Intent intent = new Intent(this,GoodsTypeActivity.class);
		intent.putExtra("prevActivityIsMain", true);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_bottom,R.anim.no_anim);
		
		popupMenuWindow.dismiss();
	}
	

	/**
	 * 初始化关注列表
	 */
	private void initLvlvMyFocus(){
		lvMyFocus = (ExpandableListViewSuper)this.findViewById(R.id.lvMyFocus);
		
		//初始化商品类型集合数据
		if(goodsTypes == null || goodsTypes.size() == 0){
			goodsTypes = goodsTypeBusiness.findFocusGoodsTypes();
		}
		
		lvMyFocus.setAdapter(new lvMyFocusAdapter());
		lvMyFocus.setOnGroupExpandListener(this);
		lvMyFocus.setOnChildOperationListener(this);
		
		//默认展开系统一项
		if(goodsTypes != null && goodsTypes.size() > 0)
			lvMyFocus.expandGroup(0);
	}
	
	/**
	 * 切换到商品详细页面
	 */
	@Override
	public void onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition) {
		Intent intent = new Intent(this,GoodsDeailActivity.class);
		intent.putExtra("GoodsItem", goodsTypes.get(groupPosition).getGoodses().get(childPosition));
		intent.putExtra("prevActivityIsMain", true);
		this.startActivity(intent);
	}

	/**
	 * 删除关注
	 */
	@Override
	public void onChildDelete(ExpandableListView parent, View v,int groupPosition, int childPosition) {
		lvMyFocusAdapter adapter = (lvMyFocusAdapter)lvMyFocus.getExpandableListAdapter();
		GoodsType goodsType = goodsTypes.get(groupPosition);
		Goods goods = goodsType.getGoodses().get(childPosition);
		boolean deleteResult = goodsBusiness.deleteFocusGoodsById(goods.getId());
		goods = null;
		if(deleteResult){
			goodsType.getGoodses().remove(childPosition);
			
			//如果分组事没有商品就把该分组删除
			if(goodsType.getGoodses().size() == 0){
				goodsTypes.remove(goodsType);
			}
			adapter.notifyDataSetChanged();
		}else
			Toast.makeText(this, "取消关注失败！", Toast.LENGTH_SHORT).show();
		
	}
	
	/**
	 * 商品类型展开监听器
	 */
	@Override
	public void onGroupExpand(int groupPosition) {
		lvMyFocusAdapter adapter = (lvMyFocusAdapter)lvMyFocus.getExpandableListAdapter();
		
		GoodsType goodsType = goodsTypes.get(groupPosition);
		if(!goodsType.getHasExpand()){
			List<Goods> goods = goodsBusiness.findFocusGoodsByGoodsType(goodsType, new Page<Goods>());
			goodsTypes.get(groupPosition).getGoodses().addAll(goods);
			
			adapter.notifyDataSetChanged();
			goodsType.setHasExpand(true);
		}
	}
	
	/**
	 * 商品列表适配器
	 * @author XING
	 */
	private class lvMyFocusAdapter extends BaseExpandableListAdapter{
				
		@Override
		public int getGroupCount() {
			return goodsTypes.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return goodsTypes.get(groupPosition).getGoodses().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return goodsTypes.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return goodsTypes.get(groupPosition).getGoodses().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.lv_my_focus_group, parent,false);
			}
			TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
			tvName.setText(goodsTypes.get(groupPosition).getName());
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.lv_my_focus_child, parent,false);
			}
			
			//把当前索引传递给视图
			MyFocusGoodsItemLoyout myFocusGoodsItemLoyout = (MyFocusGoodsItemLoyout)convertView;
			myFocusGoodsItemLoyout.setGroupPosition(groupPosition);
			myFocusGoodsItemLoyout.setChildPosition(childPosition);
			myFocusGoodsItemLoyout.resetViewPosition();
			
			Goods goods = goodsTypes.get(groupPosition).getGoodses().get(childPosition);
			
			TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
			tvName.setText(goods.getName());
			
			TextView tvPrePrice = (TextView) convertView.findViewById(R.id.tvPrePrice);
			tvPrePrice.setText("上次价格：" + goods.getPrePrice());
			
			//加载当前价格
			TextView tvCurrentPrice = (TextView) convertView.findViewById(R.id.tvCurrentPrice);
			if(TextUtils.isEmpty(goods.getPriceCache())){
				goodsBusiness.loadGoodsPrice(goods, tvCurrentPrice);
			}else{
				tvCurrentPrice.setText(goods.getPriceCache());
			}
			
			//加载当前图标
			ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
			ImageLoaderUtils.getInstance().displayImage(goods.getIcon(), ivIcon);
			
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}