package jd.com.view.fragment.tab;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import jd.com.R;
import jd.com.base.BaseFragment;
import jd.com.entity.SliderItem;
import jd.com.library.base.AdapterBase;
import jd.com.library.base.ViewHolder;
import jd.com.library.utils.ColorUtil;
import jd.com.library.utils.DensityUtil;
import jd.com.library.weight.refresh.MaterialRefreshLayout;
import jd.com.library.weight.refresh.MaterialRefreshListener;
import jd.com.weight.MultiStateView;
import jd.com.weight.header.TabMainHeaderView;
import jd.com.weight.slideview.SliderLayout;
import jd.com.weight.smoothListView.SmoothListView;

/**
 * 主页
 */
public class TabMainFragment extends BaseFragment {
    @BindView(R.id.listView)
    SmoothListView smoothListView;
    @BindView(R.id.mrl_lesson_refresh)
    MaterialRefreshLayout mrlLessonRefresh;
    @BindView(R.id.msv_lesson_state_view)
    MultiStateView msvLessonStateView;
    @BindView(R.id.view_title_bg)
    View viewTitleBg;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_bar)
    RelativeLayout rlBar;
    private String title;
    public static final String TAG = "MyFragment";
    private boolean isScrollIdle = true; // ListView是否在滑动
    private int bannerViewTopMargin; // 广告视图距离顶部的距离
    private int bannerViewHeight = 180; // 广告视图的高度
    private int titleViewHeight = 65; // 标题栏的高度
    private TabMainHeaderView itemHeaderBannerView;

    @Override
    protected void initData() {
        title = getArguments().getString(TAG);
    }

    @Override
    protected void aftertView() {
        msvLessonStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        itemHeaderBannerView = new TabMainHeaderView(getActivity());
        itemHeaderBannerView.setDescriptionisVisible(false);

        List<SliderItem> slides = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            slides.add(new SliderItem("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498902363&di=f2f9ac4afd42cf87a3aadc6149f88dcf&imgtype=jpg&er=1&src=http%3A%2F%2Fimg1.gtimg.com%2Fhn%2Fpics%2Fhv1%2F108%2F8%2F1763%2F114641223.jpg", i + " xx "));
        }
        itemHeaderBannerView.setSlderData(slides.subList(0, 4));
        smoothListView.setAdapter(new AdapterBase<SliderItem>(getActivity(), slides, R.layout.adapter_drugstore_tools) {
            @Override
            public void convertView(ViewHolder helper, SliderItem item) {
                helper.setText(R.id.tv_asc_title, item.title);
                helper.setImageUrl(R.id.iv_asc_icon, item.thumb);
            }
        });
        smoothListView.addHeaderView(itemHeaderBannerView);
        smoothListView.setOnScrollListener(scrollListener);
        mrlLessonRefresh.setMaterialRefreshListener(refreshListener);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_main;
    }

    @Override
    protected void initView(View view) {
    }

    private MaterialRefreshListener refreshListener = new MaterialRefreshListener() {
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            itemHeaderBannerView.setScrollerDuration(0);
            mrlLessonRefresh.isOver();
        }

        @Override
        public void onfinish() {
            super.onfinish();
            itemHeaderBannerView.setScrollerDuration(SliderLayout.indexDuration);
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
            if (isScrollIdle && bannerViewTopMargin < 0) return;
            // 处理标题栏颜色渐变
            handleTitleBarColorEvaluate();
        }
    };

    private SmoothListView.OnSmoothScrollListener scrollListener = new SmoothListView.OnSmoothScrollListener() {
        @Override
        public void onSmoothScrolling(View view) {
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            isScrollIdle = (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (itemHeaderBannerView != null) {
                bannerViewTopMargin = DensityUtil.px2dip(getActivity(), itemHeaderBannerView.getTop());
                bannerViewHeight = DensityUtil.px2dip(getActivity(), itemHeaderBannerView.getHeight());
            }

            Log.e("111",bannerViewTopMargin+"   ");
            if (isScrollIdle && bannerViewTopMargin < 0) return;
            // 处理标题栏颜色渐变
            handleTitleBarColorEvaluate();
        }
    };


    private void handleTitleBarColorEvaluate() {
        float fraction;
        if (bannerViewTopMargin > 0) {
            fraction = 1f - bannerViewTopMargin * 1f / 60;
            if (fraction < 0f) fraction = 0f;
            rlBar.setAlpha(fraction);
            return;
        }

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

}
