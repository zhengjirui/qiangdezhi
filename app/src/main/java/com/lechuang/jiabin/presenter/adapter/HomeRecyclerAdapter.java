package com.lechuang.jiabin.presenter.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.jiabin.R;
import com.lechuang.jiabin.model.bean.HomeLastProgramBean;
import com.lechuang.jiabin.view.defineView.SpannelTextView;
import com.lechuang.jiabin.view.defineView.SquareImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: guoning
 * Date: 2017/10/31
 * Description:
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.MyViewHolder> {


    private List<HomeLastProgramBean.ListBean> data;
    private Context mContext;
    private int isAgencyStatus;

    public HomeRecyclerAdapter(List<HomeLastProgramBean.ListBean> productList, Context context, int code) {
        data = productList;
        mContext = context;
        isAgencyStatus = code;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_last_program, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            HomeLastProgramBean.ListBean bean = data.get(position);
            //商品图
            Glide.with(mContext).load(bean.imgs).into(holder.ivImg);
//        //动态调整滑动时的内存占用
//        Glide.get(mContext).setMemoryCategory(MemoryCategory.LOW);
            //原价
            if (bean.couponMoney != null) {
                holder.tvJuanDescribe.setVisibility(View.VISIBLE);
                holder.llOldPrice.setVisibility(View.VISIBLE);
                holder.tvShoujia.setVisibility(View.GONE);
                holder.tvQuanMoney.setText("领券减¥" + bean.couponMoney);
            } else {
                holder.tvShoujia.setVisibility(View.VISIBLE);
                holder.tvJuanDescribe.setVisibility(View.GONE);
                holder.llOldPrice.setVisibility(View.INVISIBLE);
                holder.tvQuanMoney.setText(bean.zhuanMoney);
            }
            holder.tvOldprice.setText(bean.price + "");
            //中划线
            holder.tvOldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            //券后价
//            holder.tvNowprice.setText("¥" + bean.preferentialPrice);


            //商品名称
            SpannableString ss = new SpannableString(bean.name);
            ImageSpan imageSpan1;

            holder.spannelTextView.setShopType(Integer.parseInt(bean.shopType));
            holder.spannelTextView.setDrawText(bean.name);
            //赚
//            if (isAgencyStatus == 1) {
//                holder.tvGet.setVisibility(View.VISIBLE);
//            } else {
//                holder.tvGet.setVisibility(View.GONE);
//            }
            holder.tvGet.setText(bean.zhuanMoney);
            //销量
            holder.tvXiaoliang.setText("已抢" + bean.nowNumber + "件");
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.llLayout.setTag(position);
        holder.llLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickLitener != null) {
                    //注意这里使用getTag方法获取数据
                    mOnItemClickLitener.onItemClick(v, (int) v.getTag());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @OnClick(R.id.ll_oldPrice)
    public void onViewClicked() {
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_juan_describe)
        TextView tvJuanDescribe;
        @BindView(R.id.ll_layout)
        LinearLayout llLayout;
        @BindView(R.id.iv_img)
        SquareImageView ivImg;
        @BindView(R.id.spannelTextView)
        SpannelTextView spannelTextView;
        @BindView(R.id.tv_oldprice)
        TextView tvOldprice;
        @BindView(R.id.tv_get)
        TextView tvGet;
        @BindView(R.id.tv_nowprice)
        TextView tvNowprice;
        @BindView(R.id.tv_xiaoliang)
        TextView tvXiaoliang;
        @BindView(R.id.tv_quanMoney)
        TextView tvQuanMoney;
        @BindView(R.id.ll_oldPrice)
        LinearLayout llOldPrice;
        @BindView(R.id.tv_shoujia)
        TextView tvShoujia;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
