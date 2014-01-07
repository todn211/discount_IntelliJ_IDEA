package com.zixingchen.discount.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zixingchen.discount.R;

/**
 * 软件设置页面
 * @author 陈梓星
 */
public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about_activity);
	}

    /**
     * 返回设置页面
     * @param view
     */
	public void onBtBackClick(View view){
		this.finish();
		//执行页面切换效果
		this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
}