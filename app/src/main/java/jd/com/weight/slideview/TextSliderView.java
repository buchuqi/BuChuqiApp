package jd.com.weight.slideview;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import jd.com.R;

/**
 * This is a slider with a description TextView.
 */
public class TextSliderView extends BaseSliderView {
    public TextSliderView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_slide_item, null);
        SimpleDraweeView target = (SimpleDraweeView) v.findViewById(R.id.daimajia_slider_image);
        LinearLayout description_layout = (LinearLayout) v.findViewById(R.id.description_layout);
        description_layout.setVisibility(getIsVisibleDescription() ? View.VISIBLE : View.INVISIBLE);
        if (getIsVisibleDescription()) {
            TextView description = (TextView) v.findViewById(R.id.description);
            description.setText(getDescription());
        }
        setImageUrl(target, getUrl(), R.mipmap.bg_default_slide);
        bindClickEvent(v);
        return v;
    }

    public void setImageUrl(SimpleDraweeView imageView, String url, int resource) {
        Uri uri = Uri.parse(url);
        if (!TextUtils.isEmpty(url)) {
            imageView.setImageURI(uri);
        } else {
            imageView.setImageResource(resource);
        }
    }


}
