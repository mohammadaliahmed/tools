package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import java.util.Locale;

/**
 * Created by AliAh on 20/06/2018.
 */

public class SearchProductsAdapter extends RecyclerView.Adapter<SearchProductsAdapter.ViewHolder> {
    Context context;
    ArrayList<Product> productList;
    AddToCartInterface addToCartInterface;
    ArrayList<ProductCountModel> userCartProductList;
    ArrayList<String> userWishList;
    private ArrayList<Product> arrayList;
    boolean showPercentage = false;

    public SearchProductsAdapter(Context context,
                                 ArrayList<Product> productList,
                                 ArrayList<ProductCountModel> userCartProductList,
                                 ArrayList<String> userWishList,
                                 AddToCartInterface addToCartInterface,
                                 boolean showPercentage

    ) {
        this.context = context;
        this.productList = productList;
        this.addToCartInterface = addToCartInterface;
        this.userWishList = userWishList;
        this.userCartProductList = userCartProductList;
        this.arrayList = new ArrayList<>(productList);
        this.showPercentage = showPercentage;
//        this.arrayList.addAll(productList);


    }

    public void updatelist(ArrayList<Product> productList) {
        this.productList = productList;
        arrayList.clear();
        arrayList.addAll(productList);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        productList.clear();
        if (charText.length() == 0) {
            productList.addAll(arrayList);
        } else {
            for (Product product : arrayList) {
                if (product.getTitle().toLowerCase().contains(charText) ||
                        product.getSubtitle().toLowerCase().contains(charText) ||
                        product.getSubCategory().toLowerCase().contains(charText) ||
                        product.getMainCategory().toLowerCase().contains(charText)
                        ) {
                    productList.add(product);
                }
            }


        }
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wish_list_adapter, parent, false);
        SearchProductsAdapter.ViewHolder viewHolder = new SearchProductsAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Product model = productList.get(position);

        holder.title.setText(model.getTitle());
        if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            holder.price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (model.getOldRetailPrice() != 0) {
                holder.oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));

                String percent = "" + String.format("%.0f", ((model.getOldRetailPrice() - model.getRetailPrice()) / model.getOldRetailPrice()) * 100);


                holder.percentageOff.setVisibility(View.VISIBLE);
                holder.percentageOff.setText(percent + "% Off");

            } else {
                holder.oldPrice.setText("");
                holder.percentageOff.setVisibility(View.GONE);
            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            holder.price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (model.getOldWholeSalePrice() != 0) {
                holder.oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", model.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((model.getWholeSalePrice() - model.getWholeSalePrice()) / model.getWholeSalePrice()) * 100);


                holder.percentageOff.setVisibility(View.VISIBLE);

                holder.percentageOff.setText(percent + "% Off");


            } else {
                holder.oldPrice.setText("");
                holder.percentageOff.setVisibility(View.GONE);

            }
        }
        holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        holder.subtitle.setText(model.getSubtitle());
        Glide.with(context).load(model.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(holder.image);

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

                    if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                        if (model.getQuantityAvailable() < model.getMinOrderQuantity()) {
                            CommonUtils.showToast("Out of stock");

                        } else {
                            CommonUtils.showToast("Minimum order qty: " + model.getMinOrderQuantity());
                            holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                            holder.count.setTextColor(context.getResources().getColor(R.color.default_grey_text));

                            holder.count.setText("" + count[0]);
                            holder.increase.setVisibility(View.VISIBLE);
                            holder.decrease.setVisibility(View.VISIBLE);
                            addToCartInterface.addedToCart(model, model.getMinOrderQuantity(), position);
                        }
                    } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
//                        if (model.getQuantityAvailable() == 0) {
//                            CommonUtils.showToast("Not available in stock");
//                        } else {
                            holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                            holder.count.setTextColor(context.getResources().getColor(R.color.default_grey_text));

                            holder.count.setText("" + count[0]);
                            holder.increase.setVisibility(View.VISIBLE);
                            holder.decrease.setVisibility(View.VISIBLE);
                            addToCartInterface.addedToCart(model, count[0], position);
                        }
//                    }

                }
            }
        });
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count[0] >= model.getQuantityAvailable()) {
                    CommonUtils.showToast("Only " + model.getQuantityAvailable() + " available");
                } else {
                    count[0] += 1;
                    holder.count.setText("" + count[0]);
                    holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                    if (model != null)
                        addToCartInterface.quantityUpdate(model, count[0], position);
                }
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                    if (count[0] > (model.getMinOrderQuantity() + 1)) {
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
        TextView title, subtitle, price, count, oldPrice, percentageOff;
        ImageView image, increase, decrease;
        RelativeLayout relativeLayout;
        LikeButton heart_button;


        public ViewHolder(View itemView) {
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
            oldPrice = itemView.findViewById(R.id.oldPrice);
            percentageOff = itemView.findViewById(R.id.percentageOff);


        }
    }

}
