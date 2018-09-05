package com.appsinventiv.toolsbazzar;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by AliAh on 11/04/2018.
 */

public class ApplicationClass extends Application {
    private static  ApplicationClass instance;

    public static ApplicationClass getInstance(){
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }
}
