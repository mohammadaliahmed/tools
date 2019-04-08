package com.appsinventiv.toolsbazzar.Utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.widget.Toast;

import android.net.ConnectivityManager;


import com.appsinventiv.toolsbazzar.ApplicationClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by AliAh on 14/05/2018.
 */

public class CommonUtils {


    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static void sendCustomerStatus(final boolean b) {
        if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
            if (SharedPrefs.getUsername() != null || !SharedPrefs.getUsername().equalsIgnoreCase("")) {
                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("isOnline").setValue(b).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        CommonUtils.showToast("" + b);
                    }
                });
            }
        }
    }

    public static void showToast(final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @SuppressLint("WrongConstant")
            public void run() {
                Toast.makeText(ApplicationClass.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String getFormattedPrice(Object price) {
        DecimalFormat formatter = new DecimalFormat("##,##,###");
        String formattedPrice = formatter.format(price);
        return formattedPrice;
    }

    public static String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd MMM ";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday ";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMM , h:mm aa", smsTime).toString();
        }
    }

    public static String getFormattedDateOnly(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        return DateFormat.format("dd-MMM-yyyy", smsTime).toString();

    }


    public static String getFormattedTime(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        return DateFormat.format("h:mm aa", smsTime).toString();

    }

    public static boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static double screenSize() {

        double size = 0;
        try {

            // Compute screen size

            DisplayMetrics dm = ApplicationClass.getInstance().getApplicationContext().getResources().getDisplayMetrics();

            float screenWidth = dm.widthPixels / dm.xdpi;

            float screenHeight = dm.heightPixels / dm.ydpi;

            size = Math.sqrt(Math.pow(screenWidth, 2) +

                    Math.pow(screenHeight, 2));

        } catch (Throwable t) {

        }

        return size;

    }


}
