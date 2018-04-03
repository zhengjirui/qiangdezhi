package com.lechuang.jiabin.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseActivity;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.jiabin.mine.adapter.OnItemClick;
import com.lechuang.jiabin.mine.adapter.ViewHolderRecycler;
import com.lechuang.jiabin.mine.view.XRecyclerView;
import com.lechuang.jiabin.model.LeCommon;
import com.lechuang.jiabin.model.bean.HomeSearchResultBean;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.HomeApi;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.defineView.SpannelTextView;
import com.lechuang.jiabin.view.defineView.WiperSwitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yrj on 2017/8/17.
 * 搜索结果页面
 */


public class SearchResultActivity extends BaseActivity implements OnItemClick {

    //搜索结果展示的方式  0:按销量展示  1.按好评展示 2.按价格展示  3.按新品展示
    private int showStyle = 0;
    private ImageView iv_price;
    private Context mContext = SearchResultActivity.this;
    //展示在搜索框上的搜索内容
    private String rootName;
    //入参的搜索内容
    private String productName;
    /**
     * 入参的排序方式
     * isVolume 1代表按销量排序从高到底
     * isAppraise 1好评从高到底
     * isPrice  1价格从低到高排序
     * isPrice  2价格从高到低排序
     * isNew    1新品商品冲最近的往后排序
     */
    private String style = "isVolume=1";
    //入参 页数
    private int page = 1;
    //
    //上个界面传递过来的值,用来判断是从分类还是搜索跳过来的 1:分类 2:搜索界面
    private int productstyle = 1;
    //可以刷新的gridview
    private XRecyclerView rvSearch;
    private CommonRecyclerAdapter mAdapter;
    //无网络状态
    private LinearLayout ll_notNet;
    // 没有搜索到商品
    private LinearLayout nothingAll;
    private TextView tvRemind;
    // loading
    private RelativeLayout commonLoading;
    //拼接完的参数
    private String allParameter;
    //刷新重试按钮
    private ImageView iv_tryAgain;
    //商品头图片 淘宝或天猫
    private int headImg;
    //参数map
    private HashMap<String, String> allParamMap;
    private WiperSwitch wiperSwitch;
    //是否人工筛选
    private boolean isPeopleChange = false ;
    //保存用户登录信息的sp
    private SharedPreferences se;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        se = PreferenceManager.getDefaultSharedPreferences(mContext);
        //商品集合
        productList = new ArrayList<>();
        allParamMap = new HashMap<>();

        ll_notNet = (LinearLayout) findViewById(R.id.ll_noNet);
        //刷新重试
        iv_tryAgain = (ImageView) findViewById(R.id.iv_tryAgain);
        iv_tryAgain.setOnClickListener(this);
        iv_price = (ImageView) findViewById(R.id.iv_price);
        rvSearch = (XRecyclerView) findViewById(R.id.gv_search);

        nothingAll = (LinearLayout) findViewById(R.id.search_result_nothing);
        tvRemind = (TextView) findViewById(R.id.tv_remind_nothing);
        commonLoading = (RelativeLayout) findViewById(R.id.common_loading_all) ;

        wiperSwitch = (WiperSwitch) findViewById(R.id.wiperSwitch);
        wiperSwitch.setChecked(isPeopleChange);
        wiperSwitch.setOnChangedListener(new WiperSwitch.OnChangedListener() {
            @Override
            public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
                isPeopleChange = checkState;
                if (productList != null) {
                    productList.clear();
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }

                if (isPeopleChange) {
                    findViewById(R.id.ll_type).setVisibility(View.VISIBLE);
                    findViewById(R.id.line_search_type).setVisibility(View.VISIBLE);
//                    tvRemind.setText(getResources().getString(R.string.remind_nothing_mine));
                } else {
                    findViewById(R.id.ll_type).setVisibility(View.GONE);
                    findViewById(R.id.line_search_type).setVisibility(View.GONE);
//                    tvRemind.setText(getResources().getString(R.string.remind_nothing_all));
                }
                tvRemind.setText(getResources().getString(R.string.remind_nothing_mine));
                commonLoading.setVisibility(View.VISIBLE);
                getData();
            }
        });
    }

    @Override
    protected void initData() {
        productstyle = getIntent().getIntExtra("type", 1);
        if (productstyle == 1) {//1:分类
            productName = "&classTypeId=" + getIntent().getStringExtra("rootId");
        } else {  //2 搜索
            productName = "&name=" + getIntent().getStringExtra("rootId");
        }
        rootName = getIntent().getStringExtra("rootName");
        findViewById(R.id.ll_sale).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.ll_like).setOnClickListener(this);
        findViewById(R.id.ll_price).setOnClickListener(this);
        findViewById(R.id.ll_new).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_search)).setText(rootName);

        rvSearch.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                if (null != productList) {
                    productList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
                getData();

            }

            @Override
            public void onLoadMore() {
                page += 1;
                getData();
            }
        });

        getData();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (null != productList) {
//            productList.clear();
//        }
//        getData();
//    }


    private List<HomeSearchResultBean.ProductListBean> productList;

    //网络请求获取数据
    private void getData() {
        if (page == 1) {
            commonLoading.setVisibility(View.VISIBLE);
        }
        if (allParamMap != null) {
            allParamMap.clear();
        }
        if (Utils.isNetworkAvailable(mContext)) {
            ll_notNet.setVisibility(View.GONE);

            //区分搜索还是分类跳转
            productstyle = getIntent().getIntExtra("type", 1);
            //拼接之后的参数
            //allParameter = "?page=" + page + productName + "&" + style;
            allParamMap.put("page", page + "");
            if (productstyle == 1) {
                //分类
                allParamMap.put("classTypeId", getIntent().getStringExtra("rootId"));
            } else {
                //搜索
                allParamMap.put("name", getIntent().getStringExtra("rootId"));
            }
            //是否人工筛选
            allParamMap.put("flag", isPeopleChange ? 1 + "" : 0 + "");

            if (showStyle == 0) {
                //按销量
                allParamMap.put("isVolume", 1 + "");
            } else if (showStyle == 1) {
                //按好评排序
                allParamMap.put("isAppraise", 1 + "");
            } else if (showStyle == 2) {
                //按价格排序
                /**
                 * isPrice 1 价格从低到高排序
                 * isPrice 2 价格从高到低排序
                 */
                if (isHighToDown) {
                    //价格从高到低
                    allParamMap.put("isPrice", 2 + "");
                } else {
                    allParamMap.put("isPrice", 1 + "");
                }
            } else if (showStyle == 3) {
                //按新品排序
                allParamMap.put("isNew", 1 + "");
            }
            Netword.getInstance().getApi(HomeApi.class)
                    .searchResult(allParamMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<HomeSearchResultBean>(mContext) {
                        @Override
                        public void successed(HomeSearchResultBean result) {
                            commonLoading.setVisibility(View.GONE);
                            if (result == null) {
                                nothingAll.setVisibility(View.VISIBLE);
                                showRemind();
                                return;
                            }
                            List<HomeSearchResultBean.ProductListBean> list = result.productList;
                            if (page == 1 && (list.toString().equals("[]") || list.size() <= 0)) {
                                nothingAll.setVisibility(View.VISIBLE);
                                showRemind();
                                return;
                            }
                            nothingAll.setVisibility(View.GONE);
                            if (productList.size() > 0 && list.toString().equals("[]")) {
//                                showShortToast("亲!已经到底了");
                                rvSearch.noMoreLoading();
                                return;
                            }
                            for (int i = 0; i < list.size(); i++) {
                                productList.add(list.get(i));
                            }
                            //只有page=1 的时候设置适配器 下拉刷新直接调用notifyDataSetChanged()
                            if (page == 1) {

                                if (mAdapter == null) {
                                    mAdapter = new CommonRecyclerAdapter(mContext, R.layout.item_search_product, productList) {
                                        @Override
                                        public void convert(ViewHolderRecycler viewHolder, Object data) {
                                            try {
                                                final HomeSearchResultBean.ProductListBean bean = (HomeSearchResultBean.ProductListBean) data;
                                                viewHolder.displayImage(R.id.iv_img, bean.imgs, R.drawable.ic_search_default);
                                                //原价
                                                TextView oldPrice = viewHolder.getView(R.id.tv_oldprice);
                                                TextView tv_zhuan = viewHolder.getView(R.id.tv_zhuan);
                                                LinearLayout ll_lingquan = viewHolder.getView(R.id.ll_lingquan);
                                                oldPrice.setText(bean.price);
                                                if (se.getInt(LeCommon.KEY_AGENCY_STATUS, 0) == 1) {
                                                    if (bean.zhuanMoney != null) {
                                                        tv_zhuan.setVisibility(View.VISIBLE);
                                                        tv_zhuan.setText(bean.zhuanMoney);
                                                    } else {
                                                        tv_zhuan.setVisibility(View.INVISIBLE);
                                                    }
                                                } else {
                                                    tv_zhuan.setVisibility(View.INVISIBLE);
                                                }
                                                //销量
                                                viewHolder.setText(R.id.tv_number, "已抢" + bean.nowNumber + "件");

                                                //商品名字
                                                if (bean.couponMoney.equals("0") || bean.couponMoney == null) {
                                                    ll_lingquan.setVisibility(View.GONE);
                                                    oldPrice.getPaint().setFlags(0);
                                                } else {
                                                    ll_lingquan.setVisibility(View.VISIBLE);
                                                    viewHolder.setText(R.id.tv_juan, "领券减" + bean.couponMoney);
                                                    oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                                                }
                                      /*  viewHolder.setText(R.id.tv_name, bean.name);*/
                                                //新件
                                                viewHolder.setText(R.id.tv_nowprice, "¥" + bean.preferentialPrice);
                                       /* if (bean.shopType != null && bean.shopType.equals("2")) {
                                            headImg = R.drawable.zhuan_tianmao;
                                        } else {  //shopType 1淘宝 2天猫 默认淘宝
                                            headImg = R.drawable.zhuan_taobao;
                                        }*/
                                                SpannelTextView view1 = viewHolder.getView(R.id.tv_name);
                                                view1.setShopType(Integer.parseInt(bean.shopType));
                                                view1.setDrawText(bean.name);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    LinearLayoutManager mLinearLayout = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                                    mLinearLayout.setSmoothScrollbarEnabled(true);

                                    rvSearch.setNestedScrollingEnabled(false);
                                    rvSearch.setLayoutManager(mLinearLayout);
                                    rvSearch.setAdapter(mAdapter);
                                    mAdapter.setOnItemClick(SearchResultActivity.this);
                                }
                            } else {

                            }
                            mAdapter.notifyDataSetChanged();
                            rvSearch.refreshComplete();
                        }

                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            commonLoading.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void showRemind() {
        tvRemind.setText(getResources().getString(R.string.remind_nothing_mine));
//        if (isPeopleChange) {
//            tvRemind.setText(getResources().getString(R.string.remind_nothing_mine));
//        } else {
//            tvRemind.setText(getResources().getString(R.string.remind_nothing_all));
//        }
    }

    //价格从高到底
    private boolean isHighToDown = true;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.ll_search: //搜索
                startActivity(new Intent(mContext, SearchActivity.class));
                finish();
                break;
            case R.id.ll_sale: //按销量排序
                //style = "isVolume=1";
                showStyle = 0;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;
            case R.id.ll_like://按好评排序
                //style = "isAppraise=1";
                showStyle = 1;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;
            case R.id.ll_price: //按价格排序

                /**
                 * isPrice 1 价格从低到高排序
                 * isPrice 2 价格从高到低排序
                 */
                showStyle = 2;
                selectShowStyle(showStyle);
                if (isHighToDown) {
                    //价格从高到低
                    //style = "isPrice=2";
                    iv_price.setImageResource(R.drawable.shousuohou_jiage_shang);
                    isHighToDown = !isHighToDown;
                    page = 1;
                    if (productList != null)
                        productList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    getData();

                } else {
                    // style = "isPrice=1";
                    iv_price.setImageResource(R.drawable.sousuohou_jiage_xia);
                    isHighToDown = !isHighToDown;
                    page = 1;
                    if (productList != null)
                        productList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    getData();
                }
                break;
            case R.id.ll_new:  //按新品排序
                //style = "isNew=1";
                showStyle = 3;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;

            default:
                break;
        }
    }

    //选择展示的方式
    private void selectShowStyle(int showStyle) {

        if (showStyle == 0) {
            changeStyle(showStyle);
        } else if (showStyle == 1) {
            changeStyle(showStyle);
        } else if (showStyle == 2) {
            changeStyle(showStyle);
        } else if (showStyle == 3) {
            changeStyle(showStyle);
        }
    }


    private void changeStyle(int showStyle) {

        View[] v = {findViewById(R.id.tv_sale), findViewById(R.id.tv_like),
                findViewById(R.id.tv_price), findViewById(R.id.tv_new)};
        View[] v1 = {findViewById(R.id.v_sale), findViewById(R.id.v_like)
                , findViewById(R.id.v_price), findViewById(R.id.v_new)};
        for (int i = 0; i < v.length; i++) {
            ((TextView) v[i]).setTextColor(getResources().getColor(R.color.black));
        }
        for (int i = 0; i < v1.length; i++) {
            v1[i].setVisibility(View.GONE);
        }

        ((TextView) v[showStyle]).setTextColor(getResources().getColor(R.color.main));
        v1[showStyle].setVisibility(View.VISIBLE);
    }

    @Override
    public void itemClick(View v, int position) {
        startActivity(new Intent(mContext, ProductDetailsActivity.class)
                .putExtra(Constants.listInfo, JSON.toJSONString(productList.get(position))));
    }
}
