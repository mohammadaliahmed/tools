package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Adapters.CartAdapter;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.LocationAndChargesModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
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

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    public static ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    DatabaseReference mDatabase;
    CartAdapter adapter;
    TextView subtotal, totalAmount, deliveryCharges, shippingCharges, grandTotal;
    float total;
    int items;
    public static float grandTotalAmount;
    RelativeLayout checkout, wholeLayout, noItemInCart;
    Button startShopping;
    public static LocationAndChargesModel locationAndChargesModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        this.setTitle("My Cart");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                Intent i = new Intent(Cart.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Cart.this, Checkout.class);
                i.putExtra("grandTotal", grandTotalAmount);
                startActivity(i);
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);

        getUserCartProductsFromDB();
        getLocationChargesFromDb();
        calculateTotal();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CartAdapter(Cart.this, userCartProductList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).setValue(new ProductCountModel(product, quantity, System.currentTimeMillis(), "10", ""))
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
                        calculateTotal();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void quantityUpdate(Product product, final int quantity, int position) {
                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).child("quantity").setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            public void isProductLiked(Product product, boolean isLiked, int position) {

            }
        });
        recyclerView.setAdapter(adapter);


    }

    private void getLocationChargesFromDb() {
        mDatabase.child("Settings").child("DeliveryCharges").child(SharedPrefs.getLocationId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    locationAndChargesModel = dataSnapshot.getValue(LocationAndChargesModel.class);
                    if (locationAndChargesModel != null) {
                        calculateTotal();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void calculateTotal() {
        total = 0;
        items = 0;
        grandTotalAmount = 0;
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    noItemInCart.setVisibility(View.GONE);
                    wholeLayout.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel model = snapshot.getValue(ProductCountModel.class);
                        if (model != null) {
                            if (locationAndChargesModel != null) {
                                if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                                    total = total + (model.getQuantity() * model.getProduct().getWholeSalePrice() );

                                } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                                    total = total + ((model.getQuantity() * model.getProduct().getRetailPrice()) );

                                }

                                subtotal.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", total * Float.parseFloat(SharedPrefs.getExchangeRate())));

                                deliveryCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", locationAndChargesModel.getDeliveryCharges() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                                shippingCharges.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", locationAndChargesModel.getShippingCharges() * Float.parseFloat(SharedPrefs.getExchangeRate())));


                                grandTotalAmount = total + locationAndChargesModel.getDeliveryCharges()+locationAndChargesModel.getShippingCharges();
                                totalAmount.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", grandTotalAmount * Float.parseFloat(SharedPrefs.getExchangeRate())));
                                grandTotal.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", grandTotalAmount * Float.parseFloat(SharedPrefs.getExchangeRate())));

                            }
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
                    userCartProductList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel product = snapshot.getValue(ProductCountModel.class);
                        if (product != null) {
                            userCartProductList.add(product);
                            Collections.sort(userCartProductList, new Comparator<ProductCountModel>() {
                                @Override
                                public int compare(ProductCountModel listData, ProductCountModel t1) {
                                    Long ob1 = listData.getTime();
                                    Long ob2 = t1.getTime();

                                    return ob2.compareTo(ob1);

                                }
                            });
                            adapter.notifyDataSetChanged();
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
