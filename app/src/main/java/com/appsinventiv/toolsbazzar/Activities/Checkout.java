package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Interface.NotificationObserver;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.NotificationAsync;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Checkout extends AppCompatActivity implements NotificationObserver {
    Float grandTotal;
    RelativeLayout placeOrder, wholeLayout, progress;
    DatabaseReference mDatabase;
    TextView address, totalPrice;
    ImageView editAddress;
    EditText instructions;
    Customer customer;
    Long orderNumber = 10001L;
    String adminFcmKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        this.setTitle("Checkout");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent i = getIntent();
        grandTotal = i.getFloatExtra("grandTotal", 0);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        instructions = findViewById(R.id.instructions);
        placeOrder = findViewById(R.id.placeOrder);
        address = findViewById(R.id.address);
        totalPrice = findViewById(R.id.totalPrice);
        editAddress = findViewById(R.id.editAddress);
        progress = findViewById(R.id.progress);
        wholeLayout = findViewById(R.id.wholeLayout);

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Checkout.this, MyProfile.class);
                startActivity(i);
            }
        });

        totalPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", grandTotal * Float.parseFloat(SharedPrefs.getExchangeRate())));

        getOrdersCountFromDb();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    customer = dataSnapshot.child("Customers").child(SharedPrefs.getUsername()).getValue(Customer.class);
                    if (customer != null) {
                        address.setText(customer.getAddress() + " " + customer.getCity());
                    }
                    adminFcmKey = dataSnapshot.child("Admin").child("admin").child("fcmKey").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {
                    progress.setVisibility(View.VISIBLE);
                    if (customer != null) {
                        mDatabase.child("Orders").child("" + orderNumber)
                                .setValue(new OrderModel("" + orderNumber,
                                        customer,
                                        Cart.userCartProductList,
                                        Cart.grandTotalAmount,
                                        System.currentTimeMillis(),
                                        instructions.getText().toString() + " ",
                                        "23rd june",
                                        "33",
                                        "Pending",
                                        Cart.locationAndChargesModel.getDeliveryCharges(),
                                        Cart.locationAndChargesModel.getShippingCharges()
                                )).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("Orders").child("" + orderNumber).setValue("" + orderNumber);

                                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        NotificationAsync notificationAsync = new NotificationAsync(Checkout.this);
                                        String NotificationTitle = "New order from " + SharedPrefs.getUsername();
                                        String NotificationMessage = "Click to view ";
                                        notificationAsync.execute("ali", adminFcmKey, NotificationTitle, NotificationMessage, "Order", "1");

                                        Intent i = new Intent(Checkout.this, OrderPlaced.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                } else {
                    CommonUtils.showToast("Please connect to internet");
                }
            }
        });

    }

    private void getOrdersCountFromDb() {
        mDatabase.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    orderNumber = (dataSnapshot.getChildrenCount() + orderNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

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
}
