package com.nope.hyx.pulltomiddlerefresh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

/**
 * 下拉刷新View，刷新效果在中间位置
 * Created by huangyuxiang on 2017/7/9.
 */

public class PullToMiddleRefreshListView extends ListView {
    private float mStartY = -1;
    private float mLastY = -1;
    private OnRefreshListener mOnRefreshListener;
    private IRefreshView mRefreshView;
    private IRefreshHeaderContentView mHeaderContentView;
    private boolean isOnDragToRefresh;
    private int mTouchSlop;

    private boolean isPullToRefreshEnable = true;

    public PullToMiddleRefreshListView(Context context) {
        super(context);
        init();
    }

    public PullToMiddleRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToMiddleRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() / 4;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!isPullToRefreshEnable){
            return super.onTouchEvent(ev);
        }
        if (!isToTop() || isRefreshing()) { //未到顶部，或者正在刷新
            isOnDragToRefresh = false;
            mLastY = ev.getY();
            mStartY = mLastY;
            return super.onTouchEvent(ev);
        }
        //仅处理列表已到顶部的情况
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOnDragToRefresh = false;
                mLastY = ev.getY();
                mStartY = mLastY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                //fix adapter里给item设置了点击监听，DOWN事件会被吃的问题
                if(mStartY == -1){
                    mStartY = currentY;
                }
                if(mLastY == -1){
                    mLastY = currentY;
                }
                //是否往下拉
                boolean isOverTouchSlop = Math.abs(currentY - mStartY) > mTouchSlop;
                boolean isDragDown = currentY - mLastY > 0;
                if ((isOverTouchSlop && isDragDown) || mRefreshView.isEyeVisible()) {
                    isOnDragToRefresh = true;
                    float offset = currentY - mStartY;
                    if(mHeaderContentView != null){
                        mHeaderContentView.onPull(offset);
                    }
                    mRefreshView.onPull(offset);
                }

                mLastY = currentY;
                if(isOnDragToRefresh){
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    return super.onTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
                mStartY = -1;
                mLastY = -1;
                if(isOnDragToRefresh){
                    if (mRefreshView.isCanReleaseToRefresh()) {
                        setRefreshing();
                        if(mOnRefreshListener != null){
                            mOnRefreshListener.onRefresh();
                        }
                    }else{
                        mRefreshView.animateToInitialState();
                    }
                    if(mHeaderContentView != null){
                        mHeaderContentView.onPullRelease();
                    }
                    //不触发点击事件
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    return super.onTouchEvent(ev);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 是否已经到顶部
     */
    private boolean isToTop(){
        if(getFirstVisiblePosition() == 0 && getChildCount() > 0){
            View firstChildView = getChildAt(0);

            if(firstChildView != null){
                return firstChildView.getTop() >= 0;
            }
        }
        return false;
    }

    /**
     * 设置刷新效果View
     */
    public void setRefreshView(IRefreshView mRefreshView) {
        this.mRefreshView = mRefreshView;
    }

    public interface OnRefreshListener{
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener mOnRefreshListener) {
        this.mOnRefreshListener = mOnRefreshListener;
    }

    public boolean isRefreshing(){
        return mRefreshView.isRefreshing();
    }

    public void setRefreshComplete(String updateTips){
        mRefreshView.setRefreshComplete(updateTips);
    }

    public void setRefreshComplete(){
        mRefreshView.animateToInitialState();
    }

    public void setRefreshing(){
        mRefreshView.setRefreshing();
    }

    public void setHeaderContentView(IRefreshHeaderContentView headerContentView) {
        mHeaderContentView = headerContentView;
    }

    public void setPullToRefreshEnable(boolean pullToRefreshEnable) {
        isPullToRefreshEnable = pullToRefreshEnable;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRefreshView.stopAllAnimation();
    }
}
