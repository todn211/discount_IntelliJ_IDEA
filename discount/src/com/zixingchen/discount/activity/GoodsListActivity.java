package com.zixingchen.discount.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zixingchen.discount.R;
import com.zixingchen.discount.business.GoodsBusiness;
import com.zixingchen.discount.common.Contexts;
import com.zixingchen.discount.common.Page;
import com.zixingchen.discount.model.Goods;
import com.zixingchen.discount.model.GoodsType;
import com.zixingchen.discount.utils.ImageLoaderUtils;

/**
 * 商品列表页面
 * @author 陈梓星
 */
public class GoodsListActivity extends Activity implements OnItemClickListener{
	
	private GoodsType goodsType;//所属商品类型对象
	private ListView lvGoodsList;//商品列表
	private LvGoodsListAdapter adapter;//商品列表数据适配器
    private Dialog progressDialog;//进度条窗口
	private GoodsBusiness bussiness = new GoodsBusiness();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_list_activity);
		
		Intent intent = this.getIntent();

        popProgreesDialog();
		
		//初始化商品类型对象
		goodsType = (GoodsType) intent.getSerializableExtra("goodsType");
		
		//初始化商品列表
		initLvGoodsItem();
		
		//初始化标题
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(goodsType.getName());
	}

    /**
     * 弹出进度条窗口
     */
    private void popProgreesDialog(){
        //进度条调试
        progressDialog =  new Dialog(this,R.style.progress_dialog);
        View view = this.getLayoutInflater().inflate(R.layout.progressbar_dialog,null);// 得到加载view
        View ivLoading = view.findViewById(R.id.ivLoading);
        ivLoading.setAnimation(AnimationUtils.loadAnimation(this, R.anim.loading_animation));
        progressDialog.setContentView(view,new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        progressDialog.show();
    }
	
	/**
	 * 初始化商品列表
	 */
	private void initLvGoodsItem(){
		lvGoodsList = (ListView) this.findViewById(R.id.lvGoodsList);
		lvGoodsList.setOnItemClickListener(this);
		adapter = new LvGoodsListAdapter(null);
		lvGoodsList.setAdapter(adapter);
		
		//远程加载商品列表
		bussiness.loadGoodsByGoodsType(goodsType, new Page<Goods>(),adapter,progressDialog);
	}
	
	/**
	 * 关注商品
	 */
	public void focusGoods(View view){
		int location = ((Integer) view.getTag()).intValue();
		Goods goods = adapter.getDatas().get(location);
		boolean addResult = bussiness.addFocusGoods(goods);
		if(addResult){
			Toast.makeText(this, "关注商品成功！", Toast.LENGTH_SHORT).show();
			
			//通知主页我的关注列表数据有更改
			SharedPreferences sp = this.getSharedPreferences(Contexts.SYSTEM_CACHE, MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean(Contexts.HAS_ADD_FOCUS_GOODS,true);
			editor.commit();
			
			//把当前新关注的商品添加到主页关注列表中
			goodsType.getGoodses().add(goods);
			if(!MainActivity.readyGoodsTypes.contains(goodsType)){
				MainActivity.readyGoodsTypes.add(goodsType);
				goodsType.setHasExpand(true);
			}
		}else{
			Toast.makeText(this, "关注商品失败！", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 切换到商品详细页面
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this,GoodsDeailActivity.class);
		intent.putExtra("GoodsItem", adapter.getDatas().get(position));
		intent.putExtra("goodsType", goodsType);
		this.startActivity(intent);
	}
	
	/**
	 * 返回物理键点击监听器
	 */
	@Override
	public void onBackPressed() {
		back(null);
	}
	
	/**
	 * 返回按键事件监听
	 * @param view
	 */
	public void back(View view){
		this.finish();
		//执行页面切换效果
		doTransitionAnimation(false);
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
	 * 商品项列表适配器
	 * @author 陈梓星
	 */
	public class LvGoodsListAdapter extends BaseAdapter{
		private Page<Goods> page;
		private List<Goods> datas;//商品集合
		
		public LvGoodsListAdapter(List<Goods> datas) {
			if(datas == null)
				this.datas = new ArrayList<Goods>();
			else
				this.datas = datas;
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = GoodsListActivity.this.getLayoutInflater().inflate(R.layout.lv_goods_list_item, parent,false);
			
			ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
			TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
			TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
			
			Goods goods = datas.get(position);
			tvName.setText(goods.getName());
			tvPrice.setText("￥" + goods.getCurrentPrice());
			ImageLoaderUtils.getInstance().displayImage(goods.getIcon(), ivIcon);
			
			//把当前商品集合元素的下标增值给按钮tag，以便点击的时候获取
			Button btFocusGoods = (Button) convertView.findViewById(R.id.btFocusGoods);
			btFocusGoods.setTag(Integer.valueOf(position));
			
			//判断当前下标是否到达倒数第三个，且判断当前页是不是最后一页，如果不是最后一页就加载下一页的数据
			if(position == datas.size()-3 && !page.isLastPage()){
				page.setPageNumber(page.getPageNumber() + 1);
				bussiness.loadGoodsByGoodsType(goodsType, page,adapter,null);
			}
			return convertView;
		}
		
		public void setPage(Page<Goods> page){
			this.page = page;
		}

		public List<Goods> getDatas() {
			return datas;
		}

		public void setDatas(List<Goods> datas) {
			this.datas = datas;
		}
	}
}
