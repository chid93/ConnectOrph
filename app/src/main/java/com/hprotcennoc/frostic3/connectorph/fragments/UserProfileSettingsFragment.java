package com.hprotcennoc.frostic3.connectorph.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hprotcennoc.frostic3.connectorph.R;

public class UserProfileSettingsFragment extends Fragment{

    View rootView;

    public UserProfileSettingsFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_user_profile_settings, container, false);

        return rootView;
    }
}
