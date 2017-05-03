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
 * This fragment shows notification preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NotificationPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notification);
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
        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
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
            case "notifications_new_report":
                boolean syncEnable = sharedPreferences.getBoolean("notifications_new_report", true);
                ((SettingsActivity) getActivity()).setNotificationOn(syncEnable);
                break;
            case "notifications_new_message_vibrate":
                boolean vibrationEnable = sharedPreferences.getBoolean("notifications_new_message_vibrate", true);
                ((SettingsActivity) getActivity()).setVibrationAllow(vibrationEnable);
                break;
            default:

                break;

        }
    }
}