package com.example.beijingnews1.fragment;
/*
 *  包名: com.example.beijingnews1.fragment
 * Created by ASUS on 2017/12/23.
 *  描述: TODO
 */

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.beijingnews1.MainActivity;
import com.example.beijingnews1.view.NoScrollViewPager;
import com.example.beijingnews1.R;
import com.example.beijingnews1.base.BaseFragment;
import com.example.beijingnews1.base.BasePager;
import com.example.beijingnews1.pager.GovaffairPager;
import com.example.beijingnews1.pager.HomePager;
import com.example.beijingnews1.pager.NewsCenterPager;
import com.example.beijingnews1.pager.SettingPager;
import com.example.beijingnews1.pager.SmartServicePager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.TOUCHMODE_NONE;

public class ContentFragment extends BaseFragment {
    private ArrayList<BasePager> basePagers;

    @ViewInject(R.id.viewpager)
    private NoScrollViewPager viewpager;

    @ViewInject(R.id.rg_main)
    private RadioGroup rg_main;

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.content_fragment, null);

        x.view().inject(ContentFragment.this,view);

        return view;
    }

    @Override
    protected void initData() {
        super.initData();


         basePagers = new ArrayList<>();
         basePagers.add(new HomePager(context));
        basePagers.add(new NewsCenterPager(context));
        basePagers.add(new SmartServicePager(context));
        basePagers.add(new GovaffairPager(context));
        basePagers.add(new SettingPager(context));

        viewpager.setAdapter(new ContentFragmentAdapter());

        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());



        rg_main.check(R.id.rb_home);
        basePagers.get(0).initData();
        isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);

    }

    public NewsCenterPager getNewsCenterPager() {
        return (NewsCenterPager) basePagers.get(1);
    }


    private class ContentFragmentAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return basePagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager basePager = basePagers.get(position);
            View rootView = basePager.rootView;
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_home://主页radioButton的id
                    viewpager.setCurrentItem(0,false);
                    isEnableSlidingMenu(TOUCHMODE_NONE);
                    break;
                case R.id.rb_newscenter://新闻中心radioButton的id
                    viewpager.setCurrentItem(1,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    break;
                case R.id.rb_smartservice://智慧服务radioButton的id
                    viewpager.setCurrentItem(2,false);
                    isEnableSlidingMenu(TOUCHMODE_NONE);
                    break;
                case R.id.rb_govaffair://政要指南的RadioButton的id
                    viewpager.setCurrentItem(3,false);
                    isEnableSlidingMenu(TOUCHMODE_NONE);
                    break;
                case R.id.rb_setting://设置中心RadioButton的id
                    viewpager.setCurrentItem(4,false);
                    isEnableSlidingMenu(TOUCHMODE_NONE);
                    break;
            }
        }
    }

    private void isEnableSlidingMenu(int touchmodeFullscreen){
        MainActivity mainActivity= (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
