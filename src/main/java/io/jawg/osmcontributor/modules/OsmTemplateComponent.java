/**
 * Copyright (C) 2016 eBusiness Information
 *
 * This file is part of OSM Contributor.
 *
 * OSM Contributor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSM Contributor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OSM Contributor.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.jawg.osmcontributor.modules;

import android.app.Application;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import io.jawg.osmcontributor.database.PoiAssetLoader;
import io.jawg.osmcontributor.database.helper.DatabaseHelper;
import io.jawg.osmcontributor.database.helper.DatabaseModule;
import io.jawg.osmcontributor.database.helper.OsmSqliteOpenHelper;
import io.jawg.osmcontributor.flickr.oauth.FlickrOAuth;
import io.jawg.osmcontributor.login.LoginModule;
import io.jawg.osmcontributor.rest.CommonSyncModule;
import io.jawg.osmcontributor.rest.security.GoogleOAuthManager;
import io.jawg.osmcontributor.rest.managers.H2GeoPresetsManager;
import io.jawg.osmcontributor.rest.managers.SyncManager;
import io.jawg.osmcontributor.service.CancelButtonReceiver;
import io.jawg.osmcontributor.service.OfflineRegionDownloadService;
import io.jawg.osmcontributor.service.OfflineRegionModule;
import io.jawg.osmcontributor.setting.fragment.AccessibilityFragment;
import io.jawg.osmcontributor.setting.fragment.ConnectionFragment;
import io.jawg.osmcontributor.setting.fragment.GeneralPreferenceFragment;
import io.jawg.osmcontributor.sync.SyncModule;
import io.jawg.osmcontributor.ui.activities.EditPoiActivity;
import io.jawg.osmcontributor.ui.activities.LoadProfileActivity;
import io.jawg.osmcontributor.ui.activities.MapActivity;
import io.jawg.osmcontributor.ui.activities.NoteActivity;
import io.jawg.osmcontributor.ui.activities.OfflineRegionsActivity;
import io.jawg.osmcontributor.ui.activities.PhotoActivity;
import io.jawg.osmcontributor.ui.activities.PickValueActivity;
import io.jawg.osmcontributor.ui.activities.SplashScreenActivity;
import io.jawg.osmcontributor.ui.activities.TypeListActivity;
import io.jawg.osmcontributor.ui.activities.UploadActivity;
import io.jawg.osmcontributor.ui.adapters.OpeningHoursLinearLayoutAdapter;
import io.jawg.osmcontributor.ui.adapters.OpeningMonthAdapter;
import io.jawg.osmcontributor.ui.adapters.ProfileAdapter;
import io.jawg.osmcontributor.ui.adapters.TagsAdapter;
import io.jawg.osmcontributor.ui.adapters.parser.OpeningMonthValueParser;
import io.jawg.osmcontributor.ui.adapters.parser.OpeningTimeValueParser;
import io.jawg.osmcontributor.ui.dialogs.AddValueDialogFragment;
import io.jawg.osmcontributor.ui.dialogs.EditPoiTagDialogFragment;
import io.jawg.osmcontributor.ui.dialogs.EditPoiTypeDialogFragment;
import io.jawg.osmcontributor.ui.dialogs.EditPoiTypeNameDialogFragment;
import io.jawg.osmcontributor.ui.dialogs.LoginDialogFragment;
import io.jawg.osmcontributor.ui.dialogs.NoteCommentDialogFragment;
import io.jawg.osmcontributor.ui.fragments.EditPoiFragment;
import io.jawg.osmcontributor.ui.fragments.MapFragment;
import io.jawg.osmcontributor.ui.fragments.NoteDetailFragment;
import io.jawg.osmcontributor.ui.fragments.PoiDetailFragment;
import io.jawg.osmcontributor.ui.managers.EditPoiManager;
import io.jawg.osmcontributor.ui.managers.LoginManager;
import io.jawg.osmcontributor.ui.managers.NoteManager;
import io.jawg.osmcontributor.ui.managers.PoiManager;
import io.jawg.osmcontributor.ui.managers.TypeManager;
import io.jawg.osmcontributor.ui.managers.WaysManager;
import io.jawg.osmcontributor.ui.presenters.MapFragmentPresenter;
import io.jawg.osmcontributor.ui.presenters.TypeListActivityPresenter;
import io.jawg.osmcontributor.utils.core.ArpiInitializer;
import io.jawg.osmcontributor.utils.core.CoreModule;
import io.jawg.osmcontributor.utils.ways.Geocoder;

@Singleton
@Component(modules = {
        OsmTemplateModule.class,
        CoreModule.class,
        DatabaseModule.class,
        SyncModule.class,
        PresetsModule.class,
        CommonSyncModule.class,
        LoginModule.class,
        TypeModule.class,
        OfflineRegionModule.class
})
public interface OsmTemplateComponent {
    // INJECTING

    void inject(Application osmTemplateApplication);

    // Activities
    void inject(SplashScreenActivity splashScreenActivity);

    void inject(EditPoiActivity editPoiActivity);

    void inject(MapActivity mapActivity);

    void inject(PickValueActivity pickValueActivity);

    void inject(NoteActivity noteActivity);

    void inject(UploadActivity uploadActivity);

    void inject(TypeListActivity typeListActivity);

    void inject(TypeListActivityPresenter typeListActivityPresenter);

    void inject(LoadProfileActivity loadProfileActivity);

    // Fragments
    void inject(ConnectionFragment myPreferenceFragment);

    void inject(GeneralPreferenceFragment myPreferenceFragment);

    void inject(EditPoiFragment editPoiFragment);

    void inject(AccessibilityFragment accessibilityFragment);

    void inject(AddValueDialogFragment addValueDialogFragment);

    void inject(MapFragment mapFragment);

    void inject(MapFragmentPresenter mapFragmentPresenter);

    void inject(PoiDetailFragment poiDetailFragment);

    void inject(NoteDetailFragment noteDetailFragment);

    void inject(NoteCommentDialogFragment noteCommentDialogFragment);

    void inject(EditPoiTypeDialogFragment editPoiTypeDialogFragment);

    void inject(EditPoiTypeNameDialogFragment editPoiTypeNameDialogFragment);

    void inject(EditPoiTagDialogFragment editPoiTagDialogFragment);

    void inject(OpeningMonthAdapter openingMonthAdapter);

    void inject(OpeningHoursLinearLayoutAdapter openingHoursLinearLayoutAdapter);

    void inject(TagsAdapter tagsAdapter);

    void inject(PhotoActivity photoActivity);

    void inject(FlickrOAuth flickrOAuth);

    void inject(LoginDialogFragment loginDialogFragment);

    void inject(ProfileAdapter profileAdapter);

    void inject(OfflineRegionDownloadService offlineRegionDownloadService);

    void inject(CancelButtonReceiver cancelButtonReceiver);

    void inject(OfflineRegionsActivity offlineRegionsActivity);

    // PROVIDING

    // Core
    EventBus getEventBus();

    WaysManager getEditVectorialWayManager();

    Geocoder getGeocoder();

    ArpiInitializer getArpiInitializer();

    // Database

    DatabaseHelper getDatabaseHelper();

    OsmSqliteOpenHelper getDatabaseOpenHelper();

    PoiAssetLoader getPoiAssetLoader();

    PoiManager getPoiManager();

    NoteManager getNoteManager();

    // Login
    LoginManager getLoginManager();

    // Sync
    Gson getGson();

    EditPoiManager getEditPoiManager();

    SyncManager getSyncManager();

    // Presets

    H2GeoPresetsManager getPresetsManager();

    // Poi type
    TypeManager getTypeManager();

    OpeningTimeValueParser getOpeningTimeParser();

    OpeningMonthValueParser getOpeningMonthParser();

    GoogleOAuthManager getGoogleOAuthManager();
}
