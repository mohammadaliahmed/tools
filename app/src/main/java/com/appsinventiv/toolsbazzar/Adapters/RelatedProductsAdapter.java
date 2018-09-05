package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ViewProduct;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

/**
 * Created by AliAh on 23/06/2018.
 */

public class RelatedProductsAdapter extends RecyclerView.Adapter<RelatedProductsAdapter.Viewholder> {
    Context context;
    ArrayList<Product> productList;
    AddToCartInterface addToCartInterface;
    ArrayList<ProductCountModel> userCartProductList;
    ArrayList<String> userWishList;

    public RelatedProductsAdapter(Context context,
                                  ArrayList<Product> productList,
                                  ArrayList<ProductCountModel> userCartProductList,
                                  ArrayList<String> userWishList,
                                  AddToCartInterface addToCartInterface) {
        this.context = context;
        this.productList = productList;
        this.addToCartInterface = addToCartInterface;
        this.userWishList = userWishList;
        this.userCartProductList = userCartProductList;
    }

    @NonNull
    @Override
    public RelatedProductsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.related_product_layout, parent, false);
        RelatedProductsAdapter.Viewholder viewHolder = new RelatedProductsAdapter.Viewholder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RelatedProductsAdapter.Viewholder holder, final int position) {
        final Product model = productList.get(position);

        holder.title.setText(model.getTitle());
        if(SharedPrefs.getCustomerType().equalsIgnoreCase("retail")){
            holder.price.setText("Rs. " + model.getRetailPrice());
        }else if(SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")){
            holder.price.setText("Rs. " + model.getWholeSalePrice());
        }

        holder.subtitle.setText(model.getMeasurement());
        Glide.with(context).load(model.getThumbnailUrl()).into(holder.image);

        final int[] count = {1};
        ProductCountModel productCountModel = null;
        boolean flag = false;
        boolean isLiked = false;
        if (!userCartProductList.isEmpty()) {
            for (int i = 0; i < userCartProductList.size(); i++) {
                if (userCartProductList.get(i).getProduct().getId() != null) {
                    if (model.getId().equalsIgnoreCase(userCartProductList.get(i).getProduct().getId())) {
                        flag = true;
                        productCountModel = userCartProductList.get(i);
                    }
                }
            }
        }
        if (!userWishList.isEmpty()) {
            for (int i = 0; i < userWishList.size(); i++) {
                if (model.getId().equalsIgnoreCase(userWishList.get(i))) {
                    isLiked = true;
                }
            }
        }
        if (isLiked) {
            holder.heart_button.setLiked(true);
        } else {
            holder.heart_button.setLiked(false);
        }
        if (flag) {
            holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);


            count[0] = productCountModel.getQuantity();


            holder.count.setText("" + count[0]);
            holder.increase.setVisibility(View.VISIBLE);

            if (count[0] > 1) {
                holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                holder.count.setTextColor(context.getResources().getColor(R.color.default_grey_text));

                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                holder.decrease.setVisibility(View.VISIBLE);

            } else {
                holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                holder.count.setTextColor(context.getResources().getColor(R.color.default_grey_text));

                holder.decrease.setImageResource(R.drawable.delete);
                holder.decrease.setVisibility(View.VISIBLE);
            }
        } else {
            holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
            holder.count.setTextColor(context.getResources().getColor(R.color.colorWhite));
            holder.count.setText("Add to cart");
            holder.increase.setVisibility(View.GONE);
            holder.decrease.setVisibility(View.GONE);
            flag = false;


        }
        flag = false;

        holder.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count[0] > 1) {

                } else {
                    holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);

                    holder.count.setText("" + count[0]);
                    holder.increase.setVisibility(View.VISIBLE);
                    holder.decrease.setVisibility(View.VISIBLE);
                    if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                        CommonUtils.showToast("Minimum order qty: "+model.getMinOrderQuantity());
                        addToCartInterface.addedToCart(model, model.getMinOrderQuantity(), position);
                    } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                        addToCartInterface.addedToCart(model, count[0], position);
                    }

                }
            }
        });
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count[0] += 1;
                holder.count.setText("" + count[0]);
                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                if (model != null)
                    addToCartInterface.quantityUpdate(model, count[0], position);

            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                    if (count[0] > (model.getMinOrderQuantity() +1 )) {
                        count[0] -= 1;
                        holder.count.setText("" + count[0]);
                        addToCartInterface.quantityUpdate(model, count[0], position);

                    } else if (count[0] > model.getMinOrderQuantity()) {
                        {
                            count[0] -= 1;
                            holder.count.setText("" + count[0]);
                            holder.decrease.setImageResource(R.drawable.delete);
                            addToCartInterface.quantityUpdate(model, count[0], position);


                        }
                    } else if (count[0] == model.getMinOrderQuantity()) {
                        holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
                        holder.count.setText("Add to cart");
                        holder.count.setTextColor(context.getResources().getColor(R.color.colorWhite));
                        holder.increase.setVisibility(View.GONE);
                        holder.decrease.setVisibility(View.GONE);
                        addToCartInterface.deletedFromCart(model, position);

                    }
                } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {


                    if (count[0] > 2) {
                        count[0] -= 1;
                        holder.count.setText("" + count[0]);
                        addToCartInterface.quantityUpdate(model, count[0], position);

                    } else if (count[0] > 1) {
                        {
                            count[0] -= 1;
                            holder.count.setText("" + count[0]);
                            holder.decrease.setImageResource(R.drawable.delete);
                            addToCartInterface.quantityUpdate(model, count[0], position);


                        }
                    } else if (count[0] == 1) {
                        holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
                        holder.count.setText("Add to cart");
                        holder.count.setTextColor(context.getResources().getColor(R.color.colorWhite));
                        holder.increase.setVisibility(View.GONE);
                        holder.decrease.setVisibility(View.GONE);
                        addToCartInterface.deletedFromCart(model, position);

                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewProduct.class);
                i.putExtra("productId", model.getId());
                context.startActivity(i);
            }
        });

        holder.heart_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addToCartInterface.isProductLiked(model, true, position);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                addToCartInterface.isProductLiked(model, false, position);


            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, count;
        ImageView image, increase, decrease;
        RelativeLayout relativeLayout;
        LikeButton heart_button;

        public Viewholder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            count = itemView.findViewById(R.id.count);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            heart_button = itemView.findViewById(R.id.heart_button);

        }
    }
}
