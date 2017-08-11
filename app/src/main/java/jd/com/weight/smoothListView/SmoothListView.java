package jd.com.weight.smoothListView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Scroller;

public class SmoothListView extends ListView implements OnScrollListener {

    private OnScrollListener mScrollListener; // user's scroll listener

    /**
     * @param context
     */
    public SmoothListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public SmoothListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public SmoothListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);
    }


    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // send to user's listener
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    private float mLastY = -1; // save event y

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0
                        && deltaY > 0) {
                    // the first item is showing, header has shown or pull down.
                    invokeOnScrolling();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnSmoothScrollListener) {
            OnSmoothScrollListener l = (OnSmoothScrollListener) mScrollListener;
            l.onSmoothScrolling(this);
        }
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onSmoothScrolling when header/footer scroll back.
     */
    public interface OnSmoothScrollListener extends OnScrollListener {
        void onSmoothScrolling(View view);
    }


    private Scroller mScroller; // used for scroll back

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }
}
