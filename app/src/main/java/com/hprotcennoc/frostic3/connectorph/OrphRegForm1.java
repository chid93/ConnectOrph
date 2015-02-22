package com.hprotcennoc.frostic3.connectorph;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class OrphRegForm1 extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_reg_orph_1);
        //Populate state spinner using an adapter

        Spinner stateSpinner = (Spinner) findViewById(R.id.fr_orph_1_state_spinner);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.state, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        //Create a onItemSelectedListener for dynamically adding cities after states has been selected
        stateSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner citySpinner = (Spinner) findViewById(R.id.fr_orph_1_city_spinner);
        ArrayAdapter<CharSequence> cityAdapter;

        switch (position){
            case 1: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityAndaman_and_Nicobar_Island, android.R.layout.simple_spinner_item);
                break;
            case 2: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityAndhra_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 3: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityArunachal_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 4: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityAssam, android.R.layout.simple_spinner_item);
                break;
            case 5: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityBihar, android.R.layout.simple_spinner_item);
                break;
            case 6: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityChandigarh, android.R.layout.simple_spinner_item);
                break;
            case 7: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityChhattisgarh, android.R.layout.simple_spinner_item);
                break;
            case 8: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityDadra_and_Nagar_Haveli, android.R.layout.simple_spinner_item);
                break;
            case 9: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityDelhi, android.R.layout.simple_spinner_item);
                break;
            case 10: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityGoa, android.R.layout.simple_spinner_item);
                break;
            case 11: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityGujarat, android.R.layout.simple_spinner_item);
                break;
            case 12: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityHaryana, android.R.layout.simple_spinner_item);
                break;
            case 13: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityHimachal_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 14: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityJammu_and_Kashmir, android.R.layout.simple_spinner_item);
                break;
            case 15: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityJharkhand, android.R.layout.simple_spinner_item);
                break;
            case 16: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityKarnataka, android.R.layout.simple_spinner_item);
                break;
            case 17: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityKerala, android.R.layout.simple_spinner_item);
                break;
            case 18: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMadhya_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 19: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMaharashtra, android.R.layout.simple_spinner_item);
                break;
            case 20: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityManipur, android.R.layout.simple_spinner_item);
                break;
            case 21: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMeghalaya, android.R.layout.simple_spinner_item);
                break;
            case 22: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityMizoram, android.R.layout.simple_spinner_item);
                break;
            case 23: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityNagaland, android.R.layout.simple_spinner_item);
                break;
            case 24: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityOdisha, android.R.layout.simple_spinner_item);
                break;
            case 25: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityPuducherry, android.R.layout.simple_spinner_item);
                break;
            case 26: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityPunjab, android.R.layout.simple_spinner_item);
                break;
            case 27: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityRajasthan, android.R.layout.simple_spinner_item);
                break;
            case 28: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityTamil_Nadu, android.R.layout.simple_spinner_item);
                break;
            case 29: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityTelangana, android.R.layout.simple_spinner_item);
                break;
            case 30: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityTripura, android.R.layout.simple_spinner_item);
                break;
            case 31: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityUttar_Pradesh, android.R.layout.simple_spinner_item);
                break;
            case 32: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityUttarakhand, android.R.layout.simple_spinner_item);
                break;
            case 33: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityWest_Bengal, android.R.layout.simple_spinner_item);
                break;
            default: cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityDefault,android.R.layout.simple_spinner_item);
        }

        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
