package com.appsinventiv.toolsbazzar.Models;

/**
 * Created by AliAh on 26/11/2018.
 */

public class MainCategoryModel {
    String mainCategory,url,subCategories;

    public MainCategoryModel(String mainCategory, String url, String subCategories) {
        this.mainCategory = mainCategory;
        this.url = url;
        this.subCategories = subCategories;
    }

    public String getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(String subCategories) {
        this.subCategories = subCategories;
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
