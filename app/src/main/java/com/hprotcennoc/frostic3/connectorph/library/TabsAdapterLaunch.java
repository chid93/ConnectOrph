package com.hprotcennoc.frostic3.connectorph.library;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hprotcennoc.frostic3.connectorph.launch_view.DonorFragmentLaunch;
import com.hprotcennoc.frostic3.connectorph.launch_view.OrphanageFragmentLaunch;

public class TabsAdapterLaunch extends FragmentStatePagerAdapter{
    private int TOTAL_TABS = 2;

    public TabsAdapterLaunch(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new DonorFragmentLaunch();
            case 1:
                return new OrphanageFragmentLaunch();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TOTAL_TABS;
    }

}
