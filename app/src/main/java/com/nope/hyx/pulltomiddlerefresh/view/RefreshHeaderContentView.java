package com.nope.hyx.pulltomiddlerefresh.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

/**
 * 下拉刷新的头部内容View
 * Created by huangyuxiang on 2017/7/13.
 */

public class RefreshHeaderContentView extends LinearLayout implements IRefreshHeaderContentView{
    private static final float PULL_OFFSET_RATIO = 1f / 3;
    private int mOriginalPaddingTop;
    private ValueAnimator mValueAnimator;

    public RefreshHeaderContentView(Context context) {
        super(context);
    }

    public RefreshHeaderContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshHeaderContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mOriginalPaddingTop = getPaddingTop();
    }

    @Override
    public void onPull(float offset) {
        int realOffset = (int) (offset * PULL_OFFSET_RATIO);
        int maxIncreaseHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics());
        if(realOffset > maxIncreaseHeight){
            realOffset = maxIncreaseHeight;
        }else if(realOffset < 0){
            realOffset = 0;
        }
        int targetHeight = realOffset + mOriginalPaddingTop;
        setPadding(getPaddingLeft(), targetHeight, getPaddingRight(), getPaddingBottom());
    }

    @Override
    public void onPullRelease() {
        if(mValueAnimator != null && mValueAnimator.isRunning()){
            return;
        }
        mValueAnimator = ValueAnimator.ofInt(getPaddingTop(), mOriginalPaddingTop);
        mValueAnimator.setDuration(200);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setPadding(getPaddingLeft(), (int) valueAnimator.getAnimatedValue(), getPaddingRight(), getPaddingBottom());
            }
        });
        mValueAnimator.start();
    }
}
