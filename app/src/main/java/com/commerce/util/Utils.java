package com.commerce.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Created by Panacea-Soft on 5/8/18.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class Utils {

    public static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];

            int i = inputStream.read(bytes, 0, bytes.length);

            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean toggleUpDownWithAnimation(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(150).rotation(180);
            return true;
        } else {
            view.animate().setDuration(150).rotation(0);
            return false;
        }
    }

    public static void toggleUpDownWithAnimation(View view, int duration, int degree) {

        view.animate().setDuration(duration).rotation(degree);

    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString ;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage ;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration ;
        totalDuration = (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static int getDrawableInt(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public static int getResourceInt(Context context, String defType, String name) {
        return context.getResources().getIdentifier(name, defType, context.getPackageName());
    }

    public static void setImageToImageView(Context context, ImageView imageView, int drawable) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true);

        Glide.with(context)
                .load(drawable)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    public static void setCircleImageToImageView(Context context, ImageView imageView, int drawable, int borderWidth, int color) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .circleCrop();

        if (borderWidth > 0) {
            Glide.with(context)
                    .load(drawable)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                            imageView.setImageDrawable(resource);

                            try {
                                int colorContextCompat = ContextCompat.getColor(context, color);


                                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                                if (bitmap != null) {

                                    Drawable d = new BitmapDrawable(context.getResources(), getCircularBitmapWithBorder(bitmap, borderWidth, colorContextCompat));

                                    imageView.setImageDrawable(d);
                                } else {
                                    imageView.setImageDrawable(resource);
                                }
                            } catch (Exception e) {
                                Log.e("TEAMPS", "onResourceReady: ", e);
                            }

                        }
                    });
        } else {
            Glide.with(context)
                    .load(drawable)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }

    public static void setCircleImageToImageView(Context context, ImageView imageView, int drawable, int drawableTintColor, int borderWidth, int color) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .circleCrop();

        if (borderWidth > 0) {
            Glide.with(context)
                    .load(drawable)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                            imageView.setImageDrawable(resource);

                            try {
                                int colorContextCompat = ContextCompat.getColor(context, color);

                                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                                if (bitmap != null) {

                                    Paint paint = new Paint();
                                    paint.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(drawableTintColor), PorterDuff.Mode.SRC_IN));
                                    Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                                    Canvas canvas = new Canvas(bitmapResult);
                                    canvas.drawBitmap(bitmap, 0, 0, paint);

                                    bitmap = bitmapResult;

                                    Drawable d = new BitmapDrawable(context.getResources(), getCircularBitmapWithBorder(bitmap, borderWidth, colorContextCompat));

                                    imageView.setImageDrawable(d);
                                } else {
                                    imageView.setImageDrawable(resource);
                                }
                            } catch (Exception e) {
                                Log.e("TEAMPS", "onResourceReady: ", e);
                            }

                        }
                    });
        } else {
            Glide.with(context)
                    .load(drawable)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }

    public static void setCornerRadiusImageToImageView(Context context, ImageView imageView, int drawable, int radius, int borderWidth, int color) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .override(500, 500)
                .centerCrop()
                .skipMemoryCache(true);
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(radius));

        if (borderWidth > 0) {
            Glide.with(context)
                    .load(drawable)

                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                            imageView.setImageDrawable(resource);

                            try {
                                int colorContextCompat = ContextCompat.getColor(context, color);

                                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                                if (bitmap != null) {
                                    Drawable d = new BitmapDrawable(context.getResources(), getRoundedCornerBitmap(context, bitmap, colorContextCompat, radius, borderWidth));
                                    imageView.setImageDrawable(d);
                                } else {
                                    imageView.setImageDrawable(resource);
                                }
                            } catch (Exception e) {
                                Log.e("TEAMPS", "onResourceReady: ", e);
                            }

                        }
                    });
        } else {
            Glide.with(context)
                    .load(drawable)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }

    public static Bitmap getCircularBitmapWithBorder(Bitmap bitmap,
                                                     int borderWidth, int color) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }

    private static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, int color, int cornerDips, int borderDips) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
                context.getResources().getDisplayMetrics());
        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
                context.getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // draw border
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        return output;
    }


    @SuppressLint("RestrictedApi")
    public static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShifting(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }

    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    private static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void updateMenuIconColor(Menu menu, @ColorInt int color) {
        for (int index = 0; index < menu.size(); index++) {
            Drawable drawable = menu.getItem(index).getIcon();
            if (drawable != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public static void hideFirstFab(final View v) {
        v.setVisibility(View.GONE);
        v.setTranslationY(v.getHeight());
        v.setAlpha(0f);
    }

    public static boolean twistFab(final View v, boolean rotate) {
        v.animate().setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 165f : 0f);
        return rotate;
    }

    public static void showFab(final View v) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(0f);
        v.setTranslationY(v.getHeight());
        v.animate()
                .setDuration(300)
                .translationY(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1f)
                .start();
    }

    public static void hideFab(final View v) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(1f);
        v.setTranslationY(0);
        v.animate()
                .setDuration(300)
                .translationY(v.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(0f)
                .start();
    }

}
