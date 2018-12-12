package com.appsinventiv.toolsbazzar.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.toolsbazzar.Activities.SizeChart;
import com.appsinventiv.toolsbazzar.Adapters.RelatedProductsAdapter;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
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

public class ProductListFragment extends Fragment {

    RecyclerView recyclerView;
    Context context;
    ArrayList<Product> productArrayList = new ArrayList<>();
    ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    ArrayList<String> userWishList = new ArrayList<>();

    RelatedProductsAdapter adapter;
    String category;

    DatabaseReference mDatabase;
    ProgressBar progress;
    String flag;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager layoutManager;
    String size = "", color = "";
    int selectedColor = -1;
    String colorSelected = "";
    int selected = -1;
    String sizeSelected = "";

    public ProductListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ProductListFragment(String category, String flag) {
        this.category = category;
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

        View rootView = inflater.inflate(R.layout.product_list_fragment, container, false);
        progress = rootView.findViewById(R.id.progress);
        recyclerView = rootView.findViewById(R.id.recycler_view_products);


        setUpRecycler();

        return rootView;

    }


    private void setUpRecycler() {
        int gridCount = 2;
        if (CommonUtils.screenSize() > 7) {
            gridCount = 3;
        }
        if (CommonUtils.screenSize() > 9) {
            gridCount = 4;
        }


        gridLayoutManager = new GridLayoutManager(context, gridCount);
        if (flag != null) {
            if (flag.equalsIgnoreCase("0")) {
                recyclerView.setLayoutManager(layoutManager);
            } else if (flag.equalsIgnoreCase("1")) {
                recyclerView.setLayoutManager(gridLayoutManager);
            }
        }
        adapter = new RelatedProductsAdapter(context, productArrayList, userCartProductList, userWishList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                size = "";
                color = "";
                if (product.getSizeList() != null && product.getColorList() != null) {
                    showSizeAndColorBottomDialog(product, quantity);
                } else if (product.getSizeList() != null && product.getColorList() == null) {
                    showSizeBottomDialog(product, quantity);
                } else if (product.getColorList() != null && product.getSizeList() == null) {
                    showColorBottomDialog(product, quantity);
                } else if (product.getColorList() == null && product.getSizeList() == null) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());
                }

            }

            @Override
            public void deletedFromCart(final Product product, final int position) {

                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }

            @Override
            public void quantityUpdate(Product product, final int quantity, int position) {
                if (product != null) {
                    Log.d("here", userCartProductList.get(0).getProduct().getTitle() + "");
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

//                                    adapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

            }

            @Override
            public void isProductLiked(Product product, final boolean isLiked, int position) {
                if (isLiked) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("WishList").child(product.getId()).setValue(product.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Added to Wishlist");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonUtils.showToast("Error");

                        }
                    });
                    int likesCount = product.getLikesCount();
                    likesCount += 1;
                    mDatabase.child("Products").child(product.getId()).child("likesCount").setValue(likesCount);
                } else {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("WishList").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            CommonUtils.showToast("Removed from Wishlist");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonUtils.showToast("Error");

                        }
                    });
                    int likesCount = product.getLikesCount();
                    likesCount -= 1;
                    mDatabase.child("Products").child(product.getId()).child("likesCount").setValue(likesCount);
                }

            }
        });
        recyclerView.setAdapter(adapter);

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

        title.setText(product.getTitle());
        price.setText("Rs: " + product.getRetailPrice());
        Glide.with(context).load(product.getThumbnailUrl()).into(image);

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
                if (!colorSelected.equalsIgnoreCase("")) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());


                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("color").setValue(colorSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            colorSelected = "";
                            selectedColor = -1;
                            sizeSelected = "";
                            selected = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select color");

                }
            }
        });
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
        price.setText("Rs: " + product.getRetailPrice());
        Glide.with(context).load(product.getThumbnailUrl()).into(image);


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
                if (!sizeSelected.equalsIgnoreCase("")) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("size").setValue(sizeSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sizeSelected = "";
                            selected = -1;
                            colorSelected = "";
                            selectedColor = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select size ");

                }
            }
        });
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

        title.setText(product.getTitle());
        price.setText("Rs: " + product.getRetailPrice());
        Glide.with(context).load(product.getThumbnailUrl()).into(image);

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
                if (!sizeSelected.equalsIgnoreCase("") && !colorSelected.equalsIgnoreCase("")) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("size").setValue(sizeSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sizeSelected = "";
                            selected = -1;
                        }
                    });
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("color").setValue(colorSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            colorSelected = "";
                            selectedColor = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select size and color");

                }
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
        params.setMargins(10, 1, 10, 1);

        for (int i = 0; i < product.getSizeList().size(); i++) {
            Button btn = new Button(context);
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.size_button_layout);
            btn.setText("" + product.getSizeList().get(i));
            sizes.addView(btn);
            btn.setTextSize(8);
            btn.setId(i);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selected == -1) {
                        selected = view.getId();

                    } else {
                        sizes.getChildAt(selected).setBackgroundResource(R.drawable.size_button_layout);
                        selected = view.getId();
                    }
                    sizes.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                    sizeSelected = product.getSizeList().get(selected);
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
        params.setMargins(10, 1, 10, 1);
        if (product.getColorList() != null) {
            for (int i = 0; i < product.getColorList().size(); i++) {
                Button btn = new Button(context);
                btn.setLayoutParams(params);
                btn.setBackgroundResource(R.drawable.size_button_layout);
                btn.setText("" + product.getColorList().get(i));
                btn.setTextSize(8);
                colors.addView(btn);
                btn.setId(i);


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedColor == -1) {
                            selectedColor = view.getId();

                        } else {
                            colors.getChildAt(selectedColor).setBackgroundResource(R.drawable.size_button_layout);
                            selectedColor = view.getId();
                        }
                        colors.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                        colorSelected = product.getColorList().get(selectedColor);
                    }
                });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void getUserWishList() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("WishList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userWishList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String productId = snapshot.getValue(String.class);
                        if (productId != null) {
                            userWishList.add(productId);
                        }
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    userWishList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserCartProductsFromDB() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    progress.setVisibility(View.GONE);
                    userCartProductList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel product = snapshot.getValue(ProductCountModel.class);
                        if (product != null) {
                            userCartProductList.add(product);
                            Collections.sort(userCartProductList, new Comparator<ProductCountModel>() {
                                @Override
                                public int compare(ProductCountModel listData, ProductCountModel t1) {
                                    Long ob1 = listData.getTime();
                                    Long ob2 = t1.getTime();
                                    return ob2.compareTo(ob1);

                                }
                            });
                        }
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    userCartProductList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getProductsFromDB() {

        mDatabase.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    progress.setVisibility(View.GONE);
                    productArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            if (product.getIsActive().equals("true")) {
                                if (product.getSellingTo().equalsIgnoreCase("Both") || product.getSellingTo().equalsIgnoreCase(SharedPrefs.getCustomerType())) {

                                    if (category == null || category.equalsIgnoreCase("All")) {
                                        productArrayList.add(product);

                                    } else if (product.getCategory() != null) {
                                        if (product.getCategory().contains(category)) {
                                            productArrayList.add(product);

                                        }
                                    }
                                }

                                Collections.sort(productArrayList, new Comparator<Product>() {
                                    @Override
                                    public int compare(Product listData, Product t1) {
                                        String ob1 = listData.getTitle();
                                        String ob2 = t1.getTitle();

                                        return ob1.compareTo(ob2);

                                    }
                                });
                            }

                        }
                    }
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


        getProductsFromDB();
        getUserCartProductsFromDB();
        getUserWishList();
        super.onResume();
    }

}
