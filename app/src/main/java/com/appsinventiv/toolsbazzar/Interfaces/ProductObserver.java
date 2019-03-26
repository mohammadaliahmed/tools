package com.appsinventiv.toolsbazzar.Interfaces;

/**
 * Created by AliAh on 08/04/2018.
 */

public interface ProductObserver {
    public void onUploaded(int count, int arraySize);
    public void putThumbnailUrl(int count, String url);
}
