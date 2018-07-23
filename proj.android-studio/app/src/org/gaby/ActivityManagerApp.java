package org.gaby;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by CZ11 on 2018/1/23.
 */

public class ActivityManagerApp extends Application{
    //显示在当前的Activitiy或Fragment
    private static Object currentActivity;
    private static Map<String, Activity> destoryMap = new HashMap<>();

    public static void setCurrentActivity(Object currentActivity) {
        ActivityManagerApp.currentActivity = currentActivity;
    }

    public static Object getCurrentActivity() {
        return currentActivity;
    }

    private ActivityManagerApp() {

    }

    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */

    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    /**
     * 销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        for (String key : keySet) {
            if (key.equals(activityName)) {
                destoryMap.get(key).finish();
            }
        }
    }

    /**
     * 销毁所有Activity
     */
    public static void destroyAll() {
        Iterator iter = destoryMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Activity activity = (Activity) entry.getValue();
            activity.finish();
        }
    }
}
