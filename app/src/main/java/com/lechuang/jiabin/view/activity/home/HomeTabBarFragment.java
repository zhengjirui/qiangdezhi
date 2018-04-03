package com.lechuang.jiabin.view.activity.home;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.lechuang.jiabin.MainActivity;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseFragment;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.jiabin.mine.adapter.OnItemClick;
import com.lechuang.jiabin.mine.adapter.ViewHolderRecycler;
import com.lechuang.jiabin.mine.view.XRecyclerView;
import com.lechuang.jiabin.model.LeCommon;
import com.lechuang.jiabin.model.bean.GetBean;
import com.lechuang.jiabin.model.bean.HomeBannerBean;
import com.lechuang.jiabin.model.bean.HomeLastProgramBean;
import com.lechuang.jiabin.model.bean.HomeProgramBean;
import com.lechuang.jiabin.model.bean.HomeScrollTextViewBean;
import com.lechuang.jiabin.presenter.CommonAdapter;
import com.lechuang.jiabin.presenter.ToastManager;
import com.lechuang.jiabin.presenter.adapter.BannerAdapter;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.GetApi;
import com.lechuang.jiabin.presenter.net.netApi.HomeApi;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.activity.SigneActivity;
import com.lechuang.jiabin.view.activity.get.GetShareActivity;
import com.lechuang.jiabin.view.activity.own.ShareMoneyActivity;
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


/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class HomeTabBarFragment extends BaseFragment implements OnItemClick {
    protected Subscription subscription;

    //首页分类
    Unbinder unbinder;
    //自动滚动的textview
    private AutoTextView tvAutoText;
    private ImageView ivProgram1;
    private ImageView ivProgram2;
    private ImageView ivProgram3;
    private ImageView ivProgram4;
    private ImageView lastRollViewPager;
    private MGridView gvKind;
    private View lineHome;

    // header view中的TabLayout
    private TabLayout tabHome;
    // fragment_home_tablebar中的TabLayout
    @BindView(R.id.tablayout_home_top)
    TabLayout tabHomeTop;
    //首页最下商品gridview
    @BindView(R.id.rv_home_table)
    XRecyclerView rvHome;
    @BindView(R.id.iv_top)
    ImageView ivTop;        //回到顶部
    @BindView(R.id.line_home_tab_top)
    View lineHomeTop;

    private View v;
    private ArrayList<String> text = null;
    //轮播图
    private RollPagerView mRollViewPager;
    private SharedPreferences sp;
    private View header;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case LeCommon.ACTION_LOGIN_SUCCESS:
                case LeCommon.ACTION_LOGIN_OUT:
                    page = 1;
                    rvHome.smoothScrollToPosition(0);
                    ivTop.setVisibility(View.GONE);
                    getData();
                    break;
            }
        }
    };
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_tablebar, container, false);
        unbinder = ButterKnife.bind(this, v);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_SUCCESS));
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_OUT));
        initView();
        initEvent();
        getData();
        return v;
    }

    //网络获取数据
    public void getData() {
        if (Utils.isNetworkAvailable(getActivity())) {

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

        } else {
            //隐藏加载框
            showShortToast(getString(R.string.net_error));
        }

    }


    private void initView() {
        // TODO init XRecyclerView
        header = LayoutInflater.from(getActivity()).inflate(R.layout.header_home_tablebar,
                (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        //轮播图
        mRollViewPager = (RollPagerView) header.findViewById(R.id.rv_banner);
        tabHome = (TabLayout) header.findViewById(R.id.tablayout_home);
        tvAutoText = (AutoTextView) header.findViewById(R.id.tv_auto_text);
        ivProgram1 = (ImageView) header.findViewById(R.id.iv_program1);
        ivProgram2 = (ImageView) header.findViewById(R.id.iv_program2);
        ivProgram3 = (ImageView) header.findViewById(R.id.iv_program3);
        ivProgram4 = (ImageView) header.findViewById(R.id.iv_program4);
        lastRollViewPager = (ImageView) header.findViewById(R.id.lastRollViewPager);
        gvKind = (MGridView) header.findViewById(R.id.gv_kind);
        lineHome = (View) header.findViewById(R.id.line_home_tab);

        rvHome.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mShouldScroll) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mToPosition);
                }

                int[] loc = new int[2];
                tabHome.getLocationInWindow(loc);

                if (tabHomeTop.getHeight() >= loc[1]) {
                    tabHome.setVisibility(View.INVISIBLE);
                    lineHome.setVisibility(View.INVISIBLE);
                    tabHomeTop.setVisibility(View.VISIBLE);
                    lineHomeTop.setVisibility(View.VISIBLE);
                    ivTop.setVisibility(View.VISIBLE);
                } else {
                    tabHome.setVisibility(View.VISIBLE);
                    lineHome.setVisibility(View.VISIBLE);
                    tabHomeTop.setVisibility(View.INVISIBLE);
                    lineHomeTop.setVisibility(View.INVISIBLE);
                    ivTop.setVisibility(View.GONE);
                }
            }
        });

        rvHome.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getData();

            }

            @Override
            public void onLoadMore() {
                page += 1;
                getProductList();
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
                                } else if(programaId == 5){
                                    smoothMoveToPosition(2);
                                }else if(programaId > 5 && programaId <= 10){
                                    //5-10的栏目不处理
                                }else {
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
                            Glide.with(getActivity()).load(imgList.get(0))
                                    .placeholder(R.drawable.ic_official_default).into(ivProgram1);
                        //栏目2
                        if (imgList.get(1) != null)
                            Glide.with(getActivity()).load(imgList.get(1))
                                    .placeholder(R.drawable.ic_bargain_default).into(ivProgram2);
                        //栏目3
                        if (imgList.get(2) != null)
                            Glide.with(getActivity()).load(imgList.get(2))
                                    .placeholder(R.drawable.ic_bargain_default).into(ivProgram3);
                        //栏目4
                        if (imgList.get(3) != null)
                            Glide.with(getActivity()).load(imgList.get(3))
                                    .placeholder(R.drawable.ic_99_default).into(ivProgram4);
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

    @OnClick({R.id.tv_search, R.id.tv_sign, R.id.iv_renwu, R.id.iv_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_renwu:
//                MainActivity activity = (MainActivity) getActivity();
//                activity.showCurrentFragment(2);
                startActivity(new Intent(getActivity(), GetShareActivity.class));
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
                rvHome.smoothScrollToPosition(0);
                ivTop.setVisibility(View.GONE);
                break;
//            case R.id.iv_tryAgain:
//                //刷新数据
//                page = 1;
//                //清空商品集合
//                if (mProductList != null)
//                    mProductList.clear();
//                getData();
//                break;
            default:
                break;
        }
    }

    //分页
    private int page = 1;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        unbinder.unbind();
    }


    // viewpage标题
    private List<GetBean.TopTab> topTabList = new ArrayList<>();

    private void getTabData() {
        if (tabHome != null && tabHome.getTabCount() > 0) {
            topTabList.clear();
            tabHome.removeAllTabs();
        }

        if (Utils.isNetworkAvailable(getActivity())) {

            Netword.getInstance().getApi(GetApi.class)
                    .topTabList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<GetBean>(getActivity()) {
                        @Override
                        public void successed(GetBean result) {
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

                            for (GetBean.TopTab tab : topTabList) {
                                tabHomeTop.addTab(tabHomeTop.newTab().setText(tab.rootName));
                            }

                            CustomTabLayout.reflex(tabHome);
                            CustomTabLayout.reflex(tabHomeTop);
                            // 为Tab添加响应
                            // 切换Tab时,两个TabLayout都需要保存当前选中位置
                            onSelectTabItem();
                            onSelectTopTabItem();
                        }
                    });
        } else {

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
                        Netword.getInstance().getOkHttpClient().connectionPool().evictAll();
                        tabHomeTop.setScrollPosition(item, 0, true);
                        // TabLayout child 事件处理
                        mRootId = topTabList.get(item).rootId;
//                        new Handler().postDelayed(new Runnable() {
//                            public void run() {
                        page = 1;
                        getProductList();
//                                rvHome.smoothScrollToPosition(2);
                        rvHome.scrollToPosition(2);
//                            }
//                        }, 1000);

                    }
                });

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TabLayout事件监听
     */
    private void onSelectTopTabItem() {
        //获取Tab的数量
        int tabCount = tabHomeTop.getTabCount();
        for (int position = 0; position < tabCount; position++) {
            TabLayout.Tab tab = tabHomeTop.getTabAt(position);
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
                        Netword.getInstance().getOkHttpClient().connectionPool().evictAll();
                        tabHome.setScrollPosition(item, 0, true);
                        // TabLayout child 事件处理
                        mRootId = topTabList.get(item).rootId;
//                        new Handler().postDelayed(new Runnable() {
//                            public void run() {
                        page = 1;
                        getProductList();
//                                rvHome.smoothScrollToPosition(2);
                        rvHome.scrollToPosition(2);
//                            }
//                        }, 1000);
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
    private CommonRecyclerAdapter mAdapter;
    // 底部商品
    private ArrayList<HomeLastProgramBean.ListBean> mProductList = new ArrayList<>();
    // 当前分类ID
    private int mRootId = 0;

    /**
     * 底部商品数据
     */
    private void getProductList() {
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
                        if (result == null) return;
                        if (page == 1 && mProductList != null) {
                            mProductList.clear();
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                        }
                        List<HomeLastProgramBean.ListBean> list = result.productList;
                        if (page > 1 && list.isEmpty()) {
//                            Utils.show(getActivity(), "亲,已经到底了~");
                            rvHome.noMoreLoading();
                            return;
                        }
                        mProductList.addAll(list);
                        int isAgencyStatus = sp.getInt("isAgencyStatus", 0);
                        //bug 6. 添加图片的点击事件，还需要过去后台的接口数据(添加数据待完成)
                        if (page == 1) {
                            //轮播图图片数据集合
                            final List<HomeLastProgramBean.programaBean.ListBean> list1 = result.programa.indexBannerList;
//                            Glide.with(getActivity()).load(list1.get(0).img).into(lastRollViewPager);
//                            lastRollViewPager.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if (!TextUtils.isEmpty(list1.get(0).link)) {
//                                        startActivity(new Intent(getActivity(), EmptyWebActivity.class)
//                                                .putExtra("url", list1.get(0).link));
//                                    }
//                                    ToastManager.getInstance().showLongToast("图片点击");
//
//                                }
//                            });
                            if (mAdapter == null) {
                                mAdapter = new CommonRecyclerAdapter(getActivity(), R.layout.item_last_program, mProductList) {
                                    @Override
                                    public void convert(ViewHolderRecycler holder, Object data) {
                                        try {
                                            HomeLastProgramBean.ListBean bean = (HomeLastProgramBean.ListBean) data;
                                            //商品图
//                                            Glide.with(mContext).load(bean.imgs).into((ImageView) holder.getView(R.id.iv_img));
                                            holder.displayImage(R.id.iv_img, bean.imgs, R.drawable.ic_home_default);
                                            //动态调整滑动时的内存占用
                                            Glide.get(mContext).setMemoryCategory(MemoryCategory.LOW);
                                            // 原价
                                            TextView tvZhuan = holder.getView(R.id.tv_get);
                                            if (bean.zhuanMoney != null) {
                                                tvZhuan.setVisibility(View.VISIBLE);
                                                tvZhuan.setText(bean.zhuanMoney);
                                            } else {
                                                tvZhuan.setVisibility(View.GONE);
                                            }
                                            // 领券减
                                            TextView tvCoupon = holder.getView(R.id.tv_quanMoney);
                                            if (bean.couponMoney != null) {
                                                tvCoupon.setVisibility(View.VISIBLE);
                                                tvCoupon.setText("领券减¥" + bean.couponMoney);
                                            } else {
                                                tvCoupon.setVisibility(View.GONE);
                                            }
                                            TextView tvOldPrice = holder.getView(R.id.tv_oldprice);
                                            tvOldPrice.setText(bean.price + "");
                                            // 中划线
                                            tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                                            // 券后价
                                            holder.setText(R.id.tv_nowprice, "¥" + bean.preferentialPrice);


                                            //商品名称
                                            holder.setSpannelTextView(R.id.spannelTextView, bean.name, Integer.parseInt(bean.shopType));
                                            //赚
//            if (isAgencyStatus == 1) {
//                holder.tvGet.setVisibility(View.VISIBLE);
//            } else {
//                holder.tvGet.setVisibility(View.GONE);
//            }
                                            holder.setText(R.id.tv_get, bean.zhuanMoney);
                                            //销量
                                            holder.setText(R.id.tv_xiaoliang, "已抢" + bean.nowNumber + "件");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                mLayoutManager.setSmoothScrollbarEnabled(true);

                                rvHome.addHeaderView(header);
                                rvHome.setNestedScrollingEnabled(false);
                                rvHome.setLayoutManager(mLayoutManager);
                                rvHome.setAdapter(mAdapter);
                                mAdapter.setOnItemClick(HomeTabBarFragment.this);
                            }
                        } else {

                        }

                        mAdapter.notifyDataSetChanged();
                        rvHome.refreshComplete();
                    }

                });
    }

    @Override
    public void itemClick(View v, int position) {
        startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                .putExtra(Constants.listInfo, JSON.toJSONString(mProductList.get(position))));
    }

    /**
     * recycleview滚到指定位置
     */
    private void smoothMoveToPosition(int position){
//        int lanMuHeight = lastRollViewPager.getHeight();
        int tabHomeHeight = tabHome.getHeight();
        // 第一个可见位置
        int firstItem = rvHome.getChildLayoutPosition(rvHome.getChildAt(0));
        // 最后一个可见位置
        int lastItem = rvHome.getChildLayoutPosition(rvHome.getChildAt(rvHome.getChildCount() - 1));

        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            rvHome.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < rvHome.getChildCount()) {
                int top = rvHome.getChildAt(movePosition).getTop();
//                rvHome.smoothScrollBy(0, top - lanMuHeight - tabHomeHeight);
                rvHome.smoothScrollBy(0, top - tabHomeHeight);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            rvHome.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;
}

