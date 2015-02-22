package com.hprotcennoc.frostic3.connectorph;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;


public class OrphRegForm extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_reg_orph);
    }

    public void callOrphRegForm1(View view) {
        Intent OrphRegFormIntent1 = new Intent(this, OrphRegForm1.class);
        startActivity(OrphRegFormIntent1);
    }
}
