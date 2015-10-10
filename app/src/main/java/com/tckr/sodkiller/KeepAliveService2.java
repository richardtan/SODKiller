package com.tckr.sodkiller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

public class KeepAliveService2 extends Service {

    public static final String TAG = "KeepAliveService";

    // Get wake lock
    PowerManager pm;
    PowerManager.WakeLock wl;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DataStoreDAO dao = new DataStoreDAO(this);
        boolean isEnabled = dao.getDataBoolean(DataStoreDAO.ENABLED);

        pm = (PowerManager) getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();
        Log.i(TAG, "Wakelock acquired!");

        while (isEnabled) {
            try {
                Log.i(TAG, "Sleeping again - Waking up in 2 hours :)");
                Thread.sleep(1000*60*120);
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

        return START_STICKY;
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
