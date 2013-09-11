package com.googlecode.hibiscusapp.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.fragment.SettingsFragment;

/**
 * Package: com.googlecode.hibiscusapp.activity
 * Date: 09/09/13
 * Time: 21:47
 *
 * @author eike
 */
public class SettingsActivity extends PreferenceActivity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_settings);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();
    }
}