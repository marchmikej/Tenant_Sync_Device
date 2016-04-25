package com.tenantsync.testproject3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tenantsync.testproject3.util.SystemUiHider;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DisplayDevice extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private String token;
    private String serial;
    private String updateAPK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_device);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        updateAPK="nofilegiven.txt";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = preferences.getString("securitytoken", "n/a");
        serial = preferences.getString("serial", "n/a");
        TextView releaseView = (TextView) findViewById(R.id.releasenumber);
        releaseView.setText(MySQLConnect.RELEASE_NUMBER);
        TextView tokenView = (TextView) findViewById(R.id.token);
        tokenView.setText(token);
        TextView serialView = (TextView) findViewById(R.id.devicenumber);
        serialView.setText(serial);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    public void goback(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            System.out.println("bbbhome");
            finish();
            return true;
        }
        if (id == R.id.action_payment) {
            System.out.println("bbbpayment");
            Intent intent = new Intent(this, PayRentForm.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_contact) {
            System.out.println("bbbcontact");
            Intent intent = new Intent(this, ConversationHome.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_maintenance) {
            System.out.println("bbbmaintenance");
            Intent intent = new Intent(this, MaintenanceHome.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_info) {
            System.out.println("bbbinfo");
            Intent intent = new Intent(this, DisplayDevice.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToUpdate(View view) {
        //String url = "https://tenantsyncdev.com/images/app-debug.apk";
        //new ApkUpdateAsyncTask().execute(url);

        Button verifyButton = (Button) findViewById(R.id.verifyButton);
        verifyButton.setVisibility(View.GONE);

        verifyUpdate();

        System.out.println("yyy check for update button hit");
    }

    public void updateDevice(View view) {

        Button verifyButton = (Button) findViewById(R.id.updateDevice);
        verifyButton.setVisibility(View.GONE);

        EditText verifyText = (EditText) findViewById(R.id.verifyText);
        if(verifyText.getText().toString().equals(updateAPK)) {
            String url = MySQLConnect.URL_BASE + "/images/" + updateAPK;
            new ApkUpdateAsyncTask().execute(url);
        } else {
            Toast.makeText(getApplicationContext(),"Invalid Verification",Toast.LENGTH_LONG).show();
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
        }
    }

    private void verifyUpdate() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                MySQLConnect.API_VERIFY_UPGRADE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Response is: " + response.toString());
                        if(response.toString().contains(".apk")) {
                            updateAPK=response.toString();
                            Button verifyButton = (Button) findViewById(R.id.updateDevice);
                            verifyButton.setVisibility(View.VISIBLE);
                            EditText verifyText = (EditText) findViewById(R.id.verifyText);
                            verifyText.setVisibility(View.VISIBLE);
                        } else if(response.toString().contains("NOUPDATE")) {
                            Toast.makeText(getApplicationContext(),"No Update Available",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Error Checking Update",Toast.LENGTH_LONG).show();
                        }
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
                System.out.println("version: " + MySQLConnect.RELEASE_NUMBER);
                params.put("version", MySQLConnect.RELEASE_NUMBER);

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

    public class ApkUpdateAsyncTask extends AsyncTask<String , Void, String> {

        public ApkUpdateAsyncTask(){}

        protected String doInBackground(String... urls){
/*
            String path = Environment.getExternalStorageDirectory()+"/awesomeapp.apk";
            System.out.println("yyy Original Path: " + path);
            File file = new File(context.getFilesDir(), "newapk.apk");
            System.out.println("yyy filename: " + file.toString());
            //path = file.toString();
            //String path = Environment.getDownloadCacheDirectory()+"/awesomeapp.apk";
            System.out.println("yyy trying to run update in background");
            //download the apk from your server and save to sdk card here
            try{
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                System.out.println("yyy 111");
                System.out.println("yyy: " + urls[0]);
                System.out.println("yyy: " + path);
                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(path);



                System.out.println("yyy 222");

                byte data[] = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1)
                {
                    output.write(data, 0, count);
                }

                System.out.println("yyy 333");

                output.flush();
                output.close();
                input.close();
                System.out.println("yyy closing input");
            }catch(Exception e)
            {
                System.out.println("yyy exception1");
                Log.e("YOUR_APP_LOG_TAG", "yyy I got an error", e);
                System.out.println(e.toString());
            }
            return path;
            */

            String fileName = "tmp.apk";
            try {
                FileOutputStream output = openFileOutput(fileName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                System.out.println("yyy 111");
                System.out.println("yyy: " + urls[0]);
                // download the file
                InputStream input = new BufferedInputStream(url.openStream());

                System.out.println("yyy 222");

                byte data[] = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1)
                {
                    output.write(data, 0, count);
                }

                System.out.println("yyy 333");

                output.flush();
                output.close();
                input.close();
                System.out.println("yyy closing input");
            } catch(Exception e)
            {
                System.out.println("yyy exception1");
                Log.e("YOUR_APP_LOG_TAG", "yyy I got an error", e);
                System.out.println(e.toString());
            }
// write the .apk content here ... flush() and close()

// Now start the standard instalation window
            File fileLocation = new File(context.getFilesDir(), fileName);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(fileLocation),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);

            return "whatever";
        }

        @Override
        protected void onPostExecute(String path)
        {
            /*  This is how you install through the Play Store
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("market://details?id=com.example.yourapp"));
            startActivity(goToMarket);
            */

            /*
            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(Uri.parse("file:///" + path),
                            "application/vnd.android.package-archive");
            startActivity(promptInstall);
            */


            /*
            Process process = null;
            System.out.println("yyy post execute");
            // call to superuser command, pipe install updated APK without writing over files/DB
            try
            {
                process = Runtime.getRuntime().exec("/system/xbin/  su");
                System.out.println("yyy inbetween1");
                DataOutputStream outs = new DataOutputStream(process.getOutputStream());

                System.out.println("yyy inbetween2");

                String cmd = "pm install -r "+path;

                outs.writeBytes(cmd+"\n");
                System.out.println("yyy should be over");
            }
            catch (IOException e)
            {
                System.out.println("yyy exception2");
                Log.e("YOUR_APP_LOG_TAG", "yyy I got an error", e);
                System.out.println(e.toString());
            }
            */
        }
    }
}
