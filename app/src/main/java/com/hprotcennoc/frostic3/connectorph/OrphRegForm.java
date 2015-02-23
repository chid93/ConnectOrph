package com.hprotcennoc.frostic3.connectorph;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class OrphRegForm extends ActionBarActivity{

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
                else if(v == findViewById(R.id.fr_orph_email_ET)){
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
                else if(v == findViewById(R.id.fr_orph_retype_password_ET)){
                    EditText password = (EditText) findViewById(R.id.fr_orph_password_ET);
                    if(!(view.getText().toString().equals(password.getText().toString()))){
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                        view.setError("Passwords does not match");
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
        setContentView(R.layout.form_reg_orph);

        EditText name = (EditText) findViewById(R.id.fr_orph_name_ET);
        EditText email = (EditText) findViewById(R.id.fr_orph_email_ET);
        EditText password = (EditText) findViewById(R.id.fr_orph_password_ET);
        EditText retypePass = (EditText) findViewById(R.id.fr_orph_retype_password_ET);

        name.setOnFocusChangeListener(mOnFocusChangeListener);
        email.setOnFocusChangeListener(mOnFocusChangeListener);
        password.setOnFocusChangeListener(mOnFocusChangeListener);
        retypePass.setOnFocusChangeListener(mOnFocusChangeListener);
    }

    public void callOrphRegForm1(View view) {
        Intent OrphRegFormIntent1 = new Intent(this, OrphRegForm1.class);
        startActivity(OrphRegFormIntent1);
    }
}
