package com.appsinventiv.toolsbazzar.ListOfStrores;

import com.appsinventiv.toolsbazzar.Models.VendorModel;

public interface StoreCallbacks {
    public  void followStore(VendorModel vendorModel);
    public void unFollowStore(VendorModel vendorModel);
}
