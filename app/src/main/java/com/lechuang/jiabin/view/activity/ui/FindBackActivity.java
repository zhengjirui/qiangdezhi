package com.lechuang.jiabin.view.activity.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseActivity;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.Netword;
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

/**
 * 作者：li on 2017/10/6 08:58
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class FindBackActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phoneNumber)
    ClearEditText etPhoneNumber;
    @BindView(R.id.et_good)
    ClearEditText etGood;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.et_mima)
    ClearEditText etMima;
    @BindView(R.id.et_mima_confirm)
    ClearEditText etMimaConfirm;
    @BindView(R.id.btn_complete)
    Button btnComplete;
    @BindView(R.id.iv_yanjing)
    ImageView ivYanjing;
    @BindView(R.id.iv_yanjing_confirm)
    ImageView ivYanjingConfirm;
    //type 判断是找回密码还是修改密码    1  找回    2 修改
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_find_back);
        ButterKnife.bind(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_find_back;
    }

    @Override
    protected void initTitle() {
        type = getIntent().getIntExtra("type", 1);
        //type 判断是找回密码还是修改密码    1  找回    2 修改
//        if (type == 1) {
//            ((TextView) findViewById(R.id.tv_title)).setText("找回密码");
//        } else {
//            ((TextView) findViewById(R.id.tv_title)).setText("修改密码");
//        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
//        findViewById(R.id.iv_back).setVisibility(View.GONE);
//        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_code, R.id.btn_complete, R.id.iv_back, R.id.iv_yanjing, R.id.iv_yanjing_confirm})
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
                   /* if (type == 1) {
                        //找回密码获取验证码
                        findPwdsendCode(s);
                    } else {
                        //修改密码获取验证码
                        changePwdSendCode(s);
                    }*/
                    if (getIntent().getIntExtra("type", 1) == 1) {
                        findPwdsendCode(s);
                    } else {
                        changePwdSendCode(s);
                    }
                    tvCode.setEnabled(false);
                } else {
                    Utils.show(this, "亲！您的网络开小差了哦");
                }
                break;
            case R.id.iv_yanjing:
                changeVeiw();
                break;
            case R.id.iv_yanjing_confirm:
                changeVeiwConfirm();
                break;
            case R.id.btn_complete:
                if (etMima.getText().toString().equals(etMimaConfirm.getText().toString())) {
                    HashMap map = new HashMap();
                    map.put("phone", etPhoneNumber.getText().toString());
                    map.put("password", Utils.getMD5(etMima.getText().toString()));
                    map.put("verifiCode", etGood.getText().toString());
                    if (getIntent().getIntExtra("type", 1) == 1) {
                        //找回密码
                        findPwd(map);
                    } else {
                        //修改密码
                        updatePwd(map);
                    }
                } else {
                    Utils.show(this, "两次输入密码不一样");
                }
                break;
        }
    }

    /**
     * 找回密码
     *
     * @param map
     */
    private void findPwd(HashMap map) {
        Netword.getInstance().getApi(CommenApi.class)
                .findPwd(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(FindBackActivity.this, result);
                        tvCode.setEnabled(true);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", etPhoneNumber.getText().toString());
                        se.commit();
                        startActivity(new Intent(FindBackActivity.this, LoginActivity.class));
                        finish();
                    }

                });
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
                        Utils.show(FindBackActivity.this, result);
                        tvCode.setEnabled(true);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", etPhoneNumber.getText().toString());
                        se.commit();
                        startActivity(new Intent(FindBackActivity.this, LoginActivity.class));
                        finish();
                    }

                });
    }

    /**
     * 找回密码获取验证码
     */
    private void findPwdsendCode(String phoneNumber) {
        Netword.getInstance().getApi(CommenApi.class)
                .findCode(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(FindBackActivity.this, result);
                        PhotoUtil.getCode(tvCode);
                        PhotoUtil.handler.post(t);

                    }

                    @Override
                    protected void error300(int errorCode, String s) {
                        super.error300(errorCode, s);
                        tvCode.setEnabled(true);
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
                        Utils.show(FindBackActivity.this, result);
                        PhotoUtil.getCode(tvCode);
                        PhotoUtil.handler.post(t);

                    }
                });
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

    /**
     *  @author li
     *  邮箱：961567115@qq.com
     *  @time 2017/10/5  17:55
     *  @describe 改变视图
     */
    public boolean isShowConfirm;
    private void changeVeiwConfirm() {
        if(isShowConfirm){
            isShowConfirm=false;
        }else {
            isShowConfirm=true;
        }
        if(isShowConfirm){
            ivYanjingConfirm.setImageResource(R.drawable.denglu_yanjing_s);
            etMimaConfirm.setTransformationMethod(null);
        }else {
            ivYanjingConfirm.setImageResource(R.drawable.denglu_yanjing);
            etMimaConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoUtil.closeCode();
    }


}
