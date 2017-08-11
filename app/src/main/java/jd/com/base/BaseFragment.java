package jd.com.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import jd.com.library.utils.ToastUtils;

/**
 * Fragment的基类
 * Created by jian_zhou on 2016/8/18.
 */
public abstract class BaseFragment extends Fragment {

    protected boolean isVisible;
    private View currentView;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != currentView) {
            ViewGroup parent = (ViewGroup) currentView.getParent();
            if (null != parent) {
                parent.removeView(currentView);
            }
        } else {
            currentView = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, currentView);
            initView(currentView);
        }

        return currentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        aftertView();
    }

    protected abstract void initData();

    protected abstract void aftertView();

    /**
     * 得到layout布局文件，R.layout.activity_xxxx
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化数据，包括从bundle中获取数据保存到当前activity中
     */
    protected abstract void initView(View view);

    protected void onInvisible() {
    }

    protected void onVisible() {
    }

    protected void injectorPresenter() {
    }

    protected void startActi(Class<?> acti) {
        startActi(null, acti);
    }

    protected void startActi(Bundle bundle, Class<?> acti) {
        Intent intent = new Intent(getActivity(), acti);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        startActivity(intent);
    }

    protected void showToask(int msgID) {
        showToask(getString(msgID));
    }

    protected void showToask(String msg) {
        ToastUtils.show(getActivity(), msg);
    }

    @Override
    public void onResume() {
        super.onResume();
        injectorPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = YaoSTApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }
}