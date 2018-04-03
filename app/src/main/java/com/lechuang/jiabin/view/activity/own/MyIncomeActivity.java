package com.lechuang.jiabin.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.model.DemoTradeCallback;
import com.lechuang.jiabin.model.LeCommon;
import com.lechuang.jiabin.model.bean.OwnIncomeBean;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.OwnApi;
import com.lechuang.jiabin.view.defineView.PopChoseAgency;
import com.lechuang.jiabin.view.defineView.XCRoundImageView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 我的收益
 *
 * 作者：li on 2017/10/6 14:37
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class MyIncomeActivity extends AppCompatActivity implements PopChoseAgency.OnChoseAgency{
    @BindView(R.id.iv_income_back)
    ImageView ivBackIncome;
    @BindView(R.id.tv_income_nickname)
    TextView tvNickName;
    @BindView(R.id.sum_jinfen)
    TextView mSumJIFen;
    @BindView(R.id.ketixianjifen)
    TextView mKeTiXian;
    @BindView(R.id.buketixianjifen)
    TextView mBuKeTiXian;
    @BindView(R.id.tv_income_forecast_today)
    TextView tvForecastToday;
    @BindView(R.id.tv_income_total_today)
    TextView tvTotalToday;
    @BindView(R.id.tv_income_forecast_month)
    TextView tvForecastMonth;
    @BindView(R.id.tv_income_forecast_lastmonth)
    TextView tvForecastLastMonth;
    @BindView(R.id.iv_income_headImg)
    XCRoundImageView ivHead;
    @BindView(R.id.tv_orderDetail_list)
    TextView tvOrderDetail;
    @BindView(R.id.tl_income_count)
    TabLayout tlCount;
    @BindView(R.id.vp_income_details)
    ViewPager vpDetails;
    @BindView(R.id.agency_income_count)
    LinearLayout mAgenctCount;

    //改为都显示
    @BindView(R.id.income_my_team)
    LinearLayout mTeam;
    @BindView(R.id.income_invite_friends)
    LinearLayout mFriends;

    private Context mContext = MyIncomeActivity.this;

    private List<IncomeBaseFragment> fragments;
    //标题信息
    public String[] title = new String[]{"今日统计", "昨日统计", "近7日统计", "本月统计", "上月统计"};
    private IncomePaperAdapter mPaperAdapter;
    private SharedPreferences se;

    //打开页面的方法
    private AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
    private Map exParams = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_income);
        ButterKnife.bind(this);
        se = PreferenceManager.getDefaultSharedPreferences(this);

        initView();
    }

    private void initView() {
        //不管是代理还是非代理都显示
        if (se.getInt("isAgencyStatus", 0) == 1) {
//            mTeam.setVisibility(View.VISIBLE);
//            mFriends.setVisibility(View.VISIBLE);
//            tvOrderDetail.setText("订单明细");
            mAgenctCount.setVisibility(View.VISIBLE);

        } else {
//            mTeam.setVisibility(View.INVISIBLE);
//            mFriends.setVisibility(View.INVISIBLE);
//            tvOrderDetail.setText("积分账单");
            mAgenctCount.setVisibility(View.GONE);

        }

        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改

        fragments = new ArrayList<>();
        int length = title.length;
        //创建页面
        for (int i = 0; i < length; i++) {
            fragments.add((IncomeBaseFragment) setTitle(new IncomeBaseFragment(), title[i],i+1));
        }
        //设置适配器
        mPaperAdapter = new IncomePaperAdapter(getSupportFragmentManager());
        vpDetails.setAdapter(mPaperAdapter);
        /*//设置tablout 滑动模式
        tablayoutTopoff.setTabMode(TabLayout.MODE_SCROLLABLE);*/
        //联系tabLayout和viewpager
        tlCount.setupWithViewPager(vpDetails);
        tlCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 设置头目
     */
    private Fragment setTitle(Fragment fragment, String title,int i) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("type",i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // onstart里面执行 提现积分回来后 可刷新积分信息
        initData();
    }

    public void initData() {

//        if (!se.getString("photo", "").equals("")) {
//            Glide.with(MyApplication.getInstance()).load(se.getString("photo", "")).error(R.drawable.pic_morentouxiang).into(ivHead);
//        }
//        tvNickName.setText(se.getString("nickName",""));


        Netword.getInstance().getApi(OwnApi.class)
                .ownIncome(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnIncomeBean>(mContext) {
                    @Override
                    public void successed(OwnIncomeBean result) {
//                        Log.i(" ResultBack_successed", JSON.toJSONString(result));
                        mSumJIFen.setText(result.record.sumIntegral);
                        mKeTiXian.setText(result.record.withdrawalIntegral);
                        mBuKeTiXian.setText(result.record.notWithdrawalIntegral);
                        tvForecastToday.setText(result.record.todayEstimatedIncome);
                        tvTotalToday.setText(result.record.todayVolum);
                        tvForecastMonth.setText(result.record.currentMonthIncome);
                        tvForecastLastMonth.setText(result.record.totalIncome);
                    }
                });
    }

    @OnClick({R.id.txt_tixian,R.id.iv_income_back, R.id.income_my_team, R.id.income_order_detail,
            R.id.income_invite_friends,R.id.income_invite_jifen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_tixian://积分提现
                startActivity(new Intent(this, JinfenReflectActivity.class));
                break;
            case R.id.iv_income_back:
                finish();
                break;
            case R.id.income_my_team:
                // 我的团队
                if (se.getInt(LeCommon.KEY_AGENCY_STATUS, 0) == 1) {
                    // 有团队
                    startActivity(new Intent(MyIncomeActivity.this, MyTeamActivity.class));
                } else {
                    // 无团队
                    new PopChoseAgency(mContext, MyIncomeActivity.this);
                }


                break;
            case R.id.income_order_detail:
                //订单详情
                if (se.getInt("isAgencyStatus", 0) == 1) {
                    // 订单明细
                    startActivity(new Intent(MyIncomeActivity.this, MyOrderActivity.class));
                } else {
                    // add: 2018/1/9 goto taobao order list
                    AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(0, true);
                    AlibcTrade.show(MyIncomeActivity.this, alibcBasePage, alibcShowParams, null, exParams, new DemoTradeCallback());
                }
//                if (se.getInt("isAgencyStatus", 0) == 1) {
//                    // 订单明细
//                    startActivity(new Intent(MyIncomeActivity.this, MyOrderActivity.class));
//                } else {
//                    // 积分账单(非代理)
//                    Intent intent=new Intent(this, ProfitActivity.class);
//                    intent.putExtra("flag","积分账单");
//                    startActivity(intent);
//                }
                break;
            case R.id.income_invite_friends:
                // 邀请好友
//                startActivity(new Intent(mContext, ShareMoneyActivity.class));
                break;
            case R.id.income_invite_jifen:
                //积分账单
                Intent intent=new Intent(this, ProfitActivity.class);
                intent.putExtra("flag","积分账单");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void gotoAgency() {
        // 去成为代理
        startActivity(new Intent(MyIncomeActivity.this, ApplyAgentActivity.class));
    }


    /**
     * 适配器
     */
    private class IncomePaperAdapter extends FragmentPagerAdapter {

        public IncomePaperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getArguments().getString("title");
        }
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


}
