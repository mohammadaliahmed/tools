package com.appsinventiv.toolsbazzar.Activities.RecentlyViewed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.NewCart;
import com.appsinventiv.toolsbazzar.Activities.Search;
import com.appsinventiv.toolsbazzar.Activities.SizeChart;
import com.appsinventiv.toolsbazzar.Adapters.WishListAdapter;
import com.appsinventiv.toolsbazzar.BottomSheets.ShowBottomDialog;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
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

public class RecentViewedActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ArrayList<Product> productArrayList = new ArrayList<>();
    ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    WishListAdapter adapter;
    DatabaseReference mDatabase;
    EditText search;
    long cartItemCountFromDb;
    Product product;
    ArrayList<String> userWishList = new ArrayList<>();
    RelativeLayout wholeLayout;
    Button startShopping;
    String size = "", color = "";
    int selectedColor = -1;
    String colorSelected = "";
    int selected = -1;
    String sizeSelected = "";
    private ArrayList<RecentlyViewedModel> recentlyViewedModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_viewed);
        this.setTitle("Recently Viewed");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recycler);
        wholeLayout = findViewById(R.id.wholeLayout);
        startShopping = findViewById(R.id.startShopping);

        startShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        GridLayoutManager grid = new GridLayoutManager(this, 2);
        layoutManager = new LinearLayoutManager(RecentViewedActivity.this, LinearLayoutManager.VERTICAL, false);

        if (CommonUtils.screenSize() < 7) {
            recyclerView.setLayoutManager(layoutManager);

        }
        if (CommonUtils.screenSize() > 7) {
            recyclerView.setLayoutManager(grid);

        }
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new WishListAdapter(RecentViewedActivity.this, productArrayList, userCartProductList, userWishList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                size = "";
                color = "";
                if (product.getAttributesWithPics() != null) {
                    ShowBottomDialog.showSizeAndColorDialogNew(RecentViewedActivity.this, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                        @Override
                        public void onCancelled() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    if (product.getSizeList() != null && product.getColorList() != null) {
                        ShowBottomDialog.showSizeAndColorDialog(RecentViewedActivity.this, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                            @Override
                            public void onCancelled() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else if (product.getSizeList() != null && product.getColorList() == null) {
                        ShowBottomDialog.showSizeBottomDialog(RecentViewedActivity.this, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

                            @Override
                            public void onCancelled() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else if (product.getColorList() != null && product.getSizeList() == null) {
                        ShowBottomDialog.showColorBottomDialog(RecentViewedActivity.this, product, quantity, mDatabase, new ShowBottomDialog.AttributesListener() {

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
        });
        recyclerView.setAdapter(adapter);

        getUserWishList();
        getUserRecentProductsFromDB();
        getUserCartProductsFromDB();


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

    private void getUserRecentProductsFromDB() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("recentlyViewed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    productArrayList.clear();
                    recentlyViewedModelList.clear();
                    wholeLayout.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        RecentlyViewedModel recentlyViewedModel = snapshot.getValue(RecentlyViewedModel.class);
                        if (recentlyViewedModel != null) {
                            recentlyViewedModelList.add(recentlyViewedModel);
                            getProductsFromDB(recentlyViewedModel.getProductId());
                        }
                    }

                } else {
                    wholeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getUserCartProductsFromDB() {

        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userCartProductList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel product = snapshot.getValue(ProductCountModel.class);
                        if (product != null) {
                            if (!userCartProductList.contains(product)) {
                                userCartProductList.add(product);

                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getProductsFromDB(String id) {
        mDatabase.child("Products").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        if (product.isActive()) {
                            productArrayList.add(product);


                        }


                    }
                    Collections.sort(recentlyViewedModelList, new Comparator<RecentlyViewedModel>() {
                        @Override
                        public int compare(RecentlyViewedModel listData, RecentlyViewedModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob1.compareTo(ob2);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent i = new Intent(RecentViewedActivity.this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_cart) {
//            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
//                CommonUtils.showToast("Your Cart is empty");
//            } else {
            Intent i = new Intent(RecentViewedActivity.this, NewCart.class);
            startActivity(i);
//            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
