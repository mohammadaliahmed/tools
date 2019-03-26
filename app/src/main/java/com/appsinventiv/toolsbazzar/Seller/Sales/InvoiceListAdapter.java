package com.appsinventiv.toolsbazzar.Seller.Sales;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ViewInvoice;
import com.appsinventiv.toolsbazzar.Models.InvoiceModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;


import java.util.ArrayList;

/**
 * Created by AliAh on 12/09/2018.
 */

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ViewHolder> {
    Context context;
    ArrayList<InvoiceModel> itemList;
    int showCheckbox;
    String from;
    SelectInvoices selectInvoices;

    public InvoiceListAdapter(Context context, ArrayList<InvoiceModel> itemList, int showCheckbox, String from, SelectInvoices selectInvoices) {
        this.context = context;
        this.itemList = itemList;
        this.showCheckbox = showCheckbox;
        this.selectInvoices = selectInvoices;

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_item_list_layout, parent, false);
        InvoiceListAdapter.ViewHolder viewHolder = new InvoiceListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final InvoiceModel model = itemList.get(position);
        holder.date.setText(CommonUtils.getFormattedDateOnly(model.getTime()));
        holder.invoiceNumber.setText("" + model.getId());
        holder.invoiceTotal.setText("Rs " + CommonUtils.getFormattedPrice(model.getGrandTotal()));
        holder.orderId.setText("" + model.getOrderId());

        if (showCheckbox == 1) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        selectInvoices.addToArray(model.getId(), position);
                    } else {
                        selectInvoices.removeFromArray(model.getId(), position);
                    }
                }
            });
        } else {
            holder.checkBox.setVisibility(View.GONE);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ViewInvoice.class);
                    i.putExtra("invoiceNumber", model.getId());
                    context.startActivity(i);
                }
            });

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewInvoice.class);
                i.putExtra("invoiceNumber", model.getId());
                i.putExtra("from", "pending");
                i.putExtra("by", "seller");

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, invoiceNumber, orderId, invoiceTotal;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            invoiceNumber = itemView.findViewById(R.id.invoiceNumber);
            orderId = itemView.findViewById(R.id.orderId);
            invoiceTotal = itemView.findViewById(R.id.invoiceTotal);
            checkBox = itemView.findViewById(R.id.checkBox);


        }
    }

    public interface SelectInvoices {
        public void addToArray(long id, int position);

        public void removeFromArray(long id, int position);
    }
}
