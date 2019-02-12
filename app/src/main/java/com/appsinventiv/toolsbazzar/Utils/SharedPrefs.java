package com.appsinventiv.toolsbazzar.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.appsinventiv.toolsbazzar.ApplicationClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }

    public static String getUsername() {
        return preferenceGetter("username");
    }

    public static void setUsername(String username) {
        preferenceSetter("username", username);
    }

    public static String getLocationId() {
        return preferenceGetter("LocationId");
    }

    public static void setLocationId(String username) {
        preferenceSetter("LocationId", username);
    }


    public static String getExchangeRate() {
        return preferenceGetter("ExchangeRate");
    }

    public static void setExchangeRate(String username) {
        preferenceSetter("ExchangeRate", username);
    }

    public static String getOneKgRate() {
        return preferenceGetter("oneKg");
    }

    public static void setOneKgRate(String username) {
        preferenceSetter("oneKg", username);
    }

    public static String getHalfKgRate() {
        return preferenceGetter("halfKg");
    }

    public static void setHalfKgRate(String username) {
        preferenceSetter("halfKg", username);
    }


    public static String getCurrencySymbol() {
        return preferenceGetter("CurrencySymbol");
    }

    public static void setCurrencySymbol(String username) {
        preferenceSetter("CurrencySymbol", username);
    }


    public static String getCustomerType() {
        return preferenceGetter("customerType");
    }

    public static void setCustomerType(String username) {
        preferenceSetter("customerType", username);
    }


    public static void setCartCount(String count) {
        preferenceSetter("cartCount", count);
    }

    public static String getCartCount() {
        return preferenceGetter("cartCount");
    }


    public static void setName(String value) {

        preferenceSetter("name", value);
    }

    public static String getName() {
        return preferenceGetter("name");
    }


    public static void setCity(String value) {

        preferenceSetter("city", value);
    }

    public static String getCity() {
        return preferenceGetter("city");
    }

    public static void setCountry(String value) {

        preferenceSetter("country", value);
    }

    public static String getCountry() {
        return preferenceGetter("country");
    }


    public static void setIsLoggedIn(String value) {

        preferenceSetter("isLoggedIn", value);
    }

    public static String getIsLoggedIn() {
        return preferenceGetter("isLoggedIn");
    }


    public static void setFcmKey(String fcmKey) {
        preferenceSetter("fcmKey", fcmKey);
    }

    public static String getFcmKey() {
        return preferenceGetter("fcmKey");
    }


    public static void saveArrayList(ArrayList<String> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationClass.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationClass.getInstance().getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }
}
