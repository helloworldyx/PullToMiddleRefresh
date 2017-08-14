package com.nope.hyx.pulltomiddlerefresh.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.nope.hyx.pulltomiddlerefresh.R;


/**
 * 两个小球旋转动画View
 */
public class BallLoadingView extends View {
    private static final float BIG_RADIUS_DP_VALUE = 3.5f;

    private ValueAnimator mRotateValueAnimator;

    private float mBigRadius;
    private Status mStatus;
    private float mProgress;

    private Paint mPaint;

    enum Status{
        LEAVE_TRANSLATE, //分离
        ROTATE  //旋转
    }

    public BallLoadingView(Context context) {
        this(context, null);
        init();
    }

    public BallLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public BallLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        initValue();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getContext().getResources().getColor(R.color.red_b));
    }

    private void initValue() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mBigRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BIG_RADIUS_DP_VALUE, displayMetrics);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        int centerX = width / 2;
        int centerY = height / 2;
        canvas.save();

        float radius = mBigRadius;
        float margin = 0;
        if(mStatus == Status.LEAVE_TRANSLATE){
            radius = mBigRadius * (1 - mProgress * 0.35f);
            margin = (height - radius * 4) / 2 * mProgress;
        }else if(mStatus == Status.ROTATE){
            radius = mBigRadius * 0.65f;
            margin = (height - radius * 4) / 2;
            canvas.rotate(360 * mProgress, centerX, centerY);
        }
        canvas.drawCircle(centerX, centerY - margin, radius, mPaint);
        canvas.drawCircle(centerX, centerY + margin, radius, mPaint);

        canvas.restore();
    }

    /**
     * 设置progress
     * @param status 当前状态
     * @param progress 对应状态的进度
     */
    public void setProgress(Status status, float progress){
        this.mStatus = status;
        this.mProgress = progress;
        invalidate();
    }

    public Status getStatus() {
        return mStatus;
    }

    public float getProgress() {
        return mProgress;
    }

    /**
     * 开始旋转动画
     * @param duration 时长
     */
    public void startRotateAnimation(int duration) {
        stopRotateAnimation();
        if(mStatus != Status.ROTATE){
            mStatus = Status.ROTATE;
            mProgress = 0;
        }
        mRotateValueAnimator = ValueAnimator.ofFloat(mProgress, -1 + mProgress);
        mRotateValueAnimator.setDuration(duration);
        mRotateValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRotateValueAnimator.setInterpolator(new LinearInterpolator());
        mRotateValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mProgress = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mRotateValueAnimator.start();
    }

    public void stopRotateAnimation(){
        if(mRotateValueAnimator != null && mRotateValueAnimator.isRunning()){
            mRotateValueAnimator.cancel();
        }
    }
}

