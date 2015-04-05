package com.hprotcennoc.frostic3.connectorph.user_my_donations_fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.R;
import com.hprotcennoc.frostic3.connectorph.library.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserClaimedDonationsFeedFragment extends ListFragment {

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> donationsList;

    // url to get all products list
    private static String url_feed_user_my_donation = "http://connectorph.byethost11.com/connectorph_php/user_my_donation_feed.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SELECTED_DONATIONS = "SelectedDonationsFeed";
    private static final String TAG_CLAIM_CODE = "claim_code";
    private static final String TAG_DONATIONID = "donationid";
    private static final String TAG_CLAIMED_AT = "claimed_at";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_SUB_CATEGORY = "subCategory";
    private static final String TAG_DESC = "description";
    private static final String TAG_PHONE_NUMBER = "phoneNumber";
    private static final String TAG_NUM_OF_ITEMS = "numberOfItems";
    private static final String TAG_ADDRESS_LINE_1 = "caddress1";
    private static final String TAG_ADDRESS_LINE_2 = "caddress2";
    private static final String TAG_STATE = "cstate";
    private static final String TAG_CITY = "ccity";

    private static final String TAG_ORPH_NAME = "orphanageName";
    private static final String TAG_ORPH_PHONE_NUMBER = "orphanagePhoneNumber";
    private static final String TAG_ORPH_ADDRESS_LINE_1 = "orphanageAddress1";
    private static final String TAG_ORPH_ADDRESS_LINE_2 = "orphanageAddress2";
    private static final String TAG_ORPH_STATE = "orphanageState";
    private static final String TAG_ORPH_CITY = "orphanageCity";

    private static final String tag = "MyClaimedDonations";
    // products JSONArray
    JSONArray donationsArray = null;

    View rootView;
    private static String demail;

    public UserClaimedDonationsFeedFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_my_claimed_donation_feed, container, false);

        demail = getActivity().getIntent().getStringExtra("email");
        // Hashmap for ListView
        donationsList = new ArrayList<>();
        // Loading products in Background Thread
        new LoadAllProducts().execute();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get listview
        //ListView lv = getListView();
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
            params.add(new BasicNameValuePair("tag",tag));
            // getting JSON string from URL
            JSONObject json = JSONParser.makeHttpRequest(url_feed_user_my_donation, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("All Donations: ", json.toString());
            //Log.d("Email: ", params.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Donations
                    donationsArray = json.getJSONArray(TAG_SELECTED_DONATIONS);
                    Log.i("UserClaimedDonationsFeedFragment", donationsArray.toString());

                    // looping through All Donations
                    for (int i = 0; i < donationsArray.length(); i++) {
                        JSONObject c = donationsArray.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.optString(TAG_DONATIONID);
                        String claim_code = c.optString(TAG_CLAIM_CODE);
                        String claimed_at = c.optString(TAG_CLAIMED_AT);
                        String category = c.optString(TAG_CATEGORY);
                        String subCategory = c.optString(TAG_SUB_CATEGORY);
                        String desc = c.optString(TAG_DESC);
                        String phoneNumber = c.optString(TAG_PHONE_NUMBER);
                        String numOfItems = c.optString(TAG_NUM_OF_ITEMS);
                        String address1 = c.optString(TAG_ADDRESS_LINE_1);
                        String address2 = c.optString(TAG_ADDRESS_LINE_2);
                        String state = c.optString(TAG_STATE);
                        String city = c.optString(TAG_CITY);

                        String orphName = c.optString(TAG_ORPH_NAME);
                        String orphPhoneNumber = c.optString(TAG_ORPH_PHONE_NUMBER);
                        String orphAddress1 = c.optString(TAG_ORPH_ADDRESS_LINE_1);
                        String orphAddress2 = c.optString(TAG_ORPH_ADDRESS_LINE_2);
                        String orphState = c.optString(TAG_ORPH_STATE);
                        String orphCity = c.optString(TAG_ORPH_CITY);

                        /*if(claim_code.equals("null")){
                            claim_code="NOT YET CLAIMED";
                        }*/

                        // Converting timestamp into x ago format
                        CharSequence timeAgoClaim = DateUtils.getRelativeTimeSpanString(
                                Long.parseLong(claimed_at) * 1000,
                                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_DONATIONID, id);
                        map.put(TAG_CLAIM_CODE, claim_code);
                        map.put(TAG_CLAIMED_AT, timeAgoClaim.toString());
                        map.put(TAG_CATEGORY, category);
                        map.put(TAG_SUB_CATEGORY, subCategory);
                        map.put(TAG_DESC, desc);
                        map.put(TAG_NUM_OF_ITEMS, numOfItems);
                        map.put(TAG_PHONE_NUMBER, phoneNumber);
                        map.put(TAG_ADDRESS_LINE_1, address1);
                        map.put(TAG_ADDRESS_LINE_2, address2);
                        map.put(TAG_STATE, state);
                        map.put(TAG_CITY, city);

                        map.put(TAG_ORPH_NAME, orphName);
                        map.put(TAG_ORPH_PHONE_NUMBER, orphPhoneNumber);
                        map.put(TAG_ORPH_ADDRESS_LINE_1, orphAddress1);
                        map.put(TAG_ORPH_ADDRESS_LINE_2, orphAddress2);
                        map.put(TAG_ORPH_STATE, orphState);
                        map.put(TAG_ORPH_CITY, orphCity);

                        // adding HashList to ArrayList
                        donationsList.add(map);
                    }
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

            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter( getActivity(), donationsList, R.layout.list_item_user_my_claimed_donation_feed,
                            new String[] { TAG_DONATIONID, TAG_CATEGORY, TAG_SUB_CATEGORY, TAG_DESC, TAG_NUM_OF_ITEMS, TAG_CLAIM_CODE, TAG_CLAIMED_AT,
                                    TAG_ORPH_NAME, TAG_ORPH_PHONE_NUMBER, TAG_ORPH_ADDRESS_LINE_1, TAG_ORPH_ADDRESS_LINE_2, TAG_ORPH_CITY, TAG_ORPH_STATE},
                            new int[] { R.id.LT_donationid, R.id.LT_category, R.id.LT_subCategory, R.id.LT_description, R.id.LT_numOfItems, R.id.LT_claimCode,
                                    R.id.LT_timestamp, R.id.LT_orphName, R.id.LT_orphPhoneNumber, R.id.LT_orphAddressLine1, R.id.LT_orphAddressLine2,
                                    R.id.LT_orphCity, R.id.LT_orphState});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }

}
