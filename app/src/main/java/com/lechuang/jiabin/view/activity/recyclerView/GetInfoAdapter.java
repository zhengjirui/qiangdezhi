package com.lechuang.jiabin.view.activity.recyclerView;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.model.bean.GetBean;
import com.lechuang.jiabin.view.activity.get.OnKeyShareCallback;
import com.lechuang.jiabin.view.defineView.SpannelTextView;

/**
 * Author: guoning
 * Date: 2017/10/6
 * Description:  赚列表页面的Adapter
 */

public class GetInfoAdapter extends BaseQuickAdapter<GetBean.ListInfo, GetInfoHolder> {


    private OnKeyShareCallback callback;
    private Context context;

    public GetInfoAdapter(Context context) {
        super(R.layout.item_get);
        this.context = context;
    }

    @Override
    protected void convert(final GetInfoHolder helper, final GetBean.ListInfo listInfo) {
        Glide.with(context).load(listInfo.imgs).into((ImageView) helper.getView(R.id.img));
        Glide.get(context).setMemoryCategory(MemoryCategory.LOW);
        if (listInfo.couponMoney != null && !listInfo.couponMoney.equals("")) {
            helper.setText(R.id.price, listInfo.price);
            ((TextView) helper.getView(R.id.price)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            helper.setText(R.id.preferentialPrice, "¥" +listInfo.preferentialPrice);
            helper.setText(R.id.couponMoney,  listInfo.couponMoney + "元");
        } else {
            helper.getView(R.id.ll_price).setVisibility(View.GONE);
            helper.getView(R.id.tv_quanhoujia).setVisibility(View.GONE);
            helper.getView(R.id.ll_quan).setVisibility(View.GONE);
            helper.setText(R.id.preferentialPrice, "售价 ¥" + listInfo.preferentialPrice);
        }
        helper.setText(R.id.nowNumber, "已售出 " + listInfo.nowNumber + " 件");
        TextView tv_zhuan = helper.getView(R.id.zhuanMoney);
        if (listInfo.zhuanMoney != null) {
            tv_zhuan.setVisibility(View.VISIBLE);
            helper.setText(R.id.zhuanMoney, listInfo.zhuanMoney);
        } else {
            tv_zhuan.setVisibility(View.GONE);
        }
        SpannelTextView productName = helper.getView(R.id.spannelTextView);
        productName.setShopType(listInfo.shopType == null ? 1 : Integer.parseInt(listInfo.shopType));
        productName.setDrawText(listInfo.productName);

        final View ll_get_good = helper.getView(R.id.ll_get_good);
        final View share = helper.getView(R.id.share);

        final int adapterPosition = helper.getAdapterPosition();
        share.setTag(adapterPosition);
        ll_get_good.setTag(adapterPosition);
//        final CommFragment.InfoHolder tempHolder = holder;
        ll_get_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) share.getTag();
                if (pos != adapterPosition) return;
                if (callback != null) callback.show(listInfo, adapterPosition, ll_get_good.getId());
            }
        });

    }


    public void setOnKeyShareCallback(OnKeyShareCallback callback) {
        this.callback = callback;
    }

}
