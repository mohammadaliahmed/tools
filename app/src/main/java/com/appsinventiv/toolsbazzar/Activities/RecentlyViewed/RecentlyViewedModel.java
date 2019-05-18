package com.appsinventiv.toolsbazzar.Activities.RecentlyViewed;

import com.appsinventiv.toolsbazzar.Models.Product;

public class RecentlyViewedModel {
    String productId;
    long time;

    public RecentlyViewedModel() {
    }

    public RecentlyViewedModel(String productId, long time) {
        this.productId = productId;
        this.time = time;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
