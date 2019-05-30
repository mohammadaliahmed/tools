package com.appsinventiv.toolsbazzar.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.SizeChart;
import com.appsinventiv.toolsbazzar.Adapters.RelatedProductsAdapter;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerProductsAdapter;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SellerProductsFragment extends Fragment {

    RecyclerView recyclerView;
    Context context;
    ArrayList<Product> productArrayList = new ArrayList<>();
    ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    ArrayList<String> userWishList = new ArrayList<>();

    SellerProductsAdapter adapter;
    String sellerProductStatus;

    DatabaseReference mDatabase;
    ProgressBar progress;
    String flag;
    GridLayoutManager gridLayoutManager;
    String size = "", color = "";
    int selectedColor = -1;
    String colorSelected = "";
    int selected = -1;
    String sizeSelected = "";


    public SellerProductsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SellerProductsFragment(String sellerProductStatus, String flag) {
        this.sellerProductStatus = sellerProductStatus;
        this.flag = flag;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.seller_product_list_fragment, container, false);
        progress = rootView.findViewById(R.id.progress);
        recyclerView = rootView.findViewById(R.id.recycler_view_products);


        setUpRecycler();

        return rootView;

    }


    private void setUpRecycler() {
        int gridCount = 2;
//        if (CommonUtils.screenSize() > 7) {
//            gridCount = 3;
//        }
//        if (CommonUtils.screenSize() > 9) {
//            gridCount = 4;
//        }


        gridLayoutManager = new GridLayoutManager(context, gridCount);
//        if (flag != null) {
//            if (flag.equalsIgnoreCase("0")) {
//                recyclerView.setLayoutManager(layoutManager);
//            } else if (flag.equalsIgnoreCase("1")) {
//                recyclerView.setLayoutManager(gridLayoutManager);
//            }
//        }
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new SellerProductsAdapter(context, productArrayList);
        recyclerView.setAdapter(adapter);

        adapter.setCallbacks(new SellerProductsAdapter.SellerProductsAdapterCallbacks() {
            @Override
            public void onOptionClicked(Product product) {
                if (product.getSizeList() != null && product.getColorList() != null) {
                    showSizeAndColorBottomDialog(product, 1);
                } else if (product.getSizeList() != null && product.getColorList() == null) {
                    showSizeBottomDialog(product, 1);
                } else if (product.getColorList() != null && product.getSizeList() == null) {
                    showColorBottomDialog(product, 1);
                } else {
//                    showEmptyBottomDialog(product);
                }
            }
        });

    }

    private void showColorBottomDialog(final Product product, final int quantity) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_color_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(getContext())
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        TextView title = customView.findViewById(R.id.title);
        TextView price = customView.findViewById(R.id.price);
        ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        Button confirm = customView.findViewById(R.id.confirm);
        LinearLayout sizes = customView.findViewById(R.id.sizes);
        LinearLayout colors = customView.findViewById(R.id.colors);
        TextView percentageOff = customView.findViewById(R.id.percentageOff);
        TextView oldPrice = customView.findViewById(R.id.oldPrice);
        TextView quantityText = customView.findViewById(R.id.quantityText);
        ImageView whichArrow = customView.findViewById(R.id.whichArrow);

        if (product.getQuantityAvailable() == 0) {

            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");


        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());


        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));

            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        title.setText(product.getTitle());
        Glide.with(context).load(product.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(image);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        confirm.setText(sellerProductStatus);

        initializeColorButtons(product, colors);

        bottomDialog.show();
    }

    private void showSizeBottomDialog(final Product product, final int quantity) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_size_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(getContext())
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        TextView title = customView.findViewById(R.id.title);
        TextView price = customView.findViewById(R.id.price);
        TextView percentageOff = customView.findViewById(R.id.percentageOff);
        TextView oldPrice = customView.findViewById(R.id.oldPrice);
        TextView quantityText = customView.findViewById(R.id.quantityText);
        ImageView whichArrow = customView.findViewById(R.id.whichArrow);

        if (product.getQuantityAvailable() == 0) {
            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");
        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());
        }

        if (product.getQuantityAvailable() == 0) {

            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");


        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());


        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));

            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        final Button confirm = customView.findViewById(R.id.confirm);
        LinearLayout sizes = customView.findViewById(R.id.sizes);


        TextView sizeChart = customView.findViewById(R.id.sizeChart);

        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SizeChart.class);
                context.startActivity(i);
            }
        });

        title.setText(product.getTitle());

        Glide.with(context).load(product.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(image);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        confirm.setText(sellerProductStatus);

        initiliazeSizeButtons(product, sizes);

        bottomDialog.show();
    }

    private void showSizeAndColorBottomDialog(final Product product, final int quantity) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_size_color_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(getContext())
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        TextView title = customView.findViewById(R.id.title);
        TextView price = customView.findViewById(R.id.price);
        ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        Button confirm = customView.findViewById(R.id.confirm);
        LinearLayout sizes = customView.findViewById(R.id.sizes);
        LinearLayout colors = customView.findViewById(R.id.colors);
        TextView sizeChart = customView.findViewById(R.id.sizeChart);
        TextView percentageOff = customView.findViewById(R.id.percentageOff);
        TextView oldPrice = customView.findViewById(R.id.oldPrice);
        TextView quantityText = customView.findViewById(R.id.quantityText);
        ImageView whichArrow = customView.findViewById(R.id.whichArrow);
        confirm.setText(sellerProductStatus);

        if (product.getQuantityAvailable() == 0) {

            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");


        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());


        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));

            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        title.setText(product.getTitle());
        Glide.with(context).load(product.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(image);

        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SizeChart.class);
                context.startActivity(i);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        initializeColorButtons(product, colors);
        initiliazeSizeButtons(product, sizes);

        bottomDialog.show();
    }

    @SuppressLint("ResourceAsColor")
    public void initiliazeSizeButtons(final Product product, final LinearLayout sizes) {
        sizes.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 120;
        params.width = 130;
        params.setMargins(5, 1, 5, 1);
        final ArrayList<Button> btnList = new ArrayList<>();

        for (int i = 0; i < product.getSizeList().size(); i++) {
            final Button btn = new Button(context);
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.size_button_layout);
            btn.setText("" + product.getSizeList().get(i));
            sizes.addView(btn);
            btn.setTextSize(9);
            btn.setId(i);
            btnList.add(btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selected == -1) {
                        selected = view.getId();

                    } else {
                        sizes.getChildAt(selected).setBackgroundResource(R.drawable.size_button_layout);


                        selected = view.getId();
                    }
                    for (Button j : btnList) {

                        j.setTextColor(getResources().getColor(R.color.default_grey_text));

                    }

                    sizes.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                    sizeSelected = product.getSizeList().get(selected);
                    btn.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    public void initializeColorButtons(final Product product, final LinearLayout colors) {
        colors.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 120;
        params.width = 170;
        params.setMargins(5, 1, 5, 1);
        final ArrayList<Button> btnList = new ArrayList<>();
        if (product.getColorList() != null) {
            for (int i = 0; i < product.getColorList().size(); i++) {
                final Button btn = new Button(context);
                btn.setLayoutParams(params);
                btn.setBackgroundResource(R.drawable.size_button_layout);
                btn.setText("" + product.getColorList().get(i));
                btn.setTextSize(9);
                colors.addView(btn);
                btn.setId(i);
                btnList.add(btn);


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedColor == -1) {
                            selectedColor = view.getId();

                        } else {
                            colors.getChildAt(selectedColor).setBackgroundResource(R.drawable.size_button_layout);
                            selectedColor = view.getId();

                        }
                        for (Button j : btnList) {

                            j.setTextColor(getResources().getColor(R.color.default_grey_text));

                        }

                        colors.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                        colorSelected = product.getColorList().get(selectedColor);
                        btn.setTextColor(getResources().getColor(R.color.colorPrimary));

                    }
                });
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();


    }


    private void getProductsFromDB() {

        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    progress.setVisibility(View.GONE);
                    productArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            if (product.getVendor() != null) {
                                if (product.getVendor() != null && product.getVendor().getVendorId() != null) {
                                    if (product.getVendor().getVendorId().equalsIgnoreCase(SharedPrefs.getVendor().getVendorId())) {

                                        if (product.getSellerProductStatus() != null) {
                                            if (product.getSellerProductStatus().equalsIgnoreCase(sellerProductStatus)) {
                                                productArrayList.add(product);

                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                    Collections.sort(productArrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product listData, Product t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob2.compareTo(ob1);
                        }
                    });
//                    adapter.updateList(productArrayList);
                    adapter.notifyDataSetChanged();
                } else {
                    CommonUtils.showToast("No Data");
                    progress.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {

//        if (sellerProductStatus.equalsIgnoreCase("All")) {
        getProductsFromDB();
//        } else {
//            getCategoryProductsFromDB();
//
//
//        }
//        getUserCartProductsFromDB();
//        getUserWishList();
        super.onResume();
    }

}
