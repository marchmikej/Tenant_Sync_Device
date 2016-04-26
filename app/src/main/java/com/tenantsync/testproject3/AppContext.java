package com.tenantsync.testproject3;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

/**
 * Created by Dad on 9/16/2015.
 */
public class AppContext extends Application {

    private AppContext instance;
    private PowerManager.WakeLock wakeLock;
    private OnScreenOffReceiver onScreenOffReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("In AppContext onCreate");
        instance = this;
        registerKioskModeScreenOffReceiver();
    }

    private void registerKioskModeScreenOffReceiver() {
        // register screen off receiver
        System.out.println("In AppContext registerKioskModeScreenOffReciver");
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        onScreenOffReceiver = new OnScreenOffReceiver();
        registerReceiver(onScreenOffReceiver, filter);
    }

    public PowerManager.WakeLock getWakeLock() {
        if(wakeLock == null) {
            System.out.println("In AppContext getWakeLock part 2");
            // lazy loading: first call, create wakeLock via PowerManager.
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
        }
        return wakeLock;
    }
}
