package com.appsinventiv.toolsbazzar.ListOfStrores;

import com.appsinventiv.toolsbazzar.Models.VendorModel;

import java.util.ArrayList;

public class
StoreListModel {
    VendorModel seller;
    ArrayList<String> pictures;

    public StoreListModel(VendorModel seller, ArrayList<String> pictures) {
        this.seller = seller;
        this.pictures = pictures;
    }

    public VendorModel getSeller() {
        return seller;
    }

    public void setSeller(VendorModel seller) {
        this.seller = seller;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

}
