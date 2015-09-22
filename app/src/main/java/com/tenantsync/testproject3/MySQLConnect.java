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
    public static final String API_BASE= "http://rootedindezign.com/api/request";
    public static final String API_SUMMARY= "http://rootedindezign.com/api/device";
    public MySQLConnect() {
    }

    public static String getApiBase() {
        return API_BASE;
    }

    public static String getApiSummary() {
        return API_SUMMARY;
    }
}
