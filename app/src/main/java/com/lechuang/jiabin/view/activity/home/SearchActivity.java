package com.lechuang.jiabin.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.base.BaseActivity;
import com.lechuang.jiabin.base.Constants;
import com.lechuang.jiabin.presenter.CommonAdapter;
import com.lechuang.jiabin.utils.SearchHistoryBean;
import com.lechuang.jiabin.utils.SearchHistoryUtils;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.defineView.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yrj
 * @date 2017/9/29
 * @E-mail 1422947831@qq.com
 * @desc 搜索界面
 */
public class SearchActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private EditText etProduct;  //搜索框
    private TextView tvSearch;   //搜索
    private FlowLayout flKeyword;  //搜索历史
    private ImageView tvHistory;  //删除历史
    private ImageView ivClose;  //删除搜索框内容
    //保存搜索历史的sp
    private SharedPreferences sp;
    private Context mContext = SearchActivity.this;
    //搜索历史
    private SearchHistoryUtils historyUtils;
    private CommonAdapter<SearchHistoryBean> mAdapter;
    private ArrayList<SearchHistoryBean> list;
    private Intent intent;
    private int whereSearch;
    private String search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search);
        //MODE_APPEND会一直叠加数据 MODE_PRIVATE存一个
        sp = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        //最大数量10条
        historyUtils = new SearchHistoryUtils(mContext, 20, sp);
        //initState();
        initEvent();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        etProduct = (EditText) findViewById(R.id.et_product);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        flKeyword = (FlowLayout) findViewById(R.id.lv_history);
        tvHistory = (ImageView) findViewById(R.id.tv_history);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEmpty(etProduct)) {
                    etProduct.setText("");
                }
            }
        });
        etProduct.setOnEditorActionListener(SearchActivity.this);
    }

    @Override
    protected void initData() {

    }


    private void initEvent() {
        intent = new Intent(mContext, SearchResultActivity.class);
        whereSearch = getIntent().getIntExtra("whereSearch", 1);
        // if(list!=null)
        findViewById(R.id.tv_search).setOnClickListener(this);
        findViewById(R.id.tv_history).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initHistory();
    }

    private void initHistory() {
        list = historyUtils.sortHistory();
        if (list != null && list.size() > 0) {
            tvHistory.setVisibility(View.VISIBLE);
        }
        List<String> mHistoryList = new ArrayList<>();
        for (SearchHistoryBean historyBean : list) {
            if (!search.equals(historyBean.history))
                mHistoryList.add(historyBean.history);
        }

        flKeyword.setFlowLayout(mHistoryList, new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(String content) {
                search(content);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:   //搜索
                //搜索内容
                search = etProduct.getText().toString().trim();
                if (search == null || search.isEmpty()) {
                    Utils.show(mContext, "搜索内容不能为空");
                    return;
                }
                //判断是否包含Emoji表情
                if (Utils.containsEmoji(search)) {
                    Utils.show(mContext, mContext.getResources().getString(R.string.no_emoji));
                    return;
                }
                historyUtils.save(search);
                tvHistory.setVisibility(View.VISIBLE);
                search(search);
                break;
            case R.id.tv_history:  //清除历史
                //清除sp里面所有数据
                historyUtils.clear();
                //清除list数据
                list.clear();
                //刷新listview
                // mAdapter.notifyDataSetChanged();
                tvHistory.setVisibility(View.GONE);
                flKeyword.clearFlowLayout();
                break;
            default:
                break;
        }
    }

    /**
     * 开始搜索
     *
     * @param search
     */
    private void search(String search) {
        if (whereSearch == 0) {
            intent = new Intent();
            intent.putExtra("content", search);
            this.setResult(1, intent);
            finish();
        } else {
            //传递一个值,搜索结果页面用来判断是从分类还是搜索跳过去的 1:分类 2:搜索界面
            intent.putExtra("type", 2);
            //rootName传递过去显示在搜索框上
            intent.putExtra("rootName", search);
            //rootId传递过去入参
            intent.putExtra("rootId", search);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        //以下方法防止两次发送请求
        if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_UP:
                    //搜索内容
                    search = etProduct.getText().toString().trim();
                    if (search == null || search.isEmpty()) {
                        Utils.show(mContext, "搜索内容不能为空");
                        return true;
                    }
                    //判断是否包含Emoji表情
                    if (Utils.containsEmoji(search)) {
                        Utils.show(mContext, mContext.getResources().getString(R.string.no_emoji));
                        return true;
                    }
                    historyUtils.save(search);
                    tvHistory.setVisibility(View.VISIBLE);
                    search(search);
                    return true;
                default:
                    return true;
            }
        }

        return false;
    }
    /**
     * 沉浸式状态栏
     */
   /* private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }*/
}
