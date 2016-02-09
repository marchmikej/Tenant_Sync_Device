package com.tenantsync.testproject3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateMaintenance extends Activity {
    private Context context;
    private String messageBody;
    private String strDate;
    private String serial;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_create_maintenance_three);

        messageBody="";
        strDate="";
        context=this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = preferences.getString("securitytoken", "n/a");
        serial = preferences.getString("serial", "n/a");
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

    public void returnToMaintenance() {
        Intent intent = new Intent(this, MaintenanceHome.class);
        startActivity(intent);
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

    public void createMaintenance(View view) {
        EditText requestField = (EditText)findViewById(R.id.request);
        messageBody = requestField.getText().toString();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        strDate = sdfDate.format(now);
        Button maintButton = (Button) findViewById(R.id.submitNewMaintButton);
        maintButton.setVisibility(View.GONE);
        System.out.println("request: " + messageBody);
        System.out.println("request length: " + messageBody.length());
        if(messageBody.length()>0) {
            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest myReq = new StringRequest(Request.Method.POST,
                    MySQLConnect.API_BASE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            System.out.println("Response is: " + response.toString());
                            returnToMaintenance();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("That didn't work!");
                            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                            returnToMaintenance();
                        }
                    }) {

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    System.out.println("putting: " + messageBody);
                    System.out.println("update_key: " + strDate);
                    params.put("message",messageBody);
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
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Please Enter Maintenance Description.";
            maintButton.setVisibility(View.VISIBLE);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_maintenance, menu);
        return super.onCreateOptionsMenu(menu);
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
