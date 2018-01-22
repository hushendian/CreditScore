package com.xl.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hushendian on 2018/1/5.
 */

public class ZMScore extends View {
    private int maxScore = 700;
    private int minScore = 650;
    private int brokenLineColor = 0xff02bbb7;
    private int straightLineColor = 0xffe2e2e2;//0xffeaeaea
    private int textNormalColor = 0xff7e7e7e;
    private Paint brokenPaint;
    private Paint straightPaint;
    private Paint dottedPaint;
    private Paint textPaint;
    private Path brokenPath;
    private float brokenLineWith;
    private int viewWidth;
    private int viewHeight;
    private List<Point> scorePoints;
    private String[] monthText = new String[]{"6月", "7月", "8月", "9月", "10月", "11月"};
    private int[] score = new int[]{681, 698, 669, 686, 675, 689};
    private int maxScoreWidth;
    private int maxScoreHeigh;
    private int minScoreWidth;
    private int minScoreHeigh;
    private int monthCount = 6;
    private int selectMonth = 6;//选中的月份

    public ZMScore(Context context) {
        this(context, null);
    }

    public ZMScore(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZMScore(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyleAttribute(attrs);
        initPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        viewWidth = w;
        initData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        drawDottedLine(canvas, getPaddingLeft() + maxScoreWidth + dp2px(10), viewHeight * 0.15f,
                viewWidth - getPaddingRight
                        ()
                , viewHeight * 0.15f);
        drawDottedLine(canvas, getPaddingLeft() + minScoreWidth + dp2px(10), viewHeight * 0.4f,
                viewWidth - getPaddingRight()

                , viewHeight * 0.4f);
        drawMonthLine(canvas);
        drawText(canvas);

        brokenLine(canvas);
        drawPoint(canvas);
    }

    private void drawDottedLine(Canvas canvas, float startX, float startY, float stopX, float
            stopY) {
        //虚线
        dottedPaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 4));
        dottedPaint.setStrokeWidth(dp2px(1));
        Path path = new Path();
        path.reset();
        path.moveTo(startX, stopY);
        path.lineTo(stopX, stopY);
        canvas.drawPath(path, dottedPaint);
    }

    private void drawMonthLine(Canvas canvas) {

        straightPaint.setStrokeWidth(dp2px(1));
        canvas.drawLine(0, viewHeight * 0.7f, viewWidth, viewHeight * 0.7f, straightPaint);
        float newWith = viewWidth - getPaddingRight() - getPaddingLeft();
        float coordinateX;//分隔线X坐标
        for (int i = 0; i < 6; i++) {
            coordinateX = newWith * ((float) (i) / (6 - 1)) + getPaddingLeft() + maxScoreWidth +
                    dp2px(15);
            canvas.drawLine(coordinateX, viewHeight * 0.7f, coordinateX, viewHeight * 0.7f +
                    dp2px(4), straightPaint);
        }

    }

    private void drawText(Canvas canvas) {

        textPaint.setTextSize(dp2px(12));
        textPaint.setColor(textNormalColor);
        canvas.drawText(maxScore + "", getPaddingLeft() + maxScoreWidth, viewHeight *
                        0.15f + maxScoreHeigh / 4,
                textPaint);
        canvas.drawText(minScore + "", getPaddingLeft() + minScoreWidth, viewHeight *
                        0.4f + minScoreHeigh / 4,
                textPaint);
        textPaint.setColor(0xff7c7c7c);
        float newWith = viewWidth - getPaddingLeft() - getPaddingRight();
        float coordinateX;
        textPaint.setTextSize(dp2px(12));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textNormalColor);
        for (int i = 0; i < 6; i++) {
            coordinateX = newWith * ((float) (i) / (6 - 1)) + getPaddingLeft() + maxScoreWidth +
                    dp2px(15);


            if (i == selectMonth - 1) {

                textPaint.setStyle(Paint.Style.STROKE);
                textPaint.setColor(brokenLineColor);
                RectF r2 = new RectF();
                r2.left = coordinateX - textPaint.measureText(monthText[i] + "") - dp2px(1);
                r2.top = viewHeight * 0.7f +  textPaint.measureText(monthText[i] + "")
                        / 4;
                r2.right = coordinateX + textPaint.measureText(monthText[i] + "") + dp2px(1);
                r2.bottom = viewHeight * 0.7f  + textPaint.measureText(monthText[i] +
                        "") +dp2px(4);
                canvas.drawRoundRect(r2, 10, 10, textPaint);
            }
            canvas.drawText(monthText[i], coordinateX, viewHeight * 0.7f + dp2px(4) +
                    getTextHeight(textPaint), textPaint);
            textPaint.setColor(textNormalColor);

        }

    }

    private void brokenLine(Canvas canvas) {
        brokenPath.reset();
        brokenPaint.setColor(brokenLineColor);
        brokenPaint.setStyle(Paint.Style.STROKE);
        brokenPath.moveTo(scorePoints.get(0).x, scorePoints.get(0).y);

        for (int i = 0; i < 6; i++) {
            brokenPath.lineTo(scorePoints.get(i).x, scorePoints.get(i).y);
        }
        canvas.drawPath(brokenPath, brokenPaint);

    }

    //绘制折线穿过的点
    protected void drawPoint(Canvas canvas) {
        if (scorePoints == null) {
            return;
        }
        brokenPaint.setStrokeWidth(dp2px(1));
        for (int i = 0; i < scorePoints.size(); i++) {
            brokenPaint.setColor(brokenLineColor);
            brokenPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dp2px(3), brokenPaint);
            brokenPaint.setColor(Color.WHITE);
            brokenPaint.setStyle(Paint.Style.FILL);
            if (i == selectMonth - 1) {
                brokenPaint.setColor(0xffd0f3f2);
                canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dp2px(8f),
                        brokenPaint);
                brokenPaint.setColor(0xff81dddb);
                canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dp2px(5f),
                        brokenPaint);

                //绘制浮动文本背景框
                drawFloatTextBackground(canvas, scorePoints.get(i).x, scorePoints.get(i).y
                        - dp2px(8f));

                textPaint.setColor(0xffffffff);
                //绘制浮动文字
                canvas.drawText(String.valueOf(score[i]), scorePoints.get(i).x, scorePoints.get
                        (i).y - textPaint.measureText(score[i] + "") + dp2px(2), textPaint);
            }
            brokenPaint.setColor(0xffffffff);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dp2px(1.5f),
                    brokenPaint);
            brokenPaint.setStyle(Paint.Style.STROKE);
            brokenPaint.setColor(brokenLineColor);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dp2px(2.5f),
                    brokenPaint);
        }
    }

    //绘制显示浮动文字的背景
    private void drawFloatTextBackground(Canvas canvas, int x, int y) {
        brokenPath.reset();
        brokenPaint.setColor(brokenLineColor);
        brokenPaint.setStyle(Paint.Style.FILL);
        //P1
        Point point = new Point(x, y);
        brokenPath.moveTo(point.x, point.y);

        //P2
        point.x = point.x + dp2px(5);
        point.y = point.y - dp2px(5);
        brokenPath.lineTo(point.x, point.y);

        //P3
        point.x = point.x + dp2px(12);
        brokenPath.lineTo(point.x, point.y);

        //P4
        point.y = point.y - dp2px(17);
        brokenPath.lineTo(point.x, point.y);

        //P5
        point.x = point.x - dp2px(34);
        brokenPath.lineTo(point.x, point.y);

        //P6
        point.y = point.y + dp2px(17);
        brokenPath.lineTo(point.x, point.y);

        //P7
        point.x = point.x + dp2px(12);
        brokenPath.lineTo(point.x, point.y);

        //最后一个点连接到第一个点
        brokenPath.lineTo(x, y);

        canvas.drawPath(brokenPath, brokenPaint);
    }

    private void initData() {
        scorePoints = new ArrayList<>();
        float maxScoreYCoordinate = viewHeight * 0.15f;
        float minScoreYCoordinate = viewHeight * 0.4f;
        //自定义左边距右边距
        float newWith = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int coordinateX;
        for (int i = 0; i < score.length; i++) {
            Point point = new Point();
            point.x = (int) (newWith * ((float) (i) / (6 - 1)) + getPaddingLeft());
//            Log.d("ZMScore", "initData: " + point.x);
            if (score[i] > maxScore) {
                score[i] = maxScore;
            } else if (score[i] < minScore) {
                score[i] = minScore;
            }

            point.y = (int) ((((float) (maxScore - score[i]) / (maxScore - minScore)) * viewHeight /
                    4) + 0.15 * viewHeight);
//            Log.d("ZMScore", "initData: "+point.y);
            Log.d("ZMScore", "initData: " + (maxScore));
            Log.d("ZMScore", "initData: " + (score[i]));
            Log.d("ZMScore", "initData: " + (maxScore - score[i]));
            Log.d("ZMScore", "initData: " + (maxScore - minScore));

            Log.d("ZMScore", "initData: " + (float) (maxScore - score[i]) / (maxScore - minScore));

            scorePoints.add(point);
        }


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_UP:
                onEventUp(event);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return true;
    }

    private void onEventUp(MotionEvent event) {
        boolean isValidTouch = validateTouch(event.getX(), event.getY());
        Log.d("ZMScore", "onEventUp: " + isValidTouch);
        if (isValidTouch) {
            invalidate();
        }
    }

    //是否是有效的触摸范围
    private boolean validateTouch(float x, float y) {
        for (int i = 0; i < 6; i++) {
            if (x > (scorePoints.get(i).x - dp2px(8) * 2) && x < (scorePoints.get(i).x + dp2px(8)
                    * 2)) {
                if (y > (scorePoints.get(i).y - dp2px(8) * 2) && y < (scorePoints.get(i).y +
                        dp2px(8) * 2)) {
                    selectMonth = i + 1;
                    return true;
                }
            }

        }
        return false;
    }

    private void obtainStyleAttribute(AttributeSet attributeSet) {
        //获取属性值
        TypedArray array = getContext().obtainStyledAttributes(attributeSet, R.styleable.ZMScore);
        maxScore = array.getInt(R.styleable.ZMScore_maxScore, 700);
        minScore = array.getInt(R.styleable.ZMScore_minScore, 650);
        brokenLineColor = array.getColor(R.styleable.ZMScore_brokenLineColor, 0xff02bbb7);
        straightLineColor = array.getColor(R.styleable.ZMScore_straightLineColor, 0xffe2e2e2);
        textNormalColor = array.getColor(R.styleable.ZMScore_brokenLineColor, 0xff7e7e7e);
        brokenLineWith = array.getDimension(R.styleable.ZMScore_brokenLineWith, 0.5f);
        array.recycle();
    }

    private void initPaint() {
        brokenPath = new Path();
        //初始化画笔
        //折线
        brokenPath = new Path();
        brokenPaint = new Paint();
        brokenPaint.setAntiAlias(true);
        brokenPaint.setStyle(Paint.Style.STROKE);
        brokenPaint.setStrokeWidth(dp2px(brokenLineWith));
        brokenPaint.setStrokeCap(Paint.Cap.ROUND);
        //月份的线
        straightPaint = new Paint();
        straightPaint.setAntiAlias(true);
        straightPaint.setStyle(Paint.Style.STROKE);
        straightPaint.setStrokeWidth(brokenLineWith);
        straightPaint.setColor((straightLineColor));
        straightPaint.setStrokeCap(Paint.Cap.ROUND);
        //虚线
        dottedPaint = new Paint();
        dottedPaint.setAntiAlias(true);
        dottedPaint.setStyle(Paint.Style.STROKE);
        dottedPaint.setStrokeWidth(brokenLineWith);
        dottedPaint.setColor((straightLineColor));
        dottedPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor((textNormalColor));
        textPaint.setTextSize(dp2px(15));

        maxScoreWidth = (int) textPaint.measureText(maxScore + "");
        minScoreWidth = (int) textPaint.measureText(minScore + "");
        maxScoreHeigh = getTextHeight(textPaint);
        minScoreHeigh = getTextHeight(textPaint);
    }

    public int[] getScore() {
        return score;
    }

    public void setScore(int[] score) {
        this.score = score;
    }

    public String[] getMonthText() {
        return monthText;
    }

    public void setMonthText(String[] monthText) {
        this.monthText = monthText;
    }

    public int dp2px(float dp) {
        //获取设备密度
        float density = getContext().getResources().getDisplayMetrics().density;
        //4.3, 4.9, 加0.5是为了四舍五入
        int px = (int) (dp * density + 0.5f);
        return px;
    }

    private int getTextHeight(Paint paint) {
        Paint.FontMetrics forFontMetrics = paint.getFontMetrics();
        return (int) (forFontMetrics.descent - forFontMetrics.ascent);
    }

}
