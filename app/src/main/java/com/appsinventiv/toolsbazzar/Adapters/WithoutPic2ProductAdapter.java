package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsinventiv.toolsbazzar.Activities.ViewProduct;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class WithoutPic2ProductAdapter extends RecyclerView.Adapter<WithoutPic2ProductAdapter.ViewHolder> {
    Context context;
    ArrayList<Product> itemList;

    public WithoutPic2ProductAdapter(Context context, ArrayList<Product> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.product_image_layout,parent,false);
        WithoutPic2ProductAdapter.ViewHolder viewHolder=new WithoutPic2ProductAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Product product=itemList.get(position);
        Glide.with(context).load(product.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,ViewProduct.class);
                i.putExtra("productId",product.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
        }
    }
}
