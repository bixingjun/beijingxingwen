package com.example.beijingnews1.menudatailpager;
/*
 *  包名: com.example.beijingnews1.menudatailpager
 * Created by ASUS on 2017/12/24.
 *  描述: TODO
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.beijingnews1.R;
import com.example.beijingnews1.base.MenuDetaiBasePager;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.domain.PhotosMenuDetailPagerBean;
import com.example.beijingnews1.utils.BitmapCacheUtils;
import com.example.beijingnews1.utils.Constants;
import com.example.beijingnews1.utils.LogUtil;
import com.example.beijingnews1.utils.NetCacheUtils;
import com.example.beijingnews1.utils.SpUtils;
import com.example.beijingnews1.volley.VolleyManager;
import com.google.gson.Gson;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class InteracMenuDetailPager extends MenuDetaiBasePager{
    private  NewsCenterPagerBean2.DetailPagerData detailPagerData;

    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.gridview)
    private GridView gridView;
    private String url;
    private List<PhotosMenuDetailPagerBean.DataEntity.NewsEntity> news;
    private PhotosMenuDetailPagerAdapter adapter;
    private boolean isShowListView = true;
    private BitmapCacheUtils bitmapCacheUtils;


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NetCacheUtils.SUCESS:
                    int position=msg.arg1;
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if(listView.isShown()){
                        ImageView iv_icon = (ImageView)listView.findViewWithTag(position);
                        if(iv_icon!=null&&bitmap!=null){
                            iv_icon.setImageBitmap(bitmap);
                        }

                    }
                    if(gridView.isShown()){
                        ImageView iv_icon = (ImageView)gridView.findViewWithTag(position);
                        if(iv_icon!=null&&bitmap!=null){
                            iv_icon.setImageBitmap(bitmap);
                        }

                    }
                    break;
                case NetCacheUtils.FAIL:
                    position = msg.arg1;
                    LogUtil.e("请求图片失败=="+position);
                    break;

            }

        }
    };



    public InteracMenuDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData detailPagerData) {
        super(context);
        this.detailPagerData=detailPagerData;
        bitmapCacheUtils=new BitmapCacheUtils(handler);

    }

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.photos_menudetail_pager, null);
        x.view().inject(this,view);

        return view;

    }

    @Override
    public void initData() {
        super.initData();

        url = Constants.BASE_URL + detailPagerData.getUrl();
        String saveJson = SpUtils.getString(context, url);
        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }

        getDataFromNetByVolley();
    }

    private void processData(String json) {

        PhotosMenuDetailPagerBean bean=parsedJson(json);
        isShowListView = true;
        news = bean.getData().getNews();

        adapter = new PhotosMenuDetailPagerAdapter();
        listView.setAdapter(adapter);

    }
    public void swichListAndGrid(ImageButton ib_swich_list_grid) {
        if(isShowListView){

            isShowListView = false;
            //显示ListView，隐藏GridView
            listView.setVisibility(View.VISIBLE);
            adapter = new PhotosMenuDetailPagerAdapter();
            listView.setAdapter(adapter);
            gridView.setVisibility(View.GONE);
            //按钮显示--GridView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_grid_type);



        }else{
            isShowListView = true;
            //显示GridView,隐藏ListView
            gridView.setVisibility(View.VISIBLE);
            adapter = new PhotosMenuDetailPagerAdapter();
            gridView.setAdapter(adapter);
            listView.setVisibility(View.GONE);
            //按钮显示--ListView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_list_type);
        }
    }



    class PhotosMenuDetailPagerAdapter extends BaseAdapter {

        
        
        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PhotosMenuDetailPager.ViewHolder viewHolder;

            if(convertView==null){
                convertView= View.inflate(context,R.layout.item_photos_menudetail_pager,null);
                viewHolder=new PhotosMenuDetailPager.ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (PhotosMenuDetailPager.ViewHolder) convertView.getTag();
            }

            PhotosMenuDetailPagerBean.DataEntity.NewsEntity newsEntity = news.get(position);
            viewHolder.tv_title.setText(newsEntity.getTitle());
            String imageUrl = Constants.BASE_URL+newsEntity.getSmallimage();
           // loaderImager(viewHolder, imageUrl );

            viewHolder.iv_icon.setTag(position);

            Bitmap bitmap=bitmapCacheUtils.getBitmap(imageUrl,position);
            if(bitmap!=null){
                viewHolder.iv_icon.setImageBitmap(bitmap);
            }




            return convertView;
        }
    }


    private void loaderImager(final PhotosMenuDetailPager.ViewHolder viewHolder, String imageurl) {

        //设置tag
        viewHolder.iv_icon.setTag(imageurl);
        //直接在这里请求会乱位置
        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer != null) {

                    if (viewHolder.iv_icon != null) {
                        if (imageContainer.getBitmap() != null) {
                            //设置图片
                            viewHolder.iv_icon.setImageBitmap(imageContainer.getBitmap());
                        } else {
                            //设置默认图片
                            viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
                        }
                    }
                }
            }
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //如果出错，则说明都不显示（简单处理），最好准备一张出错图片
                viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
            }
        };
        VolleyManager.getImageLoader().get(imageurl, listener);
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_title;
    }


    private PhotosMenuDetailPagerBean parsedJson(String json) {
        return new Gson().fromJson(json,PhotosMenuDetailPagerBean.class);
    }


    private void getDataFromNetByVolley() {


        StringRequest request=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                SpUtils.putString(context,url,result);
                processData(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String parsed = new String(response.data, "UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return super.parseNetworkResponse(response);
            }
        };

        VolleyManager.getRequestQueue().add(request);
    }
}



