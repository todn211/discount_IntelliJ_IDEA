package com.zixingchen.discount.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.zixingchen.discount.business.GoodsBusiness;
import com.zixingchen.discount.model.Goods;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台更新关注商品的价格
 * Created by 陈梓星
 */
public class GoodsPriceService extends Service {
    private GoodsBusiness goodsBusiness;
    private List<Goods> goodses;//关注的商品集合
    @Override
    public void onCreate() {
        super.onCreate();
        goodsBusiness = new GoodsBusiness(this);
        goodses = new ArrayList<Goods>();
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
                        //两个小时请求一次
                        Thread.sleep(7200000);

                        //加载商品关注列表
                        goodses.clear();
                        goodses.addAll(goodsBusiness.findFocusGoods(null));

                        //从列表中加载每个商品的价格，如果价格有变动就通知用户
                        for (Goods goods : goodses){
                            goodsBusiness.loadGoodsPrice(goods);
                        }
                    } catch (InterruptedException e) {
                        GoodsPriceService.this.stopSelf();
                    }
                }
            }
        }.start();
    }
}
