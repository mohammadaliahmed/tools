package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.appsinventiv.toolsbazzar.Activities.ChooseCategory;
import com.appsinventiv.toolsbazzar.Models.MainCategoryModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.EditProduct;
import com.appsinventiv.toolsbazzar.Seller.SellerAddProduct;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by AliAh on 27/11/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<String> list;
    GetNewData getNewData;
    private ArrayList<String> arrayList;


    public CategoryAdapter(Context context, ArrayList<String> list, GetNewData getNewData) {
        this.context = context;
        this.list = list;
        this.getNewData = getNewData;
        this.arrayList = new ArrayList<>(list);

    }

    public void updateList(ArrayList<String> list) {
        this.list = list;
        arrayList.clear();
        arrayList.addAll(list);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.length() == 0) {
            list.addAll(arrayList);
        } else {
            for (String text : arrayList) {
                if (text.toLowerCase().contains(charText)
                        ) {
                    list.add(text);
                }
            }


        }
        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_category_item, parent, false);
        CategoryAdapter.ViewHolder viewHolder = new CategoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String title = list.get(position);
        holder.title.setText(title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(context, ChooseCategory.class);
                SellerAddProduct.categoryList.add(title);
                EditProduct.categoryList.add(title);
//
//                i.putExtra("parentCategory", title);
////                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(i);
                getNewData.whichCategory(title);


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }

    public interface GetNewData {
        public void whichCategory(String title);
    }

}
