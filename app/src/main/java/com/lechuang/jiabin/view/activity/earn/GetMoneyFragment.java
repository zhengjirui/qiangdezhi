package com.lechuang.jiabin.view.activity.earn;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseFragment;
import com.lechuang.jiabin.model.LeCommon;
import com.lechuang.jiabin.model.bean.GetBean;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.GetApi;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.activity.get.GetInfoActivity;
import com.lechuang.jiabin.view.activity.home.SearchActivity;
import com.lechuang.jiabin.view.defineView.CustomTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class GetMoneyFragment extends BaseFragment {
    @BindView(R.id.tablayout_get)
    TabLayout tablayoutGet;
    @BindView(R.id.vp_get)
    ViewPager vpGet;
    @BindView(R.id.ll_search)
    LinearLayout etTopSearch;
    @BindView(R.id.ll_noNet)
    LinearLayout llNoNet; //没有网络
    @BindView(R.id.iv_tryAgain)
    ImageView tryAgain;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.iv_wenhao)
    ImageView ivShuoming;
    Unbinder unbinder;
    //fragments集合
    private List<Fragment> fragments;
    //viewpage标题
    private List<GetBean.TopTab> topTabList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_get, null, false);
        unbinder = ButterKnife.bind(this, content);
        etTopSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        ivShuoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),GetInfoActivity.class));
            }
        });
        getData();
        return content;
    }

    private void getData() {

        if (Utils.isNetworkAvailable(getActivity())) {
//            refreshScrollView.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.VISIBLE);
            llNoNet.setVisibility(View.GONE);
            Netword.getInstance().getApi(GetApi.class)
                    .topTabList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<GetBean>(getActivity()) {
                        @Override
                        public void successed(GetBean result) {
                            if (result == null) return;
                            GetBean.TopTab topTab = new GetBean.TopTab();
                            topTab.rootId = -1;
                            topTab.rootName = "精选";
                            topTabList.add(topTab);
                            List<GetBean.TopTab> tabList = result.tbclassTypeList;
                            if (tabList != null) {
                                topTabList.addAll(tabList);
                            }
                            CustomTabLayout.reflex(tablayoutGet);
                            initFragment();
                        }
                    });
        } else {
            llNoNet.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
        }
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/22  12:22
     * @describe 中间viewPager和fragment联动
     */
    private void initFragment() {
        fragments = new ArrayList<>();
        for (GetBean.TopTab tab : topTabList) {
            fragments.add(setTitle(new GetBaseFragment(), tab.rootId, tab.rootName));
        }
        //设置适配器
        CommonPagerAdapter mPaperAdapter = new CommonPagerAdapter(getFragmentManager());
        mPaperAdapter.addFragment(fragments, topTabList);
//        vpGet.setOffscreenPageLimit(3);
        vpGet.setAdapter(mPaperAdapter);
        //设置tablout 滑动模式
        tablayoutGet.setTabMode(TabLayout.MODE_SCROLLABLE);
        //联系tabLayout和viwpager
        tablayoutGet.setupWithViewPager(vpGet);
    }

    /**
     * 设置头目
     */
    private Fragment setTitle(Fragment fragment, int rootId, String title) {
        Bundle args = new Bundle();
        args.putInt(LeCommon.KEY_ROOT_ID, rootId);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_tryAgain})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_tryAgain:
                if (Utils.isNetworkAvailable(getActivity())) {
                    getData();
                } else {
                    showShortToast(getString(R.string.net_error));
                }
                break;
        }

    }

}
