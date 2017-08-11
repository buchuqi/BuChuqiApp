package jd.com.library.weight.refresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import jd.com.library.R;


/**
 * 下拉刷新，上拉加载
 */
public class MaterialRefreshLayout extends FrameLayout {

    /**
     * 是否是要执行下拉刷新上拉加载
     */
    public boolean wantRefresh = false;
    public static final String Tag = "cjj_log";
    protected float mWaveHeight;
    protected float mHeadHeight;
    protected boolean isRefreshing;
    protected FrameLayout mHeadLayout;
    private boolean isOverlay;
    private boolean isShowWave;
    private boolean isLoadMoreing;
    private boolean isLoadMore;
    private boolean isRefresh;
    private boolean showArrow;
    private boolean showProgressBg;
    private float headHeight;
    private float mTouchY;
    private float mTouchX;
    private float mCurrentY;
    private float waveHeight;
    private int colorsId;
    private int progressTextColor;
    private int progressValue, progressMax;
    private int progressBg;
    private int progressSizeType;
    private int textType;
    private int waveType;
    private int waveColor;
    private int[] colorSchemeColors;
    private int BIG_PROGRESS_SIZE = 60;
    private int DEFAULT_HEAD_HEIGHT = 70;
    private int DEFAULT_WAVE_HEIGHT = 140;
    private int DEFAULT_PROGRESS_SIZE = 50;
    private int HIGHER_WAVE_HEIGHT = 180;
    private int hIGHER_HEAD_HEIGHT = 100;
    private int progressSize = 0;
    private int PROGRESS_STOKE_WIDTH = 3;
    private DecelerateInterpolator decelerateInterpolator;
    private MaterialHeadView materialHeadView;
    private MaterialFoodView materialFoodView;
    private MaterialRefreshListener refreshListener;
    private View mChildView;

    public MaterialRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public MaterialRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defstyleAttr) {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("can only have one child widget");
        }

        decelerateInterpolator = new DecelerateInterpolator(10);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MaterialRefreshLayout, defstyleAttr, 0);
        isOverlay = t.getBoolean(R.styleable.MaterialRefreshLayout_overlay, false);
        /**attrs for materialWaveView*/
        waveType = t.getInt(R.styleable.MaterialRefreshLayout_wave_height_type, 0);
        if (waveType == 0) {
            headHeight = DEFAULT_HEAD_HEIGHT;
            waveHeight = DEFAULT_WAVE_HEIGHT;
            MaterialWaveView.DefaulHeadHeight = DEFAULT_HEAD_HEIGHT;
            MaterialWaveView.DefaulWaveHeight = DEFAULT_WAVE_HEIGHT;
        } else {
            headHeight = hIGHER_HEAD_HEIGHT;
            waveHeight = HIGHER_WAVE_HEIGHT;
            MaterialWaveView.DefaulHeadHeight = hIGHER_HEAD_HEIGHT;
            MaterialWaveView.DefaulWaveHeight = HIGHER_WAVE_HEIGHT;
        }
        waveColor = t.getColor(R.styleable.MaterialRefreshLayout_wave_color, Color.WHITE);
        isShowWave = t.getBoolean(R.styleable.MaterialRefreshLayout_wave_show, true);

        /**attrs for circleprogressbar*/
        colorsId = t.getResourceId(R.styleable.MaterialRefreshLayout_progress_colors, R.array.material_colors);
        colorSchemeColors = context.getResources().getIntArray(colorsId);
        showArrow = t.getBoolean(R.styleable.MaterialRefreshLayout_progress_show_arrow, true);
        textType = t.getInt(R.styleable.MaterialRefreshLayout_progress_text_visibility, 1);
        progressTextColor = t.getColor(R.styleable.MaterialRefreshLayout_progress_text_color, Color.BLACK);
        progressValue = t.getInteger(R.styleable.MaterialRefreshLayout_progress_value, 0);
        progressMax = t.getInteger(R.styleable.MaterialRefreshLayout_progress_max_value, 100);
        showProgressBg = t.getBoolean(R.styleable.MaterialRefreshLayout_progress_show_circle_backgroud, true);
        progressBg = t.getColor(R.styleable.MaterialRefreshLayout_progress_backgroud_color, CircleProgressBar.DEFAULT_CIRCLE_BG_LIGHT);
        progressSizeType = t.getInt(R.styleable.MaterialRefreshLayout_progress_size_type, 0);
        if (progressSizeType == 0) {
            progressSize = DEFAULT_PROGRESS_SIZE;
        } else {
            progressSize = BIG_PROGRESS_SIZE;
        }
        isLoadMore = t.getBoolean(R.styleable.MaterialRefreshLayout_isLoadMore, false);
        isRefresh = t.getBoolean(R.styleable.MaterialRefreshLayout_isRefresh, true);
        t.recycle();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Context context = getContext();

        FrameLayout headViewLayout = new FrameLayout(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.TOP;
        headViewLayout.setLayoutParams(layoutParams);

        mHeadLayout = headViewLayout;

        this.addView(mHeadLayout);

        mChildView = getChildAt(0);

        if (mChildView == null) {
            return;
        }

        setWaveHeight(Util.dip2px(context, waveHeight));
        setHeaderHeight(Util.dip2px(context, headHeight));

        materialHeadView = new MaterialHeadView(context);
        materialHeadView.setWaveColor(isShowWave ? waveColor : Color.WHITE);
        materialHeadView.showProgressArrow(showArrow);
        materialHeadView.setProgressSize(progressSize);
        materialHeadView.setProgressColors(colorSchemeColors);
        materialHeadView.setProgressStokeWidth(PROGRESS_STOKE_WIDTH);
        materialHeadView.setTextType(textType);
        materialHeadView.setProgressTextColor(progressTextColor);
        materialHeadView.setProgressValue(progressValue);
        materialHeadView.setProgressValueMax(progressMax);
        materialHeadView.setIsProgressBg(showProgressBg);
        materialHeadView.setProgressBg(progressBg);
        setHeaderView(materialHeadView);

        materialFoodView = new MaterialFoodView(context);
        LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dip2px(context, hIGHER_HEAD_HEIGHT));
        layoutParams2.gravity = Gravity.BOTTOM;
        materialFoodView.setLayoutParams(layoutParams2);
        materialFoodView.showProgressArrow(showArrow);
        materialFoodView.setProgressSize(progressSize);
        materialFoodView.setProgressColors(colorSchemeColors);
        materialFoodView.setProgressStokeWidth(PROGRESS_STOKE_WIDTH);
        materialFoodView.setTextType(textType);
        materialFoodView.setProgressValue(progressValue);
        materialFoodView.setProgressValueMax(progressMax);
        materialFoodView.setIsProgressBg(showProgressBg);
        materialFoodView.setProgressBg(progressBg);
        materialFoodView.setVisibility(View.GONE);
        setFooderView(materialFoodView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isRefreshing) return true;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mTouchX = ev.getX();
                mCurrentY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                float currentX = ev.getX();
                float dy = currentY - mTouchY;
                float dx = currentX - mTouchX;

                //此处判断如果是X方向的滑动距离的绝对值较大的话就视为非上下拉，不拦截触摸事件
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (wantRefresh) {
                        if (Math.abs(dx - dy) < 5) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }

                if (dy > 15 && !canChildScrollUp()) {
                    if (materialHeadView != null) {
                        materialHeadView.setVisibility(VISIBLE);
                        materialHeadView.onBegin(this);
                    }
                    return true;
                } else if (dy < 0 && !canChildScrollDown() && isLoadMore && Math.abs(dy) > Math.abs(dx)) {
                    if (materialFoodView != null && !isLoadMoreing) {
                        soveLoadMoreLogic();
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                /*if (isSwipeRemove) {
                    if (wantRefresh) {
                        //此处判断如果是X方向的滑动距离的绝对值较大的话就视为非上下拉，不拦截触摸事件
                        if (Math.abs(dx) < 5) {
                            return super.onInterceptTouchEvent(ev);
                        } else {
                            return true;
                        }
                    }
                }*/
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void soveLoadMoreLogic() {
        isLoadMoreing = true;
        materialFoodView.setVisibility(View.VISIBLE);
        materialFoodView.onBegin(this);
        materialFoodView.onRefreshing(this);
        if (refreshListener != null) {
            refreshListener.onRefreshLoadMore(MaterialRefreshLayout.this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isRefreshing) {
            return super.onTouchEvent(e);
        }

        if (!isRefresh) {
            return super.onTouchEvent(e);
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = e.getY();
                float dy = mCurrentY - mTouchY;
                dy = Math.min(mWaveHeight * 2, dy);
                dy = Math.max(0, dy);
                if (mChildView != null) {
                    float offsetY = decelerateInterpolator.getInterpolation(dy / mWaveHeight / 2) * dy / 2;
                    float fraction = offsetY / mHeadHeight;
                    mHeadLayout.getLayoutParams().height = (int) offsetY;
                    mHeadLayout.requestLayout();

                    if (refreshListener!=null){
                        refreshListener.onHeaderTopHeight(mHeadLayout.getLayoutParams().height);
                    }
                    if (materialHeadView != null) {
                        materialHeadView.onPull(this, fraction);

                    }
                    if (!isOverlay)
                        ViewCompat.setTranslationY(mChildView, offsetY);

                }
                return true;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mChildView != null) {
                    if (isOverlay) {
                        if (mHeadLayout.getLayoutParams().height > mHeadHeight) {
                            updateListener();

                            mHeadLayout.getLayoutParams().height = (int) mHeadHeight;
                            mHeadLayout.requestLayout();
                        } else {
                            mHeadLayout.getLayoutParams().height = 0;
                            mHeadLayout.requestLayout();
                        }
                    } else {
                        if (ViewCompat.getTranslationY(mChildView) >= mHeadHeight) {
                            createAnimatorTranslationY(mChildView, mHeadHeight, mHeadLayout);
                            updateListener();
                        } else {
                            createAnimatorTranslationY(mChildView, 0, mHeadLayout);
                        }
                    }

                }
                return true;
        }
        return super.onTouchEvent(e);
    }

    public void autoRefresh() {
        if (!isRefreshing) {
            updateListener();
            if (isOverlay) {
                mHeadLayout.getLayoutParams().height = (int) mHeadHeight;
                mHeadLayout.requestLayout();
            } else {
                createAnimatorTranslationY(mChildView, mHeadHeight, mHeadLayout);
            }
        }
    }

    public void autoRefreshLoadMore() {
        if (isLoadMore) {
            soveLoadMoreLogic();
        } else {
            if (refreshListener != null) {
                refreshListener.onRefreshLoadMore(MaterialRefreshLayout.this);
            }
        }
    }

    public void updateListener() {
        isRefreshing = true;

        if (materialHeadView != null) {
            materialHeadView.onRefreshing(MaterialRefreshLayout.this);
        }

        if (refreshListener != null) {
            refreshListener.onRefresh(MaterialRefreshLayout.this);
        }
    }

    public void setLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public void setRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public void setProgressColors(int[] colors) {
        this.colorSchemeColors = colors;
    }

    public void setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
    }

    public void setShowProgressBg(boolean showProgressBg) {
        this.showProgressBg = showProgressBg;
    }

    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
    }

    public void setWaveShow(boolean isShowWave) {
        this.isShowWave = isShowWave;
    }

    public void setIsOverLay(boolean isOverLay) {
        this.isOverlay = isOverLay;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
        materialHeadView.setProgressValue(progressValue);
    }

    public void createAnimatorTranslationY(final View v, final float h, final FrameLayout fl) {
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(v);
        viewPropertyAnimatorCompat.setDuration(200);
        viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
        viewPropertyAnimatorCompat.translationY(h);
        viewPropertyAnimatorCompat.start();
        viewPropertyAnimatorCompat.setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(View view) {
                float height = ViewCompat.getTranslationY(v);
                fl.getLayoutParams().height = (int) height;
                fl.requestLayout();
            }
        });
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, -1);
        }
    }

    public boolean canChildScrollDown() {
        if (mChildView == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                if (absListView.getChildCount() > 0) {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1).getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1 && lastChildBottom <= absListView.getMeasuredHeight();
                } else {
                    return false;
                }
            } else {
                return ViewCompat.canScrollVertically(mChildView, 1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, 1);
        }
    }

    public void setWaveHigher() {
        headHeight = hIGHER_HEAD_HEIGHT;
        waveHeight = HIGHER_WAVE_HEIGHT;
        MaterialWaveView.DefaulHeadHeight = hIGHER_HEAD_HEIGHT;
        MaterialWaveView.DefaulWaveHeight = HIGHER_WAVE_HEIGHT;
    }

    public void finishRefreshing() {
        if (mChildView != null) {
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(mChildView);
            viewPropertyAnimatorCompat.setDuration(200);
            viewPropertyAnimatorCompat.y(ViewCompat.getTranslationY(mChildView));
            viewPropertyAnimatorCompat.translationY(0);
            viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
            viewPropertyAnimatorCompat.start();

            if (materialHeadView != null) {
                materialHeadView.setVisibility(GONE);
                materialHeadView.onComlete(MaterialRefreshLayout.this);
            }

            if (refreshListener != null) {
                refreshListener.onfinish();
            }
        }
        isRefreshing = false;
        progressValue = 0;
        setProgressValue(0);
    }

    public void finishRefresh() {
        this.post(new Runnable() {
            @Override
            public void run() {
                finishRefreshing();
            }
        });
    }

    public void isOver() {
        finishRefresh();
        finishRefreshLoadMore();
    }

    public void finishRefreshLoadMore() {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (materialFoodView != null && isLoadMoreing) {
                    isLoadMoreing = false;
                    materialFoodView.setVisibility(GONE);
                    materialFoodView.onComlete(MaterialRefreshLayout.this);
                    if (refreshListener != null) {
                        refreshListener.onfinish();
                    }
                }
            }
        });
    }

    public void setHeaderView(final View headerView) {
        post(new Runnable() {
            @Override
            public void run() {
                mHeadLayout.addView(headerView);
            }
        });
    }

    public void setFooderView(final View fooderView) {
        this.addView(fooderView);
    }

    public void setWaveHeight(float waveHeight) {
        this.mWaveHeight = waveHeight;
    }

    public void setHeaderHeight(float headHeight) {
        this.mHeadHeight = headHeight;
    }

    public void setMaterialRefreshListener(MaterialRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    private ListView livew;

    public void setMaterialRefreshListener(MaterialRefreshListener refreshListener, ListView livew) {
        this.livew = livew;
        this.refreshListener = refreshListener;
        livew.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        autoRefreshLoadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

}