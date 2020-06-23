package com.appsinventiv.toolsbazzar.Seller.Reviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.NewProductModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerAddProduct;
import com.appsinventiv.toolsbazzar.Seller.SellerListOfProducts;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.Constants;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class SellerProductReviews extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Product> productArrayList = new ArrayList<>();
    SellerProductsReviewsAdapter adapter;
    DatabaseReference mDatabase;

    TextView positiveRatingText, positiveCount, neutralCount, negativeCount;
    ProgressBar positiveBar, neutralBar, negativeBar;

    float posCount, neutCount, negCount, totalReviewCount, totalPositiveCount;
    TextView totalReviews;
    FloatingActionButton fab;


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

        positiveCount = findViewById(R.id.positiveCount);
        fab = findViewById(R.id.fab);
        neutralCount = findViewById(R.id.neutralCount);
        negativeCount = findViewById(R.id.negativeCount);
        totalReviews = findViewById(R.id.totalReviews);
        positiveBar = findViewById(R.id.positiveBar);
        neutralBar = findViewById(R.id.neutralBar);
        negativeBar = findViewById(R.id.negativeBar);
        positiveRatingText = findViewById(R.id.positiveRatingText);

        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new SellerProductsReviewsAdapter(this, productArrayList);
        recyclerView.setAdapter(adapter);


        getProductsFromDB();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.ADDING_PRODUCT_BACK=false;
                Intent i = new Intent(SellerProductReviews.this, SellerAddProduct.class);
                startActivity(i);
            }
        });

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

                            if (product.getVendor() != null) {
                                if (product.getVendor() != null && product.getVendor().getVendorId() != null) {
                                    if (product.getVendor().getVendorId().equalsIgnoreCase(SharedPrefs.getVendor().getUsername())) {

                                        productArrayList.add(product);
                                        performCalculations();


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

    private void performCalculations() {
        totalReviewCount = 0;
        totalPositiveCount = 0;
        negCount = 0;
        posCount = 0;
        neutCount = 0;
        int personRated = 0;
        for (Product product : productArrayList) {
            totalReviewCount = totalReviewCount + product.getRatingCount();
            if (product.getRating() >= 3) {
                posCount = posCount + product.getPositiveCount();
                totalPositiveCount = totalPositiveCount + 1;
            } else if (product.getRating() > 2 && product.getRating() < 3) {
                neutCount = neutCount + product.getNeutralCount();
            } else {
                negCount = negCount + product.getNegativeCount();
            }
        }

        positiveCount.setText("" + posCount);
        negativeCount.setText("" + negCount);
        neutralCount.setText("" + neutCount);
        posCount = ((float) posCount / (float) totalReviewCount) * 100;
        neutCount = (neutCount / totalReviewCount) * 100;
        negCount = (negCount / totalReviewCount) * 100;
        positiveBar.setProgress((int) posCount);
        neutralBar.setProgress((int) neutCount);
        negativeBar.setProgress((int) negCount);
        float tt = ((float) totalPositiveCount / (float) totalReviewCount) * 100;
        positiveRatingText.setText(String.format("%.2f", tt) + "%");

        totalReviews.setText("" + totalReviewCount + " Customer reviews");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
