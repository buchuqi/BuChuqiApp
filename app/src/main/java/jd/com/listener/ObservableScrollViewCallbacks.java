package jd.com.listener;

/**
 * Created by jian_zhou on 2017/6/25.
 */

public interface ObservableScrollViewCallbacks {
    /**
     * 滑动过程监听
     *
     * @param scrollY    当前滑动位置
     * @param firstScoll 是否第一次滑动
     * @param dragging   是否滑动中
     */
    void onScrollChanged(int scrollY, boolean firstScoll, boolean dragging);

    //当按下事件发生时候调用此方法
    void onDownMotionEvent();

    //滑动的状态
    void onUpOrCancleMotionEvent(ScrollState scrollState);
}
