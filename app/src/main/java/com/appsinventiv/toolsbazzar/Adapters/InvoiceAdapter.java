package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.appsinventiv.toolsbazzar.Models.LocationAndChargesModel;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by AliAh on 04/09/2018.
 */

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductCountModel> list1;
    ArrayList<ProductCountModel> list2;
    String customerType;
    LocationAndChargesModel locationAndChargesModel;

    public InvoiceAdapter(Context context,
                          ArrayList<ProductCountModel> list1,
                          ArrayList<ProductCountModel> list2,
                          String customerType
            , LocationAndChargesModel locationAndChargesModel
    ) {
        this.context = context;
        this.list1 = list1;
        this.list2 = list2;
        this.customerType = customerType;
        this.locationAndChargesModel = locationAndChargesModel;
    }

    public void location(LocationAndChargesModel locationAndChargesModel) {
        this.locationAndChargesModel = locationAndChargesModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_item_layout, parent, false);
        InvoiceAdapter.ViewHolder viewHolder = new InvoiceAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCountModel model = list1.get(position);
        holder.position.setText("" + (position + 1) + ". ");
        holder.title.setText(model.getProduct().getTitle());
        holder.subtitle.setText("Description: " + model.getProduct().getSubtitle() + "\nQuantity: " + model.getQuantity());
        if (model.getSize() != null) {
            holder.size.setText("Size: " + model.getSize());
        } else {

        }
        if (model.getColor() != null) {
            holder.color.setText("Color: " + model.getColor());
        } else {

        }

        if (locationAndChargesModel != null) {
            if (customerType.equalsIgnoreCase("wholesale")) {
                holder.price.setText("Unit price: Rs " + CommonUtils.getFormattedPrice(model.getProduct().getWholeSalePrice()));
                if (list2 != null) {
                    if (list2.contains(model)) {
                        holder.outOfStockText.setVisibility(View.GONE);
                        holder.totalItemPrice.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        holder.totalItemPrice.setText(locationAndChargesModel.getCurrency() + (CommonUtils.getFormattedPrice(model.getProduct().getWholeSalePrice() * model.getQuantity() * locationAndChargesModel.getCurrencyRate())));

                    } else {
                        holder.outOfStockText.setVisibility(View.VISIBLE);
                        holder.totalItemPrice.setTextColor(context.getResources().getColor(R.color.colorRed));
                        holder.totalItemPrice.setText("-" + locationAndChargesModel.getCurrency() + CommonUtils.getFormattedPrice(model.getProduct().getWholeSalePrice() * model.getQuantity() * locationAndChargesModel.getCurrencyRate()));
                    }
                }


            } else if (customerType.equalsIgnoreCase("retail")) {
                holder.price.setText("Unit price: " + locationAndChargesModel.getCurrency() + CommonUtils.getFormattedPrice(model.getProduct().getRetailPrice() * locationAndChargesModel.getCurrencyRate()));

                if (list2 != null) {
                    if (list2.contains(model)) {
                        holder.outOfStockText.setVisibility(View.GONE);
                        holder.totalItemPrice.setText(locationAndChargesModel.getCurrency() + CommonUtils.getFormattedPrice(model.getProduct().getRetailPrice() * model.getQuantity() * locationAndChargesModel.getCurrencyRate()));

                    } else {
                        holder.outOfStockText.setVisibility(View.VISIBLE);
                        holder.totalItemPrice.setTextColor(context.getResources().getColor(R.color.colorRed));

                        holder.totalItemPrice.setText("-" + locationAndChargesModel.getCurrency() + CommonUtils.getFormattedPrice(model.getProduct().getRetailPrice() * model.getQuantity() * locationAndChargesModel.getCurrencyRate()));
                    }
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return list1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView position, title, subtitle, price, outOfStockText, totalItemPrice, size, color;

        public ViewHolder(View itemView) {
            super(itemView);
            position = itemView.findViewById(R.id.position);

            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            price = itemView.findViewById(R.id.price);
            outOfStockText = itemView.findViewById(R.id.outOfStock);
            totalItemPrice = itemView.findViewById(R.id.totalItemPrice);
            size = itemView.findViewById(R.id.size);
            color = itemView.findViewById(R.id.color);


        }
    }
}
