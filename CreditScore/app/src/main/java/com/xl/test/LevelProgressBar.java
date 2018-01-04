package com.xl.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * Created by hushendian on 2018/1/4.
 */

public class LevelProgressBar extends ProgressBar {
    private final int EMPTY_MESSAGE = 1;
    //等级选定时的颜色
    private int levelTextChooseColor;
    //等级未选定时的颜色
    private int levelTextUnChooseColor;
    //等级的字体大小
    private int levelTextSize;
    //进度条起始颜色
    private int progressStartColor;
    //进度条结束时的颜色
    private int progressEndColor;
    //进度条的背景颜色
    private int progressBgColor;
    //进度条的高度
    private int progressHeight;
    private Paint paint;
    //进度条的宽度
    private int mTotalWidth;
    //等级字体的宽度
    private int textHeight;
    //等级，默认为4
    private int levels = 4;
    //等级分类
    private String[] levelTexts;
    //当前等级
    private int currentLevel;
    //延迟时间
    private int animInterval;
    //设置的进度条刻度值
    private int targetProgress;

    public LevelProgressBar(Context context) {
        this(context, null);
    }

    public LevelProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttributes(attrs);
        initPaint();
    }

    //初始化画笔
    private void initPaint() {
        paint = new Paint();
        paint.setTextSize(levelTextSize);
        paint.setColor(levelTextUnChooseColor);
    }

    //获取属性值
    private void obtainStyledAttributes(AttributeSet attributeSet) {
        TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable
                .LevelProgressBar);
        levelTextUnChooseColor = a.getColor(R.styleable.LevelProgressBar_LevelTextUnChooseColor,
                0x000000);
        levelTextChooseColor = a.getColor(R.styleable.LevelProgressBar_LevelTextChooseColor,
                0x333333);
        levelTextSize = (int) a.getDimension(R.styleable.LevelProgressBar_LevelTextSize, dp2px(15));
        progressStartColor = a.getColor(R.styleable.LevelProgressBar_ProgressBarStartColor,
                0xCCFFCC);
        progressEndColor = a.getColor(R.styleable.LevelProgressBar_ProgressBarEndColor, 0x00FF00);
        progressBgColor = a.getColor(R.styleable.LevelProgressBar_ProgressBarBGColor, 0x000000);
        progressHeight = (int) a.getDimension(R.styleable.LevelProgressBar_progressBraHeight,
                dp2px(20));
        a.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);
        //适用于未设定高度的情况：warp_content
        if (hm != MeasureSpec.EXACTLY) {
            textHeight = (int) (paint.descent() - paint.ascent());

            //view的整体大小=paddingTop+等级高度+等级与进度条之间的高度+进度条的高度+paddingButtom
            h = getPaddingTop() + getPaddingBottom() + textHeight + progressHeight + dp2px(10);
        }
        setMeasuredDimension(w, h);
        mTotalWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

    }

    public int dp2px(float dp) {
        //获取设备密度
        float density = getContext().getResources().getDisplayMetrics().density;
        //4.3, 4.9, 加0.5是为了四舍五入
        int px = (int) (dp * density + 0.5f);
        return px;
    }

    // 设置等级数
    public void setLevels(int levels) {
        this.levels = levels;
    }

    // 设置不同等级对应的文字
    public void setLevelTexts(String[] texts) {
        levelTexts = texts;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
        this.targetProgress = (int) (level * 1f / levels * getMax());
        Log.d("LevelProgressBar", "setCurrentLevel: " + targetProgress + "");
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //画布移到x轴为左边距位置，Y轴为上边距位置
        canvas.translate(getPaddingLeft(), getPaddingTop());
        for (int i = 0; i < levels; i++) {
            int textwidth = (int) paint.measureText(levelTexts[i]);
            paint.setColor(levelTextUnChooseColor);
            paint.setTextSize(levelTextSize);
            if (getProgress() == targetProgress && currentLevel >= 1 && currentLevel <= levels &&
                    i == currentLevel - 1) {
                paint.setColor(levelTextChooseColor);
            }
            canvas.drawText(levelTexts[i], mTotalWidth / levels * (i + 1) - textwidth - dp2px(10),
                    textHeight, paint);
        }
        int lineY = textHeight + progressHeight / 2 + dp2px(10);
        paint.setColor(progressBgColor);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(progressHeight);
        //灰色背景的进度条
        canvas.drawLine(getPaddingLeft(), lineY, mTotalWidth - getPaddingLeft(), lineY, paint);
        int reachedParEnd = (int) (getProgress() * 1f / getMax() * mTotalWidth);
        if (reachedParEnd > 0) {
            //带颜色的进度条
            paint.setStrokeCap(Paint.Cap.ROUND);

            Shader shader = new LinearGradient(0, lineY, getWidth(), lineY, progressStartColor,
                    progressEndColor, Shader.TileMode.REPEAT);
            paint.setShader(shader);
            //理论上是从getPaddingLeft开始的，但是出现了开始时有一段颜色比较深，之后才慢慢变浅
            canvas.drawLine(progressHeight / 2, lineY, reachedParEnd - progressHeight / 2, lineY,
                    paint);
            paint.setShader(null);
        }
        canvas.restore();
    }

    public void setAnimInterval(final int animInterval) {
        this.animInterval = animInterval;
        handler.sendEmptyMessage(EMPTY_MESSAGE);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int progress = getProgress();

            Log.d("LevelProgressBar", "handleMessage: " + progress + "-------" + targetProgress);
            if (progress < targetProgress) {
                setProgress(++progress);

                handler.sendEmptyMessageDelayed(EMPTY_MESSAGE, animInterval);
            } else if (progress > targetProgress) {
                setProgress(--progress);
                handler.sendEmptyMessageDelayed(EMPTY_MESSAGE, animInterval);
            } else {
                handler.removeMessages(EMPTY_MESSAGE);
            }
        }
    };
}
