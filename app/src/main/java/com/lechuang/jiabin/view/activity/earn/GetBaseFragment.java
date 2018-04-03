package com.lechuang.jiabin.view.activity.earn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.jiabin.mine.adapter.OnItemClick;
import com.lechuang.jiabin.mine.adapter.ViewHolderRecycler;
import com.lechuang.jiabin.mine.view.XRecyclerView;
import com.lechuang.jiabin.model.LeCommon;
import com.lechuang.jiabin.model.bean.GetBean;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.GetApi;
import com.lechuang.jiabin.view.activity.home.ProductDetailsActivity;
import com.lechuang.jiabin.view.activity.own.ApplyAgentActivity;
import com.lechuang.jiabin.view.activity.ui.LoginActivity;
import com.lechuang.jiabin.view.defineView.SpannelTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetBaseFragment extends ViewPagerFragment implements OnItemClick, XRecyclerView.LoadingListener {

    private CommonRecyclerAdapter mAdapter;
    private XRecyclerView rvProduct;
    private ImageView ivTop;
    private int page = 1;
    private int pageFlag = 0;
    private int rootId;//当前页面的分类id
    private List<GetBean.ListInfo> mProductList = new ArrayList<>();
    private SharedPreferences sp;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout commonLoading;

    public GetBaseFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_get_base, container, false);
        }

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        rootId = getArguments().getInt(LeCommon.KEY_ROOT_ID);
        rvProduct = (XRecyclerView) rootView.findViewById(R.id.get_product);
        ivTop = (ImageView) rootView.findViewById(R.id.iv_top);
        commonLoading = (RelativeLayout) rootView.findViewById(R.id.common_loading_all);

        initView();

        return rootView;
    }


    private void initView() {
        rvProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastVisibleItemPosition() > 15) {
                    ivTop.setVisibility(View.VISIBLE);
                } else {
                    ivTop.setVisibility(View.GONE);
                }
            }

        });
        ivTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvProduct.smoothScrollToPosition(0);
            }
        });
        rvProduct.setLoadingListener(GetBaseFragment.this);
    }


    private void getInfoList() {
        if (1 == page) {
            commonLoading.setVisibility(View.VISIBLE);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        if (rootId != -1) {
            map.put("classTypeId", rootId);
        }

        Netword.getInstance().getApi(GetApi.class)
                .listInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<GetBean>(getActivity()) {
                    @Override
                    public void successed(GetBean result) {
                        commonLoading.setVisibility(View.GONE);
                        if (result == null) return;
                        final List<GetBean.ListInfo> list = result.productList;
                        if (page > 1 && list.isEmpty()) {
                            //数据没有了
                            rvProduct.noMoreLoading();
                            return;
                        }
                        if (pageFlag != page) {
                            mProductList.addAll(list);
                        }

                        if (page == 1) {
                            if (mAdapter == null) {
                                mAdapter = new CommonRecyclerAdapter(getActivity(), R.layout.item_get, mProductList) {
                                    @Override
                                    public void convert(ViewHolderRecycler helper, Object data) {
                                        try {
                                            GetBean.ListInfo item = (GetBean.ListInfo) data;

//                                            Glide.with(getActivity()).load(item.imgs).into((ImageView) helper.getView(R.id.img));
                                            helper.displayImage(R.id.img, item.imgs, R.drawable.ic_get_default);
                                            Glide.get(getActivity()).setMemoryCategory(MemoryCategory.LOW);

                                            helper.setText(R.id.price, item.price);
                                            ((TextView) helper.getView(R.id.price)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                            helper.setText(R.id.preferentialPrice, "¥ " + item.preferentialPrice);
                                            helper.setText(R.id.nowNumber, "已售出 " + item.nowNumber + " 件");
                                            helper.setText(R.id.couponMoney, item.couponMoney + "元");
                                            //helper.setText(R.id.zhuanMoney, String.format("赚:%s", listInfo.zhuanMoney));
//                                            if (sp.getInt("isAgencyStatus", 0) != 1) {
//                                                //不是代理 不显示赚
//                                                helper.setText(R.id.zhuanMoney, "");
//                                            } else {
//                                                helper.setText(R.id.zhuanMoney, item.zhuanMoney);
//                                            }
                                            helper.setText(R.id.zhuanMoney, item.zhuanMoney);
                                            SpannelTextView productName = helper.getView(R.id.spannelTextView);
                                            productName.setShopType(item.shopType == null ? 1 : Integer.parseInt(item.shopType));
                                            productName.setDrawText(item.productName);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                mLayoutManager.setSmoothScrollbarEnabled(true);

                                rvProduct.setNestedScrollingEnabled(false);
                                rvProduct.setLayoutManager(mLayoutManager);
                                rvProduct.setAdapter(mAdapter);
                                mAdapter.setOnItemClick(GetBaseFragment.this);
                            }
                        } else {

                        }
                        mAdapter.notifyDataSetChanged();
                        rvProduct.refreshComplete();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        commonLoading.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void itemClick(View v, int position) {
        boolean isLogin = sp.getBoolean(LeCommon.KEY_HAS_LOGIN, false);
        if (!isLogin) {
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), 0, null);
        } else {
            int isAgency = sp.getInt(LeCommon.KEY_AGENCY_STATUS, 0);
            if (isAgency == 0) {
                startActivity(new Intent(getActivity(), ApplyAgentActivity.class));
            } else {
                startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                        .putExtra(Constants.listInfo, JSON.toJSONString(mProductList.get(position))));
            }
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        if (mProductList != null) {
            mProductList.clear();
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
        getInfoList();
    }

    @Override
    public void onLoadMore() {
        page += 1;
        getInfoList();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            getInfoList();
        } else {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
