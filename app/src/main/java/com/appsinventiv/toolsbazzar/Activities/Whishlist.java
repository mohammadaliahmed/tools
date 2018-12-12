package com.appsinventiv.toolsbazzar.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.appsinventiv.toolsbazzar.Adapters.AttributesAdapter;
import com.appsinventiv.toolsbazzar.Adapters.RelatedProductsAdapter;
import com.appsinventiv.toolsbazzar.Adapters.SearchProductsAdapter;
import com.appsinventiv.toolsbazzar.Adapters.WishListAdapter;
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

public class Whishlist extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whishlist);
        this.setTitle("Wish List");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        layoutManager = new LinearLayoutManager(Whishlist.this, LinearLayoutManager.VERTICAL, false);

        if (CommonUtils.screenSize() < 7) {
            recyclerView.setLayoutManager(layoutManager);

        }
        if (CommonUtils.screenSize() > 7) {
            recyclerView.setLayoutManager(grid);

        }
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WishListAdapter(Whishlist.this, productArrayList, userCartProductList, userWishList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                size = "";
                color = "";
                if (product.getSizeList() != null && product.getColorList() != null) {
                    showSizeAndColorBottomDialog(product, quantity);
                } else if (product.getSizeList() != null && product.getColorList() == null) {
                    showSizeBottomDialog(product, quantity);
                } else if (product.getColorList() != null && product.getSizeList() == null) {
                    showColorBottomDialog(product, quantity);
                } else if (product.getColorList() == null && product.getSizeList() == null) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());
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
        getWishListfromDB();
        getUserCartProductsFromDB();


    }

    private void showColorBottomDialog(final Product product, final int quantity) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_color_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(Whishlist.this)
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        TextView title = customView.findViewById(R.id.title);
        TextView price = customView.findViewById(R.id.price);
        ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        Button confirm = customView.findViewById(R.id.confirm);
        LinearLayout sizes = customView.findViewById(R.id.sizes);
        LinearLayout colors = customView.findViewById(R.id.colors);

        title.setText(product.getTitle());
        price.setText("Rs: " + product.getRetailPrice());
        Glide.with(Whishlist.this).load(product.getThumbnailUrl()).into(image);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!colorSelected.equalsIgnoreCase("")) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());


                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("color").setValue(colorSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            colorSelected = "";
                            selectedColor = -1;
                            sizeSelected = "";
                            selected = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select color");

                }
            }
        });
        initializeColorButtons(product, colors);

        bottomDialog.show();
    }

    private void showSizeBottomDialog(final Product product, final int quantity) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_size_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(Whishlist.this)
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        TextView title = customView.findViewById(R.id.title);
        TextView price = customView.findViewById(R.id.price);
        ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        final Button confirm = customView.findViewById(R.id.confirm);
        LinearLayout sizes = customView.findViewById(R.id.sizes);


        TextView sizeChart = customView.findViewById(R.id.sizeChart);

        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Whishlist.this, SizeChart.class);
                Whishlist.this.startActivity(i);
            }
        });

        title.setText(product.getTitle());
        price.setText("Rs: " + product.getRetailPrice());
        Glide.with(Whishlist.this).load(product.getThumbnailUrl()).into(image);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sizeSelected.equalsIgnoreCase("")) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("size").setValue(sizeSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sizeSelected = "";
                            selected = -1;
                            colorSelected = "";
                            selectedColor = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select size ");

                }
            }
        });
        initiliazeSizeButtons(product, sizes);

        bottomDialog.show();
    }

    @SuppressLint("ResourceAsColor")
    public void initiliazeSizeButtons(final Product product, final LinearLayout sizes) {
        sizes.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 120;
        params.width = 130;
        params.setMargins(10, 1, 10, 1);

        for (int i = 0; i < product.getSizeList().size(); i++) {
            Button btn = new Button(Whishlist.this);
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.size_button_layout);
            btn.setText("" + product.getSizeList().get(i));
            sizes.addView(btn);
            btn.setTextSize(8);
            btn.setId(i);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selected == -1) {
                        selected = view.getId();

                    } else {
                        sizes.getChildAt(selected).setBackgroundResource(R.drawable.size_button_layout);
                        selected = view.getId();
                    }
                    sizes.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                    sizeSelected = product.getSizeList().get(selected);
                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    public void initializeColorButtons(final Product product, final LinearLayout colors) {
        colors.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 120;
        params.width = 170;
        params.setMargins(10, 1, 10, 1);
        if (product.getColorList() != null) {
            for (int i = 0; i < product.getColorList().size(); i++) {
                Button btn = new Button(Whishlist.this);
                btn.setLayoutParams(params);
                btn.setBackgroundResource(R.drawable.size_button_layout);
                btn.setText("" + product.getColorList().get(i));
                btn.setTextSize(8);
                colors.addView(btn);
                btn.setId(i);


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedColor == -1) {
                            selectedColor = view.getId();

                        } else {
                            colors.getChildAt(selectedColor).setBackgroundResource(R.drawable.size_button_layout);
                            selectedColor = view.getId();
                        }
                        colors.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                        colorSelected = product.getColorList().get(selectedColor);
                    }
                });
            }
        }
    }

    private void showSizeAndColorBottomDialog(final Product product, final int quantity) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_size_color_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(Whishlist.this)
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        TextView title = customView.findViewById(R.id.title);
        TextView price = customView.findViewById(R.id.price);
        ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        Button confirm = customView.findViewById(R.id.confirm);
        LinearLayout sizes = customView.findViewById(R.id.sizes);
        LinearLayout colors = customView.findViewById(R.id.colors);
        TextView sizeChart = customView.findViewById(R.id.sizeChart);

        title.setText(product.getTitle());
        price.setText("Rs: " + product.getRetailPrice());
        Glide.with(Whishlist.this).load(product.getThumbnailUrl()).into(image);

        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Whishlist.this, SizeChart.class);
                Whishlist.this.startActivity(i);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sizeSelected.equalsIgnoreCase("") && !colorSelected.equalsIgnoreCase("")) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("size").setValue(sizeSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sizeSelected = "";
                            selected = -1;
                        }
                    });
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("color").setValue(colorSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            colorSelected = "";
                            selectedColor = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select size and color");

                }
            }
        });
        initializeColorButtons(product, colors);
        initiliazeSizeButtons(product, sizes);

        bottomDialog.show();
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

    private void getWishListfromDB() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("WishList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    productArrayList.clear();
                    wholeLayout.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        getProductsFromDB(snapshot.getKey());
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

    private void getProductsFromDB(String id) {
        mDatabase.child("Products").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        if (product.getIsActive().equals("true")) {
                            productArrayList.add(product);
                            Collections.sort(productArrayList, new Comparator<Product>() {
                                @Override
                                public int compare(Product listData, Product t1) {
                                    String ob1 = listData.getTitle();
                                    String ob2 = t1.getTitle();

                                    return ob1.compareTo(ob2);

                                }
                            });

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
            Intent i = new Intent(Whishlist.this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_cart) {
//            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
//                CommonUtils.showToast("Your Cart is empty");
//            } else {
            Intent i = new Intent(Whishlist.this, Cart.class);
            startActivity(i);
//            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
