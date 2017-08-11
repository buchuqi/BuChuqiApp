package jd.com.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zaaach.citypicker.CityPickerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import jd.com.R;
import jd.com.library.weight.CustomViewPager;
import jd.com.library.weight.smarttablayout.SmartTabLayout;
import jd.com.library.weight.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import jd.com.library.weight.smarttablayout.utils.v4.FragmentPagerItems;
import jd.com.view.fragment.tab.TabDatingFragment;
import jd.com.view.fragment.tab.TabMainFragment;
import jd.com.view.fragment.tab.TabSearchFragment;
import jd.com.view.fragment.tab.TabSetFragment;

/**
 * 主页面
 */
public class MainActivity extends AppCompatActivity {
    //城市选择
    private static final int REQUEST_CODE_PICK_CITY = 233;
    final int[] tabIcons = {R.drawable.selector_tab_lesson, R.drawable.selector_tab_message, R.drawable.selector_tab_company, R.drawable.selector_tab_setting};
    final int[] tabNames = {R.string.tab_lesson, R.string.tab_message, R.string.tab_news, R.string.tab_setting};
    @BindView(R.id.cvp_main)
    CustomViewPager cvpMain;
    @BindView(R.id.stl_main)
    SmartTabLayout stlMain;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        afterViewInit();
    }

    protected void initData() {
    }

    private FragmentPagerItemAdapter vpAdapter;

    protected void afterViewInit() {
        final FragmentPagerItems pages = FragmentPagerItems.with(this)
                .add(R.string.tab_lesson, TabSearchFragment.class)
                .add(R.string.tab_message, TabMainFragment.class)
                .add(R.string.tab_news, TabDatingFragment.class)
                .add(R.string.tab_setting, TabSetFragment.class).create();
        vpAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        cvpMain.setOffscreenPageLimit(pages.size());
        cvpMain.setAdapter(vpAdapter);
        cvpMain.setCanScroll(false);

        final LayoutInflater inflater = LayoutInflater.from(this);
        stlMain.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View view = inflater.inflate(R.layout.view_tab_icon, container, false);
                ImageView iconView = (ImageView) view.findViewById(R.id.iv_tab_icon);
                iconView.setBackgroundResource(tabIcons[position % tabIcons.length]);
                TextView textView = (TextView) view.findViewById(R.id.tv_tab_name);
                textView.setText(tabNames[position % tabNames.length]);
                return view;
            }
        });
        stlMain.setViewPager(cvpMain);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_CITY && resultCode == RESULT_OK) {
            if (data != null) {
                String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                TabSearchFragment tabSearchFragment = (TabSearchFragment) vpAdapter.getPage(0);
                tabSearchFragment.setCityName(city);
            }
        }
    }

    public void startCity() {
        startActivityForResult(new Intent(MainActivity.this, CityPickerActivity.class),
                REQUEST_CODE_PICK_CITY);
    }

}
