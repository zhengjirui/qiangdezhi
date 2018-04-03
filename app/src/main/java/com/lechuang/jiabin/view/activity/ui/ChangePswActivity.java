package com.lechuang.jiabin.view.activity.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseActivity;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.CommenApi;
import com.lechuang.jiabin.utils.PhotoUtil;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.defineView.ClearEditText;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.jiabin.utils.PhotoUtil.t;

public class ChangePswActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.et_phoneNumber)
    ClearEditText etPhoneNumber;
    @BindView(R.id.et_good)
    ClearEditText etGood;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.et_mima)
    ClearEditText etMima;
    @BindView(R.id.iv_yanjing)
    ImageView ivYanjing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_find_back);
        ButterKnife.bind(this);
        etMima.addTextChangedListener(this);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_change_psw;
    }

    @Override
    protected void initTitle() {
//        ((TextView) findViewById(R.id.tv_title)).setText("修改密码");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_code, R.id.btn_complete, R.id.iv_back, R.id.iv_yanjing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_code:
                if (!Utils.isTelNumber(etPhoneNumber.getText().toString())) {
                    Utils.show(this, "请输入正确的手机号");
                    return;
                }
                if (Utils.isNetworkAvailable(this)) {
                    String s = etPhoneNumber.getText().toString();
                    changePwdSendCode(s);
                    tvCode.setEnabled(false);
                } else {
                    Utils.show(this, "亲！您的网络开小差了哦");
                }
                break;
            case R.id.iv_yanjing://切换图片和密码显示
                changeVeiw();
                break;
            case R.id.btn_complete:
                String s = etPhoneNumber.getText().toString();
                //输入框为空 提示用户
                if (Utils.isEmpty(etPhoneNumber)) {
                    Utils.show(this, "请输入手机号!");
                    return;
                }
                //输入的不是正确的手机号
                if (!Utils.isTelNumber(s)) {
                    Utils.show(this, "请输入正确的手机号!");
                    return;
                }
                //没有输入密码
                if (Utils.isEmpty(etMima)) {
                    Utils.show(this, "请输入密码");
                    return;
                }

                if (Utils.isEmpty(etGood)) {
                    Utils.show(this, "请输入验证码");
                }
                if (etMima.getText().toString().length() < 6 || etMima.getText().toString().length() > 20) {
                    Utils.show(this, "密码长度6～20位");
                    return;
                }
                if (etMima.getText().toString().trim().contains(" ")) {
                    Utils.show(this, "密码不能包含空格");
                    return;
                }
                HashMap map = new HashMap();
                map.put("phone", etPhoneNumber.getText().toString());
                map.put("password", Utils.getMD5(etMima.getText().toString()));
                map.put("verifiCode", etGood.getText().toString());
                updatePwd(map);
                break;
        }
    }

    /**
     * 修改密码
     *
     * @param map
     */
    private void updatePwd(HashMap map) {
        Netword.getInstance().getApi(CommenApi.class)
                .changePassword(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(ChangePswActivity.this, result);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", etPhoneNumber.getText().toString());
                        se.commit();
                        startActivity(new Intent(ChangePswActivity.this, LoginActivity.class));
                        finish();
                    }

                });
    }


    /**
     * 修改密码获取验证码
     */
    private void changePwdSendCode(String phoneNumber) {
        Netword.getInstance().getApi(CommenApi.class)
                .updatePwdCode(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(ChangePswActivity.this, result);
                        tvCode.setEnabled(false);
                        PhotoUtil.getCode(tvCode);
                        PhotoUtil.handler.post(t);

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoUtil.closeCode();
    }

    /**
     *  @author li
     *  邮箱：961567115@qq.com
     *  @time 2017/10/5  17:55
     *  @describe 改变视图
     */
    public boolean isShow;
    private void changeVeiw() {
        if(isShow){
            isShow=false;
        }else {
            isShow=true;
        }
        if(isShow){
            ivYanjing.setImageResource(R.drawable.denglu_yanjing_s);
            etMima.setTransformationMethod(null);
        }else {
            ivYanjing.setImageResource(R.drawable.denglu_yanjing);
            etMima.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence str, int start, int before, int count) {
        if (str.length() > 0) {
            ivYanjing.setVisibility(View.VISIBLE);
        } else {
            ivYanjing.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
