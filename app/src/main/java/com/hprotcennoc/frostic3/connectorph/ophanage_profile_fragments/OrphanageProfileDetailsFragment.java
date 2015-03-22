package com.hprotcennoc.frostic3.connectorph.ophanage_profile_fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.R;
import com.hprotcennoc.frostic3.connectorph.library.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrphanageProfileDetailsFragment extends Fragment {
    // Progress Dialog
    private ProgressDialog pDialog;

    // url to get all products list
    private static String url_orph_profile_details = "http://192.168.0.101/connectorph_php/orph_profile_details.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_ORPH_NAME = "orphanageName";
    private static final String TAG_ORPH_MISSION = "orphanageMission";
    private static final String TAG_ORPH_WEBSITE = "orphanageWebsite";
    private static final String TAG_ORPH_PHONE_NUMBER = "orphanagePhoneNumber";
    private static final String TAG_ORPH_ADDRESS_LINE_1 = "orphanageAddress1";
    private static final String TAG_ORPH_ADDRESS_LINE_2 = "orphanageAddress2";
    private static final String TAG_ORPH_STATE = "orphanageState";
    private static final String TAG_ORPH_CITY = "orphanageCity";
    String orphNameS, orphAddress1S, orphAddress2S, orphPhoneNumberS, orphCityS, orphStateS, orphMissionS, orphWebsiteS;
    TextView orphName, orphAddress1, orphAddress2, orphPhoneNumber, orphCity, orphState, orphMission, orphWebsite;
    View rootView;
    String demail;

    public OrphanageProfileDetailsFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_orphanage_profile_details, container, false);

        demail = getActivity().getIntent().getStringExtra("email");
        // Loading products in Background Thread
        new LoadAllProducts().execute();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        orphName = (TextView) getActivity().findViewById(R.id.orphName_TV);
        orphWebsite = (TextView) getActivity().findViewById(R.id.orphWebsite_TV);
        orphMission = (TextView) getActivity().findViewById(R.id.orphMission_TV);
        orphAddress1 = (TextView) getActivity().findViewById(R.id.orphAddress1_TV);
        orphAddress2 = (TextView) getActivity().findViewById(R.id.orphAddress2_TV);
        orphPhoneNumber = (TextView) getActivity().findViewById(R.id.orphPhoneNumber_TV);
        orphCity = (TextView) getActivity().findViewById(R.id.orphCity_TV);
        orphState = (TextView) getActivity().findViewById(R.id.orphState_TV);
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        int flag=0;
        String message;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", demail));
            // getting JSON string from URL
            JSONObject json = JSONParser.makeHttpRequest(url_orph_profile_details, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("Orphanage Details: ", json.toString());
            Log.d("OrphanageNeedFeedFragment Email: ", params.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    orphNameS = json.getString(TAG_ORPH_NAME);
                    orphWebsiteS = json.getString(TAG_ORPH_WEBSITE);
                    orphMissionS = json.getString(TAG_ORPH_MISSION);
                    orphPhoneNumberS = json.getString(TAG_ORPH_PHONE_NUMBER);
                    orphAddress1S = json.getString(TAG_ORPH_ADDRESS_LINE_1);
                    orphAddress2S = json.getString(TAG_ORPH_ADDRESS_LINE_2);
                    orphCityS = json.getString(TAG_ORPH_CITY);
                    orphStateS = json.getString(TAG_ORPH_STATE);
                } else {
                    // no Donations found
                    message = json.getString(TAG_MESSAGE);
                    flag=1;
                    // Launch Add New Donations Activity
                    //Intent i = new Intent(getActivity(), NewProductActivity.class);
                    // Closing all previous activities
                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivity(i);
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
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            if(flag == 1) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
            else{
                orphName.setText(orphNameS);
                orphWebsite.setText(orphWebsiteS);
                orphMission.setText(orphMissionS);
                orphAddress1.setText(orphAddress1S);
                orphAddress2.setText(orphAddress2S);
                orphCity.setText(orphCityS);
                orphState.setText(orphStateS);
                orphPhoneNumber.setText(orphPhoneNumberS);
            }
        }
    }
}