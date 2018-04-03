package cn.sharesdk.onekeyshare;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mob.tools.utils.ResHelper;
import com.mob.tools.utils.UIHandler;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * @author: LGH
 * @since: 2018/3/16
 * @describe:
 */

public class ShowShareDialog extends Dialog implements View.OnClickListener, PlatformActionListener {

    private Context mContent;
    private String[] mImgUrlFiles;
    private String mImgUrlFile;
    private OnekeyShare mOks;

    public ShowShareDialog(Context context, String[] imgUrlFiles) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.show_share_dialog);
        this.mContent = context;
        this.mImgUrlFiles = imgUrlFiles;
        init();

        //setCancelable(true);
        //设置该属性之后点击dialog之外的地方不消失
        //setCanceledOnTouchOutside(false);
    }

    private void init() {

        findViewById(R.id.share_qq_kongjian).setOnClickListener(this);
        findViewById(R.id.share_qq).setOnClickListener(this);
        findViewById(R.id.share_weixin).setOnClickListener(this);
        findViewById(R.id.share_friends).setOnClickListener(this);
        mOks = new OnekeyShare();
        mOks.disableSSOWhenAuthorize();
        mOks.setCallback(this);

        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.share_qq_kongjian) {
//                goToShareImage(QZone.NAME);
            mOks.setPlatform(QZone.NAME);
            mOks.setImagePath(mImgUrlFiles[0]);//确保SDcard下面存在此张图片
            mOks.show(mContent);
        } else if (id == R.id.share_qq) {
            goToShareImage(QQ.NAME);
        } else if (id == R.id.share_weixin) {
            goToShareImage(Wechat.NAME);
        } else if (id == R.id.share_friends) {
            goToShareImage(WechatMoments.NAME);
        }

    }

    private void goToShareImage(String sharePlatfrom) {

        mOks.setPlatform(sharePlatfrom);
        //mOks.setImagePath(mImgUrlFiles);//确保SDcard下面存在此张图片
        mOks.setImageArray(mImgUrlFiles);
        mOks.show(mContent);
    }

    private void goToShareOneImg() {
        mOks.setImagePath(mImgUrlFile);//确保SDcard下面存在此张图片
        mOks.show(mContent);
    }


    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        if (this.isShowing()) {
            this.dismiss();
        }
        Log.i("ssss","onComplete");

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        if (this.isShowing()) {
            this.dismiss();
        }
        Log.i("ssss","onError"+throwable);
    }

    @Override
    public void onCancel(Platform platform, int i) {
        if (this.isShowing()) {
            this.dismiss();
        }
        Log.i("ssss","oncancel");
    }

    private void toast(final String resOrName) {
        UIHandler.sendEmptyMessage(0, new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                int resId = ResHelper.getStringRes(mContent, resOrName);
                if (resId > 0) {
                    Toast.makeText(mContent, resId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContent, resOrName, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
}
