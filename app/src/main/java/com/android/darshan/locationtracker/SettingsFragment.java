package com.android.darshan.locationtracker;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_general);

    }



}
