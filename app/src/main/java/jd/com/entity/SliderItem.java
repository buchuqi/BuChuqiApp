package jd.com.entity;

import java.io.Serializable;

/**
 * Created by jian_zhou on 2016/8/24.
 */
public class SliderItem implements Serializable {

    public int id;
    public int contentid;
    public int modelid;
    public String title;
    public String thumb;
    public String url;

    public SliderItem() {
    }

    public SliderItem(String url, String title) {
        this.thumb = url;
        this.title = title;
    }

    public SliderItem(String title) {
        this.title = title;
    }

    public SliderItem(int id, String url, String title, int contentid, int modelid, String thumb) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.contentid = contentid;
        this.modelid = modelid;
        this.thumb = thumb;
    }

}
