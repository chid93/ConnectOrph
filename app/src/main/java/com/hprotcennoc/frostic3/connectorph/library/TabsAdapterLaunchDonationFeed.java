package com.hprotcennoc.frostic3.connectorph.library;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hprotcennoc.frostic3.connectorph.user_my_donations_activities.UserClaimedDonationsFeedFragment;
import com.hprotcennoc.frostic3.connectorph.user_my_donations_activities.UserUnclaimedDonationsFeedFragment;

public class TabsAdapterLaunchDonationFeed extends FragmentStatePagerAdapter{
    private int TOTAL_TABS = 2;

    public TabsAdapterLaunchDonationFeed(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new UserClaimedDonationsFeedFragment();
            case 1:
                return new UserUnclaimedDonationsFeedFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TOTAL_TABS;
    }

}
