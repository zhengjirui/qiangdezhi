package com.lechuang.jiabin.view.activity.own;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;

/**
 * Author: guoning
 * Date: 2017/10/10
 * Description:
 */

public class PayFailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fail);
        initViews();
    }

    private void initViews() {
        ((TextView) findViewById(R.id.tv_title)).setText("支付失败");
//        ((TextView) findViewById(R.id.tv_fail)).setText(String.format(Locale.CHINESE,"%f(元)", getIntent().getDoubleExtra("payPriceStr",0)));
        ((TextView) findViewById(R.id.tv_fail1)).setText(String.format(Locale.CHINESE,"%d(元)", (int)getIntent().getDoubleExtra("payPriceStr",0)));

        findViewById(R.id.iv_back).setVisibility(View.GONE);
    }

    public void finish(View view) {
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
