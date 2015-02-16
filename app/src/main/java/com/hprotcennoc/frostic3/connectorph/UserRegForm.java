package com.hprotcennoc.frostic3.connectorph;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserRegForm extends ActionBarActivity{

    private EditText name;
    private RelativeLayout layout;
    private TextView validation_req;

    RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    );

     private View.OnFocusChangeListener mOnFocusChangeListener
            = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.i("UserRegForm","onFocusChange called");
            /* String check = name.getText().toString(); */
            if(!hasFocus){
                if(name.length() == 0) {
                    Log.i("UserRegForm", "Condition called");
                    name.setBackground(getResources().getDrawable(R.drawable.rounded_errortext));

                    validation_req = new TextView(UserRegForm.this);
                    validation_req.setText("Required");

                    layout = (RelativeLayout) findViewById(R.id.fr_user_relative_layout);
                    lParams.addRule(RelativeLayout.BELOW,R.id.fr_user_name_ET);
                    layout.addView(validation_req,lParams);/**/
                }
                else{

                    name.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_reg_user);
        Log.i("UserRegForm","onCreate called");
        name = (EditText) findViewById(R.id.fr_user_name_ET);
        name.setOnFocusChangeListener(mOnFocusChangeListener);/* */
    }
}
