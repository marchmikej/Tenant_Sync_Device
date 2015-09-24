package com.tenantsync.testproject3;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
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

public class ConversationHome extends Activity {

    private Context context;
    private MessageAdapter messageAdapter;
    private EditText messageBodyField;
    private ListView messagesList;
    private String serial;
    private String token;
    private MySQLConnect mySQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);

        context = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = preferences.getString("securitytoken", "n/a");
        serial = preferences.getString("serial", "n/a");
        mySQL = new MySQLConnect();

        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        populateMessageHistory();

        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        getSummary();
    }

    //get previous messages from parse & display
    private void populateMessageHistory() {
        String[] tempMessageArray = {"first message is a super huge message that just keeps going and going and going and going and going and going and going and going and going and going and then keep going and going and more and more and more and going and more and more and more and going and going and going", "second message", "third message"};
        for (int i = 0; i < tempMessageArray.length; i++) {
            /*
            if (messageList.get(i).get("senderId").toString().equals(currentUserId)) {
                messageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
            } else {
                messageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
            } */
            if (tempMessageArray[i].equals("second message")) {
                System.out.println("!!!Adding message: " + tempMessageArray[i]);
                messageAdapter.addMessage(tempMessageArray[i], MessageAdapter.DIRECTION_OUTGOING);
            } else {
                System.out.println("!!!Adding message: " + tempMessageArray[i]);
                messageAdapter.addMessage(tempMessageArray[i], MessageAdapter.DIRECTION_INCOMING);
            }
        }
    }

    private void sendMessage() {
        String messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }
        //Send message to server here
        System.out.println("Tried sending message: " + messageBody.toString());
    }

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
                    if(key.equals("messages")) {
                        JSONObject jsonTempArray = json.getJSONObject(key);
                        System.out.println("Temparray " + key + ": " + jsonTempArray.toString());
                        Iterator<String> messageKeys = jsonTempArray.keys();
                        while(messageKeys.hasNext()) {
                            numberOfMessages++;
                            String messageKey = messageKeys.next();
                            System.out.println("messageKey: " + messageKey);
                            JSONObject jsonMessageTemp = jsonTempArray.getJSONObject(messageKey);
                            System.out.println("jsonMessageTemp: " + jsonMessageTemp.toString());
                        }
                    }
                }
            }
            System.out.println("Number of tickets: " + numberOfTickets);
            System.out.println("Number of messages: " + numberOfMessages);
        }
        catch (Exception e) {
            System.out.println("exception in json");
            e.printStackTrace();
        }
    }
}