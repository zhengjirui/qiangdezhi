package com.lechuang.jiabin.view.activity.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.base.Extra;
import com.lechuang.jiabin.presenter.net.QUrl;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.netApi.CommenApi;
import com.lechuang.jiabin.utils.PhotoUtil;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.activity.home.EmptyWebActivity;
import com.lechuang.jiabin.view.defineView.ClearEditText;
import com.lechuang.jiabin.view.dialog.FlippingLoadingDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.jiabin.utils.PhotoUtil.t;

/**
 * 作者：li on 2017/9/27 15:02
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phoneNumber)
    ClearEditText et_phonenum;
    @BindView(R.id.et_good)
    ClearEditText etGood;
    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.et_mima)
    ClearEditText etMima;
    @BindView(R.id.iv_yanjing)
    ImageView ivYanjing;
    @BindView(R.id.btn_complete)
    Button btnComplete;
    FlippingLoadingDialog mLoadingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        ButterKnife.bind(this);
        mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");

    }

    @OnClick({R.id.iv_back, R.id.tv_code, R.id.iv_yanjing, R.id.btn_complete, R.id.tv_user_agreement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_code://获取验证码
                if (!Utils.isTelNumber(et_phonenum.getText().toString())) {
                Utils.show(this,"请输入正确的手机号");
                return;
            }
                if (Utils.isNetworkAvailable(this)) {
                    String s = et_phonenum.getText().toString();
                    sendCode(s);//获取验证码

                } else {
                    Utils.show(this,"亲！您的网络开小差了哦");
                }
                break;
            case R.id.iv_yanjing://切换图片和密码显示
                changeVeiw();
                break;
            // 用户协议
            case R.id.tv_user_agreement:
                startActivity(new Intent(RegisterActivity.this, EmptyWebActivity.class)
                        .putExtra(Extra.LOAD_URL, QUrl.userAgreement).putExtra(Extra.TITLE, "用户协议"));
                break;
            case R.id.btn_complete://提交
                if (!Utils.isTelNumber(et_phonenum.getText().toString())) {
                    Utils.show(this,"请输入正确的手机号");
                    return;
                }
                if (Utils.isEmpty(et_phonenum)) {
                    Utils.show(this,"请输入手机号");
                    return;
                }
                if (Utils.isEmpty(etGood)) {
                    Utils.show(this,"请输入验证码");
                }
                if (Utils.isEmpty(etMima)) {
                    Utils.show(this,"请输入密码");
                    return;
                }
                if (etMima.getText().toString().length() < 6 || etMima.getText().toString().length() > 20) {
                    Utils.show(this,"密码长度6～20位");
                    return;
                }
                if (etMima.getText().toString().trim().contains(" ")) {
                    Utils.show(this,"密码不能包含空格");
                    return;
                }
                if (Utils.isTelNumber(et_phonenum.getText().toString())) {
                    showLoadingDialog("请稍后");
                    HashMap map=new HashMap();
                    map.put("phone",et_phonenum.getText().toString());
                    map.put("password",Utils.getMD5(etMima.getText().toString()));
                    map.put("verifiCode",etGood.getText().toString());
                    register(map);
                } else {
                    Utils.show(this,"请输入正确的手机号");
                    return;
                }
                break;
        }
    }
/**
 *  @author li
 *  邮箱：961567115@qq.com
 *  @time 2017/10/5  18:43
 *  @describe 注册
 */
    private void register(HashMap<String ,String> map) {
        Netword.getInstance().getApi(CommenApi.class)
                .register(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        dismissLoadingDialog();
                        Utils.show(RegisterActivity.this,result);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", et_phonenum.getText().toString());
                        se.commit();
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        finish();
                    }

                    @Override
                    protected void error300(int errorCode, String s) {
                        super.error300(errorCode, s);
                        dismissLoadingDialog();
                    }
                });
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
                        Utils.show(RegisterActivity.this, s);
                        dismissLoadingDialog();
                    }
                    @Override
                    public void successed(String result) {
                        dismissLoadingDialog();
                        Utils.show(RegisterActivity.this,result);
                        tv_code.setEnabled(false);
                        PhotoUtil.getCode(tv_code);
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


    // 显示加载框
    public void showLoadingDialog(String text) {
        if (text != null) {
            mLoadingDialog.setText(text);
        }

        mLoadingDialog.show();
    }

    // 关闭加载框
    public void dismissLoadingDialog() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
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
