package com.appsinventiv.toolsbazzar.Seller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.No_Internet;
import com.appsinventiv.toolsbazzar.Activities.ViewProduct;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

/**
 * Created by AliAh on 23/06/2018.
 */

public class SellerProductsAdapter extends RecyclerView.Adapter<SellerProductsAdapter.Viewholder> {
    Context context;
    ArrayList<Product> productList;
    private ArrayList<Product> arrayList = new ArrayList<>();


    public SellerProductsAdapter(Context context,
                                 ArrayList<Product> productList
    ) {
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @Override
    public SellerProductsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seller_product_item_layout, parent, false);
        SellerProductsAdapter.Viewholder viewHolder = new SellerProductsAdapter.Viewholder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SellerProductsAdapter.Viewholder holder, final int position) {
        final Product model = productList.get(position);

        holder.title.setText(model.getTitle());
        holder.count.setText(model.getSellerProductStatus());
//        if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
        holder.price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
        if (model.getOldRetailPrice() != 0) {
            holder.oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
        } else {
            holder.oldPrice.setText("");
        }
//        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
        holder.price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));

        if (model.getOldWholeSalePrice() != 0) {
            holder.oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
        } else {
            holder.oldPrice.setText("");
        }
//        }
        holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        holder.subtitle.setText(model.getSubtitle());
        Glide.with(context).load(model.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(holder.image);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {
                    Intent i = new Intent(context, SellerViewProduct.class);
                    i.putExtra("productId", model.getId());
                    context.startActivity(i);
                } else {
                    Intent i = new Intent(context, No_Internet.class);
                    context.startActivity(i);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, count, oldPrice;
        ImageView image, increase, decrease;
        RelativeLayout relativeLayout;
        LikeButton heart_button;

        public Viewholder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            count = itemView.findViewById(R.id.count);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            heart_button = itemView.findViewById(R.id.heart_button);
            oldPrice = itemView.findViewById(R.id.oldPrice);

        }
    }
}
