package com.appsinventiv.toolsbazzar.Utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by AliAh on 01/03/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    DatabaseReference mDatabase;
    Context context = this;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        SharedPrefs.setFcmKey(refreshedToken);
    }



}
