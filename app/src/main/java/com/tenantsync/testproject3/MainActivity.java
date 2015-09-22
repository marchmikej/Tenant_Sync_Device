package com.tenantsync.testproject3;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Context context;
    private MySQLConnect mySQL;
    private String serial;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("In onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);
        context=this;
        mySQL = new MySQLConnect();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("securitytoken", "n/a");
        if(token.equals("n/a"))
        {
            System.out.println("No security token!");
            Intent intent = new Intent(this, EnterToken.class);
            startActivity(intent);
        }
        else {
            System.out.println("securitytoken is: " + token);
        }
        serial = preferences.getString("serial", "n/a");
        if(serial.equals("n/a"))
        {
            System.out.println("No serial!");
            SharedPreferences.Editor edit = preferences.edit();
            serial=android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            edit.putString("serial", serial);
            edit.commit();
        }
        else {
            System.out.println("serial is: " + serial);
        }

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("In onResume");
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("refresh"));
        getSummary();
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            getSummary();
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    //@Override
    //protected void onNewIntent(Intent intent) {
    //    super.onNewIntent(intent);
    //    String message = intent.getStringExtra("message");
    //    System.out.println("Intent message:" + message);
    //}

    private void getSummary() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.GET,
                mySQL.getApiSummary(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response is: " + response.toString());
                        handleSummary(response.toString());
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
                params.put("token", token);
                params.put("serial", serial);
                return params;
            };
        };
        queue.add(myReq);
    }

    private void handleSummary(String incomingSummary) {
        System.out.println("Incoming Summary: " + incomingSummary);
        try {
            JSONObject json = new JSONObject(incomingSummary);
            System.out.println("jsonObject: " + json.toString());
            System.out.println("json size: " + json.length());
            Iterator<String> keys = json.keys();
            int numberOfTickets = 0;
            int numberOfMessages = 0;
            while(keys.hasNext()){
                String key = keys.next();
                System.out.println("Key " + key);
                if(!json.isNull(key)) {
                    System.out.println("1111111");
                    if(key.equals("status")) {
                        json.getString(key);
                        System.out.println("status_id is: " + json.getString(key));
                    }
                    if(key.equals("messages")) {
                        JSONObject jsonTempArray = json.getJSONObject(key);
                        System.out.println("Temparray " + key + ": " + jsonTempArray.toString());
                        Iterator<String> messageKeys = jsonTempArray.keys();
                        while(messageKeys.hasNext()) {
                            numberOfMessages++;
                            String messageKey = messageKeys.next();
                            System.out.println("messageKey: " + messageKey);
                        }
                    }
                    if(key.equals("tickets")) {
                        JSONObject jsonTempArray = json.getJSONObject(key);
                        System.out.println("Temparray " + key + ": " + jsonTempArray.toString());
                        Iterator<String> messageKeys = jsonTempArray.keys();
                        while(messageKeys.hasNext()) {
                            numberOfTickets++;
                            String messageKey = messageKeys.next();
                            System.out.println("ticketsKey: " + messageKey);
                        }
                    }
                    if(key.equals("alarm_id")) {
                        json.getString(key);
                        System.out.println("alarm_id is: " + json.getString(key));
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                        if(json.getInt(key)==0) {
                            setContentView(R.layout.activity_main);
                        } else if(json.getInt(key)>0) {
                            setContentView(R.layout.activity_main_rentdue);
                        }
                    }
                }
            }
            System.out.println("Number of tickets: " + numberOfTickets);
            System.out.println("Number of messages: " + numberOfMessages);
            /*
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
            */

        }
        catch (Exception e) {
            System.out.println("exception in json");
            e.printStackTrace();
        }
    }

    public void goToMaintenance(View view) {
        Intent intent = new Intent(this, MaintenanceHome.class);
        startActivity(intent);
    }

    public void goToContact(View view) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // Instantiate the RequestQueue.
        /*
        // This is a volley request with a POST including headers
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                "http://rootedindezign.com/api/request",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Response is: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("That didn't work!");
                    }
                }) {

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("message","Test Maintenace request");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws
                    com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("serial", "f0832247461623e1");
                params.put("token", "a4345B32");
                return params;
            };
        };
        queue.add(myReq);
        */
    }

    public void goToPayRent(View view) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_main_rentdue);
    }

    ////////////////////////////////////////////
    // This function disables the back button //
    ////////////////////////////////////////////
    //@Override
    //public void onBackPressed() {
        // This is disabling the back button for this activity.  We never want this activity to
        // close without our consent.
    //}

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            // custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_token);
            dialog.setTitle("Enter Token");

            // set the custom dialog components - text, image and button
            EditText text = (EditText) dialog.findViewById(R.id.textDialog);

            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                System.out.println("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
