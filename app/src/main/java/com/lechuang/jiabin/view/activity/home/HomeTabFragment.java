package com.lechuang.jiabin.view.activity.home;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.lechuang.jiabin.MainActivity;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseFragment;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.model.bean.GetBean;
import com.lechuang.jiabin.model.bean.HomeBannerBean;
import com.lechuang.jiabin.model.bean.HomeLastProgramBean;
import com.lechuang.jiabin.model.bean.HomeProgramBean;
import com.lechuang.jiabin.model.bean.HomeScrollTextViewBean;
import com.lechuang.jiabin.presenter.CommonAdapter;
import com.lechuang.jiabin.presenter.adapter.BannerAdapter;
import com.lechuang.jiabin.presenter.adapter.HomeRecyclerAdapter;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.GetApi;
import com.lechuang.jiabin.presenter.net.netApi.HomeApi;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.activity.SigneActivity;
import com.lechuang.jiabin.view.activity.ui.LoginActivity;
import com.lechuang.jiabin.view.defineView.AutoTextView;
import com.lechuang.jiabin.view.defineView.CustomTabLayout;
import com.lechuang.jiabin.view.defineView.MGridView;
import com.lechuang.jiabin.view.defineView.ViewHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.jiabin.utils.DesignViewUtils.isSlideToBottom;

/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class HomeTabFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {
    protected Subscription subscription;

    @BindView(R.id.tv_search)
    TextView tvSearch;         //搜索
    @BindView(R.id.tv_sign)
    ImageView tvSign;           //签到
    //首页分类
    Unbinder unbinder;
    @BindView(R.id.iv_top)
    ImageView ivTop;        //回到顶部
    @BindView(R.id.tv_auto_text)
    AutoTextView tvAutoText; //自动滚动的textview
    @BindView(R.id.iv_program1)
    ImageView ivProgram1;
    @BindView(R.id.iv_program2)
    ImageView ivProgram2;
    @BindView(R.id.iv_program3)
    ImageView ivProgram3;
    @BindView(R.id.iv_program4)
    ImageView ivProgram4;
    @BindView(R.id.ll_noNet)
    LinearLayout llNoNet; //没有网络
    @BindView(R.id.iv_tryAgain)
    ImageView tryAgain;
    @BindView(R.id.iv_renwu)
    ImageView ivRenwu;
    @BindView(R.id.lastRollViewPager)
    ImageView lastRollViewPager;
    @BindView(R.id.gv_kind)
    MGridView gvKind;

    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.root_layout)
    SwipeRefreshLayout rootLayout;
    @BindView(R.id.tablayout_home)
    TabLayout tabHome;
    @BindView(R.id.ns_scroll)
    NestedScrollView nsScroll;
    //首页最下商品gridview
    @BindView(R.id.rv_home)
    RecyclerView rvHome;

    private View v;
    private ArrayList<String> text = null;
    //轮播图
    private RollPagerView mRollViewPager;
    private SharedPreferences sp;
    public boolean isBottom = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_table, container, false);
        unbinder = ButterKnife.bind(this, v);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initView();
        initEvent();
        getData();
        return v;
    }

    //网络获取数据
    private void getData() {
        //showWaitDialog("");
        if (Utils.isNetworkAvailable(getActivity())) {
            llNoNet.setVisibility(View.GONE);
            //获取首页轮播图数据
            getHomeBannerData();
            //首页分类数据
            getHomeKindData();
            //首页滚动文字数据
            getHomeScrollTextView();
            //首页4个图片栏目数据
            getHomeProgram();
            // tab数据
            getTabData();
            // 商品数据(默认'全部')
            mRootId = 0;
            getProductList();

            rootLayout.setRefreshing(false);
        } else {
            rootLayout.setRefreshing(false);
            //隐藏加载框
            hideWaitDialog();
            llNoNet.setVisibility(View.VISIBLE);//刷新重试
            showShortToast(getString(R.string.net_error));
        }

    }


    private void initView() {
        //轮播图
        mRollViewPager = (RollPagerView) v.findViewById(R.id.rv_banner);
        lastRollViewPager = (ImageView) v.findViewById(R.id.lastRollViewPager);

        nsScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!isBottom && scrollY + v.getHeight() > mProductList.size() * 200 && scrollY > oldScrollY) {
                    isBottom = true;
                    page += 1;
                    showWaitDialog("");
                    getProductList();
                }

                if (scrollY < 200 && ivTop.getVisibility() == View.VISIBLE) {
                    ivTop.setVisibility(View.GONE);
                } else if (scrollY > 200) {
                    ivTop.setVisibility(View.VISIBLE);
                }

            }
        });

        rvHome.addOnScrollListener(new SwipyAppBarScrollListener(appBarLayout, rootLayout, rvHome));
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener(this);
        }
        rootLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getData();
            }
        });

    }

    private void initEvent() {
        //tryAgain = (ImageView) v.findViewById(R.id.tryAgain);
    }

    private List<HomeBannerBean.IndexBannerList> bannerList;

    //获取首页轮播图数据
    private void getHomeBannerData() {
        //首页轮播图数据
        Netword.getInstance().getApi(HomeApi.class)
                .homeBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeBannerBean>(getActivity()) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        hideWaitDialog();
                    }

                    @Override
                    public void successed(HomeBannerBean result) {
                        if (result == null) {
                            return;
                        }
                        bannerList = result.indexBannerList0;
                        List<String> imgList = new ArrayList<>();
                        for (int i = 0; i < bannerList.size(); i++) {
                            imgList.add(bannerList.get(i).img);
                        }
                        //设置播放时间间隔
                        mRollViewPager.setPlayDelay(3000);
                        //设置透明度
                        mRollViewPager.setAnimationDurtion(500);
                        //设置适配器
                        mRollViewPager.setAdapter(new BannerAdapter(getActivity(), imgList));
                        //设置指示器（顺序依次）
                        //自定义指示器图片
                        //设置圆点指示器颜色
                        //设置文字指示器
                        //隐藏指示器
                        //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
                        mRollViewPager.setHintView(new ColorPointHintView(getActivity(), getResources().getColor(R.color.main), Color.WHITE));
                        //mRollViewPager.setHintView(new TextHintView(this));
                        //mRollViewPager.setHintView(null);
                        mRollViewPager.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                //获取到点击条目
                                int programaId = bannerList.get(position).programaId;
                                if (programaId == 1) {
                                    //栏目1
                                    startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                            .putExtra("programaId", 1));
                                } else if (programaId == 2) {
                                    //栏目2
                                    startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                            .putExtra("programaId", 2));
                                } else if (programaId == 3) {
                                    //栏目3
                                    startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                            .putExtra("programaId", 3));
                                } else if (programaId == 4) {
                                    //栏目4
                                    startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                            .putExtra("programaId", 4));
                                } else {
                                    // TODO: 2017/10/1 跳转奔溃
                                    startActivity(new Intent(getActivity(), EmptyWebActivity.class)
                                            .putExtra("url", bannerList.get(position).link));
                                }
                            }
                        });
                    }
                });
    }

    private String str[] = {"今日特价", "9块9", "热销榜", "今日必抢","省钱爆料"};
    private int images[] = {R.drawable.icon_jinritejia, R.drawable.icon_9, R.drawable.icon_rexiao,
            R.drawable.icon_biqiang,R.drawable.icon_biaoliao};
    //获取首页分类数据
    private void getHomeKindData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", images[i]);
            map.put("name", str[i]);
            list.add(map);
        }
        gvKind.setAdapter(new CommonAdapter<Map<String, Object>>(getActivity(), list, R.layout.home_kinds_item1) {
            @Override
            public void setData(ViewHolder viewHolder, Object item) {
                viewHolder.getView(R.id.iv_kinds_img).setVisibility(View.GONE);
                viewHolder.getView(R.id.iv_own).setVisibility(View.VISIBLE);
                HashMap<String, Object> map = (HashMap<String, Object>) item;
                String name = (String) map.get("name");
                int img = (int) map.get("image");
                viewHolder.setText(R.id.tv_kinds_name, name);
                viewHolder.setImageResource(R.id.iv_own, img);
            }
        });
        gvKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                .putExtra("type", 9).putExtra("name", "今日特价"));
                        break;

                    case 1:
                        startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                .putExtra("type", 2).putExtra("name", "9.9包邮"));
                        break;
                    case 2:
                        startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                .putExtra("type", 7).putExtra("name", "热销榜"));
                        break;
                    case 3:
                        startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                .putExtra("type", 8).putExtra("name", "今日必抢"));
                        break;
                    case 4:
//                        startActivity(new Intent(getActivity(), KindDetailActivity.class)
//                                .putExtra("type", 5).putExtra("name", "省钱爆料"));
                        MainActivity activity = (MainActivity) getActivity();
                        activity.showCurrentFragment(1);
                        break;
                }
            }
        });
    }

    //首页滚动文字数据
    private void getHomeScrollTextView() {
        Netword.getInstance().getApi(HomeApi.class)
                .homeScrollTextView()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeScrollTextViewBean>(getActivity()) {
                    @Override
                    public void successed(HomeScrollTextViewBean result) {
                        if (result == null) {
                            return;
                        }
                        final List<HomeScrollTextViewBean.IndexMsgListBean> list = result.indexMsgList;
                        //滚动TextView
                        text = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            text.add(list.get(i).productName);
                        }
                        //自定义的滚动textview
                        tvAutoText.setTextAuto(text);
                        //设置时间
                        tvAutoText.setGap(3000);
                        tvAutoText.setOnItemClickListener(new AutoTextView.onItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                               /* if (sp.getBoolean("isLogin", false)) {*/
                                startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                                        .putExtra(Constants.listInfo, JSON.toJSONString(list.get(position))));
                               /* } else {
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                }*/
                            }
                        });
                    }
                });
    }

    //首页4个图片栏目数据
    private void getHomeProgram() {
        Netword.getInstance().getApi(HomeApi.class)
                .homeProgramaImg()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeProgramBean>(getActivity()) {
                    @Override
                    public void successed(HomeProgramBean result) {
                        if (result == null) {
                            return;
                        }
                        final List<HomeProgramBean.ListBean> list = result.programaImgList;
                        List<String> imgList = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i) != null)
                                imgList.add(list.get(i).img);
                        }
                        //栏目1
                        if (imgList.get(0) != null)
                            Glide.with(getActivity()).load(imgList.get(0)).into(ivProgram1);
                        //栏目2
                        if (imgList.get(1) != null)
                            Glide.with(getActivity()).load(imgList.get(1)).into(ivProgram2);
                        //栏目3
                        if (imgList.get(2) != null)
                            Glide.with(getActivity()).load(imgList.get(2)).into(ivProgram3);
                        //栏目4
                        if (imgList.get(3) != null)
                            Glide.with(getActivity()).load(imgList.get(3)).into(ivProgram4);
                        ivProgram1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(0).programaId));
                            }
                        });
                        ivProgram2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(1).programaId));
                            }
                        });
                        ivProgram3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(2).programaId));
                            }
                        });
                        ivProgram4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(3).programaId));
                            }
                        });
                    }
                });
    }

    //顶部的条目
    private ArrayList<HomeLastProgramBean.ListBean> lastProgramList = new ArrayList<>();

    @OnClick({R.id.tv_search, R.id.tv_sign, R.id.iv_renwu, R.id.iv_top,R.id.iv_tryAgain})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_renwu:
                MainActivity activity = (MainActivity) getActivity();
                activity.showCurrentFragment(2);
                //搜索
                break;
            case R.id.tv_search:
                startActivity(new Intent(getActivity(), SearchActivity.class).putExtra("whereSearch", 1));
                break;
            //签到
            case R.id.tv_sign:
                if (sp.getBoolean("isLogin", false) == true) {
                    startActivity(new Intent(getActivity(), SigneActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            //底部跳转到顶部的按钮
            case R.id.iv_top:
                // 回到顶部
                nsScroll.smoothScrollTo(0, 0);
                ivTop.setVisibility(View.GONE);
                break;
            case R.id.iv_tryAgain:
                showWaitDialog("");
                //刷新数据
                page = 1;
                //清空商品集合
                if (mProductList != null)
                    mProductList.clear();
                getData();
                break;
            default:
                break;
        }
    }

    //分页
    private int page = 1;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    // viewpage标题
    private List<GetBean.TopTab> topTabList = new ArrayList<>();

    private void getTabData() {
        if (tabHome != null && tabHome.getTabCount() > 0) {
            topTabList.clear();
            tabHome.removeAllTabs();
        }
        showWaitDialog("");
        if (Utils.isNetworkAvailable(getActivity())) {
            llNoNet.setVisibility(View.GONE);
            Netword.getInstance().getApi(GetApi.class)
                    .topTabList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<GetBean>(getActivity()) {
                        @Override
                        public void successed(GetBean result) {
                            hideWaitDialog();
                            if (result == null) return;
                            GetBean.TopTab topTab = new GetBean.TopTab();
                            topTab.rootId = 0;
                            topTab.rootName = "全部";
                            topTabList.add(topTab);
                            List<GetBean.TopTab> tabList = result.tbclassTypeList;
                            if (tabList != null) {
                                topTabList.addAll(tabList);
                            }

                            for (GetBean.TopTab tab : topTabList) {
                                tabHome.addTab(tabHome.newTab().setText(tab.rootName));
                            }

                            CustomTabLayout.reflex(tabHome);
                            // 为Tab添加响应
                            onSelectTabItem();
                        }
                    });
        } else {
            hideWaitDialog();
            llNoNet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * TabLayout事件监听
     */
    private void onSelectTabItem() {
        //获取Tab的数量
        int tabCount = tabHome.getTabCount();
        for (int position = 0; position < tabCount; position++) {
            TabLayout.Tab tab = tabHome.getTabAt(position);
            if (tab == null) {
                continue;
            }
            // 这里使用到反射，拿到Tab对象后获取Class
            Class c = tab.getClass();
            try {
                //c.getDeclaredField 获取私有属性。
                //“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                Field field = c.getDeclaredField("mView");
                if (field == null) {
                    continue;
                }
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) {
                    continue;
                }
                final int item = position;
                view.setTag(item);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TabLayout child 事件处理
                        mRootId = topTabList.get(item).rootId;
                        page = 1;
                        nsScroll.smoothScrollTo(0, 0);
                        getProductList();
                    }
                });

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // 设置Adapter
    private HomeRecyclerAdapter mAdapter;
    // 底部商品
    private ArrayList<HomeLastProgramBean.ListBean> mProductList = new ArrayList<>();
    // 当前分类ID
    private int mRootId = 0;
    private LinearLayoutManager linearLayoutManager;
    /**
     * 底部商品数据
     */
    private void getProductList() {
        showWaitDialog("");
        HashMap<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("classTypeId", mRootId + "");
//        if (mRootId != 0) {  //Integer classTypeId   分类id,精选不传
//            map.put("classTypeId", mRootId + "");
//        } else {             //Integer is_official   精选传1,其他不传
//            map.put("type", "");
//        }

        subscription = Netword.getInstance().getApi(HomeApi.class)
                .homeLastProgram(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeLastProgramBean>(getActivity()) {
                    @Override
                    public void successed(HomeLastProgramBean result) {
                        hideWaitDialog();
                        if (result == null) return;
                        if (page == 1 && mProductList != null) {
                            mProductList.clear();
                        }
                        List<HomeLastProgramBean.ListBean> list = result.productList;
                        if (page > 1 && list.isEmpty()) {
                            Utils.show(getActivity(), "亲,已经到底了~");
                            hideWaitDialog();
                            return;
                        }
                        mProductList.addAll(list);
                        int isAgencyStatus = sp.getInt("isAgencyStatus", 0);
                        if (page == 1) {
                            //轮播图图片数据集合
                            final List<HomeLastProgramBean.programaBean.ListBean> list1 = result.programa.indexBannerList;
                            Glide.with(getActivity()).load(list1.get(0).img).into(lastRollViewPager);
                            lastRollViewPager.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!TextUtils.isEmpty(list1.get(0).link)) {
                                        startActivity(new Intent(getActivity(), EmptyWebActivity.class)
                                                .putExtra("url", list1.get(0).link));
                                    }

                                }
                            });
//                            mAdapter = new CustomHomeAdapter(mProductList);
//                            rvHome.addItemDecoration(new GridSpacingItemDecoration(9, 8, false));
//                            rvHome.setAdapter(mAdapter);
//                            mAdapter.setOnItemClick(HomeTableFragment.this);
                            mAdapter = new HomeRecyclerAdapter(mProductList, getContext(), isAgencyStatus);
//                            linearLayoutManager = new LinearLayoutManager(getContext());
//                            linearLayoutManager.setSmoothScrollbarEnabled(true);

                            LinearLayoutManager mLinearLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                            mLinearLayout.setSmoothScrollbarEnabled(true);

                            rvHome.setNestedScrollingEnabled(false);
                            rvHome.setLayoutManager(mLinearLayout);
                            rvHome.setAdapter(mAdapter);
                            mAdapter.setOnItemClickLitener(new HomeRecyclerAdapter.OnItemClickLitener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    startActivity(new Intent(getActivity(), ProductDetailsActivity.class
                                    ).putExtra(Constants.listInfo, JSON.toJSONString(mProductList.get(position))));
                                }
                            });
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        isBottom = false;
                        hideWaitDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        hideWaitDialog();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        hideWaitDialog();
                    }
                });
    }

    boolean result;
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (rootLayout == null)
            return;
        boolean tem = isSlideToBottom(rvHome);
        result = verticalOffset >= 0 || tem ? true : false;
        appBarLayout.setEnabled(result);
    }

}

