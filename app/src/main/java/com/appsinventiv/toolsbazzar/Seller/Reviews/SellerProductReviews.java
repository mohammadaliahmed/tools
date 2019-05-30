package com.appsinventiv.toolsbazzar.Seller.Reviews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.appsinventiv.toolsbazzar.Activities.MainActivity;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerMainActivity;
import com.appsinventiv.toolsbazzar.Seller.SellerProductsAdapter;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SellerProductReviews extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Product> productArrayList = new ArrayList<>();
    SellerProductsReviewsAdapter adapter;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_revieiws);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new SellerProductsReviewsAdapter(this, productArrayList);
        recyclerView.setAdapter(adapter);


        getProductsFromDB();

    }

    private void getProductsFromDB() {

        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    productArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            if (product.getVendor() != null) {
                                if (product.getVendor() != null && product.getVendor().getVendorId() != null) {
                                    if (product.getVendor().getVendorId().equalsIgnoreCase(SharedPrefs.getVendor().getUsername())) {

                                        productArrayList.add(product);


                                    }
//
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
                    adapter.notifyDataSetChanged();
                } else {
                    CommonUtils.showToast("No Data");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent i = new Intent(SellerProductReviews.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
