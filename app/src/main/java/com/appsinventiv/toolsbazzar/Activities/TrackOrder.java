package com.appsinventiv.toolsbazzar.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackOrder extends AppCompatActivity {
    String orderId;
    Button back;
    ImageView img;
    private AppBarLayout app_bar_layout;
    private CollapsingToolbarLayout collapsing_toolbar;
    private Toolbar toolbar;
    private MenuItem menuItem;
    private TextView textCartItemCount;
    private DatabaseReference mDatabase;
    private long cartItemCountFromDb;
    ImageView trackImage, trackImage2;
    TextView orderNumber, orderStatus, paymentMethod, shippingCarrier, trackingNumber, address, deliveredTo;
    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        date = findViewById(R.id.date);
        orderNumber = findViewById(R.id.orderNumber);
        orderStatus = findViewById(R.id.orderStatus);
        paymentMethod = findViewById(R.id.paymentMethod);
        shippingCarrier = findViewById(R.id.shippingCarrier);
        trackingNumber = findViewById(R.id.trackingNumber);
        address = findViewById(R.id.address);
        deliveredTo = findViewById(R.id.deliveredTo);
        trackImage = findViewById(R.id.trackImage);
        trackImage2 = findViewById(R.id.trackImage2);
        img = findViewById(R.id.img);
        app_bar_layout = findViewById(R.id.app_bar_layout);
        collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        back = findViewById(R.id.back);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        Intent i = getIntent();
        orderId = i.getStringExtra("orderId");

        getOrderDataFromDB();


        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == -collapsing_toolbar.getHeight() + toolbar.getHeight()) {
                    //toolbar is collapsed here
                    //write your code here
                    collapsing_toolbar.setTitle("Tracking Order #: " + orderId);
                    collapsing_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.default_back));


                } else {
                    collapsing_toolbar.setTitle("");
                }

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void getOrderDataFromDB() {
        mDatabase.child("Orders").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OrderModel model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {
                        orderStatus.setText("Order status: " + model.getOrderStatus());
                        orderNumber.setText("Order Number: " + model.getOrderId());
                        paymentMethod.setText("Payment method: Not available");
                        shippingCarrier.setText("Shipping Carrier: " + (model.getCarrier() == null ? "Not Available" : model.getCarrier()));
                        trackingNumber.setText("Tracking Number: " + (model.getTrackingNumber() == null ? "Not Available" : model.getTrackingNumber()));
                        address.setText("Address: " + (model.getCustomer().getAddress() == null ? "Not Available" : model.getCustomer().getAddress()));
                        deliveredTo.setText("Delivered to: " + (model.getReceiverName() == null ? "Not Available" : model.getReceiverName()));

                        date.setText("Date: " + CommonUtils.getFormattedDate(model.getTime()));

                        if (model.getOrderStatus().equalsIgnoreCase("pending")) {
                            collapsing_toolbar.setBackgroundColor(Color.parseColor("#DAF5C8"));
                            Glide.with(TrackOrder.this).load(R.drawable.ic_pending1).into(trackImage);
                            Glide.with(TrackOrder.this).load(R.drawable.ic_pending2).into(trackImage2);
                        } else if (model.getOrderStatus().equalsIgnoreCase("under process")) {
                            collapsing_toolbar.setBackgroundColor(Color.parseColor("#C0CDE2"));
                            Glide.with(TrackOrder.this).load(R.drawable.ic_under_process1).into(trackImage);
                            Glide.with(TrackOrder.this).load(R.drawable.ic_under_process2).into(trackImage2);
                        } else if (model.getOrderStatus().equalsIgnoreCase("shipped")) {
                            collapsing_toolbar.setBackgroundColor(Color.parseColor("#FFC8AD"));
                            Glide.with(TrackOrder.this).load(R.drawable.ic_shipped1).into(trackImage);
                            Glide.with(TrackOrder.this).load(R.drawable.ic_shipped2).into(trackImage2);
                        } else if (model.getOrderStatus().equalsIgnoreCase("delivered")) {
                            collapsing_toolbar.setBackgroundColor(Color.parseColor("#FFEFC2"));
                            Glide.with(TrackOrder.this).load(R.drawable.ic_delviered1).into(trackImage);
                            Glide.with(TrackOrder.this).load(R.drawable.ic_delviered2).into(trackImage2);
                        } else if (model.getOrderStatus().equalsIgnoreCase("cancelled")) {
                            collapsing_toolbar.setBackgroundColor(Color.parseColor("#FFFFC2C3"));
                            Glide.with(TrackOrder.this).load(R.drawable.ic_cancelled).into(trackImage);
                            Glide.with(TrackOrder.this).load(R.drawable.ic_cancelled2).into(trackImage2);
                        } else if (model.getOrderStatus().equalsIgnoreCase("out of stock")) {
                            collapsing_toolbar.setBackgroundColor(Color.parseColor("#FD8C8E"));
                            Glide.with(TrackOrder.this).load(R.drawable.ic_out_of_stock1).into(trackImage);
                            Glide.with(TrackOrder.this).load(R.drawable.ic_out_of_stock2).into(trackImage2);
                        } else if (model.getOrderStatus().equalsIgnoreCase("delivered by courier")) {
                            collapsing_toolbar.setBackgroundColor(Color.parseColor("#FFEFC2"));
                            Glide.with(TrackOrder.this).load(R.drawable.ic_delviered1).into(trackImage);
                            Glide.with(TrackOrder.this).load(R.drawable.ic_delviered2).into(trackImage2);
                        }  else if (model.getOrderStatus().equalsIgnoreCase("shipped by courier")) {
                            collapsing_toolbar.setBackgroundColor(Color.parseColor("#FFC8AD"));
                            Glide.with(TrackOrder.this).load(R.drawable.ic_shipped1).into(trackImage);
                            Glide.with(TrackOrder.this).load(R.drawable.ic_shipped2).into(trackImage2);
                        } else if (model.getOrderStatus().equalsIgnoreCase("refused by courier")) {
                            collapsing_toolbar.setBackgroundColor(Color.parseColor("#FD8C8E"));
                            Glide.with(TrackOrder.this).load(R.drawable.ic_cancelled).into(trackImage);
                            Glide.with(TrackOrder.this).load(R.drawable.ic_cancelled2).into(trackImage2);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        if (id == R.id.action_cart) {
            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
                CommonUtils.showToast("Your Cart is empty");
            } else {
                Intent i = new Intent(TrackOrder.this, NewCart.class);
                startActivity(i);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_product, menu);

        menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = actionView.findViewById(R.id.cart_badge);


        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItemCountFromDb = dataSnapshot.getChildrenCount();
                textCartItemCount.setText("" + cartItemCountFromDb);
                SharedPrefs.setCartCount("" + cartItemCountFromDb);
                if (dataSnapshot.getChildrenCount() == 0) {
                    SharedPrefs.setCartCount("0");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });


        return true;
    }

}
