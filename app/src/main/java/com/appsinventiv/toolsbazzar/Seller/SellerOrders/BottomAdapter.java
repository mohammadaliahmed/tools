package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

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


public class BottomAdapter extends RecyclerView.Adapter<BottomAdapter.ViewHolder> {
    Context context;
    ArrayList<BottomDialogModel> itemList;
    ShareMessageFriendsAdapterCallbacks callbacks;

    public BottomAdapter(Context context, ArrayList<BottomDialogModel> itemList, ShareMessageFriendsAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_item_layout, viewGroup, false);
        BottomAdapter.ViewHolder viewHolder = new BottomAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        final BottomDialogModel model = itemList.get(i);
        holder.name.setText(model.getName());
        holder.subtitle.setText(model.getSubtitle());
        if (model.getPicUrl() != null) {
            Glide.with(context).load(model.getPicUrl()).into(holder.pic);
        } else {
            Glide.with(context).load(R.drawable.placeholder).into(holder.pic);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onChoose(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, subtitle;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            name = itemView.findViewById(R.id.name);
            subtitle = itemView.findViewById(R.id.subtitle);
        }
    }

    public interface ShareMessageFriendsAdapterCallbacks {
        public void onChoose(int position);
    }

}
