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

        } else {

        }


//        holder.cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (model.getOrderStatus().equalsIgnoreCase("Cancelled")) {
//                    updateOrderStatus.markAsDeleted(model.getOrderId());
//                } else if (model.getOrderStatus().equalsIgnoreCase("Pending")) {
//                    updateOrderStatus.markAsCancelled(model.getOrderId());
//                }
//            }
//        });
        if (model.getOrderStatus().equalsIgnoreCase("Pending")) {

            holder.checkbox.setVisibility(View.VISIBLE);

        } else {
            holder.checkbox.setVisibility(View.GONE);
        }

        if (model != null) {
            holder.orderNumber.setText(model.getOrderId());
            holder.orderItems.setText("" + model.getCountModelArrayList().size());
            holder.orderAmount.setText(CommonUtils.getFormattedPrice(model.getTotalPrice()));
            holder.orderStatus.setText(model.getOrderStatus());
            holder.orderTime.setText(CommonUtils.getFormattedDate(model.getTime()));
            holder.customerName.setText(model.getCustomer().getName());
            holder.paymentMethod.setText("Not Available");
            holder.customerType.setText("Customer type: " + model.getCustomer().getCustomerType());
//            holder.address.setText(model.getCustomer().getAddress() + ", " + model.getCustomer().getCity() + ", " + model.getCustomer().getCountry());

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
                holder.orderTracking.setVisibility(View.VISIBLE);
                holder.orderTracking.setText("Carrier: " + model.getCarrier() + "\nTracking: " + model.getTrackingNumber());
            } else if (model.getDeliveryBy() != null) {
                holder.orderTracking.setVisibility(View.VISIBLE);
                holder.orderTracking.setText("Delivery by: " + model.getDeliveryBy());
            } else {
                holder.orderTracking.setText("No Record");
            }
        }
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderTime, customerName, address, orderNumber, orderItems, orderAmount, paymentMethod, orderStatus, orderTracking, customerType;
        ImageView phone_dial;
        CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customerName);
            address = itemView.findViewById(R.id.address);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            orderItems = itemView.findViewById(R.id.orderItems);
            orderAmount = itemView.findViewById(R.id.orderAmount);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderTracking = itemView.findViewById(R.id.orderTracking);
            customerType = itemView.findViewById(R.id.customerType);
            orderTime = itemView.findViewById(R.id.orderTime);
            phone_dial = itemView.findViewById(R.id.phone_dial);
            checkbox = itemView.findViewById(R.id.checkbox);

        }
    }


    public interface UpdateOrderStatus {
        public void markAsProcessing(String orderId);

        public void markAsCancelled(String orderId);

        public void markAsDeleted(String orderId);
    }
}
