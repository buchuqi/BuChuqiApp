package jd.com.view.fragment.tab;

import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import jd.com.R;
import jd.com.base.BaseFragment;
import jd.com.entity.SliderItem;
import jd.com.library.utils.ColorUtil;
import jd.com.library.utils.DensityUtil;
import jd.com.library.weight.refresh.MaterialRefreshLayout;
import jd.com.library.weight.refresh.MaterialRefreshListener;
import jd.com.listener.ObservableScrollViewCallbacks;
import jd.com.listener.ScrollState;
import jd.com.view.MainActivity;
import jd.com.weight.MultiStateView;
import jd.com.weight.ObservableScrollView;
import jd.com.weight.header.TabMainHeaderView;

public class TabSearchFragment extends BaseFragment implements ObservableScrollViewCallbacks {

    @BindView(R.id.news_tab_header)
    TabMainHeaderView newsHeaderSlider;
    @BindView(R.id.gridview)
    GridView gridview;
    @BindView(R.id.mrl_lesson_refresh)
    MaterialRefreshLayout mrlLessonRefresh;
    @BindView(R.id.msv_lesson_state_view)
    MultiStateView msvLessonStateView;
    @BindView(R.id.view_title_bg)
    View viewTitleBg;
    @BindView(R.id.tv_title_search)
    TextView tvTitle;
    @BindView(R.id.rl_bar)
    RelativeLayout rlBar;
    @BindView(R.id.scrollviewtesst)
    ObservableScrollView scrollviewtesst;
    private TextView content;
    private String title;
    public static final String TAG = "MyFragment";
    private boolean isScrollIdle = true; // ListView是否在滑动
    private int bannerViewTopMargin; // 广告视图距离顶部的距离
    private int bannerViewHeight = 180; // 广告视图的高度
    private int titleViewHeight = 65; // 标题栏的高度

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_search;
    }

    @Override
    protected void initData() {
        title = getArguments().getString(TAG);
    }

    @Override
    protected void aftertView() {
        msvLessonStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        List<SliderItem> slides = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            slides.add(new SliderItem("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498902363&di=f2f9ac4afd42cf87a3aadc6149f88dcf&imgtype=jpg&er=1&src=http%3A%2F%2Fimg1.gtimg.com%2Fhn%2Fpics%2Fhv1%2F108%2F8%2F1763%2F114641223.jpg", i + " xx "));
        }
        newsHeaderSlider.setSlderData(slides.subList(0, 4));

        scrollviewtesst.setScrollViewCallbacks(this);
        mrlLessonRefresh.setMaterialRefreshListener(refreshListener);
    }


    @Override
    protected void initView(View view) {
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScoll, boolean dragging) {
        if (newsHeaderSlider != null) {
            bannerViewTopMargin = DensityUtil.px2dip(getActivity(), scrollY);
            bannerViewHeight = DensityUtil.px2dip(getActivity(), newsHeaderSlider.getHeight());
        }
        if (isScrollIdle && bannerViewTopMargin < 0) return;
        // 处理标题栏颜色渐变
        handleTitleBarColorEvaluate();
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancleMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.STOP) {
            isScrollIdle = false;
        } else {
            isScrollIdle = true;
        }
    }


    private MaterialRefreshListener refreshListener = new MaterialRefreshListener() {
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            mrlLessonRefresh.isOver();
        }

        @Override
        public void onfinish() {
            super.onfinish();
            rlBar.setAlpha(1f);
        }

        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            mrlLessonRefresh.isOver();
        }

        @Override
        public void onHeaderTopHeight(int topHeader) {
            super.onHeaderTopHeight(topHeader);
            bannerViewTopMargin = DensityUtil.px2dip(getActivity(), topHeader);
            float fraction;
            // 处理标题栏颜色渐变
            if (bannerViewTopMargin > 0) {
                fraction = 1f - bannerViewTopMargin * 1f / 60;
                if (fraction < 0f) fraction = 0f;
                rlBar.setAlpha(fraction);
                return;
            }
        }
    };


    private void handleTitleBarColorEvaluate() {
        float fraction;
        float space = Math.abs(bannerViewTopMargin) * 1f;
        fraction = space / (bannerViewHeight - titleViewHeight);
        if (fraction < 0f) fraction = 0f;
        if (fraction > 1f) fraction = 1f;
        rlBar.setAlpha(1f);
        if (fraction >= 1f) {
            viewTitleBg.setAlpha(0f);
            rlBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        } else {
            viewTitleBg.setAlpha(1f - fraction);
            rlBar.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(getActivity(), fraction, R.color.transparent, R.color.colorPrimary));
        }
    }

    @OnClick(R.id.tv_title_search)
    public void onClick() {
        MainActivity main = (MainActivity) getActivity();
        main.startCity();
    }

    /**
     * 设置城市名称
     *
     * @param name
     */
    public void setCityName(String name) {
        tvTitle.setText(name);
    }
}
