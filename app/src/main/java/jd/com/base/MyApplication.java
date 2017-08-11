package jd.com.base;


import android.app.Application;

import cn.sharesdk.framework.ShareSDK;

/**
 * Application
 * Created by jian_zhou on 2017/7/10.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ShareSDK.initSDK(this);
    }
}
