package com.pax.tradepaypw.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class MyVedioView extends VideoView {

    public MyVedioView(Context context) {
        super(context);
    }

    public MyVedioView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVedioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getWidth(), widthMeasureSpec);
        int height = getDefaultSize(getHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
