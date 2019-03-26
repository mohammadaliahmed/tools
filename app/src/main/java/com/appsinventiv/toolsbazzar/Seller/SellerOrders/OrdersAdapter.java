package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;


import java.util.ArrayList;

/**
 * Created by AliAh on 30/06/2018.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderModel> itemList;
    UpdateOrderStatus updateOrderStatus;


    public OrdersAdapter(Context context, ArrayList<OrderModel> itemList, UpdateOrderStatus updateOrderStatus) {
        this.context = context;
        this.itemList = itemList;
        this.updateOrderStatus = updateOrderStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        OrdersAdapter.ViewHolder viewHolder = new OrdersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final OrderModel model = itemList.get(position);


        if (model.getOrderStatus().equalsIgnoreCase("Cancelled")
                || model.getOrderStatus().equalsIgnoreCase("Pending")) {
            holder.cancel.setVisibility(View.VISIBLE);
        } else {
            holder.cancel.setVisibility(View.GONE);
        }


        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getOrderStatus().equalsIgnoreCase("Cancelled")) {
                    updateOrderStatus.markAsDeleted(model.getOrderId());
                } else if (model.getOrderStatus().equalsIgnoreCase("Pending")) {
                    updateOrderStatus.markAsCancelled(model.getOrderId());
                }
            }
        });
        if (model.getOrderStatus().equalsIgnoreCase("Pending")) {

            holder.checkbox.setVisibility(View.VISIBLE);

        } else {
            holder.checkbox.setVisibility(View.GONE);
        }

        if (model != null) {
            holder.orderDetails.setText(
                    "Order No: " + model.getOrderId()
                            + "\n\nOrder Time: " + CommonUtils.getFormattedDate(model.getTime())
                            + "\n\nOrder Status: " + model.getOrderStatus()
                            + "\n\nOrder Items: " + model.getCountModelArrayList().size()
                            + "\n\nOrder Amount: Rs." + CommonUtils.getFormattedPrice(model.getTotalPrice())
            );
            holder.userDetails.setText("Name: " + model.getCustomer().getName()
                    + "\n\nPhone: " + model.getCustomer().getPhone()
                    + "\n\nAddress: " + model.getCustomer().getAddress() + ", " + model.getCustomer().getCity() + ", " + model.getCustomer().getCountry()
                    + "\n\nType: " + model.getCustomer().getCustomerType()


            );
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = null;

                    if (model.getOrderStatus().equalsIgnoreCase("Under Process")) {
                        i = new Intent(context, ViewUnderProcessOrder.class);

                    } else if (model.getOrderStatus().equalsIgnoreCase("Shipped")) {
                        i = new Intent(context, ViewDeliveryShippedOrder.class);

                    } else {
                        i = new Intent(context, ViewOrder.class);

                    }
                    i.putExtra("orderId", model.getOrderId());
                    context.startActivity(i);
                }
            });

        }
        holder.phone_dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getCustomer().getPhone()));
                context.startActivity(i);
            }
        });
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    updateOrderStatus.markAsProcessing(model.getOrderId());
                } else {

                }
            }
        });
        if (!model.getOrderStatus().equalsIgnoreCase("refused")) {
            if (model.getCarrier() != null) {
                holder.shippingInfo.setVisibility(View.VISIBLE);
                holder.shippingInfo.setText("Carrier: " + model.getCarrier() + "\nTracking: " + model.getTrackingNumber());
            } else if (model.getDeliveryBy() != null) {
                holder.shippingInfo.setVisibility(View.VISIBLE);
                holder.shippingInfo.setText("Delivery by: " + model.getDeliveryBy());
            } else {
                holder.shippingInfo.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userDetails, orderDetails, shippingInfo;
        ImageView phone_dial, cancel;
        CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            userDetails = itemView.findViewById(R.id.userDetails);
            orderDetails = itemView.findViewById(R.id.orderDetails);
            phone_dial = itemView.findViewById(R.id.phone_dial);
            checkbox = itemView.findViewById(R.id.checkbox);
            cancel = itemView.findViewById(R.id.cancel);
            shippingInfo = itemView.findViewById(R.id.shippingInfo);
        }
    }

    public interface UpdateOrderStatus {
        public void markAsProcessing(String orderId);

        public void markAsCancelled(String orderId);

        public void markAsDeleted(String orderId);
    }
}
