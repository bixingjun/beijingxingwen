package com.example.beijingnews1.base;
/*
 *  包名: com.example.beijingnews1.base
 * Created by ASUS on 2017/12/24.
 *  描述: TODO
 */

import android.content.Context;
import android.view.View;

public abstract class MenuDetaiBasePager {

    public final Context context;
    public View rootView;


    public MenuDetaiBasePager(Context context) {
        this.context = context;
        rootView=initView();
    }

    public abstract View initView();

    public void initData(){

    }
}
