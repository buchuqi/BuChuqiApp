package jd.com.library.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 帧动画
 */
public class FrameAnimImage extends ImageView {

    public FrameAnimImage(Context context) {
        super(context);
        /* TODO Auto-generated constructor stub */
        anim = (AnimationDrawable) this.getBackground();
    }

    public FrameAnimImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        try {
            anim = (AnimationDrawable) this.getBackground();
        } catch (Exception e) {
            System.out
                    .println("MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }


    public FrameAnimImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        try {
            anim = (AnimationDrawable) this.getBackground();
        } catch (Exception e) {
            System.out
                    .println("MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }

    public interface OnFrameAnimationListener {
        /**
         * 动画开始播放后调用
         */
        void onStart();

        /**
         * 动画结束播放后调用
         */
        void onEnd();
    }

    private AnimationDrawable anim;
    private Handler handler;

    /**
     * 带动画监听的播放
     */
    public void startAnim() {
        anim.start();
/*        if (listener != null) {
            // 调用回调函数onStart
            listener.onStart();
        }

        // 计算动态图片所花费的事件
        int durationTime = 0;
        for (int i = 0; i < anim.getNumberOfFrames(); i++) {
            durationTime += anim.getDuration(i);
        }

        // 动画结束后
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    // 调用回调函数onEnd
                    listener.onEnd();
                }
                anim.stop();
//                recyleAnim(anim);
            }
        }, durationTime);*/
    }

    public void recyleAnim(AnimationDrawable animationDrawables) {
        if (animationDrawables != null) {
            animationDrawables.stop();
            for (int i = 0; i < animationDrawables.getNumberOfFrames(); i++) {
                Drawable frame = animationDrawables.getFrame(i);
                if (frame instanceof BitmapDrawable) {
                    Bitmap bmp = ((BitmapDrawable) frame).getBitmap();
                    if (!bmp.isRecycled() && null != bmp) {
//                        bmp.recycle();
                        bmp = null;
                    }
                }
                frame.setCallback(null);
            }
            animationDrawables.setCallback(null);
        }
        handler.removeCallbacksAndMessages(null);
    }

    public void endAnim() {
        anim.stop();
        anim.selectDrawable(0);
    }

    private OnFrameAnimationListener listener;

    public void setAnimListener(OnFrameAnimationListener listener) {
        this.listener = listener;
    }

}
