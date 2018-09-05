package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ViewProduct;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 25/06/2018.
 */

public class OrderedProductsLayout extends RecyclerView.Adapter<OrderedProductsLayout.ViewHolder> {
    Context context;
    ArrayList<ProductCountModel> itemList;

    public OrderedProductsLayout(Context context, ArrayList<ProductCountModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ordered_products_layout, parent, false);
        OrderedProductsLayout.ViewHolder viewHolder = new OrderedProductsLayout.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ProductCountModel model = itemList.get(position);
        holder.title.setText(model.getProduct().getTitle());

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            holder.price.setText("Rs. " + model.getProduct().getRetailPrice());
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            holder.price.setText("Rs. " + model.getProduct().getWholeSalePrice());
        }
        holder.subtitle.setText(model.getProduct().getMeasurement());
        holder.quantity.setText("Quantity: " + model.getQuantity());
        Glide.with(context).load(model.getProduct().getThumbnailUrl()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewProduct.class);
                i.putExtra("productId", model.getProduct().getId());
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, quantity;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);

            quantity = itemView.findViewById(R.id.quantity);


        }
    }
}
