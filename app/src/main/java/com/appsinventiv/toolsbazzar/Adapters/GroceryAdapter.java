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

import com.appsinventiv.toolsbazzar.Activities.ListOfProducts;
import com.appsinventiv.toolsbazzar.Models.GroceryListModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 26/06/2018.
 */

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {
    Context context;
    ArrayList<GroceryListModel> itemList;

    public GroceryAdapter(Context context, ArrayList<GroceryListModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.grocery_item_layout,parent,false);
        GroceryAdapter.ViewHolder holder=new GroceryAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final GroceryListModel model=itemList.get(position);
        holder.title.setText(model.getTitle());
        Glide.with(context).load(model.getImageId()).placeholder(R.drawable.placeholder).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommonUtils.isNetworkConnected()) {
                    Intent i = new Intent(context, ListOfProducts.class);
                    i.putExtra("category", model.getTitle());
                    context.startActivity(i);
                }else{
                    CommonUtils.showToast("Please connect to Internet");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.text);
            image=itemView.findViewById(R.id.image);
        }
    }
}
