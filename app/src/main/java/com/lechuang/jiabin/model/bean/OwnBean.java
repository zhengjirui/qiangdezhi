package com.lechuang.jiabin.model.bean;

/**
 * 作者：li on 2017/10/6 15:48
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class OwnBean {

    public  Agency agency;


    public static class Agency {


        public int agencyShareRate;
        public String allianceLicenseId;
        public String androidAppkey;
        public String androidSecret;
        public int avtiveAward;
        public int avtiveSourceAward;
        public int baskAward;
        public double discountRate;
        public int exchangeInterval;
        public String id;
        public String img;
        public String integralName;
        public int integralRate;
        public int integralShowFlag;
        public String iosAppkey;
        public String iosSecret;
        public int payPrice;
        public String payPriceStr;
        public int perfectAlipayAward;
        public int perfectMaterialAward;
        public int perfectNicknameAward;
        public int perfectPhoneAward;
        public int perfectTaobaoTward;
        public double pushMoneyRate;
        public int signAward;
        public int type;
        public int uploadImgAward;
        public double upushMoneyRate;
        public int withdrawDate;
        public int withdrawMinPrice;
    }

    public class Pay {
        /**
         * sign : alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2017092008836511&biz_content=%7B%22out_trade_no%22%3A%2217110200100001%22%2C%22subject%22%3A%22%E4%BB%A3%E7%90%86%E8%B4%AD%E4%B9%B0%22%2C%22total_amount%22%3A%220.00%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&sign=OVdaeOZNlFuNr16yKQ6KAb2gLFSgESfsW9R0UU5jKx3ZeqJ1JbNE2PYZIiCSzWizjcjpMLXrwvOOwuIlbCBxttNfK64Ob6eb8hEjlLIX63GDnmvvzuPLfosxxjxuDFaVH9ZO%2Br71Mar0ccbjy5yG5ya9%2B1GqTwA0KYv8Gx0EqliGnfBTg3Ac1ErXktlB12K%2FLTXzWA90fvTs898X8K61qCUU7TRsH87eJFCtYAaq53n12xyCpR2kLmWY%2FXvXjpuHqT5m0ENNeslV2wPhjXsvzubaIWyFDaNvyM%2FzyyegY4413YqEiUZLZb5E21R4GT8HnMFEi3sTdOWlceT%2FolRMng%3D%3D&sign_type=RSA2&timestamp=2017-11-02+14%3A16%3A53&version=1.0
         */
        public String sign;
    }
}
