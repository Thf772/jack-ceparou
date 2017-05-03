package io.jawg.osmcontributor.setting.fragment;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import io.jawg.osmcontributor.OsmTemplateApplication;
import io.jawg.osmcontributor.R;
import io.jawg.osmcontributor.setting.activities.SettingsActivity;
import io.jawg.osmcontributor.ui.events.edition.PleaseSetMapboxStyle;

import static io.jawg.osmcontributor.setting.activities.SettingsActivity.bindPreferenceSummaryToValue;

/**
 * This fragment shows data and sync preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AccessibilityFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String MAP_STYLE = "map_style";

    private final String DYSLEXY_KEY = "dyslexy";
    private final String COLOR_BLIND_KEY = "color_blind";
    private final String FONT_SIZE_KEY = "font_size";
    private String mapboxStyle;
    private String font;

    private boolean dyslexy;
    private String fontSize = "";

    @Inject
    EventBus eventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_accessibility);
        setHasOptionsMenu(true);

        OsmTemplateApplication application = ((OsmTemplateApplication) getActivity().getApplication());
        application.getOsmTemplateComponent().inject(this);

        SettingsActivity appCompatActivity = (SettingsActivity) getActivity();
        if (appCompatActivity != null) {
            ActionBar actionBar = appCompatActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
//        eventBus.register(this);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference("font_size"));
    }

    private String getFont() {
        SharedPreferences pref = getPreferenceManager().getDefaultSharedPreferences(getActivity().getApplicationContext());
        String styleUrl = "asset://";
        if (dyslexy) {
            styleUrl += "Dyslexy";
        } else {
            styleUrl += "Normal";
        }

        switch (fontSize) {
            case "small":
                styleUrl += "S";
                break;
            case "normal":
                styleUrl += "N";
                break;
            case "large":
                styleUrl += "L";
                break;
            case "xlarge":
                styleUrl += "XL";
                break;
            default:
                styleUrl += "N";
                break;
        }
        styleUrl += ".json";

        pref.edit().putString(MAP_STYLE, styleUrl).commit();
        return styleUrl;
    }

    private void changeMapFont() {
        eventBus.post(new PleaseSetMapboxStyle(getFont()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
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
            case DYSLEXY_KEY:
                if (sharedPreferences.getBoolean(key, false)) {
                    font = "fonts/OpenDyslexic3-Regular.ttf";
                    dyslexy = true;
                } else {
                    font = "fonts/arial.ttf";
                    dyslexy = false;
                }
                ((SettingsActivity) getActivity()).setFont(font);
                changeMapFont();
                break;
            case COLOR_BLIND_KEY:

                break;
            case FONT_SIZE_KEY:
                fontSize = sharedPreferences.getString(key, "normal");
            default:
                break;
        }
    }
}