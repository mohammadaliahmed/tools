package com.appsinventiv.toolsbazzar.Utils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;

import java.util.Calendar;

public class SampleLifecycleListener implements LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        CommonUtils.sendCustomerStatus(true);

//        CommonUtils.showToast("start");
//        String getUserId = MyApp.getInstance().UserId;
//        if (getUserId != null && !getUserId.equals("")) {
//            if (MyApp.getInstance().myProfile != null && MyApp.getInstance().myProfile.profileId != null && MyApp.getInstance().myProfile.profileId.equalsIgnoreCase(getUserId)) {
//                MyApp.getInstance().myProfile.online = "online";
//                MyApp.getInstance().myProfile.lastactivetime = Calendar.getInstance().getTimeInMillis();
//                Intent otpIntent = new Intent(MyApp.getInstance().getApplicationContext(), UpdateprofileService.class);
//                MyApp.getInstance().getApplicationContext().startService(otpIntent);
//
//
//            }
//        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onDestroy() {

        CommonUtils.sendCustomerStatus(false);

//        String getUserId = MyApp.getInstance().UserId;
//        if (getUserId != null && !getUserId.equals("")) {
//            if (MyApp.getInstance().myProfile != null && MyApp.getInstance().myProfile.profileId != null && MyApp.getInstance().myProfile.profileId.equalsIgnoreCase(getUserId)) {
//                MyApp.getInstance().myProfile.online = "offline";
//                MyApp.getInstance().myProfile.lastactivetime = Calendar.getInstance().getTimeInMillis();
//                Intent otpIntent = new Intent(MyApp.getInstance().getApplicationContext(), UpdateprofileService.class);
//                MyApp.getInstance().getApplicationContext().startService(otpIntent);
//
//
//            }
//
//        }
    }
}