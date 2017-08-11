package jd.com.view.fragment;

import android.view.View;
import android.widget.TextView;

import jd.com.R;
import jd.com.base.BaseFragment;

public class MyFragment extends BaseFragment {

    public static final String TAG = "MyFragment";
    private TextView content;
    private String title;

    @Override
    protected void initData() {
        title = getArguments().getString(TAG);
    }

    @Override
    protected void aftertView() {
        content.setText(title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView(View view) {
        content = (TextView) view.findViewById(R.id.content);
    }
}
