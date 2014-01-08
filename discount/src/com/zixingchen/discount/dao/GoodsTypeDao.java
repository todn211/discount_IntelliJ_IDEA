package com.zixingchen.discount.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zixingchen.discount.model.Goods;
import com.zixingchen.discount.model.GoodsType;
import com.zixingchen.discount.utils.ContextUtil;

/**
 * 商品类别数据库操作类
 * @author 陈梓星
 */
public class GoodsTypeDao {
	private DBHelp dbHelp;
	
	public GoodsTypeDao() {
		this.dbHelp = new DBHelp(ContextUtil.getInstance(), DBHelp.VERSION);
	}

    public GoodsTypeDao(Context context) {
        this.dbHelp = new DBHelp(context, DBHelp.VERSION);
    }
	
	/**
	 * 查询已关注的商品类别
	 * @return 已关注的商品类别集合
	 */
	public List<GoodsType> findFocusGoodsTypes(){
		List<GoodsType> goodsTypes = new ArrayList<GoodsType>();
		String sql = "select * from goods_type where ID in ("
						+ "select max(goodsType.ID) as goods_type_id from goods_type goodsType join "
						+ "focus_goods focusGoods on goodsType.id = focusGoods.goods_type_id "
						+ "group by focusGoods.id"
						+ ")";
		
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			
			if(cursor.getCount() > 0){
				while(cursor.moveToNext()){
					Long id = cursor.getLong(cursor.getColumnIndex("ID"));
					Long parentId = cursor.getLong(cursor.getColumnIndex("PARENT_ID"));
					String name = cursor.getString(cursor.getColumnIndex("NAME"));
					String typeCode = cursor.getString(cursor.getColumnIndex("TYPE_CODE"));
					String keyWord = cursor.getString(cursor.getColumnIndex("KEY_WORD"));
					String isLeaf = cursor.getString(cursor.getColumnIndex("IS_LEAF"));
					
					GoodsType goodsType = new GoodsType(id,parentId,name,typeCode,keyWord,isLeaf);
					goodsType.setGoodses(new ArrayList<Goods>());
					goodsTypes.add(goodsType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(cursor != null)
				cursor.close();
			
			if(db != null)
				db.close();
		}
		
		return goodsTypes;
	}
	
	/**
	 * 查询商品类别
	 * @param filter 要过滤的条件
	 * @return 商品类别集合
	 */
	public List<GoodsType> findGoodsTypes(GoodsType filter){
		List<GoodsType> goodsTypes = null;
		List<String> args = new ArrayList<String>();
		StringBuilder sql = new StringBuilder("select * from goods_type where 1=1 ");
		if(filter != null){
			if(filter.getId() != null){
				sql.append("and ID=? ");
				args.add(String.valueOf(filter.getId()));
			}
			
			if(filter.getParentId() != null){
				sql.append("and PARENT_ID=? ");
				args.add(String.valueOf(filter.getParentId()));
			}
			
			if(!TextUtils.isEmpty(filter.getName())){
				sql.append("and NAME=? ");
				args.add(filter.getName());
			}
			
			if(!TextUtils.isEmpty(filter.getTypeCode())){
				sql.append("and TYPE_CODE=? ");
				args.add(filter.getTypeCode());
			}
			
			if(!TextUtils.isEmpty(filter.getKeyWord())){
				sql.append("and KEY_WORD=?");
				args.add(filter.getKeyWord());
			}
			
			if(!TextUtils.isEmpty(filter.isLeaf())){
				sql.append("and IS_LEAF=?");
				args.add(filter.isLeaf());
			}
			
			if(!TextUtils.isEmpty(filter.isShow())){
				sql.append("and IS_SHOW=?");
				args.add(filter.isShow());
			}
		}
		
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql.toString(), args.toArray(new String[args.size()]));
			
			if(cursor.getCount() > 0){
				goodsTypes = new ArrayList<GoodsType>(cursor.getCount());
				while(cursor.moveToNext()){
					Long id = cursor.getLong(cursor.getColumnIndex("ID"));
					Long parentId = cursor.getLong(cursor.getColumnIndex("PARENT_ID"));
					String name = cursor.getString(cursor.getColumnIndex("NAME"));
					String typeCode = cursor.getString(cursor.getColumnIndex("TYPE_CODE"));
					String keyWord = cursor.getString(cursor.getColumnIndex("KEY_WORD"));
					String isLeaf = cursor.getString(cursor.getColumnIndex("IS_LEAF"));
					
					GoodsType goodsType = new GoodsType(id,parentId,name,typeCode,keyWord,isLeaf);
					goodsType.setGoodses(new ArrayList<Goods>());
					goodsTypes.add(goodsType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(cursor != null)
				cursor.close();
			
			if(db != null)
				db.close();
		}
		
		return goodsTypes;
	}
}
