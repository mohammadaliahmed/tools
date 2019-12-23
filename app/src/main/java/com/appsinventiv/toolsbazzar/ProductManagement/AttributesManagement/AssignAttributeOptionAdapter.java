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
import com.appsinventiv.toolsbazzar.Utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by AliAh on 27/11/2018.
 */

public class AssignAttributeOptionAdapter extends RecyclerView.Adapter<AssignAttributeOptionAdapter.ViewHolder> {
    Context context;
    ArrayList<SubAttributeModel> list;
    ChooseAssignOptionCallback callback;
    private ArrayList<SubAttributeModel> arrayList;
    private ArrayList<SubAttributeModel> selectedList;


    public AssignAttributeOptionAdapter(Context context, ArrayList<SubAttributeModel> list, ChooseAssignOptionCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
        this.arrayList = new ArrayList<>(list);


    }

    public void setSelectedList(ArrayList<SubAttributeModel> selectedList) {
        this.selectedList = selectedList;
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<SubAttributeModel> list) {
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
            for (SubAttributeModel item : arrayList) {
                if (item.getMainCategory().toLowerCase().contains(charText)
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
        View view = LayoutInflater.from(context).inflate(R.layout.assign_attribute_option, parent, false);
        AssignAttributeOptionAdapter.ViewHolder viewHolder = new AssignAttributeOptionAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final SubAttributeModel title = list.get(position);

        if (selectedList != null && selectedList.size() > 0) {
            for (SubAttributeModel item : selectedList) {
                if (item.getMainCategory().contains(title.getMainCategory())) {
                    holder.checkBox.setChecked(true);
                    return;
                } else {
                    holder.checkBox.setChecked(false);
                }
            }
        }
        holder.title.setText(title.getMainCategory());
        if (Constants.SKU_ATT.toLowerCase().contains("sku")) {
            Glide.with(context).load(R.drawable.ic_sku).into(holder.icon);
        } else {
            Glide.with(context).load(R.drawable.ic_att).into(holder.icon);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {

                    callback.onOptionSelected(title, b, position);

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
        CheckBox checkBox;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            checkBox = itemView.findViewById(R.id.checkBox);
            icon = itemView.findViewById(R.id.icon);
        }
    }

    public interface ChooseAssignOptionCallback {
        public void onOptionSelected(SubAttributeModel value, boolean checked, int position);
    }

}
