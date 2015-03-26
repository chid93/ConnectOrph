package com.hprotcennoc.frostic3.connectorph.user_my_donations_activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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

public class UserUnclaimedDonationsFeedFragment extends ListFragment implements AdapterView.OnItemSelectedListener{

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> donationsList;

    // url to get all products list
    private static String url_feed_user_my_donation = "http://192.168.0.101/connectorph_php/user_my_donation_feed.php";
    private static String url_edit_delete_donation = "http://192.168.0.101/connectorph_php/edit_delete_donation.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_CLAIMED_DONATIONS = "claimedDonations";
    private static final String TAG_DONATIONID = "donationid";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_SUB_CATEGORY = "subCategory";
    private static final String TAG_DESC = "description";
    private static final String TAG_PHONE_NUMBER = "phoneNumber";
    private static final String TAG_NUM_OF_ITEMS = "numberOfItems";
    private static final String TAG_ADDRESS_LINE_1 = "caddress1";
    private static final String TAG_ADDRESS_LINE_2 = "caddress2";
    private static final String TAG_STATE = "cstate";
    private static final String TAG_CITY = "ccity";
    private static String tag = "MyUnclaimedDonations";
    // products JSONArray
    JSONArray donationsArray = null;
    ListView lv;
    ListAdapter adapter;
    View rootView, currentItem;
    TextView did, TVitemCount, TVdesc, TVaddress1, TVaddress2, TVphoneNumber, TVstate, TVcity, TVcategory, TVsubCategory;
    static int currentSelection;
    private static String demail;
    Spinner city, state, categories, subCategories;
    EditText itemCount, desc, address1, address2, phoneNumber;
    ImageButton save_button;
    ArrayAdapter<CharSequence> stateAdapter, categoriesAdapter;
    static boolean flagForSpinnerCity, flagForSpinnerCategory;
    private ActionMode.Callback modeCallBack;

    public UserUnclaimedDonationsFeedFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_my_un_claimed_donation_feed, container, false);

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
                mode.getMenuInflater().inflate(R.menu.menu_actionbar, menu);
                return true;
            }
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                did = (TextView) lv.getChildAt(currentSelection).findViewById(R.id.LT_donationid);
                switch (id) {
                    case R.id.delete: {
                        Log.i("UserUnclaimedDonationFeedFragment", "delete");
                        tag = "delete_donation";
                        new LoadAllProducts().execute();
                        mode.finish();
                    }
                    case R.id.edit: {
                        Log.i("UserUnclaimedDonationFeedFragment", "edit");
                        currentItem = lv.getChildAt(currentSelection);
                        TVitemCount = (TextView) currentItem.findViewById(R.id.LT_numOfItems);
                        TVdesc = (TextView) currentItem.findViewById(R.id.LT_description);
                        TVphoneNumber = (TextView) currentItem.findViewById(R.id.LT_phoneNumber);
                        TVaddress1 = (TextView) currentItem.findViewById(R.id.LT_addressLine1);
                        TVaddress2 = (TextView) currentItem.findViewById(R.id.LT_addressLine2);
                        TVstate = (TextView) currentItem.findViewById(R.id.LT_state);
                        TVcity = (TextView) currentItem.findViewById(R.id.LT_city);
                        TVcategory = (TextView) currentItem.findViewById(R.id.LT_category);
                        TVsubCategory = (TextView) currentItem.findViewById(R.id.LT_subCategory);

                        state = (Spinner) currentItem.findViewById(R.id.spinner_state);
                        city = (Spinner) currentItem.findViewById(R.id.spinner_city);
                        categories = (Spinner) currentItem.findViewById(R.id.spinner_category);
                        subCategories = (Spinner) currentItem.findViewById(R.id.spinner_subCategory);
                        itemCount = (EditText) currentItem.findViewById(R.id.ET_numOfItems);
                        desc = (EditText) currentItem.findViewById(R.id.ET_description);
                        address1 = (EditText) currentItem.findViewById(R.id.ET_addressLine1);
                        address2 = (EditText) currentItem.findViewById(R.id.ET_addressLine2);
                        phoneNumber = (EditText) currentItem.findViewById(R.id.ET_phoneNumber);
                        save_button = (ImageButton) currentItem.findViewById(R.id.save_button);

                        TVitemCount.setVisibility(View.GONE);
                        TVdesc.setVisibility(View.GONE);
                        TVphoneNumber.setVisibility(View.GONE);
                        TVaddress1.setVisibility(View.GONE);
                        TVaddress2.setVisibility(View.GONE);
                        TVstate.setVisibility(View.GONE);
                        TVcity.setVisibility(View.GONE);
                        TVcategory.setVisibility(View.GONE);
                        TVsubCategory.setVisibility(View.GONE);

                        save_button.setVisibility(View.VISIBLE);
                        itemCount.setVisibility(View.VISIBLE);
                        desc.setVisibility(View.VISIBLE);
                        phoneNumber.setVisibility(View.VISIBLE);
                        address1.setVisibility(View.VISIBLE);
                        address2.setVisibility(View.VISIBLE);
                        categories.setVisibility(View.VISIBLE);
                        subCategories.setVisibility(View.VISIBLE);
                        state.setVisibility(View.VISIBLE);
                        city.setVisibility(View.VISIBLE);

                        itemCount.setText(TVitemCount.getText().toString());
                        desc.setText(TVdesc.getText().toString());
                        phoneNumber.setText(TVphoneNumber.getText().toString());
                        address1.setText(TVaddress1.getText().toString());
                        address2.setText(TVaddress2.getText().toString());
                        flagForSpinnerCity = true;
                        flagForSpinnerCategory = true;

                        categoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.categories, android.R.layout.simple_spinner_item);
                        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categories.setAdapter(categoriesAdapter);
                        categories.setOnItemSelectedListener(UserUnclaimedDonationsFeedFragment.this);
                        categories.setSelection(categoriesAdapter.getPosition(TVcategory.getText().toString()));

                        stateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.state, android.R.layout.simple_spinner_item);
                        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        state.setAdapter(stateAdapter);
                        state.setOnItemSelectedListener(UserUnclaimedDonationsFeedFragment.this);
                        state.setSelection(stateAdapter.getPosition(TVstate.getText().toString()));

                        //Save button Listener
                        save_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                tag = "edit_donation";
                                new LoadAllProducts().execute();
                            }
                        });

                        /*save_button.setOnTouchListener(new View.OnTouchListener(){
                            @Override
                            public boolean onTouch(View arg0, MotionEvent arg1) {
                                switch (arg1.getAction()) {
                                    case MotionEvent.ACTION_DOWN: {
                                        save_button.setColorFilter(Color.BLUE);
                                        break;
                                    }
                                }
                                return true;
                            }
                        });*/

                        //Toast.makeText(getActivity(), itemCount.getText().toString(), Toast.LENGTH_LONG).show();
                        mode.finish();
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
            params.add(new BasicNameValuePair("tag",tag));
            JSONObject json;
            // getting JSON string from URL
            if(tag.equals("delete_donation")){
                params.add(new BasicNameValuePair(TAG_DONATIONID, did.getText().toString()));
                json = JSONParser.makeHttpRequest(url_edit_delete_donation, "POST", params);
                Log.d("Delete donation: ", json.toString());
                tag = "MyUnclaimedDonations";
            }
            else if(tag.equals("edit_donation")){
                params.add(new BasicNameValuePair(TAG_DONATIONID, did.getText().toString()));
                params.add(new BasicNameValuePair("desc", desc.getText().toString()));
                params.add(new BasicNameValuePair("phoneNumber", phoneNumber.getText().toString()));
                params.add(new BasicNameValuePair("itemCount", itemCount.getText().toString()));
                params.add(new BasicNameValuePair("address1", address1.getText().toString()));
                params.add(new BasicNameValuePair("address2", address2.getText().toString()));
                params.add(new BasicNameValuePair("categories", categories.getSelectedItem().toString()));
                params.add(new BasicNameValuePair("subCategories", subCategories.getSelectedItem().toString()));
                params.add(new BasicNameValuePair("state", state.getSelectedItem().toString()));
                params.add(new BasicNameValuePair("city", city.getSelectedItem().toString()));
                json = JSONParser.makeHttpRequest(url_edit_delete_donation, "POST", params);
                Log.d("Delete donation: ", json.toString());
                tag = "MyUnclaimedDonations";
            }

            params.add(new BasicNameValuePair("email", demail));
            json = JSONParser.makeHttpRequest(url_feed_user_my_donation, "POST", params);


            // Check your log cat for JSON reponse
            Log.d("All unclaimed donations: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                donationsList.clear();

                if (success == 1) {
                    // products found
                    // Getting Array of Donations
                    donationsArray = json.getJSONArray(TAG_CLAIMED_DONATIONS);

                    // looping through All Donations
                    for (int i = 0; i < donationsArray.length(); i++) {
                        JSONObject c = donationsArray.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.optString(TAG_DONATIONID);
                        //String claim_code = c.optString(TAG_CLAIM_CODE);
                        String created_at = c.optString(TAG_CREATED_AT);
                        //String claimed_at = c.optString(TAG_CLAIMED_AT);
                        String category = c.optString(TAG_CATEGORY);
                        String subCategory = c.optString(TAG_SUB_CATEGORY);
                        String desc = c.optString(TAG_DESC);
                        String phoneNumber = c.optString(TAG_PHONE_NUMBER);
                        String numOfItems = c.optString(TAG_NUM_OF_ITEMS);
                        String address1 = c.optString(TAG_ADDRESS_LINE_1);
                        String address2 = c.optString(TAG_ADDRESS_LINE_2);
                        String state = c.optString(TAG_STATE);
                        String city = c.optString(TAG_CITY);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_DONATIONID, id);
                        map.put(TAG_CREATED_AT, created_at);
                        map.put(TAG_CATEGORY, category);
                        map.put(TAG_SUB_CATEGORY, subCategory);
                        map.put(TAG_DESC, desc);
                        map.put(TAG_NUM_OF_ITEMS, numOfItems);
                        map.put(TAG_PHONE_NUMBER, phoneNumber);
                        map.put(TAG_ADDRESS_LINE_1, address1);
                        map.put(TAG_ADDRESS_LINE_2, address2);
                        map.put(TAG_STATE, state);
                        map.put(TAG_CITY, city);

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
            if(message!=null)
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    adapter = new SimpleAdapter( getActivity(), donationsList, R.layout.list_item_user_my_un_claimed_donation_feed,
                            new String[] { TAG_DONATIONID, TAG_CATEGORY, TAG_SUB_CATEGORY, TAG_DESC, TAG_NUM_OF_ITEMS, TAG_PHONE_NUMBER,
                                    TAG_ADDRESS_LINE_1, TAG_ADDRESS_LINE_2, TAG_CITY, TAG_STATE, TAG_CREATED_AT},
                            new int[] { R.id.LT_donationid, R.id.LT_category, R.id.LT_subCategory, R.id.LT_description, R.id.LT_numOfItems,
                                    R.id.LT_phoneNumber, R.id.LT_addressLine1, R.id.LT_addressLine2, R.id.LT_city, R.id.LT_state,
                                    R.id.LT_timestamp });
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
        ArrayAdapter<CharSequence> subCategoriesAdapter;

        if(parent.getId() == currentItem.findViewById(R.id.spinner_state).getId() ) {
            switch (position) {
                case 1:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityAndaman_and_Nicobar_Island, android.R.layout.simple_spinner_item);
                    break;
                case 2:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityAndhra_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 3:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityArunachal_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 4:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityAssam, android.R.layout.simple_spinner_item);
                    break;
                case 5:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityBihar, android.R.layout.simple_spinner_item);
                    break;
                case 6:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityChandigarh, android.R.layout.simple_spinner_item);
                    break;
                case 7:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityChhattisgarh, android.R.layout.simple_spinner_item);
                    break;
                case 8:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityDadra_and_Nagar_Haveli, android.R.layout.simple_spinner_item);
                    break;
                case 9:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityDelhi, android.R.layout.simple_spinner_item);
                    break;
                case 10:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityGoa, android.R.layout.simple_spinner_item);
                    break;
                case 11:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityGujarat, android.R.layout.simple_spinner_item);
                    break;
                case 12:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityHaryana, android.R.layout.simple_spinner_item);
                    break;
                case 13:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityHimachal_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 14:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityJammu_and_Kashmir, android.R.layout.simple_spinner_item);
                    break;
                case 15:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityJharkhand, android.R.layout.simple_spinner_item);
                    break;
                case 16:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityKarnataka, android.R.layout.simple_spinner_item);
                    break;
                case 17:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityKerala, android.R.layout.simple_spinner_item);
                    break;
                case 18:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityMadhya_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 19:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityMaharashtra, android.R.layout.simple_spinner_item);
                    break;
                case 20:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityManipur, android.R.layout.simple_spinner_item);
                    break;
                case 21:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityMeghalaya, android.R.layout.simple_spinner_item);
                    break;
                case 22:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityMizoram, android.R.layout.simple_spinner_item);
                    break;
                case 23:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityNagaland, android.R.layout.simple_spinner_item);
                    break;
                case 24:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityOdisha, android.R.layout.simple_spinner_item);
                    break;
                case 25:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityPuducherry, android.R.layout.simple_spinner_item);
                    break;
                case 26:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityPunjab, android.R.layout.simple_spinner_item);
                    break;
                case 27:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityRajasthan, android.R.layout.simple_spinner_item);
                    break;
                case 28:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityTamil_Nadu, android.R.layout.simple_spinner_item);
                    break;
                case 29:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityTelangana, android.R.layout.simple_spinner_item);
                    break;
                case 30:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityTripura, android.R.layout.simple_spinner_item);
                    break;
                case 31:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityUttar_Pradesh, android.R.layout.simple_spinner_item);
                    break;
                case 32:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityUttarakhand, android.R.layout.simple_spinner_item);
                    break;
                case 33:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityWest_Bengal, android.R.layout.simple_spinner_item);
                    break;
                default:
                    cityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cityDefault, android.R.layout.simple_spinner_item);
            }

            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            city.setAdapter(cityAdapter);
            if(flagForSpinnerCity) {
                Log.i("UserUnclaimedDonationsFeedFragment", "In flagForSpinnerCity " + TVcity.getText().toString());
                city.setSelection(cityAdapter.getPosition(TVcity.getText().toString()));
                flagForSpinnerCity = false;
            }
        }
        else if( parent.getId() == currentItem.findViewById(R.id.spinner_category).getId()){
            switch (position) {
                case 1:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subCategories_Apparel_and_Clothes, android.R.layout.simple_spinner_item);
                    break;
                case 2:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subCategories_Baby_Learning_Toys, android.R.layout.simple_spinner_item);
                    break;
                case 3:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subCategories_Beauty_Personal_Care, android.R.layout.simple_spinner_item);
                    break;
                case 4:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subCategories_Books_Music_and_DVDs, android.R.layout.simple_spinner_item);
                    break;
                case 5:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subCategories_Electronics, android.R.layout.simple_spinner_item);
                    break;
                case 6:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subCategories_Gourmet_Food, android.R.layout.simple_spinner_item);
                    break;
                case 7:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subCategories_household_items, android.R.layout.simple_spinner_item);
                    break;
                case 8:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subCategories_Sports_and_Fitness, android.R.layout.simple_spinner_item);
                    break;
                default:
                    subCategoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subCategoriesDefault, android.R.layout.simple_spinner_item);
            }
            subCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategories.setAdapter(subCategoriesAdapter);
            if(flagForSpinnerCategory) {
                Log.i("UserUnclaimedDonationsFeedFragment", "In flagForSpinnerCategory " + TVsubCategory.getText().toString());
                subCategories.setSelection(subCategoriesAdapter.getPosition(TVsubCategory.getText().toString()));
                flagForSpinnerCategory = false;
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    //STATE/CITY DATA LOADER ENDS HERE
}
