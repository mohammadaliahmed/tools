package com.appsinventiv.toolsbazzar.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Adapters.SearchProductsAdapter;
import com.appsinventiv.toolsbazzar.BottomSheets.ShowBottomDialog;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.NewProductModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.github.javiersantos.bottomdialogs.BottomDialog;
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

public class ListOfProducts extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ArrayList<Product> productArrayList = new ArrayList<>();
    ArrayList<Product> arrayList = new ArrayList<>();
    ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    SearchProductsAdapter adapter;
    DatabaseReference mDatabase;
    long cartItemCountFromDb;
    String size = "", color = "";
    ArrayList<String> userWishList = new ArrayList<>();
    String category;
    int selectedColor = -1;
    String colorSelected = "";
    int selected = -1;
    String sizeSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_products);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        category = getIntent().getStringExtra("parentCategory");
        this.setTitle("" + category);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(ListOfProducts.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchProductsAdapter(ListOfProducts.this, productArrayList, userCartProductList, userWishList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                size = "";
                color = "";
                if (product.getAttributesWithPics() != null) {
                    ShowBottomDialog.showSizeAndColorDialogNew(ListOfProducts.this, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                        @Override
                        public void onCancelled() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    if (product.getSizeList() != null && product.getColorList() != null) {

                        ShowBottomDialog.showSizeAndColorDialog(ListOfProducts.this, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                            @Override
                            public void onCancelled() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    } else if (product.getSizeList() != null && product.getColorList() == null) {
                        ShowBottomDialog.showSizeBottomDialog(ListOfProducts.this, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                            @Override
                            public void onCancelled() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else if (product.getColorList() != null && product.getSizeList() == null) {
                        ShowBottomDialog.showColorBottomDialog(ListOfProducts.this, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                            @Override
                            public void onCancelled() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else if (product.getColorList() == null && product.getSizeList() == null) {
                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                .child("cart").child(product.getId()).child("product").setValue(product);
                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());
                    }
                }

            }

            @Override
            public void deletedFromCart(final Product product, final int position) {

                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
            public void quantityUpdate(Product product, final int quantity, int position) {
                if (product != null) {
                    Log.d("here", userCartProductList.get(0).getProduct().getTitle() + "");
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

//                                    adapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

            }

            @Override
            public void isProductLiked(Product product, final boolean isLiked, int position) {
                if (isLiked) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("WishList").child(product.getId()).setValue(product.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Added to Wishlist");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonUtils.showToast("Error");

                        }
                    });
                    int likesCount = product.getLikesCount();
                    likesCount += 1;
                    mDatabase.child("Products").child(product.getId()).child("likesCount").setValue(likesCount);
                } else {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("WishList").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            CommonUtils.showToast("Removed from Wishlist");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonUtils.showToast("Error");

                        }
                    });
                    int likesCount = product.getLikesCount();
                    likesCount -= 1;
                    mDatabase.child("Products").child(product.getId()).child("likesCount").setValue(likesCount);
                }

            }
        }, true);
        recyclerView.setAdapter(adapter);


        getProductsFromDB();
        getUserCartProductsFromDB();
        getUserWishList();


    }

    private void getUserWishList() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("WishList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userWishList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String productId = snapshot.getValue(String.class);
                        if (productId != null) {
                            userWishList.add(productId);
                        }
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    userWishList.clear();
                    adapter.notifyDataSetChanged();
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

                        }
                    }
                    Collections.sort(userCartProductList, new Comparator<ProductCountModel>() {
                        @Override
                        public int compare(ProductCountModel listData, ProductCountModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();
                            return ob2.compareTo(ob1);

                        }
                    });
                    adapter.notifyDataSetChanged();

                } else {
                    userCartProductList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void getProductsFromDB() {
//        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    productArrayList.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Product product = snapshot.getValue(Product.class);
//                        if (product != null) {
//                            if (product.getAttributesWithPics() != null && snapshot.child("newAttributes").getValue() != null) {
//                                HashMap<String, ArrayList<NewProductModel>> newMap = new HashMap<>();
//                                for (DataSnapshot color : snapshot.child("newAttributes").getChildren()) {
//                                    ArrayList<NewProductModel> newProductModelArrayList = new ArrayList<>();
//                                    for (DataSnapshot size : color.getChildren()) {
//                                        NewProductModel countModel = size.getValue(NewProductModel.class);
//                                        if (countModel != null) {
//                                            newProductModelArrayList.add(countModel);
//                                        }
//                                        newMap.put(color.getKey(), newProductModelArrayList);
//                                    }
//
//                                }
//                                product.setProductCountHashmap(newMap);
//
//                            }
//
//                            if (product.isActive() && product.getSellerProductStatus().equalsIgnoreCase("approved")) {
//                                if (product.getCategory() != null) {
//
//                                    if (product.getCategory().contains(category)) {
////                                        productArrayList.add(product);
//                                        if (product.getSellingTo().equalsIgnoreCase("Both") || product.getSellingTo().equalsIgnoreCase(SharedPrefs.getCustomerType())) {
//
//                                            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
//                                                productArrayList.add(product);
//
//                                            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
//                                                productArrayList.add(product);
//
//                                            }
//                                        }
//
////
//                                    } else {
//                                    }
//                                }
//                            }
//                        }
//
//
//                    }
////                    adapter.updatelist(productArrayList);
//                    Collections.sort(productArrayList, new Comparator<Product>() {
//                        @Override
//                        public int compare(Product listData, Product t1) {
//                            String ob1 = listData.getTitle();
//                            String ob2 = t1.getTitle();
//
//                            return ob1.compareTo(ob2);
//
//                        }
//                    });
//                    adapter.notifyDataSetChanged();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void getProductsFromDB() {

        mDatabase.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
//                    progress.setVisibility(View.GONE);
                    productArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            if (product.getCategory().contains(category)) {
                                if (product.getAttributesWithPics() != null && snapshot.child("newAttributes").getValue() != null) {
                                    HashMap<String, ArrayList<NewProductModel>> newMap = new HashMap<>();
                                    for (DataSnapshot color : snapshot.child("newAttributes").getChildren()) {
                                        ArrayList<NewProductModel> newProductModelArrayList = new ArrayList<>();
                                        for (DataSnapshot size : color.getChildren()) {
                                            NewProductModel countModel = size.getValue(NewProductModel.class);
                                            if (countModel != null) {
                                                newProductModelArrayList.add(countModel);
                                            }
                                            newMap.put(color.getKey(), newProductModelArrayList);
                                        }

                                    }
                                    product.setProductCountHashmap(newMap);

                                }

//                            if (product.getAttributesWithPics() != null && snapshot.child("newAttributes").getValue() != null) {
//                                HashMap<String, ArrayList<NewProductModel>> newMap = new HashMap<>();
//                                for (DataSnapshot color : snapshot.child("newAttributes").getChildren()) {
//                                    ArrayList<NewProductModel> newProductModelArrayList = new ArrayList<>();
//                                    for (DataSnapshot size : color.getChildren()) {
//                                        NewProductModel countModel = size.getValue(NewProductModel.class);
//                                        if (countModel != null) {
//                                            newProductModelArrayList.add(countModel);
//                                        }
//                                        newMap.put(color.getKey(), newProductModelArrayList);
//                                    }
//
//                                }
//                                product.setProductCountHashmap(newMap);
//
//                            }

                                if (product.isActive() && (product.getUploadedBy() == null || product.getUploadedBy().equalsIgnoreCase("admin"))) {
                                    if (product.getSellingTo().equalsIgnoreCase("Both") || product.getSellingTo().equalsIgnoreCase(SharedPrefs.getCustomerType())) {
                                        productArrayList.add(product);
                                    }
                                } else if (product.getUploadedBy() != null && product.getUploadedBy().equalsIgnoreCase("seller")
                                        && product.getSellerProductStatus().equalsIgnoreCase("Approved")) {
                                    if (product.getSellingTo().equalsIgnoreCase("Both") || product.getSellingTo().equalsIgnoreCase(SharedPrefs.getCustomerType())) {
                                        productArrayList.add(product);
                                    }
                                }
                            }
                        }
                    }
                    Collections.sort(productArrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product listData, Product t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob2.compareTo(ob1);

                        }
                    });
                    adapter.notifyDataSetChanged();

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
        if (id == android.R.id.home) {

            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent i = new Intent(ListOfProducts.this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_cart) {
//            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
//                CommonUtils.showToast("Your Cart is empty");
//            } else {
            Intent i = new Intent(ListOfProducts.this, NewCart.class);
            startActivity(i);
//            }

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

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

    }


    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
