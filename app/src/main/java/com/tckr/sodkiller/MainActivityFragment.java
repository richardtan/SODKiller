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
import android.widget.TextView;
import android.widget.ToggleButton;

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
        ToggleButton mainToggle = (ToggleButton) view.findViewById(R.id.main_toggle);
        mainToggle.setChecked(isEnabled);
        mainToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

}
