package com.otc.ui.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;

public final class BitmapUtils {
    public static Bitmap getResBitmap(Context context, int bmpResId) {
        Resources res = context.getResources();
        Bitmap originalBitmap = BitmapFactory.decodeResource(res, bmpResId);
        Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        return whiteBgBitmap;
    }

    public static Bitmap getWhiteBgBitmapScaleTo(Bitmap originalBitmap, int maxSize) {
        Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        int outWidth;
        int outHeight;
        int inWidth = whiteBgBitmap.getWidth();
        int inHeight = whiteBgBitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(whiteBgBitmap, outWidth, outHeight, false);
        return resizedBitmap;
    }

    public static int getDrawable(Context context, String id) {
        return context.getResources().getIdentifier(id, "drawable", context.getPackageName());
    }

    public static Bitmap loadBitmapFromView(Context context, View v) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return bitmap;
    }

    public static Bitmap getPrinableBitmap(Bitmap bitmap) {
        Bitmap whiteBgBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return whiteBgBitmap;
    }

    public static Bitmap getWhiteBgBitmap(String bitmapInBase64, int maxSize) {
        byte[] imageBytes = Base64.decode(bitmapInBase64, Base64.NO_WRAP);
        Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return BitmapUtils.getWhiteBgBitmapScaleTo(originalBitmap, maxSize);
    }
}
