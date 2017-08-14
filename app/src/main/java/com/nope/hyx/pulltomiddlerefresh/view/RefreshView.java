package com.nope.hyx.pulltomiddlerefresh.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nope.hyx.pulltomiddlerefresh.R;


/**
 * 刷新效果View
 * Created by huangyuxiang on 2017/7/9.
 */

public class RefreshView extends RelativeLayout implements IRefreshView {

    //下拉和加载中View
    private ViewGroup mLoadingTipsLayout;
    private TextView tvLoadingTips;
    private BallLoadingView mBallLoadingView;
    //加载完成View
    private RelativeLayout mCompleteTipsLayout;
    private ImageView ivCompleteBg;
    private TextView tvCompleteTips;

    private ValueAnimator mHeightValueAnimator;
    private ValueAnimator mCompleteBgWidthValueAnimator;
    private ObjectAnimator mCompleteAlphaAnimator;
    private ValueAnimator mBallAlphaValueAnimator;

    //刷新中固定的高度
    private int mRefreshingHeight;
    //刷新中圆圈开始旋转时的高度
    private int mCircleStartRotateHeight;
    //大于这个高度松手可以触发刷新的高度
    private int mReleaseToRefreshHeight;
    //刷新完成背景展开宽度
    private int mCompleteBgStartWidth;
    //下拉位移比例
    private float mPullRatio = 2f / 3;
    //是否正在刷新
    private boolean isRefreshing;
    //是否能够下拉刷新
    private boolean isRefreshEnable = true;
    //每一个旋转角度对应多少个下拉位移
    private float mPerDegreePullOffset;
    //起始高度
    private int mOriginalHeight;

    public RefreshView(Context context) {
        super(context);
        init();
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initValue();
        initLayout();
    }

    private void initLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_middle_refresh_view, this);
        mLoadingTipsLayout = (ViewGroup) findViewById(R.id.ll_loading_tips);
        tvLoadingTips = (TextView) findViewById(R.id.tv_loading_tips);
        mBallLoadingView = (BallLoadingView) findViewById(R.id.ball_loading_view);

        mCompleteTipsLayout = (RelativeLayout) findViewById(R.id.rl_complete_tips);
        ivCompleteBg = (ImageView) findViewById(R.id.iv_complete_bg);
        tvCompleteTips = (TextView) findViewById(R.id.tv_complete_tips);
    }

    private void initValue() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        final int REFRESHING_HEIGHT_DP_VALUE = 20;
        mRefreshingHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, REFRESHING_HEIGHT_DP_VALUE, displayMetrics) + mOriginalHeight;
        int CIRCLE_ROTATE_HEIGHT_DP_VALUE = 40;
        mCircleStartRotateHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_ROTATE_HEIGHT_DP_VALUE, displayMetrics) + mOriginalHeight;
        final int RELEASE_TO_REFRESH_HEIGHT_DP_VALUE = 60;
        mReleaseToRefreshHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RELEASE_TO_REFRESH_HEIGHT_DP_VALUE, displayMetrics) + mOriginalHeight;
        final int COMPLETE_BG_START_WIDTH_DP_VALUE = 20;
        mCompleteBgStartWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, COMPLETE_BG_START_WIDTH_DP_VALUE, displayMetrics) + mOriginalHeight;

        mPerDegreePullOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.3f, displayMetrics);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int height = MeasureSpec.makeMeasureSpec(layoutParams.height >= 0 ? layoutParams.height : mOriginalHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, height);
    }

    @Override
    public boolean isEyeVisible() {
        return getHeight() > mOriginalHeight && getVisibility() == VISIBLE;
    }

    @Override
    public boolean isCanReleaseToRefresh() {
        return getHeight() >= mReleaseToRefreshHeight;
    }

    @Override
    public void setRefreshing() {
        if(isRefreshing){
            return;
        }
        isRefreshing = true;

        mCompleteTipsLayout.setAlpha(0);
        mLoadingTipsLayout.setAlpha(1);

        stopAllAnimation();
        mHeightValueAnimator = getToRefreshingHeightValueAnimator();
        mHeightValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mBallLoadingView.setAlpha(1);
                mBallLoadingView.startRotateAnimation(500);
            }
        });
        mHeightValueAnimator.start();

        tvLoadingTips.setAlpha(1);
        tvLoadingTips.setText(getContext().getString(R.string.pull_to_refreshing_tips));
    }

    @Override
    public boolean isRefreshing() {
        return isRefreshing;
    }

    @Override
    public void setRefreshComplete(final String updateTips) {
        if(!isRefreshing){
            return;
        }

        stopAllAnimation();

        //先把高度还原为正在刷新的高度
        mHeightValueAnimator = getToRefreshingHeightValueAnimator();
        mHeightValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                mBallAlphaValueAnimator = ValueAnimator.ofFloat(1.0f, 0f);
                mBallAlphaValueAnimator.setDuration(125);
                mBallAlphaValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mLoadingTipsLayout.setAlpha((float) valueAnimator.getAnimatedValue());
                    }
                });
                mBallAlphaValueAnimator.start();

                mCompleteTipsLayout.setAlpha(1);
                tvCompleteTips.setText(updateTips);
                mCompleteBgWidthValueAnimator = ValueAnimator.ofInt(mCompleteBgStartWidth, getWidth());
                mCompleteBgWidthValueAnimator.setDuration(160);
                mCompleteBgWidthValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mCompleteTipsLayout.setAlpha(valueAnimator.getAnimatedFraction());
                        int width = (int) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = ivCompleteBg.getLayoutParams();
                        layoutParams.width = width;
                        ivCompleteBg.requestLayout();
                    }
                });
                mCompleteBgWidthValueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        try {
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isRefreshing = false;
                                    //上移恢复到未下拉状态
                                    animateToInitialState();
                                    //透明度1->0
                                    mCompleteAlphaAnimator = ObjectAnimator.ofFloat(mCompleteTipsLayout, "alpha", 1f, 0f);
                                    mCompleteAlphaAnimator.setDuration(280);
                                    mCompleteAlphaAnimator.start();
                                }
                            }, 800);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mCompleteBgWidthValueAnimator.start();
            }
        });
        mHeightValueAnimator.start();
    }

    private ValueAnimator getToRefreshingHeightValueAnimator(){
        int duration = getHeight() == mRefreshingHeight ? 0 : 200;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(getHeight(), mRefreshingHeight);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float height = (int) valueAnimator.getAnimatedValue();
                setViewHeight(RefreshView.this, height);
            }
        });
        return valueAnimator;
    }


    @Override
    public void animateToInitialState() {
        stopAllAnimation();

        int currentHeight = getHeight();
        int duration = 400;
        if(currentHeight < mRefreshingHeight){
            duration = (int) ((currentHeight * 1.0f / mRefreshingHeight) * duration);
        }
        mHeightValueAnimator = ValueAnimator.ofFloat(currentHeight, mOriginalHeight);
        mHeightValueAnimator.setDuration(duration);
        mHeightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float height = (float) valueAnimator.getAnimatedValue();
                setViewHeight(RefreshView.this, height);
                handleLoadingView(height);
            }
        });
        mHeightValueAnimator.start();
    }

    public void stopAllAnimation(){
        if(mBallAlphaValueAnimator != null && mBallAlphaValueAnimator.isRunning()){
            mBallAlphaValueAnimator.cancel();
        }
        mBallLoadingView.stopRotateAnimation();
        if(mCompleteBgWidthValueAnimator != null && mCompleteBgWidthValueAnimator.isRunning()){
            mCompleteBgWidthValueAnimator.cancel();
        }
        if(mHeightValueAnimator != null && mHeightValueAnimator.isRunning()){
            mHeightValueAnimator.cancel();
        }
        if(mCompleteAlphaAnimator != null && mCompleteAlphaAnimator.isRunning()){
            mCompleteAlphaAnimator.cancel();
        }
    }

    @Override
    public void onPull(float offset) {
        if(!isRefreshEnable){
            return;
        }
        stopAllAnimation();
        mCompleteTipsLayout.setAlpha(0);
        mLoadingTipsLayout.setAlpha(1);

        float height = offset * mPullRatio + mOriginalHeight;
        if(height < mOriginalHeight){
            height = mOriginalHeight;
        }
        //设置当前View高度
        setViewHeight(this, height);
        //处理小球和文字
        handleLoadingView(height);
    }

    /**
     * 处理小球加载效果
     */
    private void handleLoadingView(float height){
        if(height <= mRefreshingHeight){
            mBallLoadingView.setAlpha((height -  mOriginalHeight) / (mRefreshingHeight - mOriginalHeight));
            mBallLoadingView.setProgress(BallLoadingView.Status.LEAVE_TRANSLATE, 0);
        }else{
            mBallLoadingView.setAlpha(1);
            if(height <= mCircleStartRotateHeight){
                mBallLoadingView.setProgress(BallLoadingView.Status.LEAVE_TRANSLATE, (height - mRefreshingHeight) / (mCircleStartRotateHeight - mRefreshingHeight));
            }else{
                mBallLoadingView.setProgress(BallLoadingView.Status.ROTATE, (height - mCircleStartRotateHeight) / mPerDegreePullOffset / 360);
            }
        }
        //处理文字透明度和内容
        if(height <= mReleaseToRefreshHeight){
            tvLoadingTips.setText(getContext().getString(R.string.pull_to_refresh_guide_tips));
            tvLoadingTips.setAlpha((height - mOriginalHeight)/ (mReleaseToRefreshHeight - mOriginalHeight));
        }else{
            tvLoadingTips.setText(getContext().getString(R.string.release_to_refresh_guide_tips));
            tvLoadingTips.setAlpha(1);
        }
    }

    /**
     * 设置View指定高度
     */
    private void setViewHeight(View view, float height){
        if(view != null && view.getLayoutParams() != null){
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if(layoutParams.height != (int) height){
                layoutParams.height = (int) height;
                view.requestLayout();
            }
        }
    }

    /**
     * 设置下拉位移和高度变化比例
     */
    public void setPullRatio(float pullRatio) {
        mPullRatio = pullRatio;
    }

    /**
     * 设置是否能够下拉刷新
     */
    public void setRefreshEnable(boolean refreshEnable) {
        isRefreshEnable = refreshEnable;
    }

    public void setOriginalHeight(int originalHeight) {
        mOriginalHeight = originalHeight;
        initValue();
    }
}
