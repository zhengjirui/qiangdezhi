package com.lechuang.jiabin.view.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.AlibcTaokeParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseActivity;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.base.Extra;
import com.lechuang.jiabin.model.DemoTradeCallback;
import com.lechuang.jiabin.utils.WebViewUtils;
import com.lechuang.jiabin.view.defineView.ProgressWebView;

import java.util.HashMap;
import java.util.Map;

public class EmptyWebActivity extends BaseActivity {
    private ProgressWebView mWeb;
    private Context mContext = EmptyWebActivity.this;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数

    @Override
    protected int getContentView() {
        return R.layout.activity_empty_web;
    }

    @Override
    protected void initTitle() {
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {
        mWeb = (ProgressWebView) findViewById(R.id.wv_empty);

        String title = getIntent().getStringExtra(Extra.TITLE);
        String loadUrl = getIntent().getStringExtra(Extra.LOAD_URL);
        WebViewUtils.loadUrl(mWeb, mContext, loadUrl);
        if (TextUtils.isEmpty(title)) {
            mWeb.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    ((TextView)findViewById(R.id.tv_title)).setText(view.getTitle());
                }
            });
        } else {
            ((TextView) findViewById(R.id.tv_title)).setText(title);
        }

        if (loadUrl.contains("taobao") || loadUrl.contains("tmall")) {
            alibcShowParams = new AlibcShowParams(OpenType.Native, false);
            AlibcTaokeParams taoke = new AlibcTaokeParams(Constants.PID, "", "");
            exParams = new HashMap<>();
            exParams.put("isv_code", "appisvcode");
            exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
            //Utils.E("url:"+url);
            //view.loadUrl(url);

            //Utils.E("cccccc:"+getIntent().getStringExtra("alipayItemId"));
            //AlibcBasePage alibcBasePage = new AlibcDetailPage(getIntent().getStringExtra("alipayItemId"));
            //实例化URL打开page
            //AlibcBasePage alibcBasePage = new AlibcPage("https://uland.taobao.com/coupon/edetail?activityId=91cdf70f6a944043b21c9dfca39a889c&pid=mm_116411007_36292444_142176907&itemId=544410512702&src=lc_tczs");
            AlibcBasePage alibcBasePage = new AlibcPage(loadUrl);
            //AlibcTrade.show(ProductDetailsActivity.this, alibcBasePage, alibcShowParams, null, exParams , new DemoTradeCallback());

            //添加购物车
            //AlibcBasePage alibcBasePage = new AlibcAddCartPage(getIntent().getStringExtra("alipayItemId"));
            AlibcTrade.show(EmptyWebActivity.this, alibcBasePage, alibcShowParams, taoke, exParams, new DemoTradeCallback());
            finish();
        } /*else {
            WebViewUtils.loadUrl(mWeb, mContext, getIntent().getStringExtra("url"));
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeb.destroy();
        //调用了AlibcTrade.show方法的Activity都需要调用AlibcTradeSDK.destory()
        AlibcTradeSDK.destory();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWeb.canGoBack()) {
            mWeb.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
