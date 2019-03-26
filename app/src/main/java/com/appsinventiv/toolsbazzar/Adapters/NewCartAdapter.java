package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ViewProduct;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.NewCartModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by AliAh on 22/06/2018.
 */

public class NewCartAdapter extends RecyclerView.Adapter<NewCartAdapter.ViewHolder> {
    Context context;
    AddToCartInterface addToCartInterface;
    ArrayList<NewCartModel> userCartProductList;


    public NewCartAdapter(Context context, ArrayList<NewCartModel> userCartProductList, AddToCartInterface addToCartInterface) {
        this.context = context;
        this.addToCartInterface = addToCartInterface;
        this.userCartProductList = userCartProductList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_cart_product_item_layout, parent, false);
        NewCartAdapter.ViewHolder viewHolder = new NewCartAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final NewCartModel model = userCartProductList.get(position);
        holder.recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        holder.vendorName.setText("By: " + model.getName());
        holder.deliveryCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", (model.getDeliveryCharges() * Float.parseFloat(SharedPrefs.getExchangeRate()))));
        holder.shippingCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", (model.getShippingCharges() * Float.parseFloat(SharedPrefs.getExchangeRate()))));
        holder.total.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", (model.getTotal() * Float.parseFloat(SharedPrefs.getExchangeRate()))));
        final CartAdapter adapter = new CartAdapter(context, model.getList(), new AddToCartInterface() {
            @Override
            public void addedToCart(Product product, int quantity, int position) {

            }

            @Override
            public void deletedFromCart(Product product, int position) {
                addToCartInterface.deletedFromCart(product, position);
            }

            @Override
            public void quantityUpdate(Product product, int quantity, int position) {

                addToCartInterface.quantityUpdate(product, quantity, position);
            }

            @Override
            public void isProductLiked(Product product, boolean isLiked, int position) {

            }
        });
        holder.recyclerview.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return userCartProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerview;
        TextView vendorName, shippingCharges, deliveryCharges, total;

        public ViewHolder(View itemView) {
            super(itemView);
            recyclerview = itemView.findViewById(R.id.recyclerview);
            vendorName = itemView.findViewById(R.id.vendorName);
            shippingCharges = itemView.findViewById(R.id.shippingCharges);
            deliveryCharges = itemView.findViewById(R.id.deliveryCharges);
            total = itemView.findViewById(R.id.total);


        }
    }
}
