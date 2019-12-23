package com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.appsinventiv.toolsbazzar.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by AliAh on 27/11/2018.
 */

public class ChooseAttributeOptionAdapter extends RecyclerView.Adapter<ChooseAttributeOptionAdapter.ViewHolder> {
    Context context;
    ArrayList<String> list;
    ChooseOptionCallback callback;
    int selected = -1;
    boolean multiSelect;
    String selectedText = "";
    private ArrayList<String> arrayList;


    public ChooseAttributeOptionAdapter(Context context, ArrayList<String> list, ChooseOptionCallback callback) {
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


    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
        notifyDataSetChanged();
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_attribute_option, parent, false);
        ChooseAttributeOptionAdapter.ViewHolder viewHolder = new ChooseAttributeOptionAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String title = list.get(position);
        holder.title.setText(title);


        if (!multiSelect) {
            holder.arrow.setVisibility(View.VISIBLE);
            holder.tick.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);

            if (selected == position) {
                holder.tick.setVisibility(View.VISIBLE);
            } else {
                holder.tick.setVisibility(View.GONE);
            }
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.arrow.setVisibility(View.GONE);
            holder.tick.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!multiSelect) {
                    selected = position;
                    notifyDataSetChanged();
                    callback.onOptionSelected(title);
                } else {

                }
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (selectedText.equalsIgnoreCase("")) {
                        selectedText = title;
                    } else {
                        selectedText = selectedText + ", " + title;
                    }
                    callback.onOptionSelected(selectedText);
                }else{

                }
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
            tick = itemView.findViewById(R.id.tick);
            checkBox = itemView.findViewById(R.id.checkBox);
            arrow = itemView.findViewById(R.id.arrow);
        }
    }

    public interface ChooseOptionCallback {
        public void onOptionSelected(String value);
    }

}
