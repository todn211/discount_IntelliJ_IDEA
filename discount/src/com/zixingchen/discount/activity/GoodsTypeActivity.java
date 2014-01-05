package com.zixingchen.discount.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zixingchen.discount.R;
import com.zixingchen.discount.business.GoodsTypeBusiness;
import com.zixingchen.discount.model.GoodsType;
import com.zixingchen.discount.utils.TaobaoUtil;

/**
 * 选择关注商品类
 * @author 陈梓星
 */
public class GoodsTypeActivity extends Activity implements OnItemClickListener{

	private ListView lvGoodsType;//商品选择列表，选择要关注的商品
	private List<GoodsType> goodsTypes;//商品类型（或者商品）集合
	private boolean prevActivityIsMain;//上一个Activity是否为MainActivity，true时为是。
	private TextView tvTitle;//窗口标题
	private Button btLeft;//工具栏左边按钮
	private GoodsType parentGoodsType;//父级对象
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.goods_type_activity);
		
		prevActivityIsMain = this.getIntent().getBooleanExtra("prevActivityIsMain", false);
		
		//初始工具栏
		initToolbar();
		
		//初始化商品类型集合数据
		initLvGoodsType();
	}
	
	/**
	 * 初始工具栏
	 */
	private void initToolbar(){
		//初始化标题
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		Intent intent = this.getIntent();
		parentGoodsType = (GoodsType) intent.getSerializableExtra("goodsType");
		String title = parentGoodsType == null ? "选择关注" : parentGoodsType.getName();
		tvTitle.setText(title);
		
		//初始化按钮
		btLeft = (Button) this.findViewById(R.id.btLeft);
		if(prevActivityIsMain){
			btLeft.setVisibility(View.INVISIBLE);
		}else{
//			btLeft.setText(this.getResources().getString(R.string.back));
			btLeft.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 初始化商品类型集合数据
	 */
	private void initLvGoodsType(){
		lvGoodsType = (ListView) this.findViewById(R.id.lvGoodsType);
		lvGoodsType.setOnItemClickListener(GoodsTypeActivity.this);
		
		if(goodsTypes == null || goodsTypes.size() == 0){
			new Thread(){
				public void run() {
					try {
						//获取商品类型集合
						GoodsTypeBusiness goodsTypeBusiness = new GoodsTypeBusiness();
						if(parentGoodsType == null)
							goodsTypes = goodsTypeBusiness.findGoodsTypesByParentId(0L);
						else
							goodsTypes = goodsTypeBusiness.findGoodsTypesByParentId(parentGoodsType.getId());
						
						if(goodsTypes != null && goodsTypes.size() > 0){
							GoodsTypeActivity.this.runOnUiThread(new Thread(){
								public void run() {
									lvGoodsType.setAdapter(new LvGoodsTypeSelectedAdapater());
								};
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}
	
	/**
	 * 商品类型点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		GoodsType goodsType = goodsTypes.get(position);
		Intent intent = null;
		//如果商品类型是最后一级，就切换到商品页面，否则依然在商品类型页面切换
		if(GoodsType.YES.equals(goodsType.isLeaf())){
			intent = new Intent(this,GoodsListActivity.class);
		}else{
			intent = new Intent(this,GoodsTypeActivity.class);
		}
		
		intent.putExtra("goodsType", goodsType);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	
	
	/**
	 * 返回物理键点击监听器
	 */
	@Override
	public void onBackPressed() {
		close(null);
	}
	
	/**
	 * 关闭按键事件监听
	 * @param view
	 */
	public void close(View view){
		if(prevActivityIsMain)
			this.setResult(RESULT_OK);
		
		this.finish();
		
		//执行页面切换效果
		doTransitionAnimation(prevActivityIsMain);
	}
	
	/**
	 * 跳转到主页
	 * @param view
	 */
	public void goToHome(View view){
		Intent intent = new Intent(this,MainActivity.class);
		this.startActivity(intent);
		this.finish();
		
		//执行页面切换效果
		doTransitionAnimation(true);
	}
	
	/**
	 * 执行页面切换效果
	 */
	private void doTransitionAnimation(boolean prevActivityIsMain){
		if(prevActivityIsMain)
			this.overridePendingTransition(0,R.anim.out_from_top);
		else
			this.overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
	}
	
	/**
	 * 商品类型列表适配器
	 * @author 陈梓星
	 */
	private class LvGoodsTypeSelectedAdapater extends BaseAdapter{

		@Override
		public int getCount() {
			return goodsTypes.size();
		}

		@Override
		public Object getItem(int i) {
			return goodsTypes.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewgroup) {
			if(view == null)
				view = GoodsTypeActivity.this
											.getLayoutInflater()
											.inflate(R.layout.lv_goods_type_item, viewgroup, false);
			
			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			tvName.setText(goodsTypes.get(i).getName());
			return view;
		}
	}
}