package com.appsinventiv.toolsbazzar.Seller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {
    Context context;
    ArrayList<Product> productList;
    OnProductStatusChanged productStatusChanged;
    private ArrayList<Product> arrayList;


    public ProductsListAdapter(Context context, ArrayList<Product> productList, OnProductStatusChanged productStatusChanged) {
        this.context = context;
        this.productList = productList;
        this.productStatusChanged = productStatusChanged;
        this.arrayList = new ArrayList<>(productList);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seller_product_list_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Product model = productList.get(position);


        holder.title.setText(model.getTitle());
        holder.wholesalePrice.setText("Whole sale   Rs. " + model.getWholeSalePrice() + "\nRetail Price   Rs. " + model.getRetailPrice());
//        holder.retailPrice.setText("Rs. " + model.getRetailPrice()+"\nRetail Price");

        holder.subtitle.setText(model.getSubtitle());
        Glide.with(context).load(model.getThumbnailUrl()).into(holder.image);

        if (model.isActive()) {
            holder.switchh.setChecked(true);
        } else if (model.isActive()) {
            holder.switchh.setChecked(false);
        } else {
            holder.switchh.setChecked(false);
//            holder.switchh.setVisibility(View.GONE);
        }

        holder.switchh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    productStatusChanged.onStatusChanged(model, b);

                }
//                if (productStatusChanged != null) {
//                    model.setIsActive("" + b);
//                    productStatusChanged.onStatusChanged(compoundButton, model, b);
//
//                }

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, EditProduct.class);
                i.putExtra("productId", model.getId());
                Constants.PRODUCT_ID=model.getId();

                context.startActivity(i);
            }
        });


    }

    public void updatelist(ArrayList<Product> productList) {
        this.productList = productList;
        arrayList.clear();
        arrayList.addAll(productList);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        productList.clear();
        if (charText.length() == 0) {
            productList.addAll(arrayList);
        } else {
            for (Product product : arrayList) {
                if (product.getTitle().toLowerCase().contains(charText) ||
                        product.getSubtitle().toLowerCase().contains(charText) ||
                        product.getSubCategory().toLowerCase().contains(charText) ||
                        product.getMainCategory().toLowerCase().contains(charText)
                        ) {
                    productList.add(product);
                }
            }


        }
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, wholesalePrice, retailPrice;
        ImageView image;

        Switch switchh;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            wholesalePrice = itemView.findViewById(R.id.wholesalePrice);
            retailPrice = itemView.findViewById(R.id.retailPrice);

            image = itemView.findViewById(R.id.image);

            switchh = itemView.findViewById(R.id.switchh);


        }
    }

    public interface OnProductStatusChanged {
        public void onStatusChanged(Product product, boolean status);

        public void onDelete(Product product);
    }
}
