package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.R;

import java.util.List;

/**
 * Created by AliAh on 03/11/2018.
 */

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.ViewHolder> {
    Context context;
    List<String> list;
    OnItemSelected onItemSelected;
    int selected = -1;

    public AttributesAdapter(Context context, List<String> list, OnItemSelected onItemSelected) {
        this.context = context;
        this.list = list;
        this.onItemSelected = onItemSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attribute_item_layout, parent, false);
        AttributesAdapter.ViewHolder viewHolder = new AttributesAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String value = list.get(position);
        if (selected != -1) {
            if (selected == position) {
                holder.title.setBackgroundResource(R.drawable.size_button_selected);
                holder.title.setTextColor(context.getResources().getColor(R.color.colorYellow));
            } else {
                holder.title.setBackgroundResource(R.drawable.size_button_layout);
                holder.title.setTextColor(context.getResources().getColor(R.color.default_grey_text));
            }
        }
        holder.title.setText(value);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onItemSelected.onOptionSelected(value);
                onItemSelected.onOptionSelected(""+position);
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
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }

    public interface OnItemSelected {
        public void onOptionSelected(String value);
    }
}
