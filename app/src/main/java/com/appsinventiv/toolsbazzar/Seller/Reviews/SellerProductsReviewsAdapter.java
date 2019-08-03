package com.appsinventiv.toolsbazzar.Seller.Reviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.No_Internet;
import com.appsinventiv.toolsbazzar.Activities.ProductComments;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerProductComments;
import com.appsinventiv.toolsbazzar.Seller.SellerViewProduct;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.like.LikeButton;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AliAh on 23/06/2018.
 */

public class SellerProductsReviewsAdapter extends RecyclerView.Adapter<SellerProductsReviewsAdapter.Viewholder> {
    Context context;
    ArrayList<Product> productList;
    private ArrayList<Product> arrayList = new ArrayList<>();
    SellerProductsAdapterCallbacks callbacks;

    public SellerProductsReviewsAdapter(Context context,
                                        ArrayList<Product> productList
    ) {
        this.context = context;
        this.productList = productList;

    }

    public void setCallbacks(SellerProductsAdapterCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public SellerProductsReviewsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seller_product_review_item_layout, parent, false);
        SellerProductsReviewsAdapter.Viewholder viewHolder = new SellerProductsReviewsAdapter.Viewholder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SellerProductsReviewsAdapter.Viewholder holder, final int position) {
        final Product model = productList.get(position);

        HashMap<String, Double> map = SharedPrefs.getCommentsCount();

        if (map != null && map.size() > 0) {

            if (map.get(model.getId()) != null) {
                if (map.get(model.getId()) == 0) {
                    holder.unreadCommentCount.setVisibility(View.GONE);
                } else {
                    holder.unreadCommentCount.setVisibility(View.VISIBLE);
                    holder.unreadCommentCount.setText("" + map.get(model.getId()).toString().replace(".0",""));

                }

            } else {
                holder.unreadCommentCount.setVisibility(View.GONE);
            }
        } else {
            holder.unreadCommentCount.setVisibility(View.GONE);
        }
        holder.title.setText(model.getTitle());

        holder.ratingBar.setRating(model.getRating());
        holder.subtitle.setText(model.getSubtitle());
        Glide.with(context).load(model.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(holder.image);
        holder.quantity.setText("Quantity in stock: " + model.getQuantityAvailable());

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SellerProductComments.class);
                i.putExtra("productId", model.getId());
                context.startActivity(i);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {
                    Intent i = new Intent(context, SellerViewProduct.class);
                    i.putExtra("productId", model.getId());
                    context.startActivity(i);
                } else {
                    Intent i = new Intent(context, No_Internet.class);
                    context.startActivity(i);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView image;
        TextView quantity, unreadCommentCount;
        RatingBar ratingBar;
        RelativeLayout comments;

        public Viewholder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            image = itemView.findViewById(R.id.image);
            quantity = itemView.findViewById(R.id.quantity);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            comments = itemView.findViewById(R.id.comments);
            unreadCommentCount = itemView.findViewById(R.id.unreadCommentCount);


        }
    }

    public interface SellerProductsAdapterCallbacks {
        public void onOptionClicked(Product product);
    }
}
