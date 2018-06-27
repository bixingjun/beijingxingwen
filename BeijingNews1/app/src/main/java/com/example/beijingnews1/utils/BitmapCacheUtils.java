package com.example.beijingnews1.utils;
/*
 *  包名: com.example.beijingnews1.utils
 * Created by ASUS on 2018/1/22.
 *  描述: TODO
 */

import android.graphics.Bitmap;
import android.os.Handler;

public class BitmapCacheUtils {

    private NetCacheUtils netCacheUtils;

    /**
     * 本地缓存工具类
     */

    private LocalCacheUtils localCacheUtils;

    /**
     内存缓存工具类
     */
    private MemoryCacheUtils memoryCacheUtils;

    public BitmapCacheUtils(Handler handler){
        memoryCacheUtils = new MemoryCacheUtils();
        localCacheUtils = new LocalCacheUtils(memoryCacheUtils);
        netCacheUtils = new NetCacheUtils(handler,localCacheUtils,memoryCacheUtils);
    }


    public Bitmap getBitmap(String imageUrl,int position) {

        if(memoryCacheUtils!=null){
            Bitmap bitmap=memoryCacheUtils.getBitmapFromUrl(imageUrl);
            if(bitmap!=null){
                return bitmap;
            }
        }


        if(localCacheUtils!=null){
            Bitmap bitmap=localCacheUtils.getBitmapFromUrl(imageUrl);
            if(bitmap!=null){
                return bitmap;
            }
        }


        netCacheUtils.getBitmapFromNet(imageUrl,position);
        return null;

    }
}
