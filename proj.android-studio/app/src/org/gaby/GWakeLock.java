package org.gaby;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;

/**
 */

public class GWakeLock {

    private PowerManager.WakeLock mWakeLock;

    public GWakeLock(Activity activity) {
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Gabriel-Tag");
    }

    public void onResume() {
        mWakeLock.acquire();
    }

    public void onPause() {
        mWakeLock.release();
    }
}
