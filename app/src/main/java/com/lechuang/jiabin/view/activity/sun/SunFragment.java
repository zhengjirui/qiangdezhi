package com.lechuang.jiabin.view.activity.sun;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.utils.Utils;
import com.lechuang.jiabin.view.activity.home.SearchResultActivity;
import com.lechuang.jiabin.view.defineView.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class SunFragment extends Fragment {
    Unbinder unbinder;
    @BindView(R.id.et_content)
    ClearEditText etContent;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.iv_checkbox)
    ImageView ivCheckbox;
    @BindView(R.id.iv_ticket_step)
    ImageView ivStep;

    private boolean isShow = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_suofanli, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
      /*  ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText("some thing");*/
        Editable text1 = etContent.getText();
        if(!TextUtils.isEmpty(text1)){
            return;
        }
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        CharSequence text = cm.getText();
        if (text != null) {
            etContent.setText(text);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_search, R.id.iv_checkbox})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search: {
                //搜索内容
                String search = etContent.getText().toString().trim();
                if (search == null || search.isEmpty()) {
                    Utils.show(getActivity(), "搜索内容不能为空");
                    return;
                }
                //判断是否包含Emoji表情
                if (Utils.containsEmoji(search)) {
                    Utils.show(getActivity(), getActivity().getResources().getString(R.string.no_emoji));
                    return;
                }

                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra("type", 2);
                //rootName传递过去显示在搜索框上
                intent.putExtra("rootName", search);
                //rootId传递过去入参
                intent.putExtra("rootId", search);
                startActivity(intent);
            }
                break;
            case R.id.iv_checkbox:
                if (!isShow) {
                    isShow = true;
                    ivCheckbox.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticket_hide));
                    ivStep.setVisibility(View.VISIBLE);
                } else {
                    isShow = false;
                    ivCheckbox.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticket_show));
                    ivStep.setVisibility(View.GONE);
                }
                break;
        }
    }
}
