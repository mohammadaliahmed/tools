package com.appsinventiv.toolsbazzar.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.SizeChart;
import com.appsinventiv.toolsbazzar.Adapters.AttributesAdapter;
import com.appsinventiv.toolsbazzar.Adapters.RelatedProductsAdapter;
import com.appsinventiv.toolsbazzar.BottomSheets.ShowBottomDialog;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.NewProductModel;
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
import java.util.HashMap;

/**
 * Created by AliAh on 27/08/2018.
 */

public class MostLikedFragment extends Fragment {
    RecyclerView recyclerView;
    Context context;
    ArrayList<Product> productArrayList = new ArrayList<>();
    ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    ArrayList<String> userWishList = new ArrayList<>();

    RelatedProductsAdapter adapter;

    DatabaseReference mDatabase;
    ProgressBar progress;
    LinearLayoutManager layoutManager;
    String size = "", color = "";
    int selectedColor = -1;
    String colorSelected = "";
    int selected = -1;
    String sizeSelected = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_list_fragment, container, false);
        progress = rootView.findViewById(R.id.progress);
        recyclerView = rootView.findViewById(R.id.recycler_view_products);
        setUpRecycler();


        return rootView;
    }

    private void setUpRecycler() {
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new RelatedProductsAdapter(context, productArrayList, userCartProductList, userWishList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                size = "";
                color = "";
                if (product.getAttributesWithPics() != null) {
                    ShowBottomDialog.showSizeAndColorDialogNew(context, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                        @Override
                        public void onCancelled() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    if (product.getSizeList() != null && product.getColorList() != null) {
                        ShowBottomDialog.showSizeAndColorDialog(context, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                            @Override
                            public void onCancelled() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else if (product.getSizeList() != null && product.getColorList() == null) {
                        ShowBottomDialog.showSizeBottomDialog(context, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                            @Override
                            public void onCancelled() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else if (product.getColorList() != null && product.getSizeList() == null) {
                        ShowBottomDialog.showColorBottomDialog(context, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                            @Override
                            public void onCancelled() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else if (product.getColorList() == null && product.getSizeList() == null) {
                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                .child("cart").child(product.getId()).child("product").setValue(product);
                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());
                    }
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
                            if (product.getAttributesWithPics() != null && snapshot.child("newAttributes").getValue() != null) {
                                HashMap<String, ArrayList<NewProductModel>> newMap = new HashMap<>();
                                for (DataSnapshot color : snapshot.child("newAttributes").getChildren()) {
                                    ArrayList<NewProductModel> newProductModelArrayList = new ArrayList<>();
                                    for (DataSnapshot size : color.getChildren()) {
                                        NewProductModel countModel = size.getValue(NewProductModel.class);
                                        if (countModel != null) {
                                            newProductModelArrayList.add(countModel);
                                        }
                                        newMap.put(color.getKey(), newProductModelArrayList);
                                    }

                                }
                                product.setProductCountHashmap(newMap);

                            }

                            if (product.isActive()&& product.getSellerProductStatus().equalsIgnoreCase("approved")) {
                                productArrayList.add(product);

                            }

                        }
                    }
                    Collections.sort(productArrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product listData, Product t1) {
                            Integer ob1 = listData.getLikesCount();
                            Integer ob2 = t1.getLikesCount();

                            return ob2.compareTo(ob1);

                        }
                    });
                    mDatabase.removeEventListener(this);
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
    public void onResume() {
        super.onResume();
        getProductsFromDB();
        getUserCartProductsFromDB();
        getUserWishList();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
