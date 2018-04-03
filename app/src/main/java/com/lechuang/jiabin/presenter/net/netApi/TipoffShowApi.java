package com.lechuang.jiabin.presenter.net.netApi;

import com.lechuang.jiabin.model.bean.ResultBean;
import com.lechuang.jiabin.model.bean.TipoffDetail;
import com.lechuang.jiabin.model.bean.TipoffListBean;
import com.lechuang.jiabin.model.bean.TipoffShowBean;
import com.lechuang.jiabin.presenter.net.QUrl;


import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;


/**
 * 作者：li on 2017/9/28 15:52
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public interface TipoffShowApi {
    //爆料详情接口
    @FormUrlEncoded
    @POST(QUrl.tipOff)
    Observable<ResultBean<TipoffShowBean>> getTipoff(@Field("condition") String data, @Field("conditionType") int type, @Field("page") int page);
    //爆料详细接口
    @FormUrlEncoded
    @POST(QUrl.tipOffDetail)
    Observable<ResultBean<TipoffDetail>> getTipoffDetail(@Field("id") String id);
    //爆料评论内容接口
    @FormUrlEncoded
    @POST(QUrl.tipOffList)
    Observable<ResultBean<TipoffListBean>> getTipoffList(@Field("tipOffId") String id, @Field("page") int page);
    //发表爆料评论接口
    @FormUrlEncoded
    @POST(QUrl.tipContent)
    Observable<ResultBean<String>> sendContent(@Field("id") String id, @Field("type") int page ,@Field("content") String content);
    //为爆料点赞
    @FormUrlEncoded
    @POST(QUrl.tipPraise)
    Observable<ResultBean<String>> tipPraise(@Field("id") String id, @Field("type") int page );

}
