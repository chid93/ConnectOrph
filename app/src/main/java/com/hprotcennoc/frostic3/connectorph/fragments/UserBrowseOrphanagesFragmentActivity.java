package com.hprotcennoc.frostic3.connectorph.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.hprotcennoc.frostic3.connectorph.R;
import com.hprotcennoc.frostic3.connectorph.google_places.GooglePlacesReadTask;

public class UserBrowseOrphanagesFragmentActivity extends ActionBarActivity implements LocationListener {

    private static final String GOOGLE_API_KEY = "AIzaSyABxEhA3abVcf0BbWXzBZTKKvmclx9Ibak";
    GoogleMap googleMap;
    double latitude = 0;
    double longitude = 0;
    private int PROXIMITY_RADIUS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(UserBrowseOrphanagesFragmentActivity.this, "Google Play Services is not available!!", Toast.LENGTH_LONG).show();
            finish();
        }
        setContentView(R.layout.fragment_activity_user_browse_orphanages);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = fragment.getMap();
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Log.i("Google Places Chid bestProvider", bestProvider);

        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            Toast.makeText(UserBrowseOrphanagesFragmentActivity.this, "Retrieving Location..", Toast.LENGTH_SHORT).show();
            onLocationChanged(location);
        }

        //If Location is not enabled, then prompt the user to enable it by taking them to the location settings.
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //All location services are disabled
            AlertDialog.Builder builder = new AlertDialog.Builder(UserBrowseOrphanagesFragmentActivity.this);
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

        //Refresh every 60 seconds!!
        //locationManager.requestLocationUpdates(bestProvider, 60000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);


    }

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
        Toast.makeText(UserBrowseOrphanagesFragmentActivity.this, "Retrieving Location..", Toast.LENGTH_SHORT).show();
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

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask(UserBrowseOrphanagesFragmentActivity.this);
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
        // TODO Auto-generated method stub
    }
}
