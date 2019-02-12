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
import com.appsinventiv.toolsbazzar.R;

import java.util.ArrayList;

/**
 * Created by AliAh on 27/11/2018.
 */

public class AddressChooseAdapter extends RecyclerView.Adapter<AddressChooseAdapter.ViewHolder> {
    Context context;
    ArrayList<String> list;
    String key;
    WhichOption whichOption;

    public AddressChooseAdapter(Context context, ArrayList<String> list,String key, WhichOption whichOption) {
        this.context = context;
        this.list = list;
        this.key=key;
        this.whichOption=whichOption;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_category_item, parent, false);
        AddressChooseAdapter.ViewHolder viewHolder = new AddressChooseAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String title = list.get(position);
        holder.title.setText(title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               whichOption.option(key,title);

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
    public interface  WhichOption{
        public void option(String key,String value);
    }

}
