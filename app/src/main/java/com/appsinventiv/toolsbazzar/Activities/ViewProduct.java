package com.appsinventiv.toolsbazzar.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.RecentlyViewed.RecentlyViewedModel;
import com.appsinventiv.toolsbazzar.Adapters.RelatedProductsAdapter;
import com.appsinventiv.toolsbazzar.Adapters.SliderAdapter;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.Models.FortcityFeaturesModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerStore.SellerStoreView;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.CustomBottomDialog;
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
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

public class ViewProduct extends AppCompatActivity implements View.OnClickListener {
    TextView cart_count;
    ImageView cartIcon;
    String text;
    String productId;
    TextView textCartItemCount;
    FrameLayout blackCart, whiteCart;
    DatabaseReference mDatabase;
    TextView title, price, oldPrice, subtitle, count, product_description, textSize, textColor;
    public static ArrayList<String> picUrls = new ArrayList<>();
    LinearLayout sizes, colors;
    MenuItem menuItem;
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

    CollapsingToolbarLayout collapsing_toolbar;
    AppBarLayout app_bar_layout;
    Product product;
    RelativeLayout relativeLayout;
    RatingBar rating;
    String size = "", color = "";
    TextView percentageOff;
    TextView commentsCount, sizeGuide;
    ImageView share;
    private String productIdFromLink;
    LikeButton heart_button;
    boolean isLiked = false;
    int selectedColor = -1;
    String colorSelected = "";
    int selected = -1;
    String sizeSelected = "";
    RelativeLayout sizeSection, colorSection;
    TextView quantityText;
    ImageView whichArrow;
    TextView productDetails;
    CardView gotoStore;
    private TextView storeName;
    CircleImageView gotoSstore;
    private VendorModel vendorModel;
    ImageView fPromo, fEasy, fCash, fOnline, fPaypal;
    private FortcityFeaturesModel forCityFeatures;
    private boolean cannotRate;
    private boolean forCityProduct;
    ImageView social;
    ImageView backImage;
    NestedScrollView scrollview;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        onNewIntent(getIntent());

        Intent i = getIntent();
        productId = i.getStringExtra("productId");

        if (productId == null) {
            productId = productIdFromLink;
            getDataFromServer(productId);
        } else {
            getDataFromServer(productId);
        }


        toolbarTitle = findViewById(R.id.toolbarTitle);
        scrollview = findViewById(R.id.scrollview);
        backImage = findViewById(R.id.backImage);
        cart_count = findViewById(R.id.cart_count);
        cartIcon = findViewById(R.id.cartIcon);
        social = findViewById(R.id.social);
        fPromo = findViewById(R.id.fPromo);
        fCash = findViewById(R.id.fCash);
        fOnline = findViewById(R.id.fOnline);
        fEasy = findViewById(R.id.fEasy);
        fPaypal = findViewById(R.id.fPaypal);
        storeName = findViewById(R.id.storeName);
        gotoSstore = findViewById(R.id.gotoSstore);
        gotoStore = findViewById(R.id.gotoStore);
        productDetails = findViewById(R.id.productDetails);
        heart_button = findViewById(R.id.heart_button);
        colorSection = findViewById(R.id.colorSection);
        sizeSection = findViewById(R.id.sizeSection);
        collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        title = findViewById(R.id.title);
        commentsCount = findViewById(R.id.commentsCount);
        share = findViewById(R.id.share);
        price = findViewById(R.id.price);
        oldPrice = findViewById(R.id.oldPrice);
        subtitle = findViewById(R.id.subtitle);
        count = findViewById(R.id.count);
        increase = findViewById(R.id.increase);
        rating = findViewById(R.id.rating);
        textSize = findViewById(R.id.textSize);
        textColor = findViewById(R.id.textColor);
        percentageOff = findViewById(R.id.percentageOff);
        sizeGuide = findViewById(R.id.sizeGuide);
        quantityText = findViewById(R.id.quantityText);
        whichArrow = findViewById(R.id.whichArrow);

        decrease = findViewById(R.id.decrease);
        relativeLayout = findViewById(R.id.relativeLayout);
        product_description = findViewById(R.id.product_description);
        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);
        sizes = findViewById(R.id.sizes);
        colors = findViewById(R.id.colors);
        app_bar_layout = findViewById(R.id.app_bar_layout);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareProduct();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (i1 > 1) {
                        toolbarTitle.setText(product.getTitle());
                    } else {
                        toolbarTitle.setText("");
                    }
                }
            });
        }


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewProduct.this, ConnectWithUs.class));
            }
        });

        fPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 1);
                i.putExtra("text", forCityFeatures.getPromoCode());
                startActivity(i);
            }
        });
        fEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomBottomDialog.showFeatureDialog(ViewProduct.this, forCityFeatures.getEasyCheckout(), 2);
                Intent i = new Intent(ViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 2);
                i.putExtra("text", forCityFeatures.getEasyCheckout());
                startActivity(i);
            }
        });
        fCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomBottomDialog.showFeatureDialog(ViewProduct.this, forCityFeatures.getCashOnDelivery(), 3);
                Intent i = new Intent(ViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 3);
                i.putExtra("text", forCityFeatures.getCashOnDelivery());
                startActivity(i);
            }
        });
        fOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomBottomDialog.showFeatureDialog(ViewProduct.this, forCityFeatures.getOnlinePayments(), 4);
                Intent i = new Intent(ViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 4);
                i.putExtra("text", forCityFeatures.getOnlinePayments());
                startActivity(i);
            }
        });


        fPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomBottomDialog.showFeatureDialog(ViewProduct.this, forCityFeatures.getPaypalOptions(), 5);
                Intent i = new Intent(ViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 5);
                i.putExtra("text", forCityFeatures.getPaypalOptions());
                startActivity(i);
            }
        });
        gotoStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewProduct.this, SellerStoreView.class);
                i.putExtra("sellerId", product.getVendor().getUsername());
                startActivity(i);
            }
        });
        getLikeFromDB();


        sizeGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewProduct.this, SizeChart.class);
                startActivity(i);
            }
        });

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
//                    CommonUtils.showToast("Your Cart is empty");
//                } else {
                    Intent i = new Intent(ViewProduct.this, NewCart.class);
                    startActivity(i);
//                }
            }
        });

        heart_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
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
            }

            @Override
            public void unLiked(LikeButton likeButton) {
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
        });

        commentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewProduct.this, ProductComments.class);
                i.putExtra("productId", productId);
                startActivity(i);
            }
        });
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == -collapsing_toolbar.getHeight() + toolbar.getHeight()) {
                    //toolbar is collapsed here
                    //write your code here
                    collapsing_toolbar.setTitle(product.getTitle());
                    collapsing_toolbar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
                    collapsing_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.default_back));


                } else {
                    collapsing_toolbar.setTitle("");
                }

            }
        });


        recyclerView = findViewById(R.id.relatedProducts);
//        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new RelatedProductsAdapter(ViewProduct.this, productArrayList, userCartProductList, userWishList, new AddToCartInterface() {
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
        mViewPager = findViewById(R.id.viewpager);
        try {
            sliderAdapter = new SliderAdapter(ViewProduct.this, picUrls, 1);
            mViewPager.setAdapter(sliderAdapter);
        } catch (Exception e) {

        }

        dotsIndicator.setViewPager(mViewPager);
        addToRecentlyViewed(productId);


        getForCitFeaturesFrom();
        getCustomersRatedProducts();


    }

    private void setStatusBarColor(boolean abc) {
        if (abc) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                toolbarTitle.setText("");
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                toolbarTitle.setText(product.getTitle());
            }
        }
    }

    private void getCustomersRatedProducts() {
        mDatabase.child("Ratings").child(SharedPrefs.getUsername()).child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    rating.setEnabled(false);
                    cannotRate = true;
                } else {
                    rating.setEnabled(true);

                    cannotRate = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getForCitFeaturesFrom() {
        mDatabase.child("Settings").child("FortCityFeatures").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    forCityFeatures = dataSnapshot.getValue(FortcityFeaturesModel.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addToRecentlyViewed(String productId) {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("recentlyViewed").child(productId).setValue(new RecentlyViewedModel(productId, System.currentTimeMillis()));
    }

    private void showColorBottomDialog(final Product product, final int quantity) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(ViewProduct.this.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_color_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(this)
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
        TextView percentageOff = customView.findViewById(R.id.percentageOff);
        TextView oldPrice = customView.findViewById(R.id.oldPrice);
        TextView quantityText = customView.findViewById(R.id.quantityText);
        ImageView whichArrow = customView.findViewById(R.id.whichArrow);
        if (product.getQuantityAvailable() == 0) {
            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");
        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());
        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        title.setText(product.getTitle());
        Glide.with(ViewProduct.this).load(product.getThumbnailUrl()).into(image);

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
        LayoutInflater inflater = (LayoutInflater) getSystemService(ViewProduct.this.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_size_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(this)
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
        TextView sizeChart = customView.findViewById(R.id.sizeChart);
        TextView percentageOff = customView.findViewById(R.id.percentageOff);
        TextView oldPrice = customView.findViewById(R.id.oldPrice);
        TextView quantityText = customView.findViewById(R.id.quantityText);
        ImageView whichArrow = customView.findViewById(R.id.whichArrow);
        if (product.getQuantityAvailable() == 0) {
            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");
        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());
        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewProduct.this, SizeChart.class);
                startActivity(i);
            }
        });


        title.setText(product.getTitle());
        price.setText("Rs: " + product.getRetailPrice());
        Glide.with(ViewProduct.this).load(product.getThumbnailUrl()).into(image);

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

    private void showSizeAndColorBottomDialog(final Product product, final int quantity) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(ViewProduct.this.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_size_color_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(this)
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
        TextView percentageOff = customView.findViewById(R.id.percentageOff);
        TextView oldPrice = customView.findViewById(R.id.oldPrice);
        TextView quantityText = customView.findViewById(R.id.quantityText);
        ImageView whichArrow = customView.findViewById(R.id.whichArrow);
        if (product.getQuantityAvailable() == 0) {
            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");
        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());
        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewProduct.this, SizeChart.class);
                startActivity(i);
            }
        });

        title.setText(product.getTitle());
        Glide.with(ViewProduct.this).load(product.getThumbnailUrl()).into(image);

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

    @SuppressLint("ResourceAsColor")
    public void initiliazeSizeButtons(final Product product, final LinearLayout sizes) {
        sizes.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 120;
        params.width = 130;
        params.setMargins(5, 1, 5, 1);
        final ArrayList<Button> btnList = new ArrayList<>();

        for (int i = 0; i < product.getSizeList().size(); i++) {
            final Button btn = new Button(ViewProduct.this);
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.size_button_layout);
            btn.setText("" + product.getSizeList().get(i));
            sizes.addView(btn);
            btn.setTextSize(9);
            btn.setId(i);
            btnList.add(btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selected == -1) {
                        selected = view.getId();

                    } else {
                        sizes.getChildAt(selected).setBackgroundResource(R.drawable.size_button_layout);
                        selected = view.getId();
                    }
                    for (Button j : btnList) {

                        j.setTextColor(getResources().getColor(R.color.default_grey_text));

                    }
                    sizes.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                    sizeSelected = product.getSizeList().get(selected);
                    btn.setTextColor(getResources().getColor(R.color.colorPrimary));

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
        params.setMargins(5, 1, 5, 1);
        final ArrayList<Button> btnList = new ArrayList<>();

        if (product.getColorList() != null) {
            for (int i = 0; i < product.getColorList().size(); i++) {
                final Button btn = new Button(ViewProduct.this);
                btn.setLayoutParams(params);
                btn.setBackgroundResource(R.drawable.size_button_layout);
                btn.setText("" + product.getColorList().get(i));
                btn.setTextSize(9);
                colors.addView(btn);
                btn.setId(i);
                btnList.add(btn);


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedColor == -1) {
                            selectedColor = view.getId();

                        } else {
                            colors.getChildAt(selectedColor).setBackgroundResource(R.drawable.size_button_layout);
                            selectedColor = view.getId();
                        }


                        for (Button j : btnList) {

                            j.setTextColor(getResources().getColor(R.color.default_grey_text));

                        }
                        colors.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                        colorSelected = product.getColorList().get(selectedColor);
                        btn.setTextColor(getResources().getColor(R.color.colorPrimary));

                    }
                });
            }
        }
    }


    private void getLikeFromDB() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("WishList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.child(productId).getValue() != null) {
                        heart_button.setLiked(true);
                        isLiked = true;
                    } else {
                        heart_button.setLiked(false);
                        isLiked = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDataFromServer(String id) {
        mDatabase.child("Products").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        title.setText(product.getTitle());
                        subtitle.setText(product.getSubtitle());


                        if (product.getQuantityAvailable() == 0) {

                            quantityText.setText("Sorry item is currently out of stock");
                            whichArrow.setImageResource(R.drawable.ic_arrow_red);
                        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
                            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");


                        } else {
                            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());


                        }

                        if (product.getUploadedBy() != null && product.getUploadedBy().equalsIgnoreCase("seller")) {
                            getVendorDetailsFromDB();
                        }else{
                            storeName.setText("" + "Fort City");
                            forCityProduct = true;
                            Glide.with(ViewProduct.this).load(R.drawable.logo_small).into(gotoSstore);
                        }





                        String text1 = product.getBrandName() == null ? "Not available\n\n" : product.getBrandName() + "\n\n";
                        String text2 = product.getWarrantyType() == null ? "No Warranty\n\n" : product.getWarrantyType() + "\n\n";
                        String text3 = product.getDimen() == null ? "Not available\n\n" : product.getDimen() + "\n\n";
                        String text4 = product.getProductContents() == null ? "Not available" : product.getProductContents();
                        String newtext = text1 + text2 + text3 + text4;

                        productDetails.setText(
                                newtext
                        );


                        if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));

                            if (product.getOldRetailPrice() != 0) {
                                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));


                                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);

                                percentageOff.setText(percent + "% Off");
                            } else {
                                oldPrice.setVisibility(View.GONE);
                                percentageOff.setVisibility(View.GONE);
                            }

                        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                            if (product.getOldWholeSalePrice() != 0) {
                                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                                String percent = "" + String.format("%.0f", ((product.getOldWholeSalePrice() - product.getWholeSalePrice()) / product.getOldWholeSalePrice()) * 100);

                                percentageOff.setText(percent + "% Off");
                            } else {
                                oldPrice.setVisibility(View.GONE);

                                percentageOff.setVisibility(View.GONE);
                            }

                        }


                        rating.setRating(product.getRating());
                        setupRatingBar();
                        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        ViewProduct.this.setTitle(product.getTitle());
                        productCategory = product.getMainCategory();
                        product_description.setText(product.getDescription());
                        getProductsFromDB(product.getCategory().get(0));

                        getUserCartProductsFromDB();

                        setUpAddToCartButton();
                        picUrls.clear();
                        for (DataSnapshot childSnapshot : dataSnapshot.child("pictures").getChildren()) {
                            String model = childSnapshot.getValue(String.class);
                            picUrls.add(model);
                            try {
                                sliderAdapter.notifyDataSetChanged();

                            } catch (Exception e) {

                            }

                        }
                        if (product.getSizeList() != null) {
                            initiliazeSizeButtons();
                        } else {
                            sizeSection.setVisibility(View.GONE);
                        }
                        if (product.getColorList() != null) {
                            initializeColorButtons();
                        } else {
                            colorSection.setVisibility(View.GONE);
                            textColor.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupRatingBar() {
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float value, boolean b) {
                float rat = 0;
                if (product.getRating() == 0) {
                    rat = value;
                } else {
                    rat = (value + product.getRating()) / 2;
                }
                final float finalRat = rat;
                mDatabase.child("Products").child(productId).child("rating").setValue(rat).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Thanks for your rating");
                        mDatabase.child("Ratings").child(SharedPrefs.getUsername()).child(productId).setValue(productId);
                        mDatabase.child("Products").child(productId).child("ratingCount").setValue(product.getRatingCount() + 1);
                        if (finalRat > 3) {
                            mDatabase.child("Products").child(productId).child("positiveCount").setValue(product.getPositiveCount() + 1);

                        } else if (finalRat >= 2 && finalRat <= 3) {
                            mDatabase.child("Products").child(productId).child("neutralCount").setValue(product.getNeutralCount() + 1);

                        } else {
                            mDatabase.child("Products").child(productId).child("negativeCount").setValue(product.getNegativeCount() + 1);

                        }

                    }
                });


            }
        });
    }

    private void getVendorDetailsFromDB() {
        mDatabase.child("Sellers").child(product.getVendor().getVendorId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    vendorModel = dataSnapshot.getValue(VendorModel.class);
                    if (vendorModel != null) {
                        if (vendorModel.getPicUrl() != null) {
                            Glide.with(ViewProduct.this).load(vendorModel.getPicUrl()).into(gotoSstore);
                        }
                        storeName.setText("" + vendorModel.getStoreName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void shareProduct() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Check this out on " + getResources().getString(R.string.app_name) + ": \n" + product.getTitle() + "\n\nhttp://fortcity.com/product/" + product.getTitle().replace(" ", "-") + "/" + product.getId());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        startActivity(Intent.createChooser(shareIntent, "Share  via.."));
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
                    if (product.getSizeList() != null && product.getColorList() != null) {
                        if (!sizeSelected.equalsIgnoreCase("") && !colorSelected.equalsIgnoreCase("")) {
                            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                                if (product.getQuantityAvailable() < product.getMinOrderQuantity()) {
                                    CommonUtils.showToast("Out of stock");

                                } else {
                                    relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                                    count.setTextColor(getResources().getColor(R.color.default_grey_text));
                                    quantity = product.getMinOrderQuantity();
                                    count.setText("" + quantity);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("product").setValue(product);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("size").setValue(sizeSelected);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("color").setValue(colorSelected);


                                }
                            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                                if (quantity > 0) {
                                } else {
                                    if (product.getQuantityAvailable() == 0) {
                                        CommonUtils.showToast("Not available in stock");
                                    } else {
                                        relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                                        count.setTextColor(getResources().getColor(R.color.default_grey_text));
                                        quantity = 1;
                                        count.setText("" + quantity);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("product").setValue(product);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("size").setValue(sizeSelected);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("color").setValue(colorSelected);
                                    }
                                }
                            }
                        } else {
                            CommonUtils.showToast("Please select size & color");
                        }
                    } else if (product.getSizeList() != null) {
                        if (!sizeSelected.equalsIgnoreCase("")) {
                            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                                if (product.getQuantityAvailable() < product.getMinOrderQuantity()) {
                                    CommonUtils.showToast("Out of stock");

                                } else {
                                    relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                                    count.setTextColor(getResources().getColor(R.color.default_grey_text));
                                    quantity = product.getMinOrderQuantity();
                                    count.setText("" + quantity);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("product").setValue(product);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("size").setValue(sizeSelected);

                                }
                            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                                if (quantity > 0) {
                                } else {
                                    if (product.getQuantityAvailable() == 0) {
                                        CommonUtils.showToast("Not available in stock");
                                    } else {
                                        relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                                        count.setTextColor(getResources().getColor(R.color.default_grey_text));
                                        quantity = 1;
                                        count.setText("" + quantity);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("product").setValue(product);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("size").setValue(sizeSelected);
                                    }
                                }
                            }
                        } else {
                            CommonUtils.showToast("Please select size");
                        }
                    } else if (product.getColorList() != null) {
                        if (!colorSelected.equalsIgnoreCase("")) {
                            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                                if (product.getQuantityAvailable() < product.getMinOrderQuantity()) {
                                    CommonUtils.showToast("Out of stock");

                                } else {
                                    relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                                    count.setTextColor(getResources().getColor(R.color.default_grey_text));
                                    quantity = product.getMinOrderQuantity();
                                    count.setText("" + quantity);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("product").setValue(product);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                            .child("cart").child(product.getId()).child("color").setValue(colorSelected);

                                }
                            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                                if (quantity > 0) {
                                } else {
                                    if (product.getQuantityAvailable() == 0) {
                                        CommonUtils.showToast("Not available in stock");
                                    } else {
                                        relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                                        count.setTextColor(getResources().getColor(R.color.default_grey_text));
                                        quantity = 1;
                                        count.setText("" + quantity);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("product").setValue(product);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                                        mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                                .child("cart").child(product.getId()).child("color").setValue(colorSelected);
                                    }
                                }
                            }

                        } else {
                            CommonUtils.showToast("Please select color");
                        }
                    } else {
                        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                            if (product.getQuantityAvailable() < product.getMinOrderQuantity()) {
                                CommonUtils.showToast("Out of stock");

                            } else {
                                relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                                count.setTextColor(getResources().getColor(R.color.default_grey_text));
                                quantity = product.getMinOrderQuantity();
                                count.setText("" + quantity);
                                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                        .child("cart").child(product.getId()).child("product").setValue(product);
                                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                        .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                        .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                        .child("cart").child(product.getId()).child("color").setValue(colorSelected);

                            }

                        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                            if (product.getQuantityAvailable() < 1) {
                                CommonUtils.showToast("Out of stock");

                            } else if (quantity > 0) {
                            } else {
                                relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                                count.setTextColor(getResources().getColor(R.color.default_grey_text));
                                quantity = 1;
                                count.setText("" + quantity);
                                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                        .child("cart").child(product.getId()).child("product").setValue(product);
                                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                        .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                        .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                                mDatabase.child("Customers").child(SharedPrefs.getUsername())
                                        .child("cart").child(product.getId()).child("color").setValue(colorSelected);
                            }
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
                    if (quantity >= product.getQuantityAvailable()) {
                        CommonUtils.showToast("Only " + product.getQuantityAvailable() + " available");
                    } else {
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
                    }
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

    @SuppressLint("ResourceAsColor")
    public void initiliazeSizeButtons() {
        sizes.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 120;
        params.width = 130;
        params.setMargins(5, 1, 5, 1);
        final ArrayList<Button> btnList = new ArrayList<>();

        for (int i = 0; i < product.getSizeList().size(); i++) {
            final Button btn = new Button(ViewProduct.this);
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.size_button_layout);
            btn.setText("" + product.getSizeList().get(i));
            sizes.addView(btn);
            btn.setTextSize(9);
            btn.setId(i);
            btnList.add(btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selected == -1) {
                        selected = view.getId();

                    } else {
                        sizes.getChildAt(selected).setBackgroundResource(R.drawable.size_button_layout);
                        selected = view.getId();
                    }
                    for (Button j : btnList) {

                        j.setTextColor(getResources().getColor(R.color.default_grey_text));

                    }
                    sizes.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                    sizeSelected = product.getSizeList().get(selected);
                    btn.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    public void initializeColorButtons() {
        colors.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 120;
        params.width = 170;
        params.setMargins(5, 1, 5, 1);
        final ArrayList<Button> btnList = new ArrayList<>();

        if (product.getColorList() != null) {
            for (int i = 0; i < product.getColorList().size(); i++) {
                final Button btn = new Button(ViewProduct.this);
                btn.setLayoutParams(params);
                btn.setBackgroundResource(R.drawable.size_button_layout);
                btn.setText("" + product.getColorList().get(i));
                btn.setTextSize(9);
                colors.addView(btn);
                btn.setId(i);
                btnList.add(btn);


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedColor == -1) {
                            selectedColor = view.getId();

                        } else {
                            colors.getChildAt(selectedColor).setBackgroundResource(R.drawable.size_button_layout);
                            selectedColor = view.getId();
                        }
                        for (Button j : btnList) {

                            j.setTextColor(getResources().getColor(R.color.default_grey_text));

                        }
                        colors.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
                        colorSelected = product.getColorList().get(selectedColor);
                        btn.setTextColor(getResources().getColor(R.color.colorPrimary));

                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
//        getProductsFromDB(productCategory);
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
                                if (product.getCategory().contains(cat)) {
                                    if (!product.getId().equals(productId)) {
                                        productArrayList.add(product);

                                    }
                                }
                            }
                        }
                    }
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (item.getItemId() == android.R.id.home) {
//
//            finish();
//        }
//
//        if (id == R.id.action_cart) {
//            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
//                CommonUtils.showToast("Your Cart is empty");
//            } else {
//                Intent i = new Intent(ViewProduct.this, NewCart.class);
//                startActivity(i);
//            }
//
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_view_product, menu);

//        menuItem = menu.findItem(R.id.action_cart);
//
//        View actionView = MenuItemCompat.getActionView(menuItem);
//        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
//
//
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItemCountFromDb = dataSnapshot.getChildrenCount();
//                textCartItemCount.setText("" + cartItemCountFromDb);
                cart_count.setText("" + cartItemCountFromDb);
                SharedPrefs.setCartCount("" + cartItemCountFromDb);
                if (dataSnapshot.getChildrenCount() == 0) {
                    SharedPrefs.setCartCount("0");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        actionView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onOptionsItemSelected(menuItem);
//            }
//        });


        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
//        if (product.getSizeList() != null) {
//            if (selected == -1) {
//                selected = view.getId();
//
//            } else {
//                sizes.getChildAt(selected).setBackgroundResource(R.drawable.size_button_layout);
//                selected = view.getId();
//            }
//            sizes.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
//            sizeSelected = product.getSizeList().get(selected);
//        }

//        if (product.getColorList() != null) {
//            if (selectedColor == -1) {
//                selectedColor = view.getId();
//
//            } else {
//                colors.getChildAt(selectedColor).setBackgroundResource(R.drawable.size_button_layout);
//                selectedColor = view.getId();
//            }
//            colors.getChildAt(view.getId()).setBackgroundResource(R.drawable.size_button_selected);
//            colorSelected = product.getColorList().get(selectedColor);
//        }

    }

    protected void onNewIntent(Intent intent) {

        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            productIdFromLink = data.substring(data.lastIndexOf("/") + 1);
            productId = productIdFromLink;
        }
    }
}



