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

import com.appsinventiv.toolsbazzar.Activities.ChooseCategory;
import com.appsinventiv.toolsbazzar.Models.MainCategoryModel;
import com.appsinventiv.toolsbazzar.ProductManagement.CategoryPackage.ChooseOtherMainCategory;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.EditProduct;
import com.appsinventiv.toolsbazzar.Seller.SellerAddProduct;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<MainCategoryModel> itemList;
    private ArrayList<MainCategoryModel> arrayList;


    public MainCategoryAdapter(Context context, ArrayList<MainCategoryModel> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.arrayList = new ArrayList<>(itemList);

    }

    public void updateList(ArrayList<MainCategoryModel> list) {
        this.itemList = list;
        arrayList.clear();
        arrayList.addAll(list);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (MainCategoryModel text : arrayList) {
                if (text.getMainCategory().toLowerCase().contains(charText)
                        ) {
                    itemList.add(text);
                }
            }


        }
        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_category_item_layout, parent, false);
        MainCategoryAdapter.ViewHolder viewHolder = new MainCategoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MainCategoryModel model = itemList.get(position);
        holder.subCategories.setText(model.getSubCategories() == null ? "" : model.getSubCategories());

        holder.maincategory.setText(model.getMainCategory());
        Glide.with(context).load(model.getUrl()).into(holder.icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SellerAddProduct.categoryList.add(model.getMainCategory());
                EditProduct.categoryList.add(model.getMainCategory());

                Intent i = new Intent(context, ChooseOtherMainCategory.class);
                i.putExtra("mainCategory", model.getMainCategory());

                context.startActivity(i);
//                    ((ChooseMainCategory) context).finish();

            }
        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SellerAddProduct.categoryList.add(model.getMainCategory());
//                Intent i = new Intent(context, ChooseCategory.class);
//                i.putExtra("parentCategory", model.getMainCategory());
//                context.startActivity(i);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView maincategory, subCategories;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            maincategory = itemView.findViewById(R.id.maincategory);
            subCategories = itemView.findViewById(R.id.subCategories);
        }
    }
}
