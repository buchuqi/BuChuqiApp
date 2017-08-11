package jd.com.weight.header;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import jd.com.R;
import jd.com.entity.SliderItem;
import jd.com.library.utils.ListUtils;
import jd.com.weight.slideview.BaseSliderView;
import jd.com.weight.slideview.SliderLayout;
import jd.com.weight.slideview.TextSliderView;

/**
 * 课程首页效果
 * Created by jian_zhou on 2016/12/26.
 */

public class TabMainHeaderView extends RelativeLayout implements BaseSliderView.OnSliderClickListener {

    private Context context;
    private SliderLayout sliderView;

    public TabMainHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public TabMainHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TabMainHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        init(context);
        View headerView = LayoutInflater.from(context).inflate(R.layout.view_newsheader, this);
        sliderView = (SliderLayout) headerView.findViewById(R.id.news_header_slider);
        sliderView.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderView.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderView.setScrollerDuration(SliderLayout.indexDuration);
    }

    private void init(Context context) {
        this.context = context;
    }

    public void setScrollerDuration(int duration) {
        sliderView.setScrollerDuration(duration);
    }

    public void setSlderData(List<SliderItem> slides) {
        if (ListUtils.isEmpty(slides)) {
            this.removeAllViews();
            return;
        }
        sliderView.removeAllSliders();
        sliderView.setJustOne(slides.size() == 1);
        for (int i = 0; i < slides.size(); i++) {
            TextSliderView textSliderView = new TextSliderView(context);
            textSliderView.setOnSliderClickListener(this);
            textSliderView.description(slides.get(i).title).image(slides.get(i).thumb);
            textSliderView.descriptionisVisible(isvisible);
            textSliderView.getBundle().putSerializable("slide", slides.get(i));
            sliderView.addSlider(textSliderView);
        }
        sliderView.notifyData();
        sliderView.setDefaultIndicatorColor(Color.parseColor("#999999"), Color.parseColor("#d0d0d1"));
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        if (noListener) {
            return;
        }
        SliderItem slide = (SliderItem) slider.getBundle().getSerializable("slide");
        if (slide == null) {
            return;
        }
    }

    private boolean isvisible = true;

    public void setDescriptionisVisible(boolean isvisible) {
        this.isvisible = isvisible;
    }

    private boolean noListener;

    /**
     * 不设置监听事件
     */
    public void setNullListener() {
        noListener = true;
    }

    public void setSliderViewHeight(int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sliderView.getLayoutParams();
        params.height = (int) getResources().getDimension(height);
        sliderView.setLayoutParams(params);
    }

}
