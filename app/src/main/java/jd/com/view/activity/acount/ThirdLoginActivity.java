package jd.com.view.activity.acount;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import jd.com.R;
import jd.com.base.BaseActivity;
import jd.com.library.utils.ToastUtils;


public class ThirdLoginActivity extends BaseActivity implements Callback, PlatformActionListener {

    @BindView(R.id.sina_login)
    Button sinaLogin;
    @BindView(R.id.qq_login)
    Button qqLogin;
    @BindView(R.id.wechat_login)
    Button wechatLogin;
    private Handler handler;
    private static final int MSG_AUTH_CANCEL = 2;
    private static final int MSG_AUTH_ERROR = 3;
    private static final int MSG_AUTH_COMPLETE = 4;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_third_login;
    }

    @Override
    protected void initData() {
        handler = new Handler(this);
    }

    @Override
    protected void afterViewInit() {
    }

    @OnClick({R.id.sina_login, R.id.qq_login, R.id.wechat_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sina_login:
                //新浪微博
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(sina);
                break;
            case R.id.qq_login:
                //QQ
                Platform qzone = ShareSDK.getPlatform(QQ.NAME);
                authorize(qzone);
                break;
            case R.id.wechat_login:
                //微信登录
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                authorize(wechat);
                break;
        }
    }

    //执行授权,获取用户信息
    //文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
    private void authorize(Platform plat) {
        if (plat == null) {
            //popupOthers();
            return;
        }
        plat.setPlatformActionListener(this);
        //开启SSO授权
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTH_CANCEL:
                //取消授权
                ToastUtils.show(this, "取消授权");
                break;
            case MSG_AUTH_ERROR:
                //授权失败
                ToastUtils.show(this, "授权失败");
                break;
            case MSG_AUTH_COMPLETE:
                //授权成功
                Object[] objs = (Object[]) msg.obj;
                String platform = (String) objs[0];
                HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                Iterator ite = res.entrySet().iterator();
                while (ite.hasNext()) {
                    Map.Entry entry = (Map.Entry) ite.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    System.out.println(key + "： " + value);
                    Log.e("MSG_AUTH_COMPLETE", key + "： " + value);
                }
                break;
        }
        return false;
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
        if (action == Platform.ACTION_USER_INFOR) {
            Message msg = new Message();
            msg.what = MSG_AUTH_COMPLETE;
            msg.obj = new Object[]{platform.getName(), hashMap};
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_ERROR);
        }
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }
    }
}
