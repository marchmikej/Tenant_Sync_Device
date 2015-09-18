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
import java.util.Iterator;
import java.util.Map;

public class DisplayMaintenance extends ListActivity {
    private Context context;
    private String serial;
    private String token;
    private MySQLConnect mySQL;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        context = this;
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = preferences.getString("securitytoken", "n/a");
        serial = preferences.getString("serial", "n/a");
        mySQL = new MySQLConnect();
        System.out.println("token: " + token);
        System.out.println("serial: " + serial);
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
                mySQL.getApiBase(),
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
                        finish();
                    }
                }) {

            public Map<String, String> getHeaders() throws
                    com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                System.out.println("serial is: '" + serial + "'");
                System.out.println("token is: '" + token + "'");
                params.put("token", token);
                params.put("serial", serial);
                return params;
            };
        };
        queue.add(myReq);
    }

    private void handleMaintenanceAll(String incomingMaintenance) {
        System.out.println("Incoming maintenance: " + incomingMaintenance);
        try {
            JSONObject json = new JSONObject(incomingMaintenance);
            System.out.println("jsonObject: " + json.toString());
            System.out.println("json size: " + json.length());
            String[] values = new String[json.length()+1];
            values[0]="Create New Maintenance Request";
            Iterator<String> keys = json.keys();
            int i=0;
            //for(int i=0;i<json.length();i++) {
            while(keys.hasNext()){
                i++;
                String key = keys.next();
                JSONObject jsonTempArray = json.getJSONObject(key);
                System.out.println("temparray " + key + ": " + jsonTempArray.toString());
                if(jsonTempArray.has("request")) {
                    System.out.println("request is: " + jsonTempArray.getString("request"));
                    values[i] = jsonTempArray.getString("request");
                }

                if(jsonTempArray.has("response")) {
                    System.out.println("response is: " + jsonTempArray.getString("response"));
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
            finish();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }
}