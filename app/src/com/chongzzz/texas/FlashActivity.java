package com.chongzzz.texas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import org.cocos2dx.javascript.AppActivity;
import org.gaby.JLog;



/**
 *  on 2017/11/16.
 */

public class FlashActivity extends Activity {

    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = savedInstanceState;
        JLog.getInstance().log("== FlashActivity ==");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(FlashActivity.this, AppActivity.class);
                        startActivity(intent);
                    }
                });
            }
        };
        t.start();
    }
}
