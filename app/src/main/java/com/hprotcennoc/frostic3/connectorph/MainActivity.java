package com.hprotcennoc.frostic3.connectorph;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener{

    private ViewPager tabsviewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabsviewPager = (ViewPager) findViewById(R.id.tabspager);

        TabsAdapterLaunch mTabsAdapter = new TabsAdapterLaunch(getSupportFragmentManager());

        tabsviewPager.setAdapter(mTabsAdapter);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab donorstab = getSupportActionBar().newTab().setText("Donor").setTabListener(this);
        ActionBar.Tab orphanagetab = getSupportActionBar().newTab().setText("Orphanage").setTabListener(this);

        getSupportActionBar().addTab(donorstab);
        getSupportActionBar().addTab(orphanagetab);


        //This helps in providing swiping effect for v7 compat library
        tabsviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onTabReselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(ActionBar.Tab selectedtab, FragmentTransaction arg1) {
        // TODO Auto-generated method stub
        tabsviewPager.setCurrentItem(selectedtab.getPosition()); //update tab position on tap
    }

    @Override
    public void onTabUnselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

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
