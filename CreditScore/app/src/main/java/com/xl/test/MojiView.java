package com.xl.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hushendian on 2018/1/8.
 */

public class MojiView extends View {
    private Paint bitmapPaint, windyBoxPaint, linePaint, pointPaint, dashLinePaint;
    private TextPaint textPaint;
    private int width;
    private int height;
    private static final int ITEM_SIZE = 24;
    private int item_width;
    private List<HourItem> listItems;
    private int maxTemp = 26;
    private int minTemp = 21;
    private int maxWindy = 5;
    private int minWindy = 2;
    private int tempBaseTop;  //温度折线的上边Y坐标
    private int tempBaseBottom; //温度折线的下边Y坐标
    private static final int windyBoxAlpha = 80;
    private static final int windyBoxMaxHeight = 80;
    private static final int windyBoxMinHeight = 20;
    private static final int windyBoxSubHight = windyBoxMaxHeight - windyBoxMinHeight;
    private static final int bottomTextHeight = 60;
    private static final int TEMP[] = {22, 23, 23, 23, 23,
            22, 23, 23, 23, 22,
            21, 21, 22, 22, 23,
            23, 24, 24, 25, 25,
            25, 26, 25, 24};
    private static final int WINDY[] = {2, 2, 3, 3, 3,
            4, 4, 4, 3, 3,
            3, 4, 4, 4, 4,
            2, 2, 2, 3, 3,
            3, 5, 5, 5};
    private static final int WEATHER_RES[] = {R.mipmap.w0, R.mipmap.w1, R.mipmap.w3, -1, -1
            , R.mipmap.w5, R.mipmap.w7, R.mipmap.w9, -1, -1
            , -1, R.mipmap.w10, R.mipmap.w15, -1, -1
            , -1, -1, -1, -1, -1
            , R.mipmap.w18, -1, -1, R.mipmap.w19};

    public MojiView(Context context) {
        this(context, null);
    }

    public MojiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MojiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        pointPaint = new Paint();
        pointPaint.setColor(new Color().WHITE);
        pointPaint.setAntiAlias(true);
        pointPaint.setTextSize(8);

        linePaint = new Paint();
        linePaint.setColor(new Color().WHITE);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);

        dashLinePaint = new Paint();
        dashLinePaint.setColor(new Color().WHITE);
        PathEffect effect = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        dashLinePaint.setPathEffect(effect);
        dashLinePaint.setStrokeWidth(3);
        dashLinePaint.setAntiAlias(true);
        dashLinePaint.setStyle(Paint.Style.STROKE);

        windyBoxPaint = new Paint();
        windyBoxPaint.setTextSize(1);
        windyBoxPaint.setColor(new Color().WHITE);
        windyBoxPaint.setAlpha(80);
        windyBoxPaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setTextSize(DensityUtils.sp2px(getContext(), 12));
        textPaint.setColor(new Color().WHITE);
        textPaint.setAntiAlias(true);

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        item_width = w / 30;
        width = getPaddingRight() + getPaddingLeft() + ITEM_SIZE * item_width;
        height = h / 2;
    }


    private void initHourItem() {
        listItems = new ArrayList<>();
        for (int i = 0; i < ITEM_SIZE; i++) {
            String item = null;
            {
                if (i < 10) {
                    item = "0" + i + ":00";
                } else {
                    item = i + ":00";
                }
                int left = getPaddingLeft() + i * item_width;
                int right = getPaddingLeft() + (i + 1) * item_width - 1;
                int top = (int) (height - bottomTextHeight +
                        (maxWindy - WINDY[i]) * 1.0 / (maxWindy - minWindy) * windyBoxSubHight
                        - windyBoxMaxHeight);
                int bottom=height-bottomTextHeight;
                Rect  rect=new Rect(left,top,right,bottom);
            }
        }
    }

//    private Point calculateTempPoint(int left,int right,int temp){
////        double minHeight=
//    }
}
