package com.tenantsync.testproject3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PayRentForm extends Activity {

    private String card_number;
    private String exp;
    private String cvv2;
    private String card_holder;
    private String payment_amount;
    private Context context;
    private String serial;
    private String token;
    private int firstRun;
    private String balanceDue;
    private String rentAmount;
    private String paymentType;
    private String account_number;
    private String routing_number;
    private String check_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_pay_rent_form);
        setContentView(R.layout.content_pay_rent_form_three);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        System.out.println("xxxInOnCreatePayRentForm");
        context = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = preferences.getString("securitytoken", "n/a");
        serial = preferences.getString("serial", "n/a");
        card_number="";
        exp="";
        cvv2="";
        card_holder="";
        payment_amount="";
        balanceDue="0";
        rentAmount="0";
        firstRun=0;
        paymentType="";
        account_number="";
        routing_number="";
        check_number="";
        Spinner spnr = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnr.setAdapter(adapter);
        spnr.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        // An item was selected. You can retrieve the selected item using
                        // parent.getItemAtPosition(pos)
                        String selection = parent.getItemAtPosition(pos).toString();
                        if(firstRun==0) {
                            firstRun++;
                        } else if(selection.equals("Credit")) {
                                System.out.println("whoa1");
                                //setContentView(R.layout.content_pay_rent_form_three);
                        } else if(selection.equals("Check")) {
                            System.out.println("whoa2");
                            setContentView(R.layout.content_pay_rent_form_check);
                            TextView rent_amount = (TextView)findViewById(R.id.monthlyrent);
                            rent_amount.setText("$" + rentAmount);
                            TextView amount_due = (TextView)findViewById(R.id.amountdue);
                            amount_due.setText("$" + balanceDue);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                }
        );
        rentStatus();
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

    public void submitcreditpayment(View view) {
        paymentType="card";
        Button submitButton = (Button) findViewById(R.id.submitpaymentbutton);
        submitButton.setVisibility(View.GONE);
        EditText card_holder_button = (EditText) findViewById(R.id.cardholder);
        card_holder = card_holder_button.getText().toString();
        EditText exp_month = (EditText) findViewById(R.id.expirationMonth);
        EditText exp_year = (EditText) findViewById(R.id.expirationYear);
        exp = exp_month.getText().toString() + exp_year.getText().toString();
        EditText cvv2_button = (EditText) findViewById(R.id.cvv);
        cvv2 = cvv2_button.getText().toString();
        EditText card_number_button = (EditText) findViewById(R.id.creditcardnumber);
        card_number = card_number_button.getText().toString();
        EditText payment_amount_button = (EditText) findViewById(R.id.paymentamount);
        payment_amount = payment_amount_button.getText().toString();
        int paymentProceed = 1;
        String errorText="";
        if(exp_month.getText().toString().length()!=2) {
            // Expiration month incorrect
            paymentProceed=0;
            errorText="Expiration Month Incorrect\n" + errorText;
        }
        if(exp_year.getText().toString().length()!=2) {
            // Expiration year incorrect
            paymentProceed=0;
            errorText="Expiration Year Incorrect\n" + errorText;
        }
        if(payment_amount.length()==0) {
            // Payment amount needs to be inserted
            paymentProceed=0;
            errorText="Payment Amount Incorrect\n" + errorText;
        }
        if(card_number.length()==0) {
            // Card number incorrect
            paymentProceed=0;
            errorText="Card Number Incorrect\n" + errorText;
        }
        if(card_holder.length()==0) {
            // Card holder incorrect
            paymentProceed=0;
            errorText="Card Holder Incorrect\n" + errorText;
        }
        if(paymentProceed==1) {
            sendCreditMessage();
        } else {
            System.out.println("xxxError Message: " + errorText);
            TextView error_text = (TextView) findViewById(R.id.errortext);
            error_text.setText(errorText);
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    public void submitcheckpayment(View view) {
        account_number="";
        routing_number="";
        check_number="";
        paymentType="check";
        Button submitButton = (Button) findViewById(R.id.submitpaymentbutton);
        submitButton.setVisibility(View.GONE);
        EditText card_holder_button = (EditText) findViewById(R.id.cardholder);
        card_holder = card_holder_button.getText().toString();
        EditText routing_button = (EditText) findViewById(R.id.routingnumber);
        routing_number = routing_button.getText().toString();
        EditText card_number_button = (EditText) findViewById(R.id.accountnumber);
        account_number = card_number_button.getText().toString();
        EditText payment_amount_button = (EditText) findViewById(R.id.paymentamount);
        payment_amount = payment_amount_button.getText().toString();
        EditText check_number_button = (EditText) findViewById(R.id.checknumber);
        check_number = check_number_button.getText().toString();
        sendCreditMessage();
    }

    private void sendCreditMessage() {
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
                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                        openConfirmationActivity(MySQLConnect.RENT_CONFIRMATION_ERROR);
                    }
                }) {

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                if(paymentType.equals("card")) {
                    System.out.println("xxxexpiration: " + exp);
                    System.out.println("xxxcard_number: " + card_number);
                    System.out.println("xxxcvv2: " + cvv2);
                    System.out.println("xxxcard_holder: " + card_holder);
                    System.out.println("xxxpayment_amount: " + payment_amount);

                    params.put("card_number", card_number);
                    params.put("payment_type", paymentType);
                    params.put("payment_amount", payment_amount);
                    params.put("expiration", exp);
                    params.put("cvv2", cvv2);
                    params.put("account_holder", card_holder);
                } else if(paymentType.equals("check")) {
                    System.out.println("xxxaccount_number: " + account_number);
                    System.out.println("xxxrouting_number: " + routing_number);
                    System.out.println("xxxaccount_holder: " + card_holder);
                    System.out.println("xxxpayment_amount: " + payment_amount);
                    System.out.println("xxxcheck_number: " + payment_amount);
                    params.put("payment_type", paymentType);
                    params.put("payment_amount", payment_amount);
                    params.put("routing_number", routing_number);
                    params.put("account_number", account_number);
                    params.put("account_holder", card_holder);
                } else if(paymentType.equals("transfer")) {

                }
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

    private void rentStatus() {
        System.out.println("xxxIn rent status");
        //Send message to server here
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.GET,
                MySQLConnect.API_RENT_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("xxxResponse is: " + response.toString());
                        handleRentStatus(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("xxxThat didn't work: "  + error.toString());
                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                    }
                }) {

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
        System.out.println("Sending to RentConfirmationForm: " + sendInfo);
        Intent intent = new Intent(this, RentConfirmationForm.class);
        intent.putExtra(MySQLConnect.SEND_RENT_CONFIRMATION, sendInfo);
        startActivity(intent);
        finish();
    }

    private void handleRentStatus(String incomingString) {
        try {
            JSONObject json = new JSONObject(incomingString);
            System.out.println("xxxjsonObject: " + json.toString());
            System.out.println("xxxjson size: " + json.length());
            if(!json.isNull("rent_amount")) {
                System.out.println("xxxrent_amount is: " + json.getString("rent_amount"));
                TextView rent_amount = (TextView)findViewById(R.id.monthlyrent);
                rentAmount=json.getString("rent_amount");
                rent_amount.setText("$" + rentAmount);
            }
            if(!json.isNull("balance_due")) {
                System.out.println("xxxbalance_due is: " + json.getString("balance_due"));
                TextView amount_due = (TextView)findViewById(R.id.amountdue);
                balanceDue=json.getString("balance_due");
                amount_due.setText("$" + balanceDue);
            }
        } catch (Exception e) {
            System.out.println("xxxexception in json");
            e.printStackTrace();
            finish();
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
