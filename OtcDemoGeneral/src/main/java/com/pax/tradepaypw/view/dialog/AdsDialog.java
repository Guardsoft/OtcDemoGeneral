package com.pax.tradepaypw.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.pax.jemv.demo.R;
import com.pax.tradepaypw.GlideImageLoader;
import com.pax.tradepaypw.utils.TickTimer;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class AdsDialog extends Dialog implements TickTimer.TickTimerListener {

    private List<Integer> imageUrl;
    private Banner banner;
    private TextView tv_jump;
    private TickTimer tickTimer;

    private int timeout;


    public AdsDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        tickTimer = new TickTimer(3, 1);
        tickTimer.setTimeCountListener(this);
        tickTimer.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initData();
        initView();
    }

    private void initData() {
        //本地图片地址
        imageUrl = new ArrayList<>();
        imageUrl.add(R.drawable.splash1);
        imageUrl.add(R.drawable.splash2);
        imageUrl.add(R.drawable.splash3);
    }

    private void initView() {
        tv_jump = (TextView) findViewById(R.id.tv_jump);
        banner = (Banner) findViewById(R.id.banner_splash);
        if (imageUrl.size() <= 1) {
            //一张图片，不显示指示器和标题
            banner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        } else {
            //显示数字指示器和标题
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        }
        banner.setImageLoader(new GlideImageLoader());
        banner.setImages(imageUrl);

    }

    @Override
    protected void onStart() {
        super.onStart();
        banner.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //在页面不可见时停止轮播
        banner.isAutoPlay(false);
    }

    @Override
    public void onFinish() {
        super.dismiss();
        tickTimer.cancel();
    }

    @Override
    public void onTick(long leftTime) {

    }
}
