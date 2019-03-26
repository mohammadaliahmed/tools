package com.appsinventiv.toolsbazzar.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by M Ali Ahmed on 9/22/2017.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FIRST_TIME_LAUNCH_SELLER = "IsFirstTimeLaunchSeller";

    private static final String PREF_NAME_WELCOME = "tools_welcome";

    private static final String IS_FIRST_TIME_LAUNCH_WELCOME = "IsFirstTimeLaunchWelcome";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setFirstTimeLaunchSeller(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH_SELLER, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isFirstTimeLaunchSeller() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH_SELLER, true);
    }


    public void setIsFirstTimeLaunchWelcome(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH_WELCOME, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunchWelcome() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH_WELCOME, true);
    }

}
