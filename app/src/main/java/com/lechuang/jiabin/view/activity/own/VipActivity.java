package com.lechuang.jiabin.view.activity.own;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.model.LocalSession;
import com.lechuang.jiabin.presenter.net.QUrl;
import com.lechuang.jiabin.utils.WebViewUtils;
import com.lechuang.jiabin.view.defineView.ProgressWebView;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author yrj
 * @date   2017/10/10
 * @E-mail 1422947831@qq.com
 * @desc   会员等级
 */

public class VipActivity extends AppCompatActivity {

    @BindView(R.id.huiyuan_back)
    ImageView huiyuanBack;
    @BindView(R.id.wv_vip)
    ProgressWebView wvVip;
    private ProgressWebView mWeb;
    private LocalSession mSession;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        ButterKnife.bind(this);
        initView();
        initEvents();
    }


    public void initView() {
        mWeb = (ProgressWebView) findViewById(R.id.wv_vip);
    }


    public void initEvents() {
        //String id = mSession.getId();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String id = sp.getString("id", "");
       /* mSession = LocalSession.get(this);
        Utils.E("----"+mSession.toString());*/
        //会员中心
        //加载网页
        String url = QUrl.vipCenter + "?i=" + id;
        WebViewUtils.loadUrl(mWeb, this, url);
    }

    @OnClick(R.id.huiyuan_back)
    public void onViewClicked() {
        finish();
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
