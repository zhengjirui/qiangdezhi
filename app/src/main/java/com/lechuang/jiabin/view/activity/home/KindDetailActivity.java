package com.lechuang.jiabin.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseActivity;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.jiabin.mine.adapter.OnItemClick;
import com.lechuang.jiabin.mine.adapter.ViewHolderRecycler;
import com.lechuang.jiabin.mine.view.XRecyclerView;
import com.lechuang.jiabin.model.bean.HomeSearchResultBean;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.HomeApi;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.defineView.GridItemDecoration;
import com.lechuang.jiabin.view.defineView.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/3
 * @E-mail 1422947831@qq.com
 * @desc 栏目详情
 */
public class KindDetailActivity extends BaseActivity implements OnItemClick {

    private Context mContext = KindDetailActivity.this;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductList = new ArrayList<>();
        linkList = new ArrayList<>();
        imgList = new ArrayList<>();
        allParamMap = new HashMap<>();
        getData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_program_detail_recycler;
    }

    @Override
    protected void initTitle() {
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
        //标题
        ((TextView) findViewById(R.id.tv_title)).setText(getIntent().getStringExtra("name"));
        findViewById(R.id.iv_search).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_search).setOnClickListener(this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //没有网络时的默认图片
        ll_noNet = (LinearLayout) findViewById(R.id.ll_noNet);
        //刷新重试
        iv_tryAgain = (ImageView) findViewById(R.id.iv_tryAgain);
        iv_tryAgain.setOnClickListener(this);
        rvProduct = (XRecyclerView) findViewById(R.id.gv_product);

    }

    @Override
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
    }


    private void getData() {
        allParamMap.put("page", page + "");
        allParamMap.put("type", getIntent().getIntExtra("type", 1) + "");
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

    //商品头图片 淘宝或天猫
    private int headImg;

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
                mAdapter.setOnItemClick(KindDetailActivity.this);
            }
        } else {

        }
        mAdapter.notifyDataSetChanged();
        rvProduct.refreshComplete();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_tryAgain://刷新重试
                page = 1;
                if (imgList != null)
                    imgList.clear();
                if (linkList != null)
                    linkList.clear();
                if (mProductList != null)
                    mProductList.clear();
                getData();
                break;
            // 去搜索
            case R.id.iv_search:
                startActivity(new Intent(KindDetailActivity.this, SearchActivity.class).putExtra("whereSearch", 1));
                break;
            default:
                break;
        }
    }


    @Override
    public void itemClick(View v, int position) {
        startActivity(new Intent(mContext, ProductDetailsActivity.class)
                .putExtra(Constants.listInfo, JSON.toJSONString(mProductList.get(position))));
    }
}