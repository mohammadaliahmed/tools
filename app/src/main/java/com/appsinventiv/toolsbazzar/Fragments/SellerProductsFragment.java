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
    LinearLayoutManager layoutManager;
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
                                if (product.getVendor() != null && product.getVendor().getVendorId()!=null) {
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
