package com.lechuang.jiabin.view.activity.get;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseActivity;
import com.lechuang.jiabin.model.LeCommon;
import com.lechuang.jiabin.model.ShareModel;
import com.lechuang.jiabin.model.bean.GetBean;
import com.lechuang.jiabin.model.bean.ShareImageBean;
import com.lechuang.jiabin.model.bean.TaobaoUrlBean;
import com.lechuang.jiabin.presenter.ToastManager;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.QUrl;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.CommenApi;
import com.lechuang.jiabin.presenter.net.netApi.GetApi;
import com.lechuang.jiabin.utils.QRCodeUtils;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.activity.earn.CommonPagerAdapter;
import com.lechuang.jiabin.view.activity.earn.GetBaseFragment;
import com.lechuang.jiabin.view.activity.home.SearchActivity;
import com.lechuang.jiabin.view.defineView.CustomTabLayout;
import com.lechuang.jiabin.view.defineView.SpannelTextView;
import com.lechuang.jiabin.view.dialog.ShareDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sharesdk.onekeyshare.ShowShareDialog;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GetShareActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_get);
        unbinder = ButterKnife.bind(this);
        initView();
    }


    protected void initView() {
        etTopSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GetShareActivity.this, SearchActivity.class));
            }
        });
        ivShuoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GetShareActivity.this,GetInfoActivity.class));
            }
        });
        getData();
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


    private void getData() {
        if (Utils.isNetworkAvailable(this)) {
//            refreshScrollView.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.VISIBLE);
            llNoNet.setVisibility(View.GONE);
            Netword.getInstance().getApi(GetApi.class)
                    .topTabList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<GetBean>(this) {
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


    private void initFragment() {
        fragments = new ArrayList<>();
        for (GetBean.TopTab tab : topTabList) {
            fragments.add(setTitle(new GetBaseFragment(), tab.rootId, tab.rootName));
        }
        //设置适配器
        CommonPagerAdapter mPaperAdapter = new CommonPagerAdapter(getSupportFragmentManager());
        mPaperAdapter.addFragment(fragments, topTabList);
//        vpGet.setOffscreenPageLimit(3);
        vpGet.setAdapter(mPaperAdapter);
        //设置tablout 滑动模式
        tablayoutGet.setTabMode(TabLayout.MODE_SCROLLABLE);
        //联系tabLayout和viwpager
        tablayoutGet.setupWithViewPager(vpGet);
    }

    @OnClick({R.id.iv_tryAgain,R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_close:
                finish();
            break;
            case R.id.iv_tryAgain:
                if (Utils.isNetworkAvailable(this)) {
                    getData();
                } else {
                    showShortToast(getString(R.string.net_error));
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void showShortToast(String msg) {
        ToastManager.getInstance().showShortToast(msg);
    }
}
