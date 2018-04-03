package com.lechuang.jiabin.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.lechuang.jiabin.R;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.defineView.BigPicMap;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * @author zhf 2017/08/25
 * 【晒单图片放大】
 */

public class SunBigPicActivity extends AppCompatActivity implements BigPicMap.OnItemClickListener {
    private BigPicMap iv_sun_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sun_big_pic);
        initView();
        initEvents();
    }
    public void initView() {
        iv_sun_map = (BigPicMap) findViewById(R.id.iv_sun_map);
    }

    public void initEvents() {

        iv_sun_map.setOnItemClickListener(this);
        if(getIntent().getIntExtra("live",0)==1){
            ArrayList<String> list = new ArrayList<>();
            list.add(getIntent().getStringExtra("bigImg"));
            iv_sun_map.setAdapter(list);
        }else{
            ArrayList<String> list = getIntent().getStringArrayListExtra("list");
            Utils.E(list.toString());
            iv_sun_map.setAdapter(getIntent().getStringArrayListExtra("list"));
            iv_sun_map.setCurrentItem(getIntent().getIntExtra("current", 0));
        }
    }

    @Override
    public void onItemClick(int position, Object object) {

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
