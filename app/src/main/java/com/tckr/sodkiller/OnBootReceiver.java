package com.tckr.sodkiller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Run on start up
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        DataStoreDAO dao = new DataStoreDAO(context);

        // Start only if we are enabled.
        if (dao.getDataBoolean(DataStoreDAO.ENABLED)) {

            // Start the service
            Intent notificationIntent = new Intent(context, KeepAliveService.class);
            context.startService(notificationIntent);

        }
    }
}
