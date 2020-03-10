package com.github.huihuangui.ovalseekbar;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

/**
 * 可拖拽椭圆进度条
 *
 * @author Hui Huangui
 * @email: huihuangui@yeah.net
 */
public class OvalSeekBar extends View {

    //Debug相关变量
    protected static final String TAG = "OvalSeekBar";

    protected static final boolean DEBUG = true;
    protected static final boolean DEBUG_TOUCH = false && DEBUG;
    protected static final boolean DEBUG_REFER_LINE = false && DEBUG;
    protected static final boolean DEBUG_PROCESS = false && DEBUG;
    protected static final boolean DEBUG_ANIMATION = false && DEBUG;
    protected static final boolean DEBUG_MEASURE = false && DEBUG;

    /**
     * Alpha最大值
     */
    public static final int MAX_ALPHA = 0xff;

    /**
     * 默认最大进度
     */
    public static final int DEFAULT_MAX_PROGRESS = 100;

    /**
     * 默认最小进度
     */
    public static final int DEFAULT_MIN_PROGRESS = 0;

    /**
     * 由于线条和球太小极难操作，设置一个扩展区域
     */
    protected int touchExpendDelta = 60;

    /**
     * Seek 过程中的监听
     */
    protected OnSeekBarChangeListener mOnSeekBarChangeListener;

    /**
     * View本身的宽
     */
    protected int mWidth;

    /**
     * View本身的高
     */
    protected int mHeight;

    /**
     * 椭圆的宽
     */
    protected int mOvalRectWith;
    /**
     * 椭圆的高
     */
    protected int mOvalRectHeight;

    /**
     * 椭圆矩形的左
     */
    protected int mOvalRectLeft;

    /**
     * 椭圆矩形的上
     */
    protected int mOvalRectTop;
    /**
     * 椭圆矩形的右
     */
    protected int mOvalRectRight;
    /**
     * 椭圆矩形的下
     */
    protected int mOvalRectBottom;

    /**
     * 椭圆所在矩形
     */
    protected RectF mOvalRect;

    /**
     * 四条曲线之间的间隔角度
     */
    protected int mArcGapAngle;

    /**
     * 椭圆的圆心
     */
    protected PointF mOvalCenter;

    /**
     * 椭圆的长轴a
     */
    protected int mA;
    /**
     * 椭圆的短轴b
     */
    protected int mB;

    /**
     * 拖拽控制球可以Seek的区域，在这个区域Seek， 离开这个区域就结束Seek
     */
    protected RectF mSeekRect;

    /**
     * 进度条的粗细
     */
    protected int mStrokeWidth;
    /**
     * 进度条的初始颜色
     */
    protected int mLineColor;
    /**
     * 进度条的起始角度
     */
    protected int mStartAngle;
    /**
     * 进度条的结束角度
     */
    protected int mEndAngle;

    /**
     * 进度渐变色的起始颜色
     */
    protected int mStartColor;

    /**
     * 进度渐变色的结束颜色
     */
    protected int mEndColor;

    /**
     * 进度球的半径
     */
    protected int mThumbRadius;
    /**
     * 进度球的颜色
     */
    protected int mThumbColor;
    /**
     * 进度球的圆心坐标X
     */
    protected int mThumbX;
    /**
     * 进度球的圆心坐标Y
     */
    protected int mThumbY;

    /**
     * 旋转时，控制球的位置X
     */
    protected double mRotateThumbX;
    /**
     * 旋转时，控制球的位置Y
     */
    protected double mRotateThumbY;

    /**
     * 拖拽时控制球的位置X
     */
    protected double mDragThumbX;
    /**
     * 拖拽时控制球的位置Y
     */
    protected double mDragThumbY;

    /**
     * 要不要绘制控制球
     */
    protected boolean bDrawThumb = true;

    /**
     * 前端弧线起始位置X坐标
     */
    protected double mFrontStartX;
    /**
     * 前端弧线起始位置Y坐标
     */
    protected double mFrontStartY;
    /**
     * 前端弧线结束位置的X坐标
     */
    protected double mFrontEndX;
    /**
     * 前端弧线结束位置的Y坐标
     */
    protected double mFrontEndY;

    /**
     * 拖拽时当前进度文本位置X坐标
     */
    protected double mDragDurationStartX;

    /**
     * 拖拽时当前进度文本位置Y坐标
     */
    protected double mDragDurationStartY;

    /**
     * 拖拽时总进度文本位置X坐标
     */
    protected double mDragDurationEndX;

    /**
     * 拖拽时总进度文本位置Y坐标
     */
    protected double mDragDurationEndY;

    /**
     * 当前进度要不要跟着进度球前进
     */
    protected boolean bPlayedTimeMoving = false;

    /**
     * 拖拽的角度
     */
    protected int mDragAngle;

    /**
     * 椭圆初始画笔--前
     */
    protected Paint mFrontPaint;
    /**
     * 椭圆初始画笔--左
     */
    protected Paint mLeftPaint;
    /**
     * 椭圆初始画笔--右
     */
    protected Paint mRightPaint;
    /**
     * 椭圆初始画笔--后
     */
    protected Paint mBackPaint;

    /**
     * 进度画笔
     */
    protected Paint mProgressPaint;

    /**
     * 进度点画笔
     */
    protected Paint mThumbPaint;

    /**
     * 进度文字画笔
     */
    protected Paint mProgressTextPaint;

    /**
     * 总进度文字画笔
     */
    protected Paint mMaxProgressTextPaint;

    /**
     * 调试画笔，画参考线使用，只有开启调试才初始化
     */
    protected Paint mDebugPaint;

    /**
     * 当前进度
     */
    protected int mProgress;

    /**
     * 当前进度对应的角度
     */
    protected float mProgressAngle;

    /**
     * 旋转总时间
     */
    protected int mRotateDuration;
    /**
     * 旋转角度
     */
    protected int mRotateAngle;

    /**
     * 进度球透明度，分10次变完; 控制球的Alpha渐变速度
     */
    protected int mThumbPaintAlphaStep = 0xff / 10;

    /**
     * 是否正在旋转
     */
    protected boolean bIsRotating;

    /**
     * 是否正在拖拽
     */
    protected boolean bIsDragging;

    /**
     * 是否在追踪触摸事件
     */
    protected boolean bIsTrackingTouch;

    /**
     * 进度渐变器
     */
    protected LinearGradient mProgressGradient;

    /**
     * 拖拽时进度渐变
     */
    protected LinearGradient mDragProgressGradient;

    protected int[] mProgressGradientColors = new int[2];

    //左边弧线渐变
    private LinearGradient mLeftLineGradient;
    private int[] mLeftArcGradientColors = new int[2];


    //右边弧线渐变
    protected LinearGradient mRightLineGradient;
    protected int[] mRightArcGradientColors = new int[2];


    //拖拽时左边弧线
    protected LinearGradient mDragLeftLineGradient;
    private int[] mDragLeftArcGradientColors = new int[2];


    //拖拽时右边弧线
    protected LinearGradient mDragRightLineGradient;
    private int[] mDragRightArcGradientColors = new int[2];

    /**
     * 时长字体大小
     */
    protected int mDurationTextSize;

    /**
     * 时长字体颜色
     */
    protected int mDurationTextColor;

    /**
     * 保存四条弧线中前段的静止信息
     */
    protected ArcData mFrontArc;

    /**
     * 保存四条弧线中左段的静止信息
     */
    protected ArcData mLeftArc;

    /**
     * 保存四条弧线中右段的静止信息
     */
    protected ArcData mRightArc;

    /**
     * 保存四条弧线中后段的静止信息
     */
    protected ArcData mBackArc;

    /**
     * 旋转时四条弧线中前段的信息
     */
    protected ArcData mRotateFrontArc;

    /**
     * 旋转时四条弧线中左段的信息
     */
    protected ArcData mRotateLeftArc;

    /**
     * 旋转时四条弧线中后段的信息
     */
    protected ArcData mRotateBackArc;

    /**
     * 旋转时四条弧线中右段的信息
     */
    protected ArcData mRotateRightArc;

    /**
     * 拖拽时四条弧线中前段的信息
     */
    protected ArcData mDragFrontArc;

    /**
     * 拖拽时四条弧线中左段的信息
     */
    protected ArcData mDragLeftArc;

    /**
     * 拖拽时四条弧线中后段的信息
     */
    protected ArcData mDragBackArc;

    /**
     * 拖拽时四条弧线中右段的信息
     */
    protected ArcData mDragRightArc;


    /**
     * 旋转时进度弧线弧度信息
     */
    protected ArcData mRotateProgressArc;

    /**
     * 拖拽时进度弧线弧度信息
     */
    protected ArcData mDragProgressArc;

    /**
     * 拖拽时，根据角度产生一个透明度变化，用在前端弧线和进度弧线上
     */
    protected float mDragAlpha = 0.4f;

    /**
     * 拖拽的偏移量
     */
    protected float mDragOffset;

    /**
     * 最大进度
     */
    protected int mMaxProgress;

    /**
     * 旋转动画上一个值
     */
    protected float mAnimatorPreValue;

    /**
     * 延迟画控制球
     */
    protected Runnable mThumbDrawRunnable;

    protected ValueAnimator mRotateAnimator;

    /**
     * 按下事件的X坐标
     */
    private float mDownX;

    /**
     * 按下事件的Y坐标
     */
    private float mDownY;

    /**
     * 点击和拖拽事件的分界值
     */
    private float mScaledTouchSlop;


    // debug 相关
    private double debugDragProcessStartX, debugDragProcessStartY, debugDragProcessEndX, debugDragProcessEndY;
    private double debugLeftStartX, debugLeftStartY, debugLeftEndX, debugLeftEndY;
    protected double debugRightStartX, debugRightStartY, debugRightEndX, debugRightEndY;
    private double debugDragLeftStartX, debugDragLeftStartY, debugDragLeftEndX, debugDragLeftEndY;
    private double debugDragRightStartX, debugDragRightStartY, debugDragRightEndX, debugDragRightEndY;


    public OvalSeekBar(Context context) {
        this(context, null);
    }

    public OvalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OvalSeekBar);
        mLineColor = ta.getColor(R.styleable.OvalSeekBar_oval_line_color,
                Color.parseColor("#ffb0b0b0"));
        mStrokeWidth = ta.getDimensionPixelOffset(R.styleable.OvalSeekBar_oval_stroke_width,
                4);
        mStartAngle = ta.getInt(R.styleable.OvalSeekBar_oval_start_angle, 45);
        mEndAngle = ta.getInt(R.styleable.OvalSeekBar_oval_end_angle, 135);
        mStartColor = ta.getColor(R.styleable.OvalSeekBar_oval_progress_start_color,
                Color.parseColor("#FF27A5FE"));
        mEndColor = ta.getColor(R.styleable.OvalSeekBar_oval_progress_end_color,
                Color.parseColor("#FF34DAB5"));
        mThumbRadius = ta.getDimensionPixelOffset(
                R.styleable.OvalSeekBar_oval_progress_thumb_radius, 15);
        mThumbColor = ta.getColor(R.styleable.OvalSeekBar_oval_progress_thumb_color,
                Color.parseColor("#8B0A50"));
        mArcGapAngle = ta.getInt(R.styleable.OvalSeekBar_oval_arc_gap_angle, 10);
        mRotateDuration = ta.getInt(R.styleable.OvalSeekBar_oval_rotate_duration, 300);
        mRotateAngle = ta.getInt(R.styleable.OvalSeekBar_oval_rotate_angle, 90);
        mDurationTextColor = ta.getColor(R.styleable.OvalSeekBar_oval_duration_text_color, mLineColor);
        mDurationTextSize = ta.getDimensionPixelOffset(R.styleable.OvalSeekBar_oval_duration_text_size,
                mStrokeWidth);
        mMaxProgress = ta.getInteger(R.styleable.OvalSeekBar_oval_max_progress, DEFAULT_MAX_PROGRESS);
        mProgress = ta.getInteger(R.styleable.OvalSeekBar_oval_initial_progress, 0);
        setProgress(mProgress, mMaxProgress);
        ta.recycle();

        //弧线画笔前端
        mFrontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFrontPaint.setStyle(Paint.Style.STROKE);
        mFrontPaint.setStrokeWidth(mStrokeWidth);
        mFrontPaint.setColor(mLineColor);
        mFrontPaint.setColorFilter(new LightingColorFilter(mLineColor, mLineColor));

        //左侧弧线画笔
        mLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLeftPaint.setStyle(Paint.Style.STROKE);
        mLeftPaint.setStrokeWidth(mStrokeWidth);
        mLeftPaint.setColor(mLineColor);

        mLeftArcGradientColors[0] = adjustAlpha(mLineColor, 0.95f);
        mLeftArcGradientColors[1] = adjustAlpha(mLineColor, 0.001f);
        mLeftPaint.setColorFilter(new LightingColorFilter(mLeftArcGradientColors[0],
                mLeftArcGradientColors[1]));

        //右侧弧线画笔
        mRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRightPaint.setStyle(Paint.Style.STROKE);
        mRightPaint.setStrokeWidth(mStrokeWidth);
        mRightPaint.setColor(mLineColor);

        mRightArcGradientColors[0] = adjustAlpha(mLineColor, 0.001f);
        mRightArcGradientColors[1] = adjustAlpha(mLineColor, 0.950f);
        mRightPaint.setColorFilter(new LightingColorFilter(mRightArcGradientColors[0],
                mRightArcGradientColors[1]));

        //后侧
        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setStyle(Paint.Style.STROKE);
        mBackPaint.setStrokeWidth(mStrokeWidth);
        mBackPaint.setColor(mLineColor);
        mBackPaint.setAlpha(30);


        //进度画笔
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mStrokeWidth);
        mProgressGradientColors[0] = mStartColor;
        mProgressGradientColors[1] = mEndColor;

        //圆钉画笔
        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbPaint.setStyle(Paint.Style.STROKE);
        mThumbPaint.setColor(mThumbColor);
        transferThumbPaintAlpha(0xff);
        mThumbPaint.setStyle(Paint.Style.FILL);

        //节目时长画笔
        mProgressTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressTextPaint.setColor(mDurationTextColor);
        mProgressTextPaint.setTextSize(mDurationTextSize);

        //结束时长画笔
        mMaxProgressTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaxProgressTextPaint.setColor(mDurationTextColor);
        mMaxProgressTextPaint.setTextSize(mDurationTextSize);
        mMaxProgressTextPaint.setTextAlign(Paint.Align.RIGHT);


        //debug 画笔
        if (DEBUG_REFER_LINE) {
            initDebugPaint();
        }
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!bIsRotating && !bIsDragging) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();

            mOvalRectLeft = getPaddingLeft();
            mOvalRectTop = getPaddingTop();

            mOvalRectWith = mWidth - mOvalRectLeft - getPaddingRight();
            mOvalRectHeight = mHeight - mOvalRectTop - getPaddingTop();

            mOvalRectRight = mOvalRectLeft + mOvalRectWith;
            mOvalRectBottom = mOvalRectTop + mOvalRectHeight;

            if (DEBUG_MEASURE) {
                Log.d(TAG, "onMeasure: mWidth: " + mWidth + ", mHeight:" + mHeight);
            }
            mA = mOvalRectWith / 2;
            mB = mOvalRectHeight / 2;
            mOvalRect = new RectF(mOvalRectLeft, mOvalRectTop, mOvalRectRight, mOvalRectBottom);
            mSeekRect = new RectF(
                    (float) (mA + mA * Math.cos(Math.toRadians(mEndAngle)) - touchExpendDelta) + getPaddingLeft(),
                    mB + mB / 2 + touchExpendDelta,
                    (float) (mA + mA * Math.cos(Math.toRadians(mStartAngle)) + touchExpendDelta + getPaddingLeft()),
                    mHeight);
            setProgress(mProgress);
            measureOvalCenter();
            measureArcAngle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bIsDragging) { // 拖拽
            drawDragging(canvas);
        } else if (bIsRotating) { // 旋转
            drawRotating(canvas);
        } else { // 静止
            drawIdle(canvas);
        }
    }

    protected void drawDragging(Canvas canvas) {
        boolean debugDraw = DEBUG_REFER_LINE;
        mFrontPaint.setAlpha((int) (MAX_ALPHA * mDragAlpha));
        canvas.drawArc(mOvalRect, mDragFrontArc.startAngle, mDragFrontArc.sweepAngle, false, mFrontPaint);
        if (mDragLeftLineGradient == null) {
            mLeftPaint.setShader(mLeftLineGradient);
        } else {
            mLeftPaint.setShader(mDragLeftLineGradient);
        }
        canvas.drawArc(mOvalRect, mDragLeftArc.startAngle, mDragLeftArc.sweepAngle, false, mLeftPaint);
        canvas.drawArc(mOvalRect, mDragBackArc.startAngle, mDragBackArc.sweepAngle, false, mBackPaint);
        if (mDragRightLineGradient == null) {
            mRightPaint.setShader(mRightLineGradient);
        } else {
            mRightPaint.setShader(mDragRightLineGradient);
        }
        canvas.drawArc(mOvalRect, mDragRightArc.startAngle, mDragRightArc.sweepAngle, false, mRightPaint);
        // 画进度
        if (mProgress > 0) {
            if (mDragProgressGradient != null) {
                mProgressPaint.setShader(mDragProgressGradient);
            } else {
                mProgressPaint.setShader(mProgressGradient);
            }
            canvas.drawArc(mOvalRect, mDragProgressArc.startAngle, -mProgressAngle, debugDraw, mProgressPaint);
        }
        mThumbPaint.setAlpha((int) (MAX_ALPHA * mDragAlpha));
        canvas.drawCircle((int) mDragThumbX, (int) mDragThumbY, mThumbRadius, mThumbPaint);
        if (DEBUG_PROCESS) {
            Log.d(TAG, "onDraw bDrawThumb mDragThumbX: " + mDragThumbX + ", mDragThumbY: " + mDragThumbY);
        }
        String progressText = String.valueOf(mProgress);
        if (bPlayedTimeMoving) {
            canvas.drawText(progressText, 0, progressText.length(), (int) mDragThumbX,
                    (int) mDragThumbY + 20, mProgressTextPaint);
        } else {
            canvas.drawText(progressText, 0, progressText.length(), (int) mDragDurationStartX,
                    (int) mDragDurationStartY + 40, mProgressTextPaint);
        }
        String maxProgressText = String.valueOf(mMaxProgress);
        if (maxProgressText != null) {
            canvas.drawText(maxProgressText, 0, maxProgressText.length(), (int) mDragDurationEndX,
                    (int) mDragDurationEndY + 40, mMaxProgressTextPaint);
        }
        if (debugDraw) {
            canvas.drawLine(mOvalCenter.x, mOvalCenter.y, (int) mDragThumbX,
                    (int) mDragThumbY, mDebugPaint);
            String text = mProgress + "%";
            canvas.drawText(text, 0, text.length(), mOvalCenter.x + 20, mOvalCenter.y - 50, mDebugPaint);
            canvas.drawLine((float) debugDragLeftStartX, (float) debugDragLeftStartY,
                    (float) debugDragLeftEndX, (float) debugDragLeftEndY, mDebugPaint);
            canvas.drawLine((float) debugDragRightStartX, (float) debugDragRightStartY,
                    (float) debugDragRightEndX, (float) debugDragRightEndY, mDebugPaint);
            canvas.drawLine((float) debugDragProcessStartX, (float) debugDragProcessStartY,
                    (float) debugDragProcessEndX, (float) debugDragProcessEndY, mDebugPaint);
        }
    }

    protected void drawIdle(Canvas canvas) {
        boolean debugDraw = DEBUG_REFER_LINE;
        //画前端弧线
        mFrontPaint.setAlpha(MAX_ALPHA);
        canvas.drawArc(mOvalRect, mFrontArc.startAngle, mFrontArc.sweepAngle, debugDraw, mFrontPaint);
        //画左侧弧线
        mLeftPaint.setShader(mLeftLineGradient);
        canvas.drawArc(mOvalRect, mLeftArc.startAngle, mLeftArc.sweepAngle, false, mLeftPaint);
        //画后面的弧线
        canvas.drawArc(mOvalRect, mBackArc.startAngle, mBackArc.sweepAngle, false, mBackPaint);
        //画右边的弧线
        mRightPaint.setShader(mRightLineGradient);
        canvas.drawArc(mOvalRect, mRightArc.startAngle, mRightArc.sweepAngle, false, mRightPaint);

        // 画进度
        if (mProgress > 0) {
            mProgressPaint.setShader(mProgressGradient);
            canvas.drawArc(mOvalRect, mEndAngle, -mProgressAngle, false, mProgressPaint);
        }
        if (bDrawThumb) {
            if (DEBUG_PROCESS) {
                Log.d(TAG, "onDraw bDrawThumb mThumbX: " + mThumbX + ", mThumbY: " + mThumbY);
            }
            mThumbPaint.setAlpha(MAX_ALPHA);
            if (bIsTrackingTouch) {
                mThumbPaint.setAlpha((int) (MAX_ALPHA * 0.5));
                canvas.drawCircle(mThumbX, mThumbY, mThumbRadius * 4, mThumbPaint);
                mThumbPaint.setAlpha(MAX_ALPHA);
                canvas.drawCircle(mThumbX, mThumbY, mThumbRadius, mThumbPaint);
            } else {
                mThumbPaint.setAlpha(MAX_ALPHA);
                canvas.drawCircle(mThumbX, mThumbY, mThumbRadius, mThumbPaint);
            }
        }
        String progressText = String.valueOf(mProgress);
        if (progressText != null) {
            if (bPlayedTimeMoving) {
                canvas.drawText(progressText, 0, progressText.length(),
                        mThumbX + mThumbRadius, mThumbY + 20, mProgressTextPaint);
            } else {
                canvas.drawText(progressText, 0, progressText.length(),
                        (int) mFrontEndX - 4, (int) mFrontEndY + 55, mProgressTextPaint);
            }

        }
        String maxProgressText = String.valueOf(mMaxProgress);
        if (maxProgressText != null) {
            if (debugDraw) {
                Log.d(TAG, "onDraw mMaxProgressText mFrontStartX = " + mFrontStartX
                        + ", mFrontStartY = " + mFrontStartY + ", mMaxProgressText: "
                        + maxProgressText);
            }
            canvas.drawText(maxProgressText, 0, maxProgressText.length(),
                    (int) mFrontStartX, (int) mFrontStartY + 55, mMaxProgressTextPaint);
        } else {
            if (debugDraw) {
                Log.d(TAG, "onDraw mMaxProgressText = null");
            }
        }

        if (debugDraw) {
            canvas.drawRect(mSeekRect, mDebugPaint);
            canvas.drawCircle(mOvalCenter.x, mOvalCenter.y, 10, mDebugPaint);
            canvas.drawLine(mOvalCenter.x, mOvalCenter.y, mOvalCenter.x - mA, mOvalCenter.y, mDebugPaint);
            canvas.drawLine(mOvalCenter.x, mOvalCenter.y, mOvalCenter.x, mOvalCenter.y - mB, mDebugPaint);
            canvas.drawLine(mOvalCenter.x, mOvalCenter.y, mOvalCenter.x + mA, mOvalCenter.y, mDebugPaint);
            canvas.drawLine(mOvalCenter.x, mOvalCenter.y, mOvalCenter.x, mOvalCenter.y + mB, mDebugPaint);
            canvas.drawRect(mOvalRect, mDebugPaint);
            canvas.drawLine((float) debugLeftEndX, (float) debugLeftEndY,
                    (float) debugLeftStartX, (float) debugLeftStartY, mDebugPaint);
            canvas.drawLine((float) debugRightStartX, (float) debugRightStartY,
                    (float) debugRightEndX, (float) debugRightEndY, mDebugPaint);
            canvas.drawLine(mOvalCenter.x, mOvalCenter.y, mThumbX, mThumbY, mDebugPaint);
            String text = mProgress + "%";
            canvas.drawText(text, 0, text.length(), mOvalCenter.x + 20, mOvalCenter.y - 50, mDebugPaint);
            Log.d(TAG, "onDraw center x = " + mOvalCenter.x + ", y = " + mOvalCenter.y + ", left");
            Log.d(TAG, "onDraw circle x = " + mThumbX + ", y = " + mThumbY
                    + ", mA = " + mA + ", mB = " + mB);
        }
    }

    protected void drawRotating(Canvas canvas) {
        boolean debugDraw = DEBUG_REFER_LINE;
        canvas.drawArc(mOvalRect, mRotateFrontArc.startAngle, mRotateFrontArc.sweepAngle, false, mFrontPaint);
        mLeftPaint.setShader(mLeftLineGradient);
        canvas.drawArc(mOvalRect, mRotateLeftArc.startAngle, mRotateLeftArc.sweepAngle, false, mLeftPaint);
        canvas.drawArc(mOvalRect, mRotateBackArc.startAngle, mRotateBackArc.sweepAngle, false, mBackPaint);
        mRightPaint.setShader(mRightLineGradient);
        canvas.drawArc(mOvalRect, mRotateRightArc.startAngle, mRotateRightArc.sweepAngle, false, mRightPaint);
        // 画进度
        if (mProgress > 0) {
            mProgressPaint.setShader(mProgressGradient);
            canvas.drawArc(mOvalRect, mRotateProgressArc.startAngle, -mProgressAngle, debugDraw, mProgressPaint);
        }
        canvas.drawCircle((int) mRotateThumbX, (int) mRotateThumbY, mThumbRadius, mThumbPaint);
        if (DEBUG_PROCESS) {
            Log.d(TAG, "onDraw bDrawThumb mRotateThumbX: " + mRotateThumbX + ", mRotateThumbY: " + mRotateThumbY);
        }
        if (debugDraw) {
            canvas.drawLine(mOvalCenter.x, mOvalCenter.y, (int) mRotateThumbX, (int) mRotateThumbY, mDebugPaint);
            String text = mProgress + "%";
            canvas.drawText(text, 0, text.length(), mOvalCenter.x + 20, mOvalCenter.y - 50, mDebugPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (DEBUG_TOUCH) {
            Log.d(TAG, "----onTouchEvent, isEnabled " + isEnabled());
        }
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float xd = event.getX(), yd = event.getY();
                boolean onThumb = eventDownOnThumb(xd, yd);
                boolean onSeekRect = eventMoveOnProgressBar(xd, yd);
                if (onThumb || onSeekRect) {
                    mDownX = xd;
                    mDownY = yd;
                    startTrackingTouch();
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                float xm = event.getX(), ym = event.getY();
                boolean onSeekArea = eventMoveOnProgressBar(xm, ym);
                if (onSeekArea) {
                    int progress = getSeekProgress(xm, ym);
                    int nowProgress = mProgress + progress;
                    if (DEBUG_TOUCH) {
                        Log.d(TAG, "----onTouchEvent, nowProgress " + nowProgress);
                    }
                    setProgress(mProgress + progress, true);
                    return true;
                } else {
                    stopTrackingTouch();
                    return false;
                }
            case MotionEvent.ACTION_UP:
                eventUp(event.getX(), event.getY());
                stopTrackingTouch();
                break;
            case MotionEvent.ACTION_CANCEL:
                stopTrackingTouch();
                break;
        }
        return false;
    }

    public void setProgress(int progress) {
        if (DEBUG_PROCESS) {
            Log.d(TAG, "setProgress progress: " + progress);
        }
        setProgress(progress, mMaxProgress);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress, int maxProgress) {
        if (DEBUG_PROCESS) {
            Log.d(TAG, "setProgress progress: " + progress + ", mProgress: " + mProgress
                    + ", duration: " + maxProgress);
        }
        if (progress == mProgress && progress != 0) {
            return;
        }
        if (maxProgress != 0 && maxProgress != mMaxProgress) {
            setMaxProgress(maxProgress);
        }
        if (DEBUG_PROCESS) {
            Log.d(TAG, "setProgress bIsRotating: " + bIsRotating + ", bIsDragging: " + bIsDragging);
        }
        if (bIsRotating || bIsDragging) {
            return;
        }
        setProgress(progress, false);
    }

    /**
     * 执行旋转动画
     *
     * @param clockwise 是否是顺时针方向
     */
    public void rotate(boolean clockwise) {
        if (bIsRotating) {
            //如果正在旋转，则忽略新的旋转调用
            return;
        }
        if (bIsDragging) {
            mRotateThumbX = mDragThumbX;
            mRotateThumbY = mDragThumbY;
        } else {
            mRotateThumbX = mThumbX;
            mRotateThumbY = mThumbY;
        }
        startRotateAnimation(clockwise);
    }

    /**
     * 获取进度条的当前进度
     *
     * @return 当前进度
     */
    public int getProgress() {
        return mProgress;
    }

    public OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return mOnSeekBarChangeListener;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mOnSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = mOnSeekBarChangeListener;
    }

    /**
     * 设置时长, 以毫秒计算
     *
     * @param millisecond
     */
    public void setMaxProgress(int millisecond) {
        if (DEBUG_PROCESS) {
            Log.d(TAG, "setMaxProgress millisecond: " + millisecond);
        }
        mMaxProgress = millisecond;
        invalidate();
    }


    /**
     * 获取进度条的最大进度
     *
     * @return 最大进度
     */
    public int getMaxProgress() {
        return mMaxProgress;
    }

    private void setProgress(int progress, boolean fromUser) {
        if (DEBUG_PROCESS) {
            Log.d(TAG, "setProgress progress: " + progress + ", mProgress: "
                    + mProgress + ", fromUser: " + fromUser);
        }
        if (progress < 0) {
            progress = DEFAULT_MIN_PROGRESS;
        } else if (progress > mMaxProgress) {
            progress = mMaxProgress;
        }
        updateProgress(progress, fromUser);
    }

    private void startTrackingTouch() {
        bIsTrackingTouch = true;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    private void stopTrackingTouch() {
        if (!bIsTrackingTouch) {
            return;
        }
        bIsTrackingTouch = false;
        invalidate();
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    private void measureOvalCenter() {
        mOvalCenter = new PointF();
        mOvalCenter.x = (mOvalRectRight + mOvalRectLeft) / 2;
        mOvalCenter.y = (mOvalRectBottom + mOvalRectTop) / 2;
    }

    /*
     *  计算每一条弧线的起始角度和扫过角度
     */
    private void measureArcAngle() {
        //前面的弧线，从布局文件的属性获取它的开始和结束角度作为它的初始值，计算弧线扫过的角度
        int startAngle = mStartAngle + mArcGapAngle / 2;
        int sweepAngle = mEndAngle - mArcGapAngle / 2 - mStartAngle;
        mFrontArc = new ArcData(startAngle, sweepAngle);

        //左边的弧线，从前面的弧线结束位置加上弧线间的缝隙开始，扫过它起点与x轴夹角的二倍
        startAngle = mEndAngle + mArcGapAngle / 2;
        sweepAngle = (180 - startAngle) * 2;
        mLeftArc = new ArcData(startAngle, sweepAngle);
        //创建左边弧线的渐变
        float la = startAngle;
        double lx = mA * Math.cos(Math.toRadians(la));
        double ly = mB * Math.sin(Math.toRadians(la));
        float lsx = (int) lx + mWidth / 2;
        float lsy = (int) ly + mHeight / 2;
        la = sweepAngle + startAngle;
        lx = mA * Math.cos(Math.toRadians(la));
        ly = mB * Math.sin(Math.toRadians(la));
        float lex = (int) lx + mWidth / 2;
        float ley = (int) ly + mHeight / 2;
        mLeftLineGradient = new LinearGradient((int) lsx, (int) lsy, lex, ley,
                mLeftArcGradientColors, null, Shader.TileMode.CLAMP);
        if (DEBUG_REFER_LINE) {
            debugLeftStartX = lsx;
            debugLeftStartY = lsy;
            debugLeftEndX = lex;
            debugLeftEndY = ley;
            Log.d(TAG, "measureArcAngle left debugLeftEndX: " + debugLeftEndX
                    + ", debugLeftEndY: " + debugLeftEndY + ", debugLeftStartX: "
                    + debugLeftStartX + ", debugLeftStartY: " + debugLeftStartY);
        }

        // 后面的弧线，从左边弧线的结束位置加上缝隙开始，扫过和前面的弧线一样的弧度
        startAngle = startAngle + sweepAngle + mArcGapAngle;
        sweepAngle = (270 - startAngle) * 2;
        mBackArc = new ArcData(startAngle, sweepAngle);

        // 右边的弧线, 从后边弧线的结束位置加上缝隙开始，扫过前面的弧线与x轴夹角减去缝隙的二倍
        startAngle = startAngle + sweepAngle + mArcGapAngle;
        sweepAngle = (360 - startAngle) * 2;
        mRightArc = new ArcData(startAngle, sweepAngle);

        //创建右边弧线的渐变
        float ra = startAngle;
        double rx = mA * Math.cos(Math.toRadians(ra));
        double ry = mB * Math.sin(Math.toRadians(ra));
        float rsx = (int) rx + mWidth / 2;
        float rsy = (int) ry + mHeight / 2;
        ra = sweepAngle + startAngle;
        rx = mA * Math.cos(Math.toRadians(ra));
        ry = mB * Math.sin(Math.toRadians(ra));
        float rex = (int) rx + mWidth / 2;
        float rey = (int) ry + mHeight / 2;
        mRightLineGradient = new LinearGradient(
                (int) rsx, (int) rsy, rex, rey,
                mRightArcGradientColors, null, Shader.TileMode.CLAMP);
        if (DEBUG_REFER_LINE) {
            debugRightStartX = rsx;
            debugRightStartY = rsy;
            debugRightEndX = rex;
            debugRightEndY = rey;
            Log.d(TAG, "measureArcAngle right debugRightStartX: " + debugRightStartX
                    + ", debugRightStartY: " + debugRightStartY + ", debugRightEndX: "
                    + debugRightEndX + ", debugRightEndY: " + debugRightEndY);
        }

        //进度弧线
        mRotateProgressArc = new ArcData(mEndAngle, 0);

        // 位置从布局左上为原点，下面计算出来的x， y是以椭圆圆心为原点，
        // 所以要用画布一半的宽度减去或者加上x，高度的一半减去或者加上y，换算成在view中的坐标

        double x = mA * Math.cos(Math.toRadians(mStartAngle));
        double y = mB * Math.sin(Math.toRadians(mStartAngle));
        mFrontStartX = x + mWidth / 2;
        mFrontStartY = y + mHeight / 2;

        x = mA * Math.cos(Math.toRadians(mEndAngle));
        y = mB * Math.sin(Math.toRadians(mEndAngle));
        mFrontEndX = x + mWidth / 2;
        mFrontEndY = y + mHeight / 2;

        float arcStart = mEndAngle - mProgressAngle;
        x = mA * Math.cos(Math.toRadians(arcStart));
        y = mB * Math.sin(Math.toRadians(arcStart));
        mThumbX = (int) x + mWidth / 2;
        mThumbY = (int) y + mHeight / 2;
        if (DEBUG_PROCESS) {
            Log.d(TAG, "measureArcAngle mThumbX: " + mThumbX + ", mThumbY: " + mThumbY);
        }

        //用原始角度初始化旋转弧线的初始值
        setRotateInitialData();
        setDragInitialData();
    }

    private void setRotateInitialData() {
        mRotateFrontArc = new ArcData(mFrontArc.startAngle, mFrontArc.sweepAngle);
        mRotateLeftArc = new ArcData(mLeftArc.startAngle, mLeftArc.sweepAngle);
        mRotateBackArc = new ArcData(mBackArc.startAngle, mBackArc.sweepAngle);
        mRotateRightArc = new ArcData(mRightArc.startAngle, mRightArc.sweepAngle);
        mRotateProgressArc = new ArcData(mEndAngle, (int) mProgressAngle);
    }

    protected void resetRotateInitialData() {
        mRotateFrontArc.setValue(mFrontArc.startAngle, mFrontArc.sweepAngle);
        mRotateLeftArc.setValue(mLeftArc.startAngle, mLeftArc.sweepAngle);
        mRotateBackArc.setValue(mBackArc.startAngle, mBackArc.sweepAngle);
        mRotateRightArc.setValue(mRightArc.startAngle, mRightArc.sweepAngle);
        mRotateProgressArc.setValue(mEndAngle, (int) mProgressAngle);
    }

    protected void setDragInitialData() {
        mDragFrontArc = new ArcData(mFrontArc.startAngle, mFrontArc.sweepAngle);
        mDragLeftArc = new ArcData(mLeftArc.startAngle, mLeftArc.sweepAngle);
        mDragBackArc = new ArcData(mBackArc.startAngle, mBackArc.sweepAngle);
        mDragRightArc = new ArcData(mRightArc.startAngle, mRightArc.sweepAngle);
        mDragProgressArc = new ArcData(mEndAngle, (int) mProgressAngle);
        mDragAngle = 0;
        mDragDurationStartX = mFrontEndX;
        mDragDurationStartY = mFrontEndY;
    }

    private void rotateInner(boolean clockwise, float angle) {
        if (DEBUG_ANIMATION) {
            Log.d(TAG, "rotateInner step: " + angle);
        }
        if (clockwise) {
            mRotateFrontArc.startAngle = mRotateFrontArc.startAngle + angle;
            mRotateLeftArc.startAngle = mRotateLeftArc.startAngle + angle;
            mRotateBackArc.startAngle = mRotateBackArc.startAngle + angle;
            mRotateRightArc.startAngle = mRotateRightArc.startAngle + angle;
            mRotateProgressArc.startAngle = mRotateProgressArc.startAngle + angle;
        } else {
            mRotateFrontArc.startAngle = mRotateFrontArc.startAngle - angle;
            mRotateLeftArc.startAngle = mRotateLeftArc.startAngle - angle;
            mRotateBackArc.startAngle = mRotateBackArc.startAngle - angle;
            mRotateRightArc.startAngle = mRotateRightArc.startAngle - angle;
            mRotateProgressArc.startAngle = mRotateProgressArc.startAngle - angle;
        }

        if (DEBUG_PROCESS) {
            Log.d(TAG, "rotateRight: mRotateFrontArc.startAngle: " + mRotateFrontArc.startAngle);
        }

        //计算旋转时控制球的坐标
        calculateRotateThumbXY();
        invalidate();
    }

    protected void startDrag() {
        bIsDragging = true;
        if (DEBUG_TOUCH) {
            Log.d(TAG, "--drag-- startDrag: mThumbX: " + mThumbX + ", mThumbY: " + mThumbY);
        }
        mDragThumbX = mThumbX;
        mDragThumbY = mThumbY;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelRotateAnimator();
    }

    protected void endDrag() {
        bIsDragging = false;
        if (DEBUG_TOUCH) {
            Log.d(TAG, "--drag-- endDrag: ");
        }
        setDragInitialData();
        calculateDragDurationXY(false, 0);
        invalidate();
    }

    public void drag(boolean left, int angle) {
        dragInner(left, angle);
    }

    protected void dragInner(boolean left, int angle) {
        if (mDragAngle >= mStartAngle * 2 || mDragAngle <= mStartAngle * -2) {
            return; //拖拽角度不能大于某一个值，参考起始角度
        }
        if (mDragFrontArc == null) { //说明measure还没有完成
            return;
        }
        if (left) {
            mDragFrontArc.startAngle = mDragFrontArc.startAngle + angle;
            mDragLeftArc.startAngle = mDragLeftArc.startAngle + angle;
            mDragBackArc.startAngle = mDragBackArc.startAngle + angle;
            mDragRightArc.startAngle = mDragRightArc.startAngle + angle;
            mDragProgressArc.startAngle = mDragProgressArc.startAngle + angle;
        } else {
            mDragFrontArc.startAngle = mDragFrontArc.startAngle - angle;
            mDragLeftArc.startAngle = mDragLeftArc.startAngle - angle;
            mDragBackArc.startAngle = mDragBackArc.startAngle - angle;
            mDragRightArc.startAngle = mDragRightArc.startAngle - angle;
            mDragProgressArc.startAngle = mDragProgressArc.startAngle - angle;
        }
        //拖拽的过程中，旋转的起始角度随之变化，和拖拽的起始角度保持一致，这样可以避免在旋转的时候回调
        mRotateFrontArc.startAngle = mDragFrontArc.startAngle;
        mRotateLeftArc.startAngle = mDragLeftArc.startAngle;
        mRotateBackArc.startAngle = mDragBackArc.startAngle;
        mRotateRightArc.startAngle = mDragRightArc.startAngle;
        mRotateProgressArc.startAngle = mDragProgressArc.startAngle;

        if (DEBUG_TOUCH) {
            Log.d(TAG, "dragInner: mRotateFrontArc.startAngle: " + mRotateFrontArc.startAngle);
        }
        //透明度渐变，拖满90度，变成100%， 也就是255，
        float alpha = Math.abs((mDragFrontArc.startAngle - mStartAngle)) / mRotateAngle;
        if (alpha > 1f) {
            alpha = 1f;
        }

        mDragLeftArcGradientColors[0] = mLineColor;
        mDragLeftArcGradientColors[1] = adjustAlpha(mLineColor, alpha);

        float la = mDragLeftArc.startAngle;
        double lx = mA * Math.cos(Math.toRadians(la));
        double ly = mB * Math.sin(Math.toRadians(la));
        float lsx = (int) lx + mWidth / 2;
        float lsy = (int) ly + mHeight / 2;
        la = mDragLeftArc.sweepAngle + mDragLeftArc.startAngle;
        lx = mA * Math.cos(Math.toRadians(la));
        ly = mB * Math.sin(Math.toRadians(la));
        float lex = (int) lx + mWidth / 2;
        float ley = (int) ly + mHeight / 2;

        if (DEBUG_REFER_LINE) {
            debugDragLeftStartX = lsx;
            debugDragLeftStartY = lsy;
            debugDragLeftEndX = lex;
            debugDragLeftEndY = ley;
            Log.d(TAG, "dragInner left debugLeftEndX: " + debugDragLeftEndX
                    + ", debugDragLeftEndY: " + debugDragLeftEndY + ", debugDragLeftStartX: "
                    + debugDragLeftStartX + ", debugDragLeftStartY: " + debugDragLeftStartY
                    + " alpha: " + alpha);
        }

        mDragRightArcGradientColors[0] = adjustAlpha(mLineColor, alpha);
        mDragRightArcGradientColors[1] = mLineColor;

        float ra = mDragRightArc.startAngle;
        double rx = mA * Math.cos(Math.toRadians(ra));
        double ry = mB * Math.sin(Math.toRadians(ra));
        float rsx = (int) rx + mWidth / 2;
        float rsy = (int) ry + mHeight / 2;
        ra = mDragRightArc.sweepAngle + mDragRightArc.startAngle;
        rx = mA * Math.cos(Math.toRadians(ra));
        ry = mB * Math.sin(Math.toRadians(ra));
        float rex = (int) rx + mWidth / 2;
        float rey = (int) ry + mHeight / 2;

        if (DEBUG_REFER_LINE) {
            debugDragRightStartX = rsx;
            debugDragRightStartY = rsy;
            debugDragRightEndX = rex;
            debugDragRightEndY = rey;
            Log.d(TAG, "dragInner right debugDragRightStartX: " + debugDragRightStartX
                    + ", debugDragRightStartY: " + debugDragRightStartY + ", debugDragRightEndX: "
                    + debugDragRightEndX + ", debugDragRightEndY: " + debugDragRightEndY
                    + " alpha: " + alpha);
        }

        if (mProgressAngle > 0) {
            int[] colors = new int[2];
            float da = mDragProgressArc.startAngle;
            double dx = mA * Math.cos(Math.toRadians(da));
            double dy = mB * Math.sin(Math.toRadians(da));
            float dsx = (int) dx + mWidth / 2;
            float dsy = (int) dy + mHeight / 2;
            da = mDragProgressArc.startAngle - mDragProgressArc.sweepAngle;
            dx = mA * Math.cos(Math.toRadians(da));
            dy = mB * Math.sin(Math.toRadians(da));
            float dex = (int) dx + mWidth / 2;
            float dey = (int) dy + mHeight / 2;
            mDragAlpha = (float) Math.max(1 - alpha, 0.4); //避免变成0
            colors[0] = adjustAlpha(mProgressGradientColors[0], mDragAlpha);
            colors[1] = adjustAlpha(mProgressGradientColors[1], mDragAlpha);
            mDragProgressGradient = new LinearGradient((int) dsx, (int) dsy, dex, dey,
                    colors, null, Shader.TileMode.CLAMP);
            if (DEBUG_REFER_LINE) {
                debugDragProcessStartX = dsx;
                debugDragProcessStartY = dsy;
                debugDragProcessEndX = dex;
                debugDragProcessEndY = dey;
                Log.d(TAG, "dragInner drag process mDragProgressArc.startAngle:" +
                        mDragProgressArc.startAngle + ", mDragProgressArc.sweepAngle: " +
                        mDragProgressArc.sweepAngle + ", mDragProgressArc.endAngle: " +
                        (mDragProgressArc.startAngle - mDragProgressArc.sweepAngle));
                Log.d(TAG, "dragInner process debugDragProcessStartX: " + debugDragProcessStartX
                        + ", debugDragProcessStartY: " + debugDragProcessStartY + ", debugDragProcessEndX: "
                        + debugDragProcessEndX + ", debugDragProcessEndY: " + debugDragProcessEndY
                        + " alpha: " + alpha);
            }
            if (DEBUG_PROCESS) {
                Log.d(TAG, "mDragProgressGradient dsx: " + dsx + ", dsy: " + dsy + ", dex: "
                        + dex + ", dey: " + dey);
            }
        }

        //如果往左拖，应该是右侧弧线有渐变，如果往右拖，应该是左侧弧线有渐变，当然正确的做法是两侧都有渐变。
        //但是由于另一侧的滑动不容易被关注到，而一侧很容易被关注到
        //所以暂时先处理一种情形。没有渐变的一侧用静止时的渐变, 所以把没有一侧的渐变对象置空。
        if (left) {
            mDragLeftLineGradient = null;
            mDragRightLineGradient = new LinearGradient((int) rsx, (int) rsy, rex, rey,
                    mDragRightArcGradientColors, null, Shader.TileMode.CLAMP);
        } else {
            mDragRightLineGradient = null;
            mDragLeftLineGradient = new LinearGradient((int) lsx, (int) lsy, lex, ley,
                    mDragLeftArcGradientColors, null, Shader.TileMode.CLAMP);
        }


        //计算旋转时控制球的坐标
        calculateDragThumbXY();
        calculateDragDurationXY(left, angle);
        invalidate();
    }

    private void calculateRotateThumbXY() {
        float arcStart = mRotateProgressArc.startAngle - mRotateProgressArc.sweepAngle;
        double x = mA * Math.cos(Math.toRadians(arcStart));
        double y = mB * Math.sin(Math.toRadians(arcStart));
        mRotateThumbX = (int) x + mWidth / 2;
        mRotateThumbY = (int) y + mHeight / 2;
        if (DEBUG_TOUCH) {
            Log.d(TAG, "calculateRotateThumbXY mRotateThumbX: " + mRotateThumbX
                    + ", mRotateThumbY: " + mRotateThumbY);
        }

        transferThumbPaintAlpha(mThumbPaint.getAlpha() - mThumbPaintAlphaStep);
    }

    private void calculateDragThumbXY() {
        float arcStart = mDragProgressArc.startAngle - mDragProgressArc.sweepAngle;
        double x = mA * Math.cos(Math.toRadians(arcStart));
        double y = mB * Math.sin(Math.toRadians(arcStart));
        mDragThumbX = (int) x + mWidth / 2;
        mDragThumbY = (int) y + mHeight / 2;
        mRotateThumbX = mDragThumbX;
        mRotateThumbY = mDragThumbY;

        if (DEBUG_PROCESS) {
            Log.d(TAG, "calculateDragThumbXY mDragThumbX: " + mDragThumbX
                    + ", mDragThumbY: " + mDragThumbY);
        }
    }

    protected void calculateDragDurationXY(boolean left, int angle) {
        int start = 0;
        if (left) {
            mDragAngle = mDragAngle + angle;
        } else {
            mDragAngle = mDragAngle - angle;
        }
        start = mStartAngle + mDragAngle;
        double x = mA * Math.cos(Math.toRadians(start));
        double y = mB * Math.sin(Math.toRadians(start));
        mDragDurationEndX = (int) x + mWidth / 2;
        mDragDurationEndY = (int) y + mHeight / 2;

        float arcStart = mDragProgressArc.startAngle;
        x = mA * Math.cos(Math.toRadians(arcStart));
        y = mB * Math.sin(Math.toRadians(arcStart));
        mDragDurationStartX = (int) x + mWidth / 2;
        mDragDurationStartY = (int) y + mHeight / 2;


        if (DEBUG_PROCESS) {
            Log.d(TAG, "calculateDragDurationXY angle: " + angle
                    + ", mDragAngle: " + mDragAngle
                    + ", mDragDurationEndX: " + mDragDurationEndX
                    + ", mDragDurationEndY: " + mDragDurationEndY);
        }
    }

    private void transferThumbPaintAlpha(int alpha) {
        if (alpha > MAX_ALPHA) {
            alpha = 0xff;
        } else if (alpha < 0) {
            alpha = 0;
        }
        mThumbPaint.setAlpha(alpha);
    }

    private void startThumbAlphaTimer() {
        transferThumbPaintAlpha(0);
        invalidate();
        new ThumbTimer(mRotateDuration, 10 / 2).start();
    }

    private void changeThumbPaintAlpha() {
        transferThumbPaintAlpha(mThumbPaint.getAlpha() + mThumbPaintAlphaStep);
        invalidate();
    }

    private void cancelRotateAnimator() {
        if (mRotateAnimator != null && mRotateAnimator.isRunning()) {
            mRotateAnimator.cancel();
        }
    }

    private void startRotateAnimation(final boolean clockwise) {
        bIsRotating = true;
        bDrawThumb = false;

        cancelRotateAnimator();

        mRotateAnimator = ValueAnimator.ofFloat(/*new MyTypeEvaluator(),*/ 0f,
                mRotateAngle - Math.abs(mDragAngle));
        mRotateAnimator.setDuration(mRotateDuration);
        mRotateAnimator.setInterpolator(new DecelerateInterpolator(2f));
        mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                MyTypeEvaluatorResult ret = (MyTypeEvaluatorResult) animation.getAnimatedValue();
                float ret = (float) animation.getAnimatedValue();
                if (ret != 0) {
                    float offset = ret - mAnimatorPreValue;
                    if (DEBUG_ANIMATION) {
                        Log.d(TAG, "onAnimationUpdate ret.value: " + ret + ", offset = " + offset);
                    }
                    mAnimatorPreValue = ret;
                    if (offset != 0) {
                        rotateInner(clockwise, offset);
                    }
                }
            }
        });
        mRotateAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //旋转结束时将状态还原
                restoreAfterRotate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mRotateAnimator.start();
    }

    protected void restoreAfterRotate() {
        bIsRotating = false;
        mAnimatorPreValue = 0;
        setProgress(0, 0);
        resetRotateInitialData();
        if (mThumbDrawRunnable != null) {
            removeCallbacks(mThumbDrawRunnable);
        }
        mThumbDrawRunnable = new Runnable() {
            @Override
            public void run() {
                bDrawThumb = true;
                startThumbAlphaTimer();
                mThumbDrawRunnable = null;
            }
        };
        postDelayed(mThumbDrawRunnable, 200);
    }

    /*
     * 计算进度球的x， y坐标，同时根据进度的长度设计进度渐变的宽度.
     *
     */
    private void calculateThumbPoint(int progress) {
        if (progress == 0 || mMaxProgress == 0 || mEndAngle - mStartAngle == 0) {
            mProgressAngle = 0;
        } else {
            mProgressAngle = (float) progress / mMaxProgress * (mEndAngle - mStartAngle);
        }
        //修改旋转弧线的sweep角度，这样拖拽开始时，控制球的初始位置和进度条上的位置一致，
        mRotateProgressArc.sweepAngle = (int) mProgressAngle;
        //修改拖拽弧线的sweep角度，这样拖拽开始时，控制球的初始位置和进度条上的位置一致，
        mDragProgressArc.sweepAngle = (int) mProgressAngle;
        //椭圆标准方程， x = a * cos(弧度), y = b * sin(弧度)，首先求角度，然后将角度转弧度
        //角度求法：以3点钟方向为x正向坐标，9点钟方向为180度，由于进度是从结束角度mEndAngle开始，
        //所以球的位置与x轴的初始夹角是mEndAngle - mProgressAngle,
        // 简单来看是180 - mEndAngle + mProgressAngle，借助余弦函数的周期性，我们可以直接写成
        //mEndAngle - mProgressAngle, 如果有进度角，这个夹角会变大进度角度数
        float arcStart = mEndAngle - mProgressAngle;
        double x = mA * Math.cos(Math.toRadians(arcStart));
        double y = mB * Math.sin(Math.toRadians(arcStart));
        //位置从布局左上为原点，上面计算出来的x， y是以椭圆圆心为原点的值，所以要用画布一半的宽度减去x
        mThumbX = (int) x + mWidth / 2; //相当于mWidth / 2 - x
        mThumbY = (int) y + mHeight / 2;
        //进度渐变
        if (mProgressAngle > 0) {
            mProgressGradient = new LinearGradient(
                    (int) mDragDurationStartX, (int) mDragDurationStartY, mThumbX, mThumbY,
                    mProgressGradientColors, null, Shader.TileMode.CLAMP);
            if (DEBUG_PROCESS) {
                Log.d(TAG, "mProgressGradient mDragDurationStartX: " + mDragDurationStartX
                        + ", mDragDurationStartY: " + mDragDurationStartY + ", mThumbX: "
                        + mThumbX + ", mThumbY: " + mThumbY);
            }
        }
        if (DEBUG_PROCESS) {
            Log.d(TAG, "calculateThumbPoint progress: " + progress
                    + ", mProgressAngle: " + mProgressAngle);
        }

        if (DEBUG_PROCESS) {
            Log.d(TAG, "calculateThumbPoint mThumbX: " + mThumbX
                    + ", mThumbY: " + mThumbY);
        }
    }

    private void updateProgress(int progress, boolean fromUser) {
        mProgress = progress;
        onProgressRefresh(fromUser, progress);
        if (mRotateProgressArc == null) {
            // View 还没有measure完成， 无法计算和绘制，所以return
            return;
        }
        calculateThumbPoint(progress);
        if (!bIsRotating && !bIsDragging) { //拖拽的时候绘制会产生跳动
            invalidate();
        }
    }

    private boolean eventDownOnThumb(float x, float y) {
        boolean onThumb = false;
        if (x >= mThumbX - mThumbRadius - touchExpendDelta
                && x <= mThumbX + mThumbRadius + touchExpendDelta
                && y >= mThumbY - mThumbRadius - touchExpendDelta
                && y <= mThumbY + mThumbRadius + touchExpendDelta) {
            onThumb = true;
        } else {
            onThumb = false;
        }
        if (DEBUG_TOUCH) {
            Log.d(TAG, "onTouchEvent: down event(" + x + ", " + y + "), thumb("
                    + mThumbX + ", " + mThumbY + "), radius: " + mThumbRadius
                    + ", onThumb: " + onThumb);
        }
        return onThumb;
    }

    private boolean eventMoveOnProgressBar(float x, float y) {
        if (DEBUG_TOUCH) {
            Log.d(TAG, "eventMoveOnProgressBar seek area: [" + mSeekRect.left + ", " + mSeekRect.top + ", "
                    + mSeekRect.right + ", " + mSeekRect.bottom + "], event: ("
                    + x + ", " + y + ")");
        }
        if (x > mSeekRect.left && x < mSeekRect.right
                && y > mSeekRect.top && y < mSeekRect.bottom) {
            return true;
        } else {
            return false;
        }
    }

    private int getSeekProgress(float x, float y) {
        float deltaX = x - mThumbX;
        float progress = deltaX / (mSeekRect.right - mSeekRect.left) * mMaxProgress;
        if (DEBUG_PROCESS) {
            Log.d(TAG, "getSeekProgress deltaX: " + deltaX + ", progress: " + progress);
        }
        return (int) progress;
    }

    private int getClickProgress(float x, float y) {
        float progress = (x - mSeekRect.left) / (mSeekRect.right - mSeekRect.left) * mMaxProgress;
        return (int) progress;
    }

    private boolean eventUp(float x, float y) {
        if (DEBUG_TOUCH) {
            Log.d(TAG, "eventUp: event(" + x + ", " + y + ")");
        }
        boolean onSeekArea = eventMoveOnProgressBar(x, y);
        boolean isClick = x - mDownX < mScaledTouchSlop && y - mDownY < mScaledTouchSlop;
        if (onSeekArea && isClick) {
            int progress = getClickProgress(x, y);
            if (DEBUG_TOUCH) {
                Log.d(TAG, "----eventUp, progress " + progress);
            }
            setProgress(progress, true);
        }
        return true;
    }

    private void initDebugPaint() {
        mDebugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setColor(Color.parseColor("#9370DB"));
        mDebugPaint.setStrokeWidth(10);
        mDebugPaint.setTextSize(22);
    }

    private void onProgressRefresh(boolean fromUser, int progress) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, progress, fromUser);
        }
    }

    @ColorInt
    private static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(OvalSeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(OvalSeekBar seekBar);

        void onStopTrackingTouch(OvalSeekBar seekBar);
    }

    protected class ArcData {
        float startAngle, sweepAngle;

        ArcData(float startAngle, float sweepAngle) {
            this.startAngle = startAngle;
            this.sweepAngle = sweepAngle;
        }

        void setValue(float startAngle, float sweepAngle) {
            this.startAngle = startAngle;
            this.sweepAngle = sweepAngle;
        }
    }

    private class ThumbTimer extends CountDownTimer {

        public ThumbTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            changeThumbPaintAlpha();
        }

        @Override
        public void onFinish() {
            mThumbPaint.setAlpha(0xff);
            invalidate();
        }
    }

    private class MyTypeEvaluator implements TypeEvaluator {
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            if (DEBUG_ANIMATION) {
                Log.d(TAG, "MyTypeEvaluator evaluate fraction : " + fraction
                        + ", startValue: " + startValue + ", endValue:" + endValue);
            }
            MyTypeEvaluatorResult ret = new MyTypeEvaluatorResult();
            float result = (1.0f - (1.0f - fraction) * (1.0f - fraction));
            float dur = (float) endValue;
            result = dur * (1 - result);
            ret.value = result;
            return ret;
        }
    }

    private class MyTypeEvaluatorResult {
        float value;
    }

}