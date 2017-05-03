package io.jawg.osmcontributor.setting.fragment;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;

import io.jawg.osmcontributor.R;
import io.jawg.osmcontributor.setting.activities.SettingsActivity;

import static io.jawg.osmcontributor.setting.activities.SettingsActivity.bindPreferenceSummaryToValue;

/**
 * This fragment shows data and sync preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DataSyncPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_data_sync);
        setHasOptionsMenu(true);

        SettingsActivity appCompatActivity = (SettingsActivity) getActivity();
        if (appCompatActivity != null) {
            ActionBar actionBar = appCompatActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference("sync_frequency"));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (String.valueOf(key)) {
            case "enable_sync":
                boolean syncEnable = sharedPreferences.getBoolean(key, false);
                if (syncEnable) {
                    ((SettingsActivity) getActivity()).startSync();
                } else {
                    ((SettingsActivity) getActivity()).stopSync();
                }
                break;
            case "sync_frequency":
                String sync_freq = sharedPreferences.getString(key, "300000");
                ((SettingsActivity) getActivity()).setmInterval(Integer.valueOf(sync_freq));
                break;
            default:

                break;

        }
    }
}