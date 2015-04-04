package com.hprotcennoc.frostic3.connectorph.fragments;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

public class OrphanageDonateFeedFragment extends ListFragment {

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> donationsList;

    // url to get all products list
    private static String url_feed_donation = "http://connectorph.byethost7.com/connectorph_php/feed_donation.php";
    private static String url_claim_donation = "http://connectorph.byethost7.com/connectorph_php/claim_donation.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_DONATIONS = "donations";
    private static final String TAG_DONATIONID = "donationid";
    private static final String TAG_SMS_BODY = "body";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_SUB_CATEGORY = "subCategory";
    private static final String TAG_DESC = "description";
    private static final String TAG_NUM_OF_ITEMS = "numberOfItems";
    private static final String TAG_PHONE_NUMBER = "phoneNumber";
    private static final String TAG_STATE = "cstate";
    private static final String TAG_CITY = "ccity";
    // products JSONArray
    JSONArray donationsArray = null;

    View rootView;
    Button claimADontaion;
    String demail, phoneNumber, smsBody;
    CheckBox cb;
    ListView lv;
    TextView donationid;
    int MAX_SMS_MESSAGE_LENGTH = 160;
    static int selectedIndex;

    public OrphanageDonateFeedFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_orphanage_donate_feed, container, false);

        demail = getActivity().getIntent().getStringExtra("email");
        // Hashmap for ListView
        donationsList = new ArrayList<>();
        selectedIndex = -1;
        // Loading products in Background Thread
        new LoadAllProducts().execute();

        claimADontaion = (Button) rootView.findViewById(R.id.fragment_orphanage_donate_feed_buttonbar);
        claimADontaion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(selectedIndex == -1){
                    Toast toast = Toast.makeText(getActivity(), "No items checked!!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    new claimDonation().execute();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get listview
        lv = getListView();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Get View's (List item) id and Change flag status of checkbox..
        cb = (CheckBox) v.findViewById(R.id.LT_checkbox);
        if(selectedIndex == -1){
            //No items selected goes here
            cb.setChecked(!cb.isChecked());
            selectedIndex = position;
            donationid=(TextView) v.findViewById(R.id.LT_donationid);
            //Log.d("OrphDonateFeed IF", "Selected Index: " + selectedIndex + "Position =" + position + "donation id text: " + donationid.getText().toString());
        }
        else if(selectedIndex == position){
            //When the item already selected is clicked goes here
            cb.setChecked(!cb.isChecked());
            selectedIndex = -1;
            //Log.d("OrphDonateFeed ELSE", "Selected Index: " + selectedIndex + "Position =" + position + "donation id text: " + donationid.getText().toString());
        }
        /*
        else{
            Toast toast = Toast.makeText(getActivity(), "Multiple items cannot be claimed at once", Toast.LENGTH_SHORT);
            toast.show();
        }
        */
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

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
            // getting JSON string from URL
            JSONObject json = JSONParser.makeHttpRequest(url_feed_donation, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Donations: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Donations
                    donationsArray = json.getJSONArray(TAG_DONATIONS);

                    // looping through All Donations
                    for (int i = 0; i < donationsArray.length(); i++) {
                        JSONObject c = donationsArray.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.optString(TAG_DONATIONID);
                        String created_at = c.optString(TAG_CREATED_AT);
                        String category = c.optString(TAG_CATEGORY);
                        String subCategory = c.optString(TAG_SUB_CATEGORY);
                        String desc = c.optString(TAG_DESC);
                        String numOfItems = c.optString(TAG_NUM_OF_ITEMS);
                        String state = c.optString(TAG_STATE);
                        String city = c.optString(TAG_CITY) + ", ";

                        // Converting timestamp into x ago format
                        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                                Long.parseLong(created_at) * 1000,
                                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_DONATIONID, id);
                        map.put(TAG_CREATED_AT, timeAgo.toString());
                        map.put(TAG_CATEGORY, category);
                        map.put(TAG_SUB_CATEGORY, subCategory);
                        map.put(TAG_DESC, desc);
                        map.put(TAG_NUM_OF_ITEMS, numOfItems);
                        map.put(TAG_STATE, state);
                        map.put(TAG_CITY, city);

                        // adding HashList to ArrayList
                        donationsList.add(map);
                    }
                } else {
                    // no Donations found
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
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                     ListAdapter adapter = new SimpleAdapter( getActivity(), donationsList, R.layout.list_item_orphanage_donation_feed,
                            new String[] { TAG_DONATIONID, TAG_CREATED_AT, TAG_CATEGORY, TAG_SUB_CATEGORY, TAG_DESC, TAG_NUM_OF_ITEMS, TAG_CITY, TAG_STATE},
                            new int[] { R.id.LT_donationid, R.id.LT_timestamp, R.id.LT_category, R.id.LT_subCategory, R.id.LT_description, R.id.LT_numOfItems,
                                    R.id.LT_city, R.id.LT_state });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }

    /**
     * Background Async Task to claim Donation by making HTTP Request
     * */
    class claimDonation extends AsyncTask<String, String, String> {

        int flag = 0;
        boolean flagForRecreate = false;
        String message;

        //Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Claiming donation..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        //submitting donation
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", demail));
            params.add(new BasicNameValuePair(TAG_DONATIONID, donationid.getText().toString()));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest(url_claim_donation, "POST", params);
            // check log cat from response
            Log.d("Create Response", json.toString());
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
                if (success == 1) {
                    // successfully claimed donation. View Claimed Donations
                    phoneNumber = json.getString(TAG_PHONE_NUMBER);
                    smsBody = json.getString(TAG_SMS_BODY);
                    flagForRecreate = true;
                } else {
                    // failed to create user
                    Log.d("Failed to claim donation", json.toString());
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
            if(flagForRecreate)
                getActivity().recreate();
            if (flag == 1) {
                toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                toast.show();
            } else {
                //SMS send start
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    if(smsBody.length() > MAX_SMS_MESSAGE_LENGTH)
                    {
                        ArrayList<String> messagelist = smsManager.divideMessage(smsBody);

                        smsManager.sendMultipartTextMessage(phoneNumber, null, messagelist, null, null);
                    }
                    else
                    {
                        smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null);
                    }
                    //smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null);
                    Toast.makeText(getActivity(), "SMS Sent to the donor!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                // SMS send Close
                toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                toast.show();
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + phoneNumber));
                sendIntent.putExtra("sms_body", "Our representative will come collect your donation by [Specify Date]");
                startActivity(sendIntent);
            }
        }

    }

}
