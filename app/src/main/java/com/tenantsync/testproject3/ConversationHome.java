package com.tenantsync.testproject3;

import android.app.ActionBar;
import android.app.Activity;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConversationHome extends Activity {

    private Context context;
    private MessageAdapter messageAdapter;
    private EditText messageBodyField;
    private ListView messagesList;
    private String strDate;
    private String serial;
    private String token;
    private String messageBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging_two);
        context = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = preferences.getString("securitytoken", "n/a");
        serial = preferences.getString("serial", "n/a");
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("outstandingmessage", "viewed");
        edit.commit();
        strDate="";

        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);

        messageBodyField = (EditText) findViewById(R.id.messageBodyField);
        messageBody="";
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        getSummary();
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            if(message.startsWith("NEWMESSAGE:")) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                System.out.println("xxxdate: " + dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
                messageAdapter.addMessage(new MessageContain(message.substring(12), MessageAdapter.DIRECTION_OUTGOING, dateFormat.format(cal.getTime())));
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

    private void sendMessage() {
        messageBody = messageBodyField.getText().toString();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        java.util.Date now = new java.util.Date();
        strDate = sdfDate.format(now);
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }
        if(messageBody.equals("End Screen Pinning 12345!!")) {
            //stopLockTask();
            return;
        }
        messageBodyField.setText("");
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                MySQLConnect.API_SEND_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Response is: " + response.toString());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        System.out.println("xxxdate: " + dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
                        messageAdapter.addMessage(new MessageContain(messageBody, MessageAdapter.DIRECTION_INCOMING, dateFormat.format(cal.getTime())));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("That didn't work!");
                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                System.out.println("putting: " + messageBody);
                System.out.println("update_key: " + strDate);
                params.put("message", messageBody);
                params.put("update_key",strDate);

                return params;
            }

            @Override
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

    private void getSummary() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.GET,
                MySQLConnect.API_SEND_MESSAGE,
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
            JSONArray json = new JSONArray(incomingSummary);
            System.out.println("xxjsonObject: " + json.toString());
            System.out.println("xxjson size: " + json.length());
            for(int i=0;i<json.length();i++) {
                JSONObject jsonMessageObject = json.getJSONObject(i);
                System.out.println("jsonMArray: " + jsonMessageObject.toString());
                String messageBody="";
                if(!jsonMessageObject.isNull("body")) {
                    messageBody=jsonMessageObject.getString("body");
                }

                String year = "";
                String month = "";
                String day = "";
                String hour = "";
                String minute = "";
                String created_at="";
                if(!jsonMessageObject.isNull("created_at")) {
                    created_at =  jsonMessageObject.getString("created_at");
                    System.out.println("xxxcreated_at: " + created_at);
                    year = created_at.substring(0, 4);
                    month = created_at.substring(5,7);
                    day = created_at.substring(8,10);
                    hour = created_at.substring(11,13);
                    minute = created_at.substring(14,16);
                }

                if(!jsonMessageObject.isNull("from_device")) {
                    System.out.println("xxxfrom_device: " + jsonMessageObject.getInt("from_device"));
                    if (jsonMessageObject.getInt("from_device")== 0) {
                        messageAdapter.addMessage(new MessageContain(messageBody, MessageAdapter.DIRECTION_OUTGOING, created_at));
                    } else {
                        messageAdapter.addMessage(new MessageContain(messageBody, MessageAdapter.DIRECTION_INCOMING, created_at));
                    }
                }
                else {
                    messageAdapter.addMessage(new MessageContain(messageBody, MessageAdapter.DIRECTION_OUTGOING, created_at));
                }
            }
        }
        catch (Exception e) {
            System.out.println("exception in json");
            e.printStackTrace();
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