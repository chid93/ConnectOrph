package com.hprotcennoc.frostic3.connectorph.launch_view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.OrphHome;
import com.hprotcennoc.frostic3.connectorph.R;
import com.hprotcennoc.frostic3.connectorph.ResetPassword;
import com.hprotcennoc.frostic3.connectorph.library.JSONParser;
import com.hprotcennoc.frostic3.connectorph.registration_forms.OrphRegForm;

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

public class OrphanageFragmentLaunch extends android.support.v4.app.Fragment {

    //DATABASE STARTS HERE
    // Progress Dialog
    private ProgressDialog pDialog;
    private static String url_login_orph = "http://connectorph.byethost11.com/connectorph_php/login_orph.php";
    private static String url_forgot_password = "http://connectorph.byethost11.com/connectorph_php/login_user.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String login_tag = "login";
    private static final String databaseOnFocus = "orphanages";
    private static final String forgotPassword_tag = "forgotPassword";
    //DATABASE CONTINUES LATER

    View rootView;
    private static String tag;
    Button btnlogin;
    Button btnreg;
    TextView TVforgotpass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.launch_view_orphanage, container, false);

        btnlogin = (Button) rootView.findViewById(R.id.lv_orphanage_login_button);
        btnreg = (Button) rootView.findViewById(R.id.lv_orphanage_register_button);
        TVforgotpass = (TextView) rootView.findViewById(R.id.lv_donor_forgot_password_TV);
        //Login Listener
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // get user details from email and password in background thread
                tag=login_tag;
                new NetCheck().execute();
            }
        });
        //Register Listener
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent OrphRegFormIntent = new Intent(getActivity(), OrphRegForm.class);
                startActivity(OrphRegFormIntent);
            }
        });
        TVforgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tag=forgotPassword_tag;
                new NetCheck().execute();
            }
        });

        return rootView;
    }

    //DATABASE CONTINUES HERE
    /**
     * Background Async Task to getOrphByEmailAndPassword
     * */
    class getOrphByEmailAndPassword extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        int flag=0;
        String message;
        EditText email;
        EditText password;
        @Override
        protected void onPreExecute() {
            Log.i("OrphanageFragmentLaunch", "In onPreExecute");
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Logging in..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Sending email and password for verification
         * */
        protected String doInBackground(String... args) {
            Log.i("OrphanageFragmentLaunch","In doInBackground");

            email = (EditText) rootView.findViewById(R.id.view_orphanage_orph_id_ET);
            password = (EditText) rootView.findViewById(R.id.view_orphanage_password_ET);
            String demail = email.getText().toString();
            String dpassword = password.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("tag", tag));
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair("password", dpassword));

            Log.i("OrphanageFragmentLaunch","In doInBackground1");
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json;
            if (tag.equals(login_tag)) {
                json = JSONParser.makeHttpRequest(url_login_orph,
                        "POST", params);
            }
            else{
                params.add(new BasicNameValuePair("databaseOnFocus", databaseOnFocus)); //By default, forgot password!!
                json = JSONParser.makeHttpRequest(url_forgot_password,
                        "POST", params);
            }

            Log.i("OrphanageFragmentLaunch","In doInBackground2");
            // check log cat from response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1 && tag.equals(login_tag)) {
                    flag=0;
                    // successfully logged in
                    Intent UserProfileIntent = new Intent(getActivity(), OrphHome.class);
                    UserProfileIntent.putExtra("email", demail);
                    startActivity(UserProfileIntent);

                } else if (success == 1 && tag.equals(forgotPassword_tag)) {
                    flag=0;
                    // Email found. Reset password mail sent. Go to page to enter code.
                    Intent ForgotPasswordIntent = new Intent(getActivity(), ResetPassword.class);
                    ForgotPasswordIntent.putExtra("databaseOnFocus", databaseOnFocus);
                    ForgotPasswordIntent.putExtra("email", demail);
                    startActivity(ForgotPasswordIntent);

                } else {
                    // failed to login
                    message = json.getString(TAG_MESSAGE);
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
            //Close keyboard before displaying toast for visibility concerns (Thank you Zoho Rep Mani for identifying this bug)
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            //If login error occurs, display the error
            if(flag == 1) {
                Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                //Reset Email and Password editText
                email.setText("");
                password.setText("");
            }
        }

    }
    //DATABASE ENDS HERE
    //NETCHECK STARTS HERE
    class NetCheck extends AsyncTask<String,String,Boolean> {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(getActivity());
            nDialog.setMessage("Checking Network..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        protected void onPostExecute(Boolean connectivity) {

            if (connectivity) {
                nDialog.dismiss();
                Log.i("NetCheck", "Connection Successful");
                new getOrphByEmailAndPassword().execute();
            } else {
                nDialog.dismiss();
                Log.i("NetCheck", "No connectivity");
                Toast.makeText(getActivity(), "Error in Network Connection", Toast.LENGTH_LONG).show();
            }
        }
    }
    //NETCHECK ENDS HERE
}
