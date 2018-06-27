package com.example.beijingnews1.menudatailpager;
/*
 *  包名: com.example.beijingnews1.menudatailpager
 * Created by ASUS on 2017/12/24.
 *  描述: TODO
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.beijingnews1.MainActivity;
import com.example.beijingnews1.R;
import com.example.beijingnews1.base.MenuDetaiBasePager;
import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.menudatailpager.tabdetailpager.TabDetailPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class NewsMenuDetailPager extends MenuDetaiBasePager{


    private final List<NewsCenterPagerBean2.DetailPagerData.ChildrenData> children;

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;
    @ViewInject(R.id.tabPageIndicator)
    private TabPageIndicator tabPageIndicator;

    @ViewInject(R.id.ib_tab_next)
    private ImageButton ib_tab_next;

    private ArrayList<TabDetailPager> tabDetailPager;

    public NewsMenuDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData dataBean) {
        super(context);
         children = dataBean.getChildren();
    }

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.newsmenu_detail_pager, null);
        x.view().inject(this,view);

        ib_tab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        });

        return view;

    }

    @Override
    public void initData() {
        super.initData();

        tabDetailPager=new ArrayList<>();
        for (int i=0;i<children.size();i++){
            tabDetailPager.add(new TabDetailPager(context,children.get(i)));
        }

        viewPager.setAdapter(new MyNewsMenuDetailPagerAdapter());

        tabPageIndicator.setViewPager(viewPager);

        tabPageIndicator.setOnPageChangeListener(new MyOnPageChangeListener());
    }



    private class MyNewsMenuDetailPagerAdapter extends PagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
            return children.get(position).getTitle();
        }

        @Override
        public int getCount() {
            return tabDetailPager.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager tabDetailPager = NewsMenuDetailPager.this.tabDetailPager.get(position);
            View rootView = tabDetailPager.rootView;
            tabDetailPager.initData();
            container.addView(rootView);


            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
                if(position==0){
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else {
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void isEnableSlidingMenu(int touchmodeFullscreen){
        MainActivity mainActivity= (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }
}
