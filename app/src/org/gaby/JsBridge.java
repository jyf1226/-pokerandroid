package org.gaby;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

//import com.chongzzz.texas.R;
//import com.chongzzz.texas.wxapi.WXPayEntryActivity;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;

//import com.tencent.mm.sdk.modelmsg.SendAuth;
//import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
//import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
//import com.tencent.mm.sdk.modelmsg.WXImageObject;
//import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
//import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
//import com.tencent.mm.sdk.modelpay.PayReq;
import com.tendcloud.tenddata.TalkingDataGA;


import org.cocos2dx.javascript.AppActivity;

import org.cocos2dx.javascript.SDKWrapper;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxHelper;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import org.gaby.weixin_imsdk.IMUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.oply.opuslib.OpusPlayer;
import top.oply.opuslib.OpusRecorder;
import top.oply.opuslib.OpusTool;

import static android.content.Context.TELEPHONY_SERVICE;


/**
 * * Created by gaby on 2017/6/29 0029.
 */

public class JsBridge {

    public static WeakReference<AppActivity> context = null;

    public final static int THUMB_SIZE = 144;

    public static String PAY_ID = "";

    public static String getWorkingDir() {
        // String folder = context.get().getExternalFilesDir("").getAbsolutePath(); // 不带斜杠结尾的
        String folder = Cocos2dxHelper.getCocos2dxWritablePath();
        GFiles.newFolder(folder);
        return folder;
    }

    public static String getSubFileNames(String path) {
        StringBuilder sb = new StringBuilder();
        File f = new File(path);
        if (f.isDirectory()) {
            File[] fs = f.listFiles();
            if (fs != null) {
                for (int i = 0; i < fs.length; i++) {
                    sb.append(fs[i].getName());
                    sb.append("|");
                }
            }
        }
        return sb.toString();
    }

    public static String getSoundDir() {
        String timDir = getWorkingDir() + "/tim";
        GFiles.newFolder(timDir);
        return timDir;
    }

    public static void shake() {
        final AppActivity a = context.get();
        if (a != null) {
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Vibrator vibrator = (Vibrator) a.getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {100, 400};   // 停止 开启 停止 开启
                    vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
                }
            });
        }
    }

    public static String getThumbDir() {
        String thumbDir = getWorkingDir() + "/thumb";
        return thumbDir;
    }

    public static void init(String file) {
        log("log file = " + file);
        JLog.getInstance().start(file);
    }

    public static void log(String str) {
        JLog.getInstance().log(str);
    }

    public static void checkRecordPermission() {
        log("checkRecordPermission Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
        Log.e("yufs", "check permission");
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context.get(), Manifest.permission.RECORD_AUDIO);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                Log.e("yufs", "没有权限");
                ActivityCompat.requestPermissions(context.get(), new String[]{Manifest.permission.RECORD_AUDIO}, 2);
            } else {
                Log.e("yufs", "有权限");
            }
        }


    }

//    public static void checkRecordPermissionSms() {
//        log("checkRecordPermission Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
//        Log.e("yufs","check permission");
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context.get(), Manifest.permission.SEND_SMS);
//            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
//                Log.e("yufs","没有权限");
//                ActivityCompat.requestPermissions(context.get(), new String[]{Manifest.permission.SEND_SMS}, 1);
//            }else{
//                Log.e("yufs","有权限");
//            }
//        }
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context.get(), Manifest.permission.READ_PHONE_STATE);
//            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
//                Log.e("yufs","没有权限");
//                ActivityCompat.requestPermissions(context.get(), new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
//            }else{
//                Log.e("yufs","有权限");
//            }
//        }
//    }


    // ============================================ 录制 ============================================
    public synchronized static void startRecord(final String file) {

        Thread t = new Thread("start record") {
            @Override
            public void run() {
                log("JsBridge start record prepare");
                boolean playing = OpusPlayer.getInstance().isWorking();
                log("playing = " + playing);
                if (playing) {
                    OpusPlayer.getInstance().stop();
                    try {
                        // 由于输送到播放设备的buffer无法被暂停
                        // 所以在停止播放后需要等待一下才能录音
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (OpusRecorder.getInstance().isWorking()) {
                    OpusRecorder.getInstance().stopRecording();
                }
                try {
                    log("JsBridge start record real");
                    OpusRecorder.getInstance().startRecording(file);
                } catch (Exception e) {
                    context.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context.get(), "请先设置语音权限", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }

            }
        };
        t.start();

    }

    public static void stopRecord() {
        log("JsBridge stop record");
        OpusRecorder.getInstance().stopRecording();
    }

    // ============================================ 播放 ============================================
    public static void playOpus(String file) {
        log("JsBridge start play");
        if (OpusPlayer.getInstance().isWorking()) {
            OpusPlayer.getInstance().stop();
        }
        if (OpusRecorder.getInstance().isWorking()) {
            OpusRecorder.getInstance().stopRecording();
        }
        OpusPlayer.getInstance().play(file);
    }

    public synchronized static void stopPlay() {
        log("JsBridge stop play");
        OpusPlayer.getInstance().stop();
    }

    // ============================================ 编码 ============================================
    private static OpusTool opusTool = new OpusTool();

    public static int decode(String wav, String out) {
        log("JsBridge decode ");
        int r = opusTool.decode(wav, out, null);
        log("out = " + out);
        log("decode = " + r);
        log("out size = " + new File(out).length());
        return r;
    }

    public static int encode(String opus, String out) {
        log("JsBridge encode ");
        String op = " --vbr --comp 1 --bitrate 32 --framesize 20 ";
        int r = opusTool.decode(opus, out, op);
        opusTool.closeOpusFile();
        log("out = " + out);
        log("op = " + op);
        log("encode = " + r);
        log("out size = " + new File(out).length());
        return r;
    }

    public static void wxLogin() {
//        final SendAuth.Req req = new SendAuth.Req();
//        req.scope = "snsapi_userinfo";
//        req.state = "none";
//        WXPayEntryActivity.api.sendReq(req);
    }

    public static void wxPay(final String json) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _wxPay(json);
            }
        });
    }

    public static void _wxPay(String json) {
//        log("json = " + json);
//        try {
//            JSONObject obj = new JSONObject(json);
//            PayReq request = new PayReq();
//            request.appId = obj.optString("appid"); // APPID
//            request.nonceStr = obj.optString("noncestr");/// 随机数
//            request.partnerId = obj.optString("partnerid"); // 商户号
//            request.packageValue = obj.optString("package"); // 固定值
//            request.timeStamp = obj.optString("timestamp"); // 时间戳
//            request.sign = obj.optString("sign"); //签名
//            request.prepayId = obj.optString("prepayid");  // 预付款订单号
//            WXPayEntryActivity.api.sendReq(request);
//        } catch (JSONException e) {
//            log("wxPay err " + e.getMessage());
//            e.printStackTrace();
//        }
    }

    public static void callJs(final String js) {
        if (jsReady) {
            log("native called js: (" + js + ")");
            Cocos2dxHelper.runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    Cocos2dxJavascriptJavaBridge.evalString(js);
                }
            });
        }
    }

    public static void aliPay(final String json) {
        try {
            Class mClass = SDKWrapper.getInstance().getClass();
            SDKWrapper instance = SDKWrapper.getInstance();
            mClass.getMethod("aliPay", String.class).invoke(instance, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //在需要分享的地方添加代码：
    // 0 分享到微信好友
    // 1 分享到微信朋友圈
    public static void share(int flag, String title, String desc, String url) {
//        Map<String,Object> map=new HashMap<>();
//        map.put("type","链接");
//        String str = flag == 0 ? "微信好友" : "微信朋友圈";
//        map.put("flag",str);
//        TalkingDataGA.onEvent("微信分享");
//        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = url;// "http://192.168.199.158:7456/build/1.html";
//        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = title;
//        msg.description = desc;
//        //这里替换一张自己工程里的图片资源
//
//        {
//            Bitmap bmp = BitmapFactory.decodeResource(context.get().getResources(), R.mipmap.logo512);
//            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//            bmp.recycle();
//            msg.thumbData = Ut.bmpToByteArray(thumbBmp);
//            thumbBmp.recycle();
//        }
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = msg;
//        req.scene = (flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline);
//        WXPayEntryActivity.api.sendReq(req);
    }

    public static void shareShot(final int flag, final String fullfile, final String shot) {
        Map<String,Object> map=new HashMap<>();
        map.put("type","截图");
        String str=  flag==0?"微信好友":"微信朋友圈";
        map.put("flag",str);
        TalkingDataGA.onEvent("微信分享");
        Thread t = new Thread("share pic") {
            @Override
            public void run() {

                // 如果截图需要另存
                if (shot != null) {
                    copyFile(shot, fullfile);
                }

                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 开始真正的分享
                context.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _sharePic(flag, fullfile);
                }
                });
            }
        };
        t.start();
    }

    public static void _sharePic(int flag, String path) {
//        log("sharePic path=" + path);
//        File file = new File(path);
//        if (!file.exists()) {
//            JsBridge.callJs("cz.tool.toast('分享的图片不存在');");
//        } else {
//
//            WXImageObject imgObj = new WXImageObject();
//            imgObj.setImagePath(path);
//
//            WXMediaMessage msg = new WXMediaMessage();
//            msg.mediaObject = imgObj;
//
//            Bitmap bmp = BitmapFactory.decodeFile(path);
//
//            float w = bmp.getWidth();
//            float h = bmp.getHeight();
//
//            float scale = 1.0f;
//            int w2 = 0;
//            int h2 = 0;
//            if (w > h) {
//                scale = THUMB_SIZE / w;
//                w2 = THUMB_SIZE;
//                h2 = (int) (h * scale);
//            } else {
//                scale = THUMB_SIZE / h;
//                w2 = (int) (w * scale);
//                h2 = THUMB_SIZE;
//            }
//
//            JsBridge.log("_sharePic() w2=" + w2 + " h2=" + h2);
//            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, w2, h2, true);
//            bmp.recycle();
//            msg.thumbData = Ut.bmpToByteArray(thumbBmp);
//            thumbBmp.recycle();
//
//            SendMessageToWX.Req req = new SendMessageToWX.Req();
//            req.transaction = buildTransaction("img");
//            req.message = msg;
//            req.scene = (flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline);
//            WXPayEntryActivity.api.sendReq(req);
//        }
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    // 不支持
    public static void share1(int flag) {
//        final WXAppExtendObject appdata = new WXAppExtendObject();
//        appdata.extInfo = "this is ext info";
//        appdata.fileData = "see ya!".getBytes();
//        appdata.filePath = "asdfasdfasdf";
//        final WXMediaMessage msg = new WXMediaMessage();
//        msg.title = "这里填写标题";
//        msg.description = "这里填写内容";
//        msg.mediaObject = appdata;
//        {
//            int THUMB_SIZE = 144;
//            Bitmap bmp = BitmapFactory.decodeResource(context.get().getResources(), R.mipmap.logo512);
//            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//            bmp.recycle();
//            msg.thumbData = Ut.bmpToByteArray(thumbBmp);
//            thumbBmp.recycle();
//        }
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = msg;
//        req.scene = (flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline);
//        req.openId = "";
//        WXPayEntryActivity.api.sendReq(req);
    }


    public static void choosePhoto(final int max, final int width, final int height) {
        log("choose photos max=" + max);
        final AppActivity a = context.get();
        if (a != null) {
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   log("ImageChooserActivity.startChooser(a, (max == 1), max, width, height);");
                }
            });
        }
    }


    private static String getMd5ByFile(String file) {
        File f = new File(file);
        return getMd5ByFile(f);
    }

    private static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }





    // 文件移动
    public static void copyFile(final String ffrom, final String fto) {
        Thread t = new Thread("copy_file") {
            @Override
            public void run() {
                try {
                    GFiles.copyFileFast(ffrom, fto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }


    public volatile static boolean jsReady = false;

    public static void setJsReady() {
        log("Js is ready, ok");
        Log.e("yufs","Js is ready, ok");
        jsReady = true;
        // 开始获取GPS
        log("start location GPS...");
        AppActivity ac = context.get();
        ac.removeLaunchImage();
//        if (ac != null) {
//            ac.getGpsUtil().onResume(ac);
//        }
    }

    public static void handleScheme(Intent intent) {
        log("read intent:" + intent);
        if (intent != null) {
            final String dataString = intent.getDataString();
            final String scheme = intent.getScheme();
            handleScheme(scheme, dataString);
        }
    }

    public static void handleScheme(final String scheme, final String dataString) {
        log("read scheme:" + scheme);
        log("read scheme-url:" + dataString);
        if (scheme != null && dataString != null) {
            Thread t = new Thread("handle-scheme") {
                @Override
                public void run() {

                    // 当GLThread没有准备好的时候等待
                    while (!jsReady) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // 呼叫入
                    callJs("cz.sys.receiveScheme('" + scheme + "','" + dataString + "');");
                }
            };
            t.start();
        }
    }

    ////////////////////// 电池监听 ////////////////////////////////////////////////////////////////////////////////////////
    private static WeakReference<BroadcastReceiver> batteryLevelRcvr = null;

    public static void monitorBatteryState(Activity activity) {
        BroadcastReceiver b = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                if (rawlevel >= 0 && scale > 0) {
                    int battery = (rawlevel * 100) / scale;
                    callJs("cz.sys.setBattery(" + battery + ")");
                }
            }
        };
        batteryLevelRcvr = new WeakReference<BroadcastReceiver>(b);
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        activity.registerReceiver(b, batteryLevelFilter);
    }

    public static void unregisterBatteryLevelRcvr(Activity activity) {
        if (batteryLevelRcvr != null && batteryLevelRcvr.get() != null) {
            activity.unregisterReceiver(batteryLevelRcvr.get());
        }
    }

    ////////////////////// 信号监听 ////////////////////////////////////////////////////////////////////////////////////////
    private static PhoneStateListener signalListener = null;
    private static String signalType = null;
    private static int signalValue = 0;
    private static Thread signalThread = null;
    private static int signalStrengthDbm;
    private static int lastSignalValue;
    private static  TelephonyManager tel;

    public static void monitorSignal(final Activity activity, boolean isStart) {
        if (signalListener == null && isStart) {
            signalListener = new PhoneStateListener() {
                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    super.onSignalStrengthsChanged(signalStrength);
//                    String type = getNetworkType();
//                    int value = signalStrength.getGsmSignalStrength();
//                    if (signalType != null && signalType.equals(type) && signalValue == value) {
//                        // 信号相同，不回调
//                    } else {
//                        signalType = type;
//                        signalValue = value;
//                    }
//                    if (signalType != null  && signalValue == value) {
//                        // 信号相同，不回调
//                    } else {
//                        signalType = type;
//                        signalValue = value;
//                    }

                    //获取dbm型号强度
                    signalStrengthDbm = getSignalStrengthByName(signalStrength, "getDbm");

                }
            };
            tel = (TelephonyManager) activity.getSystemService(TELEPHONY_SERVICE);
            tel.listen(signalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }else{
            //解注册
            if(tel!=null&&signalListener!=null){
                tel.listen(signalListener,PhoneStateListener.LISTEN_NONE);
                signalThread = null;
            }
        }


        if (signalThread == null && isStart) {
            signalThread = new Thread("SignalThread") {
                @Override
                public void run() {
                    while (signalThread != null) {//改下界面显示再去调用
                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        signalType= getNetworkType();
                        if (signalType != null&&"wifi".equals(signalType)) {
                            signalValue= obtainWifiInfo(activity);
                            callJs("cz.sys.setSignalStrength('" + signalType + "', " + signalValue + ")");
                        }else if(signalType!=null){
                            if (signalStrengthDbm>=-90){
                                signalValue=100;
                            }else if(signalStrengthDbm>=-105){
                                signalValue=80;
                            }else if(signalStrengthDbm>=-115){
                                signalValue=60;
                            }else if(signalStrengthDbm>=-135){
                                signalValue=40;
                            }else{
                                signalValue=10;
                            }
                           if(signalValue != lastSignalValue){
                               callJs("cz.sys.setSignalStrength('" + signalType + "', " + signalValue + ")");
                           }
                           lastSignalValue = signalValue;
                        }
                    }
                }
            };
            signalThread.start();
        }
    }

    public static int obtainWifiInfo(Context context) {
        // Wifi的连接速度及信号强度：
        int strength = 0;
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            // 链接信号强度，100为获取的信号强度值在5以内
            strength = WifiManager.calculateSignalLevel(info.getRssi(), 100);
        }
        return strength;
    }

    private static int getSignalStrengthByName(SignalStrength signalStrength, String methodName)
    {
        try
        {
            Class classFromName = Class.forName(SignalStrength.class.getName());
            java.lang.reflect.Method method = classFromName.getDeclaredMethod(methodName);
            Object object = method.invoke(signalStrength);
            return (int)object;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    ////////////////////// 网络类型获取 ////////////////////////////////////////////////////////////////////////////////////////

    private static String getNetworkType() {
        String netType = "unknown";
        Activity ac = context.get();
        if (ac != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) ac.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                int nType = networkInfo.getType();
                if (nType == ConnectivityManager.TYPE_MOBILE) {
                    String extraInfo = networkInfo.getExtraInfo();
                    if (extraInfo != null && extraInfo.trim().length() > 0) {
                        if (extraInfo.toLowerCase().equals("cmnet")) {
                            netType = "cmnet";
                        } else {
                            netType = "cmwap";
                        }
                    }
                } else if (nType == ConnectivityManager.TYPE_WIFI) {
                    netType = "wifi";
                }
            }
        }
        return netType;
    }


    // optional string		imei	= 1;	//设备IMEI
    // optional string		osVer   = 2;	//系统版本号
    // optional string		model	= 3;	//机型
    // optional string		macAddr	= 4;	//MAC地址
    public static String getDeviceInfo() {
        Context ctx = context.get();
        JSONObject json = new JSONObject();
        if (ctx != null) {
            try {
                TelephonyManager service = (TelephonyManager) ctx.getSystemService(TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(context.get(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context.get(),new String[] {
                            Manifest.permission.READ_PHONE_STATE
                    },2);
                }
                json.put("imei", service.getDeviceId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                // WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                // String mac = wifi.getConnectionInfo().getMacAddress();
                //得到的值是一个0到-100的区间值，是一个int型数据，其中0到-50表示信号最好，-50到-70表示信号偏差，小于-70表示最差，有可能连接不上或者掉线，一般Wifi已断则值为-200。
                String mac = getMacAddress();
                // String mac = NetworkInterface.getHardwareAddress();
                json.put("macAddr", mac);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                // getPackageName()是当前类的包名，0代表是获取版本信息
                // PackageManager packageManager = ctx.getPackageManager();
                // PackageInfo packInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
                String release = Build.VERSION.RELEASE;
                json.put("osVer", release);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                // model 机型
                String model = Build.MODEL;
                json.put("model", model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String jsonString = json.toString();
        log("device info json string = " + jsonString);
        return jsonString;
    }



    private static String getMacAddress() throws SocketException {

        String mac = null;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iF = interfaces.nextElement();

            byte[] addr = iF.getHardwareAddress();
            if (addr == null || addr.length == 0) {
                continue;
            }
            StringBuilder buf = new StringBuilder();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            String _mac = buf.toString();

            // 优先 wlan0
            if (iF.getName().contains("wlan")) {
                mac = _mac;
                break;
            }
            // 次要 dummy0
            if (mac == null && iF.getName().contains("dummy")) {
                mac = _mac;
            }
        }
        return mac;
    }

    public static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null && line.contains(filter) == false) {
                //result += line;
                log("line: " + line);
            }
            result = line;
            log("line: " + line);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void loading(final int showing, final int autoCloseSecond) {
        log("loading ... " + showing);
        final AppActivity act = context.get();
        if (act == null) {
        } else {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (showing == 1) {
                        act.getLoadingUtil().showLoading(true, autoCloseSecond);
                    } else {
                        act.getLoadingUtil().showLoading(false, 0);
                    }
                }
            });
        }
    }

    public static void copyToClipboard(final String str) {
        AppActivity act = context.get();
//        if (act != null) {
//            copy(act, str);
//            callJs("cz.tool.toast('已复制到系统剪切板')");
//        }
        try
        {
            Runnable runnable = new Runnable() {
                public void run() {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) Cocos2dxActivity.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", str);
                    clipboard.setPrimaryClip(clip);
                }
            };
            //getSystemService运行所在线程必须执行过Looper.prepare()
            //否则会出现Can't create handler inside thread that has not called Looper.prepare()
            act.runOnUiThread(runnable);
        }catch(Exception e){
             Log.e("cocos2dx","copyToClipboard error");
            e.printStackTrace();
        }
        callJs("cz.tool.toast('已复制到系统剪切板')");

    }

    private static void copy(Context context, String str) {
        Log.e("yufs","ClipboardManager===");
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(str.trim());
        Log.e("yufs","ClipboardManager===2"+cmb);
    }

    private static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    public static void enterTimChatRoom(final String uid, final String sig, final String groupId) {
        log("[TIM] JsBridge.enterTimChatRoom() uid=" + uid + " sig=" + sig + " groupId=" + groupId);
        final AppActivity act = context.get();
        if (act != null) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    act.getImUtil().enter(uid, sig, groupId, iTimListener);
                }
            };
            t.start();
        }
    }

    public static void quitTimChatRoom(final String groupId) {
        log("[TIM] JsBridge.quitTimChatRoom() groupId=" + groupId);
        final AppActivity act = context.get();
        if (act != null) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    act.getImUtil().exitGroup(groupId);
                }
            };
            t.start();
        }
    }


    // 发送语音
    public static void sendTimSound(final int sec, final String file, final String groupId) {
        log("[TIM] JsBridge.sendTimSound() sec=" + sec + " file=" + file + " groupId=" + groupId);
        final AppActivity act = context.get();
        if (act != null) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        act.getImUtil().sendMessage(groupId, file, sec);
                    } catch (Exception e) {
                        log("发送语音发生异常" + e.getMessage() + "\n" + e.getLocalizedMessage());
                    }
                }
            };
            t.start();
        }
    }

    private static IMUtil.ITimListener iTimListener = new IMUtil.ITimListener() {
        @Override
        public boolean onNewMessages(List<TIMMessage> msgs) {
            // 收到新消息
            for (int r = 0; r < msgs.size(); ++r) {
                final TIMMessage msg = msgs.get(r);
                final String sender = msg.getSender();
                for (int i = 0; i < msg.getElementCount(); ++i) {
                    TIMElem em = msg.getElement(i);
                    TIMElemType elemType = em.getType();
                    // 如果是语音
                    if (elemType == TIMElemType.Sound) {
                        final TIMSoundElem sound = (TIMSoundElem) em;
                        final String dir = JsBridge.getSoundDir();
                        final String file = Ut.getAvailableFile(dir, ".opus");
                        log("[TIM] IMUtil 收到语音文件，正在下载...file=" + file);
                        sound.getSoundToFile(file, new TIMCallBack() {
                            @Override
                            public void onError(int code, String desc) {
                                JsBridge.log("[TIM] Tencent IM download sound error, code=" + code + " desc=" + desc);
                            }

                            @Override
                            public void onSuccess() {
                                // 当下载完成
                                log("[TIM] IMUtil 语音文件下载完成.file=" + file);
                                long sec = sound.getDuration();
                                JsBridge.log("[TIM] Tencent IM download OK, duration=" + sound.getDuration());
                                JsBridge.callJs("cz.sys.receivedSound('groupId', '" + sender + "', " + sec + ", '" + file + "');");
                            }
                        });

                    } else {
                        JsBridge.log("[TIM] Tencent IM receive unkown type message.");
                    }
                }
            }
            return true;
        }

        @Override
        public void onSuccess() {
            JsBridge.log("[TIM] 成功进入聊天室");
        }

        @Override
        public void onError(String step, int code, String desc) {
            JsBridge.log("[TIM] 进入聊天室错误,code=" + code + " desc=" + desc);
            JsBridge.callJs("cz.sys.onTimError('" + step + "'," + code + ", '" + desc + "');");
        }
    };

    public static void crop(String file, int formatWidth, int formatHeight) {
        final AppActivity act = context.get();
        if (act != null) {
            String dir = JsBridge.getWorkingDir() + "/shot";
            String fileTo = Ut.getAvailableFile(dir, ".jpg");
            GFiles.newFolder(dir);
            act.getgChooser().start(file, fileTo, formatWidth, formatHeight);
        }
    }

    public static String getBuildOption(String key) {
        String ret = "";

        // dpi
      if ("dpi".equals(key)) {
            ret = "" + Cocos2dxHelper.getDPI();
        }

        if (ret == null) {
            ret = "";
        }
        log("getBuildOption('" + key + "') = " + ret);
        return ret;
    }

    private static Bundle metaData = null;



    private static String getVersionName() {
        String version = null;//verison_code
        AppActivity act = context.get();
        if (act != null) {
            try {
                PackageManager packageManager = act.getPackageManager();
                PackageInfo packInfo = packageManager.getPackageInfo(act.getPackageName(), 0);
                version = packInfo.versionName+"."+packInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return version;
    }

    /**
     * 判断微信是否安装
     * @return
     */
    public static boolean isWeixinAvilible() {
        final PackageManager packageManager = context.get().getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void printDeviceInf() {
        StringBuilder sb = new StringBuilder();
        sb.append("PRODUCT=").append(Build.PRODUCT).append("\n");
        sb.append("BOARD=").append(Build.BOARD).append("\n");
        sb.append("BOOTLOADER=").append(Build.BOOTLOADER).append("\n");
        sb.append("BRAND=").append(Build.BRAND).append("\n");
        sb.append("CPU_ABI=").append(Build.CPU_ABI).append("\n");
        sb.append("CPU_ABI2=").append(Build.CPU_ABI2).append("\n");
        sb.append("DEVICE=").append(Build.DEVICE).append("\n");
        sb.append("DISPLAY=").append(Build.DISPLAY).append("\n");
        sb.append("FINGERPRINT=").append(Build.FINGERPRINT).append("\n");
        sb.append("HARDWARE=").append(Build.HARDWARE).append("\n");
        sb.append("HOST=").append(Build.HOST).append("\n");
        sb.append("ID=").append(Build.ID).append("\n");
        sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n");
        sb.append("MODEL=").append(Build.MODEL).append("\n");
        sb.append("PRODUCT=").append(Build.PRODUCT).append("\n");
        sb.append("RADIO=").append(Build.RADIO).append("\n");
        sb.append("SERIAL=").append(Build.SERIAL).append("\n");
        sb.append("TAGS=").append(Build.TAGS).append("\n");
        sb.append("TIME=").append(Build.TIME).append("\n");
        sb.append("TYPE=").append(Build.TYPE).append("\n");
        sb.append("USER=").append(Build.USER).append("\n");
        log(sb.toString());
    }

    //    private static SharedPreferences mShareChongzzz = null;
    //    private static Map<String, String> shareMap = null;
    //
    //    public static void initSharePreference(AppActivity activity) {
    //        if (mShareChongzzz == null) {
    //            mShareChongzzz = activity.getSharedPreferences("CHONGZZZ", Context.MODE_PRIVATE);
    //        }
    //        if (shareMap == null) {
    //            shareMap = new HashMap<String, String>();
    //            for (String key : mShareChongzzz.getAll().keySet()) {
    //                Object value = mShareChongzzz.getAll().get(key);
    //                if (value != null) {
    //                    shareMap.put(key, value.toString());
    //                }
    //            }
    //
    //        }
    //    }
    //
    //    public static void saveLocalData(final String key, final String val) {
    //        Activity activity = context.get();
    //        if (val == null) {
    //            shareMap.remove(key);
    //        } else {
    //            shareMap.put(key, val);
    //        }
    //
    //        if (activity != null) {
    //            Thread th = new Thread("SaveShare[" + key + "]") {
    //                @Override
    //                public void run() {
    //                    SharedPreferences.Editor ed = mShareChongzzz.edit();
    //                    if (val == null) {
    //                        ed.remove(key);
    //                    } else {
    //                        ed.putString(key, val);
    //                    }
    //                    ed.commit();
    //                }
    //            };
    //            th.start();
    //        }
    //    }
    //
    //    public static String getLocalData(String key) {
    //        String r = shareMap.get(key);
    //        if (r == null) {
    //            r = "";
    //        }
    //        return r;
    //    }


    public static void openUrl(final String url) {
        final AppActivity ac = context.get();
        if (ac != null) {
            ac.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    ac.startActivity(intent);
                }
            });
        }
    }

    public static void huaweiLogin() {
        try {
            Class mClass = SDKWrapper.getInstance().getClass();
            SDKWrapper instance = SDKWrapper.getInstance();
            mClass.getMethod("huaweiLogin").invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void huaweiPay(String s){
        try {

            Class mClass = SDKWrapper.getInstance().getClass();
            SDKWrapper instance = SDKWrapper.getInstance();
            mClass.getMethod("huaweiPay",String.class).invoke(instance,s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void vivoPay(String s){
        try{
            Class mClass=SDKWrapper.getInstance().getClass();
            SDKWrapper instance=SDKWrapper.getInstance();
            mClass.getMethod("vivoPay",String.class).invoke(instance,s);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void vivoPayNow(String type,String s){
        try{
            Class mClass=SDKWrapper.getInstance().getClass();
            SDKWrapper instance=SDKWrapper.getInstance();
            mClass.getMethod("vivoPayNow", String.class, String.class).invoke(instance,type,s);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void vivoLogin( ){
//        checkRecordPermissionSms();
        try{
            Class mClass=SDKWrapper.getInstance().getClass();
            SDKWrapper instance=SDKWrapper.getInstance();
            mClass.getMethod("vivoLogin").invoke(instance);
            Log.e("yufs","Vivo logining====================");
        }catch (Exception e){
            e.printStackTrace();
            Log.e("yufs","Vivo login err:");
        }
    }

    public static void vivoExit(){
        try{
            Class mClass=SDKWrapper.getInstance().getClass();
            SDKWrapper instance=SDKWrapper.getInstance();
            mClass.getMethod("vivoExit").invoke(instance);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void oppoLogin(){
        try{
            Class mClass=SDKWrapper.getInstance().getClass();
            SDKWrapper instance=SDKWrapper.getInstance();
            mClass.getMethod("oppoLogin").invoke(instance);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void oppoLoginOut(){
        try{
            Class mClass=SDKWrapper.getInstance().getClass();
            SDKWrapper instance=SDKWrapper.getInstance();
            mClass.getMethod("oppoLoginOut").invoke(instance);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void exitApp(){
//        System.exit(0);
        try{
            Class mClass=SDKWrapper.getInstance().getClass();
            SDKWrapper instance=SDKWrapper.getInstance();
            mClass.getMethod("oppoExitLogin").invoke(instance);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void oppoPay(String params){
        try{
            Class mClass=SDKWrapper.getInstance().getClass();
            SDKWrapper instance=SDKWrapper.getInstance();
            mClass.getMethod("oppoPay",String.class).invoke(instance,params);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void paySuccess(String s) {
        TalkingData.onChargeSuccess(s);
    }
}
