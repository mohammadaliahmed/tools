package com.appsinventiv.toolsbazzar.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ViewProduct;
import com.appsinventiv.toolsbazzar.Adapters.CommentsAdapter;
import com.appsinventiv.toolsbazzar.Interface.NotificationObserver;
import com.appsinventiv.toolsbazzar.Models.CommentsModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.NotificationAsync;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SellerProductComments extends AppCompatActivity implements NotificationObserver {
    CommentsAdapter adapter;
    RecyclerView recyclerView;
    EditText comment;
    ImageView send;
    DatabaseReference mDatabase;
    String productId;
    ArrayList<CommentsModel> itemList = new ArrayList<>();
    ImageView productImage;
    TextView title, price;
    CardView productLayout;
    private Product product;
    String commentText=" ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_comments);
        this.setTitle("Q&A");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
        comment = findViewById(R.id.comment);
        send = findViewById(R.id.send);
        productImage = findViewById(R.id.productImage);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        productLayout = findViewById(R.id.productLayout);

        productId = getIntent().getStringExtra("productId");
        productLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SharedPrefs.getVendor()!=null){
                    Intent intent = new Intent(SellerProductComments.this, SellerViewProduct.class);
                    intent.putExtra("productId", productId);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(SellerProductComments.this, ViewProduct.class);
                    intent.putExtra("productId", productId);
                    startActivity(intent);
                    finish();
                }
            }
        });
        getProductFromDB();
        getCommentsDromDB();


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CommentsAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comment.getText().length() == 0) {
                    comment.setError("Empty");
                } else {
                    postComment();
                }
            }
        });

    }

    private void getProductFromDB() {
        mDatabase.child("Products").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    product = dataSnapshot.getValue(Product.class);
                    if (product != null) {

                        Glide.with(SellerProductComments.this).load(product.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(productImage);
                        title.setText(product.getTitle());
                        if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                            if (product.getOldRetailPrice() != 0) {
                            }
                        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));


                        }
                        if (product.getUploadedBy() != null && product.getUploadedBy().equalsIgnoreCase("seller")) {
                            if(product.getVendor()!=null)
                                getVendorDetailsFromDB(product.getVendor().getUsername());
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getVendorDetailsFromDB(String username) {
        mDatabase.child("Sellers").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    VendorModel model = dataSnapshot.getValue(VendorModel.class);
                    if (model != null) {
                        adapter.setVendor(model);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getCommentsDromDB() {

        mDatabase.child("Comments").child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CommentsModel model = snapshot.getValue(CommentsModel.class);
                        if (model != null) {
                            itemList.add(model);

                        }
                    }
                    if (product != null) {
                        adapter.setProduct(product);
                    }

                    Collections.sort(itemList, new Comparator<CommentsModel>() {
                        @Override
                        public int compare(CommentsModel listData, CommentsModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob1.compareTo(ob2);

                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        String key = mDatabase.push().getKey();
        mDatabase.child("Comments").child(productId).child(key)
                .setValue(new CommentsModel(key,
                        productId,
                        SharedPrefs.getUsername(),
                        SharedPrefs.getName()
                        , comment.getText().toString()
                        , System.currentTimeMillis(),
                        SharedPrefs.getVendor().getPicUrl()
                ))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        commentText=comment.getText().toString();
                        comment.setText("");
//
//                        NotificationAsync notificationAsync = new NotificationAsync(SellerProductComments.this);
//                        String NotificationTitle = "New comment on " + product.getTitle() + " by " + SharedPrefs.getUsername();
//                        String NotificationMessage = "Comment: " + commentText;
//                        if (product.getUploadedBy().equalsIgnoreCase("seller")) {
////                            if (SharedPrefs.getVendor().getUsername().equalsIgnoreCase(product.getUploadedBy())) {
////
////                            } else {
//                            notificationAsync.execute("ali", SharedPrefs.getVendor().getFcmKey(), NotificationTitle, NotificationMessage, "NewComment", productId);
////                            }
//                        } else {
//                            notificationAsync.execute("ali", SharedPrefs.getAdminFcmKey(), NotificationTitle, NotificationMessage, "NewComment", productId);
//                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}
