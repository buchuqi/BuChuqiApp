package jd.com.listener;

import android.view.ViewGroup;

/**
 * Created by jian_zhou on 2017/6/25.
 */

public interface Scrollable {

    void setScrollViewCallbacks(ObservableScrollViewCallbacks listener);

    //获取Y轴滑动距离
    int getCurrentScrollY();

    //设置手势拦截
    void setTouchInterceptionViewGroup(ViewGroup viewGroup);

}
