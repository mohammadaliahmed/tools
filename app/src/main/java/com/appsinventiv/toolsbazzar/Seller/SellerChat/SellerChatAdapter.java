package com.appsinventiv.toolsbazzar.Seller.SellerChat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.ChatModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AliAh on 24/06/2018.
 */

public class SellerChatAdapter extends RecyclerView.Adapter<SellerChatAdapter.ViewHolder> {
    Context context;
    ArrayList<ChatModel> chatList;

    public int RIGHT_CHAT = 1;
    public int LEFT_CHAT = 0;

    public SellerChatAdapter(Context context, ArrayList<ChatModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SellerChatAdapter.ViewHolder viewHolder;
        if (viewType == RIGHT_CHAT) {
            View view = LayoutInflater.from(context).inflate(R.layout.seller_right_chat_layout, parent, false);
            viewHolder = new SellerChatAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.seller_left_chat_layout, parent, false);
            viewHolder = new SellerChatAdapter.ViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatModel model = chatList.get(position);
        holder.msgtext.setText(model.getText());

        if (SharedPrefs.getVendor().getPicUrl() != null) {
            try {
                Glide.with(context).load(SharedPrefs.getVendor().getPicUrl()).placeholder(R.drawable.ic_profile).into(holder.profilePic);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        holder.time.setText("" + CommonUtils.getFormattedDate(model.getTime()));
        if (model.getStatus().equals("sent")) {
            holder.status.setImageResource(R.drawable.ic_sent);
        } else if (model.getStatus().equals("sending")) {
            holder.status.setImageResource(R.drawable.ic_time);
        } else if (model.getStatus().equals("delivered")) {
            holder.status.setImageResource(R.drawable.ic_delivered);
        } else if (model.getStatus().equals("read")) {
            holder.status.setImageResource(R.drawable.ic_read);
        }
        if (model.getWhoReplied() != null) {
            holder.whoReplied.setText(model.getWhoReplied() + ", Im your assistant");
        } else {
            holder.whoReplied.setText("");
        }

    }

    @Override
    public int getItemViewType(int position) {
        ChatModel model = chatList.get(position);
        if (model.getUsername().equals(SharedPrefs.getUsername())) {
            return RIGHT_CHAT;
        } else {
            return LEFT_CHAT;
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView msgtext, time, whoReplied;
        ImageView status;
        CircleImageView profilePic;


        public ViewHolder(View itemView) {
            super(itemView);
            msgtext = itemView.findViewById(R.id.msgtext);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            profilePic = itemView.findViewById(R.id.profilePic);
            whoReplied = itemView.findViewById(R.id.whoReplied);
        }
    }
}
