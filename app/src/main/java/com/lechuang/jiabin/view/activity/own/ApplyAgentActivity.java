package com.lechuang.jiabin.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.model.LeCommon;
import com.lechuang.jiabin.model.bean.ResultBean;
import com.lechuang.jiabin.model.bean.ShareMoneyBean;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.model.bean.OwnBean;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.netApi.OwnApi;
import com.lechuang.jiabin.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/6 15:31
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class ApplyAgentActivity extends AppCompatActivity{

    private ImageView iv_noNet, iv_applyAgent;
    private Context mContext = ApplyAgentActivity.this;
    private ImageView web_back;
    private ImageView mContentView;
    private boolean mLoadImg = false;
    private String payPriceStr;
    private OwnBean.Agency agency;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_agent);


        initView();
//        WebViewUtils.loadUrl(progressWebView,this,"http://192.168.1.210:8889/user/appUsers/agencyDetail");

        Netword.getInstance().getApi(OwnApi.class).agency() .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnBean>(this) {
                    @Override
                    public void successed(OwnBean result) {
                        agency = result.agency;
                        String img = agency.img;
                        payPriceStr = agency.payPriceStr;
                        Glide.with(ApplyAgentActivity.this).load(img).centerCrop().into(mContentView);

                        mLoadImg = true;
                    }
                });


        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLoadImg)return;
                if(agency.type==1){
                    return;
                }
                if(agency.type==0&&agency.payPrice==0){
                    Netword.getInstance().getApi(OwnApi.class)
                            .autoAgent()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ResultBack<String>(mContext) {
                                @Override
                                public void successed(String result) {
                                    Utils.show(mContext,result);
                                    finish();
                                }
                            });
                    return;
                }
                startActivity(new Intent(ApplyAgentActivity.this,PayStyleActivity.class).putExtra(LeCommon.KEY_PAY_PRICE,payPriceStr));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        getData();
//        findViewById(R.id.web_back).setVisibility(View.VISIBLE);
    }

    private void initView() {
        iv_noNet = (ImageView) findViewById(R.id.iv_noNet);
        iv_applyAgent = (ImageView) findViewById(R.id.iv_applyAgent);
        web_back = (ImageView) findViewById(R.id.web_back);
        mContentView = (ImageView) findViewById(R.id.content_agency);
        findViewById(R.id.iv_agent_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getData() {
        // TODO: 2017/10/6
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
