package com.appsinventiv.toolsbazzar.Seller.SellerStore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Adapters.SellerStoreProductsAdapter;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersAdapter;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.appsinventiv.toolsbazzar.Utils.SwipeControllerActions;
import com.appsinventiv.toolsbazzar.Utils.SwipeToDeleteCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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


public class SellerStoreProductsFragment extends Fragment {


    private Context context;
    String text;
    private SellerStoreProductsAdapter adapter;
    DatabaseReference mDatabase;
    private ArrayList<Product> productArrayList=new ArrayList<>();
    private ArrayList<String> userWishList=new ArrayList<>();



    private String sellerId;

    public SellerStoreProductsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SellerStoreProductsFragment(String text,String sellerId) {
      this.text=text;
      this.sellerId=sellerId;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase=FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_seller_store
                , container, false);
        RecyclerView recyclerview = rootView.findViewById(R.id.recycler_orders);
        recyclerview.setLayoutManager(new GridLayoutManager(context, 2));
        adapter = new SellerStoreProductsAdapter(context, productArrayList, userWishList, new AddToCartInterface() {
            @Override
            public void addedToCart(Product product, int quantity, int position) {

            }

            @Override
            public void deletedFromCart(Product product, int position) {

            }

            @Override
            public void quantityUpdate(Product product, int quantity, int position) {

            }

            @Override
            public void isProductLiked(Product product, boolean isLiked, int position) {
                if (isLiked) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("WishList").child(product.getId()).setValue(product.getId())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    CommonUtils.showToast("Added to Wish list");
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
                            .child("WishList").child(product.getId()).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    CommonUtils.showToast("Removed from Wish list");

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
        recyclerview.setAdapter(adapter);


        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();
//        getSellerFromDB();
        getSellerProductsFromDB();
        getUserWishList();
    }


    private void getSellerProductsFromDB() {
        mDatabase.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    productArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {

                            if (product.getVendor() != null && product.getVendor().getVendorId() != null) {
                                if (product.getVendor().getVendorId().equalsIgnoreCase(sellerId)) {

                                    productArrayList.add(product);


                                }
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
//                    adapter.updatelist(productArrayList);

                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }


}
