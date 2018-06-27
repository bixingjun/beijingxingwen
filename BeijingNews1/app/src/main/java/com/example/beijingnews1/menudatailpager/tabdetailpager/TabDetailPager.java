package com.example.beijingnews1.menudatailpager.tabdetailpager;
/*
 *  包名: com.example.beijingnews1.menudatailpager.tabdetailpager
 * Created by ASUS on 2017/12/25.
 *  描述: TODO
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beijingnews1.R;
import com.example.beijingnews1.activity.NewsDetailActivity;
import com.example.beijingnews1.base.MenuDetaiBasePager;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.domain.TabDetailPagerBean;
import com.example.beijingnews1.utils.Constants;
import com.example.beijingnews1.utils.SpUtils;
import com.example.beijingnews1.view.HorizontalScrollViewPager;
import com.example.refreshlistview.RefreshListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TabDetailPager extends MenuDetaiBasePager {
    public static final String READ_ARRAY_ID = "read - array_id";
    private final NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData;
    private final ImageOptions imageOptions;
    private String url;
    private List<TabDetailPagerBean.DataEntity.TopnewsData> topnews;

    private HorizontalScrollViewPager viewpager;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    private RefreshListView listview;
    private int prePosition;
    private List<TabDetailPagerBean.DataEntity.NewsData> news;

    private String moreUrl;
    private boolean isLoadMore=false;
    private TabDetailPagerListAdapter adapter;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    int item=(viewpager.getCurrentItem()+1)%topnews.size();
                    viewpager.setCurrentItem(item);
                    handler.sendEmptyMessageDelayed(0,4000);
                    break;
                case 1:
                    listview.onRefreshFinish(false);
                    break;
                case 2:
                    String result= (String) msg.obj;
                    Log.i("aaaaa",result);

                    SpUtils.putString(context,url,result);
                    processData(result);
                    listview.onRefreshFinish(true);
                    break;


            }
        }
    };
    private OkHttpClient mOkHttpClient;


    public TabDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData) {
        super(context);
        this.childrenData=childrenData;

        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.e
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.news_pic_default)
                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();

    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        listview=(RefreshListView)view.findViewById(R.id.listview);
        View topNewsView = View.inflate(context, R.layout.topnews, null);
        viewpager=(HorizontalScrollViewPager)topNewsView.findViewById(R.id.viewpager);
        tv_title=(TextView)topNewsView.findViewById(R.id.tv_title);
        ll_point_group=(LinearLayout)topNewsView.findViewById(R.id.ll_point_group);

        listview.addTopNewsView(topNewsView);

        listview.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                getDataFromNet();
            }

            @Override
            public void onLoadMore() {
                if(TextUtils.isEmpty(moreUrl)){
                    listview.onRefreshFinish(false);
                }else {
                    getMoreDateFromNet();
                }


            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int realPosition=position-1;
                TabDetailPagerBean.DataEntity.NewsData newsData = news.get(realPosition);
                String idArray = SpUtils.getString(context, READ_ARRAY_ID);
                if(!idArray.contains(newsData.getId()+"")){
                    SpUtils.putString(context,READ_ARRAY_ID,idArray+newsData.getId()+",");
                    adapter.notifyDataSetChanged();

                }

                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("url",Constants.BASE_URL+newsData.getUrl());

                context.startActivity(intent);

            }
        });




        return view;


    }

    private void getMoreDateFromNet() {
        RequestParams params=new RequestParams(moreUrl);
       /* params.setConnectTimeout(4000);*/
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                isLoadMore=true;
                processData(result);
                listview.onRefreshFinish(false);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listview.onRefreshFinish(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    @Override
    public void initData() {
        super.initData();
         url = Constants.BASE_URL + childrenData.getUrl();
       //getDataFromNet();
        okhttp3getData();
    }

    private void okhttp3getData() {
        prePosition=0;
            mOkHttpClient=new OkHttpClient();
            Request.Builder requestBuilder = new Request.Builder().url(url);
            //可以省略，默认是GET请求
            requestBuilder.method("GET",null);
            Request request = requestBuilder.build();
            Call mcall= mOkHttpClient.newCall(request);
          /*  mcall.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(1);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Message message=Message.obtain();
                    message.what=2;
                    message.obj=response.body().string();
                   handler.sendMessage(message);

                }
            });*/
        try {
            mcall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void getDataFromNet() {
        //解决BUG
        prePosition=0;

        RequestParams params=new RequestParams(url);
      /*  params.setConnectTimeout(4000);*/

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("aaaaa",result);
                SpUtils.putString(context,url,result);
                processData(result);
                listview.onRefreshFinish(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listview.onRefreshFinish(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processData(String json) {

        TabDetailPagerBean bean = parseJson(json);


        if(TextUtils.isEmpty(bean.getData().getMore())){
            moreUrl="";
        }else {
            moreUrl=Constants.BASE_URL+bean.getData().getMore();
        }
        if(!isLoadMore){
            topnews = bean.getData().getTopnews();
            viewpager.setAdapter(new TabDetailPagerTopNewsAdapter());

            addPoint();


            viewpager.addOnPageChangeListener(new MyOnPageChangeListener());

            tv_title.setText(topnews.get(0).getTitle());

            news = bean.getData().getNews();
             adapter = new TabDetailPagerListAdapter();
            listview.setAdapter(adapter);
        }else {
            isLoadMore=false;
            List<TabDetailPagerBean.DataEntity.NewsData> morenews = bean.getData().getNews();
            news.addAll(morenews);
            adapter.notifyDataSetChanged();
        }
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(0,4000);


    }

    private void addPoint() {
        ll_point_group.removeAllViews();

        for(int i=0;i<topnews.size();i++){
               ImageView imageView=new ImageView(context);
               imageView.setBackgroundResource(R.drawable.point_selector);


            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(DensityUtil.dip2px(8),DensityUtil.dip2px(8));

            if(i==0){
                imageView.setEnabled(true);
            }else {
                imageView.setEnabled(false);
                params.leftMargin = DensityUtil.dip2px(5);
            }
            imageView.setLayoutParams(params);
               ll_point_group.addView(imageView);
        }
    }


    class TabDetailPagerTopNewsAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView=new ImageView(context);
            imageView.setBackgroundResource(R.drawable.home_scroll_default);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);

            TabDetailPagerBean.DataEntity.TopnewsData topnewsData = topnews.get(position);
            String imageUrl = Constants.BASE_URL+topnewsData.getTopimage();
            x.image().bind(imageView,imageUrl);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        handler.removeCallbacksAndMessages(null);

                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacksAndMessages(null);
                        handler.sendEmptyMessageDelayed(0,4000);

                        break;
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacksAndMessages(null);
                        handler.sendEmptyMessageDelayed(0,4000);

                        break;
                }

                    return true;
                }

            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView((View) object);
        }
    }


    private TabDetailPagerBean parseJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }


    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
                tv_title.setText(topnews.get(position).getTitle());
                ll_point_group.getChildAt(prePosition).setEnabled(false);

                ll_point_group.getChildAt(position).setEnabled(true);
            prePosition=position;
        }

        private boolean isDragging=false;
        @Override
        public void onPageScrollStateChanged(int state) {

            if(state==ViewPager.SCROLL_STATE_DRAGGING){
                isDragging=true;
                handler.removeCallbacksAndMessages(null);
            }else if(state==ViewPager.SCROLL_STATE_SETTLING&&isDragging){
                isDragging=false;

                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(0,4000);
            }else if(state==ViewPager.SCROLL_STATE_IDLE&&isDragging){
                isDragging=false;

                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(0,4000);
            }
        }
    }

     class TabDetailPagerListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return news.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView= View.inflate(context,R.layout.item_tabdetail_pager,null);
                viewHolder=new ViewHolder();
                viewHolder.iv_icon=(ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title=(TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);

                convertView.setTag(viewHolder);
            }else {
                 viewHolder = (ViewHolder) convertView.getTag();
            }
            TabDetailPagerBean.DataEntity.NewsData newsData = news.get(position);
            String imageUrl = Constants.BASE_URL + newsData.getListimage();
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.news_pic_default)
                    .error(R.drawable.news_pic_default)
                    .into(viewHolder.iv_icon);

            viewHolder.tv_title.setText(newsData.getTitle());
            viewHolder.tv_time.setText(newsData.getPubdate());

            String idArray=SpUtils.getString(context,READ_ARRAY_ID);
            if(idArray.contains(newsData.getId()+"")){
                viewHolder.tv_title.setTextColor(Color.GRAY);
            }else {
                viewHolder.tv_title.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }
}
