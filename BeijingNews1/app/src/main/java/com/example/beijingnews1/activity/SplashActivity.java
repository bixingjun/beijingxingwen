package com.example.beijingnews1.activity;
/*
 *  包名: com.example.beijingnews1.activity
 * Created by ASUS on 2017/12/18.
 *  描述: TODO
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.beijingnews1.MainActivity;
import com.example.beijingnews1.R;
import com.example.beijingnews1.utils.SpUtils;

public class SplashActivity extends Activity{
    private RelativeLayout rl_splahs_root;
    public static final String START_MAIN="start_main";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    private void initView() {
         rl_splahs_root = findViewById(R.id.rl_splahs_root);

        AlphaAnimation animation=new AlphaAnimation(0,1);
        animation.setFillAfter(true);

        ScaleAnimation scaleAnimation=new ScaleAnimation(0,1,0,1,ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setFillAfter(true);

        RotateAnimation rotateAnimation=new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setFillAfter(true);

        AnimationSet set=new AnimationSet(false);
        set.addAnimation(animation);
        set.addAnimation(scaleAnimation);
        set.addAnimation(rotateAnimation);
        set.setDuration(2000);

        rl_splahs_root.startAnimation(set);

        set.setAnimationListener(new MyAnimationListener());
    }

    private class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            boolean start_main = SpUtils.getBoolean(SplashActivity.this, START_MAIN);
            Intent intent;
            if(start_main){
                intent= new Intent(SplashActivity.this, MainActivity.class);

            }else {
                intent= new Intent(SplashActivity.this,GuideActivity.class);
            }
            startActivity(intent);
            finish();

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
