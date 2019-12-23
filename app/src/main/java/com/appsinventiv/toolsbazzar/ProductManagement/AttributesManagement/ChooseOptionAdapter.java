package com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 27/11/2018.
 */

public class ChooseOptionAdapter extends RecyclerView.Adapter<ChooseOptionAdapter.ViewHolder> {
    Context context;
    ArrayList<String> list;
    ChooseOptionCallback callback;
    String type;
    int selected = -1;

    public ChooseOptionAdapter(Context context, ArrayList<String> list, String type, ChooseOptionCallback callback) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.callback = callback;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_product_option, parent, false);
        ChooseOptionAdapter.ViewHolder viewHolder = new ChooseOptionAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String title = list.get(position);
        holder.title.setText(title);

        if (type.equalsIgnoreCase("dangerous")) {
            Glide.with(context).load(R.drawable.ic_danger).into(holder.image);
        } else if (type.equalsIgnoreCase("warrentyPeriod")) {
            Glide.with(context).load(R.drawable.ic_warrent).into(holder.image);
        } else {
            Glide.with(context).load(R.drawable.image347).into(holder.image);
        }
        if (selected == position) {
            holder.tick.setVisibility(View.VISIBLE);
        } else {
            holder.tick.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = position;
                notifyDataSetChanged();
                callback.onOptionSelected(title);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView tick, image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            tick = itemView.findViewById(R.id.tick);
            image = itemView.findViewById(R.id.image);
        }
    }

    public interface ChooseOptionCallback {
        public void onOptionSelected(String value);
    }

}
