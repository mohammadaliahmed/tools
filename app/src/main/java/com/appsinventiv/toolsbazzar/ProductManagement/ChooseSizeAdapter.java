package com.appsinventiv.toolsbazzar.ProductManagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.appsinventiv.toolsbazzar.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by AliAh on 27/11/2018.
 */

public class ChooseSizeAdapter extends RecyclerView.Adapter<ChooseSizeAdapter.ViewHolder> {
    Context context;
    ArrayList<String> list;
    ChooseSizeOptionCallback callback;
    private ArrayList<String> arrayList;


    public ChooseSizeAdapter(Context context, ArrayList<String> list, ChooseSizeOptionCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
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
            for (String item : arrayList) {
                if (item.toLowerCase().contains(charText)
                        ) {
                    list.add(item);
                }
            }


        }
        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_size_option, parent, false);
        ChooseSizeAdapter.ViewHolder viewHolder = new ChooseSizeAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String title = list.get(position);
        holder.title.setText(title);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        ImageView tick, arrow;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            arrow = itemView.findViewById(R.id.arrow);
        }
    }

    public interface ChooseSizeOptionCallback {
        public void onOptionSelected(String value);
    }

}
