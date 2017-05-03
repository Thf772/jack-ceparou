package io.jawg.osmcontributor.setting.fragment;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
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
public class AccessibilityFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String DYSLEXY_KEY = "dyslexy";
    private final String COLOR_BLIND_KEY = "color_blind";
    private final String FONT_SIZE_KEY = "font_size";

    private Preference dyslexy;
    private Preference fontSize;
    private Preference colorBlind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_accessibility);
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
        bindPreferenceSummaryToValue(findPreference("font_size"));
    }

/*
    private void changeMapFont(String fontName) {
//        curl "https://api.mapbox.com/styles/v1/{username}/{style_id}?access_token=your-access-token"
        retrieveMapBoxStyle();
    }

    private String retrieveMapBoxStyle() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("conf.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String api_key = prop.getProperty("mapbox_token");
        String url = "https://api.mapbox.com/styles/v1/{username}/{style_id}?access_token=" + api_key;

        return "";
    }

    private void changeMapFontSize() {

    }
*/

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
            case DYSLEXY_KEY:
                String font;
                if (sharedPreferences.getBoolean(key, false)) {
                    font = "fonts/OpenDyslexic3-Regular.ttf";
                } else {
                    font = "fonts/arial.ttf";
                }
                ((SettingsActivity) getActivity()).setFont(font);
//                changeMapFont(font);
                break;
            case COLOR_BLIND_KEY:

                break;
            default:
                break;
        }
    }
}