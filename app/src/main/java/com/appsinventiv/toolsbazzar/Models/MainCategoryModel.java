package com.appsinventiv.toolsbazzar.Models;

/**
 * Created by AliAh on 26/11/2018.
 */

public class MainCategoryModel {
    String mainCategory,url;

    public MainCategoryModel(String mainCategory, String url) {
        this.mainCategory = mainCategory;
        this.url = url;
    }

    public MainCategoryModel() {
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
