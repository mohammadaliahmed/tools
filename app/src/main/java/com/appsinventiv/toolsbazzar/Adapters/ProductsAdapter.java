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

import com.appsinventiv.toolsbazzar.Activities.ViewProduct;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 20/06/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    Context context;
    ArrayList<Product> productList;
    AddToCartInterface addToCartInterface;
    ArrayList<ProductCountModel> userCartProductList;


    public ProductsAdapter(Context context,
                           ArrayList<Product> productList,
                           ArrayList<ProductCountModel> userCartProductList,
                           AddToCartInterface addToCartInterface) {
        this.context = context;
        this.productList = productList;
        this.addToCartInterface = addToCartInterface;
        this.userCartProductList = userCartProductList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item_layout, parent, false);
        ProductsAdapter.ViewHolder viewHolder = new ProductsAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Product model = productList.get(position);
        holder.title.setText(model.getTitle());
        holder.price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
        holder.subtitle.setText(model.getSubtitle());
        Glide.with(context).load(model.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(holder.image);

        final int[] count = {1};
        ProductCountModel productCountModel = null;
        boolean flag = false;
        for (int i = 0; i < userCartProductList.size(); i++) {
            if (model.getId().equals(userCartProductList.get(i).getProduct().getId())) {
                flag = true;
                productCountModel = userCartProductList.get(i);
            }
        }
        if (flag) {
            count[0] = productCountModel.getQuantity();
            holder.count.setText("" + count[0]);
            holder.increase.setVisibility(View.VISIBLE);

            if (count[0] > 1) {
                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                holder.decrease.setVisibility(View.VISIBLE);
            } else {
                holder.decrease.setImageResource(R.drawable.delete);
                holder.decrease.setVisibility(View.VISIBLE);
            }
        } else {
            holder.count.setText("Add to cart");
            holder.increase.setVisibility(View.GONE);
            holder.decrease.setVisibility(View.GONE);

        }
        flag = false;

        holder.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {
                    if (count[0] > 1) {

                    } else {
                        holder.count.setText("" + count[0]);
                        holder.increase.setVisibility(View.VISIBLE);
                        holder.decrease.setVisibility(View.VISIBLE);
                        addToCartInterface.addedToCart(model, count[0], position);
                    }
                } else {
                    CommonUtils.showToast("Please connect to internet");
                }
            }
        });

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {

                    count[0] += 1;
                    holder.count.setText("" + count[0]);
                    holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                    addToCartInterface.quantityUpdate(model, count[0], position);
                } else {
                    CommonUtils.showToast("Please connect to internet");
                }
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {
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
                        holder.count.setText("Add to cart");
                        holder.increase.setVisibility(View.GONE);
                        holder.decrease.setVisibility(View.GONE);
                        addToCartInterface.deletedFromCart(model, position);

                    }
                } else {
                    CommonUtils.showToast("Please connect to internet");
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {
                    Intent i = new Intent(context, ViewProduct.class);
                    i.putExtra("productId", model.getId());
                    context.startActivity(i);
                } else {
                    CommonUtils.showToast("Please connect to internet");
                }
            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, count;
        ImageView image, increase, decrease;


        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            count = itemView.findViewById(R.id.count);


        }
    }

}
