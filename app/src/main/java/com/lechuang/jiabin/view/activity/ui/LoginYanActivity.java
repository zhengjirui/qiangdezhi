package com.lechuang.jiabin.view.activity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.netApi.CommenApi;
import com.lechuang.jiabin.utils.PhotoUtil;
import com.lechuang.jiabin.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.jiabin.utils.PhotoUtil.t;

/**
 * 作者：li on 2017/9/27 16:53
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class LoginYanActivity extends AppCompatActivity {
    @BindView(R.id.et_phonenum)
    EditText et_phonenum;
    @BindView(R.id.et_proving)
    EditText etProving;
    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.tv_mima)
    TextView tvMima;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_zhuche)
    Button btnZhuche;
    @BindView(R.id.tv_taobao)
    TextView tvTaobao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denglu_yanzheng);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_code, R.id.btn_login, R.id.btn_zhuche, R.id.tv_taobao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_code://验证码
                if (!Utils.isTelNumber(et_phonenum.getText().toString())) {
                    Utils.show(this,"请输入正确的手机号");
                    return;
                }
                if (Utils.isNetworkAvailable(this)) {
                    String s = et_phonenum.getText().toString();
                    sendCode(s);//获取验证码
                    tv_code.setEnabled(false);
                } else {
                    Utils.show(this,"亲！您的网络开小差了哦");
                }
                break;
            case R.id.btn_login://登录
                break;
            case R.id.btn_zhuche://注册
                startActivity(new Intent(this,RegisterActivity.class));
                break;
            case R.id.tv_taobao://淘宝登录
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void sendCode(String phoneNumber){
        Netword.getInstance().getApi(CommenApi.class)
                .threeBind(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    protected void error300(int errorCode, String s) {
                        Utils.show(LoginYanActivity.this,s);
                    }

                    @Override
                    public void successed(String result) {
                        Utils.show(LoginYanActivity.this,result);
                        PhotoUtil.getCode(tv_code);
                        PhotoUtil.handler.post(t);

                    }
                });
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
