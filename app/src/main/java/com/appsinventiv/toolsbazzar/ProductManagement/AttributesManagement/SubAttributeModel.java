package com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement;

/**
 * Created by AliAh on 26/11/2018.
 */

public class SubAttributeModel {
    String mainCategory, url, selection;

    public SubAttributeModel(String mainCategory, String url, String selection) {
        this.mainCategory = mainCategory;
        this.url = url;
        this.selection = selection;
    }

    public SubAttributeModel() {
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
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
