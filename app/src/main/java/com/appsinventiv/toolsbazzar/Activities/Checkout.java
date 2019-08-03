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
import com.appsinventiv.toolsbazzar.Models.NewCartModel;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
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

import java.util.ArrayList;

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
    int current = 0;
    int listSize;
    ArrayList<String> fcmKeys = new ArrayList<>();
    ArrayList<String> vendorId = new ArrayList<>();
    boolean adminHasOrder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        this.setTitle("Checkout");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        listSize = NewCart.newCart.size();
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
                        checkoutFromCart(current);
                    }
                } else {


                    Intent i = new Intent(Checkout.this, No_Internet.class);
                    startActivity(i);

                }
            }
        });

    }

    private void checkoutFromCart(int val) {
        final NewCartModel model = NewCart.newCart.get(val);
//        for (final NewCartModel model : NewCart.newCart) {
        orderNumber = orderNumber + 1;
        OrderModel orderModel = new OrderModel("" + orderNumber,
                customer,
                model.getList(),
                Float.parseFloat("" + model.getTotal()),
                System.currentTimeMillis(),
                instructions.getText().toString() + " ",
                "23rd june",
                "33",
                "Pending",
                Float.parseFloat("" + model.getShippingCharges()),
                Float.parseFloat("" + model.getDeliveryCharges())
                , model.getName().equalsIgnoreCase("Fort City") ? "admin" : "seller",
                model.getVendorModel()
        );
        mDatabase.child("Orders").child("" + orderNumber).setValue(orderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateProductQuantityInDB(model.getList());
                if (model.getName().equalsIgnoreCase("Fort City")) {
//                    vendorId.add(adminFcmKey);
                    adminHasOrder = true;
                } else {
                    vendorId.add(model.getList().get(0).getProduct().getVendor().getUsername());
                    setOrderIDs(model.getList(), orderNumber);
                }

                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("Orders").child("" + orderNumber).setValue("" + orderNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        current = current + 1;
                        if (current < listSize) {
                            checkoutFromCart(current);
                        } else {
                            mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            getVendorsFcmKeys();
//                                            sendNotificationsAboutOrderToAll();
                                        }
                                    });

                        }
                    }
                });

            }
        });

    }

    private void getVendorsFcmKeys() {

        mDatabase.child("Sellers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        VendorModel model = snapshot.getValue(VendorModel.class);
                        if (model != null) {
                            if (vendorId.contains(model.getUsername())) {
                                fcmKeys.add(model.getFcmKey());
                            }
                        }
                    }
                    sendNotificationsAboutOrderToAll();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendNotificationsAboutOrderToAll() {
        if (fcmKeys.size() > 0) {
            for (String key : fcmKeys) {
                NotificationAsync notificationAsync = new NotificationAsync(Checkout.this);
                String NotificationTitle = "New order from " + SharedPrefs.getUsername();
                String NotificationMessage = "Click to view ";
                notificationAsync.execute("ali", key, NotificationTitle, NotificationMessage, "SellerOrder", "1");
            }
            if (adminHasOrder) {
                NotificationAsync notificationAsync = new NotificationAsync(Checkout.this);
                String NotificationTitle = "New order from " + SharedPrefs.getUsername();
                String NotificationMessage = "Click to view ";
                notificationAsync.execute("ali", adminFcmKey, NotificationTitle, NotificationMessage, "Order", "1");
            }
            Intent i = new Intent(Checkout.this, OrderPlaced.class);
            startActivity(i);
            finish();
        }
    }

    private void setOrderIDs(ArrayList<ProductCountModel> list, Long orderNumber) {
        mDatabase.child("Sellers").child(list.get(0).getProduct().getVendor().getUsername()).child("Orders").child("" + orderNumber).setValue("" + orderNumber);
    }

    private void updateProductQuantityInDB(ArrayList<ProductCountModel> userCartProductList) {
        for (ProductCountModel model : userCartProductList) {
            int qun = model.getProduct().getQuantityAvailable() - model.getQuantity();
            mDatabase.child("Products").child(model.getProduct().getId()).child("quantityAvailable").setValue(qun);
        }
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
