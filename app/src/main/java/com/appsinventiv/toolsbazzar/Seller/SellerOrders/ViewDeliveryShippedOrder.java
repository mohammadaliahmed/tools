package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewDeliveryShippedOrder extends AppCompatActivity {
    Button markAsDeliveredCredit, markAsDeliveredCOD, markAsRefused;
//    Spinner chooseDeliveryBoy;
    EditText receiverName, recieverNameCredit, creditDueDate;

//    ArrayList<Employee> employeeArrayList = new ArrayList<>();
//    Employee employee;
    TextView orderId, orderTime, quantity, price, username, phone, address, city, ship_country, instructions;
    String orderIdFromIntent;
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    OrderedProductsAdapter adapter;
    ArrayList<ProductCountModel> list = new ArrayList<>();
    Button button, button_refused;

    String s_orderId, s_quantity, s_price, s_username;
    String userFcmKey;
    long invoiceNumber = 10001;
    Customer customer;
    OrderModel model;
//    RelativeLayout wholeLayout;

    ArrayList<ProductCountModel> newList = new ArrayList<>();
    long totalPrice;
    private Float deliveryCharges = 0.0f;
    private Float shippingCharges = 0.0f;
    long grandTotal = 0;
    CardView shipping_card, order_card, shipping_info_card, delivered_card;
    EditText dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_delivery_shipped_order);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        orderIdFromIntent = intent.getStringExtra("orderId");

        this.setTitle("Order # " + orderIdFromIntent);


        markAsDeliveredCredit = findViewById(R.id.markAsDeliveredCredit);
        receiverName = findViewById(R.id.receiverName);
        recieverNameCredit = findViewById(R.id.recieverNameCredit);
        creditDueDate = findViewById(R.id.creditDueDate);
        orderId = findViewById(R.id.order_id);
        markAsDeliveredCredit = findViewById(R.id.markAsDeliveredCredit);
        markAsDeliveredCOD = findViewById(R.id.markAsDeliveredCOD);
        markAsRefused = findViewById(R.id.markAsRefused);
//        chooseDeliveryBoy = findViewById(R.id.chooseDeliveryBoy);
        orderTime = findViewById(R.id.order_time);
        quantity = findViewById(R.id.order_quantity);
        ship_country = findViewById(R.id.ship_country);
        price = findViewById(R.id.order_price);
        instructions = findViewById(R.id.instructions);
//        wholeLayout = findViewById(R.id.wholeLayout);

        username = findViewById(R.id.ship_username);
        phone = findViewById(R.id.ship_phone);
        address = findViewById(R.id.ship_address);
        city = findViewById(R.id.ship_city);
        delivered_card = findViewById(R.id.delivered_card);
        shipping_card = findViewById(R.id.shipping_card);
        order_card = findViewById(R.id.order_card);
        shipping_info_card = findViewById(R.id.shipping_info_card);


        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mDatabase.child("Orders").child(orderIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {
                        orderId.setText("" + model.getOrderId());
                        orderTime.setText("" + CommonUtils.getFormattedDate(model.getTime()));
                        quantity.setText("" + model.getCountModelArrayList().size());
                        price.setText("Rs " + model.getTotalPrice());
                        username.setText("" + model.getCustomer().getName());
                        phone.setText("" + model.getCustomer().getPhone());
                        instructions.setText("Instructions: " + model.getInstructions());
                        address.setText(model.getCustomer().getAddress());
                        ship_country.setText(model.getCustomer().getCountry());
                        if (model.getCountModelArrayList().size() > 1) {
                            recyclerView.setMinimumHeight(model.getCountModelArrayList().size() * 520);
                        }

                        city.setText(model.getCustomer().getCity());
                        list = model.getCountModelArrayList();
                        customer = model.getCustomer();
                        adapter = new OrderedProductsAdapter(ViewDeliveryShippedOrder.this, list, model.getCustomer().getCustomerType(), 0, new OrderedProductsAdapter.OnProductSelected() {
                            @Override
                            public void onChecked(ProductCountModel product, int position) {
                                if (!newList.contains(product)) {
                                    newList.add(product);
                                }
                            }

                            @Override
                            public void onUnChecked(ProductCountModel product, int position) {
                                if (newList.contains(product)) {
                                    newList.remove(newList.indexOf(newList.get(position)));
                                }

                            }
                        },0);
                        recyclerView.setAdapter(adapter);


                        s_orderId = model.getOrderId();
                        s_quantity = "" + model.getCountModelArrayList().size();
                        s_price = "" + model.getTotalPrice();
                        s_username = model.getCustomer().getUsername();

                        userFcmKey = model.getCustomer().getFcmKey();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        markAsDeliveredCOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (receiverName.getText().length() == 0) {
                    receiverName.setError("Enter name");
                } else {
                    markAsDeliveredCOD();
                }
            }
        });

        markAsRefused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markOrderAsRefused();
            }
        });
        markAsDeliveredCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recieverNameCredit.getText().length() == 0) {
                    recieverNameCredit.setError("Please enter receiver name");
                } else if (creditDueDate.getText().length() == 0) {
                    creditDueDate.setError("Please enter date ");
                } else {
                    markOrderAsDeliveredCredit();
                }
            }
        });


        getDeliveryBoysFromDb();

    }


    private void addPurchase() {
        final ArrayList<String> keys = new ArrayList<>();

        mDatabase.child("Purchases").child("PendingPurchases").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        keys.add(snapshot.getKey());
                    }
                    for (final ProductCountModel model : newList) {
                        if (!keys.contains(model.getProduct().getId())) {
                            mDatabase.child("Purchases").child("PendingPurchases").child(model.getProduct().getId()).setValue(model);
                            mDatabase.child("Purchases").child("PendingPurchases").child(model.getProduct().getId()).child("orderId").child(orderIdFromIntent).setValue(orderIdFromIntent);


                        } else {
                            setQuantity(model);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setQuantity(final ProductCountModel model) {
        final int[] quantity = new int[1];
        mDatabase.child("Purchases").child("PendingPurchases").child(model.getProduct().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    ProductCountModel model1 = dataSnapshot.getValue(ProductCountModel.class);
                    if (model1 != null) {
                        quantity[0] = model.getQuantity() + model1.getQuantity();
                        mDatabase.child("Purchases").child("PendingPurchases").child(model.getProduct().getId()).child("quantity").setValue(quantity[0]);
                        mDatabase.child("Purchases").child("PendingPurchases").child(model.getProduct().getId()).child("orderId").child(orderIdFromIntent).setValue(orderIdFromIntent);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private long getTotalPrice() {
        totalPrice = 0;
        for (ProductCountModel abc : newList) {
            if (model.getCustomer().getCustomerType().equalsIgnoreCase("wholesale")) {
                totalPrice += abc.getQuantity() * abc.getProduct().getWholeSalePrice();
            } else if (model.getCustomer().getCustomerType().equalsIgnoreCase("retail")) {
                totalPrice += abc.getQuantity() * abc.getProduct().getRetailPrice();
            }
        }
        return totalPrice;
    }

    private void updateInvoiceStatus() {
        mDatabase.child("Orders").child(orderIdFromIntent).child("isInvoiced").setValue(true);
        mDatabase.child("Orders").child(orderIdFromIntent).child("invoiceNumber").setValue(invoiceNumber);


    }


    private void markAsDeliveredCOD() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDeliveryShippedOrder.this);
        builder.setMessage("Mark order " + orderIdFromIntent + " as delivered cod");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Delivered");
                mDatabase.child("Orders").child(orderIdFromIntent).child("receiverName").setValue(receiverName.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Order Marked as delivered");
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void getDeliveryBoysFromDb() {
//        mDatabase.child("Admin").child("Employees").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    employeeArrayList.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Employee employee = snapshot.getValue(Employee.class);
//                        if (employee != null) {
//                            if (employee.getRole() == 6)
//                                employeeArrayList.add(employee);
//                        }
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }


    private void markOrderAsRefused() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDeliveryShippedOrder.this);
        builder.setMessage("Mark order " + orderIdFromIntent + " as refused?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Refused").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Order marked as refused");
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void markOrderAsDeliveredCredit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDeliveryShippedOrder.this);
        builder.setMessage("Mark order " + orderIdFromIntent + " as courier");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Orders").child(orderIdFromIntent).child("receiverNameCredit").setValue(recieverNameCredit.getText().toString());
                mDatabase.child("Orders").child(orderIdFromIntent).child("creditDueDate").setValue(creditDueDate.getText().toString());
                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Credit").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Order marked as delivered credit");
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

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
