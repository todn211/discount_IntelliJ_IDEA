package com.zixingchen.discount.dao;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.zixingchen.discount.R;
import com.zixingchen.discount.model.GoodsType;
import com.zixingchen.discount.utils.StreamUtil;

public class DBHelp extends SQLiteOpenHelper {
	public static int VERSION = 1;
	
	private Context context;

	public DBHelp(Context context, int version) {
		super(context, "discount.db", null, version);

		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建商品类型表
		this.createGoodsTypeTable(db);
		
		//创建商品关注表
		this.createFocusGoods(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	/**
	 * 创建商品关注表
	 */
	private void createFocusGoods(SQLiteDatabase db){
		try {
			String sql = tableXMLParser(R.xml.table_focus_goods);
			
			db.beginTransaction();
			db.execSQL(sql);

            //插入默认关注的商品
            insertDeafultGoodsFocus(db);

			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			db.endTransaction();
		}
	}

	/**
	 * 创建商品类型表
	 */
	private void createGoodsTypeTable(SQLiteDatabase db) {
		try {
			String sql = tableXMLParser(R.xml.table_goods_type);
			
			db.beginTransaction();
			db.execSQL(sql);
			
			//默认插入一条记录，ID为-1，用于收集用户关注搜索的商品
			ContentValues values = new ContentValues();
			values.put("ID", GoodsType.DEFAULT_ID);
			values.put("NAME", "我的搜索");
			values.put("PARENT_ID", GoodsType.DEFAULT_ID);
			values.put("IS_LEAF", GoodsType.YES);
			values.put("IS_SHOW", GoodsType.NO);
			db.insert("goods_type", null, values);

			//初始化表数据
			InputStream in = context.getAssets().open("goodsTpye.json");
			String goodsTpyeJSONString = StreamUtil.convertStreamToString(in);
			if(!TextUtils.isEmpty(goodsTpyeJSONString)){
				JSONArray goodsTypeJSONArray = new JSONArray(goodsTpyeJSONString);
				//插入一级类型
				for (int i = 0; i < goodsTypeJSONArray.length(); i++) {
					JSONObject goodtypeJSON = goodsTypeJSONArray.optJSONObject(i);
					Long topParentId = insertGoodsType(db,goodtypeJSON,0,GoodsType.NO);
					
					//插入二级类型
					JSONArray secondGoodsTypeJSONArray = goodtypeJSON.getJSONArray("children");
					for (int j = 0; j < secondGoodsTypeJSONArray.length(); j++) {
						goodtypeJSON = secondGoodsTypeJSONArray.optJSONObject(j);
						Long secondParentId = insertGoodsType(db,goodtypeJSON,topParentId,GoodsType.NO);
						
						//插入三级类型
						JSONArray thirdGoodsTypeJSONArray = goodtypeJSON.getJSONArray("children");
						for (int k = 0; k < thirdGoodsTypeJSONArray.length(); k++) {
							goodtypeJSON = thirdGoodsTypeJSONArray.optJSONObject(k);
							insertGoodsType(db,goodtypeJSON,secondParentId,GoodsType.YES);
						}
					}
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

    /**
     * 插入默认关注的商品
     * @param db
     */
    private void insertDeafultGoodsFocus(SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("ID", 26527212560L);
        values.put("GOODS_TYPE_ID",-1);
        values.put("NAME","全不锈钢电热开水桶奶茶桶加热保温热水桶开水器20L-55L");
        values.put("PRICE",150.0f);
        values.put("ICON","http://img01.taobaocdn.com/bao/uploaded/i1/T1DcY6FipgXXXXXXXX_!!0-item_pic.jpg_sum.jpg");
        values.put("HREF","http://item.taobao.com/item.htm?id=26527212560");
        db.insert("focus_goods", null, values);

        ContentValues values2 = new ContentValues();
        values2.put("ID", 36355543415L);
        values2.put("GOODS_TYPE_ID",-1);
        values2.put("NAME","红乐正品 煎蛋羹蒸蛋器煮蛋机小家电厨房电器煮鸡蛋");
        values2.put("PRICE",108.0f);
        values2.put("ICON","http://img04.taobaocdn.com/bao/uploaded/i4/15409043754988469/T1AUD9FnlaXXXXXXXX_!!0-item_pic.jpg_sum.jpg");
        values2.put("HREF","http://item.taobao.com/item.htm?id=36355543415");
        db.insert("focus_goods", null, values2);
    }

	/**
	 * 插入商品类型数据
	 * @param db 数据库对象
	 * @param goodstypeJSON 商品类型JSON对象
	 * @param parentId 当前商品的父ID
	 * @param isLeaf 是否为最后一级
	 * @return 返回主键ID
	 * @throws org.json.JSONException
	 */
	private Long insertGoodsType(SQLiteDatabase db,JSONObject goodstypeJSON,long parentId,String isLeaf) throws Exception{
		ContentValues values = new ContentValues();
		values.put("NAME", goodstypeJSON.getString("name"));
		values.put("PARENT_ID", parentId);
		values.put("IS_LEAF", isLeaf);
		values.put("IS_SHOW", GoodsType.YES);
		
		if(!goodstypeJSON.isNull("cat")){
			values.put("TYPE_CODE", goodstypeJSON.getString("cat"));
		}
		
		if(!goodstypeJSON.isNull("query")){
			values.put("KEY_WORD", goodstypeJSON.getString("query").replace("/", " "));
		}
		
		long rowId = db.insert("goods_type", null, values);
		
		if(rowId == -1)
			throw new SQLException("插入商品类型失败！！");
		
		Cursor cursor = db.rawQuery("select max(ID) from goods_type", null);
		cursor.moveToNext();
		Long id = cursor.getLong(0);
		cursor.close();
		return id;
	}
	
	/**
	 * 解释创建表的XML，把其中的SQL语句解释出来
	 * @param tableXMLRes 要解释的XML资源ID
	 * @return 创建表的SQL语句
	 */
	private String tableXMLParser(int tableXMLRes) throws Exception{
		
		XmlResourceParser parser = context.getResources().getXml(tableXMLRes);
		int eventType = parser.getEventType();
		while(XmlPullParser.END_DOCUMENT != eventType){
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if(parser.getName().equals("table"))
						return parser.nextText();
					break;
			}
			
			eventType = parser.next();
		}
		return null;
	}
}
