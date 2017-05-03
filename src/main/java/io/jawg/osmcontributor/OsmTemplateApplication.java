/**
 * Copyright (C) 2016 eBusiness Information
 * <p>
 * This file is part of OSM Contributor.
 * <p>
 * OSM Contributor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * OSM Contributor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with OSM Contributor.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.jawg.osmcontributor;


import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.stetho.Stetho;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.mapbox.mapboxsdk.MapboxAccountManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import io.fabric.sdk.android.Fabric;
import io.jawg.osmcontributor.modules.DaggerOsmTemplateComponent;
import io.jawg.osmcontributor.modules.OsmTemplateComponent;
import io.jawg.osmcontributor.modules.OsmTemplateModule;
import io.jawg.osmcontributor.ui.activities.MapActivity;
import io.jawg.osmcontributor.utils.core.StoreConfigManager;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static io.jawg.osmcontributor.setting.fragment.AccessibilityFragment.MAP_STYLE;

public class OsmTemplateApplication extends Application {

    /*=========================================*/
    /*--------------ATTRIBUTES-----------------*/
    /*=========================================*/
    public static final int NOTIFICATION_ID = 001;

    private static int mInterval;
    private static boolean syncEnable;
    private static boolean notificationEnable;
    private SharedPreferences prefs;
    private static Handler mHandler;
    private static boolean vibrationAllow;
    private static NotificationCompat.Builder notification;
    private PendingIntent notificationClickEffect;

    private String mapStyle;

    private OsmTemplateComponent osmTemplateComponent;

    private Flickr flickr;

    /*=========================================*/
    /*---------------OVERRIDE------------------*/
    /*=========================================*/
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Fabric.with(this, new Crashlytics());

        // Init Stetho for debug purpose (database)
        Stetho.initializeWithDefaults(this);

        // Init Dagger
        osmTemplateComponent = DaggerOsmTemplateComponent.builder().osmTemplateModule(new OsmTemplateModule(this)).build();
        osmTemplateComponent.inject(this);



        // Init Flickr object
        StoreConfigManager configManager = new StoreConfigManager();
        flickr = new Flickr(configManager.getFlickrApiKey(), configManager.getFlickrApiKeySecret(), new REST());

        // Cache Disk for Fresco
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), getPackageName()))
                .setBaseDirectoryName("images")
                .build();
        // Cache Memory for Fresco
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
                    @Override
                    public MemoryCacheParams get() {
                        return new MemoryCacheParams(10485760, 100, 100, 100, 100);
                    }
                })
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();

        // Init Fresco
        Fresco.initialize(this, imagePipelineConfig);

        // Init event bus
        EventBus bus = osmTemplateComponent.getEventBus();
        bus.register(getOsmTemplateComponent().getLoginManager());
        bus.register(getOsmTemplateComponent().getEditPoiManager());
        bus.register(getOsmTemplateComponent().getPoiManager());
        bus.register(getOsmTemplateComponent().getNoteManager());
        bus.register(getOsmTemplateComponent().getSyncManager());
        bus.register(getOsmTemplateComponent().getTypeManager());
        bus.register(getOsmTemplateComponent().getPresetsManager());
        bus.register(getOsmTemplateComponent().getGeocoder());
        bus.register(getOsmTemplateComponent().getArpiInitializer());
        bus.register(getOsmTemplateComponent().getEditVectorialWayManager());

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.shared_prefs_preset_default), false)) {
            editor.putBoolean(getString(R.string.shared_prefs_preset_default), true);
        }
        editor.apply();

        MapboxAccountManager.start(this, BuildConfig.MAPBOX_TOKEN);

        createNotification();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mapStyle = prefs.getString(MAP_STYLE, "asset://mapnik.json");

        if (prefs.getBoolean("dyslexy", false)) {
            setFont("fonts/OpenDyslexic3-Regular.ttf");
        } else {
            setFont("fonts/arial.ttf");
        }

        vibrationAllow = prefs.getBoolean("notifications_new_message_vibrate", false);

        notificationEnable = prefs.getBoolean("notifications_new_report", false);

        syncEnable = prefs.getBoolean("enable_sync", false);

        mHandler = new Handler();
        mInterval = Integer.valueOf(prefs.getString("sync_frequency", "300000"));

        if (syncEnable) {
            startRepeatingTask();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    private void createNotification() {
        notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(getResources().getString(R.string.notification_message))
                        .setContentText(getResources().getString(R.string.notification_message_values));

        notification.setAutoCancel(true);
        Intent resultIntent = new Intent(this, MapActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationClickEffect =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        notification.setContentIntent(notificationClickEffect);
    }

    public void setVibrationAllow(boolean vAllow) {
        vibrationAllow = vAllow;
        notificationVibration();
    }

    public void setmInterval(int freq) {
        mInterval = freq;
    }

    public void setNotificationOn(boolean notifOn) {
        notificationEnable = notifOn;
    }

    public void setFont(String fontName) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(fontName)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    public void init(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = sharedPref.getString("font_size", "normal");
        switch (fontSize) {
            case "small":
                context.setTheme(R.style.FontSmall);
                break;
            case "normal":
                context.setTheme(R.style.FontMedium);
                break;
            case "large":
                context.setTheme(R.style.FontLarge);
                break;
            case "xlarge":
                context.setTheme(R.style.FontXLarge);
                break;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (syncEnable) {
            stopRepeatingTask();
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                //Application.showToast("Données mises à jour");
                if (notificationEnable) {
                    notification();
                }
                /*synchro*/
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };


    public void notification() {
        NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, notification.build());

        if (vibrationAllow) {
            notificationVibration();
        }
    }

    public void notificationVibration() {
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds

        long[] pattern = {100, 100};
        v.vibrate(200);
    }

    public void showToast(String data) {
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
    }

    public void startRepeatingTask() {
        mStatusChecker.run();
        showToast("Synchronisation activé");
        syncEnable = true;
    }

    public void stopRepeatingTask() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mStatusChecker);
        }
        showToast("Synchronisation désactivé");
        syncEnable = false;
    }

    /*=========================================*/
    /*----------------GETTER-------------------*/
    /*=========================================*/

    /**
     * Use for Dagger Injection.
     * @return an object to inject a class
     */
    public OsmTemplateComponent getOsmTemplateComponent() {
        return osmTemplateComponent;
    }

    /**
     * Get Flickr Helper for API request.
     * @return flickr object with API key set
     */
    public Flickr getFlickr() {
        return flickr;
    }

    public String getMapStyle() {
        return mapStyle;
    }


}
