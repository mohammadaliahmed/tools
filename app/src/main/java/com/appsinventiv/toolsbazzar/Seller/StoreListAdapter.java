package com.appsinventiv.toolsbazzar.Seller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.ListOfStrores.StoreCallbacks;
import com.appsinventiv.toolsbazzar.ListOfStrores.StoreListModel;
import com.appsinventiv.toolsbazzar.ListOfStrores.StorePicsAdapter;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerStore.SellerStoreView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {
    Context context;
    ArrayList<StoreListModel> itemList = new ArrayList<>();
    int[] colorList = {R.color.lightPink, R.color.lightPurple, R.color.lightYellow, R.color.lightBlue, R.color.lightGreen};

    public StoreListAdapter(Context context, ArrayList<StoreListModel> itemList) {
        this.context = context;
        this.itemList = itemList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.store_list_other_item_layout, parent, false);
        StoreListAdapter.ViewHolder viewHolder = new StoreListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final StoreListModel model = itemList.get(position);
        Random r = new Random();
        int randomNumber = r.nextInt(colorList.length);
        holder.linear.setBackgroundColor(context.getResources().getColor(colorList[randomNumber]));


        holder.storeName.setText(model.getSeller().getStoreName());
        StorePicsAdapter adapter = new StorePicsAdapter(context, model.getPictures(), model.getSeller().getUsername());
        holder.recyler.setLayoutManager(new GridLayoutManager(context, 3));
        holder.recyler.setAdapter(adapter);
        if (model.getSeller().getPicUrl() != null) {
            Glide.with(context).load(model.getSeller().getPicUrl()).into(holder.storeImg);

        } else {
            Glide.with(context).load(R.drawable.logo).into(holder.storeImg);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.getSeller().getStoreName().equalsIgnoreCase("fort city")){
                    Intent i = new Intent(context, SellerStoreView.class);
                    i.putExtra("sellerId", model.getSeller().getUsername());
                    context.startActivity(i);
                }else {
                    Intent i = new Intent(context, SellerStoreView.class);
                    i.putExtra("sellerId", model.getSeller().getUsername());
                    context.startActivity(i);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeName, followerCount;
        CircleImageView storeImg;
        RecyclerView recyler;
        LinearLayout linear;

        public ViewHolder(View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.storeName);
            followerCount = itemView.findViewById(R.id.followerCount);
            storeImg = itemView.findViewById(R.id.storeImg);
            recyler = itemView.findViewById(R.id.recyler);
            linear = itemView.findViewById(R.id.linear);
        }
    }

}
