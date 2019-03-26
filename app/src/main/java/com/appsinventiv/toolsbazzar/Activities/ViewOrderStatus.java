package com.appsinventiv.toolsbazzar.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrderedProductsAdapter;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewOrderStatus extends AppCompatActivity {
    String orderIdFromIntent;
    RecyclerView recyclerView;
    TextView orderId, orderCharges, totalAmount, orderTime, orderDate, orderStatus, vendorName;
    Button track;
    DatabaseReference mDatabase;
    OrderModel model;
    ImageView delete;
    OrderedProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_status);
        orderIdFromIntent = getIntent().getStringExtra("orderId");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.setTitle("Order # " + orderIdFromIntent);
        recyclerView = findViewById(R.id.recyclerView);
        delete = findViewById(R.id.delete);
        orderId = findViewById(R.id.orderId);
        orderCharges = findViewById(R.id.orderCharges);
        totalAmount = findViewById(R.id.totalAmount);
        orderTime = findViewById(R.id.orderTime);
        orderDate = findViewById(R.id.orderDate);
        orderStatus = findViewById(R.id.orderStatus);
        vendorName = findViewById(R.id.vendorName);
        track = findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewOrderStatus.this, TrackOrder.class);
                i.putExtra("orderId", model.getOrderId());
                i.putExtra("orderStatus", model.getOrderStatus());
                startActivity(i);

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewOrderStatus.this);
                builder1.setMessage("Cancel Order?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Cancelled")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                CommonUtils.showToast("Order Canceled");
//
                                            }
                                        });
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        getOrderDataFromDB();
    }

    private void getOrderDataFromDB() {
        mDatabase.child("Orders").child(orderIdFromIntent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {
                        orderId.setText("Order Number: " + model.getOrderId());
                        orderCharges.setText("Order Total: " + model.getTotalPrice()
                                + "\nDelivery Charges: " + SharedPrefs.getCurrencySymbol() + " "
                                + String.format("%.2f", (model.getDeliveryCharges() * Float.parseFloat(SharedPrefs.getExchangeRate())))
                                + "\nShipping Charges: " + SharedPrefs.getCurrencySymbol() + " "
                                + String.format("%.2f", (model.getShippingCharges() * Float.parseFloat(SharedPrefs.getExchangeRate()))));
                        totalAmount.setText("Total amount: " + SharedPrefs.getCurrencySymbol() + " "
                                + String.format("%.2f", (model.getTotalPrice() * Float.parseFloat(SharedPrefs.getExchangeRate()))));

                        orderTime.setText("Order Time: " + CommonUtils.getFormattedTime(model.getTime()));
                        orderDate.setText("Order Date: " + CommonUtils.getFormattedDate(model.getTime()));
                        orderStatus.setText("Order Status: " + model.getOrderStatus());
                        if(model.getOrderStatus().equalsIgnoreCase("Under Process")){
                            delete.setVisibility(View.VISIBLE);
                        }else{
                            delete.setVisibility(View.GONE);
                        }
                        if (model.getOrderFor() == null || model.getOrderFor().equalsIgnoreCase("admin")) {
                            vendorName.setText("By: Fort City");
                        } else if (model.getOrderFor().equalsIgnoreCase("seller")) {
                            vendorName.setText("By: " + model.getCountModelArrayList().get(0).getProduct().getVendor().getVendorName());
                        }
                        adapter = new OrderedProductsAdapter(ViewOrderStatus.this, model.getCountModelArrayList(),
                                SharedPrefs.getCustomerType(), 0, new OrderedProductsAdapter.OnProductSelected() {
                            @Override
                            public void onChecked(ProductCountModel product, int position) {

                            }

                            @Override
                            public void onUnChecked(ProductCountModel product, int position) {

                            }
                        }, 0
                        );
                        recyclerView.setLayoutManager(new LinearLayoutManager(ViewOrderStatus.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
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


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
