package com.hprotcennoc.frostic3.connectorph;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.hprotcennoc.frostic3.connectorph.user_my_donations_fragments.UserClaimedDonationsFeedFragment;
import com.hprotcennoc.frostic3.connectorph.user_my_donations_fragments.UserDeliveredDonationsFeedFragment;
import com.hprotcennoc.frostic3.connectorph.user_my_donations_fragments.UserUnclaimedDonationsFeedFragment;

public class UserMyDonations extends ActionBarActivity {

    MyAdapter mAdapter;
    ViewPager mPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPadding(0, 0, 20, 0);
        mPager.setClipToPadding(false);
        mPager.setPageMargin(0);
        mPager.setAdapter(mAdapter);

        /*Button button = (Button) findViewById(R.id.first);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });
        button = (Button) findViewById(R.id.last);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(ITEMS - 1);
            }
        });*/
    }

    //Handle Up button as Back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show image
                    return new UserClaimedDonationsFeedFragment();
                case 1:// Fragment # 2-9 - Will show list
                    return new UserUnclaimedDonationsFeedFragment();
                default:
                    return new UserDeliveredDonationsFeedFragment();
            }
        }
    }

}
