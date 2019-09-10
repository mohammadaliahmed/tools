package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.InvoiceModel;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;

import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewUnderProcessOrder extends AppCompatActivity {
    Button markAsCourier, markAsShipped, markAsOutOfStock;
    Spinner chooseDeliveryBoy;
    TextView carrier;
    EditText trackingNumber;
    ArrayList<ShippingCompanyModel> shippingList = new ArrayList<>();


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
    FloatingActionButton invoice, purchase;
    long invoiceNumber = 10001;
    Customer customer;
    OrderModel model;
    CheckBox selectAll;

    ArrayList<ProductCountModel> newList = new ArrayList<>();
    long totalPrice;
    private Float deliveryCharges = 0.0f;
    private Float shippingCharges = 0.0f;
    long grandTotal = 0;
    CardView shipping_card, order_card, shipping_info_card, delivered_card;
    Button markAsCOD, markAsDeliveredCredit, markAsRefused;
    EditText dueDate;

    private ArrayList<BottomDialogModel> shippingCompanies = new ArrayList<>();
    private ShippingCompanyModel shippingCarrier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_under_process_order);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        orderIdFromIntent = intent.getStringExtra("orderId");

        this.setTitle("Order # " + orderIdFromIntent);

        carrier = findViewById(R.id.carrier);
        selectAll = findViewById(R.id.selectAll);
        trackingNumber = findViewById(R.id.trackingNumber);
        orderId = findViewById(R.id.order_id);
        markAsCourier = findViewById(R.id.markAsCourier);
        markAsShipped = findViewById(R.id.markAsShipped);
        markAsOutOfStock = findViewById(R.id.markAsOutOfStock);
        chooseDeliveryBoy = findViewById(R.id.chooseDeliveryBoy);
        orderTime = findViewById(R.id.order_time);
        quantity = findViewById(R.id.order_quantity);
        ship_country = findViewById(R.id.ship_country);
        price = findViewById(R.id.order_price);
        instructions = findViewById(R.id.instructions);
        invoice = findViewById(R.id.invoice);
        purchase = findViewById(R.id.purchase);

        username = findViewById(R.id.ship_username);
        phone = findViewById(R.id.ship_phone);
        address = findViewById(R.id.ship_address);
        city = findViewById(R.id.ship_city);
        delivered_card = findViewById(R.id.delivered_card);
        shipping_card = findViewById(R.id.shipping_card);
        order_card = findViewById(R.id.order_card);
        shipping_info_card = findViewById(R.id.shipping_info_card);

        carrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShippingBottomDialog(shippingCompanies);
            }
        });


        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    newList.addAll(model.getCountModelArrayList());
                    adapter.selectAll(1);
                } else {
                    adapter.selectAll(0);
                    newList.clear();
                }
            }
        });

        recyclerView = findViewById(R.id.recylerview);
        recyclerView.setNestedScrollingEnabled(false);

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
                            recyclerView.setMinimumHeight(model.getCountModelArrayList().size() * 550);
                        }

                        city.setText(model.getCustomer().getCity());
                        list = model.getCountModelArrayList();
                        customer = model.getCustomer();
                        adapter = new OrderedProductsAdapter(ViewUnderProcessOrder.this, list,
                                model.getCustomer().getCustomerType(),
                                1,
                                new OrderedProductsAdapter.OnProductSelected() {
                                    @Override
                                    public void onChecked(ProductCountModel product, int position) {
                                        newList.add(product);


                                    }

                                    @Override
                                    public void onUnChecked(ProductCountModel product, int position) {
                                        try {
                                            newList.remove(position);
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, 0);
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
        markAsShipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markOrderAsShipped();
            }
        });

        markAsOutOfStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markOrderAsOutOfStock();
            }
        });
        markAsCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (carrier.getText().length() == 0) {
                    carrier.setError("Please enter carrier");
                } else if (trackingNumber.getText().length() == 0) {
                    trackingNumber.setError("Please enter tracking number");
                } else {
                    markOrderAsCourier();
                }
            }
        });
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newList.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewUnderProcessOrder.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Do you want to add this order to purchases " + "?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

//                            addPurchase();

                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();


                } else {
                    CommonUtils.showToast("Nothing selected");
                }
            }
        });
        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newList.size() > 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewUnderProcessOrder.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Do you want to add this order to invoices " + "?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addToInvoice();

                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    CommonUtils.showToast("Nothing selected");
                }

            }
        });


        getDeliveryBoysFromDb();
        getInvoiceCountFromDb();
        getShippingCompaniesFromDb();

    }

    @SuppressLint("WrongConstant")
    private void showShippingBottomDialog(ArrayList<BottomDialogModel> list) {


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.bottom_option, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dialog.setContentView(customView);
        RecyclerView recyclerview = customView.findViewById(R.id.recyclerview);


        BottomAdapter adapter = new BottomAdapter(this, list, new BottomAdapter.ShareMessageFriendsAdapterCallbacks() {
            @Override
            public void onChoose(int position) {
                shippingCarrier = shippingList.get(position);
                dialog.dismiss();
                carrier.setText(shippingCarrier.getName());
            }
        });

//        dialog.dismiss();


        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);

        dialog.show();


    }

    private void getShippingCompaniesFromDb() {

        mDatabase.child("Settings").child("ShippingCompanies").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    shippingList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ShippingCompanyModel model = snapshot.getValue(ShippingCompanyModel.class);
                        if (model != null) {
                            shippingList.add(model);
                        }
                    }
                    setUpShippingCompanySpinner();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpShippingCompanySpinner() {
        shippingCompanies = new ArrayList<>();
        for (int i = 0; i < shippingList.size(); i++) {
            shippingCompanies.add(new BottomDialogModel(shippingList.get(i).getId(), shippingList.get(i).getName()
                    , shippingList.get(i).getTelephone(), shippingList.get(i).getPicUrl()));

        }

    }

    private void addToInvoice() {
        mDatabase.child("SellerInvoice").child(SharedPrefs.getVendor().getUsername()).child("" + invoiceNumber)
                .setValue(new InvoiceModel(

                        invoiceNumber,
                        list,
                        newList,
                        customer,
                        model.getTotalPrice(),
                        System.currentTimeMillis(),
                        orderIdFromIntent,
                        model.getDeliveryCharges(),
                        model.getShippingCharges(),
                        model.getTotalPrice(),
                        model.getOrderStatus(),
                        list.size(),
                        "sdfsdf",
                        0,
                        "waiting"

                ))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        updateInvoiceStatus();
                        CommonUtils.showToast("Added to pending invoices");
                        updateInvoiceCount();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void updateInvoiceCount() {
        mDatabase.child("SellerInvoice").child(SharedPrefs.getVendor().getUsername()).child("InvoiceCount").setValue(invoiceNumber);
    }

    private void getInvoiceCountFromDb() {
        mDatabase.child("SellerInvoice").child(SharedPrefs.getVendor().getUsername()).child("InvoiceCount").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    CommonUtils.showToast("Done\nGo to purchases");
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


    private void markOrderAsShipped() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUnderProcessOrder.this);
//        builder.setMessage("Mark order " + orderIdFromIntent + " as shipped");
//
//        // add the buttons
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Shipped");
//                mDatabase.child("Orders").child(orderIdFromIntent).child("deliveryBy").setValue(employee.getName()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        CommonUtils.showToast("Order Marked as shipped");
//                        finish();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
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
//                    setUpDeliveryBoySpinner();
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

    private void setUpDeliveryBoySpinner() {
//        ArrayList<String> items = new ArrayList<>();
//        for (int i = 0; i < employeeArrayList.size(); i++) {
//            items.add("" + employeeArrayList.get(i).getName());
//
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        chooseDeliveryBoy.setAdapter(adapter);
//
//        chooseDeliveryBoy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
//
//                employee = employeeArrayList.get(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });
    }

    private void markOrderAsOutOfStock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUnderProcessOrder.this);
        builder.setMessage("Mark order " + orderIdFromIntent + " as out of stock?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Out Of Stock").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Order marked as out of stock");

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

    private void markOrderAsCourier() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUnderProcessOrder.this);
        builder.setMessage("Mark order " + orderIdFromIntent + " as courier");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue("Shipped By Courier").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        insertOtherValues();
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

    private void insertOtherValues() {
        mDatabase.child("Orders").child(orderIdFromIntent).child("carrier").setValue(carrier.getText().toString());
        mDatabase.child("Orders").child(orderIdFromIntent).child("trackingNumber").setValue(trackingNumber.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Order marked as courier");
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
