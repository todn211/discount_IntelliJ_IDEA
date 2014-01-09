package com.zixingchen.discount.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.CheckBox;

import com.zixingchen.discount.R;
import com.zixingchen.discount.common.Contexts;
import com.zixingchen.discount.service.GoodsPriceService;

/**
 * 软件设置页面
 * @author 陈梓星
 */
public class SettingsActivity extends Activity {
    private static final String IS_PUSH_INFO = "isPushInfo";
    private CheckBox cbPushInfo;//是否开启降价通知

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings_activity);

        cbPushInfo = (CheckBox) this.findViewById(R.id.cbPushInfo);
        SharedPreferences sharedPreferences = this.getSharedPreferences(Contexts.SYSTEM_CACHE, MODE_PRIVATE);
        cbPushInfo.setChecked(sharedPreferences.getBoolean(IS_PUSH_INFO,false));
	}

    /**
     * 返回上一个页面
     * @param view
     */
	public void onBtBackClick(View view){
        //把是否开启降价通知的状态写入到缓存中
        SharedPreferences sharedPreferences = this.getSharedPreferences(Contexts.SYSTEM_CACHE, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(IS_PUSH_INFO,cbPushInfo.isChecked()).commit();

        //根据开启状态开启或者关注后台商品价格通知服务
        Intent intent = new Intent(this,GoodsPriceService.class);
        if (cbPushInfo.isChecked()){
            this.startService(intent);
        }else{
            this.stopService(intent);
        }

		this.finish();
	}

    /**
     * 显示关于页面
     * @param view
     */
	public void showAboutActivity(View view){
		this.startActivity(new Intent(this,AboutActivity.class));
        this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

    /**
     * 显示联系我们页面
     * @param view
     */
    public void showContactUsActivity(View view){
        this.startActivity(new Intent(this,ContactUsActivity.class));
        this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}