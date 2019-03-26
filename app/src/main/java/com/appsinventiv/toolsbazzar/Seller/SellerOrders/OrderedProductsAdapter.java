package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.ProductCountModel;

import com.appsinventiv.toolsbazzar.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 30/06/2018.
 */

public class OrderedProductsAdapter extends RecyclerView.Adapter<OrderedProductsAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductCountModel> productList;
    String customerType;
    OnProductSelected onProductSelected;
    int flag;
    int abc;
    public OrderedProductsAdapter(Context context, ArrayList<ProductCountModel> productList, String customerType, int flag
            , OnProductSelected onProductSelected,int abc
    ) {
        this.context = context;
        this.productList = productList;
        this.customerType = customerType;
        this.onProductSelected = onProductSelected;
        this.flag = flag;
        this.abc=abc;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ordered_product_layout, parent, false);
        OrderedProductsAdapter.ViewHolder viewHolder = new OrderedProductsAdapter.ViewHolder(view);
        return viewHolder;
    }


    public void selectAll(int abc){
        this.abc=abc;
       notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ProductCountModel model = productList.get(position);

        holder.title.setText(model.getProduct().getTitle());


        if (customerType.equalsIgnoreCase("wholesale")) {
            holder.price.setText("Rs. " + model.getProduct().getWholeSalePrice());

        } else if (customerType.equalsIgnoreCase("retail")) {
            holder.price.setText("Rs. " + model.getProduct().getRetailPrice());

        }
        holder.subtitle.setText(model.getProduct().getMeasurement());
        holder.count.setText("Item Qty: " + model.getQuantity());
        holder.serial.setText("" + (position + 1) + ")");
        holder.vendor.setText("Vendor: " + model.getProduct().getVendor().getVendorName());
        holder.productSku.setText("SKU: " + model.getProduct().getSku());
        if (model.getColor() != null) {
            if (!model.getColor().equalsIgnoreCase("")) {
                holder.color.setVisibility(View.VISIBLE);
                holder.color.setText("Color: " + model.getColor());
            }
        }
        if (model.getSize() != null) {
            if (!model.getSize().equalsIgnoreCase("")) {
                holder.size.setVisibility(View.VISIBLE);

                holder.size.setText("Size: " + model.getSize());
            }
        }

        Glide.with(context).load(model.getProduct().getThumbnailUrl()).into(holder.image);
        if(abc==1){
            holder.checkBox.setChecked(true);

        }else{
            holder.checkBox.setChecked(false);

        }
        if (flag == 1) {
//            if (model.getIsSelected() == 1) {
//                holder.checkBox.setChecked(true);
//            } else {
//                holder.checkBox.setChecked(false);
//            }
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    if (b) {
//                        model.setIsSelected(1);
//                        onProductSelected.onChecked(model, position);
//                    } else {
//                        model.setIsSelected(0);
//                        onProductSelected.onUnChecked(model, position);
//                    }
                }
            });
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, count, serial, vendor, productSku, color, size;
        CheckBox checkBox;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            price = itemView.findViewById(R.id.costPrice);
            image = itemView.findViewById(R.id.image);
            count = itemView.findViewById(R.id.quantity);
            checkBox = itemView.findViewById(R.id.checkbox);
            serial = itemView.findViewById(R.id.count);
            vendor = itemView.findViewById(R.id.vendor);
            productSku = itemView.findViewById(R.id.productSku);
            color = itemView.findViewById(R.id.color);
            size = itemView.findViewById(R.id.size);


        }
    }

    public interface OnProductSelected {
        public void onChecked(ProductCountModel product, int position);

        public void onUnChecked(ProductCountModel product, int position);
    }
}
