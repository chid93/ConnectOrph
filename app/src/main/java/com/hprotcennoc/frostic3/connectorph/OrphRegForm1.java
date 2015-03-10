package com.hprotcennoc.frostic3.connectorph;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrphRegForm1 extends ActionBarActivity implements AdapterView.OnItemSelectedListener{

    String name;
    String email;
    String password;
    EditText website;
    EditText mission;
    EditText phoneNumber;
    EditText address1;
    EditText address2;
    Spinner state;
    Spinner city;
    Button btnreg;

    //DATABASE STARTS HERE
    // Progress Dialog
    private ProgressDialog pDialog;

    // url to create new product
    private static String url_new_orph = "http://192.168.0.102/connectorph_php/new_orph.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    //DATABASE CONTINUES LATER

    // Validation starts here
    public boolean clientSideCheck(EditText view) {
        if(view.length() == 0) {
            view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
            view.setError("Required");
            return false;
        }
        if(view == findViewById(R.id.fr_orph_1_phone_number_ET)) {
            String phoneNumber = view.getText().toString();
            if (phoneNumber.length() < 8) {
                view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                view.setError("Invalid Phone Number");
                return false;
            }
        }
        view.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
        view.setError(null);
        return true;
    }
    public boolean clientSideCheck(Spinner view) {
        View selectedView = view.getSelectedView();
        TextView selectedTextView = (TextView) selectedView;
        if (selectedTextView.getText().toString().equals("[State]") || selectedTextView.getText().toString().equals("[City]")) {
            String errorString = "Required";
            selectedTextView.setError(errorString);
            return false;
        }
        selectedTextView.setError(null);
        return true;
    }
    private View.OnFocusChangeListener mOnFocusChangeListener
            = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            //Get the view called and store it as an edit text
            EditText view = (EditText) v;
            if(!hasFocus){
                //Check for Null value
                if(view.length() == 0) {
                    view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                    view.setError("Required");
                }
                //Check for Valid Phone Number
                else if(v == findViewById(R.id.fr_orph_1_phone_number_ET)){
                    String phoneNumber = ((EditText) v).getText().toString();
                    if(phoneNumber.length()<8){
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                        view.setError("Invalid Phone Number");
                    }
                    else{
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
                        view.setError(null);
                    }
                }
                //Clear error
                else{
                    view.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
                    view.setError(null);
                }
            }
        }
    };
    // Validation ends here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_reg_orph_1);
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        mission = (EditText) findViewById(R.id.fr_orph_1_mission_ET);
        website = (EditText) findViewById(R.id.fr_orph_1_website_ET);
        address1 = (EditText) findViewById(R.id.fr_orph_1_street_address_ET);
        address2 = (EditText) findViewById(R.id.fr_orph_1_street_address_line_2_ET);
        phoneNumber = (EditText) findViewById(R.id.fr_orph_1_phone_number_ET);
        state = (Spinner) findViewById(R.id.fr_orph_1_state_spinner);
        city = (Spinner) findViewById(R.id.fr_orph_1_city_spinner);

        address1.setOnFocusChangeListener(mOnFocusChangeListener);
        phoneNumber.setOnFocusChangeListener(mOnFocusChangeListener);

        //Populate state spinner using an adapter
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.state, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(stateAdapter);
        //Create a onItemSelectedListener for dynamically adding cities after states has been selected
        state.setOnItemSelectedListener(this);

        // Create button
        btnreg = (Button) findViewById(R.id.fr_orph_1_buttonbar);

        // button click event
        btnreg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if( clientSideCheck(address1) && clientSideCheck(phoneNumber) && clientSideCheck(state) && clientSideCheck(city)) {
                    // creating new product in background thread
                    new CreateNewProduct().execute();
                    Intent i = new Intent(OrphRegForm1.this, MainActivity.class);
                    startActivity(i);
                }
                else{
                    clientSideCheck(address1);
                    clientSideCheck(phoneNumber);
                    clientSideCheck(state);
                    clientSideCheck(city);
                }
            }

        });
    }
    //DATABASE CONTINUES HERE
    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, String> {

        int flag = 0;
        String message;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OrphRegForm1.this);
            pDialog.setMessage("Registering New User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String dname = name;
            String demail = email;
            String dpassword = password;
            String dwebsite = website.getText().toString();
            String dmission = mission.getText().toString();
            String dphoneNumber = phoneNumber.getText().toString();
            String daddress1 = address1.getText().toString();
            String daddress2 = address2.getText().toString();
            String dstate = state.getSelectedItem().toString();
            String dcity = city.getSelectedItem().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", dname));
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair("password", dpassword));
            params.add(new BasicNameValuePair("mission", dmission));
            params.add(new BasicNameValuePair("website", dwebsite));
            params.add(new BasicNameValuePair("phoneNumber", dphoneNumber));
            params.add(new BasicNameValuePair("address1", daddress1));
            params.add(new BasicNameValuePair("address2", daddress2));
            params.add(new BasicNameValuePair("state", dstate));
            params.add(new BasicNameValuePair("city", dcity));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest(url_new_orph,
                    "POST", params);

            // check log cat from response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    // successfully created a user
                    // closing this screen. Back to MainActivity
                    finish();
                } else {
                    // failed to create user
                    Log.d("failed to create new orphanage", json.toString());
                    flag = 1;

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
            Toast toast;
            if(flag == 1) {
                toast = Toast.makeText(OrphRegForm1.this, message, Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                toast = Toast.makeText(OrphRegForm1.this, "Successfully Registered", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }
    //DATABASE ENDS HERE

    //STATE/CITY DATA LOADER STARTS HERE
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayAdapter<CharSequence> cityAdapter;

        switch (position){
            case 1: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityAndaman_and_Nicobar_Island, android.R.layout.simple_spinner_item);
                break;
            case 2: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityAndhra_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 3: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityArunachal_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 4: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityAssam, android.R.layout.simple_spinner_item);
                break;
            case 5: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityBihar, android.R.layout.simple_spinner_item);
                break;
            case 6: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityChandigarh, android.R.layout.simple_spinner_item);
                break;
            case 7: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityChhattisgarh, android.R.layout.simple_spinner_item);
                break;
            case 8: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityDadra_and_Nagar_Haveli, android.R.layout.simple_spinner_item);
                break;
            case 9: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityDelhi, android.R.layout.simple_spinner_item);
                break;
            case 10: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityGoa, android.R.layout.simple_spinner_item);
                break;
            case 11: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityGujarat, android.R.layout.simple_spinner_item);
                break;
            case 12: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityHaryana, android.R.layout.simple_spinner_item);
                break;
            case 13: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityHimachal_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 14: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityJammu_and_Kashmir, android.R.layout.simple_spinner_item);
                break;
            case 15: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityJharkhand, android.R.layout.simple_spinner_item);
                break;
            case 16: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityKarnataka, android.R.layout.simple_spinner_item);
                break;
            case 17: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityKerala, android.R.layout.simple_spinner_item);
                break;
            case 18: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMadhya_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 19: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMaharashtra, android.R.layout.simple_spinner_item);
                break;
            case 20: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityManipur, android.R.layout.simple_spinner_item);
                break;
            case 21: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMeghalaya, android.R.layout.simple_spinner_item);
                break;
            case 22: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMizoram, android.R.layout.simple_spinner_item);
                break;
            case 23: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityNagaland, android.R.layout.simple_spinner_item);
                break;
            case 24: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityOdisha, android.R.layout.simple_spinner_item);
                break;
            case 25: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityPuducherry, android.R.layout.simple_spinner_item);
                break;
            case 26: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityPunjab, android.R.layout.simple_spinner_item);
                break;
            case 27: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityRajasthan, android.R.layout.simple_spinner_item);
                break;
            case 28: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityTamil_Nadu, android.R.layout.simple_spinner_item);
                break;
            case 29: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityTelangana, android.R.layout.simple_spinner_item);
                break;
            case 30: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityTripura, android.R.layout.simple_spinner_item);
                break;
            case 31: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityUttar_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 32: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityUttarakhand, android.R.layout.simple_spinner_item);
                break;
            case 33: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityWest_Bengal, android.R.layout.simple_spinner_item);
                break;
            default: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityDefault,android.R.layout.simple_spinner_item);
        }

        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
