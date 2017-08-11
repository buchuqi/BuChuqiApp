package jd.com.library.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 防止内存泄漏
 * <p>
 * WeakReference类似于可有可无的东西。在垃圾回收器线程扫描它所管辖的内存区域的过程中，
 * 一旦发现了具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。不过，由于
 * 垃圾回收器是一个优先级很低的线程，因此不会很快发现那些具有弱引用的对象。
 * Created by jian_zhou on 2017/4/27.
 */

public abstract class WeakReferenceHandler<T> extends Handler {
    private WeakReference<T> mReference;

    public WeakReferenceHandler(T reference) {
        mReference = new WeakReference<T>(reference);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mReference.get() == null) {
            return;
        }
        handleMessage(mReference.get(), msg);
    }

    protected abstract void handleMessage(T reference, Message msg);
}
