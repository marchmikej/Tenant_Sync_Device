package com.tenantsync.testproject3;

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;

/**
 * Created by Dad on 10/15/2015.
 */
public class BasicDeviceAdminReceiver extends DeviceAdminReceiver {

    public static ComponentName getComponentName(Context context) {
        System.out.println("xxxWe are here");
        return new ComponentName(context.getApplicationContext(), BasicDeviceAdminReceiver.class);
    }

}

