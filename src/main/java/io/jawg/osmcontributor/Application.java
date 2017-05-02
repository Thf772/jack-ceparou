package io.jawg.osmcontributor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Flo Macé on 01/05/2017.
 */

public final class Application extends android.app.Application {


    public final static int NOTIFICATION_ID = 001;

    private static int mInterval;
    private static Context context;
    private static boolean syncEnable;
    private static boolean notificationEnable;
    private SharedPreferences prefs;
    private static Handler mHandler;
    private static boolean vibrationAllow;
    private static NotificationCompat.Builder notification;
    private PendingIntent notificationClickEffect;

    @Override
    public void onCreate() {
        super.onCreate();
        Application.context = getApplicationContext();

        createNotification();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("dyslexy", false)){
            setFont("fonts/OpenDyslexic3-Regular.ttf");
        }else
        {
            setFont("fonts/arial.ttf");
        }

        vibrationAllow = prefs.getBoolean("notifications_new_message_vibrate", false);

        notificationEnable = prefs.getBoolean("notifications_new_report", false);

        syncEnable = prefs.getBoolean("enable_sync", false);
        if(syncEnable)
        {
            mHandler = new Handler();
            mInterval = Integer.valueOf(prefs.getString("sync_frequency", "300000"));
            startRepeatingTask();
        }
    }

    private void createNotification(){
        notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.accessibility_black_24x24)
                        .setContentTitle(getResources().getString(R.string.notification_message))
                        .setContentText(getResources().getString(R.string.notification_message_values));

        notification.setAutoCancel(true);
        Intent resultIntent = new Intent(this, MapActivity.class);

        notificationClickEffect =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        notification.setContentIntent(notificationClickEffect);
    }

    public static void setVibrationAllow(boolean vAllow){
        vibrationAllow = vAllow;
        notificationVibration();
    }

    public static void setmInterval(int freq){
        mInterval = freq;
    }

    public static void setNotificationOn(boolean notifOn){
        notificationEnable = notifOn;
    }

    public static void setFont(String fontName)
    {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(fontName)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    public static void init(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = sharedPref.getString("font_size", "normal");
        switch(fontSize){
            case "small" :
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
        if (syncEnable){
            stopRepeatingTask();
        }
    }

    static Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                //Application.showToast("Données mises à jour");
                 if(notificationEnable) {
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

    public static Context getContext() {
        return context;
    }

    public static void notification(){
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, notification.build());

        if(vibrationAllow) {
            notificationVibration();
        }
    }

    public static void notificationVibration(){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds

        long[] pattern = {100, 100};
        v.vibrate(200);
    }

    public static void showToast(String data) {
        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
    }

    public static void startRepeatingTask() {
        mStatusChecker.run();
        Application.showToast("Synchronisation activé");
        syncEnable = true;
    }

    public static void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
        Application.showToast("Synchronisation désactivé");
        syncEnable = false;
    }

}

