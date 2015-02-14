package com.hprotcennoc.frostic3.connectorph;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by frostic3 on 2/14/2015.
 */
public class OrphanageFragmentLaunch extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.launch_view_orphanage, container, false);

        return view;
    }
}
