package com.hprotcennoc.frostic3.connectorph;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener{

    private ViewPager tabsviewPager;
    int chooseTab = 0;

    //DATABASE STARTS HERE
    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // url to create new product
    private static String url_login_user = "http://192.168.0.101/connectorph_php/login_user.php";
    private static String url_login_orph = "http://192.168.0.101/connectorph_php/login_orph.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    //DATABASE CONTINUES LATER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabsviewPager = (ViewPager) findViewById(R.id.tabspager);
        TabsAdapterLaunch mTabsAdapter = new TabsAdapterLaunch(getSupportFragmentManager());

        tabsviewPager.setAdapter(mTabsAdapter);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab donorstab = getSupportActionBar().newTab().setText("Donor").setTabListener(this);
        ActionBar.Tab orphanagetab = getSupportActionBar().newTab().setText("Orphanage").setTabListener(this);

        getSupportActionBar().addTab(donorstab);
        getSupportActionBar().addTab(orphanagetab);

        //This helps in providing swiping effect for v7 compat library
        tabsviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public void onTabReselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab selectedtab, FragmentTransaction arg1) {
        tabsviewPager.setCurrentItem(selectedtab.getPosition()); //update tab position on tap
    }

    @Override
    public void onTabUnselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
    }

    public void callOrphRegForm(View view) {
        Intent OrphRegFormIntent = new Intent(this, OrphRegForm.class);
        startActivity(OrphRegFormIntent);
    }

    public void callUserRegForm(View view) {
        Intent UserRegFormIntent = new Intent(this, UserRegForm.class);
        startActivity(UserRegFormIntent);
    }

    public void callUserProfile(View view) {
        chooseTab = 1;
        new NetCheck().execute();
    }

    public void callOrphProfile(View view) {
        chooseTab = 2;
        new NetCheck().execute();
    }

    //NET CHECK STARTS HERE
    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                if(chooseTab == 1){
                    // get user details from email and password in background thread
                    new getUserByEmailAndPassword().execute();
                }
                else if (chooseTab == 2){
                    // get orph details from email and password in background thread
                    new getOrphByEmailAndPassword().execute();
                }
            }
            else{
                nDialog.dismiss();
                Toast toast = Toast.makeText(MainActivity.this, "Error in Network Connection", Toast.LENGTH_LONG);
                toast.show();

            }
        }
    }
    //NET CHECK ENDS HERE

    //DATABASE CONTINUES HERE
    /**
     * Background Async Task to getUserByEmailAndPassword
     * */
    class getUserByEmailAndPassword extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */

        int flag=0;
         @Override
        protected void onPreExecute() {
             Log.i("MainActivity1","In onPreExecute");
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Logging in..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Sending email and password for verification
         * */
        protected String doInBackground(String... args) {
            Log.i("MainActivity1","In doInBackground");

            EditText email = (EditText) findViewById(R.id.view_donor_email_ET);
            EditText password = (EditText) findViewById(R.id.view_donor_password_ET);
            String demail = email.getText().toString();
            String dpassword = password.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair("password", dpassword));

            Log.i("MainActivity1","In doInBackground1");
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest(url_login_user,
                    "POST", params);

            Log.i("MainActivity1","In doInBackground2");
            // check log cat from response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully logged in
                    Intent UserProfileIntent = new Intent(MainActivity.this, UserProfile.class);
                    startActivity(UserProfileIntent);

                    // closing this screen
                    finish();
                } else {
                    // failed to login
                    Log.d("Failed to Login User", json.toString());
                    flag=1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if(flag == 1) {
                Toast toast = Toast.makeText(MainActivity.this, "Incorrect username/password", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    /**
     * Background Async Task to getOrphByEmailAndPassword
     * */
    class getOrphByEmailAndPassword extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */

        int flag=0;
        @Override
        protected void onPreExecute() {
            Log.i("MainActivity1","In onPreExecute");
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Logging in..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Sending email and password for verification
         * */
        protected String doInBackground(String... args) {
            Log.i("MainActivity1","In doInBackground");

            EditText email = (EditText) findViewById(R.id.view_orphanage_orph_id_ET);
            EditText password = (EditText) findViewById(R.id.view_orphanage_password_ET);
            String demail = email.getText().toString();
            String dpassword = password.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair("password", dpassword));

            Log.i("MainActivity1","In doInBackground1");
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest(url_login_orph,
                    "POST", params);

            Log.i("MainActivity1","In doInBackground2");
            // check log cat from response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully logged in
                    Intent UserProfileIntent = new Intent(MainActivity.this, OrphProfile.class);
                    startActivity(UserProfileIntent);

                    // closing this screen
                    finish();
                } else {
                    // failed to login
                    Log.d("Failed to Login Orphanage", json.toString());
                    flag=1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if(flag == 1) {
                Toast toast = Toast.makeText(MainActivity.this, "Incorrect username/password", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }
    //DATABASE ENDS HERE
}
