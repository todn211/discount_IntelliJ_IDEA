package com.zixingchen.discount.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zixingchen.discount.R;

/**
 * Created by XING on 14-1-7.
 */
public class HelpActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
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