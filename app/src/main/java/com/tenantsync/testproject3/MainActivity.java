package com.tenantsync.testproject3;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

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

public class MainActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);
        context=this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String securityToken = preferences.getString("securitytoken", "n/a");
        if(securityToken.equals("n/a"))
        {
            System.out.println("No security token!");
            Intent intent = new Intent(this, EnterToken.class);
            startActivity(intent);
        }
        else {
            System.out.println("securitytoken is: " + securityToken);
        }
    }

    public void goToMaintenance(View view) {
        Intent intent = new Intent(this, MaintenanceHome.class);
        startActivity(intent);
    }

    public void goToContact(View view) {
        // Instantiate the RequestQueue.
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
    }

    public void goToPayRent(View view) {
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
                        System.out.println("That didn't work!");
                    }
                }) {

            public Map<String, String> getHeaders() throws
                    com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("serial", "f0832247461623e1");
                params.put("token", "a4345B32");
                return params;
            };
        };
        queue.add(myReq);
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
}
