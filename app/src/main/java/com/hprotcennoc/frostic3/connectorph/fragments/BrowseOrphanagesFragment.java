package com.hprotcennoc.frostic3.connectorph.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hprotcennoc.frostic3.connectorph.R;

public class BrowseOrphanagesFragment extends Fragment{

    View rootView;

    public BrowseOrphanagesFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_browse_orphanages, container, false);

        return rootView;
    }
}
