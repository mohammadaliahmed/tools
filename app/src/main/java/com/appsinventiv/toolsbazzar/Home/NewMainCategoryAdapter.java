package com.appsinventiv.toolsbazzar.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ChooseCategory;
import com.appsinventiv.toolsbazzar.Models.MainCategoryModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerAddProduct;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

public class NewMainCategoryAdapter extends RecyclerView.Adapter<NewMainCategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<MainCategoryModel> itemList;
    int returnSize;


    public NewMainCategoryAdapter(Context context, ArrayList<MainCategoryModel> itemList,int returnSize) {
        this.context = context;
        this.itemList = itemList;
        this.returnSize = returnSize;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_main_category_item_layout, parent, false);
        NewMainCategoryAdapter.ViewHolder viewHolder = new NewMainCategoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainCategoryModel model = itemList.get(position);

        holder.maincategory.setText(model.getMainCategory());
        Glide.with(context).load(model.getUrl()).into(holder.icon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        if (itemList.size() < 1) {
            return 0;
        } else {
            return returnSize;

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView maincategory;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            maincategory = itemView.findViewById(R.id.maincategory);
        }
    }
}
