package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.TrackOrder;
import com.appsinventiv.toolsbazzar.Activities.ViewInvoice;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;

import java.util.ArrayList;

/**
 * Created by AliAh on 25/06/2018.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderModel> itemList;
    OrderedProductsLayout adapter;
    ChangeOrderStatus changeOrderStatus;

    public OrdersAdapter(Context context, ArrayList<OrderModel> itemList, ChangeOrderStatus changeOrderStatus) {
        this.context = context;
        this.itemList = itemList;
        this.changeOrderStatus = changeOrderStatus;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_layout, parent, false);
        OrdersAdapter.ViewHolder viewHolder = new OrdersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        final OrderModel model = itemList.get(position);
        holder.order.setText("Order Status: " + model.getOrderStatus()
                + "\nOrder Number: " + model.getOrderId()
                + "\nTotal amount: " + SharedPrefs.getCurrencySymbol() + " "
                + String.format("%.2f", (model.getTotalPrice() * Float.parseFloat(SharedPrefs.getExchangeRate()))));
        holder.time.setText("Order Time: " + CommonUtils.getFormattedTime(model.getTime()));
        holder.date.setText("Date: " + CommonUtils.getFormattedDateOnly(model.getTime()));
        holder.serial.setText((position + 1) + ")");


        if (model.getOrderStatus().equalsIgnoreCase("pending")) {
            holder.cancelOrder.setVisibility(View.VISIBLE);
        } else {
            holder.cancelOrder.setVisibility(View.GONE);

        }

        if(model.getInvoicePath()!=null){
            holder.viewInvoice.setVisibility(View.VISIBLE);

        }else{
            holder.viewInvoice.setVisibility(View.GONE);
        }
        holder.viewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, ViewInvoice.class);
                i.putExtra("path",model.getInvoicePath());
                context.startActivity(i);
            }
        });



        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recycler_order_products.setLayoutManager(layoutManager);
        adapter = new OrderedProductsLayout(context, model.getCountModelArrayList());

        holder.recycler_order_products.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        holder.cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrderStatus.onCancelOrder(model);
            }
        });

        holder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, TrackOrder.class);
                i.putExtra("orderId", model.getOrderId());
                i.putExtra("orderStatus", model.getOrderStatus());
                if (model.getOrderStatus().equalsIgnoreCase("delivered")) {
//                    model.deli
                }
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial, order, time, date, viewInvoice;
        RecyclerView recycler_order_products;
        ImageView cancelOrder;
        Button track;

        public ViewHolder(View itemView) {
            super(itemView);
            serial = itemView.findViewById(R.id.serial);
            recycler_order_products = itemView.findViewById(R.id.recycler_order_products);
            order = itemView.findViewById(R.id.order);
            time = itemView.findViewById(R.id.time);
            cancelOrder = itemView.findViewById(R.id.cancelOrder);
            date = itemView.findViewById(R.id.date);
            track = itemView.findViewById(R.id.track);
            viewInvoice = itemView.findViewById(R.id.viewInvoice);
        }
    }

    public interface ChangeOrderStatus {
        public void onCancelOrder(OrderModel product);
    }
}
