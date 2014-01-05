package com.zixingchen.discount.utils;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zixingchen.discount.R;

/**
 * 图片加载器
 * @author 陈梓星
 */
public class ImageLoaderUtils {
	private static ImageLoader imageLoader;//图片加载器
	static{
		if(imageLoader == null){
			DisplayImageOptions options = new DisplayImageOptions.Builder()
										        .showImageOnLoading(R.drawable.ic_launcher)
										        .showImageForEmptyUri(R.drawable.ic_launcher)
										        .showImageOnFail(R.drawable.ic_launcher)
										        .cacheInMemory(true)
										        .cacheOnDisc(true)
										        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//图片缩放方式
										        .displayer(new RoundedBitmapDisplayer(5))//图片圆角
										        .build();
			
			File cacheDir = StorageUtils.getCacheDirectory(ContextUtil.getInstance());
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ContextUtil.getInstance())
													.defaultDisplayImageOptions(options)
				 									.discCache(new TotalSizeLimitedDiscCache(cacheDir,10485760))
				 									.discCacheSize(10485760)//内存卡缓存10M图片
//				 									.memoryCacheSize(10485760)//内存缓存10M图片
//				 									.memoryCacheSize(memoryCacheSize)
													.build();
			
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);
		}
	}
	
	public static ImageLoader getInstance() {
        return imageLoader;
    }
}