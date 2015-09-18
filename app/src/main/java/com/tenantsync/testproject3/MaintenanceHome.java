package com.tenantsync.testproject3;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

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

public class MaintenanceHome extends ListActivity {

    private Context context;
    private String serial;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_maintenance_home);
        context=this;
        // This is how we will get the Android ID of the device
        serial=android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        // This is getting the internal security token of the device this is done at initial boot of app
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("securitytoken", "n/a");
        String[] values = new String[]{"loading"};
        //ListView lv = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    public void viewMaintenance(View view) {
        System.out.println("In viewMaintenance");
        Intent intent = new Intent(this, DisplayMaintenance.class);
        startActivity(intent);
    }

    public void createMaintenance(View view) {
        // Instantiate the RequestQueue.
        // This is a volley request with a get including headers
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
                params.put("token", token);
                return params;
            };
        };
        queue.add(myReq);
        Intent intent = new Intent(this, DisplayMaintenance.class);
        startActivity(intent);
    }

    private void handleMaintenanceAll(String incomingMaintenance) {
        System.out.println("Incoming maintenance: " + incomingMaintenance);
        try {
            JSONArray json = new JSONArray(incomingMaintenance);
            System.out.println("jsonArray: " + json.toString());
            for(int i=0;i<json.length();i++) {
                JSONObject jsonTempArray = json.getJSONObject(i);
                System.out.println("temparray " + i + ": " + jsonTempArray.toString());
                if(jsonTempArray.has("message")) {
                    System.out.println("message is: " + jsonTempArray.getString("message"));
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
        }
        catch (Exception e) {
            System.out.println("exception in jsonkey");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maintenance_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
