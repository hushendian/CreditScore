package com.xl.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by hushendian on 2018/1/3.
 */

public class CreditScoreView extends View {
    private int dataCount = 5;
    private float radian = (float) (Math.PI * 2 / dataCount);
    private float radius;
    private int centerX;
    private int centerY;
    private String[] titles = {"履约能力", "信用历史", "人脉关系", "行为偏好", "身份特质"};
    private int[] icons = {R.mipmap.ic_performance, R.mipmap.ic_history, R.mipmap.ic_contacts,
            R.mipmap.ic_predilection, R.mipmap.ic_identity};
    private float[] data = new float[5];
    //    private float[] data = {170, 180, 160, 170, 180};
    private float maxValue = 190;
    private int radarMargin = DensityUtils.dp2px(getContext(), 15);
    //雷达区画笔
    private Paint mainPaint;
    //数据区画笔
    private Paint valuePaint;
    //分数画笔
    private Paint scorePaint;
    //标题画笔
    private Paint titlePaint;
    //图标画笔
    private Paint iconPaint;
    //分数大小
    private int scoreSize = DensityUtils.dp2px(getContext(), 28);
    //标题文字大小
    private int titleSize = DensityUtils.dp2px(getContext(), 13);
    private final String TAG = "CreditScoreView";

    public CreditScoreView(Context context) {
        this(context, null);
    }

    public CreditScoreView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreditScoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setStrokeWidth(0.3f);
        mainPaint.setColor(Color.WHITE);
        mainPaint.setStyle(Paint.Style.STROKE);

        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(Color.WHITE);
        valuePaint.setAlpha(120);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        scorePaint = new Paint();
        scorePaint.setAntiAlias(true);
        scorePaint.setTextSize(scoreSize);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setStyle(Paint.Style.FILL);

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setTextSize(titleSize);
        titlePaint.setColor(Color.WHITE);
        titlePaint.setStyle(Paint.Style.FILL);

        iconPaint = new Paint();
        iconPaint.setAntiAlias(true);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged: "+w+"----"+h+"-----"+oldw+"-----"+oldh);
        radius = Math.min(h, w) /2 * 0.5f;
        centerX = w / 2;
        centerY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //五边形
        drawPolygon(canvas);
        //五边形连接线
        drawLines(canvas);
        //覆盖区域
        drawRegion(canvas);
        //分数
        drawScore(canvas);
        // 标题
        drawTitle(canvas);
        //图标
        drawIcon(canvas);
    }

    private void drawRegion(Canvas canvas) {
        //绘制覆盖区域
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            float percent = data[i] / maxValue;
            int x = getPoint(i, 0, percent).x;
            int y = getPoint(i, 0, percent).y;
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        path.close();
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);

        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }


    private void drawScore(Canvas canvas) {
        //分數
        int score = 0;
        for (int i = 0; i < dataCount; i++) {
            score += data[i];
        }
        canvas.drawText(score + "", centerX, centerY, scorePaint);
    }

    private void drawTitle(Canvas canvas) {
        //绘制标题
        for (int i = 0; i < dataCount; i++) {
            int x = getPoint(i, radarMargin, 1).x;
            int y = getPoint(i, radarMargin, 1).y;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icons[i]);
            int iconHeight = bitmap.getHeight();
            float titleWidth = titlePaint.measureText(titles[i]);
            if (i == 1) {
                y += (iconHeight / 2);
            } else if (i == 2) {
                x -= titleWidth;
                y += (iconHeight / 2);
            } else if (i == 3) {
                x -= titleWidth;
            } else if (i == 4) {
                x -= titleWidth / 2;
            }
            canvas.drawText(titles[i], x, y, titlePaint);
        }
    }

    private void drawIcon(Canvas canvas) {
        //绘制图标
        for (int i = 0; i < dataCount; i++) {
            int x = getPoint(i, radarMargin, 1).x;
            int y = getPoint(i, radarMargin, 1).y;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icons[i]);
            int iconHeight = bitmap.getHeight();
            int iconWidth = bitmap.getWidth();
            float titleWidth = titlePaint.measureText(titles[i]);

            if (i == 0) {
                x += (titleWidth - iconWidth) / 2;
                y -= (iconHeight + getTextHeight(titlePaint));
            } else if (i == 1) {
                x += (titleWidth - iconWidth) / 2;
                y -= (iconHeight / 2 + getTextHeight(titlePaint));
            } else if (i == 2) {
                x -= (iconWidth + (titleWidth - iconWidth) / 2);
                y -= (iconHeight / 2 + getTextHeight(titlePaint));
            } else if (i == 3) {
                x -= (iconWidth + (titleWidth - iconWidth) / 2);
                y -= (iconHeight + getTextHeight(titlePaint));
            } else if (i == 4) {
                x -= iconWidth / 2;
                y -= (iconHeight + getTextHeight(titlePaint));
            }
            canvas.drawBitmap(bitmap, x, y, titlePaint);
        }
    }

    private int getTextHeight(Paint paint) {
        Paint.FontMetrics forFontMetrics = paint.getFontMetrics();
        return (int) (forFontMetrics.descent - forFontMetrics.ascent);
    }


    public void setData(float a, float b, float c, float d, float e) {
        if (a > 190) {
            a = 190;
        }
        if (b > 190) {
            b = 190;
        }
        if (c > 190) {
            c = 190;
        }
        if (d > 190) {
            d = 190;
        }
        if (e > 190) {
            e = 190;
        }


        data[0] = a;
        data[1] = b;
        data[2] = c;
        data[3] = d;
        data[4] = e;
        //重新刷新视图，重绘
        invalidate();
    }

    private void drawPolygon(Canvas canvas) {
        //绘制多边形
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            if (i == 0) {
                path.moveTo(getPoint(i).x, getPoint(i).y);
            } else {
                path.lineTo(getPoint(i).x, getPoint(i).y);
            }
        }
        path.close();
        canvas.drawPath(path, mainPaint);
    }

    private void drawLines(Canvas canvas) {
        //绘制画布
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            path.lineTo(getPoint(i).x, getPoint(i).y);
            canvas.drawPath(path, mainPaint);
        }
    }

    private Point getPoint(int position) {
        return getPoint(position, 0, 1);
    }

    private Point getPoint(int position, int radarMargin, float percent) {
        int x = 0;
        int y = 0;
        if (position == 0) {
            x = (int) (centerX + (radius + radarMargin) * Math.sin(radian) * percent);
            y = (int) (centerY - (radius + radarMargin) * Math.cos(radian) * percent);
        } else if (position == 1) {
            x = (int) (centerX + (radius + radarMargin) * Math.sin(radian / 2) * percent);
            y = (int) (centerY + (radius + radarMargin) * Math.cos(radian / 2) * percent);
        } else if (position == 2) {
            x = (int) (centerX - (radius + radarMargin) * Math.sin(radian / 2) * percent);
            y = (int) (centerY + (radius + radarMargin) * Math.cos(radian / 2) * percent);
        } else if (position == 3) {
            x = (int) (centerX - (radius + radarMargin) * Math.sin(radian) * percent);
            y = (int) (centerY - (radius + radarMargin) * Math.cos(radian) * percent);
        } else if (position == 4) {
            x = centerX;
            y = (int) (centerY - ((radius + radarMargin) * percent));
        }
        return new Point(x, y);
    }
}
