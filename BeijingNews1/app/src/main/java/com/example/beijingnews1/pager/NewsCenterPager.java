package com.example.beijingnews1.pager;
/*
 *  包名: com.example.beijingnews1
 * Created by ASUS on 2017/12/23.
 *  描述: TODO
 */

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.beijingnews1.MainActivity;
import com.example.beijingnews1.base.BasePager;
import com.example.beijingnews1.base.MenuDetaiBasePager;
import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.fragment.LeftmenuFragment;
import com.example.beijingnews1.menudatailpager.InteracMenuDetailPager;
import com.example.beijingnews1.menudatailpager.NewsMenuDetailPager;
import com.example.beijingnews1.menudatailpager.PhotosMenuDetailPager;
import com.example.beijingnews1.menudatailpager.TopicMenuDetailPager;
import com.example.beijingnews1.utils.Constants;
import com.example.beijingnews1.utils.LogUtil;
import com.example.beijingnews1.utils.SpUtils;
import com.example.beijingnews1.volley.VolleyManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NewsCenterPager extends BasePager {
    private List<NewsCenterPagerBean2.DetailPagerData> data;
    private ArrayList<MenuDetaiBasePager> detaiBasePagers;

    public NewsCenterPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("主页面数据被初始化了..");
        //1.设置标题
        tv_title.setText("主页面");
        //2.联网请求，得到数据，创建视图
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        //3.把子视图添加到BasePager的FrameLayout中
        fl_content.addView(textView);
        //4.绑定数据
        textView.setText("主页面内容");

        String saveJson = SpUtils.getString(context,Constants.NEWSCENTER_PAGER_URL);//""

        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }

        //getDataFromNet();
        getDataFromNetByVolley();
    }

    private void getDataFromNetByVolley() {


        StringRequest request=new StringRequest(Request.Method.GET, Constants.NEWSCENTER_PAGER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                SpUtils.putString(context,Constants.NEWSCENTER_PAGER_URL,result);
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

    private void getDataFromNet() {
        RequestParams params=new RequestParams(Constants.NEWSCENTER_PAGER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                SpUtils.putString(context,Constants.NEWSCENTER_PAGER_URL,result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

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
        NewsCenterPagerBean2 bean = parsedJson2(json);
         data = bean.getData();
        MainActivity mainActivity= (MainActivity) context;
        LeftmenuFragment leftmenuFragment = mainActivity.getLeftmenuFragment();



        //添加详情页面
        detaiBasePagers = new ArrayList<>();
        detaiBasePagers.add(new NewsMenuDetailPager(context,data.get(0)));//新闻详情页面
        detaiBasePagers.add(new TopicMenuDetailPager(context,data.get(0)));//专题详情页面
        detaiBasePagers.add(new PhotosMenuDetailPager(context,data.get(2)));//图组详情页面
        detaiBasePagers.add(new InteracMenuDetailPager(context,data.get(2)));//互动详情页面
        leftmenuFragment.setData(data);
    }

    private NewsCenterPagerBean2 parsedJson2(String json) {
        NewsCenterPagerBean2 bean2 = new NewsCenterPagerBean2();
        try {
            JSONObject object = new JSONObject(json);


            int retcode = object.optInt("retcode");
            bean2.setRetcode(retcode);//retcode字段解析成功

            JSONArray data = object.optJSONArray("data");
            if (data != null && data.length() > 0) {

                List<NewsCenterPagerBean2.DetailPagerData> detailPagerDatas = new ArrayList<>();
                //设置列表数据
                bean2.setData(detailPagerDatas);
                //for循环，解析每条数据
                for (int i = 0; i < data.length(); i++) {

                    JSONObject jsonObject = (JSONObject) data.get(i);

                    NewsCenterPagerBean2.DetailPagerData detailPagerData = new NewsCenterPagerBean2.DetailPagerData();
                    //添加到集合中
                    detailPagerDatas.add(detailPagerData);

                    int id = jsonObject.optInt("id");
                    detailPagerData.setId(id);
                    int type = jsonObject.optInt("type");
                    detailPagerData.setType(type);
                    String title = jsonObject.optString("title");
                    detailPagerData.setTitle(title);
                    String url = jsonObject.optString("url");
                    detailPagerData.setUrl(url);
                    String url1 = jsonObject.optString("url1");
                    detailPagerData.setUrl1(url1);
                    String dayurl = jsonObject.optString("dayurl");
                    detailPagerData.setDayurl(dayurl);
                    String excurl = jsonObject.optString("excurl");
                    detailPagerData.setExcurl(excurl);
                    String weekurl = jsonObject.optString("weekurl");
                    detailPagerData.setWeekurl(weekurl);


                    JSONArray children = jsonObject.optJSONArray("children");
                    if (children != null && children.length() > 0) {

                        List<NewsCenterPagerBean2.DetailPagerData.ChildrenData> childrenDatas  = new ArrayList<>();

                        //设置集合-ChildrenData
                        detailPagerData.setChildren(childrenDatas);

                        for (int j = 0; j < children.length(); j++) {
                            JSONObject childrenitem = (JSONObject) children.get(j);

                            NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData = new NewsCenterPagerBean2.DetailPagerData.ChildrenData();
                            //添加到集合中
                            childrenDatas.add(childrenData);


                            int childId = childrenitem.optInt("id");
                            childrenData.setId(childId);
                            String childTitle = childrenitem.optString("title");
                            childrenData.setTitle(childTitle);
                            String childUrl = childrenitem.optString("url");
                            childrenData.setUrl(childUrl);
                            int childType = childrenitem.optInt("type");
                            childrenData.setType(childType);

                        }

                    }


                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return bean2;
    }


    private NewsCenterPagerBean parsedJson(String json) {


        return new Gson().fromJson(json,NewsCenterPagerBean.class);
    }

    public void swichPager(int position){

            tv_title.setText(data.get(position).getTitle());
            fl_content.removeAllViews();
        MenuDetaiBasePager menuDetaiBasePager = detaiBasePagers.get(position);
        View rootView = menuDetaiBasePager.rootView;
        menuDetaiBasePager.initData();
        fl_content.addView(rootView);

        if(position==2){
            ib_swich_list_grid.setVisibility(View.VISIBLE);

            ib_swich_list_grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                PhotosMenuDetailPager detailPager= (PhotosMenuDetailPager) detaiBasePagers.get(2);
                    detailPager.swichListAndGrid(ib_swich_list_grid);
                }
            });

        }else {
            ib_swich_list_grid.setVisibility(View.GONE);
        }
    }
}
