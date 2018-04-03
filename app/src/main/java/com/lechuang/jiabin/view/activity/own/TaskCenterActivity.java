package com.lechuang.jiabin.view.activity.own;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.model.LocalSession;
import com.lechuang.jiabin.presenter.net.QUrl;
import com.lechuang.jiabin.utils.WebViewUtils;
import com.lechuang.jiabin.view.defineView.ProgressWebView;
import com.umeng.analytics.MobclickAgent;

/**
 * 作者：li on 2017/10/6 15:06
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class TaskCenterActivity extends AppCompatActivity implements View.OnClickListener {


    ProgressWebView mWeb;
    private LocalSession session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_center);
        initVeiw();
        initData();
    }

    private void initVeiw() {
        session = LocalSession.get(this);
        ((TextView) findViewById(R.id.tv_title)).setText("任务中心");
        findViewById(R.id.iv_back).setOnClickListener(this);

    }

    private void initData() {
        mWeb = (ProgressWebView) findViewById(R.id.wv_task);
        WebViewUtils.loadUrl(mWeb, this, QUrl.taskCenter + "?id=" + session.getSafeToken());

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWeb.canGoBack()) {
            mWeb.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
