package com.hprotcennoc.frostic3.connectorph;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
public class TabsAdapterLaunch extends FragmentStatePagerAdapter{
    private int TOTAL_TABS = 2;

    public TabsAdapterLaunch(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int index) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return TOTAL_TABS;
    }

}
