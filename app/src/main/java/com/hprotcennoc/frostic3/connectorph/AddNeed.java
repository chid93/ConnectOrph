package com.hprotcennoc.frostic3.connectorph;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.library.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddNeed extends ActionBarActivity{
    Spinner categories;
    EditText desc;
    Button addNeed;
    String demail;

    //DATABASE STARTS HERE
    // Progress Dialog
    private ProgressDialog pDialog;
    // url to create new product
    private static String url_new_need = "http://connectorph.byethost11.com/connectorph_php/add_need.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    //DATABASE CONTINUES LATER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_needs);

        demail = getIntent().getStringExtra("email");
        categories = (Spinner) findViewById(R.id.need_add_categories_spinner);
        desc = (EditText) findViewById(R.id.need_add_desc_ET);
        addNeed = (Button) findViewById(R.id.need_add_buttonbar);

        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(categoriesAdapter);

        addNeed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new AddNewNeed().execute();
            }
        });
    }

    class AddNewNeed extends AsyncTask<String, String, String> {

        int flag = 0;
        String message;
        //Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddNeed.this);
            pDialog.setMessage("Adding to your list..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        //submitting need
        protected String doInBackground(String... args) {
            String ddesc = desc.getText().toString();
            String dcategories = categories.getSelectedItem().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair("desc", ddesc));
            params.add(new BasicNameValuePair("categories", dcategories));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest(url_new_need,
                    "POST", params);

            // check log cat from response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    // successfully submitted donation
                    Intent data = getIntent();
                    data.putExtra("choice", "YES");
                    AddNeed.this.setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    // failed to create user
                    Log.d("Failed to Add Need", json.toString());
                    flag = 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        //  After completing background task Dismiss the progress dialog
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            Toast toast;
            if (flag == 1) {
                toast = Toast.makeText(AddNeed.this, message, Toast.LENGTH_LONG);
                toast.show();
            } else {
                toast = Toast.makeText(AddNeed.this, message, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
    //Handle Up button as Back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }
}
