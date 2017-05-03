package io.jawg.osmcontributor.setting.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import io.jawg.osmcontributor.OsmTemplateApplication;
import io.jawg.osmcontributor.R;
import io.jawg.osmcontributor.model.events.DatabaseResetFinishedEvent;
import io.jawg.osmcontributor.model.events.ResetDatabaseEvent;
import io.jawg.osmcontributor.model.events.ResetTypeDatabaseEvent;
import io.jawg.osmcontributor.setting.activities.SettingsActivity;
import io.jawg.osmcontributor.utils.ConfigManager;

import static io.jawg.osmcontributor.setting.activities.SettingsActivity.bindPreferenceSummaryToValue;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int RC_SIGN_IN = 2;
    private Preference resetTypePref;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    EventBus bus;

    @Inject
    ConfigManager configManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((OsmTemplateApplication) getActivity().getApplication()).getOsmTemplateComponent().inject(this);
        addPreferencesFromResource(R.xml.pref_general);
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
        bindPreferenceSummaryToValue(findPreference("display_name"));
        bindPreferenceSummaryToValue(findPreference("langage_list"));

        Preference h2geoPreference = findPreference(getString(R.string.shared_prefs_h2geo_version));
        h2geoPreference.setTitle(sharedPreferences.getString(getString(R.string.shared_prefs_h2geo_version), ""));
        h2geoPreference.setSummary(sharedPreferences.getString(getString(R.string.shared_prefs_h2geo_date), ""));

        Preference resetPreference = findPreference(getString(R.string.shared_prefs_reset));
        resetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.reset_dialog_message)
                        .setPositiveButton(R.string.reset_dialog_positive_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                bus.post(new ResetDatabaseEvent());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.reset_dialog_negative_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
                return false;
            }
        });

        resetTypePref = findPreference(getString(R.string.shared_prefs_reset_type));
        resetTypePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.reset_dialog_message)
                        .setPositiveButton(R.string.reset_dialog_positive_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                bus.post(new ResetTypeDatabaseEvent());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.reset_dialog_negative_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
                return false;
            }
        });

        findPreference(getString(R.string.shared_prefs_expert_mode)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (sharedPreferences.getBoolean(getString(R.string.shared_prefs_expert_mode), false)) {
                    new AlertDialog.Builder(getActivity())
                            .setCancelable(false)
                            .setMessage(getString(R.string.expert_mode_dialog))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                return false;
            }
        });

        if (!sharedPreferences.getBoolean(getString(R.string.shared_prefs_expert_mode), false)) {
                getPreferenceScreen().removePreference(resetTypePref);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        bus.unregister(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.shared_prefs_expert_mode).equals(key)) {
            if (sharedPreferences.getBoolean(key, false)) {
                getPreferenceScreen().addPreference(resetTypePref);
            } else {
                getPreferenceScreen().removePreference(resetTypePref);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDatabaseResetFinishedEvent(DatabaseResetFinishedEvent event) {
        Snackbar.make(getView(), event.isSuccess() ? R.string.reset_success : R.string.reset_failure, Snackbar.LENGTH_SHORT).show();
    }
}