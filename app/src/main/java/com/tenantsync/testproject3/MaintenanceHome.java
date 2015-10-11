package com.tenantsync.testproject3;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
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

public class MaintenanceHome extends ListActivity {

    private Context context;
    private String serial;
    private String token;
    private MaintenaceRequest[] valuesMaintenance;

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
        setContentView(R.layout.activity_maintenance_home_two);
        context=this;
        // This is how we will get the Android ID of the device
        serial=android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        // This is getting the internal security token of the device this is done at initial boot of app
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("securitytoken", "n/a");

        valuesMaintenance = new MaintenaceRequest[1];
        valuesMaintenance[0] = new MaintenaceRequest("Loading ...", "", "", "", "");
        myMaintenanceListAdapter adapter = new myMaintenanceListAdapter(this, valuesMaintenance);
        setListAdapter(adapter);

        getActiveMaintenance();
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("refresh"));
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
        finish();
    }

    public void createMaintenance(View view) {
        Intent intent = new Intent(this, CreateMaintenance.class);
        startActivity(intent);
    }

    public void goback(View view) {
        finish();
    }

    private void getActiveMaintenance() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.GET,
                MySQLConnect.API_BASE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("zzzResponse is: " + response.toString());
                        handleMaintenanceAll(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("zzzError communicating with maintenance API");
                        finish();
                    }
                }) {

            public Map<String, String> getHeaders() throws
                    com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                System.out.println("zzzserial is: '" + serial + "'");
                System.out.println("zzztoken is: '" + token + "'");
                params.put("token", token);
                params.put("serial", serial);
                return params;
            };
        };
        queue.add(myReq);
    }

    private void handleMaintenanceAll(String incomingMaintenance) {
        System.out.println("zzzIncoming maintenance: " + incomingMaintenance);
        try {
            JSONObject json = new JSONObject(incomingMaintenance);
            System.out.println("zzzjsonObject: " + json.toString());
            System.out.println("zzzjson size: " + json.length());
            valuesMaintenance = new MaintenaceRequest[json.length()];
            Iterator<String> keys = json.keys();
            int i=0;
            while(keys.hasNext()){
                String incomingResponse = "";
                String incomingRequest= "";
                String incomingStatus="";
                String incomingAppointment="";
                String incomingId="";

                String key = keys.next();
                JSONObject jsonTempArray = json.getJSONObject(key);
                System.out.println("zzztemparray " + key + ": " + jsonTempArray.toString());
                if(!jsonTempArray.isNull("request")) {
                    System.out.println("zzzrequest is: " + jsonTempArray.getString("request"));
                    incomingRequest=jsonTempArray.getString("request");
                }
                if(!jsonTempArray.isNull("response")) {
                    System.out.println("zzzresponse is: " + jsonTempArray.getString("response"));
                    incomingResponse=(jsonTempArray.getString("response"));
                }
                if(!jsonTempArray.isNull("status")) {
                    System.out.println("zzzstatus is: " + jsonTempArray.getString("status"));
                    incomingStatus=(jsonTempArray.getString("status"));
                }
                if(!jsonTempArray.isNull("appointment")) {
                    System.out.println("zzzappointment is: " + jsonTempArray.getString("appointment"));
                    incomingAppointment=(jsonTempArray.getString("appointment"));
                }
                if(!jsonTempArray.isNull("id")) {
                    System.out.println("zzzid is: " + jsonTempArray.getString("id"));
                    incomingId=(jsonTempArray.getString("id"));
                }
                valuesMaintenance[i] = new MaintenaceRequest(incomingRequest, incomingResponse, incomingStatus, incomingAppointment, incomingId);
                i++;
            }
            myMaintenanceListAdapter adapter = new myMaintenanceListAdapter(this, valuesMaintenance);
            setListAdapter(adapter);
        }
        catch (Exception e) {
            System.out.println("zzzexception in jsonkey");
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maintenance_home, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MaintenaceRequest item = (MaintenaceRequest) getListAdapter().getItem(position);
        if(!item.getRequest().equals("Loading ...")) {
            Intent intent = new Intent(this, DisplayMaintenance.class);
            intent.putExtra(MySQLConnect.DISPLAY_REQUEST, item.request);
            intent.putExtra(MySQLConnect.DISPLAY_RESPONSE, item.response);
            intent.putExtra(MySQLConnect.DISPLAY_APPTIME, item.appointment);
            intent.putExtra(MySQLConnect.DISPLAY_MAINT_STATUS, item.status);
            intent.putExtra(MySQLConnect.DISPLAY_MAINT_ID, item.id);
            startActivity(intent);
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    // This function disables dialogues which keeps the power from turning off //
    /////////////////////////////////////////////////////////////////////////////
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
