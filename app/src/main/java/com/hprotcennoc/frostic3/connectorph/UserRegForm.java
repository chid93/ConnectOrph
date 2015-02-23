package com.hprotcennoc.frostic3.connectorph;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class UserRegForm extends ActionBarActivity implements AdapterView.OnItemSelectedListener{

    // Validation starts here
    public final static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private View.OnFocusChangeListener mOnFocusChangeListener
           = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            //Get the view called and store it as an edit text
            EditText view = (EditText) v;
            if(!hasFocus){
                //Check for Null value
                if(view.length() == 0) {
                    view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                    view.setError("Required");
                }
                //Check if it is a valid Email id
                else if(v == findViewById(R.id.fr_user_email_ET)){
                    String emailText = view.getText().toString();
                    if(!isValidEmail(emailText)){
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                        view.setError("Invalid Email");
                    }
                    else{
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
                        view.setError(null);
                    }
                }
                //Check if both Passwords match
                else if(v == findViewById(R.id.fr_user_retype_password_ET)){
                    EditText password = (EditText) findViewById(R.id.fr_user_password_ET);
                    if(!(view.getText().toString().equals(password.getText().toString()))){
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                        view.setError("Passwords does not match");
                    }
                    else{
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
                        view.setError(null);
                    }
                }
                //Check for Valid Phone Number
                else if(v == findViewById(R.id.fr_user_phone_number_ET)){
                    String phoneNumber = ((EditText) v).getText().toString();
                    if(phoneNumber.length()<8){
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                        view.setError("Invalid Phone Number");
                    }
                    else{
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
                        view.setError(null);
                    }
                }
                //Clear error
                else{
                    view.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
                    view.setError(null);
                }
            }
        }
    };
    // Validation ends here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_reg_user);

        EditText name = (EditText) findViewById(R.id.fr_user_name_ET);
        EditText email = (EditText) findViewById(R.id.fr_user_email_ET);
        EditText password = (EditText) findViewById(R.id.fr_user_password_ET);
        EditText retypePass = (EditText) findViewById(R.id.fr_user_retype_password_ET);
        EditText phoneNumber = (EditText) findViewById(R.id.fr_user_phone_number_ET);

        name.setOnFocusChangeListener(mOnFocusChangeListener);
        email.setOnFocusChangeListener(mOnFocusChangeListener);
        password.setOnFocusChangeListener(mOnFocusChangeListener);
        retypePass.setOnFocusChangeListener(mOnFocusChangeListener);
        phoneNumber.setOnFocusChangeListener(mOnFocusChangeListener);

        Spinner stateSpinner = (Spinner) findViewById(R.id.fr_user_state_spinner);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.state, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setOnItemSelectedListener(this);
        /* */
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner citySpinner = (Spinner) findViewById(R.id.fr_user_city_spinner);
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
