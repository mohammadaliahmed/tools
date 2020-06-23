package com.appsinventiv.toolsbazzar.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AliAh on 20/06/2018.
 */

public class Product {
    String id, title, subtitle, thumbnailUrl, mainCategory, subCategory;
    long time;
    float costPrice, wholeSalePrice, retailPrice;
    int minOrderQuantity;
    String measurement;
    VendorModel vendor;
    int salesCount, likesCount;
    int sku;

    String sellingTo;
    String description;
    List<String> colorList;
    List<String> sizeList;
    float oldWholeSalePrice, oldRetailPrice;
    float rating;
    ArrayList<String> category;
    int quantityAvailable;
    String brandName, productContents, warrantyType, productWeight;
    String dimen;
    String sellerProductStatus;
    ArrayList<String> pictures;
    String uploadedBy;
    int ratingCount;
    int positiveCount,neutralCount,negativeCount;
    String rejectReason;
    boolean active;

    HashMap<String,String> attributesWithPics;

    String warrantyPeriod, warrantyPolicy, dangerousGood;
    HashMap<String, Object> productAttributes;

    HashMap<String,String> colorSizeMap;
    HashMap<String,ArrayList<NewProductModel>> productCountHashmap;
    String productModel;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        if (id != null && product.id != null) {
            if (id.equalsIgnoreCase(product.id)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;

        }
//        return id != null ? !id.equals(product.id) : product.id != null;
    }

    public HashMap<String, String> getColorSizeMap() {
        return colorSizeMap;
    }

    public void setColorSizeMap(HashMap<String, String> colorSizeMap) {
        this.colorSizeMap = colorSizeMap;
    }

    public HashMap<String, ArrayList<NewProductModel>> getProductCountHashmap() {
        return productCountHashmap;
    }

    public void setProductCountHashmap(HashMap<String, ArrayList<NewProductModel>> productCountHashmap) {
        this.productCountHashmap = productCountHashmap;
    }

    public HashMap<String, String> getAttributesWithPics() {
        return attributesWithPics;
    }

    public void setAttributesWithPics(HashMap<String, String> attributesWithPics) {
        this.attributesWithPics = attributesWithPics;
    }

    public String getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(String warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    public String getWarrantyPolicy() {
        return warrantyPolicy;
    }

    public void setWarrantyPolicy(String warrantyPolicy) {
        this.warrantyPolicy = warrantyPolicy;
    }

    public String getDangerousGood() {
        return dangerousGood;
    }

    public void setDangerousGood(String dangerousGood) {
        this.dangerousGood = dangerousGood;
    }



    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public int getPositiveCount() {
        return positiveCount;
    }

    public void setPositiveCount(int positiveCount) {
        this.positiveCount = positiveCount;
    }

    public int getNeutralCount() {
        return neutralCount;
    }

    public void setNeutralCount(int neutralCount) {
        this.neutralCount = neutralCount;
    }

    public int getNegativeCount() {
        return negativeCount;
    }

    public void setNegativeCount(int negativeCount) {
        this.negativeCount = negativeCount;
    }

//    public Product(String id, String title, String subtitle,
//                   int sku, String thumbnailUrl, String mainCategory, String subCategory,
//                   long time, float costPrice, float wholeSalePrice, float retailPrice,
//                   int minOrderQuantity, String measurement, VendorModel vendor, String sellingTo,
//                   String description,
//
//                   List<String> sizeList,
//                   List<String> colorList,
//                   float oldWholeSalePrice, float oldRetailPrice
//            , float rating,
//                   ArrayList<String> category, int quantityAvailable,
//                   String brandName, String productContents, String warrantyType, String productWeight,
//                   String dimen, String sellerProductStatus, String uploadedBy
//                   , int likesCount
//
//    ) {
//        this.id = id;
//        this.title = title;
//        this.subtitle = subtitle;
//        this.sku = sku;
//        this.thumbnailUrl = thumbnailUrl;
//        this.mainCategory = mainCategory;
//        this.subCategory = subCategory;
//        this.time = time;
//        this.costPrice = costPrice;
//        this.wholeSalePrice = wholeSalePrice;
//        this.retailPrice = retailPrice;
//        this.minOrderQuantity = minOrderQuantity;
//        this.measurement = measurement;
//        this.vendor = vendor;
//        this.sellingTo = sellingTo;
//        this.description = description;
//        this.sizeList = sizeList;
//        this.colorList = colorList;
//        this.oldRetailPrice = oldRetailPrice;
//        this.oldWholeSalePrice = oldWholeSalePrice;
//        this.rating = rating;
//        this.category = category;
//        this.quantityAvailable = quantityAvailable;
//        this.brandName = brandName;
//        this.productContents = productContents;
//        this.warrantyType = warrantyType;
//        this.productWeight = productWeight;
//        this.dimen = dimen;
//        this.sellerProductStatus = sellerProductStatus;
//        this.uploadedBy = uploadedBy;
//        this.likesCount=likesCount;
//    }

    public Product(String id, String title, String subtitle, boolean active,
                   int sku, String thumbnailUrl, String mainCategory, String subCategory,
                   long time, float costPrice, float wholeSalePrice, float retailPrice,
                   int minOrderQuantity, String measurement, VendorModel vendor, String sellingTo,
                   String description,

                   List<String> sizeList,
                   List<String> colorList,
                   float oldWholeSalePrice, float oldRetailPrice
            , float rating,
                   ArrayList<String> category, int quantityAvailable,
                   String brandName, String productContents, String warrantyType, String productWeight,
                   String dimen, String uploadedBy, String sellerProductStatus,
                   String warrantyPeriod, String warrantyPolicy, String dangerousGood, String productModel


    ) {
        this.warrantyPeriod = warrantyPeriod;
        this.warrantyPolicy = warrantyPolicy;
        this.dangerousGood = dangerousGood;
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.sku = sku;
        this.thumbnailUrl = thumbnailUrl;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.time = time;
        this.costPrice = costPrice;
        this.wholeSalePrice = wholeSalePrice;
        this.active = active;
        this.retailPrice = retailPrice;
        this.minOrderQuantity = minOrderQuantity;
        this.measurement = measurement;
        this.vendor = vendor;
        this.sellingTo = sellingTo;
        this.description = description;
        this.sizeList = sizeList;
        this.colorList = colorList;
        this.oldRetailPrice = oldRetailPrice;
        this.oldWholeSalePrice = oldWholeSalePrice;
        this.rating = rating;
        this.category = category;
        this.quantityAvailable = quantityAvailable;
        this.brandName = brandName;
        this.productContents = productContents;
        this.warrantyType = warrantyType;
        this.productWeight = productWeight;
        this.dimen = dimen;
        this.uploadedBy = uploadedBy;
        this.sellerProductStatus = sellerProductStatus;
        this.productModel = productModel;
    }

    public HashMap<String, Object> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(HashMap<String, Object> productAttributes) {
        this.productAttributes = productAttributes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public String getSellerProductStatus() {
        return sellerProductStatus;
    }

    public void setSellerProductStatus(String sellerProductStatus) {
        this.sellerProductStatus = sellerProductStatus;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductContents() {
        return productContents;
    }

    public void setProductContents(String productContents) {
        this.productContents = productContents;
    }

    public String getWarrantyType() {
        return warrantyType;
    }

    public void setWarrantyType(String warrantyType) {
        this.warrantyType = warrantyType;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    public String getDimen() {
        return dimen;
    }

    public void setDimen(String dimen) {
        this.dimen = dimen;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    public Product() {

    }


    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public List<String> getSizeList() {
        return sizeList;
    }

    public void setSizeList(List<String> sizeList) {
        this.sizeList = sizeList;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getSellingTo() {
        return sellingTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getColorList() {
        return colorList;
    }


    public void setColorList(List<String> colorList) {
        this.colorList = colorList;
    }

    public float getOldWholeSalePrice() {
        return oldWholeSalePrice;
    }

    public void setOldWholeSalePrice(float oldWholeSalePrice) {
        this.oldWholeSalePrice = oldWholeSalePrice;
    }

    public float getOldRetailPrice() {
        return oldRetailPrice;
    }

    public void setOldRetailPrice(float oldRetailPrice) {
        this.oldRetailPrice = oldRetailPrice;
    }

    public void setSellingTo(String sellingTo) {
        this.sellingTo = sellingTo;
    }

    public VendorModel getVendor() {
        return vendor;
    }

    public void setVendor(VendorModel vendor) {
        this.vendor = vendor;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }



    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(float costPrice) {
        this.costPrice = costPrice;
    }

    public float getWholeSalePrice() {
        return wholeSalePrice;
    }

    public void setWholeSalePrice(float wholeSalePrice) {
        this.wholeSalePrice = wholeSalePrice;
    }

    public float getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(float retailPrice) {
        this.retailPrice = retailPrice;
    }

    public int getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(int minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }


    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }
}
