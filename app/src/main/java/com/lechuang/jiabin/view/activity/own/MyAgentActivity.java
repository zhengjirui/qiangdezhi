package com.lechuang.jiabin.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseActivity;
import com.lechuang.jiabin.presenter.net.ResultBack;
import com.lechuang.jiabin.model.bean.OwnMyAgentBean;
import com.lechuang.jiabin.presenter.CommonAdapter;
import com.lechuang.jiabin.presenter.net.Netword;
import com.lechuang.jiabin.presenter.net.netApi.OwnApi;
import com.lechuang.jiabin.view.defineView.MListView;
import com.lechuang.jiabin.view.defineView.ViewHolder;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/6 15:30
 * 邮箱：961567115@qq.com
 * 修改备注:我的代理
 */
public class MyAgentActivity extends BaseActivity implements View.OnClickListener {

    private MListView mlv_team;
    private Context mContext = MyAgentActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_my_agent;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mlv_team = (MListView) findViewById(R.id.mlv_team);
        findViewById(R.id.tv_more).setOnClickListener(this);
        findViewById(R.id.btn_invite).setOnClickListener(this);
        ((ScrollView) findViewById(R.id.sv_agent)).smoothScrollTo(0, 0);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_more: //查看更多
                startActivity(new Intent(this, MyTeamActivity.class));
                break;
            case R.id.btn_invite:  //邀请好友

                startActivity(new Intent(this, ShareMoneyActivity.class));
                break;
            default:
                break;
        }
    }

    public void getData() {
        Netword.getInstance().getApi(OwnApi.class)
                .agentInfo(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnMyAgentBean>(mContext) {

                    @Override
                    public void successed(OwnMyAgentBean result) {
                        if (result.record.photo != null)
                            Glide.with(mContext).load(result.record.photo).into((ImageView) findViewById(R.id.iv_img));
                        //总收入
                        if (result.record.sumIncome != null)
                            ((TextView) findViewById(R.id.tv_all)).setText(result.record.sumIncome);
                        //代理收入
                        if (result.record.agencyMoney != null)
                            ((TextView) findViewById(R.id.tv_dlsr)).setText(result.record.agencyMoney);
                        //分享收入
                        if (result.record.shareMoney != null)
                            ((TextView) findViewById(R.id.tv_tixian)).setText(result.record.shareMoney);
                        //返现收入
                        if (result.record.returnMoney != null)
                            ((TextView) findViewById(R.id.tv_fx)).setText(result.record.returnMoney);
                        //下级数据
                        List<OwnMyAgentBean.RecordBean.ListBean> list = result.record.list;
                        if (list != null && list.size() > 0) {
                            mlv_team.setAdapter(new CommonAdapter<OwnMyAgentBean.RecordBean.ListBean>(mContext, list, R.layout.myteam_item) {
                                @Override
                                public void setData(ViewHolder viewHolder, Object item) {
                                    if (((OwnMyAgentBean.RecordBean.ListBean) item).photo != null)
                                        viewHolder.displayImage(R.id.iv_img, ((OwnMyAgentBean.RecordBean.ListBean) item).photo);
                                    //昵称
                                    viewHolder.setText(R.id.tv_name, ((OwnMyAgentBean.RecordBean.ListBean) item).nickname);
                                    //个人收入
//                                    viewHolder.setText(R.id.tv_nextNum, "下级人数:" + ((OwnMyAgentBean.RecordBean.ListBean) item).nextAgentCount);
                                    //加入时间
                                    viewHolder.setText(R.id.tv_time, "加入时间:" + ((OwnMyAgentBean.RecordBean.ListBean) item).joinTime);
                                }
                            });
                        }
                    }
                });
    }


    public void finish(View view) {
        finish();
    }
}
