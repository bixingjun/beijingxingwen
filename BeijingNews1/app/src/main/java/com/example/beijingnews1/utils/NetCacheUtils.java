package com.example.beijingnews1.utils;
/*
 *  包名: com.example.beijingnews1.utils
 * Created by ASUS on 2018/1/22.
 *  描述: TODO
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NetCacheUtils {

    private Handler handler;


    private final LocalCacheUtils localCacheUtils;
    /**
     * 内存缓存工具类
     */
    private final MemoryCacheUtils memoryCacheUtils;
    private ExecutorService service;
    public static final int SUCESS=1;
    public static final int FAIL = 2;

    public NetCacheUtils(Handler handler,LocalCacheUtils localCacheUtils,MemoryCacheUtils memoryCacheUtils){
        this.handler=handler;
        service= Executors.newFixedThreadPool(10);
        this.localCacheUtils = localCacheUtils;
        this.memoryCacheUtils =memoryCacheUtils;

    }




    public void getBitmapFromNet(String imageUrl,int position) {

        service.execute(new MyRunnable(imageUrl,position));
    }

    class MyRunnable implements Runnable {
        private final String imageUrl;
        private final int position;


        public MyRunnable(String imageUrl, int position) {
        this.imageUrl=imageUrl;
            this.position=position;
        }

        @Override
        public void run() {

            try{
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);
                connection.connect();
                int code=connection.getResponseCode();
                if(code==200){
                    InputStream is=connection.getInputStream();
                    Bitmap bitmap= BitmapFactory.decodeStream(is);

                    Message message=Message.obtain();
                    message.what=SUCESS;
                    message.arg1=position;
                    message.obj=bitmap;
                    handler.sendMessage(message);

                    memoryCacheUtils.putBitmap(imageUrl,bitmap);
                    //在本地中缓存一份
                    localCacheUtils.putBitmap(imageUrl,bitmap);

                }


            } catch (Exception e) {
                e.printStackTrace();
                Message msg = Message.obtain();
                msg.what = FAIL;
                msg.arg1 = position;
                handler.sendMessage(msg);
            }
        }
    }
}
