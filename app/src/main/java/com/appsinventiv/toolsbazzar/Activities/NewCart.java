package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Adapters.CartAdapter;
import com.appsinventiv.toolsbazzar.Adapters.NewCartAdapter;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.NewCartModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NewCart extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    public static ArrayList<NewCartModel> newCart = new ArrayList<>();
    public ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    DatabaseReference mDatabase;
    NewCartAdapter adapter;
    TextView subtotal, totalAmount, deliveryCharges, shippingCharges, grandTotal;
    float total;
    int items;
    public static float grandTotalAmount;
    RelativeLayout checkout, wholeLayout, noItemInCart;
    Button startShopping;
    public static double shippingCharge = 0, deliveryCharge = 0;
    int position;
    //    public static LocationAndChargesModel locationAndChargesModel;
    boolean quantityChanged = false;
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        this.setTitle("My Cart");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        subtotal = findViewById(R.id.subtotal);
        totalAmount = findViewById(R.id.totalAmount);
        grandTotal = findViewById(R.id.totalPrice);
        checkout = findViewById(R.id.checkout);
        startShopping = findViewById(R.id.startShopping);
        wholeLayout = findViewById(R.id.wholeLayout);
        noItemInCart = findViewById(R.id.noItemInCart);
        shippingCharges = findViewById(R.id.shippingCharges);
        deliveryCharges = findViewById(R.id.deliveryCharges);

        startShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NewCart.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NewCart.this, Checkout.class);
                i.putExtra("grandTotal", grandTotalAmount);
                startActivity(i);
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
//        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        getUserCartProductsFromDB();
//        getLocationChargesFromDb();
        calculateTotal();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewCartAdapter(NewCart.this, newCart, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                NewCart.this.position = position;
                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId())
                        .setValue(new ProductCountModel(product, quantity, System.currentTimeMillis(), "10", ""))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                calculateTotal();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void deletedFromCart(final Product product, final int position) {
                userCartProductList.remove(position);
                if (userCartProductList.isEmpty()) {
                    wholeLayout.setVisibility(View.GONE);
                    noItemInCart.setVisibility(View.VISIBLE);

                }

                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        quantityChanged = false;
                        calculateTotal();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void quantityUpdate(Product product, final int quantity, final int position) {
                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).child("quantity")
                        .setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        quantityChanged = true;
//                        adapter.notifyItemChanged(position);
                        calculateTotal();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }

            @Override
            public void isProductLiked(Product product, boolean isLiked, int position) {

            }
        });
        recyclerView.setAdapter(adapter);


    }


    public void calculateTotal() {
        total = 0;
        items = 0;
        grandTotalAmount = 0;
        shippingCharge = 0;
        deliveryCharge = 0;
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    noItemInCart.setVisibility(View.GONE);
                    wholeLayout.setVisibility(View.VISIBLE);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel model = snapshot.getValue(ProductCountModel.class);
                        if (model != null) {
//                            if (locationAndChargesModel != null) {
                            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                                total = total + (model.getQuantity() * model.getProduct().getWholeSalePrice());

                            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                                total = total + ((model.getQuantity() * model.getProduct().getRetailPrice()));

                            }
                            if (model.getProduct().getProductWeight() != null) {
                                if (SharedPrefs.getCountry().equalsIgnoreCase("Sri Lanka")) {
                                    float pTotalWeight = Float.parseFloat(model.getProduct().getProductWeight().substring(0, model.getProduct().getProductWeight().length() - 2)) * model.getQuantity();
                                    double deliveryCh = Float.parseFloat(SharedPrefs.getOneKgRate()) + ((Float.parseFloat(SharedPrefs.getHalfKgRate()) / 0.5) * (pTotalWeight - 1));
                                    deliveryCharge = deliveryCharge + deliveryCh;
                                    deliveryCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", (deliveryCharge * Float.parseFloat(SharedPrefs.getExchangeRate()))));
                                    shippingCharges.setText(SharedPrefs.getCurrencySymbol() + " 0.00");
                                } else {
                                    float pTotalWeight = Float.parseFloat(model.getProduct().getProductWeight().substring(0, model.getProduct().getProductWeight().length() - 2)) * model.getQuantity();
                                    double shippingCh = Float.parseFloat(SharedPrefs.getOneKgRate()) + ((Float.parseFloat(SharedPrefs.getHalfKgRate()) / 0.5) * (pTotalWeight - 1));
                                    shippingCharge = shippingCharge + shippingCh;
                                    deliveryCharges.setText(SharedPrefs.getCurrencySymbol() + " 0.00");
                                    shippingCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", (shippingCharge * Float.parseFloat(SharedPrefs.getExchangeRate()))));

                                }
                            }
                            subtotal.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", total * Float.parseFloat(SharedPrefs.getExchangeRate())));

                            grandTotalAmount = total + Float.parseFloat("" + shippingCharge) + Float.parseFloat("" + deliveryCharge);
                            totalAmount.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", grandTotalAmount * Float.parseFloat(SharedPrefs.getExchangeRate())));
                            grandTotal.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", grandTotalAmount * Float.parseFloat(SharedPrefs.getExchangeRate())));

                        }
                    }
                } else {
                    noItemInCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserCartProductsFromDB() {

        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
//                    if (quantityChanged) {
////                        adapter.notifyItemChanged(position);
//                        quantityChanged = false;
////                        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
////                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
//                        adapter.notifyDataSetChanged();
//
//                    } else {
                    userCartProductList.clear();
                    newCart.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel product = snapshot.getValue(ProductCountModel.class);
                        if (product != null) {
                            userCartProductList.add(product);

                        }
                    }
                    HashMap<String, ArrayList<ProductCountModel>> map = new HashMap<>();

                    for (int i = 0; i < userCartProductList.size(); i++) {
                        if (userCartProductList.get(i).getProduct().getUploadedBy() == null || userCartProductList.get(i).getProduct().getUploadedBy().equalsIgnoreCase("admin")) {


                            if (map.containsKey("Fort City")) {
                                ArrayList<ProductCountModel> abc2 = new ArrayList<>();
                                abc2 = map.get("Fort City");
                                abc2.add(userCartProductList.get(i));
                                map.put("Fort City", abc2);
                            } else {
                                ArrayList<ProductCountModel> abc = new ArrayList<>();
                                abc.add(userCartProductList.get(i));
                                map.put("Fort City", abc);
                            }
                        } else if (userCartProductList.get(i).getProduct().getUploadedBy().equalsIgnoreCase("seller")) {
                            if (map.containsKey(userCartProductList.get(i).getProduct().getVendor().getVendorName())) {
                                ArrayList<ProductCountModel> abc2 = new ArrayList<>();
                                abc2 = map.get(userCartProductList.get(i).getProduct().getVendor().getVendorName());
                                abc2.add(userCartProductList.get(i));
                                map.put(userCartProductList.get(i).getProduct().getVendor().getVendorName(), abc2);

                            } else {
                                ArrayList<ProductCountModel> abc = new ArrayList<>();
                                abc.add(userCartProductList.get(i));
                                map.put(userCartProductList.get(i).getProduct().getVendor().getVendorName(), abc);
                            }
                        }


//                        if (map.containsKey(userCartProductList.get(i).getProduct().getVendor().getVendorName())) {
//                            ArrayList<ProductCountModel> abc2 = new ArrayList<>();
//                            abc2 = map.get(userCartProductList.get(i).getProduct().getVendor().getVendorName());
//                            abc2.add(userCartProductList.get(i));
//                            map.put(userCartProductList.get(i).getProduct().getVendor().getVendorName(), abc2);
//
//                        } else {
//                            ArrayList<ProductCountModel> abc = new ArrayList<>();
//                            abc.add(userCartProductList.get(i));
//                            map.put(userCartProductList.get(i).getProduct().getVendor().getVendorName(), abc);
//                        }
                    }


                    for (Map.Entry<String, ArrayList<ProductCountModel>> entry : map.entrySet()) {
                        String key = entry.getKey();

                        newCart.add(new NewCartModel(key,
                                (SharedPrefs.getCountry().equalsIgnoreCase("Sri Lanka") ? 0 : calculateShippingCharges(entry.getValue())),
                                (SharedPrefs.getCountry().equalsIgnoreCase("Sri Lanka") ? calculateDeliveryCharges(entry.getValue()) : 0),
                                calculateSellerTotal(entry.getValue()),
                                entry.getValue()));

                    }
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                    adapter.notifyDataSetChanged();
                }
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private double calculateSellerTotal(ArrayList<ProductCountModel> value) {
        double total = 0;
        for (ProductCountModel model : value) {
            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                total = total + (model.getQuantity() * model.getProduct().getWholeSalePrice());

            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                total = total + ((model.getQuantity() * model.getProduct().getRetailPrice()));

            }
        }
//        if (SharedPrefs.getCountry().equalsIgnoreCase("Sri Lanka")) {
        double ch = (SharedPrefs.getCountry().equalsIgnoreCase("Sri Lanka") ? calculateDeliveryCharges(value) : calculateShippingCharges(value));
        return (total + ch)
                ;


    }

    private double calculateShippingCharges(ArrayList<ProductCountModel> value) {
        double shippingCh = 0;
        for (ProductCountModel model : value) {
            if (model.getProduct().getProductWeight() != null) {
                if (SharedPrefs.getCountry().equalsIgnoreCase("Sri Lanka")) {
//                    float pTotalWeight = Float.parseFloat(model.getProduct().getProductWeight().substring(0, model.getProduct().getProductWeight().length() - 2)) * model.getQuantity();
//                    shippingCh = Float.parseFloat(SharedPrefs.getOneKgRate()) + ((Float.parseFloat(SharedPrefs.getHalfKgRate()) / 0.5) * (pTotalWeight - 1));
//                    deliveryCharge = deliveryCharge + deliveryCh;
                    shippingCh = 0;
//                    deliveryCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", (deliveryCharge * Float.parseFloat(SharedPrefs.getExchangeRate()))));
//                    shippingCharges.setText(SharedPrefs.getCurrencySymbol() + " 0.00");
                } else {
                    float pTotalWeight = Float.parseFloat(model.getProduct().getProductWeight().substring(0, model.getProduct().getProductWeight().length() - 2)) * model.getQuantity();
                    shippingCh = Float.parseFloat(SharedPrefs.getOneKgRate()) + ((Float.parseFloat(SharedPrefs.getHalfKgRate()) / 0.5) * (pTotalWeight - 1));
//                    shippingCharge = shippingCharge + shippingCh;
//                    deliveryCharges.setText(SharedPrefs.getCurrencySymbol() + " 0.00");
//                    shippingCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", (shippingCharge * Float.parseFloat(SharedPrefs.getExchangeRate()))));

                }
            }
        }
        return shippingCh;
    }

    private double calculateDeliveryCharges(ArrayList<ProductCountModel> value) {

        double deliveryCh = 0;
        for (ProductCountModel model : value) {
            if (model.getProduct().getProductWeight() != null) {
                if (SharedPrefs.getCountry().equalsIgnoreCase("Sri Lanka")) {
//                    float pTotalWeight = Float.parseFloat(model.getProduct().getProductWeight().substring(0, model.getProduct().getProductWeight().length() - 2)) * model.getQuantity();
//                    deliveryCh = Float.parseFloat(SharedPrefs.getOneKgRate()) + ((Float.parseFloat(SharedPrefs.getHalfKgRate()) / 0.5) * (pTotalWeight - 1));
//                    deliveryCharge = deliveryCharge + deliveryCh;
                    deliveryCh = 0;
//                    deliveryCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", (deliveryCharge * Float.parseFloat(SharedPrefs.getExchangeRate()))));
//                    shippingCharges.setText(SharedPrefs.getCurrencySymbol() + " 0.00");
                } else {
                    float pTotalWeight = Float.parseFloat(model.getProduct().getProductWeight().substring(0, model.getProduct().getProductWeight().length() - 2)) * model.getQuantity();
                    deliveryCh = Float.parseFloat(SharedPrefs.getOneKgRate()) + ((Float.parseFloat(SharedPrefs.getHalfKgRate()) / 0.5) * (pTotalWeight - 1));
//                    shippingCharge = shippingCharge + shippingCh;
//                    deliveryCharges.setText(SharedPrefs.getCurrencySymbol() + " 0.00");
//                    shippingCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", (shippingCharge * Float.parseFloat(SharedPrefs.getExchangeRate()))));

                }
            }
        }
        return deliveryCh;
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
