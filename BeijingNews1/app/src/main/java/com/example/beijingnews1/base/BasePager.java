package com.example.beijingnews1.base;
/*
 *  包名: com.example.beijingnews1.base
 * Created by ASUS on 2017/12/23.
 *  描述: TODO
 */

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.beijingnews1.MainActivity;
import com.example.beijingnews1.R;

public class BasePager {

    public final Context context;

    public View rootView;

    public TextView tv_title;

    /**
     * 点击侧滑的
     */
    public ImageButton ib_menu;

    /**
     * 加载各个子页面
     */
    public FrameLayout fl_content;

    public ImageButton ib_swich_list_grid;

    public Button btn_cart;


    public BasePager(Context context) {
        this.context = context;
        rootView=initView();
    }

    private View initView() {
        View view = View.inflate(context, R.layout.base_pager,null);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        ib_menu = (ImageButton) view.findViewById(R.id.ib_menu);
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        ib_swich_list_grid = (ImageButton) view.findViewById(R.id.ib_swich_list_grid);
        btn_cart = (Button) view.findViewById(R.id.btn_cart);

        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainActivity= (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();
            }
        });

        return view;
    }

    public void initData(){

    }
}
