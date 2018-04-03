package com.lechuang.jiabin.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.model.LocalSession;
import com.lechuang.jiabin.model.bean.UpdataInfoBean;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.presenter.net.netApi.CommenApi;
import com.lechuang.jiabin.utils.RegexUtils;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.defineView.ClearEditText;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/10
 * @E-mail 1422947831@qq.com
 * @desc 绑定支付宝
 */
public class BoundAlipayActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_bound_alipay)
    ClearEditText et_bound_alipay;
    @BindView(R.id.et_name)
    ClearEditText et_name;
    @BindView(R.id.btn_bound_save)
    Button btnBoundSave;
    private Context mContext = BoundAlipayActivity.this;
    private LocalSession mSession;
    //保存用户登录信息的sp
    private SharedPreferences.Editor se;
    //参数格式
    Map<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_alipay);
        ButterKnife.bind(this);
        initView();
        mSession = LocalSession.get(mContext);
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this).edit();
        //参数格式
        map = new HashMap<>();
    }

    public void initView() {
        findViewById(R.id.line).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tv_title)).setText("填写支付宝帐号");
    }


    @OnClick({R.id.iv_back, R.id.btn_bound_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_bound_save:
                if (Utils.isEmpty(et_bound_alipay)) {
                    Utils.show(this, "支付宝账号不能为空");
                    return;
                }
                if (et_bound_alipay.getText().toString().contains(" ")) {
                    Utils.show(this, "支付宝帐号不能包含空格");
                }
                //不能包含emoji表情
                if (Utils.containsEmoji(et_bound_alipay.getText().toString())) {
                    Utils.show(this, getString(R.string.no_emoji));
                    return;
                }
                if (Utils.isEmpty(et_name)) {
                    Utils.show(mContext, "姓名不能为空");
                    return;
                }
                String inputValue = et_bound_alipay.getText().toString().trim();
                if (!(RegexUtils.isEmail(inputValue) || RegexUtils.checkPhone(inputValue))) {
                    Utils.show(mContext, "支付宝帐号格式不正确");
                    return;
                }
                if (Utils.isNetworkAvailable(mContext)) {
                    updateInfo(inputValue, et_name.getText().toString().trim());
                } else {
                    Utils.show(this, getString(R.string.net_error1));
                }
                break;
        }
    }


    private void updateInfo(String number, String name) {
        map.put("alipayNumber", number);   //支付宝账号
        map.put("alipayRealName", name);   //姓名
        if (Utils.isNetworkAvailable(mContext)) {
            Netword.getInstance().getApi(CommenApi.class)
                    .updataInfo(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<UpdataInfoBean>(mContext) {
                        @Override
                        public void successed(UpdataInfoBean result) {
                            if (result == null)
                                return;
                            Utils.show(mContext, "绑定支付宝成功!");
                            mSession.setAlipayNumber(et_bound_alipay.getText().toString().trim());
                            se.putString("alipayNumber", et_bound_alipay.getText().toString().trim());
                            se.commit();
                            Intent mIntent = new Intent();
                            mIntent.putExtra("ZfbNumber", et_bound_alipay.getText().toString().trim());
                            // 设置结果，并进行传送
                            setResult(1, mIntent);
                            finish();
                        }
                    });
        } else {
            Utils.show(mContext, getString(R.string.net_error));
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
