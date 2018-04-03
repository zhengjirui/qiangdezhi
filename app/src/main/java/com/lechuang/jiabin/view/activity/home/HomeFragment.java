package com.lechuang.jiabin.view.activity.home;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.lechuang.jiabin.MainActivity;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseFragment;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.model.bean.HomeBannerBean;
import com.lechuang.jiabin.model.bean.HomeKindBean;
import com.lechuang.jiabin.model.bean.HomeLastProgramBean;
import com.lechuang.jiabin.model.bean.HomeProgramBean;
import com.lechuang.jiabin.model.bean.HomeScrollTextViewBean;
import com.lechuang.jiabin.model.bean.HomeSearchResultBean;
import com.lechuang.jiabin.presenter.CommonAdapter;
import com.lechuang.jiabin.presenter.adapter.BannerAdapter;
import com.lechuang.jiabin.presenter.adapter.HomeRecyclerAdapter;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.HomeApi;
import com.lechuang.jiabin.utils.AnimationUtils;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.activity.SigneActivity;
import com.lechuang.jiabin.view.activity.ui.LoginActivity;
import com.lechuang.jiabin.view.defineView.AutoTextView;
import com.lechuang.jiabin.view.defineView.MGridView;
import com.lechuang.jiabin.view.defineView.ViewHolder;

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

/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class HomeFragment extends BaseFragment {
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
    @BindView(R.id.tl_tab)
    TabLayout tlTab;
    @BindView(R.id.rv_home)
    RecyclerView rvHome;
    @BindView(R.id.tv_icon_tab)
    ImageView tvIconTab;
    @BindView(R.id.tv_tab)
    TextView tvTab;
    @BindView(R.id.mg_item)
    MGridView mgItem;
    @BindView(R.id.tv_sale)
    TextView tvSale;
    @BindView(R.id.v_sale)
    View vSale;
    @BindView(R.id.ll_sale)
    LinearLayout llSale;
    @BindView(R.id.tv_like)
    TextView tvLike;
    @BindView(R.id.v_like)
    View vLike;
    @BindView(R.id.ll_like)
    LinearLayout llLike;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.iv_price)
    ImageView ivPrice;
    @BindView(R.id.v_price)
    View vPrice;
    @BindView(R.id.ll_price)
    LinearLayout llPrice;
    @BindView(R.id.tv_new)
    TextView tvNew;
    @BindView(R.id.v_new)
    View vNew;
    @BindView(R.id.ll_new)
    LinearLayout llNew;
    @BindView(R.id.ll_option)
    LinearLayout llOption;
    @BindView(R.id.ll_content_item)
    LinearLayout llContentItem;
    @BindView(R.id.gv_kind)
    MGridView gvKind;

    private View v;
    private View contentView;
    private ArrayList<String> text = null;
    //轮播图
    private RollPagerView mRollViewPager;

    //刷新重试
    //private ImageView tryAgain;
    private PullToRefreshScrollView refreshScrollView;
    private ScrollView scrollView;
    private TextView tv;
    private HomeLastProgramBean.ListBean bean;
    private int postion;  //顶部当前选中的位置
    private String str[] = {"今日特价", "9块9", "热销榜", "今日必抢", "省钱爆料"};
    private int images[] = {R.drawable.icon_jinritejia, R.drawable.icon_9, R.drawable.icon_rexiao,
            R.drawable.icon_biqiang,R.drawable.icon_biaoliao};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
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
            refreshScrollView.setVisibility(View.VISIBLE);
            llNoNet.setVisibility(View.GONE);
            initHeight();
            //获取首页轮播图数据
            getHomeBannerData();
            //首页分类数据
            getHomeKindData();
            //首页滚动文字数据
            getHomeScrollTextView();
            //首页4个图片栏目数据
            getHomeProgram();

        } else {
            //隐藏加载框
            hideWaitDialog();
            llNoNet.setVisibility(View.VISIBLE);//刷新重试
            refreshScrollView.setVisibility(View.GONE);
            showShortToast(getString(R.string.net_error));
        }

    }


    private void initView() {
        refreshScrollView = (PullToRefreshScrollView) v.findViewById(R.id.refreshScrollView);
        scrollView = refreshScrollView.getRefreshableView();
        //轮播图
        mRollViewPager = (RollPagerView) v.findViewById(R.id.rv_banner);
        lastRollViewPager = (ImageView) v.findViewById(R.id.lastRollViewPager);
        //refreshScrollView最上方显示,防止refreshScrollView初始化时不在最上方
        //获取焦点  必须先获取焦点才能在顶部  另外内部的listview gridView不能有焦点
        refreshScrollView.setFocusable(true);
        refreshScrollView.setFocusableInTouchMode(true);
        refreshScrollView.requestFocus();
        refreshScrollView.getRefreshableView().scrollTo(0, 0);

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

    private void initEvent() {
        //tryAgain = (ImageView) v.findViewById(R.id.tryAgain);

        //刷新监听
        refreshScrollView.setOnRefreshListener(refresh);
        if (contentView == null) {
            contentView = scrollView.getChildAt(0);
        }
        //监听ScrollView滑动停止
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(
                                    touchEventId, scroller), 5);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            public boolean onTouch(View v, MotionEvent event) {


                if (isOpen) {
                    isOpen = !isOpen;
                    llOption.setVisibility(View.GONE);
                    llContentItem.setVisibility(View.GONE);
                    AnimationUtils.getUpRemoteAnim(tvIconTab).start();
                }


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(
                            handler.obtainMessage(touchEventId, v), 5);
                }
                return false;
            }

            private void handleStop(Object view) {

                doOnBorderListener();
            }
        });
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
                                } else if (programaId == 5) {
                                    //栏目5
                                    handler.sendEmptyMessage(1);
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

    //获取首页分类数据
    private void getHomeKindData() {
        //首页分类数据
        Netword.getInstance().getApi(HomeApi.class)
                .homeClassify()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeKindBean>(getActivity()) {
                    @Override
                    public void successed(HomeKindBean result) {
                        if (result == null) {
                            return;
                        }
                        List<HomeKindBean.ListBean> list = result.tbclassTypeList;
                        //取前11类
                        if (list.size() > 11) {
                            topTabList.clear();
                            list = list.subList(0, 11);
                        }
                        List<HomeKindBean.ListBean> tabList = list;
                        tabList.subList(0, 11);
                        if (tabList != null) {
                            topTabList.addAll(tabList);
                        }
//                        initFragment();
                        initTabView();
                        initGride();

                    }
                });
    }

    public int oldPosition = 0;

    /**
     * 初始化，
     */
    private void initGride() {
        mgItem.setAdapter(new CommonAdapter<HomeKindBean.ListBean>(getActivity(), topTabList, R.layout.home_kinds_item) {

            @Override
            public void setData(ViewHolder viewHolder, Object item) {
                HomeKindBean.ListBean item1 = (HomeKindBean.ListBean) item;
                TextView view = viewHolder.getView(R.id.tv_kinds_name);
                view.setGravity(Gravity.CENTER);
               /* if(layoutParams==null){
                    layoutParams= view.getLayoutParams();
                    WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                    int width = systemService.getDefaultDisplay().getWidth()/6-5;
                    layoutParams.width=width;
                }
              view.setLayoutParams(layoutParams);*/
                view.setText(item1.rootName);
            }
        });
        mgItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tlTab.setScrollPosition(position, 0, true);
                postion = position;
                rootId = topTabList.get(HomeFragment.this.postion).rootId;
                llOption.setVisibility(View.VISIBLE);
                changeView(position, oldPosition);
                oldPosition = position;
                initRecyclerData();

            }
        });

        //changeView(tlTab.getSelectedTabPosition(), 0);

    }

    //改变列表显示
    public void changeView(int position, int oldPostion) {
        ((TextView) ((LinearLayout) mgItem.getChildAt(oldPostion)).getChildAt(0)).setSelected(false);
        ((TextView) ((LinearLayout) mgItem.getChildAt(oldPostion)).getChildAt(0)).setTextColor(getResources().getColor(R.color.c676767));
        ((TextView) ((LinearLayout) mgItem.getChildAt(position)).getChildAt(0)).setSelected(true);
        ((TextView) ((LinearLayout) mgItem.getChildAt(position)).getChildAt(0)).setTextColor(getResources().getColor(R.color.rgb_F95657));

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


    //tablayoute标题
    private List<HomeKindBean.ListBean> topTabList = new ArrayList<>();
    private int rootId = -1;
    //底部数据展示
    private HomeRecyclerAdapter mAdapter;
    private List<HomeSearchResultBean.ProductListBean> productList = new ArrayList<>();


    /**
     * 初始化tablayout
     */
    private void initTabView() {
        initRecyclerData();
        for (int i = 0; i < topTabList.size(); i++) {
            TabLayout.Tab tab = tlTab.newTab();
            tab.setText(topTabList.get(i).rootName).setTag(i);
            tlTab.addTab(tab);
        }
        //设置tablout 滑动模式
        tlTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        tlTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //记录当前位置
                postion = (int) tab.getTag();
                rootId = topTabList.get(HomeFragment.this.postion).rootId;
                page = 1;
                if (isOpen)
                    changeView(postion, oldPosition);
                initRecyclerData();
                oldPosition = postion;
                //ToastManager.getInstance().showShortToast("onTabSelected" + postion);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    HashMap<String, String> map = new HashMap<>();
    SharedPreferences sp;
    public int showStyle = 0;
    public boolean isHighToDown = true;

    /**
     * 初始化底部数据
     */
    private void initRecyclerData() {
        showWaitDialog("");
        map.clear();
        map.put("page", page + "");
        if (rootId != -1) {  //Integer classTypeId   分类id,精选不传
            map.put("classTypeId", rootId + "");
        } else {
            if (map.containsKey("classTypeId")) {
                map.remove("classTypeId");
            }
            //Integer is_official   精选传1,其他不传
            map.put("is_official", 1 + "");
        }
        if (showStyle == 0) {
            //按销量
            map.put("isVolume", 1 + "");
        } else if (showStyle == 1) {
            //按好评排序
            map.put("isAppraise", 1 + "");
        } else if (showStyle == 2) {
            //按价格排序
            /**
             * isPrice 1 价格从低到高排序
             * isPrice 2 价格从高到低排序
             */
            if (isHighToDown) {
                //价格从高到低
                map.put("isPrice", 2 + "");
            } else {

                map.put("isPrice", 1 + "");
            }
        } else if (showStyle == 3) {
            //按新品排序
            map.put("isNew", 1 + "");
        }
        Netword.getInstance().getApi(HomeApi.class)
                .searchResult(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeSearchResultBean>(getActivity()) {
                    @Override
                    public void successed(HomeSearchResultBean result) {
                        if (result == null)
                            return;
                        List<HomeSearchResultBean.ProductListBean> list = result.productList;
                        refreshScrollView.setMode(list.size() > 0 ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
                        if (page == 1) {
                            productList.clear();
                        }
                        productList.addAll(list);
                        if (page > 1 && list.isEmpty()) {
                            Utils.show(getActivity(), "亲,已经到底了~");
                            return;
                        }
                        int isAgencyStatus = sp.getInt("isAgencyStatus", 0);

                        if (mAdapter == null) {
//                            mAdapter = new HomeRecyclerAdapter(productList, getContext(), isAgencyStatus);
                            rvHome.setLayoutManager(new LinearLayoutManager(getContext()));
                            rvHome.setAdapter(mAdapter);
                            mAdapter.setOnItemClickLitener(new HomeRecyclerAdapter.OnItemClickLitener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                                            .putExtra(Constants.listInfo, JSON.toJSONString(productList.get(position))));
                                }
                            });
                            rvHome.setNestedScrollingEnabled(false);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        hideWaitDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        hideWaitDialog();
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
                        try {
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    //顶部的条目
    private ArrayList<HomeLastProgramBean.ListBean> lastProgramList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int high = v.findViewById(R.id.iv_jump).getTop();
                            refreshScrollView.getRefreshableView().smoothScrollTo(0, high);
                        }
                    }).start();
                    break;
                default:
                    break;
            }

        }
    };

    public boolean isOpen;

    @OnClick({R.id.tv_search, R.id.ll_sale, R.id.ll_like, R.id.ll_price, R.id.tv_sign, R.id.iv_renwu, R.id.iv_top,
            R.id.iv_tryAgain, R.id.ll_new, R.id.tv_icon_tab, R.id.tv_tab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_icon_tab:
                changeItem();
                break;
            case R.id.tv_tab:
                changeItem();
                break;
            case R.id.ll_sale: //按销量排序
                //style = "isVolume=1";
                showStyle = 0;
                selectShowStyle(showStyle);
                page = 1;

                initRecyclerData();
                break;
            case R.id.ll_like://按好评排序
                //style = "isAppraise=1";
                showStyle = 1;
                selectShowStyle(showStyle);
                page = 1;
                initRecyclerData();
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
                    ivPrice.setImageResource(R.drawable.shousuohou_jiage_shang);
                    isHighToDown = !isHighToDown;
                    page = 1;
                    if (productList != null)
                        productList.clear();
                    initRecyclerData();

                } else {
                    // style = "isPrice=1";
                    ivPrice.setImageResource(R.drawable.sousuohou_jiage_xia);
                    isHighToDown = !isHighToDown;
                    page = 1;
                    initRecyclerData();
                }
                break;
            case R.id.ll_new:  //按新品排序
                //style = "isNew=1";
                showStyle = 3;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                initRecyclerData();
                break;

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
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        //  scrollView.fullScroll(ScrollView.FOCUS_DOWN);滚动到底部
                        //  scrollView.fullScroll(ScrollView.FOCUS_UP);滚动到顶部
                        //  需要注意的是，该方法不能直接被调用
                        //  因为Android很多函数都是基于消息队列来同步，所以需要异步操作，
                        //  addView完之后，不等于马上就会显示，而是在队列中等待处理，如果立即调用fullScroll， view可能还没有显示出来，所以会失败
                        //  应该通过handler在新线程中更新
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                ivTop.setVisibility(View.GONE);
                break;
            case R.id.iv_tryAgain:
                showWaitDialog("");
                //刷新数据
                page = 1;
                //清空商品集合
                if (lastProgramList != null)
                    lastProgramList.clear();
                getData();
                break;
            default:
                break;
        }
    }

    /**
     * 获得真布局高度,隐藏选择界面
     */
    private void initHeight() {
        llContentItem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = llContentItem.getHeight();
                llContentItem.setTranslationY(-height);
                llContentItem.setVisibility(View.GONE);
                llContentItem.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * 改变列表条目
     */
    public void changeItem() {
        isOpen = !isOpen;
        if (isOpen) {
            llContentItem.setVisibility(View.VISIBLE);
            AnimationUtils.getDownAnim(llContentItem).start();
            AnimationUtils.getDownRemoteAnim(tvIconTab).start();
        } else {
            AnimationUtils.getUpAnim(llContentItem).start();
            AnimationUtils.getUpRemoteAnim(tvIconTab).start();
            llOption.setVisibility(View.GONE);
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

        View[] v = {tvSale, tvLike,
                tvPrice, tvNew};
        View[] v1 = {vSale, vLike
                , vPrice, vNew};
        for (int i = 0; i < v.length; i++) {
            ((TextView) v[i]).setTextColor(getResources().getColor(R.color.c676767));
        }
        for (int i = 0; i < v1.length; i++) {
            v1[i].setVisibility(View.GONE);
        }

        ((TextView) v[showStyle]).setTextColor(getResources().getColor(R.color.c_F94A4A));
        v1[showStyle].setVisibility(View.VISIBLE);
    }


    /**
     * ScrollView 的顶部，底部判断：
     * 其中getChildAt表示得到ScrollView的child View， 因为ScrollView只允许一个child
     * view，所以contentView.getMeasuredHeight()表示得到子View的高度,
     * getScrollY()表示得到y轴的滚动距离，getHeight()为scrollView的高度。
     * 当getScrollY()达到最大时加上scrollView的高度就的就等于它内容的高度了
     */
    private void doOnBorderListener() {
        // 底部判断
        if (contentView != null
                && contentView.getMeasuredHeight() <= scrollView.getScrollY()
                + scrollView.getHeight()) {
            ivTop.setVisibility(View.VISIBLE);
        }
        // 顶部判断
        else if (scrollView.getScrollY() == 0) {
            ivTop.setVisibility(View.GONE);
            //Log.i(TAG, "top");
        } else if (scrollView.getScrollY() > 30) {
            ivTop.setVisibility(View.VISIBLE);
        }

    }

    //分页
    private int page = 1;
    //刷新加载
    private PullToRefreshBase.OnRefreshListener2 refresh = new PullToRefreshBase.OnRefreshListener2() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {

            // 显示最后更新的时间
            //refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            page = 1;
            //清空商品集合
            if (lastProgramList != null)
                lastProgramList.clear();
            getData();
            refreshScrollView.onRefreshComplete();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            // 显示最后更新的时间
            //refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            showWaitDialog("");
            page += 1;
            initRecyclerData();
            refreshScrollView.onRefreshComplete();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

