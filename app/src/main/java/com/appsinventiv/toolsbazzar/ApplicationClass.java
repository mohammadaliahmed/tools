package com.appsinventiv.toolsbazzar;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SampleLifecycleListener;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by AliAh on 11/04/2018.
 */

public class ApplicationClass extends Application {
    private static ApplicationClass instance;
    DatabaseReference mDatabase;

    public static ApplicationClass getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new SampleLifecycleListener());
//        sendUserSatus(true);

    }


}
