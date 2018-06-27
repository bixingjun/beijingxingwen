package com.example.beijingnews1;

import android.app.Application;
import android.content.Context;


import com.example.beijingnews1.volley.VolleyManager;
import com.mob.MobSDK;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;


/**
 * 作者：尚硅谷-杨光福 on 2016/8/15 09:16
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：代表整个软件
 */
public class BeijingNewsApplication extends Application {
    /**
    所有组件被创建之前执行
     */
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.setDebug(true);
        x.Ext.init(this);
        VolleyManager.init(this);

        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush


        // 通过代码注册你的AppKey和AppSecret
        MobSDK.init(this, "23f5219428f90", "37cad207831e1f82fcd850922cc44465");
    }


}
