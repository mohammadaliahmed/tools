package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.ChatModel;
import com.appsinventiv.toolsbazzar.Models.CommentsModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AliAh on 06/11/2018.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    Context context;
    Product product;
    ArrayList<CommentsModel> arrayList;
    public int RIGHT_CHAT = 1;
    public int LEFT_CHAT = 0;

    VendorModel vendor;

    public void setVendor(VendorModel vendor) {
        this.vendor = vendor;
        notifyDataSetChanged();
    }

    public CommentsAdapter(Context context, ArrayList<CommentsModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentsAdapter.ViewHolder viewHolder;
        if (viewType == RIGHT_CHAT) {
            View view = LayoutInflater.from(context).inflate(R.layout.right_comments_item_layout, parent, false);
            viewHolder = new CommentsAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.left_comments_item_layout, parent, false);
            viewHolder = new CommentsAdapter.ViewHolder(view);
        }

//        View view= LayoutInflater.from(context).inflate(R.layout.comments_item_layout,parent,false);
//        CommentsAdapter.ViewHolder viewHolder=new CommentsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        CommentsModel model = arrayList.get(position);
        if (product != null) {
            if (model.getName().equalsIgnoreCase(product.getVendor().getVendorName()) || model.getName().equalsIgnoreCase("Fort City")) {
                return RIGHT_CHAT;
            } else {
                return LEFT_CHAT;
            }
        } else {
            return LEFT_CHAT;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentsModel model = arrayList.get(position);
        if (getItemViewType(position) == RIGHT_CHAT) {
            if (model.getName().equalsIgnoreCase("Fort City")) {
                holder.name.setText("Fort City");
                if(SharedPrefs.getVendor()!=null){
                    Glide.with(context).load(R.drawable.fort_city_without_green).into(holder.pic);
                }else {
                    Glide.with(context).load(R.drawable.logo_small).into(holder.pic);
                }

            } else {
                holder.name.setText(product.getVendor().getStoreName());
                if (vendor != null && vendor.getPicUrl() != null) {
                    Glide.with(context).load(vendor.getPicUrl()).into(holder.pic);
                }
            }
        } else {
            holder.name.setText(model.getName());
            Glide.with(context).load(model.getPicUrl()).into(holder.pic);
        }
        holder.time.setText(CommonUtils.getFormattedDate(model.getTime()));
        holder.comment.setText(model.getCommentText());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, comment;
        CircleImageView pic;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            comment = itemView.findViewById(R.id.comment);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
