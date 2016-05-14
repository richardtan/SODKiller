package com.tckr.sodkiller;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Service will just loop and loop and never die
 */
public class KeepAliveService extends IntentService {

    public static final String TAG = "KeepAliveService";
    public static final int NOTIFICATION_ID = 21;

    // Get wake lock
    PowerManager pm;
    PowerManager.WakeLock wl;

    public KeepAliveService() {
        super(TAG);
        setIntentRedelivery(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    /**
     * Method used to start the foreground service
     */
    private void startForegroundService() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(pendingIntent);

        // Notification Priority note really available in ICS
        if (Build.VERSION.SDK_INT >= 16) {
            builder.setPriority(Notification.PRIORITY_MIN);
        }

        Notification notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Method used to stop the foreground service
     */
    public void stopForegroundService() {
        stopForeground(true);
    }

    /**
     * Loop until the user has disabled you. Then release the wake lock.
     * @param intent - from calling program
     */
    @Override
    public void onHandleIntent(Intent intent) {

        DataStoreDAO dao = new DataStoreDAO(this);
        boolean isEnabled = dao.getDataBoolean(DataStoreDAO.ENABLED);
        boolean isForegroundSet = dao.getDataBoolean(DataStoreDAO.FOREGROUND);

        // Set it as a foreground service if enabled
        if (isForegroundSet) {
            startForegroundService();
        }

        pm = (PowerManager) getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();
        Log.i(TAG, "Wakelock acquired!");

        while (isEnabled) {
            try {
                Log.i(TAG, "Sleeping again - Waking up in 30 minutes :)");
                Thread.sleep(1000*60*30);
                isEnabled = dao.getDataBoolean(DataStoreDAO.ENABLED);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Release the wake lock
        Log.i(TAG, "We are exiting wakelock and destroying the service");
        try {
            wl.release();
        } catch (RuntimeException e) {
            Log.e(TAG, "onHandleIntent() - Something went wrong with release wakelock: " + e);
        }

        // Set it as a foreground service if enabled
        if (!isForegroundSet) {
            stopForeground(true);
        }

    }

    /**
     * When destroying, just make sure we release the wakelock
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // release the wakelock
        try {
            wl.release();
            Log.i(TAG, "Wakelock released!");
        } catch (RuntimeException e) {
            Log.e(TAG, "onDestroy() - Something went wrong with release wakelock: " + e);
        }
    }

}
