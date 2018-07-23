package org.gaby;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


//import com.chongzzz.texas.R;

import org.cocos2dx.javascript.AppActivity;
import org.cocos2dx.lib.ResizeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * by Gabriel on 2017-09-02.
 */

public class LoadingUtil {

    private WeakReference<AppActivity> act;

    public LoadingUtil(AppActivity activity) {
        act = new WeakReference<AppActivity>(activity);
    }

    private static class GAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }

    private ImageView button = null;
    private View dialog = null;

    public void showLoading(boolean show, int autoCloseSecond) {
        JsBridge.log("show=" + show + " autoCloseSecond=" + autoCloseSecond);

        final AppActivity activity = act.get();
        if (activity == null) {

        }
        // 关闭
        else if (!show) {
            // if (dialog != null) {
            // dialog.dismiss();
            // }
            if (dialog != null) {
                dialog.setVisibility(View.GONE);
            }
        }
        // 显示
        else {

            //if (button == null) {
            // dialog = new Dialog(activity, R.style.CustomDialog);
            // dialog.setCancelable(false);
            // dialog.setCanceledOnTouchOutside(false);
            // dialog.setContentView(R.layout.progress_dialog);
            //
            // Window window = dialog.getWindow();
            // WindowManager.LayoutParams params = window.getAttributes();
            // params.dimAmount = 0f;
            // window.setAttributes(params);
            //
            // button = (ImageView) dialog.findViewById(R.id.test_button);
            // _exeAnim(button);
            //}
            //            if (!dialog.isShowing()) {
            //                dialog.show();
            //            }

            if (button == null) {
               // ResizeLayout resizeLayout = activity.getContentView();
               // dialog = activity.getLayoutInflater().inflate(R.layout.progress_dialog, null);
//                View root = dialog.findViewById(R.id.root);
//                root.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                    }
//                });
//                ResizeLayout.LayoutParams lp = (ResizeLayout.LayoutParams) resizeLayout.getLayoutParams();
//                resizeLayout.addView(dialog, lp);
//
//                button = (ImageView) dialog.findViewById(R.id.test_button);
//                _exeAnim(button);
            }
            if (dialog.getVisibility() != View.VISIBLE) {
                dialog.setVisibility(View.VISIBLE);
            }

            // 如果需要计时器就开始计时
            if (autoCloseTimer != null) {
                autoCloseTimer.cancel();
            }
            if (autoCloseSecond > 0) {
                autoCloseTimer = new Timer();
                autoCloseTimer.schedule(new TimerTask() {
                    public void run() {
                        // dialog.dismiss();
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                dialog.setVisibility(View.GONE);
                            }
                        });
                        autoCloseTimer.cancel();
                    }
                }, autoCloseSecond * 1000);
            }
        }
    }

    private Timer autoCloseTimer = null;

    private void _exeAnim(final ImageView button) {
        button.clearAnimation();
        long roundTime = 2000;//一周所用时间
        long deley = 0;

        final AnimatorSet set = new AnimatorSet();
        List<Animator> ans = new ArrayList<Animator>();
        // 黑
//        button.setImageResource(R.mipmap.loading_p1);
//        {
//            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 0f, 90f);
//            // 显示延迟
//            an1.setStartDelay(deley);
//            // 转90度
//            an1.setDuration(roundTime / 4);
//
//            // 红桃
//            an1.addListener(new GAnimatorListener() {
//                @Override
//                public void onAnimationEnd(Animator animator) {
//                    button.setImageResource(R.mipmap.loading_p2);
//                }
//            });
//            ans.add(an1);
//        }
        {
            // 转动到平摊
            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 90f, 180f);
            an1.setDuration(roundTime / 4);
            ans.add(an1);
        }
        {
            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 0f, 90f);
            // 延迟
            an1.setStartDelay(deley);
            // 转动到90度
            an1.setDuration(roundTime / 4);
            // 变梅花
//            an1.addListener(new GAnimatorListener() {
//                @Override
//                public void onAnimationEnd(Animator animator) {
//                    button.setImageResource(R.mipmap.loading_p3);
//                }
//            });
            ans.add(an1);
        }
        {
            // 转动到平摊
            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 90f, 180f);
            an1.setDuration(roundTime / 4);
            ans.add(an1);
        }
        {
            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 0f, 90f);
            // 延迟
            an1.setStartDelay(deley);
            // 转动到90度
            an1.setDuration(roundTime / 4);
            // 变方块
            an1.addListener(new GAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    //button.setImageResource(R.mipmap.loading_p4);
                }
            });
            ans.add(an1);
        }
        {
            // 转动到平摊
            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 90f, 180f);
            an1.setDuration(roundTime / 4);
            ans.add(an1);
        }
        {
            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 0f, 90f);
            // 延迟
            an1.setStartDelay(deley);
            // 转动到90度
            an1.setDuration(roundTime / 4);
            // 变梅花
            an1.addListener(new GAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                   // button.setImageResource(R.mipmap.loading_p1);
                }
            });
            ans.add(an1);
        }
        {
            // 转动到平摊
            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 90f, 180f);
            an1.setDuration(roundTime / 4);
            ans.add(an1);
        }
        {
            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 0f, 90f);
            // 延迟
            an1.setStartDelay(deley);
            // 转动到90度
            an1.setDuration(roundTime / 4);
            // 变黑桃
            an1.addListener(new GAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                //    button.setImageResource(R.mipmap.loading_p1);
                }
            });
            ans.add(an1);
        }
        {
            // 转动到平摊
            ObjectAnimator an1 = ObjectAnimator.ofFloat(button, "rotationY", 90f, 180f);
            an1.setDuration(roundTime / 4);
            an1.addListener(new GAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    set.start();
                }
            });
            ans.add(an1);
        }
        set.playSequentially(ans);
        set.start();
    }
}
