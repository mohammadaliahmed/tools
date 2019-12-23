package com.appsinventiv.toolsbazzar.Seller.SellerStore;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.NewCart;
import com.appsinventiv.toolsbazzar.Adapters.SellerStoreProductsAdapter;
import com.appsinventiv.toolsbazzar.Models.CompanyDetailsModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.appsinventiv.toolsbazzar.Utils.SimpleFragmentPagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerStoreView extends AppCompatActivity {
    RecyclerView recyclerview;
    Button follow;

    ImageView storeCover;
    CircleImageView profilePic;
    DatabaseReference mDatabase;
    TextView storeName;
    private String sellerId;
    private VendorModel model;
    CollapsingToolbarLayout collapsing_toolbar;

    AppBarLayout app_bar_layout;
    ImageView backImage;
    NestedScrollView scrollview;
    TextView toolbarTitle;
    TextView cart_count;
    ImageView cartIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_store_view);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


//        this.setTitle("");
        toolbarTitle = findViewById(R.id.toolbarTitle);
        scrollview = findViewById(R.id.scrollview);
        backImage = findViewById(R.id.backImage);
        cart_count = findViewById(R.id.cart_count);
        cartIcon = findViewById(R.id.cartIcon);
        app_bar_layout = findViewById(R.id.app_bar_layout);
        collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        profilePic = findViewById(R.id.profilePic);
        follow = findViewById(R.id.follow);
        storeName = findViewById(R.id.storeName);
        storeCover = findViewById(R.id.storeCover);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == -collapsing_toolbar.getHeight() + toolbar.getHeight()) {
                    //toolbar is collapsed here
                    //write your code here
                    collapsing_toolbar.setTitle(model == null ? "Fort City" : model.getStoreName());
                    collapsing_toolbar.setBackgroundColor(getResources().getColor(R.color.default_back));
                    collapsing_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.default_back));


                } else {
                    collapsing_toolbar.setTitle("");
                }

            }
        });

        if(SharedPrefs.getCustomerModel()!=null){

        }else{
            cartIcon.setVisibility(View.INVISIBLE);
            cart_count.setVisibility(View.INVISIBLE);
        }
        sellerId = getIntent().getStringExtra("sellerId");


        ViewPager viewPager = findViewById(R.id.viewpager1);
        ArrayList<String> orderStatusList = new ArrayList<>();
        orderStatusList.add("New Arrivals");
        orderStatusList.add("View All");
        SellerStoreFragmentAdapter adapter = new SellerStoreFragmentAdapter(this, orderStatusList, getSupportFragmentManager(), sellerId);
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        if (SharedPrefs.getVendor() != null) {
            follow.setVisibility(View.GONE);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (i1 > 1) {
                        toolbarTitle.setText(model==null?"Fort City":model.getStoreName());
                    } else {
                        toolbarTitle.setText("");
                    }
                }
            });
        }

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerStoreView.this, NewCart.class));
            }
        });


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long cartItemCountFromDb = dataSnapshot.getChildrenCount();
//                textCartItemCount.setText("" + cartItemCountFromDb);
                cart_count.setText("" + cartItemCountFromDb);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
//        adapter = new SellerStoreProductsAdapter(this, productArrayList, userWishList, new AddToCartInterface() {
//            @Override
//            public void addedToCart(Product product, int quantity, int position) {
//
//            }
//
//            @Override
//            public void deletedFromCart(Product product, int position) {
//
//            }
//
//            @Override
//            public void quantityUpdate(Product product, int quantity, int position) {
//
//            }
//
//            @Override
//            public void isProductLiked(Product product, boolean isLiked, int position) {
//                if (isLiked) {
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
//                            .child("WishList").child(product.getId()).setValue(product.getId())
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    CommonUtils.showToast("Added to Wishlist");
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            CommonUtils.showToast("Error");
//
//                        }
//                    });
//                    int likesCount = product.getLikesCount();
//                    likesCount += 1;
//                    mDatabase.child("Products").child(product.getId()).child("likesCount").setValue(likesCount);
//                } else {
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
//                            .child("WishList").child(product.getId()).removeValue()
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                    CommonUtils.showToast("Removed from Wishlist");
//
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            CommonUtils.showToast("Error");
//
//                        }
//                    });
//                    int likesCount = product.getLikesCount();
//                    likesCount -= 1;
//                    mDatabase.child("Products").child(product.getId()).child("likesCount").setValue(likesCount);
//                }
//            }
//        });
//        recyclerview.setAdapter(adapter);
        if (sellerId == null) {
            getForCityFromDB();
        } else {
            getSellerFromDB();
        }
//        getSellerProductsFromDB();
//        getUserWishList();
//        getUserWishList();

    }

    private void getForCityFromDB() {
        mDatabase.child("Settings").child("CompanyDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    CompanyDetailsModel model = dataSnapshot.getValue(CompanyDetailsModel.class);
                    if (model != null) {
//                        if (!model.getUsername().equalsIgnoreCase(SharedPrefs.getVendor().getUsername())) {
//                            follow.setVisibility(View.GONE);
//                        } else {
//                            follow.setVisibility(View.GONE);
//                        }
                        storeName.setText("Fort City");

                        Glide.with(SellerStoreView.this).load(R.drawable.logo_small).into(profilePic);


                        if (model.getCoverPicUrl() != null) {
                            Glide.with(SellerStoreView.this).load(model.getCoverPicUrl()).into(storeCover);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getSellerFromDB() {
        mDatabase.child("Sellers").child(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    model = dataSnapshot.getValue(VendorModel.class);
                    if (model != null) {
//                        if (!model.getUsername().equalsIgnoreCase(SharedPrefs.getVendor().getUsername())) {
//                            follow.setVisibility(View.GONE);
//                        } else {
//                            follow.setVisibility(View.GONE);
//                        }
                        storeName.setText(model.getStoreName());
                        if (model.getPicUrl() != null) {
                            Glide.with(SellerStoreView.this).load(model.getPicUrl()).into(profilePic);

                        }
                        if (model.getStoreCover() != null) {
                            Glide.with(SellerStoreView.this).load(model.getStoreCover()).into(storeCover);
//
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void getSellerProductsFromDB() {
//        mDatabase.child("Products").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    productArrayList.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Product product = snapshot.getValue(Product.class);
//                        if (product != null) {
//
//                            if (product.getVendor() != null && product.getVendor().getVendorId() != null) {
//                                if (product.getVendor().getVendorId().equalsIgnoreCase(sellerId)) {
//
//                                    productArrayList.add(product);
//
//
//                                }
//                            }
//
//
//                        }
//                    }
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
////                    adapter.updatelist(productArrayList);
//
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
//
//    }

//    private void getUserWishList() {
//        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("WishList").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    userWishList.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        String productId = snapshot.getValue(String.class);
//                        if (productId != null) {
//                            userWishList.add(productId);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//
//                } else {
//                    userWishList.clear();
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
