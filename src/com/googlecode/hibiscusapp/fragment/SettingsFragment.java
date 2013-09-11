package com.googlecode.hibiscusapp.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.googlecode.hibiscusapp.R;

/**
 * Package: com.googlecode.hibiscusapp.fragment
 * Date: 07/09/13
 * Time: 22:25
 *
 * @author eike
 */
public class SettingsFragment extends PreferenceFragment
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // update the activity title
        getActivity().setTitle(R.string.title_settings);
    }
}