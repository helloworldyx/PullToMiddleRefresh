package com.nope.hyx.pulltomiddlerefresh.view;

/**
 * Created by huangyuxiang on 2017/7/9.
 */

public interface IRefreshView extends IPullCallback {
    /**
     * 是否是肉眼可见的
     */
    boolean isEyeVisible();

    /**
     * 当前状态松手是否会进行刷新
     */
    boolean isCanReleaseToRefresh();

    /**
     * 设置正在刷新
     */
    void setRefreshing();

    /**
     * 是否正在刷新
     */
    boolean isRefreshing();

    /**
     * 设置刷新完毕
     */
    void setRefreshComplete(String updateTips);

    /**
     * 运动至未下拉时的状态
     */
    void animateToInitialState();

    /**
     * 停止所有动画
     */
    void stopAllAnimation();
}
