package jd.com.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import jd.com.library.utils.AppManager;
import jd.com.library.utils.ToastUtils;

/**
 * 基类
 *
 * @author jian_zhou
 */
public abstract class BaseActivity extends SwipeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setSwipeAnyWhere(true);
        initData();
        afterViewInit();
    }

    /**
     * 得到layout布局文件，R.layout.activity_xxxx
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化数据，包括从bundle中获取数据保存到当前activity中
     */
    protected abstract void initData();

    /**
     * 界面初始化之后的后处理，如启动网络读取数据、启动定位等
     */
    protected abstract void afterViewInit();

    protected void showToask(int msgID) {
        showToask(getString(msgID));
    }

    protected void showToask(String msg) {
        ToastUtils.show(this, msg);
    }

    protected void finishActi(Activity acti) {
        AppManager.getAppManager().finishActivity(acti);
    }

    protected void startActi(Class<?> acti) {
        startActi(null, acti);
    }

    protected void startActi(Bundle bundle, Class<?> acti) {
        Intent intent = new Intent(this, acti);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        //取消用于退出程序的广播注册
        super.onDestroy();
//        RefWatcher refWatcher = YaoSTApplication.getRefWatcher(this);
//        refWatcher.watch(this);
    }

}