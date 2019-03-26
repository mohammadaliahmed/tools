package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Interface.NotificationObserver;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;

import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.NotificationAsync;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewOrder extends AppCompatActivity implements NotificationObserver {
    TextView orderId, orderTime, quantity, price, username, phone, address,
            city, ship_country, instructions, deliveredBy, receivedBy;
    String orderIdFromIntent;
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    OrderedProductsAdapter adapter;
    ArrayList<ProductCountModel> list = new ArrayList<>();
    Button button, button_refused;
    TextView creditDue, amount;
    String s_orderId, s_quantity, s_price, s_username;
    String userFcmKey;
    FloatingActionButton invoice;
    int invoiceNumber = 10001;
    Customer customer;
    OrderModel model;
    RelativeLayout wholeLayout, creditDueLayout;

    ArrayList<ProductCountModel> newList = new ArrayList<>();
    long totalPrice;
    private Float deliveryCharges = 0.0f;
    private Float shippingCharges = 0.0f;
    long grandTotal = 0;
    CardView shipping_card, order_card, shipping_info_card, delivered_card;
    EditText dueDate, receiverName;

    Button transferToAccounts, transferToAccounts1, markAsDeliveredCourier, markAsRefusedCourier;
    CardView courier_card, courier_card_delivered;
    TextView courierName, trackingNumber, courierReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        orderIdFromIntent = intent.getStringExtra("orderId");
        this.setTitle("Order # " + orderIdFromIntent);

        courierName = findViewById(R.id.courierName);
        trackingNumber = findViewById(R.id.trackingNumber);
        courierReceiverName = findViewById(R.id.courierReceiverName);
        courier_card_delivered = findViewById(R.id.courier_card_delivered);
        courier_card = findViewById(R.id.courier_card);
        markAsDeliveredCourier = findViewById(R.id.markAsDeliveredCourier);
        markAsRefusedCourier = findViewById(R.id.markAsRefusedCourier);
        receiverName = findViewById(R.id.receiverName);
        orderId = findViewById(R.id.order_id);
        amount = findViewById(R.id.amount);
        creditDue = findViewById(R.id.creditDue);
        orderTime = findViewById(R.id.order_time);
        quantity = findViewById(R.id.order_quantity);
        price = findViewById(R.id.order_price);
        instructions = findViewById(R.id.instructions);
        wholeLayout = findViewById(R.id.wholeLayout);
        button_refused = findViewById(R.id.button_refused);
        creditDueLayout = findViewById(R.id.creditDueLayout);
        transferToAccounts1 = findViewById(R.id.transferToAccounts1);


        username = findViewById(R.id.ship_username);
        phone = findViewById(R.id.ship_phone);
        address = findViewById(R.id.ship_address);
        city = findViewById(R.id.ship_city);
        ship_country = findViewById(R.id.ship_country);
        delivered_card = findViewById(R.id.delivered_card);
        shipping_card = findViewById(R.id.shipping_card);
        order_card = findViewById(R.id.order_card);
        shipping_info_card = findViewById(R.id.shipping_info_card);
        transferToAccounts = findViewById(R.id.transferToAccounts);
        receivedBy = findViewById(R.id.receivedBy);
        deliveredBy = findViewById(R.id.deliveredBy);


        transferToAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToInvoiceAccounts();

            }
        });
        transferToAccounts1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToInvoiceAccounts();
            }
        });


        button_refused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markasRefusedByCourier();
            }
        });


        markAsDeliveredCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (receiverName.getText().length() == 0) {
                    receiverName.setError("Enter name");
                } else {
                    markOrderAsDeliverdByCourier();
                }
            }
        });
        markAsRefusedCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markasRefusedByCourier();
            }
        });

//        markAsRefused.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                markAsRefusedAdmin();
//
//            }
//        });
//
//
//        markAsCOD.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Delivered COD");
//                mDatabase.child("Orders").child(orderIdFromIntent).child("creditDueDate").setValue(dueDate.getText().toString());
//
//            }
//        });
//
//        markAsDeliveredCredit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Credit");
//                mDatabase.child("Orders").child(orderIdFromIntent).child("creditDueDate").setValue(dueDate.getText().toString());
//
//            }
//        });


        button = findViewById(R.id.button);
        invoice = findViewById(R.id.invoice);

        getInvoicesCountFromDb();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getOrderStatus().equalsIgnoreCase("pending") ||
                        model.getOrderStatus().equalsIgnoreCase("under process")) {
                    markOrderAsShipped();
                } else if (model.getOrderStatus().equalsIgnoreCase("shipped")) {
                    markOrderAsComplete();
                } else if (model.getOrderStatus().equalsIgnoreCase("shipped by courier")) {
                    markOrderAsDeliverdByCourier();
                } else {

                }
            }
        });


        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPurchase();
//
//                if (model.isInvoiced()) {
//                    Intent i = new Intent(ViewOrder.this, ViewInvoice.class);
//                    i.putExtra("invoiceNumber", model.getInvoiceNumber());
//                    startActivity(i);
//                    finish();
//                } else {
//
//                    wholeLayout.setVisibility(View.VISIBLE);
//                    mDatabase.child("Invoices").child("" + invoiceNumber)
//                            .setValue(new InvoiceModel(
//
//                                    invoiceNumber,
//                                    list,
//                                    list,
//                                    customer,
//                                    getTotalPrice(),
//                                    System.currentTimeMillis(),
//                                    orderIdFromIntent,
//                                    deliveryCharges,
//                                    shippingCharges,
//                                    (grandTotal + deliveryCharges + shippingCharges + totalPrice)
//
//                            ))
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    updateInvoiceStatus();
//
//
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
//                }
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        mDatabase.child("Orders").child(orderIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {

                        setUpLayout(model.getOrderStatus());

                        if (model.getCountModelArrayList().size() > 1) {
                            recyclerView.setMinimumHeight(model.getCountModelArrayList().size() * 550);
                        }

                        orderId.setText("" + model.getOrderId());
                        orderTime.setText("" + CommonUtils.getFormattedDate(model.getTime()));
                        quantity.setText("" + model.getCountModelArrayList().size());
                        price.setText("Rs " + model.getTotalPrice());
                        deliveryCharges = model.getDeliveryCharges();
                        shippingCharges = model.getShippingCharges();
//                        totalPrice=model.getTotalPrice();

                        username.setText("" + model.getCustomer().getName());
                        phone.setText("" + model.getCustomer().getPhone());
                        instructions.setText("Instructions: " + model.getInstructions());
                        address.setText(model.getCustomer().getAddress());
                        city.setText(model.getCustomer().getCity());
                        ship_country.setText(model.getCustomer().getCountry());
                        list = model.getCountModelArrayList();

//                        if (model.getDeliveryBy() != null) {
//                            deliveredBy.setText(model.getDeliveryBy());
//                        }
//                        if (model.getReceiverName() != null) {
//                            receivedBy.setText(model.getReceiverName());
//                        }
//
//                        if (model.getReceiverNameCredit() != null) {
//                            receivedBy.setText(model.getReceiverNameCredit());
//                        }
//                        if (model.getCreditDueDate() != null) {
//                            creditDue.setText("Due Date: " + model.getCreditDueDate());
//                            amount.setText("Amount: Rs " + CommonUtils.getFormattedPrice(model.getTotalPrice()));
//                        }
//
//                        if (model.getCarrier() != null) {
//                            courierReceiverName.setText(model.getReceiverName());
//                            courierName.setText(model.getCarrier());
//                            trackingNumber.setText(model.getTrackingNumber() + "");
//                        }

                        customer = model.getCustomer();
                        adapter = new OrderedProductsAdapter(ViewOrder.this, list, model.getCustomer().getCustomerType(), 0, new OrderedProductsAdapter.OnProductSelected() {
                            @Override
                            public void onChecked(ProductCountModel product, int position) {
                                if (!newList.contains(product)) {
                                    newList.add(product);
                                }
                            }

                            @Override
                            public void onUnChecked(ProductCountModel product, int position) {
                                if (newList.contains(product)) {
                                    newList.remove(product);
                                }

                            }
                        },0);
                        recyclerView.setAdapter(adapter);

                        if (model.getOrderStatus().equalsIgnoreCase("under process")) {
                            button.setText("Mark as shipped");
                            courier_card.setVisibility(View.GONE);


                        } else if (model.getOrderStatus().equalsIgnoreCase("Shipped")) {
                            button.setText("Mark as delivered");
                            courier_card.setVisibility(View.GONE);


                        } else if (model.getOrderStatus().equalsIgnoreCase("Pending")) {
                            invoice.setVisibility(View.GONE);
                            button.setVisibility(View.GONE);
                            courier_card_delivered.setVisibility(View.GONE);
                            courier_card.setVisibility(View.GONE);


                        } else if (model.getOrderStatus().equalsIgnoreCase("Shipped by courier")) {
                            courier_card.setVisibility(View.VISIBLE);
                            button.setVisibility(View.GONE);
                            courier_card_delivered.setVisibility(View.GONE);


//                            button.setText("Mark as delivered");
//                            button_refused.setVisibility(View.VISIBLE);
                        } else if (model.getOrderStatus().equalsIgnoreCase("delivered by courier")) {
                            courier_card_delivered.setVisibility(View.VISIBLE);
                            button.setVisibility(View.GONE);

                        } else {
                            button.setVisibility(View.GONE);
                            courier_card.setVisibility(View.GONE);
                            courier_card_delivered.setVisibility(View.GONE);

                        }


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

    }

    private void addToInvoiceAccounts() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrder.this);
//        builder.setTitle("Alert");
//        builder.setMessage("Move to accounts?");
//
//        // add the buttons
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                mDatabase.child("Accounts/PendingInvoices").child("" + invoiceNumber)
//                        .setValue(new InvoiceModel(
//
//                                invoiceNumber,
//                                list,
//                                list,
//                                customer,
////                                getTotalPrice(),
//                                totalPrice,
//                                System.currentTimeMillis(),
//                                orderIdFromIntent,
//                                deliveryCharges,
//                                shippingCharges,
//                                totalPrice,
//                                model.getOrderStatus(),
//                                list.size(),
//                                model.getDeliveryBy(),
//                                0,
//                                "pendingSO"
//
//
//                        ))
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                updateInvoicesCount();
//                                updateOrderStatus();
//                                CommonUtils.showToast("Moved to accounts");
//                                Intent i=new Intent(ViewOrder.this,TransferToAccountsDone.class);
//                                i.putExtra("orderId",orderIdFromIntent);
//                                startActivity(i);
//
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//
//            }
//        });
//        builder.setNegativeButton("Cancel", null);
//
//        // create and show the alert dialog
//        AlertDialog dialog = builder.create();
//        dialog.show();

    }

    private void updateOrderStatus() {
        mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Completed");
    }

    private void markAsRefusedAdmin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrder.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to mark this order as refused?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Orders").child(orderIdFromIntent).child("Refused").setValue("Refused")

                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Marked as refused");
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

    private void markasRefusedByCourier() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrder.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to mark this order as refused?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Refused By Courier")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Marked as delivered");
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

    private void markOrderAsDeliverdByCourier() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrder.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to mark this order as delivered?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Orders").child(orderIdFromIntent).child("receiverName").setValue(receiverName.getText().toString());
                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Delivered By Courier")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Marked as delivered");
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

    private void setUpLayout(String orderStatus) {
        if (model.getOrderStatus().equalsIgnoreCase("pending")
                || model.getOrderStatus().equalsIgnoreCase("under process")
                ) {
            invoice.setVisibility(View.VISIBLE);

        } else {
            invoice.setVisibility(View.GONE);
        }


        if (model.getOrderStatus().equalsIgnoreCase("delivered")) {

            delivered_card.setVisibility(View.VISIBLE);
        }
        if (model.getOrderStatus().equalsIgnoreCase("credit")) {
            creditDueLayout.setVisibility(View.VISIBLE);
            delivered_card.setVisibility(View.VISIBLE);
        }

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
//
//    private long getTotalPrice() {
//        totalPrice = 0;
//        for (ProductCountModel abc : newList) {
//            if (model.getCustomer().getCustomerType().equalsIgnoreCase("wholesale")) {
//                totalPrice += abc.getQuantity() * abc.getProduct().getWholeSalePrice();
//            } else if (model.getCustomer().getCustomerType().equalsIgnoreCase("retail")) {
//                totalPrice += abc.getQuantity() * abc.getProduct().getRetailPrice();
//            }
//        }
//        return totalPrice;
//    }


    private void updateInvoicesCount() {
        mDatabase.child("Accounts").child("InvoiceCount").setValue(invoiceNumber);
    }

    private void getInvoicesCountFromDb() {
        mDatabase.child("Accounts").child("InvoiceCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    invoiceNumber = dataSnapshot.getValue(Integer.class) + 1;
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void markOrderAsComplete() {
        showAlertDialogButtonClicked("Delivered");
    }

    private void markOrderAsShipped() {
        showAlertDialogButtonClicked("Shipped");
    }

    public void showAlertDialogButtonClicked(final String message) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrder.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to mark this order as " + message + "?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Order marked as " + message);
                                NotificationAsync notificationAsync = new NotificationAsync(ViewOrder.this);
                                String notification_title = "Your order has been " + message;
                                String notification_message = "Click to view";
                                notificationAsync.execute("ali", userFcmKey, notification_title, notification_message, "Order", "abc");

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
    public void onSuccess(String chatId) {
        CommonUtils.showToast("Notification sent to user");
    }

    @Override
    public void onFailure() {

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
