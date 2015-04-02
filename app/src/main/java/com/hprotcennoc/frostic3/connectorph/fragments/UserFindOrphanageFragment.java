package com.hprotcennoc.frostic3.connectorph.fragments;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hprotcennoc.frostic3.connectorph.OrphanageMyProfile;
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

public class UserFindOrphanageFragment extends ListFragment implements AdapterView.OnItemSelectedListener{

    // Progress Dialog
    private ProgressDialog pDialog;

    // url to get all products list
    private static String url_orph_feed = "http://connectorph.byethost24.com/connectorph_php/feed_orphanage.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_ORPH_ARRAY = "orphanagesArray";
    private static final String TAG_ORPH_EMAIL = "orphanageEmail";
    private static final String TAG_ORPH_NAME = "orphanageName";
    private static final String TAG_ORPH_MISSION = "orphanageMission";
    private static final String TAG_ORPH_ADDRESS_LINE_1 = "orphanageAddress1";
    private static final String TAG_ORPH_ADDRESS_LINE_2 = "orphanageAddress2";
    private static final String TAG_ORPH_STATE = "orphanageState";
    private static final String TAG_ORPH_CITY = "orphanageCity";
    View rootView;
    // products JSONArray
    JSONArray orphanageArray = null;
    ArrayList<HashMap<String, String>> orphanageList;
    ListView lv;
    String tag = "notYet";
    ListAdapter adapter;
    //EditText inputSearch;
    Spinner state, city;
    Button go;

    public UserFindOrphanageFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_user_find_orphanage, container, false);
        // Hashmap for ListView
        orphanageList = new ArrayList<>();
        // Loading products in Background Thread
        new LoadAllProducts().execute();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get listview
        lv = getListView();
        //inputSearch = (EditText) getActivity().findViewById(R.id.inputSearch);
        state= (Spinner) getActivity().findViewById(R.id.state_spinner);
        city= (Spinner) getActivity().findViewById(R.id.city_spinner);
        go = (Button) getActivity().findViewById(R.id.go_button);

        // launch orphanage profile onItemClick
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent OrphanageMyProfileFragment = new Intent(getActivity(), OrphanageMyProfile.class);
                TextView email = (TextView) view.findViewById(R.id.LT_orphEmail);
                OrphanageMyProfileFragment.putExtra("email", email.getText().toString());
                startActivity(OrphanageMyProfileFragment);
            }
        });

        /*inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                UserFindOrphanageFragment.this.adapter.getFilter().filter(cs);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });*/

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(state.getSelectedItem().toString().equals("[State]"))
                    tag="Not yet";
                else
                    tag="onGo";
                new LoadAllProducts().execute();
                //adapter.notifyDataSetChanged();
            }
        });

        //State/City data loader
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.state, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        state.setAdapter(stateAdapter);
        state.setOnItemSelectedListener(UserFindOrphanageFragment.this);
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
            JSONObject json;
            if(tag.equals("onGo")){
                params.add(new BasicNameValuePair("city", city.getSelectedItem().toString()));
                params.add(new BasicNameValuePair("state", state.getSelectedItem().toString()));
                json = JSONParser.makeHttpRequest(url_orph_feed, "POST", params); // getting JSON string from URL
            }
            else
                json = JSONParser.makeHttpRequest(url_orph_feed, "GET", params); // getting JSON string from URL

            // Check your log cat for JSON reponse
            Log.d("All Orphanages: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                orphanageList.clear();
                if (success == 1) {
                    // products found
                    // Getting Array of Donations
                    orphanageArray = json.getJSONArray(TAG_ORPH_ARRAY);

                    // looping through All Donations
                    for (int i = 0; i < orphanageArray.length(); i++) {
                        JSONObject c = orphanageArray.getJSONObject(i);

                        // Storing each json item in variable
                        String email = c.optString(TAG_ORPH_EMAIL);
                        String name = c.optString(TAG_ORPH_NAME);
                        String mission = c.optString(TAG_ORPH_MISSION);
                        String address1 = c.optString(TAG_ORPH_ADDRESS_LINE_1);
                        String address2 = c.optString(TAG_ORPH_ADDRESS_LINE_2);
                        String state = c.optString(TAG_ORPH_STATE);
                        String city = c.optString(TAG_ORPH_CITY);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ORPH_EMAIL, email);
                        map.put(TAG_ORPH_NAME, name);
                        map.put(TAG_ORPH_MISSION, mission);
                        map.put(TAG_ORPH_ADDRESS_LINE_1, address1);
                        map.put(TAG_ORPH_ADDRESS_LINE_2, address2);
                        map.put(TAG_ORPH_STATE, state);
                        map.put(TAG_ORPH_CITY, city);

                        // adding HashList to ArrayList
                        orphanageList.add(map);
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
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    adapter = new SimpleAdapter( getActivity(), orphanageList, R.layout.list_item_user_find_orphanage,
                            new String[] { TAG_ORPH_EMAIL, TAG_ORPH_NAME, TAG_ORPH_MISSION, TAG_ORPH_ADDRESS_LINE_1, TAG_ORPH_ADDRESS_LINE_2,
                                    TAG_ORPH_CITY, TAG_ORPH_STATE},
                            new int[] { R.id.LT_orphEmail, R.id.orphName_TV, R.id.orphMission_TV, R.id.orphAddress1_TV, R.id.orphAddress2_TV,
                                    R.id.orphCity_TV, R.id.orphState_TV });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }

    //STATE/CITY DATA LOADER STARTS HERE
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayAdapter<CharSequence> cityAdapter;

        switch (position){
            case 1: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityAndaman_and_Nicobar_Island, android.R.layout.simple_spinner_item);
                break;
            case 2: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityAndhra_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 3: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityArunachal_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 4: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityAssam, android.R.layout.simple_spinner_item);
                break;
            case 5: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityBihar, android.R.layout.simple_spinner_item);
                break;
            case 6: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityChandigarh, android.R.layout.simple_spinner_item);
                break;
            case 7: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityChhattisgarh, android.R.layout.simple_spinner_item);
                break;
            case 8: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityDadra_and_Nagar_Haveli, android.R.layout.simple_spinner_item);
                break;
            case 9: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityDelhi, android.R.layout.simple_spinner_item);
                break;
            case 10: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityGoa, android.R.layout.simple_spinner_item);
                break;
            case 11: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityGujarat, android.R.layout.simple_spinner_item);
                break;
            case 12: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityHaryana, android.R.layout.simple_spinner_item);
                break;
            case 13: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityHimachal_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 14: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityJammu_and_Kashmir, android.R.layout.simple_spinner_item);
                break;
            case 15: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityJharkhand, android.R.layout.simple_spinner_item);
                break;
            case 16: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityKarnataka, android.R.layout.simple_spinner_item);
                break;
            case 17: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityKerala, android.R.layout.simple_spinner_item);
                break;
            case 18: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityMadhya_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 19: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityMaharashtra, android.R.layout.simple_spinner_item);
                break;
            case 20: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityManipur, android.R.layout.simple_spinner_item);
                break;
            case 21: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityMeghalaya, android.R.layout.simple_spinner_item);
                break;
            case 22: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityMizoram, android.R.layout.simple_spinner_item);
                break;
            case 23: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityNagaland, android.R.layout.simple_spinner_item);
                break;
            case 24: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityOdisha, android.R.layout.simple_spinner_item);
                break;
            case 25: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityPuducherry, android.R.layout.simple_spinner_item);
                break;
            case 26: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityPunjab, android.R.layout.simple_spinner_item);
                break;
            case 27: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityRajasthan, android.R.layout.simple_spinner_item);
                break;
            case 28: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityTamil_Nadu, android.R.layout.simple_spinner_item);
                break;
            case 29: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityTelangana, android.R.layout.simple_spinner_item);
                break;
            case 30: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityTripura, android.R.layout.simple_spinner_item);
                break;
            case 31: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityUttar_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 32: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityUttarakhand, android.R.layout.simple_spinner_item);
                break;
            case 33: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityWest_Bengal, android.R.layout.simple_spinner_item);
                break;
            default: cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityDefault,android.R.layout.simple_spinner_item);
        }

        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    //STATE/CITY DATA LOADER ENDS HERE
}
