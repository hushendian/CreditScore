package com.xl.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by hushendian on 2018/1/11.
 */

public class FlikerProgressBar extends View implements Runnable {
    private int loadingColor; //加载时的颜色
    private int stopColor;//加载完成时的颜色
    private int textSize;//字体大小
    private Paint textPaint;//字体画笔
    private Paint bgPaint;//背景画笔
    private Paint pgPaint;//绘制加载进度条画笔
    private int radius;//圆角长方形圆角半径
    private int borderWidth;//边框宽度
    private int mTotalWidth;//进度条长度
    private int progressHeight;//进度条高度
    private RectF bgRectf;//圆角长方形所需参数
    private boolean isStop;//是否暂停
    private boolean isFinish;//是否结束
    private float progress;//进度值
    private int textHeight;//文本高度
    private Thread thread;//图文混排的线程
    /**
     * 进度文本、边框、进度条颜色
     */
    private int progressColor;
    private static final String TAG = "FlikerProgressBar";
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
    private Canvas pgCanvas;
    /**
     * 进度条 bitmap ，包含滑块
     */
    private Bitmap pgBitmap;
    /**
     * 左右来回移动的滑块
     */
    private Bitmap flikerBitmap;
    private float flickerLeft;
    BitmapShader bitmapShader;

    public FlikerProgressBar(Context context) {
        this(context, null);
    }

    public FlikerProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlikerProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "FlikerProgressBar: ");

        initAttrs(attrs);
//        initPaint();

    }


    private void initAttrs(AttributeSet attributeSet) {
        TypedArray array = getContext().obtainStyledAttributes(attributeSet, R.styleable
                .FlikerProgressBar);
        loadingColor = array.getColor(R.styleable.FlikerProgressBar_loadingColor, Color
                .parseColor("#40c4ff"));
        stopColor = array.getColor(R.styleable.FlikerProgressBar_stopColor, Color.parseColor
                ("#ff9800"));
        textSize = (int) array.getDimension(R.styleable.FlikerProgressBar_textSize, DensityUtils
                .dp2px
                        (getContext(), 12));
        radius = (int) array.getDimension(R.styleable.FlikerProgressBar_radius, 0);
        borderWidth = (int) array.getDimension(R.styleable.FlikerProgressBar_borderWidth, 1);
        progressHeight = (int) array.getDimension(R.styleable
                        .FlikerProgressBar_FlikerProgressBarHeight,
                DensityUtils.dp2px(getContext(), 25));
        array.recycle();
    }

    private void initPaint() {
        //文字画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);

        //背景画笔
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(borderWidth);
        bgPaint.setColor(loadingColor);

        //覆盖层画笔
        pgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pgPaint.setStyle(Paint.Style.FILL);

        textHeight = DensityUtils.getTextHeight(textPaint);
        bgRectf = new RectF(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() -
                getPaddingLeft() - getPaddingRight(),
                getPaddingTop() + progressHeight);
        if (isStop) {
            progressColor = stopColor;
        } else {
            progressColor = loadingColor;
        }
        flikerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.flicker);
        flickerLeft = -flikerBitmap.getWidth();
        init();
    }

    private void init() {
        pgBitmap = Bitmap.createBitmap(getMeasuredWidth() - borderWidth, getMeasuredHeight() -
                borderWidth, Bitmap.Config.ARGB_8888);
        pgCanvas = new Canvas(pgBitmap);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSpecMode != MeasureSpec.EXACTLY) {
            heightSpecSize = DensityUtils.dp2px(getContext(), 20) + getPaddingTop() +
                    getPaddingBottom()
                    + progressHeight;
        }
        // TODO: 2018/1/12 width、height为0时，会导致onDraw不执行
        setMeasuredDimension(widthSpecSize, heightSpecSize);
        mTotalWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: ");
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        drawRoundRect(canvas);
        drawText(canvas);
        drawLoadingRect(canvas);
//        drawProgress(canvas);
        changeColor(canvas);
    }


    private void drawRoundRect(Canvas canvas) {
//        bgRectf = new RectF();
//        bgRectf.left = getPaddingLeft();
//        bgRectf.right = mTotalWidth;
//        bgRectf.top = DensityUtils.dp2px(getContext(), 20) + getPaddingTop();
//        bgRectf.bottom = bgRectf.top + progressHeight;

        bgPaint.setColor(progressColor);
        canvas.drawRoundRect(bgRectf, radius, radius, bgPaint);
    }

    private void drawText(Canvas canvas) {
        textPaint.setColor(progressColor);
        int textX = (int) (getPaddingLeft() + mTotalWidth / 2 - textPaint.measureText
                (getProgressText()) / 2);
        int textY = getPaddingTop() + (progressHeight +
                textHeight) / 2 - DensityUtils.dp2px(getContext(), 2);
        Log.d(TAG, "drawText: 第一次 "+textX+"-------"+textY);
        canvas.drawText(getProgressText(), textX, textY, textPaint);
    }

    private void drawProgress(Canvas canvas) {
        pgPaint.setColor(progressColor);

        float right = (progress / 100f) * getMeasuredWidth();
        pgCanvas.save(Canvas.ALL_SAVE_FLAG);
        pgCanvas.clipRect(0, 0, right, getMeasuredHeight());
        pgCanvas.drawColor(progressColor);
        pgCanvas.restore();
        Log.d(TAG, "drawProgress: " + isStop);
        if (!isStop) {
            pgPaint.setXfermode(xfermode);
            pgCanvas.drawBitmap(flikerBitmap, flickerLeft, 0, pgPaint);
            pgPaint.setXfermode(null);
        }

        //控制显示区域
        bitmapShader = new BitmapShader(pgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        pgPaint.setShader(bitmapShader);
        canvas.drawRoundRect(bgRectf, radius, radius, pgPaint);
    }

    private void drawLoadingRect(Canvas canvas) {
        float right = (progress / 100f) * mTotalWidth + getPaddingLeft();
        pgCanvas.save(Canvas.ALL_SAVE_FLAG);
        pgCanvas.clipRect(getPaddingLeft(), getPaddingTop(), right, getPaddingTop() + progressHeight);
        pgCanvas.drawColor(progressColor);
        pgCanvas.restore();
        if (!isStop) {
            pgPaint.setXfermode(xfermode);
            pgCanvas.drawBitmap(flikerBitmap, flickerLeft, getPaddingTop(), pgPaint);
            pgPaint.setXfermode(null);
        }
        //控制显示区域
        bitmapShader = new BitmapShader(pgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        pgPaint.setShader(bitmapShader);
        canvas.drawRoundRect(bgRectf, radius, radius, pgPaint);
        //另一中方法绘制进度条
//        float loadingX = progress * 1.0f * mTotalWidth / 100;
//        pgRectf = new RectF();
//        pgRectf.left = getPaddingLeft();
//        pgRectf.right = loadingX;
//        pgRectf.top = DensityUtils.dp2px(getContext(), 20) + getPaddingTop();
//        pgRectf.bottom = bgRectf.top + progressHeight;
//        pgPaint.setColor(progressColor);
//        canvas.drawRoundRect(pgRectf, radius, radius, pgPaint);

    }

    private void changeColor(Canvas canvas) {
        textPaint.setColor(Color.WHITE);
        int textX = (int) (getPaddingLeft() + mTotalWidth / 2 - textPaint.measureText
                (getProgressText()) / 2);
        int textY = getPaddingTop() + (progressHeight +
                textHeight) / 2 - DensityUtils.dp2px(getContext(), 2);
        float progressWidth = (progress / 100f) * mTotalWidth+getPaddingLeft();
        if (progressWidth > textX) {
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.clipRect(0, 0, progressWidth, textY + DensityUtils.dp2px(getContext(), 2));
            Log.d(TAG, "drawText: 第二次 "+textX+"-------"+textY);
            canvas.drawText(getProgressText(), textX, textY, textPaint);
            canvas.restore();
        }
    }

    private String getProgressText() {
        Log.d(TAG, "getProgressText: " + progress);
        String text = "";
        if (!isFinish) {
            if (!isStop) {
                text = "下载中" + progress + "%";
            } else {
                text = "继续";
            }
        } else {
            text = "下载完成";
        }

        return text;
    }

    public float getProgress() {
        return progress;
    }

    public boolean isStop() {
        return isStop;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setProgress(float progress) {
        if (!isStop) {
            if (progress < 100f) {
                this.progress = progress;
            } else {
                this.progress = 100f;
                finishLoad();
            }
            invalidate();
        }

    }

    public void finishLoad() {
        isFinish = true;
        setStop(true);
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (isStop) {
            progressColor = stopColor;
            thread.interrupt();
        } else {
            progressColor = loadingColor;
            thread = new Thread(this);
            thread.start();
        }
        invalidate();
    }

    public void toggle() {
        if (!isFinish) {
            if (isStop) {
                setStop(false);
            } else {
                setStop(true);
            }
        }
    }

    @Override
    public void run() {
        int width = flikerBitmap.getWidth();

        try {
            while (!isStop && !thread.isInterrupted()) {
                flickerLeft += DensityUtils.dp2px(getContext(), 5);

                float progressWidth = (progress / 100) * mTotalWidth;
                if (flickerLeft >= progressWidth) {
                    Log.d(TAG, "run: 大于 "+progress);
                    flickerLeft = -width;
                }
                postInvalidate();
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
