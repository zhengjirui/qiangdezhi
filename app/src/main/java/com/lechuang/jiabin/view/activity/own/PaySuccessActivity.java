package com.lechuang.jiabin.view.activity.own;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseActivity;


/**
 * Created by yrj on 2017/8/16.
 * 支付成功页面
 */

public class PaySuccessActivity extends BaseActivity {
    private TextView tv_money;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_pay_success;
    }

    @Override
    protected void initTitle() {
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.tv_title)).setText("支付成功");
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        tv_money = (TextView) findViewById(R.id.tv_money);
        findViewById(R.id.btn_invite).setOnClickListener(this);
        tv_money.setText(getIntent().getStringExtra("money"));
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_invite: //邀请好友
                startActivity(new Intent(PaySuccessActivity.this, ShareMoneyActivity.class));
                finish();
                break;
            default:
                break;
        }
    }


}
