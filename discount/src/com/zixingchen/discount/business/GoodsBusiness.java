package com.zixingchen.discount.business;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zixingchen.discount.R;
import com.zixingchen.discount.activity.GoodsListActivity.LvGoodsListAdapter;
import com.zixingchen.discount.common.Page;
import com.zixingchen.discount.dao.GoodsDao;
import com.zixingchen.discount.model.Goods;
import com.zixingchen.discount.model.GoodsType;
import com.zixingchen.discount.utils.ContextUtil;
import com.zixingchen.discount.utils.TaobaoUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品逻辑处理类
 * @author 陈梓星
 */
public class GoodsBusiness {
	/**
	 * 查找商品列表失败
	 */
	public static final int FIND_GOODS_FAILURE = 0;
	
	/**
	 * 查找商品列表成功
	 */
	public static final int FIND_GOODS_SUCCESS = 1;
	
	/**
	 * 初始化商品价格
	 */
	public static final int INIT_PRICE = 2;
	
	/**
	 * 是否加载了所有数据（已经没有下一页）
	 */
	public static final int IS_ALL_DATA = 1;
	
	private GoodsDao goodsDao;
	private static GoodsHandler handler = new GoodsHandler();
	
	
	
	public GoodsBusiness() {
		goodsDao = new GoodsDao();
	}
	
	/**
	 * 添加关注关注商品
	 * @param goods 要关注的商品
	 * @return 添加成功返回true，否则返回false
	 */
	public boolean addFocusGoods(Goods goods){
		List<Goods> goodses = goodsDao.findFocusGoods(new Goods(goods.getId()));
		if(goodses != null && goodses.size() > 0)
			return true;
		return goodsDao.addFocusGoods(goods);
	}
	
	/**
	 * 根据商品ID删除关注的商品
	 * @param id 要删除的关注的商品ID
	 * @return 成功删除时返回true
	 */
	public boolean deleteFocusGoodsById(Long id){
		return goodsDao.deleteFocusGoodsById(id);
	}
	
	/**
	 * 根据商品类型获取相应关注的商品列表
	 * @param goodsType 商品类型
	 * @param page 分页对象
	 * @return 关注的商品列表
	 */
	public List<Goods> findFocusGoodsByGoodsType(final GoodsType goodsType,final Page<Goods> page){
		return goodsDao.findFocusGoodsByGoodsType(goodsType, page);
	}
	
	/**
	 * 根据商品类型获取相应的商品列表
	 * @param goodsType 商品类型
	 * @param page 分页对象
	 * @throws Exception
	 */
	public void loadGoodsByGoodsType(final GoodsType goodsType,final Page<Goods> page,final LvGoodsListAdapter adapter){
		new Thread(){
			public void run() {
				final List<Goods> goodses = new ArrayList<Goods>();
				
				try {
					RequestParams params = createBaseParams();
					params.put("data-value", String.valueOf(page.getStartRecord()));//从哪行开始获取数据
					params.put("pSize", String.valueOf(page.getPageSize()));//每页显示行数
					params.put("cat", goodsType.getTypeCode());
					if(!TextUtils.isEmpty(goodsType.getKeyWord()))
						params.put("q", URLEncoder.encode(goodsType.getKeyWord(), "UTF-8"));
					
					String url = TaobaoUtil.TAOBAO_GOODS_ITEM_LIST_URL + "?" + params.toString();
					
					AsyncHttpClient httpClient = new AsyncHttpClient();
					httpClient.get(url, new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(JSONObject response) {
							try {
								if(!response.isNull("itemList")){
									JSONArray goodsItems = response.getJSONArray("itemList");
									addGoodsToList(goodses, goodsType.getId(), goodsItems);
								}
								
								if(!response.isNull("mallItemList")){
									JSONArray goodsItems = response.getJSONArray("mallItemList");
									addGoodsToList(goodses, goodsType.getId(), goodsItems);
								}
								
								if(!response.isNull("page")){
									JSONObject pageJSON = response.getJSONObject("page");
									if(!pageJSON.isNull("currentPage"))
										page.setPageNumber(pageJSON.getInt("currentPage"));
									
									if(!pageJSON.isNull("totalPage"))
										page.setTotalPage(pageJSON.getInt("totalPage"));
								}
								
								page.setDatas(goodses);
								
								//初始化要传递的参数
								Map<String,Object> params = new HashMap<String,Object>();
								params.put("page", page);
								params.put("adapter", adapter);
								
								Message msg = Message.obtain();
								msg.what = FIND_GOODS_SUCCESS;
								msg.obj = params;
								handler.sendMessage(msg);
							} catch (Exception e) {
								e.printStackTrace();
								Message msg = Message.obtain();
								msg.what = FIND_GOODS_FAILURE;
								handler.sendMessage(msg);
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = FIND_GOODS_FAILURE;
					handler.sendMessage(msg);
				}
			}
		}.start();
	}
	
	/**
	 * 根据商品ID获取商品价格
	 * @param goods 商品对象
	 * @return 商品价格
	 */
	public void loadGoodsPrice(final Goods goods,final TextView textView){
		new Thread(){
			public void run() {
				try {
					AsyncHttpClient httpClient = new AsyncHttpClient();
					httpClient.get(TaobaoUtil.createGoodsItemUrl(goods.getId()), new AsyncHttpResponseHandler(){
						@Override
						public void onSuccess(String response) {
							String str = "<strong class=\"oran\">";
							String priceStr = response.substring(response.indexOf(str)+str.length(), response.indexOf("</strong>"));

							//如果当前价格和数据表的不一至，就把当前价格更新到数据表中，作为下一次是否降价的参考
							Float price = null;
							if(priceStr.contains("-")){
								String[] priceStrs = priceStr.split("-");
								price = Float.parseFloat(priceStrs[0].trim());
							}else{
								price = Float.parseFloat(priceStr.trim());
							}
							
							if(goods.getPrePrice().floatValue() != price.floatValue()){
								Goods newGoods = new Goods(goods.getId());
								newGoods.setCurrentPrice(price);
                                //把当前的值作为下一次的参考值
								goodsDao.updateFocusGoodsPrice(newGoods);

                                //设置当前是商品升降价属性
                                if (goods.getPrePrice().floatValue() < price.floatValue()){
                                    goods.setPriceState(Goods.PriceState.UP);
                                }else if (goods.getPrePrice().floatValue() > price.floatValue()){
                                    goods.setPriceState(Goods.PriceState.DOWN);
                                }
							}else {
                                goods.setPriceState(Goods.PriceState.EQUATION);
                            }

                            goods.setCurrentPrice(price);

                            //发布价格修改消息
                            Map<String,Object> params = new HashMap<String, Object>();
//                            params.put("price", price);
                            params.put("textView", textView);
                            params.put("goods", goods);
                            Message msg = Message.obtain();
                            msg.obj = params;
                            msg.what = INIT_PRICE;
                            handler.sendMessage(msg);
						}
						
						@Override
						public void onFailure(Throwable arg0, String arg1) {
							Message msg = Message.obtain();
							msg.what = GoodsBusiness.FIND_GOODS_FAILURE;
							handler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	/**
	 * 创建参数对象
	 * @return 参数对象
	 */
	private RequestParams createBaseParams(){
		RequestParams params = new RequestParams();
		params.put("_input_charset", "UTF-8");
		params.put("style", "list");
		params.put("json", "on");
		params.put("module", "page");
		params.put("data-key", "s");
		return params;
	}
	
	/**
	 * 把JSON形式的商品对象转化成LIST
	 * @param goodses 要添加商品的集合
	 * @param goodsItems 商品集合的JSON对象
	 * @throws org.json.JSONException
	 */
	private void addGoodsToList(final List<Goods> goodses, Long goodsTypeId,JSONArray goodsItems) throws JSONException {
		for (int i=0; i<goodsItems.length();i++) {
			Goods goods = new Goods();
			JSONObject goodsItem = goodsItems.optJSONObject(i);
			goods.setId(Long.parseLong(goodsItem.getString("itemId")));
			goods.setGoodsTypeId(goodsTypeId);
			goods.setName(goodsItem.getString("tip"));
			goods.setCurrentPrice(Float.parseFloat(goodsItem.getString("currentPrice")));
			goods.setPrePrice(goods.getCurrentPrice());
			goods.setIcon(goodsItem.getString("image") + "_sum.jpg");
			goods.setHref(goodsItem.getString("href"));
			goodses.add(goods);
		}
	}
	
	/**
	 * 消息处理器
	 * @author 陈梓星
	 */
	@SuppressLint("HandlerLeak")
	private static class GoodsHandler extends Handler{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				//初始化商品当前价格
				case GoodsBusiness.INIT_PRICE :
					Map<String,Object> priceParams = (Map<String, Object>) msg.obj;
					Goods goods = (Goods) priceParams.get("goods");
					TextView textView = (TextView) priceParams.get("textView");
					String price = "当前价格：" + goods.getCurrentPrice().toString();
					textView.setText(price);

                    //设置价格图标，是升还是降
                    Drawable drawable = null;
                    if (goods.getPriceState() == Goods.PriceState.DOWN){
                        drawable = ContextUtil.getInstance().getResources().getDrawable(R.drawable.mark_down_icon);
                        textView.setCompoundDrawablesWithIntrinsicBounds(null,null,drawable,null);
                    }else if (goods.getPriceState() == Goods.PriceState.UP){
                        drawable = ContextUtil.getInstance().getResources().getDrawable(R.drawable.mark_up_icon);
                        textView.setCompoundDrawablesWithIntrinsicBounds(null,null,drawable,null);
                    }
                    goods.setPriceStateIcon(drawable);
					break;
				
				//添加商品列表到前台展示
				case GoodsBusiness.FIND_GOODS_SUCCESS:
					Map<String,Object> params = (Map<String, Object>) msg.obj;
					Page<Goods> page = (Page<Goods>)params.get("page");
					LvGoodsListAdapter adapter = (LvGoodsListAdapter) params.get("adapter");
					List<Goods> newDatas = page.getDatas();
					if(newDatas != null && newDatas.size()>0){
						adapter.getDatas().addAll(newDatas);
						adapter.setPage(page.clonePageNotDatas());
						adapter.notifyDataSetChanged();
					}
					break;
				
				//加载商品列表失败
				case GoodsBusiness.FIND_GOODS_FAILURE:
					Toast.makeText(ContextUtil.getInstance(), "加载商品列表失败！", Toast.LENGTH_LONG).show();
					break;
			}
			
		}
	}
}
