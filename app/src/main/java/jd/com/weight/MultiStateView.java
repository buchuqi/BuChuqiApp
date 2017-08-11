package jd.com.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jd.com.library.R;


/**
 * 请求网络加载数据的状态
 */
public class MultiStateView extends FrameLayout {

    private LayoutInflater mInflater;
    private View mContentView;
    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;
    private Activity mActivity;
    private boolean mAnimateViewChanges = false;
    @ViewState
    private int mViewState = VIEW_STATE_UNKNOWN;
    public static final int VIEW_STATE_UNKNOWN = -1;
    public static final int VIEW_STATE_CONTENT = 0;
    public static final int VIEW_STATE_ERROR = 1;
    public static final int VIEW_STATE_EMPTY = 2;
    public static final int VIEW_STATE_LOADING = 3;
    public static final int VIEW_STATE_LOGIN_AGAIN = 10000;
    public static final int MEDICINE_TASK_OVER = 1203;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VIEW_STATE_UNKNOWN, VIEW_STATE_CONTENT, VIEW_STATE_ERROR, VIEW_STATE_EMPTY, VIEW_STATE_LOADING})
    public @interface ViewState {
    }

    public MultiStateView(Context context) {
        this(context, null);
    }

    public MultiStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mActivity = (Activity) context;
        init(attrs);
    }

    public MultiStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mActivity = (Activity) context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mInflater = LayoutInflater.from(getContext());
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MultiStateView);

        int loadingViewResId = a.getResourceId(R.styleable.MultiStateView_msv_loadingView, -1);
        if (loadingViewResId > -1) {
            mLoadingView = mInflater.inflate(loadingViewResId, this, false);
            addView(mLoadingView, mLoadingView.getLayoutParams());
        }

        int emptyViewResId = a.getResourceId(R.styleable.MultiStateView_msv_emptyView, -1);
        if (emptyViewResId > -1) {
            mEmptyView = mInflater.inflate(emptyViewResId, this, false);
            addView(mEmptyView, mEmptyView.getLayoutParams());
        }

        int errorViewResId = a.getResourceId(R.styleable.MultiStateView_msv_errorView, -1);
        if (errorViewResId > -1) {
            mErrorView = mInflater.inflate(errorViewResId, this, false);
            addView(mErrorView, mErrorView.getLayoutParams());
        }

        int viewState = a.getInt(R.styleable.MultiStateView_msv_viewState, VIEW_STATE_CONTENT);
        mAnimateViewChanges = a.getBoolean(R.styleable.MultiStateView_msv_animateViewChanges, false);

        switch (viewState) {
            case VIEW_STATE_CONTENT:
                mViewState = VIEW_STATE_CONTENT;
                break;

            case VIEW_STATE_ERROR:
                mViewState = VIEW_STATE_ERROR;
                break;

            case VIEW_STATE_EMPTY:
                mViewState = VIEW_STATE_EMPTY;
                break;

            case VIEW_STATE_LOADING:
                mViewState = VIEW_STATE_LOADING;
                break;

            case VIEW_STATE_UNKNOWN:
            default:
                mViewState = VIEW_STATE_UNKNOWN;
                break;
        }

        a.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mContentView == null) throw new IllegalArgumentException("Content view is not defined");
        setView(VIEW_STATE_UNKNOWN);
    }

    /**
     * All of the addView methods have been overridden so that it can obtain the content view via XML
     * It is NOT recommended to add views into MultiStateView via the addView methods, but rather use
     * any of the setViewForState methods to set views for their given ViewState accordingly
     */
    @Override
    public void addView(View child) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child, width, height);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        if (isValidContentView(child)) mContentView = child;
        return super.addViewInLayout(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        if (isValidContentView(child)) mContentView = child;
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }

    @Nullable
    public View getView(@ViewState int state) {
        switch (state) {
            case VIEW_STATE_LOADING:
                return mLoadingView;

            case VIEW_STATE_CONTENT:
                return mContentView;

            case VIEW_STATE_EMPTY:
                return mEmptyView;

            case VIEW_STATE_ERROR:
                return mErrorView;

            default:
                return null;
        }
    }

    @ViewState
    public int getViewState() {
        return mViewState;
    }

    public void setViewState(@ViewState int state) {
        if (state != mViewState) {
            int previous = mViewState;
            mViewState = state;
            setView(previous);
        }
    }

    private void setView(@ViewState int previousState) {
        switch (mViewState) {
            case VIEW_STATE_LOADING:
                if (mLoadingView == null) {
                    throw new NullPointerException("Loading View");
                }

                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }

                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }

                if (mContentView != null) {
                    mContentView.setVisibility(View.GONE);
                }

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState));
                } else {
                    mLoadingView.setVisibility(View.VISIBLE);
                }
                break;

            case VIEW_STATE_EMPTY:
                if (mEmptyView == null) {
                    throw new NullPointerException("Empty View");
                }

                if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.GONE);
                }

                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }

                if (mContentView != null) {
                    mContentView.setVisibility(View.GONE);
                }

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState));
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                break;

            case VIEW_STATE_ERROR:
                if (mErrorView == null) {
                    throw new NullPointerException("Error View");
                }

                if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.GONE);
                }

                if (mContentView != null) {
                    mContentView.setVisibility(View.GONE);
                }

                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState));
                } else {
                    mErrorView.setVisibility(View.VISIBLE);
                }
                break;

            case VIEW_STATE_CONTENT:
            default:
                if (mContentView == null) {
                    // Should never happen, the view should throw an exception if no content view is present upon creation
                    throw new NullPointerException("Content View");
                }

                if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.GONE);
                }

                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }

                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState));
                } else {
                    mContentView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /**
     * Checks if the given {@link View} is valid for the Content View
     *
     * @param view The {@link View} to check
     * @return
     */
    private boolean isValidContentView(View view) {
        if (mContentView != null && mContentView != view) {
            return false;
        }

        return view != mLoadingView && view != mErrorView && view != mEmptyView;
    }

    public void setViewForState(View view, @ViewState int state, boolean switchToState) {
        switch (state) {
            case VIEW_STATE_LOADING:
                if (mLoadingView != null) removeView(mLoadingView);
                mLoadingView = view;
                addView(mLoadingView);
                break;

            case VIEW_STATE_EMPTY:
                if (mEmptyView != null) removeView(mEmptyView);
                mEmptyView = view;
                addView(mEmptyView);
                break;

            case VIEW_STATE_ERROR:
                if (mErrorView != null) removeView(mErrorView);
                mErrorView = view;
                addView(mErrorView);
                break;

            case VIEW_STATE_CONTENT:
                if (mContentView != null) removeView(mContentView);
                mContentView = view;
                addView(mContentView);
                break;
        }

        setView(VIEW_STATE_UNKNOWN);
        if (switchToState) setViewState(state);
    }

    public void setViewForState(View view, @ViewState int state) {
        setViewForState(view, state, false);
    }

    public void setViewForState(@LayoutRes int layoutRes, @ViewState int state, boolean switchToState) {
        if (mInflater == null) mInflater = LayoutInflater.from(getContext());
        View view = mInflater.inflate(layoutRes, this, false);
        setViewForState(view, state, switchToState);
    }

    public void setViewForState(@LayoutRes int layoutRes, @ViewState int state) {
        setViewForState(layoutRes, state, false);
    }

    /**
     * Sets whether an animate will occur when changing between {@link ViewState}
     *
     * @param animate
     */
    public void setAnimateLayoutChanges(boolean animate) {
        mAnimateViewChanges = animate;
    }

    /**
     * Animates the layout changes between {@link ViewState}
     *
     * @param previousView The view that it was currently on
     */
    private void animateLayoutChange(@Nullable final View previousView) {
        if (previousView == null) {
            getView(mViewState).setVisibility(View.VISIBLE);
            return;
        }

        previousView.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(previousView, "alpha", 1.0f, 0.0f).setDuration(250L);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                previousView.setVisibility(View.GONE);
                if (getView(mViewState) != null) {
                    getView(mViewState).setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(getView(mViewState), "alpha", 1.0f, 1.0f).setDuration(120L).start();
                } else {
                    ObjectAnimator.ofFloat(mErrorView, "alpha", 1.0f, 1.0f).setDuration(120L).start();
                }
            }
        });
        anim.start();
    }

    public void setErrorState(int error) {
        setErrorState(error, "数据加载失败");
    }

    public void setErrorState(int error, String msg) {
        if (error == MultiStateView.VIEW_STATE_EMPTY) {
            setErrorMsg();
        } else {
            if (error > 100) { //后台定义的code大于100是非系统错误
                setErrorRetry(msg);
            } else {//否则一律按正常的错误来处理
                setErrorRetry("数据加载失败");
            }
        }
    }


    /**
     * 空数据的状态
     *
     * @param msg
     */
    public void setEmptyState(String msg) {
        setEmptyState(msg, -1, -1);
    }

    /**
     * 空数据的状态
     *
     * @param source
     */
    public void setEmptyState(int source, int moresource, OnEmptyMoreOnclck onEmptyMoreOnclck) {
        this.onEmptyMoreOnclck = onEmptyMoreOnclck;
        setEmptyState(null, source, moresource);
    }

    /**
     * 空数据的状态
     *
     * @param source
     */
    public void setEmptyState(int source) {
        setEmptyState(null, source, -1);
    }


    /**
     * 空数据的状态
     *
     * @param msg
     * @param source
     */
    public void setEmptyState(String msg, int source) {
        setEmptyState(msg, source, -1);
    }

    /**
     * 空数据的状态
     *
     * @param msg
     * @param source
     * @param moreSource
     */
    public void setEmptyState(String msg, int source, int moreSource) {
        this.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        TextView tvEmpty = (TextView) this.getView(VIEW_STATE_EMPTY)
                .findViewById(jd.com.R.id.tv_empty);
        ImageView ivEmpty = (ImageView) this.getView(VIEW_STATE_EMPTY)
                .findViewById(jd.com.R.id.iv_empty);
        ImageView ivMoreSetting = (ImageView) this.getView(VIEW_STATE_EMPTY)
                .findViewById(jd.com.R.id.iv_more_setting);
        //文本描述
        if (TextUtils.isEmpty(msg)) {
            tvEmpty.setVisibility(GONE);
        } else {
            tvEmpty.setVisibility(VISIBLE);
            tvEmpty.setText(msg);
        }
        //图片资源设置
        if (source == -1) {
            ivEmpty.setImageResource(jd.com.R.mipmap.icon_load_state_empty);
        } else {
            ivEmpty.setImageResource(source);
        }
        //是否显示更多
        if (moreSource == -1) {
            ivMoreSetting.setVisibility(GONE);
        } else {
            ivMoreSetting.setImageResource(moreSource);
            ivMoreSetting.setVisibility(VISIBLE);
            ivMoreSetting.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (onEmptyMoreOnclck != null) {
                        onEmptyMoreOnclck.OnEmptyMoreOnclck();
                    }
                }
            });
        }
    }


    public void setErrorRetry(String msg) {
        this.setViewState(MultiStateView.VIEW_STATE_ERROR);
        View view = this.getView(MultiStateView.VIEW_STATE_ERROR);
        view.findViewById(jd.com.R.id.retry)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onRetryClick != null)
                            onRetryClick.onRetryClick();
                    }
                });
        TextView empty = (TextView) view.findViewById(jd.com.R.id.retry_msg_content);
        empty.setText(msg);

        TextView tvAgain = (TextView) view.findViewById(jd.com.R.id.retry_msg_again);
        if (!TextUtils.isEmpty(msg) && TextUtils.equals(msg, "请检查存储权限")) {
            tvAgain.setVisibility(INVISIBLE);
        } else {
            tvAgain.setVisibility(VISIBLE);
        }

    }

    public void setErrorMsg() {
        this.setViewState(MultiStateView.VIEW_STATE_EMPTY);
//        TextView empty = (TextView) this.getView(MultiStateView.VIEW_STATE_EMPTY)
//                .findViewById(jd.com.R.id.txt_empty);
    }

    private OnRetryClick onRetryClick;

    public void setRetryOnClick(OnRetryClick onRetryClick) {
        this.onRetryClick = onRetryClick;

    }

    public interface OnRetryClick {
        void onRetryClick();
    }

    /**
     * 空白数据的更多设置点击事件
     */
    private OnEmptyMoreOnclck onEmptyMoreOnclck;

    /**
     *
     */
    public interface OnEmptyMoreOnclck {
        void OnEmptyMoreOnclck();
    }

}