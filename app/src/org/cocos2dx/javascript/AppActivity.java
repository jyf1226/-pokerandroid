/****************************************************************************
Copyright (c) 2015 Chukong Technologies Inc.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.javascript;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import org.cocos2dx.javascript.SDKWrapper;
import org.gaby.GChooser;
import org.gaby.GWakeLock;
import org.gaby.JsBridge;
import org.gaby.LoadingUtil;
import org.gaby.SoftKeyBoardListener;
import org.gaby.weixin_imsdk.IMUtil;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class AppActivity extends Cocos2dxActivity {

    private static final int WHAT_DELETE_IMAGE = 1;

    private LoadingUtil loadingUtil = new LoadingUtil(this);
    private GWakeLock gWakeLock;
    private AppActivity.WifiBroadCastReceiver wifiBroadCastReceiver;
    //    private GPSUtil gpsUtil = new GPSUtil() {
//        @Override
//        public void onLocationChanged(Location location) {
//            if (location != null) {
//                String lat = Double.toString(location.getLatitude());
//                String lon = Double.toString(location.getLongitude());
//                if (lat != null && lon != null) {
//                    JsBridge.callJs("cz.sys.receiveLocation('" + lon + "', '" + lat + "');");
//                }
//            }
//        }
//    };
    private final IMUtil imUtil = new IMUtil() {
        @Override
        protected void log(String tag, String str) {
            JsBridge.log("[TIM][" + tag + "]" + str);
        }
    };
    private final GChooser gChooser = new GChooser(this, 3413, 3533) {
        @Override
        public void chose(String file, int w, int h) {
            List<String> files = new ArrayList<String>();
            files.add(file);
            Log.e("delete","JsBridge.onChosed(files, w, h);");
        }
    };





//    public GPSUtil getGpsUtil() {
//        return gpsUtil;
//    }

    public IMUtil getImUtil() {
        return imUtil;
    }

    public GChooser getgChooser() {
        return gChooser;
    }

    private static final int appId = 1400040064;



    private void registerWifiBroadCast() {
        wifiBroadCastReceiver = new AppActivity.WifiBroadCastReceiver();
        registerReceiver(wifiBroadCastReceiver,new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    private void addImage() {
        mUIHandler = new Handler();
        addContentView(createLaunchImage(),
                new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.FILL_PARENT,
                        WindowManager.LayoutParams.FILL_PARENT));
    }

    protected static Handler mUIHandler;
    private static ImageView img = null;

    protected ImageView createLaunchImage() {
        img = new ImageView(this);

        return img;
    }

    public static void removeLaunchImage() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (img != null) {
                    img.setVisibility(View.GONE);
                    Log.e("yufs","imge gone");
                }
            }
        });
    }


    protected Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_DELETE_IMAGE:
                    if (img != null) {
                        img.setVisibility(View.GONE);
                        Log.e("yufs","imge gone 10s");
                    }
                    break;
            }
        }
    };

    private void delayedRemoveImage() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Message message=Message.obtain();
                message.what=WHAT_DELETE_IMAGE;
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task, 10000);
    }

    private void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(this,new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                Log.e("yufs","height=="+height);
            }

            @Override
            public void keyBoardHide(int height) {
                Log.e("yufs","height=="+height);
            }
        });
    }

    private void requestPer() {
        checkRecordPermissionSms();
    }

    public  void checkRecordPermissionSms() {
        String channel = JsBridge.getBuildOption("channel");
        //官方默认包无需短信状态权限
        if("1".equals(channel)){
            return;
        }
        //默认包不需要申请此权限
        if (Build.VERSION.SDK_INT >= 23) {
            boolean isAllGranted = hasPermission(
                    new String[]{
                            Manifest.permission.SEND_SMS

                    });
            if (isAllGranted) {
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.SEND_SMS
            }, 2);
        }
    }

    private void confirm(String s,int code) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean b=hasPermission(s);
            if (!b) {
                //没有权限
                ActivityCompat.requestPermissions(this, new String[]{s}, code);
            }else{
                Log.e("yufs","permission success");
            }
        }

    }

    /*
    * 为子类提供权限检查方法
    * */
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public LoadingUtil getLoadingUtil() {
        return loadingUtil;
    }

    private String getAppInfo() {
        try {
            String pkName = this.getPackageName();
            String versionName = this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = this.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("yufs","短信动态权限申请成功");
                } else {

                    // 权限请求失败的操作
                    Log.e("yufs","短信动态权限申请失败");
                }
                return;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("yufs","动态权限申请成功");
                } else {

                    // 权限请求失败的操作
                    Log.e("yufs","动态权限申请失败");
                }
                return;
            }
        }
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public int obtainWifiInfo() {
        // Wifi的连接速度及信号强度：
        int strength = 0;
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            // 链接信号强度，100为获取的信号强度值在5以内
            strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
        }
        return strength;
    }

    class WifiBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int strength = obtainWifiInfo();
            Log.e("yufs","wifiBroadCastReceiver length ="+strength);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // Workaround in https://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time/16447508
        if (!isTaskRoot()) {
            // Android launched another instance of the root activity into an existing task
            //  so just quietly finish and go away, dropping the user back into the activity
            //  at the top of the stack (ie: the last state of this task)
            // Don't need to finish it again since it's finished in super.onCreate .
            return;
        }
        // DO OTHER INITIALIZATION BELOW
        
        SDKWrapper.getInstance().init(this);
    }
	
    @Override
    public Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
        // TestCpp should create stencil buffer
        glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);

        SDKWrapper.getInstance().setGLSurfaceView(glSurfaceView);

        return glSurfaceView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SDKWrapper.getInstance().onResume();
      //  gWakeLock.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SDKWrapper.getInstance().onPause();
     //   gWakeLock.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SDKWrapper.getInstance().onDestroy();
        JsBridge.monitorSignal(this, false);
        JsBridge.unregisterBatteryLevelRcvr(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKWrapper.getInstance().onActivityResult(requestCode, resultCode, data);
        gChooser.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SDKWrapper.getInstance().onNewIntent(intent);
        // 处理scheme
        JsBridge.handleScheme(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SDKWrapper.getInstance().onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SDKWrapper.getInstance().onStop();
    }
        
    @Override
    public void onBackPressed() {
        SDKWrapper.getInstance().onBackPressed();
        super.onBackPressed();
        JsBridge.callJs("cz.global.onBack();");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        SDKWrapper.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        SDKWrapper.getInstance().onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SDKWrapper.getInstance().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        SDKWrapper.getInstance().onStart();
        super.onStart();
    }
}
