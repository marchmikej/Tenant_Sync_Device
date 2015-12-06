package com.tenantsync.testproject3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dad on 9/17/2015.
 */
public class MySQLConnect {
    public static final String API_BASE= "http://159.203.115.137/api/maintenance";
    public static final String API_SEND_MAINT_UPDATE= "http://159.203.115.137/api/maintenance";
    public static final String API_SUMMARY= "http://159.203.115.137/api/device";
    public static final String API_SEND_MESSAGE= "http://159.203.115.137/api/message";
    public static final String API_SEND_RENT= "http://159.203.115.137/api/pay";
    public static final String DISPLAY_REQUEST= "display_request";
    public static final String DISPLAY_RESPONSE= "display_response";
    public static final String DISPLAY_APPTIME= "display_apptime";
    public static final String DISPLAY_MAINT_STATUS= "display_maintenance_status";
    public static final String DISPLAY_MAINT_ID= "display_maintenance_id";
    public static final String SEND_RENT_CONFIRMATION= "send_rent_data";
    public static final String RENT_CONFIRMATION_ERROR= "rent_payment_error";


    public MySQLConnect() {
    }

    public static String getApiBase() {
        return API_BASE;
    }

    public static String getApiSummary() {
        return API_SUMMARY;
    }
}
