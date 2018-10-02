package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 22/06/2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    AddToCartInterface addToCartInterface;
    ArrayList<ProductCountModel> userCartProductList;

    public CartAdapter(Context context, ArrayList<ProductCountModel> userCartProductList, AddToCartInterface addToCartInterface) {
        this.context = context;
        this.addToCartInterface = addToCartInterface;
        this.userCartProductList = userCartProductList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item_layout, parent, false);
        CartAdapter.ViewHolder viewHolder = new CartAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ProductCountModel model = userCartProductList.get(position);
        holder.title.setText(model.getProduct().getTitle());

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            holder.price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getProduct().getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            holder.price.setText("Rs. " + model.getProduct().getWholeSalePrice());
        }

        if (model.getSize() != null && !model.getSize().equalsIgnoreCase("")) {
            holder.size.setText("Size: " + model.getSize());
        }else{
            holder.size.setVisibility(View.GONE);
        }
        if (model.getColor() != null && !model.getColor().equalsIgnoreCase("")) {
            holder.color.setText("Color: " + model.getColor());
        }else{
            holder.color.setVisibility(View.GONE);
        }


        holder.subtitle.setText(model.getProduct().getMeasurement());
        Glide.with(context).load(model.getProduct().getThumbnailUrl()).into(holder.image);

        holder.viewProduct.setVisibility(View.GONE);

        final int[] count = {model.getQuantity()};
        holder.increase.setVisibility(View.VISIBLE);
        holder.count.setText("" + count[0]);
        if (count[0] > 1) {
            holder.count.setOnClickListener(null);
            holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
            holder.decrease.setVisibility(View.VISIBLE);
        } else {
            holder.count.setOnClickListener(null);
            holder.decrease.setImageResource(R.drawable.delete);
            holder.decrease.setVisibility(View.VISIBLE);


        }

        holder.count.setTextColor(context.getResources().getColor(R.color.default_grey_text));

        holder.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.count.setText("" + count[0]);
                holder.increase.setVisibility(View.VISIBLE);
                holder.decrease.setVisibility(View.VISIBLE);
                addToCartInterface.addedToCart(model.getProduct(), count[0], position);

            }
        });
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count[0] += 1;
                holder.count.setText("" + count[0]);
                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                addToCartInterface.quantityUpdate(model.getProduct(), count[0], position);

            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                    if (count[0] > (model.getProduct().getMinOrderQuantity() + 1)) {
                        holder.count.setTextColor(context.getResources().getColor(R.color.default_grey_text));

                        count[0] -= 1;
                        holder.count.setText("" + count[0]);
                        addToCartInterface.quantityUpdate(model.getProduct(), count[0], position);

                    } else if (count[0] > model.getProduct().getMinOrderQuantity()) {
                        {
                            holder.count.setTextColor(context.getResources().getColor(R.color.default_grey_text));

                            count[0] -= 1;
                            holder.count.setText("" + count[0]);
                            holder.decrease.setImageResource(R.drawable.delete);
                            addToCartInterface.quantityUpdate(model.getProduct(), count[0], position);


                        }
                    } else if (count[0] == model.getProduct().getMinOrderQuantity()) {
//                        holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
                        holder.count.setText("Add to cart");
                        holder.count.setTextColor(context.getResources().getColor(R.color.colorWhite));
                        holder.increase.setVisibility(View.GONE);
                        holder.decrease.setVisibility(View.GONE);
                        addToCartInterface.deletedFromCart(model.getProduct(), position);

                    }
                } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                    if (count[0] > 2) {
                        holder.count.setTextColor(context.getResources().getColor(R.color.default_grey_text));

                        count[0] -= 1;
                        holder.count.setText("" + count[0]);
                        addToCartInterface.quantityUpdate(model.getProduct(), count[0], position);


                    } else if (count[0] > 1) {
                        {
                            holder.count.setTextColor(context.getResources().getColor(R.color.default_grey_text));

                            count[0] -= 1;
                            holder.count.setText("" + count[0]);
                            holder.decrease.setImageResource(R.drawable.delete);
                            addToCartInterface.quantityUpdate(model.getProduct(), count[0], position);


                        }
                    } else if (count[0] == 1) {

                        holder.count.setText("Add to cart");
                        holder.increase.setVisibility(View.GONE);
                        holder.decrease.setVisibility(View.GONE);
                        addToCartInterface.deletedFromCart(model.getProduct(), position);

                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userCartProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, count, size, color;
        ImageView image, increase, decrease, viewProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            count = itemView.findViewById(R.id.count);
            viewProduct = itemView.findViewById(R.id.viewProduct);
            size = itemView.findViewById(R.id.size);
            color = itemView.findViewById(R.id.color);


        }
    }
}
