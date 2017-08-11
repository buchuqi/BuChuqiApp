package jd.com.view.fragment.tab;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import jd.com.R;
import jd.com.base.BaseFragment;
import jd.com.view.CodeActivity;
import jd.com.view.adapter.MyAdapter;
import jd.com.view.fragment.MyFragment;

/**
 * 约会
 */
public class TabDatingFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "MyFragment";
    @BindView(R.id.saoma)
    TextView saoma;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    private String title;
    private MyAdapter adapter;
    private MyFragment myFragment;
    public static int mPosition;
    private String[] strs = {"常用分类", "服饰内衣", "鞋靴", "手机", "家用电器", "数码", "电脑办公",
            "个护化妆", "图书"};

    @Override
    protected void initData() {
        title = getArguments().getString(TAG);
    }

    @Override
    protected void aftertView() {
        adapter = new MyAdapter(getActivity(), strs);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);

        //创建MyFragment对象
        myFragment = new MyFragment();
        FragmentTransaction fragmentTransaction = getChildFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, myFragment);
        //通过bundle传值给MyFragment
        Bundle bundle = new Bundle();
        bundle.putString("MyFragment", strs[mPosition]);
        myFragment.setArguments(bundle);
        fragmentTransaction.commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_dating;
    }

    @Override
    protected void initView(View view) {

    }


    @OnClick(R.id.saoma)
    public void onClick() {
        startActi(CodeActivity.class);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mPosition = position;
        //即使刷新adapter
        adapter.notifyDataSetChanged();
        for (int i = 0; i < strs.length; i++) {
            myFragment = new MyFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, myFragment);
            Bundle bundle = new Bundle();
            bundle.putString(MyFragment.TAG, strs[position]);
            myFragment.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }
}
