package com.lechuang.jiabin.view.activity.own;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.presenter.net.QUrl;
import com.lechuang.jiabin.utils.WebViewUtils;
import com.lechuang.jiabin.view.defineView.ProgressWebView;
import com.umeng.analytics.MobclickAgent;

public class ShareGetMoneyActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressWebView mWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_get_money);
        initVeiw();
        initData();
    }

    private void initVeiw() {
        ((TextView) findViewById(R.id.tv_title)).setText("赚赏金");
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    private void initData() {
        mWeb = (ProgressWebView) findViewById(R.id.wv_task);
        //设置加载地址
//        WebViewUtils.loadUrl(mWeb, this, QUrl.taskCenter + "?id=" + session.getSafeToken());
        WebViewUtils.loadUrl(mWeb, this, "http://baidu.com/");

    }

    @Override
    public void onClick(View v) {
        if (mWeb.canGoBack()) {
            mWeb.goBack();// 返回前一个页面
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
