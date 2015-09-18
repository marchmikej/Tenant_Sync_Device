package com.tenantsync.testproject3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;

/**
 * Created by Dad on 9/16/2015.
 */
public class OnScreenOffReceiver extends BroadcastReceiver {
    private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("In OnScreenOffReceiver onReceive");
        if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
            AppContext ctx = (AppContext) context.getApplicationContext();
            // is Kiosk Mode active?
            if(isKioskModeActive(ctx)) {
                System.out.println("In OnScreenOffReceiver and waking up device");
                wakeUpDevice(ctx);
            }
        }
    }

    private void wakeUpDevice(AppContext context) {
        System.out.println("In OnScreenOffReceiver wakeUpDevice");
        PowerManager.WakeLock wakeLock = context.getWakeLock(); // get WakeLock reference via AppContextwell
        if (wakeLock.isHeld()) {
            System.out.println("In OnScreenOffReceiver and wakeupdevic wakelock.release");
            wakeLock.release(); // release old wake lock
        }

        // create a new wake lock...
        wakeLock.acquire();

        // ... and release again
        wakeLock.release();
    }

    private boolean isKioskModeActive(final Context context) {
        System.out.println("In OnScreenOffReceiver isKioskModeActive");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return true;
        //return sp.getBoolean(PREF_KIOSK_MODE, false);
    }
}
