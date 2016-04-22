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
import android.widget.TextView;

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

public class DisplayDevice extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_device);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = preferences.getString("securitytoken", "n/a");
        String serial = preferences.getString("serial", "n/a");
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
        String url = "https://tenantsyncdev.com/images/app-debug.apk";
        new ApkUpdateAsyncTask().execute(url);

        System.out.println("yyy check for update button hit");
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
