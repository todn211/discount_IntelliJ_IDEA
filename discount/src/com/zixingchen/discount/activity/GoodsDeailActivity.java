package com.zixingchen.discount.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zixingchen.discount.R;
import com.zixingchen.discount.business.GoodsBusiness;
import com.zixingchen.discount.common.Contexts;
import com.zixingchen.discount.model.Goods;
import com.zixingchen.discount.model.GoodsType;

/**
 * 商品详细信息展示页面
 * @author 陈梓星
 */
public class GoodsDeailActivity extends Activity {
	
	private WebView wvGoodsDetail;//显示商品详细信息的容器
	private Goods goods;//当前商品对象
	private GoodsType goodsType;//所属商品类型对象
	private boolean prevActivityIsMain;//上一个Activity是否为MainActivity，true时为是。
	private GoodsBusiness bussiness = new GoodsBusiness();
    private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.goods_detail_activity);

		prevActivityIsMain = this.getIntent().getBooleanExtra("prevActivityIsMain", false);

		Intent intent = this.getIntent();

		//初始化商品类型对象
		goodsType = (GoodsType) intent.getSerializableExtra("goodsType");

		//获取商品对象
		goods = (Goods) intent.getSerializableExtra("GoodsItem");

		//初始工具栏
		initToolbar();
		
		//初始化商品内容容器
		initWvGoodsDetail();

        //初始化进度条
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
	
	/**
	 * 初始工具栏
	 */
	private void initToolbar(){
		//初始化标题
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(goods.getName());
		
		//是否显示关注按钮，如果由主页跳转过来的就表示商品已经存在关注中，所以不不显示关注按钮
		if(prevActivityIsMain)
			this.findViewById(R.id.btFocusGoods).setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 初始化商品内容容器
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWvGoodsDetail(){
		wvGoodsDetail = (WebView) this.findViewById(R.id.wvGoodsDetail);
		wvGoodsDetail.getSettings().setJavaScriptEnabled(true);
		
		wvGoodsDetail.setWebViewClient(new WebViewClient(){
			/**
			 * 点击网页中按钮时，在原页面打开
			 */
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

            @Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				Toast.makeText(GoodsDeailActivity.this, "加载页面失败！", Toast.LENGTH_LONG).show();
			}
		});

        wvGoodsDetail.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                //更新进度条的值
                progressDialog.setProgress(newProgress);

                //页面加载完成时关闭进度条
                if(newProgress == 100){
                    progressDialog.dismiss();
                }
            }
        });

		wvGoodsDetail.loadUrl(goods.getHref());
	}
	
	/**
	 * 返回上一级
	 * @param view
	 */
	public void onBtBackClick(View view){
		this.finish();
		this.overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
	}
	
	/**
	 * 关注商品
	 * @param view
	 */
	public void onBtFocusGoodsClick(View view){
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
}