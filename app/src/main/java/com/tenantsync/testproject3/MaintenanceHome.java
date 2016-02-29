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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class MaintenanceHome extends AppCompatActivity {

    private Context context;
    private String serial;
    private String token;
    private MaintenaceRequest[] valuesMaintenance;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_home_three);
        context=this;
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        // This is how we will get the Android ID of the device
        serial=android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        // This is getting the internal security token of the device this is done at initial boot of app
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("securitytoken", "n/a");
/*
        This showed loading... but didn't look good with new layout
        valuesMaintenance = new MaintenaceRequest[1];
        valuesMaintenance[0] = new MaintenaceRequest("Loading ...", "", "", "", "");
        myMaintenanceListAdapter adapter = new myMaintenanceListAdapter(this, valuesMaintenance);
        setListAdapter(adapter);
*/
        getActiveMaintenance();
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String incomingMessage = intent.getStringExtra("message");
            System.out.println("xxxReceived GCM message: " + incomingMessage);
            if(incomingMessage.startsWith("NEWMESSAGE:")) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("outstandingmessage", "NOTVIEWED");
                edit.commit();
            }
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

    public void maintenanceHome(View view) {
        Intent intent = new Intent(this, MaintenanceHome.class);
        startActivity(intent);
        finish();
    }

    public void createMaintenance(View view) {
        Intent intent = new Intent(this, CreateMaintenance.class);
        startActivity(intent);
        finish();
    }

    public void goToContact(View view) {
        Intent intent = new Intent(this, ConversationHome.class);
        startActivity(intent);
        finish();
    }

    public void schedulePayment(View view) {
        Intent intent = new Intent(this, PayRentForm.class);
        startActivity(intent);
        finish();
    }

    public void goback(View view) {
        finish();
    }

    public void accept(View view) {

        //final int position = listview1.getPositionForView((View) view.getParent());
        Toast.makeText(getApplicationContext(),"You have selected accept: ",Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
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
        // This for when you have no outstanding requests
        String dataCheck = incomingMaintenance.substring(2, 47);
        System.out.println("zzzdatacheck: " + dataCheck);
        if(dataCheck.contains("There are no active requests for this device.")) {
            //valuesMaintenance[0] = new MaintenaceRequest("No Outstanding Requests", "", "", "", "");
            //myMaintenanceListAdapter adapter = new myMaintenanceListAdapter(this, valuesMaintenance);
            //setListAdapter(adapter);
            return;
        }

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
            System.out.println("aaa6");
            myMaintenanceListAdapter adapter = new myMaintenanceListAdapter(this, valuesMaintenance);
            System.out.println("aaa7");
            ListView mainListView = (ListView) findViewById(R.id.list_maintenance);
            System.out.println("aaa8");
            mainListView.setAdapter(adapter);
            System.out.println("aaa9");
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
/*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MaintenaceRequest item = (MaintenaceRequest) getListAdapter().getItem(position);
        if(!item.getRequest().equals("Loading ...") && !item.getRequest().equals("No Outstanding Requests")) {
            Intent intent = new Intent(this, DisplayMaintenance.class);
            intent.putExtra(MySQLConnect.DISPLAY_REQUEST, item.request);
            intent.putExtra(MySQLConnect.DISPLAY_RESPONSE, item.response);
            intent.putExtra(MySQLConnect.DISPLAY_APPTIME, item.appointment);
            intent.putExtra(MySQLConnect.DISPLAY_MAINT_STATUS, item.status);
            intent.putExtra(MySQLConnect.DISPLAY_MAINT_ID, item.id);
            startActivity(intent);
        }
    }
*/

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
