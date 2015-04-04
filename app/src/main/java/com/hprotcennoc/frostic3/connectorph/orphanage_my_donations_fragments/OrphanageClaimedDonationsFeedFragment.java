package com.hprotcennoc.frostic3.connectorph.orphanage_my_donations_fragments;

import android.support.v4.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class OrphanageClaimedDonationsFeedFragment extends ListFragment{

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> donationsList;

    // url to get all products list
    private static String url_feed_claimed_donation = "http://connectorph.byethost7.com/connectorph_php/orphanage_my_donation_feed.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SELECTED_DONATIONS = "SelectedDonationsFeed";
    private static final String TAG_CLAIM_CODE = "claim_code";
    private static final String TAG_DONATIONID = "donationid";
    private static final String TAG_DONORNAME = "donorName";
    private static final String TAG_CREATED_AT = "created_at";
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

    private static String tag = "MyClaimedDonations";
    // products JSONArray
    JSONArray donationsArray = null;
    ListView lv;
    TextView did;
    static int currentSelection;
    private ActionMode.Callback modeCallBack;

    View rootView;
    String demail;

    public OrphanageClaimedDonationsFeedFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_orphanage_claimed_donation_feed, container, false);

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
        lv = getListView();
        //Contextual Action Bar STARTS here
        modeCallBack = new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu){
                return false;
            }
            public void onDestroyActionMode(ActionMode mode) {
                mode = null;
            }
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle("Options");
                mode.getMenuInflater().inflate(R.menu.menu_actionbar_orphanage_claimed_donation, menu);
                return true;
            }
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                did = (TextView) lv.getChildAt(currentSelection).findViewById(R.id.LT_donationid);
                switch (id) {
                    case R.id.good: {
                        Log.i("OrphanageClaimedDonationFeedFragment", "action_good");
                        tag = "markAsDelivered";
                        new LoadAllProducts().execute();
                        mode.finish();
                        return true;
                    }
                    default:
                        return false;
                }
            }
        };
        //Contextual Action Bar ENDS here

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick (AdapterView parent, View view, int position, long id) {
                System.out.println("Long click");
                currentSelection = position; //Get position of the item
                getActivity().startActionMode(modeCallBack); //Call CAB on long click
                view.setSelected(true);
                return true;
            }
        });
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        int flag=0;
        boolean flagForRecreate = false;
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
            Log.d("OrphanageClaimedDonationsFeedFragment Email/Tag: ", params.toString());
            JSONObject json;

            if(tag.equals("markAsDelivered")){
                params.add(new BasicNameValuePair(TAG_DONATIONID, did.getText().toString()));
                params.add(new BasicNameValuePair("tag",tag));
                json = JSONParser.makeHttpRequest(url_feed_claimed_donation, "POST", params);
                tag = "MyClaimedDonations";
                Log.d("Mark Donation as Delivered: ", json.toString());
                flagForRecreate = true;
                return null;
            }

            params.add(new BasicNameValuePair("tag",tag));
            Log.d("OrphanageClaimedDonationsFeedFragment Email/Tag: ", params.toString());
            // getting JSON string from URL
            json = JSONParser.makeHttpRequest(url_feed_claimed_donation, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("All Donations: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                donationsList.clear();

                if (success == 1) {
                    // products found
                    // Getting Array of Donations
                    donationsArray = json.getJSONArray(TAG_SELECTED_DONATIONS);

                    // looping through All Donations
                    for (int i = 0; i < donationsArray.length(); i++) {
                        JSONObject c = donationsArray.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.optString(TAG_DONATIONID);
                        String claim_code = c.optString(TAG_CLAIM_CODE);
                        String created_at = c.optString(TAG_CREATED_AT);
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
                        String donorName = c.optString(TAG_DONORNAME);


                        // Converting timestamp into x ago format
                        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                                Long.parseLong(created_at) * 1000,
                                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                        CharSequence timeAgoClaim = DateUtils.getRelativeTimeSpanString(
                                Long.parseLong(claimed_at) * 1000,
                                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_DONATIONID, id);
                        map.put(TAG_CLAIM_CODE, claim_code);
                        map.put(TAG_CREATED_AT, timeAgo.toString());
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
                        map.put(TAG_DONORNAME, donorName);

                        // adding HashList to ArrayList
                        donationsList.add(map);
                    }
                } else {
                    // no Donations found
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
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(flag == 1) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
            if(flagForRecreate)
                getActivity().recreate();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                     ListAdapter adapter = new SimpleAdapter( getActivity(), donationsList, R.layout.list_item_claimed_donation_feed,
                            new String[] { TAG_DONATIONID, TAG_CATEGORY, TAG_SUB_CATEGORY, TAG_DESC, TAG_NUM_OF_ITEMS, TAG_PHONE_NUMBER, TAG_CLAIM_CODE,
                            TAG_ADDRESS_LINE_1, TAG_ADDRESS_LINE_2, TAG_CITY, TAG_STATE, TAG_CLAIMED_AT, TAG_DONORNAME},
                            new int[] { R.id.LT_donationid, R.id.LT_category, R.id.LT_subCategory, R.id.LT_description, R.id.LT_numOfItems,
                                    R.id.LT_phoneNumber, R.id.LT_claimCode, R.id.LT_addressLine1, R.id.LT_addressLine2, R.id.LT_city, R.id.LT_state,
                                    R.id.LT_timestamp, R.id.LT_donorName });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }

}
