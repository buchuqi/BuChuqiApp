package jd.com.weight.slideview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import jd.com.R;
import jd.com.weight.slideview.tricks.FixedSpeedScroller;
import jd.com.weight.slideview.tricks.InfinitePagerAdapter;
import jd.com.weight.slideview.tricks.InfiniteViewPager;
import jd.com.weight.slideview.tricks.ViewPagerEx;

@SuppressLint("HandlerLeak")
public class SliderLayout extends RelativeLayout {

    public static final int indexDuration = 600;
    private Context mContext;
    /**
     * InfiniteViewPager is extended from ViewPagerEx. As the name says, it can
     * scroll without bounder.
     */
    private InfiniteViewPager mViewPager;

    /**
     * InfiniteViewPager adapter.
     */
    private SliderAdapter mSliderAdapter;
    private PagerIndicatorView mIndicator;

    private Timer mCycleTimer;
    private TimerTask mCycleTask;

    private Timer mResumingTimer;
    private TimerTask mResumingTask;

    private boolean mCycling;

    private boolean mAutoRecover;

    private int mTransformerId;

    private int mTransformerSpan;

    private boolean mAutoCycle;

    private PagerIndicatorView.IndicatorVisibility mIndicatorVisibility = PagerIndicatorView.IndicatorVisibility.Visible;

    private BaseTransformer mViewPagerTransformer;
    private BaseAnimationInterface mCustomAnimation;


    public SliderLayout(Context context) {
        this(context, null);
    }

    public SliderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SliderStyle);
    }

    private InfinitePagerAdapter wrappedAdapter;

    public SliderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(context)
                .inflate(R.layout.view_slide_layout, this, true);

        final TypedArray attributes = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.SliderLayout,
                        defStyle, 0);

        mTransformerSpan = attributes.getInteger(
                R.styleable.SliderLayout_pager_animation_span, 100);
        mTransformerId = attributes.getInt(
                R.styleable.SliderLayout_pager_animation,
                Transformer.Default.ordinal());
        mAutoCycle = attributes.getBoolean(R.styleable.SliderLayout_auto_cycle,
                true);
        int visibility = attributes.getInt(
                R.styleable.SliderLayout_indicator_visibility, 0);
        for (PagerIndicatorView.IndicatorVisibility v : PagerIndicatorView.IndicatorVisibility
                .values()) {
            if (v.ordinal() == visibility) {
                mIndicatorVisibility = v;
                break;
            }
        }
        mSliderAdapter = new SliderAdapter(mContext);
        wrappedAdapter = new InfinitePagerAdapter(mSliderAdapter);

        mViewPager = (InfiniteViewPager) findViewById(R.id.daimajia_slider_viewpager);
        mViewPager.setAdapter(wrappedAdapter);

        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        recoverCycle();
                        break;
                }
                return false;
            }
        });

        attributes.recycle();
        setPresetIndicator(PresetIndicators.Center_Bottom);
        setPresetTransformer(mTransformerId);
        setSliderTransformDuration(mTransformerSpan, null);
        setIndicatorVisibility(mIndicatorVisibility);
        if (mAutoCycle) {
            startAutoCycle();
        }
    }

    /**
     * 仅有一个时，设置不滚动
     *
     * @param isJustOne
     */
    public void setJustOne(boolean isJustOne) {
        wrappedAdapter.setJustOne(isJustOne);
        if (isJustOne)
            setIndicatorVisibility(PagerIndicatorView.IndicatorVisibility.Invisible);
    }

    public void setCustomIndicator(PagerIndicatorView indicator) {
        if (mIndicator != null) {
            mIndicator.destroySelf();
        }
        mIndicator = indicator;
        mIndicator.setIndicatorVisibility(mIndicatorVisibility);
        mIndicator.setViewPager(mViewPager);
        mIndicator.redraw();
    }

    public <T extends BaseSliderView> void addSlider(T imageContent) {
        mSliderAdapter.addSlider(imageContent);
    }

    public void notifyData() {
        mSliderAdapter.notifyDataSetChanged();
    }

    public void startAutoCycle() {
        startAutoCycle(1000, 3000, true);
    }

    boolean isAutoCycle;

    public void setmAutoCycle(boolean isAutoCycle) {
        this.isAutoCycle = isAutoCycle;
    }

    /**
     * registerChatConversation auto cycle.
     *
     * @param delay       delay time
     * @param period      period time.
     * @param autoRecover
     */
    public void startAutoCycle(long delay, long period, boolean autoRecover) {
        mCycleTimer = new Timer();
        mAutoRecover = autoRecover;
        mCycleTask = new TimerTask() {
            @Override
            public void run() {
                if (isAutoCycle)
                    mh.sendEmptyMessage(0);
            }
        };
        mCycleTimer.schedule(mCycleTask, delay, period);
        mCycling = true;
    }

    /**
     * pause auto cycle.
     */
    private void pauseAutoCycle() {
        if (mCycling) {
            mCycleTimer.cancel();
            mCycleTask.cancel();
            mCycling = false;
        } else {
            if (mResumingTimer != null && mResumingTask != null) {
                recoverCycle();
            }
        }
    }

    /**
     * unRegisterChatConversation the auto circle
     */
    public void stopAutoCycle() {
        if (mCycleTask != null) {
            mCycleTask.cancel();
        }
        if (mCycleTimer != null) {
            mCycleTimer.cancel();
        }
        if (mResumingTimer != null) {
            mResumingTimer.cancel();
        }
        if (mResumingTask != null) {
            mResumingTask.cancel();
        }
    }

    /**
     * when paused cycle, this method can weak it up.
     */
    private void recoverCycle() {
        if (!mAutoRecover) {
            return;
        }

        if (!mCycling) {
            if (mResumingTask != null && mResumingTimer != null) {
                mResumingTimer.cancel();
                mResumingTask.cancel();
            }
            mResumingTimer = new Timer();
            mResumingTask = new TimerTask() {
                @Override
                public void run() {
                    startAutoCycle();
                }
            };
            mResumingTimer.schedule(mResumingTask, 3000);
        }
    }

    private final android.os.Handler mh = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mViewPager.nextItem();
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pauseAutoCycle();
                break;
        }
        return false;
    }

    /**
     * set ViewPager transformer.
     *
     * @param reverseDrawingOrder
     * @param transformer
     */
    public void setPagerTransformer(boolean reverseDrawingOrder,
                                    BaseTransformer transformer) {
        mViewPagerTransformer = transformer;
        mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        mViewPager.setPageTransformer(reverseDrawingOrder,
                (ViewPagerEx.PageTransformer) mViewPagerTransformer);
    }

    private FixedSpeedScroller scroller;

    /**
     * set the duration between two slider changes.
     *
     * @param period
     * @param interpolator
     */
    public void setSliderTransformDuration(int period, Interpolator interpolator) {
        try {
            Field mScroller = ViewPagerEx.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new FixedSpeedScroller(
                    mViewPager.getContext(), interpolator);
            mScroller.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int duration = 600;

    public void setScrollerDuration(int duration) {
        this.duration = duration;
        if (scroller != null)
            scroller.setmDuration(duration);
    }

    /**
     * preset transformers and their names
     */
    public enum Transformer {
        Default("Default"), Accordion("Accordion"), Background2Foreground(
                "Background2Foreground"), CubeIn("CubeIn"), DepthPage(
                "DepthPage"), Fade("Fade"), FlipHorizontal("FlipHorizontal"), FlipPage(
                "FlipPage"), Foreground2Background("Foreground2Background"), RotateDown(
                "RotateDown"), RotateUp("RotateUp"), Stack("Stack"), Tablet(
                "Tablet"), ZoomIn("ZoomIn"), ZoomOutSlide("ZoomOutSlide"), ZoomOut(
                "ZoomOut");

        private final String name;

        Transformer(String s) {
            name = s;
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean equals(String other) {
            return other != null && name.equals(other);
        }
    }

    /**
     * set a preset viewpager transformer by id.
     *
     * @param transformerId
     */
    public void setPresetTransformer(int transformerId) {
        for (Transformer t : Transformer.values()) {
            if (t.ordinal() == transformerId) {
                setPresetTransformer(t);
                break;
            }
        }
    }

    /**
     * set preset PagerTransformer via the name of transforemer.
     *
     * @param transformerName
     */
    public void setPresetTransformer(String transformerName) {
        for (Transformer t : Transformer.values()) {
            if (t.equals(transformerName)) {
                setPresetTransformer(t);
                return;
            }
        }
    }

    public void setCustomAnimation(BaseAnimationInterface animation) {
        mCustomAnimation = animation;
        if (mViewPagerTransformer != null) {
            mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        }
    }

    /**
     * pretty much right? enjoy it. :-D
     *
     * @param ts
     */
    public void setPresetTransformer(Transformer ts) {
        //
        // special thanks to https://github.com/ToxicBakery/ViewPagerTransforms
        //
        BaseTransformer t = null;
        switch (ts) {
            case Default:
                t = new DefaultTransformer();
                break;
        }
        setPagerTransformer(true, t);
    }

    /**
     * Set the visibility of the indicators.
     *
     * @param visibility
     */
    public void setIndicatorVisibility(
            PagerIndicatorView.IndicatorVisibility visibility) {
        if (mIndicator == null) {
            return;
        }
        mIndicator.setIndicatorVisibility(visibility);
    }

    public PagerIndicatorView.IndicatorVisibility getIndicatorVisibility() {
        if (mIndicator == null) {
            return mIndicator.getIndicatorVisibility();
        }
        return PagerIndicatorView.IndicatorVisibility.Invisible;

    }

    public PagerIndicatorView getPagerIndicator() {
        return mIndicator;
    }

    public enum PresetIndicators {
        Center_Bottom("Center_Bottom", R.id.default_center_bottom_indicator), Right_Bottom(
                "Right_Bottom", R.id.default_bottom_right_indicator), Left_Bottom(
                "Left_Bottom", R.id.default_bottom_left_indicator), Center_Top(
                "Center_Top", R.id.default_center_top_indicator), Right_Top(
                "Right_Top", R.id.default_center_top_right_indicator), Left_Top(
                "Left_Top", R.id.default_center_top_left_indicator);

        private final String name;
        private final int id;

        PresetIndicators(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getResourceId() {
            return id;
        }
    }

    public void setPresetIndicator(PresetIndicators presetIndicator) {
        PagerIndicatorView pagerIndicator = (PagerIndicatorView) findViewById(presetIndicator
                .getResourceId());
        setCustomIndicator(pagerIndicator);
    }

    private InfinitePagerAdapter getWrapperAdapter() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            return (InfinitePagerAdapter) adapter;
        } else {
            return null;
        }
    }

    private SliderAdapter getRealAdapter() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            return ((InfinitePagerAdapter) adapter).getRealAdapter();
        }
        return null;
    }

    /**
     * remove the slider at the position. Notice: It's a not perfect method, a
     * very small bug still exists.
     */
    public void removeSliderAt(int position) {
        if (getRealAdapter() != null) {
            getRealAdapter().removeSliderAt(position);
            mViewPager.setCurrentItem(mViewPager.getCurrentItem(), false);
        }
    }

    /**
     * remove all the sliders. Notice: It's a not perfect method, a very small
     * bug still exists.
     */
    public void removeAllSliders() {
        if (getRealAdapter() != null) {
            int count = getRealAdapter().getCount();
            getRealAdapter().removeAllSliders();
            // a small bug, but fixed by this trick.
            // bug: when remove adapter's all the sliders.some caching slider
            // still alive.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + count,
                    false);
        }
    }

    /**
     * 设置颜色值
     *
     * @param mDefaultSelectedColor
     * @param mDefaultUnSelectedColor
     */
    public void setDefaultIndicatorColor(int mDefaultSelectedColor, int mDefaultUnSelectedColor) {
        mIndicator.setIndicatorColor(mDefaultSelectedColor, mDefaultUnSelectedColor);
    }

}
