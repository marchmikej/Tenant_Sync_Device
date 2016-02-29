package com.tenantsync.testproject3;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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
    private String serial;
    private String token;
    private MediaPlayer mPlayer;

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

        //Puts app in kiosk mode
        //provisionOwner();
        setContentView(R.layout.activity_main_three);
        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context=this;
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
        mPlayer = MediaPlayer.create(context, R.raw.tenantsyncchime2); // in 2nd param u have to pass your desire ringtone
        mPlayer.setVolume(100, 100);
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
            String incomingMessage = intent.getStringExtra("message");
            System.out.println("xxxReceived GCM message: " + incomingMessage);
            if(incomingMessage.startsWith("NEWMESSAGE:")) {
                // New Message Received
                /*
                ImageButton button = (ImageButton)findViewById(R.id.contactButton);
                button.setBackgroundColor(Color.YELLOW);
                */
                LinearLayout newMessageList = (LinearLayout)findViewById(R.id.newMessageButtonList);
                newMessageList.setVisibility(View.VISIBLE);
                LinearLayout noNewMessageList = (LinearLayout)findViewById(R.id.noNewMessageButtonList);
                noNewMessageList.setVisibility(View.GONE);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("outstandingmessage", "NOTVIEWED");
                edit.commit();
            }
            if(incomingMessage.startsWith("NEWMAINTENANCE:")) {
                // New Message Received
                /*
                ImageButton button = (ImageButton)findViewById(R.id.maintenanceButton);
                button.setBackgroundColor(Color.YELLOW);
                */
                LinearLayout maintenanceList = (LinearLayout)findViewById(R.id.maintenanceButtonList);
                maintenanceList.setVisibility(View.VISIBLE);
                LinearLayout noNewMessageList = (LinearLayout)findViewById(R.id.noNewMessageButtonList);
                noNewMessageList.setVisibility(View.GONE);
            }
            if(incomingMessage.startsWith("UNPINDEVICE:")) {
                // New Message Received
                //stopLockTask();
            }
            if(incomingMessage.startsWith("PLAYSOUND:")) {
                // Going to play chime
                System.out.println("xxxPlaying chime.");
                // send the tone to the "alarm" stream (classic beeps go there) with 100% volume
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500); // 200 is duration in ms

            }
            if(incomingMessage.startsWith("REFRESH:")) {
                // Requires a refresh on home screen
                getSummary();
            }
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    private void getSummary() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.GET,
                MySQLConnect.API_SUMMARY,
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
                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
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
            int rentStatus = 0;
            while(keys.hasNext()){
                String key = keys.next();
                System.out.println("Key " + key);
                if(!json.isNull(key)) {
                    if(key.equals("status")) {
                        json.getString(key);
                        System.out.println("mmmstatus_id is: " + json.getString(key));
                    }
                    if(key.equals("messages")) {
                        numberOfMessages=Integer.parseInt(json.getString(key));
                        /*
                        JSONObject jsonTempArray = json.getJSONObject(key);
                        System.out.println("Temparray " + key + ": " + jsonTempArray.toString());
                        Iterator<String> messageKeys = jsonTempArray.keys();
                        while(messageKeys.hasNext()) {
                            numberOfMessages++;
                            String messageKey = messageKeys.next();
                        }
                        */
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
                    if(key.equals("maintenanceRequests")) {
                        numberOfTickets=Integer.parseInt(json.getString(key));
                    }
                    if(key.equals("alarm_id")) {
                        json.getString(key);
                        System.out.println("alarm_id is: " + json.getString(key));
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                        setContentView(R.layout.activity_main_three);
                        if(json.getInt(key)==0) {
                            rentStatus=0;
                        } else if(json.getInt(key)==1) {
                            rentStatus=1;
                            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
                            mainLayout.setBackgroundResource(R.drawable.rent_due_background);
                            LinearLayout rentDue = (LinearLayout) findViewById(R.id.rentDueList);
                            rentDue.setVisibility(View.VISIBLE);
                            LinearLayout noNewMessageList = (LinearLayout) findViewById(R.id.noNewMessageButtonList);
                            noNewMessageList.setVisibility(View.GONE);
                        }
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
            System.out.println("Number of tickets: " + numberOfTickets);
            if (numberOfTickets > 0) {
                LinearLayout maintenanceList = (LinearLayout) findViewById(R.id.maintenanceButtonList);
                maintenanceList.setVisibility(View.VISIBLE);
                LinearLayout noNewMessageList = (LinearLayout) findViewById(R.id.noNewMessageButtonList);
                noNewMessageList.setVisibility(View.GONE);
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String outstandingMessages = preferences.getString("outstandingmessage", "n/a");
            if (outstandingMessages.equals("NOTVIEWED")) {
                LinearLayout newMessageList = (LinearLayout) findViewById(R.id.newMessageButtonList);
                newMessageList.setVisibility(View.VISIBLE);
                LinearLayout noNewMessageList = (LinearLayout) findViewById(R.id.noNewMessageButtonList);
                noNewMessageList.setVisibility(View.GONE);
            }
            System.out.println("Number of messages: " + numberOfMessages);
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
        Intent intent = new Intent(this, ConversationHome.class);
        startActivity(intent);
    }

    public void goToPayRent(View view) {
        //setContentView(R.layout.activity_main_rentdue);
        //stopLockTask();
        Intent intent = new Intent(this, PayRentForm.class);
        startActivity(intent);
    }

    ////////////////////////////////////////////
    // This function disables the back button //
    ////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        // This is disabling the back button for this activity.  We never want this activity to
        // close without our consent.
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
/*
     // This puts the app in kiosk mode if we are a device owner
    private void provisionOwner() {

        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdminSample = new ComponentName(this, BasicDeviceAdminReceiver.class);

        if (mDPM.isDeviceOwnerApp(this.getPackageName())) {
            System.out.println("isDeviceOwnerApp: YES");
            String[] packages = {this.getPackageName()};
            mDPM.setLockTaskPackages(mDeviceAdminSample, packages);
        } else {
            System.out.println("isDeviceOwnerApp: NO");
        }

        if (mDPM.isLockTaskPermitted(this.getPackageName())) {
            System.out.println("isLockTaskPermitted: ALLOWED");
            startLockTask();
        } else {
            System.out.println("isLockTaskPermitted: NOT ALLOWED");
        }
    }
    */
}
