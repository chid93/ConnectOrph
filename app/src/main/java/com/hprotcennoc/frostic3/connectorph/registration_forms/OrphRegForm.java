package com.hprotcennoc.frostic3.connectorph.registration_forms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import com.hprotcennoc.frostic3.connectorph.R;


public class OrphRegForm extends ActionBarActivity{

    EditText name;
    EditText email;
    EditText password;
    EditText retypePass;

    // Validation starts here
    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean clientSideCheck(EditText view) {
        if(view.length() == 0) {
            view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
            view.setError("Required");
            return false;
        }
        if(view == findViewById(R.id.fr_orph_email_ET)) {
            String emailText = view.getText().toString();
            if (!isValidEmail(emailText)) {
                view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                view.setError("Invalid Email");
                return false;
            }
        }
        if(view == findViewById(R.id.fr_orph_retype_password_ET)) {
            EditText password = (EditText) findViewById(R.id.fr_orph_password_ET);
            if (!(view.getText().toString().equals(password.getText().toString()))) {
                view.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));
                view.setError("Passwords does not match");
                return false;
            }
        }
        return true;
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

        name = (EditText) findViewById(R.id.fr_orph_name_ET);
        email = (EditText) findViewById(R.id.fr_orph_email_ET);
        password = (EditText) findViewById(R.id.fr_orph_password_ET);
        retypePass = (EditText) findViewById(R.id.fr_orph_retype_password_ET);

        name.setOnFocusChangeListener(mOnFocusChangeListener);
        email.setOnFocusChangeListener(mOnFocusChangeListener);
        password.setOnFocusChangeListener(mOnFocusChangeListener);
        retypePass.setOnFocusChangeListener(mOnFocusChangeListener);
    }

    public void callOrphRegForm1(View view) {

        if( clientSideCheck(name) && clientSideCheck(email) && clientSideCheck(password) && clientSideCheck(retypePass) ) {
            Intent OrphRegFormIntent1 = new Intent(this, OrphRegForm1.class);
            OrphRegFormIntent1.putExtra("name", name.getText().toString());
            OrphRegFormIntent1.putExtra("email", email.getText().toString());
            OrphRegFormIntent1.putExtra("password", password.getText().toString());
            startActivityForResult(OrphRegFormIntent1, 1); //Change to startActivityForResult

        }
        else{
            clientSideCheck(name);
            clientSideCheck(email);
            clientSideCheck(password);
            clientSideCheck(retypePass);
        }
    }

    //Call finish() if OrphRegForm1 successfully registered a new orphanage
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if(data.getStringExtra("choice").equals("YES"))
                finish();
        }
    }

}
