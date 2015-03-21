package com.hprotcennoc.frostic3.connectorph;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.library.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResetPassword extends ActionBarActivity{
    //DATABASE STARTS HERE
    // Progress Dialog
    private ProgressDialog pDialog;
    private static String url_login_user = "http://192.168.0.101/connectorph_php/login_user.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static String tag;
    private static final String verificationCode_tag = "verificationCode";
    private static final String resetPassword_tag = "resetPassword";
    //DATABASE CONTINUES LATER

    EditText vCode;
    EditText password;
    EditText retypePassword;
    TextView vCodeLabel;
    TextView resetPasswordLabel;
    Button btnbar;
    String demail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset);

        demail = getIntent().getStringExtra("email");
        vCodeLabel = (TextView) findViewById(R.id.password_reset_enter_code_TV);
        resetPasswordLabel = (TextView) findViewById(R.id.password_reset_label_password_TV);
        password = (EditText) findViewById(R.id.password_reset_password_ET);
        retypePassword = (EditText) findViewById(R.id.password_reset_retype_password_ET);
        btnbar = (Button) findViewById(R.id.password_reset_buttonbar);
        //Button Bar Listener
        btnbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(btnbar.getText().toString().equals("NEXT")){
                    tag=verificationCode_tag;
                    // get user details from email and password in background thread
                    new getUserByEmailAndPassword().execute();
                }
                else {
                    tag=resetPassword_tag;
                    // get user details from email and password in background thread
                    new getUserByEmailAndPassword().execute();
                }

            }
        });
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
        @Override
        protected void onPreExecute() {
            Log.i("ResetPassword", "In onPreExecute");
            super.onPreExecute();
            pDialog = new ProgressDialog(ResetPassword.this);
            if(tag.equals(verificationCode_tag))
                pDialog.setMessage("Verifying Code..");
            else
                pDialog.setMessage("Password is being changed..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Sending email and password for verification
         * */
        protected String doInBackground(String... args) {
            Log.i("DonorFragmentLaunch","In doInBackground");

            vCode=(EditText) findViewById(R.id.password_reset_enter_code_ET);
            String dvCode = vCode.getText().toString();
            String dPassword = password.getText().toString();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("tag", tag));
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair("vCode", dvCode));
            params.add(new BasicNameValuePair("password", dPassword));

            Log.i("ResetPassword","In doInBackground1");
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest(url_login_user,
                    "POST", params);

            Log.i("ResetPassword","In doInBackground2");

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    flag=0;
                } else {
                    // failed to login
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
            //If verification error occurs, display the error
            if(flag == 1) {
                Toast toast = Toast.makeText(ResetPassword.this, message, Toast.LENGTH_LONG);
                toast.show();
            }
            else if(tag.equals(verificationCode_tag)){
                Toast toast = Toast.makeText(ResetPassword.this, message, Toast.LENGTH_LONG);
                toast.show();
                vCodeLabel.setVisibility(View.GONE);
                vCode.setVisibility(View.GONE);
                resetPasswordLabel.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                retypePassword.setVisibility(View.VISIBLE);
                btnbar.setText(getResources().getString(R.string.resetPasswordButton));
                Log.i("ResetPassword vCode","In postExecute");
            }
            else{
                Toast toast = Toast.makeText(ResetPassword.this, message, Toast.LENGTH_LONG);
                toast.show();
                Log.i("ResetPassword resetPassword_tag","In postExecute");
                finish();
            }

        }

    }
    //DATABASE ENDS HERE


}
