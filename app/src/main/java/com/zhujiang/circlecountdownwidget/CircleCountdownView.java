package com.zhujiang.circlecountdownwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhujiang on 2017/1/19.
 */

public class CircleCountdownView extends View {

    private static final String TAG = "CircleCountdownView";

    //圆的半径
    private float mRadius;

    //色带的宽度
    private float mStripeWidth;
    //总体大小
    private int mHeight;
    private int mWidth;

    //动画位置百分比进度
    private int mCurSeconds;

    //实际百分比进度
    //private int mPercent;
    //圆心坐标
    private float x;
    private float y;

    //要画的弧度
    private int mEndAngle;

    //小圆的颜色
    private int mSmallColor;
    //大圆颜色
    private int mBigColor;

    //中心百分比文字大小
    private float mCenterTextSize;

    public int totalSeconds;
    public int playSeconds;

    private static final int STATE_STOP = 0x1;//静止状态，可以点击
    private static final int STATE_CIRCLE_COUNT_DWON = 0x2;//圆形倒计时
    private static final int STATE_NUMBER_COUNT_DOWN = 0x3;//数字倒计时

    private int state = STATE_STOP;

    private Bitmap bm;//静止状态下的图片

    private CountDownTimer timer;

    public CircleCountdownView(Context context) {
        this(context,null);
    }

    public CircleCountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleCountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleCountdownView, defStyleAttr, 0);
        mStripeWidth = a.getDimension(R.styleable.CircleCountdownView_stripeWidth, PxUtils.dpToPx(30, context));
        mSmallColor = a.getColor(R.styleable.CircleCountdownView_smallColor,0xFFFCEBF3);
        mBigColor = a.getColor(R.styleable.CircleCountdownView_bigColor,0xffff77a4);
        mCenterTextSize = a.getDimensionPixelSize(R.styleable.CircleCountdownView_centerTextSize,PxUtils.spToPx(20,context));
        mRadius = a.getDimensionPixelSize(R.styleable.CircleCountdownView_cicleRadius,PxUtils.dpToPx(100,context));

        int drawableRes = a.getResourceId(R.styleable.CircleCountdownView_backgoundSrc,-1);

        a.recycle();

        setEnabled(false);

        if (drawableRes > 0) {
            bm = BitmapFactory.decodeResource(getResources(), drawableRes);
            mRadius = bm.getWidth()/2;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //获取测量大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            mRadius = widthSize / 2;
            x = widthSize / 2;
            y = heightSize / 2;
            mWidth = widthSize;
            mHeight = heightSize;
        }

        if(widthMode == MeasureSpec.AT_MOST&&heightMode ==MeasureSpec.AT_MOST){
            mWidth = (int) (mRadius*2);
            mHeight = (int) (mRadius*2);
            x = mRadius;
            y = mRadius;

        }

        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        switch (state) {
            case STATE_STOP:
                setEnabled(true);
                //绘制点击按钮
                Paint bigCirclePaint1 = new Paint();
                bigCirclePaint1.setAntiAlias(true);
                bigCirclePaint1.setColor(mBigColor);

                if (null != bm) {
                    canvas.drawBitmap(bm,0,0,bigCirclePaint1);
                } else {
                    canvas.drawCircle(x,y,mRadius,bigCirclePaint1);
                }

                break;
            case STATE_CIRCLE_COUNT_DWON:
                mEndAngle = (int) (((float) mCurSeconds /(float) playSeconds) * 360);
                //绘制大圆
                Paint bigCirclePaint = new Paint();
                bigCirclePaint.setAntiAlias(true);
                bigCirclePaint.setColor(mBigColor);
                canvas.drawCircle(x, y, mRadius, bigCirclePaint);

                //绘制小圆,颜色透明
                Paint smallCirclePaint = new Paint();
                smallCirclePaint.setAntiAlias(true);
                smallCirclePaint.setColor(mSmallColor);
                canvas.drawCircle(x, y, mRadius - mStripeWidth, smallCirclePaint);

                //饼状图
                Paint sectorPaint = new Paint();
                sectorPaint.setColor(mBigColor);
                sectorPaint.setAntiAlias(true);
                RectF rect = new RectF(0, 0, mWidth, mHeight);

                canvas.drawArc(rect, 270, mEndAngle, true, sectorPaint);

                break;
            case STATE_NUMBER_COUNT_DOWN:

                //绘制大圆
                Paint bigCirclePaint2 = new Paint();
                bigCirclePaint2.setAntiAlias(true);
                bigCirclePaint2.setColor(mBigColor);
                canvas.drawCircle(x, y, mRadius, bigCirclePaint2);

                //绘制文本
                Paint textPaint = new Paint();
                String text = (totalSeconds - mCurSeconds) + "s";
                textPaint.setTextSize(mCenterTextSize);
                Rect bounds = new Rect();
                textPaint.getTextBounds(text,0,text.length(),bounds);
                textPaint.setAntiAlias(true);
                textPaint.setColor(Color.WHITE);
                canvas.drawText(text, x - bounds.width()/2, y+bounds.height()/2, textPaint);
                break;
            default:

                break;
        }

    }

    /**
     * 初始化控件
     * @param coolingTime 剩余冷却时间
     */
    public void init(int totalSeconds, int playSeconds,int coolingTime) {
        this.totalSeconds = totalSeconds;
        this.playSeconds = playSeconds;

        if (playSeconds >= totalSeconds) {
            throw new IllegalArgumentException("playSeconds must less than "+totalSeconds+"!");
        }

        if (state !=STATE_STOP)
            return;
        if (coolingTime >0) {
            setCurPercent(totalSeconds,coolingTime);
        } else {
            state = STATE_STOP;
            CircleCountdownView.this.postInvalidate();
        }
    }

    /**
     * 初始化控件
     * @param coolingTime 剩余冷却时间
     */
    public void init(int coolingTime) {

        if (playSeconds >= totalSeconds) {
            throw new IllegalArgumentException("playSeconds must less than "+totalSeconds+"!");
        }

        if (state !=STATE_STOP)
            return;
        if (coolingTime >0) {
            setCurPercent(totalSeconds,coolingTime);
        } else {
            state = STATE_STOP;
            //mPercent = 0;
            CircleCountdownView.this.postInvalidate();
        }
    }

    //内部设置百分比 用于动画效果
    private void setCurPercent(final int totalSeconds, int coolingTime) {
        mCurSeconds = totalSeconds - coolingTime;
        setEnabled(false);

        //if (null == timer) {
            timer = new CountDownTimer(coolingTime*1000,1000) {
                @Override
                public void onTick(long l) {

                    mCurSeconds = totalSeconds - (int)(l/1000);

                    if (l/1000 > totalSeconds - playSeconds) {//圆圈倒计时
                        state = STATE_CIRCLE_COUNT_DWON;
                    } else {
                        state = STATE_NUMBER_COUNT_DOWN;//数字倒计时
                    }
                    CircleCountdownView.this.postInvalidate();

                }

                @Override
                public void onFinish() {
                    mCurSeconds = totalSeconds;
                    state = STATE_STOP;
                    CircleCountdownView.this.postInvalidate();
                }
            };
        //}
        timer.start();
    }

    public void onResume() {
        if(state == STATE_STOP)
            return;
        //if (null == timer) {
            timer = new CountDownTimer((totalSeconds - mCurSeconds)*1000,1000) {
                @Override
                public void onTick(long l) {

                    mCurSeconds = totalSeconds - (int)(l/1000);

                    if (l/1000 > totalSeconds - playSeconds) {//圆圈倒计时
                        state = STATE_CIRCLE_COUNT_DWON;
                    } else {
                        state = STATE_NUMBER_COUNT_DOWN;//数字倒计时
                    }
                    CircleCountdownView.this.postInvalidate();

                }

                @Override
                public void onFinish() {
                    mCurSeconds = totalSeconds;
                    state = STATE_STOP;
                    CircleCountdownView.this.postInvalidate();
                }
            };
            timer.start();
        //}
    }

    public void onPause() {
        timer.cancel();
        timer = null;
    }

    public void onDestroy() {
        if (!bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        state = STATE_STOP;
        timer.cancel();
        timer = null;
    }

}
