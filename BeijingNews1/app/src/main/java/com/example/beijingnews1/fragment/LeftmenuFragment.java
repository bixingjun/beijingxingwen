package com.example.beijingnews1.fragment;
/*
 *  包名: com.example.beijingnews1.fragment
 * Created by ASUS on 2017/12/23.
 *  描述: TODO
 */

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.beijingnews1.MainActivity;
import com.example.beijingnews1.R;
import com.example.beijingnews1.base.BaseFragment;
import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.pager.NewsCenterPager;
import com.example.beijingnews1.utils.DensityUtil;

import java.util.List;

public class LeftmenuFragment extends BaseFragment {

    private List<NewsCenterPagerBean2.DetailPagerData> data;
    private ListView listView;
    private int prePosition;
    private LeftmenuFragmentAdapter adapter;

    @Override
    public View initView() {
        listView=new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(context,40),0,0);
        listView.setDividerHeight(0);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setSelector(android.R.color.transparent);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                prePosition=position;
                adapter.notifyDataSetChanged();
                MainActivity mainActivity= (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();

                swichPager(prePosition);
            }
        });


        return listView;
    }

    private void swichPager(int prePosition) {
        MainActivity mainActivity= (MainActivity) context;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
        newsCenterPager.swichPager(prePosition);

    }

    public void setData(List<NewsCenterPagerBean2.DetailPagerData> data) {
        this.data=data;
         adapter = new LeftmenuFragmentAdapter();
        listView.setAdapter(adapter);
    }

    private class LeftmenuFragmentAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu,null);
            textView.setText(data.get(position).getTitle());
            textView.setEnabled(position==prePosition);

            return textView;
        }
    }


}
