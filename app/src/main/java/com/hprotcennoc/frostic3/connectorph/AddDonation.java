package com.hprotcennoc.frostic3.connectorph;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.library.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddDonation extends ActionBarActivity implements AdapterView.OnItemSelectedListener{


    Spinner city, state, categories, subCategories;
    EditText itemCount;
    EditText desc;
    EditText address1;
    EditText address2;
    Button submitDonation;
    String demail;

    //DATABASE STARTS HERE
    // Progress Dialog
    private ProgressDialog pDialog;

    // url to create new product
    private static String url_new_donation = "http://192.168.0.100/connectorph_php/add_donation.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    //DATABASE CONTINUES LATER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donation_add);

        demail = getIntent().getStringExtra("email");
        state = (Spinner) findViewById(R.id.donation_add_state_spinner);
        city = (Spinner) findViewById(R.id.donation_add_city_spinner);
        categories = (Spinner) findViewById(R.id.donation_add_categories_spinner);
        subCategories = (Spinner) findViewById(R.id.donation_add_subCategories_spinner);
        itemCount = (EditText) findViewById(R.id.donation_add_no_of_items_ET);
        desc = (EditText) findViewById(R.id.donation_add_desc_ET);
        address1 = (EditText) findViewById(R.id.donation_add_street_address_ET);
        address2 = (EditText) findViewById(R.id.donation_add_street_address_line_2_ET);
        submitDonation = (Button) findViewById(R.id.donation_add_buttonbar);

        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(categoriesAdapter);
        categories.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.state, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(stateAdapter);
        state.setOnItemSelectedListener(this);

        submitDonation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new AddNewDonation().execute();
            }
        });
    }

    class AddNewDonation extends AsyncTask<String, String, String> {

        int flag = 0;
        String message;


        //Before starting background thread Show Progress Dialog

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddDonation.this);
            pDialog.setMessage("Submitting your donation..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        //submitting donation

        protected String doInBackground(String... args) {
            String ddesc = desc.getText().toString();
            String dcount = itemCount.getText().toString();
            String daddress1 = address1.getText().toString();
            String daddress2 = address2.getText().toString();
            String dcategories = categories.getSelectedItem().toString();
            String dsubCategories = subCategories.getSelectedItem().toString();
            String dstate = state.getSelectedItem().toString();
            String dcity = city.getSelectedItem().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair("desc", ddesc));
            params.add(new BasicNameValuePair("itemCount", dcount));
            params.add(new BasicNameValuePair("address1", daddress1));
            params.add(new BasicNameValuePair("address2", daddress2));
            params.add(new BasicNameValuePair("categories", dcategories));
            params.add(new BasicNameValuePair("subCategories", dsubCategories));
            params.add(new BasicNameValuePair("state", dstate));
            params.add(new BasicNameValuePair("city", dcity));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest(url_new_donation,
                    "POST", params);

            // check log cat from response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    // successfully submitted donation
                    finish();
                } else {
                    // failed to create user
                    Log.d("Failed to submit donation", json.toString());
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
                toast = Toast.makeText(AddDonation.this, message, Toast.LENGTH_LONG);
                toast.show();
            } else {
                toast = Toast.makeText(AddDonation.this, message, Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }
    //state/city loader
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayAdapter<CharSequence> cityAdapter;
        ArrayAdapter<CharSequence> subCategoriesAdapter;

        if(parent.getId() == R.id.donation_add_state_spinner) {

            switch (position) {
                case 1:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityAndaman_and_Nicobar_Island, android.R.layout.simple_spinner_item);
                    break;
                case 2:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityAndhra_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 3:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityArunachal_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 4:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityAssam, android.R.layout.simple_spinner_item);
                    break;
                case 5:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityBihar, android.R.layout.simple_spinner_item);
                    break;
                case 6:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityChandigarh, android.R.layout.simple_spinner_item);
                    break;
                case 7:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityChhattisgarh, android.R.layout.simple_spinner_item);
                    break;
                case 8:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityDadra_and_Nagar_Haveli, android.R.layout.simple_spinner_item);
                    break;
                case 9:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityDelhi, android.R.layout.simple_spinner_item);
                    break;
                case 10:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityGoa, android.R.layout.simple_spinner_item);
                    break;
                case 11:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityGujarat, android.R.layout.simple_spinner_item);
                    break;
                case 12:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityHaryana, android.R.layout.simple_spinner_item);
                    break;
                case 13:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityHimachal_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 14:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityJammu_and_Kashmir, android.R.layout.simple_spinner_item);
                    break;
                case 15:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityJharkhand, android.R.layout.simple_spinner_item);
                    break;
                case 16:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityKarnataka, android.R.layout.simple_spinner_item);
                    break;
                case 17:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityKerala, android.R.layout.simple_spinner_item);
                    break;
                case 18:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMadhya_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 19:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMaharashtra, android.R.layout.simple_spinner_item);
                    break;
                case 20:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityManipur, android.R.layout.simple_spinner_item);
                    break;
                case 21:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMeghalaya, android.R.layout.simple_spinner_item);
                    break;
                case 22:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMizoram, android.R.layout.simple_spinner_item);
                    break;
                case 23:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityNagaland, android.R.layout.simple_spinner_item);
                    break;
                case 24:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityOdisha, android.R.layout.simple_spinner_item);
                    break;
                case 25:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityPuducherry, android.R.layout.simple_spinner_item);
                    break;
                case 26:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityPunjab, android.R.layout.simple_spinner_item);
                    break;
                case 27:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityRajasthan, android.R.layout.simple_spinner_item);
                    break;
                case 28:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityTamil_Nadu, android.R.layout.simple_spinner_item);
                    break;
                case 29:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityTelangana, android.R.layout.simple_spinner_item);
                    break;
                case 30:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityTripura, android.R.layout.simple_spinner_item);
                    break;
                case 31:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityUttar_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 32:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityUttarakhand, android.R.layout.simple_spinner_item);
                    break;
                case 33:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityWest_Bengal, android.R.layout.simple_spinner_item);
                    break;
                default:
                    cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityDefault, android.R.layout.simple_spinner_item);
            }
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            city.setAdapter(cityAdapter);
        }

        else if( parent.getId() == R.id.donation_add_categories_spinner){
            switch (position) {
                case 1:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategories_Apparel_and_Clothes, android.R.layout.simple_spinner_item);
                    break;
                case 2:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategories_Baby_Learning_Toys, android.R.layout.simple_spinner_item);
                    break;
                case 3:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategories_Beauty_Personal_Care, android.R.layout.simple_spinner_item);
                    break;
                case 4:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategories_Books_Music_and_DVDs, android.R.layout.simple_spinner_item);
                    break;
                case 5:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategories_Electronics, android.R.layout.simple_spinner_item);
                    break;
                case 6:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategories_Gourmet_Food, android.R.layout.simple_spinner_item);
                    break;
                case 7:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategories_household_items, android.R.layout.simple_spinner_item);
                    break;
                case 8:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategories_Sports_and_Fitness, android.R.layout.simple_spinner_item);
                    break;
                default:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategoriesDefault, android.R.layout.simple_spinner_item);
            }
            subCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategories.setAdapter(subCategoriesAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
