package com.tckr.sodkiller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple DAO layer to interact with some persistent data we have ont he app
 */
public class DataStoreDAO {

    // Filename for the shared preferences
    private static final String FILENAME = "DataStore";

    // Keys that we are using
    public static final String ENABLED = "enabled";

    // Create a global variable for the share preference
    private SharedPreferences sharedPreferences;

    /**
     * Constructor to set the share preference object.
     * @param context - the passed context
     */
    public DataStoreDAO(Context context) {
        sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    /**
     * Get the boolean for the key
     * @param key - the key
     * @return - the store value
     */
    public boolean getDataBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Insert the boolean value into the system
     * @param key - the key
     * @param value - the value to be stored
     * @return - true or false
     */
    public boolean putDataBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }


}
