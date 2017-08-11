package jd.com.library.weight.refresh;

public abstract class MaterialRefreshListener {
    public void onfinish() {
    }

    public void onHeaderTopHeight(int topHeader){
    }

    public abstract void onRefresh(MaterialRefreshLayout materialRefreshLayout);

    public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
    }

}
