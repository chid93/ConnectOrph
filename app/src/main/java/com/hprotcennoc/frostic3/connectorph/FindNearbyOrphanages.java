package com.hprotcennoc.frostic3.connectorph;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.hprotcennoc.frostic3.connectorph.fragments.UserFindOrphanageFragment;
import com.hprotcennoc.frostic3.connectorph.google_places.GooglePlacesReadTask;
import com.hprotcennoc.frostic3.connectorph.library.model.NavDrawerItem;
import com.hprotcennoc.frostic3.connectorph.library.model.NavDrawerListAdapter;

import java.util.ArrayList;

public class FindNearbyOrphanages extends ActionBarActivity implements LocationListener {

    private static final String GOOGLE_API_KEY = "AIzaSyABxEhA3abVcf0BbWXzBZTKKvmclx9Ibak";
    SupportMapFragment fragmentGM;
    LocationManager locationManager;
    android.support.v4.app.FragmentManager fm;
    android.support.v4.app.FragmentTransaction ft;
    GoogleMap googleMap;
    double latitude = 0;
    double longitude = 0;
    private int PROXIMITY_RADIUS = 10000;

    //NAVIGATION DRAWER STARTS HERE
    private static String demail;
    private static int positionFromIntent;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    //NAVIGATION DRAWER CONTINUES LATER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(FindNearbyOrphanages.this, "Google Play Services is not available!!", Toast.LENGTH_LONG).show();
            finish();
        }
        setContentView(R.layout.orphanages_find);
        positionFromIntent = getIntent().getIntExtra("position",2);

        // enabling action bar app icon and behaving it as toggle button
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        //NAVIGATION DRAWER CONTINUES HERE
        demail = getIntent().getStringExtra("email");
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_user);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<>();

        // adding nav drawer items to array
        // Donation Feed
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        // My Donations
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        // Orphanages Near You
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        // Find Orphanages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //ON DRAWER TOGGLE, DO WHATEVER YOU WISH TO DO, HERE!
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                //getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //END DRAWER TOGGLE

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            mDrawerList.setItemChecked(positionFromIntent, true);
            mDrawerList.setSelection(positionFromIntent);
            setTitle(navMenuTitles[positionFromIntent]);
            //displayView(1);
        }
        //NAVIGATION DRAWER CONTINUES LATER

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();


        fragmentGM = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        //viewGM = findFragmentById(R.id.googleMap);
        googleMap = fragmentGM.getMap();
        googleMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Log.i("Google Places Chid bestProvider", bestProvider);

        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            Toast.makeText(FindNearbyOrphanages.this, "Retrieving Location..", Toast.LENGTH_SHORT).show();
            onLocationChanged(location);
        }

        //If Location is not enabled, then prompt the user to enable it by taking them to the location settings.
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //All location services are disabled
            AlertDialog.Builder builder = new AlertDialog.Builder(FindNearbyOrphanages.this);
            builder.setTitle("Location must be ON for this feature to work!");
            builder.setMessage("Would you like to enable it now?"); // Want to enable?
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
            return;
        }
        else{
            Toast.makeText(FindNearbyOrphanages.this, "Retrieving Location..", Toast.LENGTH_SHORT).show();
        }

        //Refresh every 180 seconds!!
        //locationManager.requestLocationUpdates(bestProvider, 60000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 180000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 180000, 0, this);


    }

    //NAVIGATION DRAWER CONTINUES HERE
    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_logout:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when  is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                locationManager.removeUpdates(this);
                finish();
                break;
            case 1:
                locationManager.removeUpdates(this);
                Intent UserMyDonationsIntent = new Intent(this, UserMyDonations.class);
                UserMyDonationsIntent.putExtra("email", demail);
                startActivity(UserMyDonationsIntent);
                mDrawerList.setItemChecked(2, true);
                mDrawerList.setSelection(2);
                setTitle(navMenuTitles[2]);
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                mDrawerLayout.closeDrawer(mDrawerList);
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                break;
            case 3:
                locationManager.removeUpdates(this);
                ft.hide(fragmentGM).commit();
                fragment = new UserFindOrphanageFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("UserHome", "Error in creating fragment or OrphanagesNearYou, UserMyDonations called!");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    //NAVIGATION DRAWER ENDS HERE

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&keyword=" + "orphanage");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

        Log.i("Google Places Chid url", googlePlacesUrl.toString());

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask(FindNearbyOrphanages.this);
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(UserBrowseOrphanagesFragment.this, "Location must be ON for this feature to work!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(UserBrowseOrphanagesFragmentActivity.this, "Retrieving Location..", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
