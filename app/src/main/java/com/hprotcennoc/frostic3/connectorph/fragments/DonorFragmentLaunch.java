package com.hprotcennoc.frostic3.connectorph.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.R;
import com.hprotcennoc.frostic3.connectorph.ResetPassword;
import com.hprotcennoc.frostic3.connectorph.UserHome;
import com.hprotcennoc.frostic3.connectorph.library.JSONParser;
import com.hprotcennoc.frostic3.connectorph.registration_forms.UserRegForm;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DonorFragmentLaunch extends android.support.v4.app.Fragment{

    //DATABASE STARTS HERE
    // Progress Dialog
    private ProgressDialog pDialog;
    private static String url_login_user = "http://192.168.0.102/connectorph_php/login_user.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static String tag;
    private static final String login_tag = "login";
    private static final String forgotPassword_tag = "forgotPassword";
    //DATABASE CONTINUES LATER

    View rootView;
    Button btnlogin;
    Button btnreg;
    TextView TVforgotpass;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.launch_view_donor, container, false);

        btnlogin = (Button) rootView.findViewById(R.id.lv_donor_login_button);
        btnreg = (Button) rootView.findViewById(R.id.lv_donor_register_button);
        TVforgotpass = (TextView) rootView.findViewById(R.id.lv_donor_forgot_password_TV);
        //Login Listener
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // get user details from email and password in background thread
                tag=login_tag;
                new getUserByEmailAndPassword().execute();
            }
        });
        //Register Listener
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent UserRegFormIntent = new Intent(getActivity(), UserRegForm.class);
                startActivity(UserRegFormIntent);
            }
        });
        TVforgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tag=forgotPassword_tag;
                // get email from user and check if it exists in database
                new getUserByEmailAndPassword().execute();
            }
        });

        return rootView;
    }

    //DATABASE CONTINUES HERE
    /**
     * Background Async Task to getUserByEmailAndPassword
     * */
    class getUserByEmailAndPassword extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        int flag=0;
        String message;
        EditText email;
        EditText password;
        @Override
        protected void onPreExecute() {
            Log.i("DonorFragmentLaunch", "In onPreExecute");
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            if(tag.equals(login_tag))
                pDialog.setMessage("Logging in..");
            else
                pDialog.setMessage("We are resetting your password..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Sending email and password for verification
         * */
        protected String doInBackground(String... args) {
            Log.i("DonorFragmentLaunch","In doInBackground");

            email = (EditText) rootView.findViewById(R.id.view_donor_email_ET);
            password = (EditText) rootView.findViewById(R.id.view_donor_password_ET);
            String demail = email.getText().toString();
            String dpassword = password.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("tag", tag));
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair("password", dpassword));

            Log.i("DonorFragmentLaunch","In doInBackground1");
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest(url_login_user,
                    "POST", params);

            Log.i("DonorFragmentLaunch","In doInBackground2");

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1 && tag.equals(login_tag)) {
                    // successfully logged in
                    Intent UserProfileIntent = new Intent(getActivity(), UserHome.class);
                    UserProfileIntent.putExtra("email", demail);
                    startActivity(UserProfileIntent);
                }
                else if (success == 1 && tag.equals(forgotPassword_tag)) {
                    flag=0;
                    // Email found. Reset password mail sent. Go to page to enter code.
                    Intent ForgotPasswordIntent = new Intent(getActivity(), ResetPassword.class);
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

}
