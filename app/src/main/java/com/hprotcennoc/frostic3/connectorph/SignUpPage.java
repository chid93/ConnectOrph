package com.hprotcennoc.frostic3.connectorph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by frostic3 on 2/9/2015.
 */
public class SignUpPage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_signup);
    }

    public void callOrphRegForm(View view) {
        Intent OrphRegFormIntent = new Intent(this, OrphRegForm.class);
        startActivity(OrphRegFormIntent);
    }

    public void callUserRegForm(View view) {
        Intent UserRegFormIntent = new Intent(this, UserRegForm.class);
        startActivity(UserRegFormIntent);
    }
}
