package com.example.beijingnews1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.beijingnews1.R;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsDetailActivity extends Activity implements View.OnClickListener {

    private TextView tvTitle;
    private ImageButton ibMenu;
    private ImageButton ibTextsize;
    private ImageButton ibShare;
    private ImageButton ib_back;
    private ImageButton ibSwichListGrid;
    private Button btnCart;

    private WebView webview;
    private ProgressBar pbLoading;
    private String url;
    private int tempSize;
    private int realSize;
    private WebSettings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        findViews();
        getData();
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("biaoti");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }






    private void getData() {
        url=getIntent().getStringExtra("url");
         settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);

        settings.setUseWideViewPort(true);

        settings.setBuiltInZoomControls(true);

        //不让当前页面跳转到系统浏览器
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoading.setVisibility(View.GONE);

            }
        });
        webview.loadUrl(url);

    }


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-01-20 14:51:41 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
         ib_back =(ImageButton) findViewById(R.id.ib_back);
        tvTitle = (TextView)findViewById( R.id.tv_title );
        ibMenu = (ImageButton)findViewById( R.id.ib_menu );
        ibTextsize = (ImageButton)findViewById( R.id.ib_textsize );
        ibShare = (ImageButton)findViewById( R.id.ib_share );
        ibSwichListGrid = (ImageButton)findViewById( R.id.ib_swich_list_grid );
        btnCart = (Button)findViewById( R.id.btn_cart );
        webview = (WebView)findViewById( R.id.webview );
        pbLoading = (ProgressBar)findViewById( R.id.pb_loading );

        ib_back.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        ibMenu.setVisibility(View.GONE);
        ibShare.setVisibility(View.VISIBLE);
        ibTextsize.setVisibility(View.VISIBLE);
        ibSwichListGrid.setVisibility(View.GONE);


        ibMenu.setOnClickListener( this );
        ibTextsize.setOnClickListener( this );
        ibShare.setOnClickListener( this );
        ibSwichListGrid.setOnClickListener( this );
        btnCart.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-01-20 14:51:41 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == ibMenu ) {
            // Handle clicks for ibMenu
        } else if ( v == ibTextsize ) {
            showChangeTextSizeDialog();
        } else if ( v == ibShare ) {
            showShare();
        } else if ( v == ibSwichListGrid ) {

        } else if ( v == btnCart ) {
            // Handle clicks for btnCart
        }else if(v==ib_back){
            finish();
        }
    }

    private void showChangeTextSizeDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("设置文字大小");
                String[] items={"超大字体","大字体","正常字体","小字体","超小字体"};
                builder.setSingleChoiceItems(items, 2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    tempSize=which;
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realSize=tempSize;
                        changeTextSize(realSize);
                    }
                });
                builder.show();

    }

    private void changeTextSize(int realSize) {
        switch (realSize){
            case 0:
                settings.setTextZoom(200);
                break;
            case 1:
                settings.setTextZoom(150);
                break;
            case 2:
                settings.setTextZoom(100);
                break;
            case 3:
                settings.setTextZoom(75);
                break;
            case 4:
                settings.setTextZoom(50);
                break;
        }
    }


}
