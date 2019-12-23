package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.ColorAttributeModel;
import com.appsinventiv.toolsbazzar.R;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by AliAh on 03/11/2018.
 */

public class NewAttributesAdapter extends RecyclerView.Adapter<NewAttributesAdapter.ViewHolder> {
    Context context;
    List<ColorAttributeModel> list;
    OnItemSelected onItemSelected;
    int selected = -1;

    public NewAttributesAdapter(Context context, List<ColorAttributeModel> list, OnItemSelected onItemSelected) {
        this.context = context;
        this.list = list;
        this.onItemSelected = onItemSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_attribute_item_layout, parent, false);
        NewAttributesAdapter.ViewHolder viewHolder = new NewAttributesAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ColorAttributeModel value = list.get(position);
        if (selected != -1) {
            if (selected == position) {
                holder.image.setBackgroundResource(R.drawable.size_button_selected);
//                holder.image.setTextColor(context.getResources().getColor(R.color.colorYellow));
            } else {
                holder.image.setBackgroundResource(R.drawable.size_button_layout);
//                holder.image.setTextColor(context.getResources().getColor(R.color.default_grey_text));
            }
        }

        Glide.with(context).load(value.getImageUrl()).into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemSelected.onOptionSelected(value.getColor());
                selected = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

    public interface OnItemSelected {
        public void onOptionSelected(String value);
    }
}
