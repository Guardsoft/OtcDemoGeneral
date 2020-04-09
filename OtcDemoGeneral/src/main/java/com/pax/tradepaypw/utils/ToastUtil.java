package com.pax.tradepaypw.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by chenld on 2017/3/13.
 * 单例
 */

public class ToastUtil {
    private static Toast mToast;

    public static void showToast(final Activity activity, final String text) {

        if ("main".equals(Thread.currentThread().getName())) {
            if (mToast == null) {
                mToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
            }
            mToast.setText(text);
            mToast.show();
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
                    }
                    mToast.setText(text);
                    mToast.show();
                }
            });
        }

    }
}
