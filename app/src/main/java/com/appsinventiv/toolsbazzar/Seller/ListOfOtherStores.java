package com.appsinventiv.toolsbazzar.Seller;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appsinventiv.toolsbazzar.ListOfStrores.ListOfStroesAdapter;
import com.appsinventiv.toolsbazzar.ListOfStrores.StoreCallbacks;
import com.appsinventiv.toolsbazzar.ListOfStrores.StoreListModel;
import com.appsinventiv.toolsbazzar.Models.NewProductModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListOfOtherStores extends AppCompatActivity {

    DatabaseReference mDatabase;
    RecyclerView recyclerview;
    ArrayList<StoreListModel> itemList = new ArrayList<>();
    StoreListAdapter adapter;
    ArrayList<VendorModel> vendors = new ArrayList<>();
    ArrayList<Product> productArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_other_stores);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.setTitle("Stores");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        recyclerview = findViewById(R.id.recyclerview);


        setUpRecycler();
        getProductss();
    }


    private void setUpRecycler() {
        recyclerview.setLayoutManager(new LinearLayoutManager(ListOfOtherStores.this, LinearLayoutManager.VERTICAL, false));

        adapter = new StoreListAdapter(ListOfOtherStores.this, itemList);
        recyclerview.setAdapter(adapter);


    }

    private void getProductss() {
        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
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

                            productArrayList.add(product);
                        }
                    }
                    if (productArrayList.size() > 0) {
                        getStoreListFromDB();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getStoreListFromDB() {
        mDatabase.child("Sellers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        VendorModel model = snapshot.getValue(VendorModel.class);
                        if (model != null) {
                            int size = vendors.size();
                            vendors.add(model);
                            getProductsFromDB(model, size);

                        }
                    }
                    getFortCityProducts();

                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFortCityProducts() {
        ArrayList<String> image = new ArrayList<>();
        for (Product product : productArrayList) {
            if (product != null) {
                if (product.getUploadedBy() != null && product.getSellerProductStatus() != null) {
                    if (product.getUploadedBy().equalsIgnoreCase("admin") && product.getSellerProductStatus().equalsIgnoreCase("Approved")) {
                        image.add(product.getThumbnailUrl());
                    }
                }
            }

        }
        VendorModel vendor = new VendorModel();
        vendor.setUsername("Fort City");
        vendor.setStoreName("Fort City");
        itemList.add(new StoreListModel(vendor, image));

        adapter.notifyDataSetChanged();
    }

    private void getProductsFromDB(final VendorModel vendor, final int size) {

        ArrayList<String> image = new ArrayList<>();
        for (Product product : productArrayList) {
            if (product != null) {
                if (product.getSellerProductStatus() != null && product.getSellerProductStatus().equalsIgnoreCase("Approved")) {
                    if (product.getId() != null && vendor.getProducts() != null) {
                        for (Map.Entry<String, String> entry : vendor.getProducts().entrySet()) {
                            String key = entry.getKey();
                            if (key.equalsIgnoreCase(product.getId())) {
                                image.add(product.getThumbnailUrl());
                            }
                        }
                    }


                }
            }

        }
        vendors.get(size).setProductsimages(image);
        if (vendors.get(size).getProductsimages().size() > 5) {
            itemList.add(new StoreListModel(vendor, vendors.get(size).getProductsimages()));
        } else {
        }
        adapter.notifyDataSetChanged();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
