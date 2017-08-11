package jd.com.view.fragment.tab;

import android.view.View;
import android.widget.TextView;
import butterknife.OnClick;
import jd.com.R;
import jd.com.base.BaseFragment;
import jd.com.view.activity.acount.ThirdLoginActivity;
import jd.com.view.activity.setting.ImagePickerActivity;

/**
 * 我
 */
public class TabSetFragment extends BaseFragment {

    public static final String TAG = "MyFragment";
    private TextView content;
    private String title;

    @Override
    protected void initData() {
        title = getArguments().getString(TAG);
    }

    @Override
    protected void aftertView() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_set;
    }

    @Override
    protected void initView(View view) {
    }


    @OnClick({R.id.third_login, R.id.image_picker})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.third_login:
                startActi(ThirdLoginActivity.class);
                break;
            case R.id.image_picker:
                startActi(ImagePickerActivity.class);
                break;
        }
    }
}
