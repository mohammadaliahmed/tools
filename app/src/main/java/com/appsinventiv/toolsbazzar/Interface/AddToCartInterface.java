package com.appsinventiv.toolsbazzar.Interface;

import com.appsinventiv.toolsbazzar.Models.Product;

/**
 * Created by AliAh on 22/06/2018.
 */

public interface AddToCartInterface {
    public void addedToCart(Product product,int quantity,int position);
    public void deletedFromCart(Product product,int position);
    public void quantityUpdate(Product product,int quantity,int position);
    public void isProductLiked(Product product,boolean isLiked,int position);

}
