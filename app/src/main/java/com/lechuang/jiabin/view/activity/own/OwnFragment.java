package com.lechuang.jiabin.view.activity.own;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.AlibcTaokeParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.conversation.EServiceContact;
import com.bumptech.glide.Glide;
import com.lechuang.jiabin.MainActivity;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.base.Extra;
import com.lechuang.jiabin.base.MyApplication;
import com.lechuang.jiabin.model.DemoTradeCallback;
import com.lechuang.jiabin.model.LeCommon;
import com.lechuang.jiabin.model.LocalSession;
import com.lechuang.jiabin.model.bean.OwnUserInfoBean;
import com.lechuang.jiabin.presenter.CommonAdapter;
import com.lechuang.jiabin.presenter.ToastManager;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.OwnApi;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.activity.SigneActivity;
import com.lechuang.jiabin.view.activity.ui.LoginActivity;
import com.lechuang.jiabin.view.defineView.MGridView;
import com.lechuang.jiabin.view.defineView.ViewHolder;
import com.lechuang.jiabin.view.defineView.XCRoundImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class OwnFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.iv_set)
    ImageView ivSet;
    @BindView(R.id.iv_news)
    ImageView ivNews;
    @BindView(R.id.iv_headImg)
    XCRoundImageView iv_headImg;
    @BindView(R.id.tv_login_or_register)
    TextView tvLoginOrRegister;
    @BindView(R.id.tv_userId)
    TextView tv_userId;
    @BindView(R.id.tv_canuse)
    TextView tv_canuse;
    @BindView(R.id.ll_keyongjiangli)
    LinearLayout ll_keyongjiangli;
    //    @BindView(R.id.tv_sign)
//    TextView tv_sign;
    @BindView(R.id.line_vip)
    TextView lineVip;
    @BindView(R.id.tv_jiangli)
    TextView tv_jiangli;
    @BindView(R.id.gv_state)
    MGridView gvState;
    //    @BindView(R.id.ll_myincom)
//    TextView llMyincom;
//    @BindView(R.id.ll_task)
//    TextView llTask;
//    @BindView(R.id.line_myCar)
//    TextView lineMyCar;
    @BindView(R.id.ll_myagent)
    TextView llMyagent;
    //    @BindView(R.id.ll_share)
//    TextView llShare;
//    @BindView(R.id.ll_jifen)
//    TextView llJifen;
    Unbinder unbinder;
    private String str[] = {"待付款", "待发货", "待收货", "售后", "全部订单"};
    private int images[] = {R.drawable.wode_daifukuan, R.drawable.wode_daifahuo, R.drawable.wode_daishouhuo,
            R.drawable.wode_shouhou, R.drawable.wode_quanbudingdam};

    private LocalSession mSession;
    //打开页面的方法
    private AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
    private Map exParams = new HashMap<>();

    //保存用户登录信息的sp
    private SharedPreferences se;

    private int signedStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_own, container, false);
        unbinder = ButterKnife.bind(this, v);
        initView();
        return v;
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/22  20:01
     * @describe 初始化下边的淘宝订单信息
     */
    private void initView() {
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSession = LocalSession.get(getActivity());
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", images[i]);
            map.put("name", str[i]);
            list.add(map);
        }
        gvState.setAdapter(new CommonAdapter<Map<String, Object>>(getActivity(), list, R.layout.home_kinds_item1) {
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
        gvState.setOnItemClickListener(this);
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
    }

    @Override
    public void onStart() {
        super.onStart();
        if (se.getBoolean(LeCommon.KEY_HAS_LOGIN, false))
            getData();
        //是否登录
        mSession.setLogin(se.getBoolean("isLogin", false));
        //id
        mSession.setId(se.getString("id", ""));
        if (se.getBoolean("isLogin", false)) {
            if (!se.getString("photo", "").equals("")) {
                Glide.with(MyApplication.getInstance()).load(se.getString("photo", "")).error(R.drawable.pic_morentouxiang).into(iv_headImg);
            }
            //没有昵称时展示手机号
            String nick = se.getString("nickName", se.getString("phone", "----"));
            tv_userId.setText(nick);
            tvLoginOrRegister.setText("手机号码:" + se.getString("phone", ""));
            tvLoginOrRegister.setEnabled(false);
            tv_userId.setEnabled(false);
            ll_keyongjiangli.setVisibility(View.VISIBLE);
        } else {
            tvLoginOrRegister.setText("登录/注册");
            tv_userId.setText("用户昵称");
            iv_headImg.setImageResource(R.drawable.pic_morentouxiang);
            tvLoginOrRegister.setEnabled(true);
            tv_userId.setEnabled(true);
            ll_keyongjiangli.setVisibility(View.GONE);
        }

        if (se.getInt(LeCommon.KEY_AGENCY_STATUS, 0) == 1) {
            // 是代理
            llMyagent.setText("我的团队");
        } else {
            // 不是代理
            llMyagent.setText("开通代理");
        }

    }

    //切换刷新vip和签到状态
    /*@Override
    public void onPause() {
        super.onPause();
        getData();
    }*/

    //用户密码
    private String openImPassword;
    //用户账户
    private String phone;
    //客服账号
    private String customerServiceId;
    public YWIMKit mIMKit;

    private void getData() {
        Netword.getInstance().getApi(OwnApi.class)
                .userInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnUserInfoBean>(getActivity()) {
                    @Override
                    public void successed(OwnUserInfoBean result) {
                        signedStatus = result.signedStatus;
                        if (result.vipGradeName != null && !result.vipGradeName.equals("")) {
                            lineVip.setText(result.vipGradeName);
                        } else {
                            lineVip.setText("普通会员");
                        }
//                        if (signedStatus == 0) {
//                            tv_sign.setText("签到");
//                        } else {
//                            tv_sign.setText("已签到");
//                        }
                        if (se.getInt(LeCommon.KEY_AGENCY_STATUS, 0) == 1) {
                            // 是代理
                            llMyagent.setText("我的团队");
                        } else {
                            // 不是代理
                            llMyagent.setText("开通代理");
                        }
                        //可用奖励
                        tv_jiangli.setText(result.withdrawIntegral == null ? "" : result.withdrawIntegral);
                        se.edit().putInt("isAgencyStatus", result.isAgencyStatus).apply();

                        openImPassword = result.openImPassword;
                        phone = result.phone;
                        customerServiceId = result.customerServiceId;
                        if (phone != null && openImPassword != null && customerServiceId != null) {
                            //此实现不一定要放在Application onCreate中
                            //此对象获取到后，保存为全局对象，供APP使用
                            //此对象跟用户相关，如果切换了用户，需要重新获取
                            mIMKit = YWAPI.getIMKitInstance(phone, Constants.APP_KEY);
                            //开始登录
                            IYWLoginService loginService = mIMKit.getLoginService();
                            YWLoginParam loginParam = YWLoginParam.createLoginParam(phone, openImPassword);
                            loginService.login(loginParam, new IWxCallback() {

                                @Override
                                public void onSuccess(Object... arg0) {
//                                    Utils.show(getActivity(), "success");
                                }

                                @Override
                                public void onProgress(int arg0) {
                                    // TODO Auto-generated method stub

//                                    Utils.show(getActivity(), "onpress");
                                }

                                @Override
                                public void onError(int errCode, String description) {
                                    //如果登录失败，errCode为错误码,description是错误的具体描述信息
//                                   Utils.show(getActivity(), description);
                                }
                            });
                        } else {
                            Utils.show(getActivity(), getResources().getString(R.string.net_error));
                        }
                    }
                });

    }

    @OnClick({R.id.iv_set, R.id.iv_news, R.id.tv_login_or_register, R.id.tv_sign, R.id.ll_jifen, R.id.ll_service, R.id.tv_userId,
            R.id.ll_myincom, R.id.ll_task, R.id.line_myCar, R.id.ll_myagent, R.id.ll_share, R.id.line_vip, R.id.ll_help})
    public void onViewClicked(View view) {
        if (mSession.isLogin()) {
            switch (view.getId()) {
//                case R.id.ll_order:   //查看全部订单
//                    orderType = 0;
//                    AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(orderType, true);
//                    AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, null, exParams, new DemoTradeCallback());
//                    break;
                case R.id.iv_set://设置
                    Intent intent = new Intent(getActivity(), SetActivity.class);
                    startActivityForResult(intent, Extra.CODE_MAIN_BACK);
                    break;
                case R.id.ll_jifen://积分提现
                    startActivity(new Intent(getActivity(), JinfenReflectActivity.class));
                    break;
                case R.id.iv_news://消息
                    startActivity(new Intent(getActivity(), NewsCenterActivity.class));
                    break;
                case R.id.line_vip://vip
                    startActivity(new Intent(getActivity(), VipActivity.class));
                    break;
                case R.id.tv_userId:
                case R.id.tv_login_or_register://注册。登录
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                case R.id.tv_sign://签到
                    if (se.getBoolean(LeCommon.KEY_HAS_LOGIN, false))
                        startActivityForResult(new Intent(getActivity(), SigneActivity.class), 1);
                    else {
                        ToastManager.getInstance().showShortToast("您需要先登陆");
                    }
                    break;
                case R.id.ll_myincom://我的收益
                    startActivity(new Intent(getActivity(), MyIncomeActivity.class));
                    break;
                case R.id.ll_task://任务中心
                    startActivity(new Intent(getActivity(), TaskCenterActivity.class));
                    break;
                case R.id.line_myCar://我的购物车
                    AlibcBasePage alibcBase = new AlibcMyCartsPage();
                    AlibcTaokeParams taokeParams = new AlibcTaokeParams(Constants.PID, "", "");
                    AlibcTrade.show(getActivity(), alibcBase, alibcShowParams, taokeParams, exParams, new DemoTradeCallback());
                    break;
                case R.id.ll_myagent://开通代理
                    if (se.getInt("isAgencyStatus", 0) == 1) {
                        startActivity(new Intent(getActivity(), MyTeamActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), ApplyAgentActivity.class));
                    }
                    break;
                case R.id.ll_share://我的分享
                    startActivity(new Intent(getActivity(), ShareMoneyActivity.class));
                    break;
                case R.id.ll_service://开启客服
                    //userid是客服帐号，第一个参数是客服帐号，第二个是组ID，如果没有，传0
                    EServiceContact contact = new EServiceContact(customerServiceId, 0);
                    //如果需要发给指定的客服帐号，不需要Server进行分流(默认Server会分流)，请调用EServiceContact对象
                    //的setNeedByPass方法，参数为false。
                    contact.setNeedByPass(false);
                    Intent intent1 = mIMKit.getChattingActivityIntent(contact);
                    startActivity(intent1);
                    break;
                case R.id.ll_help:// 帮助中心
                    startActivity(new Intent(getActivity(), InfoActivity.class));
                    break;
            }
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    int orderType = 0;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mSession.isLogin()) {
            switch (images[position]) {
                case R.drawable.wode_daifukuan://代付款
                    orderType = 1;
                    break;
                case R.drawable.wode_daifahuo://待发货
                    orderType = 2;
                    break;
                case R.drawable.wode_daishouhuo://待收货
                    orderType = 3;
                    break;
                case R.drawable.wode_shouhou://售后
                    orderType = 4;
                    break;
                case R.drawable.wode_quanbudingdam://全部订单
                    orderType = 0;
                    break;
            }
            AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(orderType, true);
            AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, null, exParams, new DemoTradeCallback());
        } else {

            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlibcTradeSDK.destory();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
           /* if (data.getIntExtra("sign", 0) == 1) {
                tv_sign.setText("已签到");
            } else {
                tv_sign.setText("签到");
            }*/
        } else if (requestCode == Extra.CODE_MAIN_BACK && resultCode == Extra.CODE_MAIN_BACK) {
            // 切换到 HomeFragment
            MainActivity activity = (MainActivity) getActivity();
            activity.showCurrentFragment(0);
        }
    }

}