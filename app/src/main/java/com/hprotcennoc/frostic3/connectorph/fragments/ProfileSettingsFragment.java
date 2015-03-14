package com.hprotcennoc.frostic3.connectorph.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hprotcennoc.frostic3.connectorph.R;

public class ProfileSettingsFragment extends Fragment{

    View rootView;

    public ProfileSettingsFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile_settings, container, false);

        return rootView;
    }
}
