package com.zixingchen.discount.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * 后台更新关注商品的价格
 * Created by 陈梓星
 */
public class GoodsPriceService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        this.updateFocusGoodsPrice();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return new GoodsPriceIBinder();
    }

    private class GoodsPriceIBinder extends Binder {
        public GoodsPriceService getService(){
            return GoodsPriceService.this;
        }
    }

    /**
     * 更新关注商品价格
     */
    private void updateFocusGoodsPrice(){
        new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        GoodsPriceService.this.stopSelf();
                    }
                }
            }
        }.start();
    }
}
