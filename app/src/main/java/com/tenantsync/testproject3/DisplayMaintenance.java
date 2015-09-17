package com.tenantsync.testproject3;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

public class DisplayMaintenance extends ListActivity {
    private Context context;
    private String serial;
    private String token;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //setContentView(R.layout.activity_maintenance_main);
        //serial=android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        // This is getting the internal security token of the device this is done at initial boot of app
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //token = preferences.getString("securitytoken", "n/a");
       // String[] values = new String[]{ "Android", "iPhone", "WindowsMobile",
       //         "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
       //         "Linux", "OS/2" };
        String[] values = new String[]{"loading"};
        // use your custom layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_display_maintenance, R.id.label, values);
        setListAdapter(adapter);
        getActiveMaintenance();
    }

    private void getActiveMaintenance() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.GET,
                "http://rootedindezign.com/api/request",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response is: " + response.toString());
                        handleMaintenanceAll(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error communicating with maintenance API");
                    }
                }) {

            public Map<String, String> getHeaders() throws
                    com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                System.out.println("serial is: '" + serial + "'");
                System.out.println("token is: '" + token + "'");
                params.put("serial", "f0832247461623e1");
                params.put("token", "a4345B32");
                return params;
            };
        };
        queue.add(myReq);
    }

    private void handleMaintenanceAll(String incomingMaintenance) {
        System.out.println("Incoming maintenance: " + incomingMaintenance);
        try {
            JSONArray json = new JSONArray(incomingMaintenance);
            System.out.println("jsonArray: " + json.toString());
            String[] values = new String[json.length()+1];
            values[0]="Create New Maintenance Request";
            for(int i=0;i<json.length();i++) {
                JSONObject jsonTempArray = json.getJSONObject(i);
                System.out.println("temparray " + i + ": " + jsonTempArray.toString());
                if(jsonTempArray.has("message")) {
                    System.out.println("message is: " + jsonTempArray.getString("message"));
                    values[i+1]=jsonTempArray.getString("message");
                }
                if(jsonTempArray.has("id")) {
                    System.out.println("id is: " + jsonTempArray.getString("id"));
                }
                if(jsonTempArray.has("device_id")) {
                    System.out.println("device_id is: " + jsonTempArray.getString("device_id"));
                }
                if(jsonTempArray.has("status")) {
                    System.out.println("status is: " + jsonTempArray.getString("status"));
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.activity_display_maintenance, R.id.label, values);
            setListAdapter(adapter);
        }
        catch (Exception e) {
            System.out.println("exception in jsonkey");
            e.printStackTrace();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }
}