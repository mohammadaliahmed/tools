package com.appsinventiv.toolsbazzar.ListOfStrores;

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

import com.appsinventiv.toolsbazzar.Seller.SellerStore.SellerStoreView;
import com.appsinventiv.toolsbazzar.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListOfStroesAdapter extends RecyclerView.Adapter<ListOfStroesAdapter.ViewHolder> {
    Context context;
    ArrayList<StoreListModel> itemList = new ArrayList<>();
    StoreCallbacks callbacks;
    ArrayList<String> userStores;
    boolean flag;
    int[] colorList = {R.color.lightPink, R.color.lightPurple, R.color.lightYellow, R.color.lightBlue, R.color.lightGreen};

    public ListOfStroesAdapter(Context context, ArrayList<StoreListModel> itemList, ArrayList<String> userStores, boolean flag) {
        this.context = context;
        this.itemList = itemList;
        this.userStores = userStores;
        this.flag = flag;

    }

    public void setCallbacks(StoreCallbacks callbacks) {
        this.callbacks = callbacks;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.store_list_item_layout, parent, false);
        ListOfStroesAdapter.ViewHolder viewHolder = new ListOfStroesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final StoreListModel model = itemList.get(position);
        Random r = new Random();
        int randomNumber = r.nextInt(colorList.length);
        holder.linear.setBackgroundColor(context.getResources().getColor(colorList[randomNumber]));

        if (flag) {
            holder.unFollow.setVisibility(View.VISIBLE);
            holder.follow.setVisibility(View.GONE);

        } else {
            holder.unFollow.setVisibility(View.GONE);

            if (userStores.contains(model.getSeller().getUsername())) {
                holder.follow.setText("Following");
                holder.follow.setEnabled(false);
                holder.follow.setBackgroundResource(R.drawable.btn_black_bg);
            }
        }


        holder.storeName.setText(model.getSeller().getStoreName());
        StorePicsAdapter adapter = new StorePicsAdapter(context, model.getPictures(), model.getSeller().getUsername());
        holder.recyler.setLayoutManager(new GridLayoutManager(context, 3));
        holder.recyler.setAdapter(adapter);
        if (model.getSeller().getPicUrl() != null) {
            Glide.with(context).load(model.getSeller().getPicUrl()).into(holder.storeImg);

        } else {
            Glide.with(context).load(R.drawable.placeholder).into(holder.storeImg);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SellerStoreView.class);
                i.putExtra("sellerId", model.getSeller().getUsername());
                context.startActivity(i);
            }
        });

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callbacks.followStore(model.getSeller());
                holder.follow.setText("Following");
                holder.follow.setEnabled(false);
                holder.follow.setBackgroundResource(R.drawable.btn_black_bg);
            }
        });

        holder.unFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callbacks.unFollowStore(model.getSeller());

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
        Button follow, unFollow;
        RecyclerView recyler;
        LinearLayout linear;

        public ViewHolder(View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.storeName);
            followerCount = itemView.findViewById(R.id.followerCount);
            follow = itemView.findViewById(R.id.follow);
            storeImg = itemView.findViewById(R.id.storeImg);
            recyler = itemView.findViewById(R.id.recyler);
            unFollow = itemView.findViewById(R.id.unFollow);
            linear = itemView.findViewById(R.id.linear);
        }
    }

}
