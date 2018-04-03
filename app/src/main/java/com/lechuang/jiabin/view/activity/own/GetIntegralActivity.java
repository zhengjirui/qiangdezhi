package com.lechuang.jiabin.view.activity.own;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.model.bean.ResultBean;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.CommenApi;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.defineView.DialogAlertView;
import com.umeng.analytics.MobclickAgent;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/11 20:33
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class GetIntegralActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_orderNum; //订单编号

    private Context mContext = GetIntegralActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_integral);
        initView();
    }

    public void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText("领取积分");
        et_orderNum = (EditText) findViewById(R.id.et_orderNum);

        findViewById(R.id.iv_get).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_get:
                if (Utils.isNetworkAvailable(mContext)) {
                    //订单编号
                    String orderNum = et_orderNum.getText().toString().trim();
                    if (Utils.isEmpty(et_orderNum)) {
                        Utils.show(mContext, "订单编号不能为空");
                        return;
                    }
                    if (orderNum.contains(" ")) {
                        Utils.show(mContext, "订单编号不能包含空格");
                        return;
                    }
                    Netword.getInstance().getApi(CommenApi.class)
                            .getJf(orderNum)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ResultBack<String>(mContext) {


                                @Override
                                public void onNext(ResultBean<String> result) {
                                    showResultDialog(mContext,result.moreInfo);
                                }

                                @Override
                                public void successed(String result) {

                                }
                            });
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }

    }

    //领取积分提示
    public static void showResultDialog(Context context, String text) {
        final DialogAlertView dialog = new DialogAlertView(context, R.style.CustomDialog);
        dialog.setView(R.layout.dialog_getjifen);
        dialog.show();
        ((TextView) dialog.findViewById(R.id.txt_notice)).setText(text);
        dialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
