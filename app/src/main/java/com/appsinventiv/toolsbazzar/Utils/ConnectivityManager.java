package com.appsinventiv.toolsbazzar.Utils;

import android.content.Context;
import android.net.NetworkInfo;

/**
 * Created by AliAh on 05/03/2018.
 */

public class ConnectivityManager {
    public ConnectivityManager() {
    }

    public static boolean isNetworkConnected(Context context) {
        android.net.ConnectivityManager cm =
                (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
