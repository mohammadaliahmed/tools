package com.appsinventiv.toolsbazzar.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Adapters.ProductsAdapter;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.Product;
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
import java.util.Collections;
import java.util.Comparator;

public class Search extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ArrayList<Product> productArrayList = new ArrayList<>();
    ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    ProductsAdapter adapter;
    DatabaseReference mDatabase;
    EditText search;
    long cartItemCountFromDb;
    Product product;
    String searchTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.setTitle("Search");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProductsAdapter(Search.this, productArrayList, userCartProductList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).setValue(new ProductCountModel(product, quantity, System.currentTimeMillis()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void deletedFromCart(final Product product, int position) {
                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getUserCartProductsFromDB();
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
        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userCartProductList.clear();
                productArrayList.clear();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userCartProductList.clear();
                productArrayList.clear();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                userCartProductList.clear();
                productArrayList.clear();
                searchTerm = "" + editable;
                if (editable.length() > 0) {
                    getProductsFromDB("" + editable);
                    getUserCartProductsFromDB();
                }

            }
        });


    }


    private void getUserCartProductsFromDB() {
        userCartProductList.clear();
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel product = snapshot.getValue(ProductCountModel.class);
                        if (product != null) {
                            if (!userCartProductList.contains(product)) {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getProductsFromDB(final String text) {
        productArrayList.clear();
        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    productArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        product = snapshot.getValue(Product.class);
                        if (product != null) {
                            if (product.getIsActive().equals("true")) {
                                if (product.getTitle().toLowerCase().contains(text)) {
                                    getUserCartProductsFromDB();
                                    if (!productArrayList.contains(product)) {
                                        productArrayList.add(product);
                                        Collections.sort(productArrayList, new Comparator<Product>() {
                                            @Override
                                            public int compare(Product listData, Product t1) {
                                                String ob1 = listData.getTitle();
                                                String ob2 = t1.getTitle();

                                                return ob1.compareTo(ob2);

                                            }
                                        });
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
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
//            if (SharedPrefs.getIsLoggedIn().equals("yes")) {
            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
                CommonUtils.showToast("Your Cart is empty");
            } else {
                Intent i = new Intent(Search.this, Cart.class);
                startActivity(i);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        final MenuItem mSearch = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userCartProductList.clear();
                productArrayList.clear();
                searchTerm = "" + newText;
                if (newText.length() > 0) {
                    getProductsFromDB("" + newText);
                    getUserCartProductsFromDB();
                }
                return false;
            }
        });

        View actionView = MenuItemCompat.getActionView(menuItem);
        final TextView textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

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
        // Get SearchView object.

    }

    @Override
    protected void onResume() {

        super.onResume();
    }
}
