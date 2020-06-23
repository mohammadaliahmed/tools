package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ChooseLanguage;
import com.appsinventiv.toolsbazzar.Activities.Welcome;
import com.appsinventiv.toolsbazzar.Models.LanguageModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.EditProduct;
import com.appsinventiv.toolsbazzar.Seller.SellerAddProduct;

import java.util.ArrayList;

/**
 * Created by AliAh on 27/11/2018.
 */

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {
    Context context;
    ArrayList<LanguageModel> list;
    Listener listener;

    public LanguageListAdapter(Context context, ArrayList<LanguageModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_category_item, parent, false);
        LanguageListAdapter.ViewHolder viewHolder = new LanguageListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final LanguageModel model = list.get(position);
        holder.title.setText(model.getLanguageName());

        String[] abc = model.getLanguageName().split(" ");
        if (abc.length > 1) {
            if (abc[1].substring(0, 1).equalsIgnoreCase("&")) {
                holder.initials.setText(abc[0].substring(0, 1) + abc[2].substring(0, 1));

            } else {
                holder.initials.setText(abc[0].substring(0, 1) + abc[1].substring(0, 1));

            }
        } else {
            holder.initials.setText(abc[0].substring(0, 1));

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.countryChosen(model);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, initials;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            initials = itemView.findViewById(R.id.initials);

        }
    }

    public interface Listener {
        public void countryChosen(LanguageModel model);
    }

}
