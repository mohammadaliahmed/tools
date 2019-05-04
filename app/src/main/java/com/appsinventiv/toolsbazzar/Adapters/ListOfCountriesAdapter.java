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

import com.appsinventiv.toolsbazzar.Activities.ChooseAddress;
import com.appsinventiv.toolsbazzar.Activities.ChooseCountry;
import com.appsinventiv.toolsbazzar.Models.CountryModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by AliAh on 27/11/2018.
 */

public class ListOfCountriesAdapter extends RecyclerView.Adapter<ListOfCountriesAdapter.ViewHolder> {
    Context context;
    ArrayList<CountryModel> list;
    boolean canGoNext = false;
    Listener listener;
    private ArrayList<CountryModel> arrayList;


    public ListOfCountriesAdapter(Context context, ArrayList<CountryModel> list) {
        this.context = context;
        this.list = list;
        this.arrayList = new ArrayList<>(list);

    }

    public void setListener(boolean canGoNext, Listener listener) {
        this.canGoNext = canGoNext;
        this.listener = listener;
    }

    public void updateList(ArrayList<CountryModel> list) {
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
            for (CountryModel country : arrayList) {
                if (country.getCountryName().toLowerCase().contains(charText) ||
                        country.getCurrencySymbol().toLowerCase().contains(charText)
                        ) {
                    list.add(country);
                }
            }


        }
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_country_item, parent, false);
        ListOfCountriesAdapter.ViewHolder viewHolder = new ListOfCountriesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CountryModel model = list.get(position);
        holder.title.setText(model.getCountryName());
        holder.currency.setText("Currency: " + model.getCurrencySymbol());
        holder.countryCode.setText(model.getMobileCode());
        Glide.with(context).load(model.getImageUrl()).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canGoNext) {
                    Intent i = new Intent(context, ChooseAddress.class);
                    i.putExtra("country", model.getCountryName());
                    i.putExtra("countryType", model.isShippingCountry());
                    context.startActivity(i);
                } else {
                    listener.countryObject(model);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, countryCode, currency;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            currency = itemView.findViewById(R.id.currency);
            countryCode = itemView.findViewById(R.id.countryCode);
            image = itemView.findViewById(R.id.image);
        }
    }

    public interface Listener {
        public void countryObject(CountryModel model);
    }

}
