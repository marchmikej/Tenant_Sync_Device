package com.tenantsync.testproject3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dad on 9/27/2015.
 */
public class myMaintenanceListAdapter extends ArrayAdapter<MaintenaceRequest> {
    private final Context context;
    private final MaintenaceRequest[] values;
    private String serial;
    private String token;

    public myMaintenanceListAdapter(Context context, MaintenaceRequest[] values) {
        super(context, R.layout.maintenance_list_display, values);
        this.context = context;
        this.values = values;
        serial=android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        // This is getting the internal security token of the device this is done at initial boot of app
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = preferences.getString("securitytoken", "n/a");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=convertView;
        if(values[position].status.equals("scheduled")) {
            rowView = inflater.inflate(R.layout.maintenance_list_green, parent, false);
            final Button rescheduleButton = (Button)rowView.findViewById(R.id.reschedule);

            rescheduleButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    values[position].status="reschedule";
                    notifyDataSetChanged();
                    RequestQueue queue = Volley.newRequestQueue(context);
                    String url=MySQLConnect.API_SEND_MAINT_UPDATE + "/" + values[position].id;
                    StringRequest myReq = new StringRequest(Request.Method.POST,
                            url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    System.out.println("bbbResponse is: " + response.toString());
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("bbbThat didn't work!");
                                    System.out.println("bbbError: " + error.getMessage());
                                    Toast.makeText(context,"Network Error",Toast.LENGTH_LONG).show();
                                }
                            }) {

                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("status", values[position].status);

                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws
                                com.android.volley.AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("token", token);
                            params.put("serial", serial);
                            return params;
                        };
                    };
                    queue.add(myReq);
                }
            });
        } else {
            if(values[position].appointment.equals("Jan 1/1970 @12:00am")|| values[position].status.equals("rejected")) {
                values[position].appointment="Jan 1/1970 @12:00am";
                rowView = inflater.inflate(R.layout.maintenance_list_nobuttons_red, parent, false);
            } else {
                rowView = inflater.inflate(R.layout.maintenance_list_display, parent, false);
                final Button acceptButton = (Button)rowView.findViewById(R.id.accept);

                acceptButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //do something
                        values[position].status="scheduled";
                        notifyDataSetChanged();

                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url=MySQLConnect.API_SEND_MAINT_UPDATE + "/" + values[position].id;
                        StringRequest myReq = new StringRequest(Request.Method.POST,
                                url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("bbbThat didn't work!");
                                        System.out.println("bbbError: " + error.getMessage());
                                        Toast.makeText(context,"Network Error",Toast.LENGTH_LONG).show();
                                    }
                                }) {

                            @Override
                            protected Map<String,String> getParams(){
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("status", values[position].status);

                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws
                                    com.android.volley.AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("token", token);
                                params.put("serial", serial);
                                return params;
                            };
                        };
                        queue.add(myReq);
                    }
                });

                final Button rejectButton = (Button)rowView.findViewById(R.id.reject);

                rejectButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        values[position].status="rejected";
                        notifyDataSetChanged();

                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url=MySQLConnect.API_SEND_MAINT_UPDATE + "/" + values[position].id;
                        StringRequest myReq = new StringRequest(Request.Method.POST,
                                url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.
                                        System.out.println("bbbResponse is: " + response.toString());
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("bbbThat didn't work!");
                                        System.out.println("bbbError: " + error.getMessage());
                                        Toast.makeText(context,"Network Error",Toast.LENGTH_LONG).show();
                                    }
                                }) {

                            @Override
                            protected Map<String,String> getParams(){
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("status", values[position].status);

                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws
                                    com.android.volley.AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("token", token);
                                params.put("serial", serial);
                                return params;
                            };
                        };
                        queue.add(myReq);
                    }
                });
            }
        }
        TextView requestView = (TextView) rowView.findViewById(R.id.request);
        TextView responseView = (TextView) rowView.findViewById(R.id.response);
        TextView datetimeView = (TextView) rowView.findViewById(R.id.datetime);
        requestView.setText(values[position].getRequest());
        responseView.setText(values[position].getResponse());
        if(!values[position].appointment.equals("Jan 1/1970 @12:00am")) {
            datetimeView.setText(values[position].appointment);
        }
        return rowView;
    }
}

