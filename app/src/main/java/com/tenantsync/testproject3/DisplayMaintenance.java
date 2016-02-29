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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class DisplayMaintenance extends AppCompatActivity {
    private Context context;
    private String serial;
    private String token;
    private String id;
    private String statusUpdate;
    private Toolbar toolbar;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        context = this;
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
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            System.out.println("bbbhome");
            finish();
            return true;
        }
        if (id == R.id.action_payment) {
            System.out.println("bbbpayment");
            Intent intent = new Intent(this, PayRentForm.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_contact) {
            System.out.println("bbbcontact");
            Intent intent = new Intent(this, ConversationHome.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_maintenance) {
            System.out.println("bbbmaintenance");
            Intent intent = new Intent(this, MaintenanceHome.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_info) {
            System.out.println("bbbinfo");
            Intent intent = new Intent(this, DisplayDevice.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        }
    }
}