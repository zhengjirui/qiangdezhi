package com.lechuang.jiabin.view.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lechuang.jiabin.R;
import com.lechuang.jiabin.utils.CacheUtil;
import com.lechuang.jiabin.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author yrj
 * @date 2017/10/9
 * @E-mail 1422947831@qq.com
 * @desc 版本更新的dialog
 */

public class ClearCacheDialog extends Dialog {
    private TextView textView;
    //版本描述
    private String desc;
    private Context mContext;
    private ProgressDialog pBar;
    private SharedPreferences sp;
    private String url;

    public ClearCacheDialog(Context context, String text) {
        super(context, R.style.FullScreenDialog);
        setContentView(R.layout.dialog_clear_cache);
        this.mContext = context;
        this.desc = text;

        init();

        //setCancelable(true);
        //设置该属性之后点击dialog之外的地方不消失
        //setCanceledOnTouchOutside(false);
    }


    private void init() {
        textView = (TextView) findViewById(R.id.tv_desc);
        textView.setText(desc);
        //点击更新
        findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearCache();
                dismiss();
                //清除内部cache下的内容缓存
//                deleteFilesByDirectory(mContext.getCacheDir());//装载手机本地的，目录下的缓存
//                if (Environment.getExternalStorageState().equals(
//                        Environment.MEDIA_MOUNTED)) {
//                    //清除外部cache下的内容缓存
//                    deleteFilesByDirectory(mContext.getExternalCacheDir());//sd的缓存
//                } else {
//                    Toast.makeText(mContext, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        //取消
        findViewById(R.id.tv_quxiao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }

    /**
     * 清除指定文件目录下的文件
     * @param directory
     */
    private void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 清除缓存
     */

    private void clearCache(){
        try {
            String cacheSize = CacheUtil.getTotalCacheSize(mContext);
            CacheUtil.clearAllCache(mContext);
            Toast.makeText(mContext, "本次清除" + cacheSize, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
