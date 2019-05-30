package com.appsinventiv.toolsbazzar.ListOfStrores;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsinventiv.toolsbazzar.Seller.SellerStore.SellerStoreView;
import com.appsinventiv.toolsbazzar.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class StorePicsAdapter extends RecyclerView.Adapter<StorePicsAdapter.ViewHolder> {
    Context context;
    ArrayList<String> itemList;
    private String sellerId;

    public StorePicsAdapter(Context context, ArrayList<String> itemList,String sellerId) {
        this.context = context;
        this.itemList = itemList;
        this.sellerId=sellerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.store_pic_item_layout, parent, false);
        StorePicsAdapter.ViewHolder viewHolder = new StorePicsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String pic = itemList.get(position);
        Glide.with(context).load(pic).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SellerStoreView.class);
                i.putExtra("sellerId", sellerId);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView  img;


        public ViewHolder(View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
        }
    }
}
