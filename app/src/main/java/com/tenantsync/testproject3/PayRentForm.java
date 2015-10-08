package com.tenantsync.testproject3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class PayRentForm extends AppCompatActivity {

    private String card_number;
    private String exp;
    private String cvv2;
    private String card_holder;
    private Context context;
    private String serial;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_rent_form);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        System.out.println("xxxInOnCreatePayRentForm");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
            }
        });
        context = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = preferences.getString("securitytoken", "n/a");
        serial = preferences.getString("serial", "n/a");
        card_number="4000100011112224";
        exp="0919";
        cvv2="000";
        card_holder="John Doe";
        //sendMessage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void submitpayment(View view) {
        EditText card_holder_button = (EditText) findViewById(R.id.cardholder);
        card_holder = card_holder_button.getText().toString();
        EditText exp_button = (EditText) findViewById(R.id.expirationYear);
        exp = exp_button.getText().toString();
        EditText cvv2_button = (EditText) findViewById(R.id.cvv);
        cvv2 = cvv2_button.getText().toString();
        EditText card_number_button = (EditText) findViewById(R.id.creditcardnumber);
        card_number = card_number_button.getText().toString();
        sendMessage();
    }

    private void sendMessage() {
        System.out.println("xxxIn send message");
        //Send message to server here
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                MySQLConnect.API_SEND_RENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("xxxResponse is: " + response.toString());
                        openConfirmationActivity(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("xxxThat didn't work!");
                        openConfirmationActivity(MySQLConnect.RENT_CONFIRMATION_ERROR);
                    }
                }) {

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                System.out.println("xxxsending: " + exp + card_number + cvv2);
                params.put("card_number", card_number);
                params.put("exp", exp);
                params.put("cvv2",cvv2);
                params.put("card_holder",card_holder);

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

    public void openConfirmationActivity(String sendInfo) {
        Intent intent = new Intent(this, RentConfirmationForm.class);
        intent.putExtra(MySQLConnect.SEND_RENT_CONFIRMATION, sendInfo);
        startActivity(intent);
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
