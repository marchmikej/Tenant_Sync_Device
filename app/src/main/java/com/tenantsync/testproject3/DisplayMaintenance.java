package com.tenantsync.testproject3;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

public class DisplayMaintenance extends Activity {
    private Context context;
    private String serial;
    private String token;
    private String id;
    private String statusUpdate;

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
        statusUpdate="";
        Intent intent = getIntent();
        String request = intent.getStringExtra(MySQLConnect.DISPLAY_REQUEST);
        String response = intent.getStringExtra(MySQLConnect.DISPLAY_RESPONSE);
        String apptime = intent.getStringExtra(MySQLConnect.DISPLAY_APPTIME);
        String mainStatus = intent.getStringExtra(MySQLConnect.DISPLAY_MAINT_STATUS);
        System.out.println("bbbMAINT_STATUS is: " + mainStatus);
        // HIDES REJECT AND ACCEPT BUTTONS IF THEY ARE NOT NEEDED
        if(!mainStatus.equals("open")) {
            System.out.println("bbbButtons not needed.");
            setContentView(R.layout.activity_display_maintenance_nobutton);
        } else {
            setContentView(R.layout.activity_display_maintenance_two);
        }

        id = intent.getStringExtra(MySQLConnect.DISPLAY_MAINT_ID);
        TextView requestView = (TextView) findViewById(R.id.request);
        requestView.setText(request);
        TextView responseView = (TextView) findViewById(R.id.response);
        responseView.setText(response);
        TextView apptimeView = (TextView) findViewById(R.id.apptime);
        apptimeView.setText(apptime);
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

    public void goback(View view) {
        finish();
    }

    public void accept(View view) {
        Button accept = (Button) findViewById(R.id.accept);
        accept.setVisibility(view.GONE);
        Button reject = (Button) findViewById(R.id.reject);
        reject.setVisibility(view.GONE);
        statusUpdate="scheduled";
        sendRequest();
    }

    public void reject(View view) {
        Button accept = (Button) findViewById(R.id.accept);
        accept.setVisibility(view.GONE);
        Button reject = (Button) findViewById(R.id.reject);
        reject.setVisibility(view.GONE);
        statusUpdate="rejected";
        sendRequest();
    }

    private void sendRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url=MySQLConnect.API_SEND_MAINT_UPDATE + "/" + id;
        System.out.println("bbburl: " + url);
        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("bbbResponse is: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("bbbThat didn't work!");
                        System.out.println("bbbError: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                System.out.println("bbbputting: " + statusUpdate);
                params.put("status",statusUpdate);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws
                    com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                System.out.println("bbbserial is: '" + serial + "'");
                System.out.println("bbbtoken is: '" + token + "'");
                params.put("token", token);
                params.put("serial", serial);
                return params;
            };
        };
        queue.add(myReq);
    }

    private void getActiveMaintenance() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.GET,
                MySQLConnect.API_BASE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("bbbResponse is: " + response.toString());
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("bbbError communicating with maintenance API");
                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }) {

            public Map<String, String> getHeaders() throws
                    com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                System.out.println("bbbserial is: '" + serial + "'");
                System.out.println("bbbtoken is: '" + token + "'");
                params.put("token", token);
                params.put("serial", serial);
                return params;
            };
        };
        queue.add(myReq);
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