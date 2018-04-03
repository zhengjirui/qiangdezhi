package com.lechuang.jiabin.view.activity.earn;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseFragment;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.jiabin.mine.adapter.OnItemClick;
import com.lechuang.jiabin.mine.adapter.ViewHolderRecycler;
import com.lechuang.jiabin.mine.view.XRecyclerView;
import com.lechuang.jiabin.model.LeCommon;
import com.lechuang.jiabin.model.bean.GetBean;
import com.lechuang.jiabin.model.bean.HomeSearchResultBean;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.GetApi;
import com.lechuang.jiabin.presenter.net.netApi.HomeApi;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.activity.get.GetInfoActivity;
import com.lechuang.jiabin.view.activity.home.KindDetailActivity;
import com.lechuang.jiabin.view.activity.home.ProductDetailsActivity;
import com.lechuang.jiabin.view.activity.home.SearchActivity;
import com.lechuang.jiabin.view.defineView.CustomTabLayout;
import com.lechuang.jiabin.view.defineView.GridItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
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
public class TodayTejiaFragment extends BaseFragment {


    private Context mContext;
    //轮播图
    private XRecyclerView rvProduct;
    //没有网络状态
    private LinearLayout ll_noNet;
    //刷新重试按钮
    private ImageView iv_tryAgain;
    //分页页数
    private int page = 1;
    //参数map
    private HashMap<String, String> allParamMap;
    private CommonRecyclerAdapter mAdapter;
    //商品集合
    private List<HomeSearchResultBean.ProductListBean> mProductList;
    //轮播图链接
    private List<String> linkList;
    //图片集合
    private List<String> imgList;
    Unbinder unbinder;
    private ImageView miVSearch;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.activity_program_detail_recycler, null, false);
        unbinder = ButterKnife.bind(this, content);
        mContext = getActivity();

        //没有网络时的默认图片
        ll_noNet = (LinearLayout) content.findViewById(R.id.ll_noNet);
        //刷新重试
        iv_tryAgain = (ImageView) content.findViewById(R.id.iv_tryAgain);
        rvProduct = (XRecyclerView) content.findViewById(R.id.gv_product);

        content.findViewById(R.id.iv_back).setVisibility(View.GONE);
        content.findViewById(R.id.iv_back2).setVisibility(View.GONE);
        //标题
        ((TextView) content.findViewById(R.id.tv_title)).setText("今日特价");
        miVSearch = content.findViewById(R.id.iv_search);
        miVSearch.setVisibility(View.VISIBLE);

        mProductList = new ArrayList<>();
        linkList = new ArrayList<>();
        imgList = new ArrayList<>();
        allParamMap = new HashMap<>();
        initData();
        getData();

        return content;
    }

    protected void initData() {
        rvProduct.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                if (imgList != null)
                    imgList.clear();
                if (linkList != null)
                    linkList.clear();
                if (mProductList != null)
                    mProductList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();

            }

            @Override
            public void onLoadMore() {
                page += 1;
                getData();
            }
        });

        miVSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext ,SearchActivity.class).putExtra("whereSearch", 1));
            }
        });
    }

    private void getData() {

        allParamMap.put("page", page + "");
        allParamMap.put("type", "9");
        if (Utils.isNetworkAvailable(mContext)) {
            //网络畅通 隐藏无网络状态
            ll_noNet.setVisibility(View.GONE);
            Netword.getInstance().getApi(HomeApi.class)
                    .searchResult(allParamMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<HomeSearchResultBean>(mContext) {
                        @Override
                        public void successed(HomeSearchResultBean result) {
                            kindData(result);
                        }
                    });
        } else {
            //网络不通 展示无网络状态
            ll_noNet.setVisibility(View.VISIBLE);
        }
    }

    private void kindData(HomeSearchResultBean result) {
        if (result == null)
            return;
        ll_noNet.setVisibility(View.GONE);
        //商品集合
        List<HomeSearchResultBean.ProductListBean> list = result.productList;
        for (int i = 0; i < list.size(); i++) {
            mProductList.add(list.get(i));
        }
        if (list.toString().equals("[]")) {            //数据没有了
//            Utils.show(mContext, "亲!已经到底了");
            rvProduct.noMoreLoading();
            return;
        }
        if (page == 1) {
            if (mAdapter == null) {
                mAdapter = new CommonRecyclerAdapter(mContext, R.layout.item_program_product, mProductList) {
                    @Override
                    public void convert(ViewHolderRecycler viewHolder, Object data) {
                        try {
                            HomeSearchResultBean.ProductListBean bean = (HomeSearchResultBean.ProductListBean) data;
                            viewHolder.setText(R.id.tv_xiaoliang, "销量: " + bean.nowNumber + "件");
                            viewHolder.displayRoundImage(R.id.iv_img, bean.imgs);
                            //原件
                            TextView tvOldPrice = viewHolder.getView(R.id.tv_oldprice);
                            tvOldPrice.setText("¥" + bean.price);
                            tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
                            viewHolder.setSpannelTextViewGrid(R.id.tv_name, bean.name, bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
                            viewHolder.setText(R.id.tv_nowprice, "券后价 ¥" + bean.preferentialPrice);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                mLayoutManager.setSmoothScrollbarEnabled(true);

                rvProduct.addItemDecoration(new GridItemDecoration(
                        new GridItemDecoration.Builder(mContext)
                                .margin(4,4)
                                .horSize(16)
                                .verSize(16)
                                .showLastDivider(true)
                ));

                rvProduct.setNestedScrollingEnabled(false);
                rvProduct.setLayoutManager(mLayoutManager);
                rvProduct.setAdapter(mAdapter);
                mAdapter.setOnItemClick(new OnItemClick() {
                    @Override
                    public void itemClick(View v, int position) {
                        startActivity(new Intent(mContext, ProductDetailsActivity.class)
                                .putExtra(Constants.listInfo, JSON.toJSONString(mProductList.get(position))));
                    }
                });
            }
        } else {

        }
        mAdapter.notifyDataSetChanged();
        rvProduct.refreshComplete();
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/22  12:22
     * @describe 中间viewPager和fragment联动
     */
    private void initFragment() {

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
                page = 1;
                if (imgList != null)
                    imgList.clear();
                if (linkList != null)
                    linkList.clear();
                if (mProductList != null)
                    mProductList.clear();
                getData();
                break;
        }

    }

}
