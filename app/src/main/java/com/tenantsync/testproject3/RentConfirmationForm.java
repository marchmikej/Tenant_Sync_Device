package com.tenantsync.testproject3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Iterator;

public class RentConfirmationForm extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_rent_confirmation_form);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Intent intent = getIntent();
        String incomingData = intent.getStringExtra(MySQLConnect.SEND_RENT_CONFIRMATION);

        displayConfirmation(incomingData);
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

    public void goToDeviceData(View view) {
        Intent intent = new Intent(this, DisplayDevice.class);
        startActivity(intent);
    }

    public void goback(View view) {
        finish();
    }

    public void displayConfirmation(String displayData) {
        TextView confirmation = (TextView) findViewById(R.id.confirmation);
        String errorText = "OK";
        String resultText = "";
        String refnumText = "";
        String outputText = "";
        if(displayData.equals(MySQLConnect.RENT_CONFIRMATION_ERROR)) {
            confirmation.setText("There was an error processing the request.");
        } else {
            try {
                JSONObject json = new JSONObject(displayData);
                System.out.println("xxxjsonObject: " + json.toString());
                if(!json.isNull("Result")) {
                    System.out.println("xxxResult is:" + json.get("Result"));
                    if(json.get("Result").equals("Error")) {
                        if(!json.isNull("Error")) {
                            System.out.println("xxxError is:" + json.get("Error"));
                            outputText = "Error: " + json.get("Error") + "\n" + outputText;
                        }
                    } else if(json.get("Result").equals("Approved")) {
                        outputText = "Payment Accepted!  Thank you!\n" + outputText;
                    }
                }
                if(!json.isNull("RefNum")) {
                    System.out.println("xxxRefNum is:" + json.get("RefNum"));
                    outputText = "Reference Number: " + json.get("RefNum") + "\n" + outputText;
                }
                if(!json.isNull("Error")) {
                    System.out.println("xxxError is:" + json.get("Error"));
                }
            }
            catch (Exception e) {
                System.out.println("exception in jsonkey");
                e.printStackTrace();
                finish();
            }
            confirmation.setText(outputText);
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
