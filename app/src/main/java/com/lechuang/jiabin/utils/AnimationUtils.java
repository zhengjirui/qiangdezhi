package com.lechuang.jiabin.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import static android.R.attr.translationY;

/**
 * 作者：li on 2017/11/8 15:00
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class AnimationUtils {

    private static  ObjectAnimator translationY;
    private static ObjectAnimator translationY1;
    private static ObjectAnimator remote;
    private static ObjectAnimator remote1;

    /**
     * 下滑
     */
    public static ObjectAnimator  getDownAnim(View view){
        if(translationY==null) {
            int scrolly = view.getHeight();
            translationY = ObjectAnimator.ofFloat(view, "translationY",-scrolly,0);
            translationY.setDuration(500);
            translationY.setRepeatCount(0);
        }
        return translationY;
    }
    /**
     * 上滑
     */
    public static ObjectAnimator getUpAnim(View view){
        if(translationY1==null) {
            int scrolly = view.getHeight();
            translationY1 = ObjectAnimator.ofFloat(view, "translationY",0, -scrolly);
            translationY1.setDuration(500);
            translationY1.setRepeatCount(0);
        }
        return translationY1;
    }

    public static ObjectAnimator getDownRemoteAnim(View view) {

        if (remote == null) {
            remote = ObjectAnimator.ofFloat(view, "rotation", 0, 180);
            remote.setDuration(1000);
            remote.setRepeatCount(0);
        }
        return  remote;
    }
    public static ObjectAnimator getUpRemoteAnim(View view) {

        if (remote1 == null) {
            remote1 = ObjectAnimator.ofFloat(view, "rotation", 180,0);
            remote1.setDuration(1000);
            remote1.setRepeatCount(0);
        }
        return  remote1;
    }
}
