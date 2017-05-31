package com.example.lokesh.machadaloassignment;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by Lokesh on 31-05-2017.
 */

//prefrence activity to store the sorting prefrnce of the user
// using the Sharedprefrences
public class Settings extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener  {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add settings preferences, defined in the XML file
        addPreferencesFromResource(R.xml.xml);

        // For all preferences attached an OnPreferenceChangeListener
        bindPreferenceSummaryToValue(findPreference(getString(R.string.list_pref_key)));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }
}
