package com.tckr.sodkiller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivityFragment extends Fragment {

    Context context;
    DataStoreDAO dao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        dao = new DataStoreDAO(getActivity());

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Set the toggle button and set all the listeners
        boolean isEnabled = dao.getDataBoolean(DataStoreDAO.ENABLED);
        Switch mainEnable = (Switch) view.findViewById(R.id.main_enable);
        mainEnable.setChecked(isEnabled);
        mainEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Enable and disable part of the code
                if (isChecked) {
                    startKeepAlive();
                } else {
                    stopKeepAlive();
                }
            }
        });

        // Run when the Fragment is about to return
        if (isEnabled) {
            startKeepAlive();
        } else {
            stopKeepAlive();
        }

        boolean isForeground = dao.getDataBoolean(DataStoreDAO.FOREGROUND);
        Switch mainForeground = (Switch) view.findViewById(R.id.main_foreground);
        mainForeground.setChecked(isForeground);
        mainForeground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setForegroundService(isChecked);
            }
        });

        TextView aboutMsg = (TextView) view.findViewById(R.id.textViewMsg);
        aboutMsg.setMovementMethod(LinkMovementMethod.getInstance());
        aboutMsg.setText(Html.fromHtml(getString(R.string.description)));

        return view;
    }

    /**
     * Start keep alive
     */
    public void startKeepAlive() {
        dao.putDataBoolean(DataStoreDAO.ENABLED, true);
        Intent notificationIntent = new Intent(context, KeepAliveService.class);
        context.startService(notificationIntent);
    }

    /**
     * Used to stop the service
     */
    public void stopKeepAlive() {
        dao.putDataBoolean(DataStoreDAO.ENABLED, false);
        Intent notificationIntent = new Intent(context, KeepAliveService.class);
        context.stopService(notificationIntent);
    }

    /**
     * Used to start and stop the foreground service
     * @param enable
     *      true to set the service
     *      false to disable it
     */
    public void setForegroundService(boolean enable) {
        dao.putDataBoolean(DataStoreDAO.FOREGROUND, enable);
        Intent notificationIntent = new Intent(context, KeepAliveService.class);
        context.stopService(notificationIntent); // to remove wakelock
        context.startService(notificationIntent); // restart and apply foreground settings

        // Remove the service if keep alive is disabled
        boolean isKeepAliveEnabled = dao.getDataBoolean(DataStoreDAO.ENABLED);
        if (!isKeepAliveEnabled) {
            context.stopService(notificationIntent);
        }
    }

}
