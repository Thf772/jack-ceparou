package io.jawg.osmcontributor.setting.fragment;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.google.android.gms.common.AccountPicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import io.jawg.osmcontributor.OsmTemplateApplication;
import io.jawg.osmcontributor.R;
import io.jawg.osmcontributor.model.events.DatabaseResetFinishedEvent;
import io.jawg.osmcontributor.rest.events.GoogleAuthenticatedEvent;
import io.jawg.osmcontributor.rest.security.GoogleOAuthManager;
import io.jawg.osmcontributor.setting.activities.SettingsActivity;
import io.jawg.osmcontributor.ui.events.login.AttemptLoginEvent;
import io.jawg.osmcontributor.ui.events.login.ErrorLoginEvent;
import io.jawg.osmcontributor.ui.events.login.UpdateGoogleCredentialsEvent;
import io.jawg.osmcontributor.ui.events.login.ValidLoginEvent;
import io.jawg.osmcontributor.utils.ConfigManager;
import io.jawg.osmcontributor.utils.StringUtils;

/**
 * Created by Flo Macé on 03/05/2017.
 */

public class ConnectionFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PICK_ACCOUNT_CODE = 1;
    private String loginKey;
    private String passwordKey;
    private Preference loginPref;
    private Preference passwordPref;
    private Preference googleConnectPref;

    @Inject
    GoogleOAuthManager googleOAuthManager;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    EventBus bus;

    @Inject
    ConfigManager configManager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((OsmTemplateApplication) getActivity().getApplication()).getOsmTemplateComponent().inject(this);
        addPreferencesFromResource(R.xml.pref_connection);
        setHasOptionsMenu(true);

        SettingsActivity appCompatActivity = (SettingsActivity) getActivity();
        if (appCompatActivity != null) {
            ActionBar actionBar = appCompatActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        Preference preference = findPreference(getString(R.string.shared_prefs_server_key));
        preference.setSummary(configManager.getBasePoiApiUrl());

        loginKey = getString(R.string.shared_prefs_login);
        passwordKey = getString(R.string.shared_prefs_password);

        loginPref = findPreference(loginKey);
        passwordPref = findPreference(passwordKey);

        updateLoginSummary(getPreferenceScreen().getSharedPreferences());
        updatePasswordSummary(getPreferenceScreen().getSharedPreferences());

        googleConnectPref = findPreference(getString(R.string.shared_prefs_google_connection_key));
        googleConnectPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = AccountPicker.newChooseAccountIntent(
                        null, null,
                        new String[]{"com.google"},
                        false, null, null, null, null);
                startActivityForResult(intent, PICK_ACCOUNT_CODE);
                return false;
            }
        });

    }

    private static final String TAG = "MyPreferenceFragment";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_ACCOUNT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                googleOAuthManager.authenticate(getActivity(), email);
            }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefsSummary(sharedPreferences, key);
    }

    private void updateLoginSummary(SharedPreferences sharedPreferences) {
        String login = getLogin(sharedPreferences);
        if (!StringUtils.isEmpty(login)) {
            loginPref.setSummary(login);
        } else {
            loginPref.setSummary(getString(R.string.summary_login));
        }
    }

    private void updatePasswordSummary(SharedPreferences sharedPreferences) {
        String password = getPassword(sharedPreferences);
        if (!StringUtils.isEmpty(password)) {
            passwordPref.setSummary(password.replaceAll("(?s).", "*"));
        } else {
            passwordPref.setSummary(getString(R.string.summary_password));
        }
    }

    private void attemptLoginIfValidFields(SharedPreferences sharedPreferences) {
        String login = getLogin(sharedPreferences);
        String password = getPassword(sharedPreferences);
        if (!StringUtils.isEmpty(login) && !StringUtils.isEmpty(password)) {
            bus.post(new AttemptLoginEvent(login, password));
        }
    }

    private void updatePrefsSummary(SharedPreferences sharedPreferences, String key) {
        if (loginKey.equals(key)) {
            // Login changed
            updateLoginSummary(sharedPreferences);
            attemptLoginIfValidFields(sharedPreferences);
        } else if (passwordKey.equals(key)) {
            // Password changed
            updatePasswordSummary(sharedPreferences);
            attemptLoginIfValidFields(sharedPreferences);
        }
    }

    private String getLogin(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(loginKey, null);
    }

    private String getPassword(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(passwordKey, null);
    }

    @Subscribe
    public void onGoogleAuthenticatedEvent(GoogleAuthenticatedEvent event) {
        if (event.isSuccessful()) {
            Snackbar.make(getView(), R.string.valid_login, Snackbar.LENGTH_SHORT).show();
            bus.post(new UpdateGoogleCredentialsEvent(event.getToken(), event.getTokenSecret(), event.getConsumer(), event.getConsumerSecret()));
        } else {
            Snackbar.make(getView(), R.string.error_login, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onValidLoginEvent(ValidLoginEvent event) {
        Snackbar.make(getView(), R.string.valid_login, Snackbar.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorLoginEvent(ErrorLoginEvent event) {
        Snackbar.make(getView(), R.string.error_login, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDatabaseResetFinishedEvent(DatabaseResetFinishedEvent event) {
        Snackbar.make(getView(), event.isSuccess() ? R.string.reset_success : R.string.reset_failure, Snackbar.LENGTH_SHORT).show();
    }
}
