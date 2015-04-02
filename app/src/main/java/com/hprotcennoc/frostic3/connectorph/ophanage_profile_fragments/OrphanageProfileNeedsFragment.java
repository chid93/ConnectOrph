package com.hprotcennoc.frostic3.connectorph.ophanage_profile_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
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

public class OrphanageProfileNeedsFragment extends ListFragment {

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> needsList;

    // url to get all products list
    private static String url_feed_needs = "http://connectorph.byethost24.com/connectorph_php/feed_needs.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_NEEDS = "needs";
    private static final String TAG_NEEDSID = "needsID";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "description";
    // products JSONArray
    JSONArray needsArray = null;

    View rootView;
    String demail;

    public OrphanageProfileNeedsFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_orphanage_profile_needs, container, false);

        demail = getActivity().getIntent().getStringExtra("email");
        // Hashmap for ListView
        needsList = new ArrayList<>();
        // Loading products in Background Thread
        new LoadAllProducts().execute();

        /*
        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new View.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String donationid = ((TextView) view.findViewById(R.id.donationid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getActivity(), EditProductActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_DONATIONID, donationid);
            }
        }); */

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
            // getting JSON string from URL
            JSONObject json = JSONParser.makeHttpRequest(url_feed_needs, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("All Needs: ", json.toString());
            Log.d("OrphanageNeedFeedFragment Email: ", params.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Donations
                    needsArray = json.getJSONArray(TAG_NEEDS);

                    // looping through All Donations
                    for (int i = 0; i < needsArray.length(); i++) {
                        JSONObject c = needsArray.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.optString(TAG_NEEDSID);
                        String created_at = c.optString(TAG_CREATED_AT);
                        String category = c.optString(TAG_CATEGORY);
                        String desc = c.optString(TAG_DESC);


                        // Converting timestamp into x ago format
                        //Subtract 4.5 hours to get the right time!!!
                        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                                Long.parseLong(created_at) * 1000 - 16176729,
                                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_NEEDSID, id);
                        map.put(TAG_CREATED_AT, timeAgo.toString());
                        map.put(TAG_CATEGORY, category);
                        map.put(TAG_DESC, desc);

                        // adding HashList to ArrayList
                        needsList.add(map);
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
                    ListAdapter adapter = new SimpleAdapter( getActivity(), needsList, R.layout.list_item_orphanage_needs_feed,
                            new String[] { TAG_NEEDSID, TAG_CREATED_AT, TAG_CATEGORY, TAG_DESC},
                            new int[] { R.id.LT_needsid, R.id.LT_timestamp, R.id.LT_category, R.id.LT_description});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }

}
