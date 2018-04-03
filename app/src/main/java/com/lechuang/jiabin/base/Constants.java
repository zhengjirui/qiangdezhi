package com.lechuang.jiabin.base;

/**
 * 常量
 */
public interface Constants {
    int ERROR_NET = 400;
    int END_NET = 100;
    int SUCCESSED =200 ;
    //最大的图片选择数
    String MAX_SELECT_COUNT = "max_select_count";
    //是否单选
    String IS_SINGLE = "is_single";
    //初始位置
    String POSITION = "position";

    //初始位置
    String IS_CONFIRM = "is_confirm";
    int RESULT_CODE = 0x00000012;
    //头像名称
    String HEAD = "/head.jpg";
    //60秒倒计时
    int TIME = 60;
    //广告图6秒倒计时
    int ADVERTISEMENT_TIME = 6;
    //网络请求状态码
    int STATE = 200;
    // sp文件的名称
    String SP_NAME = "configuration";
    // 保存用户信息的sp文件的名称
    String USERINFO = "loginInfo";
    //淘宝pid
    String PID = "mm_31301838_0_0";
    //appkey  (百川的appkey 用于客服功能  注意替换)
    String APP_KEY = "24722774";
    //app  package_fileProvider
    String package_fileProvider = "com.lechuang.jiabin.fileProvider";
    //传递商品信息到商品详情的键
    String listInfo = "listInfo";
    //存商品分享域名的key
    String getShareProductHost = "getShareProductHost";
}
