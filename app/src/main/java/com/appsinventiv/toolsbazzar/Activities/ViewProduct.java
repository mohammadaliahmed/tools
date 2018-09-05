package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Adapters.RelatedProductsAdapter;
import com.appsinventiv.toolsbazzar.Adapters.SliderAdapter;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewProduct extends AppCompatActivity {
    String productId;
    TextView textCartItemCount;
    DatabaseReference mDatabase;
    TextView title, price, subtitle, count;
    public static ArrayList<String> picUrls = new ArrayList<>();

    ImageView image, increase, decrease;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    RelatedProductsAdapter adapter;
    ArrayList<Product> productArrayList = new ArrayList<>();
    ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    long cartItemCountFromDb;
    String productCategory;
    int quantity;
    ProductCountModel countModel;
    ArrayList<String> userWishList = new ArrayList<>();
    ViewPager mViewPager;
    SliderAdapter sliderAdapter;
    DotsIndicator dotsIndicator;

    Product product;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent i = getIntent();
        productId = i.getStringExtra("productId");

        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        subtitle = findViewById(R.id.subtitle);
        count = findViewById(R.id.count);
        increase = findViewById(R.id.increase);
        decrease = findViewById(R.id.decrease);
        relativeLayout = findViewById(R.id.relativeLayout);
        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Products").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        title.setText(product.getTitle());
                        subtitle.setText(product.getMeasurement());
                        price.setText("Rs. " + product.getRetailPrice());
                        ViewProduct.this.setTitle(product.getTitle());
                        productCategory = product.getMainCategory();
                        getProductsFromDB(productCategory);

                        getUserCartProductsFromDB();

                        setUpAddToCartButton();
                        picUrls.clear();
                        for (DataSnapshot childSnapshot : dataSnapshot.child("pictures").getChildren()) {
                            String model = childSnapshot.getValue(String.class);
                            picUrls.add(model);
                            sliderAdapter.notifyDataSetChanged();

                        }


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = findViewById(R.id.relatedProducts);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RelatedProductsAdapter(ViewProduct.this, productArrayList, userCartProductList, userWishList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                count.setTextColor(getResources().getColor(R.color.colorWhite));

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
        mViewPager = findViewById(R.id.viewpager);
        sliderAdapter = new SliderAdapter(ViewProduct.this, picUrls, 1);
        mViewPager.setAdapter(sliderAdapter);
        dotsIndicator.setViewPager(mViewPager);


    }

    private void setUpAddToCartButton() {

        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    countModel = dataSnapshot.getValue(ProductCountModel.class);
                    if (countModel != null) {
                        quantity = countModel.getQuantity();
                        if (quantity > 1) {
                            relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                            count.setTextColor(getResources().getColor(R.color.default_grey_text));

                            count.setText("" + quantity);
                            increase.setVisibility(View.VISIBLE);
                            decrease.setVisibility(View.VISIBLE);
                            decrease.setImageResource(R.drawable.ic_decrease_btn);
                        } else if (quantity == 1) {
                            relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                            count.setTextColor(getResources().getColor(R.color.default_grey_text));
                            count.setText("" + quantity);

                            increase.setVisibility(View.VISIBLE);
                            decrease.setVisibility(View.VISIBLE);
                            decrease.setImageResource(R.drawable.delete);
                        } else if (quantity == 0) {
                            relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
                            count.setTextColor(getResources().getColor(R.color.colorWhite));
                            increase.setVisibility(View.GONE);
                            decrease.setVisibility(View.GONE);
                            count.setText("Add to cart");
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {
                    if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {

                        relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                        count.setTextColor(getResources().getColor(R.color.default_grey_text));
                        quantity = product.getMinOrderQuantity();
                        count.setText("" + quantity);
                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                .child("cart").child(productId).setValue(new ProductCountModel(product, quantity, System.currentTimeMillis()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                        if (quantity > 0) {
                        } else {
                            relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                            count.setTextColor(getResources().getColor(R.color.default_grey_text));
                            quantity = 1;
                            count.setText("" + quantity);
                            mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                    .child("cart").child(productId).setValue(new ProductCountModel(product, quantity, System.currentTimeMillis()))
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
                    }


                } else {
                    CommonUtils.showToast("Please connect to internet");
                }
            }
        });

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {
                    quantity += 1;
                    count.setText("" + quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(countModel.getProduct().getId()).child("quantity").setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } else {
                    CommonUtils.showToast("Please connect to internet");
                }
            }

        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CommonUtils.isNetworkConnected()) {
                    if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                        if (quantity > (product.getMinOrderQuantity() + 1)) {
                            quantity--;
                            count.setText("" + quantity);

                            mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                    .child("cart").child(countModel.getProduct().getId()).child("quantity").setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        } else if (quantity > product.getMinOrderQuantity()) {
                            quantity--;
                            count.setText("" + quantity);
                            mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                    .child("cart").child(countModel.getProduct().getId()).child("quantity").setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        } else if (quantity == product.getMinOrderQuantity()) {
                            relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
                            count.setTextColor(getResources().getColor(R.color.colorWhite));
                            quantity = 0;
                            decrease.setVisibility(View.GONE);
                            increase.setVisibility(View.GONE);
                            count.setText("Add to cart");
                            mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                    .child("cart").child(countModel.getProduct().getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

                    } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                        if (quantity > 2) {
                            quantity--;
                            count.setText("" + quantity);

                            mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                    .child("cart").child(countModel.getProduct().getId()).child("quantity").setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        } else if (quantity > 1) {
                            quantity--;
                            count.setText("" + quantity);
                            mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                    .child("cart").child(countModel.getProduct().getId()).child("quantity").setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        } else if (quantity == 1) {
                            relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
                            count.setTextColor(getResources().getColor(R.color.colorWhite));
                            quantity = 0;
                            decrease.setVisibility(View.GONE);
                            increase.setVisibility(View.GONE);
                            count.setText("Add to cart");
                            mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                    .child("cart").child(countModel.getProduct().getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

                    }
                } else {
                    CommonUtils.showToast("Please connect to internet");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        getProductsFromDB(productCategory);
        getUserCartProductsFromDB();

        super.onResume();

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

    private void getProductsFromDB(final String cat) {

        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    productArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            if (product.getIsActive().equals("true")) {
                                if (product.getMainCategory().equals(cat)) {
                                    if (!product.getId().equals(productId)) {
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
        if (id == R.id.action_search) {
            Intent i = new Intent(ViewProduct.this, Search.class);
            startActivity(i);
        }
        if (id == R.id.action_cart) {
//            if (SharedPrefs.getIsLoggedIn().equals("yes")) {
            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
                CommonUtils.showToast("Your Cart is empty");
            } else {
                Intent i = new Intent(ViewProduct.this, Cart.class);
                startActivity(i);
            }
//            } else {
//                Intent i = new Intent(ListOfProducts.this, Login.class);
//                i.putExtra("takeUserToActivity", Constants.CART_ACTIVITY);
//                startActivity(i);
//            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

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
    public void onBackPressed() {
        finish();
    }
}