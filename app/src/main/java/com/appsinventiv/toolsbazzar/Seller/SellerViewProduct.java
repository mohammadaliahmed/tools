package com.appsinventiv.toolsbazzar.Seller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ConnectWithUs;
import com.appsinventiv.toolsbazzar.Activities.FortcityFeatures;
import com.appsinventiv.toolsbazzar.Activities.ProductComments;
import com.appsinventiv.toolsbazzar.Activities.SizeChart;
import com.appsinventiv.toolsbazzar.Activities.ViewProduct;
import com.appsinventiv.toolsbazzar.Adapters.SliderAdapter;
import com.appsinventiv.toolsbazzar.Models.FortcityFeaturesModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerStore.SellerStoreView;
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
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerViewProduct extends AppCompatActivity implements View.OnClickListener {
    String productId;
    DatabaseReference mDatabase;
    TextView title, price, oldPrice, subtitle, count, product_description, textSize, textColor;
    public static ArrayList<String> picUrls = new ArrayList<>();
    LinearLayout sizes, colors;
    MenuItem menuItem;
    ImageView image, increase, decrease;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    SellerRelatedProductsAdapter adapter;
    ArrayList<Product> productArrayList = new ArrayList<>();
    String productCategory;

    ViewPager mViewPager;
    SliderAdapter sliderAdapter;
    DotsIndicator dotsIndicator;

    CollapsingToolbarLayout collapsing_toolbar;
    AppBarLayout app_bar_layout;
    Product product;
    RelativeLayout relativeLayout;
    RatingBar rating;
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
    RelativeLayout relativeLayout1;
    CardView gotoStore;
    private TextView storeName;
    CircleImageView gotoSstore;
    private VendorModel vendorModel;
    ImageView fPromo, fEasy, fCash, fOnline, fPaypal;

    private FortcityFeaturesModel forCityFeatures;
    ImageView social;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_view_product);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
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

        social = findViewById(R.id.social);

        fPromo = findViewById(R.id.fPromo);
        fCash = findViewById(R.id.fCash);
        fOnline = findViewById(R.id.fOnline);
        fEasy = findViewById(R.id.fEasy);
        fPaypal = findViewById(R.id.fPaypal);

        gotoSstore = findViewById(R.id.gotoSstore);
        gotoStore = findViewById(R.id.gotoStore);
        storeName = findViewById(R.id.storeName);

        relativeLayout1 = findViewById(R.id.relativeLayout1);
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

        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerViewProduct.this,ConnectWithUs.class));
            }
        });


        HashMap<String, Double> map = SharedPrefs.getCommentsCount();
        if(map!=null&&map.size()>0) {
            map.put(productId, 0.0);
            SharedPrefs.setCommentsCount(map);
        }


        fPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 1);
                i.putExtra("text", forCityFeatures.getPromoCode());
                startActivity(i);
            }
        });
        fEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomBottomDialog.showFeatureDialog(ViewProduct.this, forCityFeatures.getEasyCheckout(), 2);
                Intent i = new Intent(SellerViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 2);
                i.putExtra("text", forCityFeatures.getEasyCheckout());
                startActivity(i);
            }
        });
        fCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomBottomDialog.showFeatureDialog(ViewProduct.this, forCityFeatures.getCashOnDelivery(), 3);
                Intent i = new Intent(SellerViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 3);
                i.putExtra("text", forCityFeatures.getCashOnDelivery());
                startActivity(i);
            }
        });
        fOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomBottomDialog.showFeatureDialog(ViewProduct.this, forCityFeatures.getOnlinePayments(), 4);
                Intent i = new Intent(SellerViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 4);
                i.putExtra("text", forCityFeatures.getOnlinePayments());
                startActivity(i);
            }
        });


        fPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomBottomDialog.showFeatureDialog(ViewProduct.this, forCityFeatures.getPaypalOptions(), 5);
                Intent i = new Intent(SellerViewProduct.this, FortcityFeatures.class);
                i.putExtra("id", 5);
                i.putExtra("text", forCityFeatures.getPaypalOptions());
                startActivity(i);
            }
        });
        gotoStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerViewProduct.this, SellerStoreView.class);
                i.putExtra("sellerId", product.getVendor().getUsername());
                startActivity(i);
            }
        });
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerViewProduct.this, EditProduct.class);
                i.putExtra("productId", product.getId());
                startActivity(i);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareProduct();
            }
        });
        getLikeFromDB();


        sizeGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerViewProduct.this, SellerSizeChart.class);
                startActivity(i);
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
                Intent i = new Intent(SellerViewProduct.this, SellerProductComments.class);
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
        adapter = new SellerRelatedProductsAdapter(SellerViewProduct.this, productArrayList);

        recyclerView.setAdapter(adapter);
        mViewPager = findViewById(R.id.viewpager);
        sliderAdapter = new SliderAdapter(SellerViewProduct.this, picUrls, 1);
        mViewPager.setAdapter(sliderAdapter);
        dotsIndicator.setViewPager(mViewPager);
        getForCitFeaturesFrom();
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

    private void getVendorDetailsFromDB() {
        mDatabase.child("Sellers").child(product.getVendor().getVendorId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    vendorModel = dataSnapshot.getValue(VendorModel.class);
                    if (vendorModel != null) {
                        if (vendorModel.getPicUrl() != null) {
                            Glide.with(SellerViewProduct.this).load(vendorModel.getPicUrl()).into(gotoSstore);
                        }
//                        SharedPrefs.setVendorModel(vendorModel);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            final Button btn = new Button(SellerViewProduct.this);
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
                final Button btn = new Button(SellerViewProduct.this);
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

                        String text1 = product.getBrandName() == null ? "Not available\n\n" : product.getBrandName() + "\n\n";
                        String text2 = product.getWarrantyType() == null ? "No Warranty\n\n" : product.getWarrantyType() + "\n\n";
                        String text3 = product.getDimen() == null ? "Not available\n\n" : product.getDimen() + "\n\n";
                        String text4 = product.getProductContents() == null ? "Not available" : product.getProductContents();
                        String newtext = text1 + text2 + text3 + text4;

                        productDetails.setText(
                                newtext
                        );
                        if (product.getUploadedBy() != null && product.getUploadedBy().equalsIgnoreCase("seller")) {
                            gotoStore.setVisibility(View.VISIBLE);
                            storeName.setText("Goto: " + product.getVendor().getStoreName());
                        } else {
                            gotoStore.setVisibility(View.GONE);
                        }


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

//                        if(product.getqua)
                        if (product.getVendor().getUsername() != null) {


                            if (product.getVendor().getUsername().equalsIgnoreCase(SharedPrefs.getVendor().getUsername())) {
                                relativeLayout1.setVisibility(View.VISIBLE);
                            } else {
                                relativeLayout1.setVisibility(View.GONE);
                            }

                        } else {
                            relativeLayout1.setVisibility(View.GONE);
                        }
                        if (product.getUploadedBy() != null && product.getUploadedBy().equalsIgnoreCase("seller")) {
                            getVendorDetailsFromDB();
                        }

                        rating.setRating(product.getRating());
                        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        SellerViewProduct.this.setTitle(product.getTitle());
                        productCategory = product.getMainCategory();
                        product_description.setText(product.getDescription());
                        getProductsFromDB(product.getCategory().get(0));

                        picUrls.clear();
                        for (DataSnapshot childSnapshot : dataSnapshot.child("pictures").getChildren()) {
                            String model = childSnapshot.getValue(String.class);
                            picUrls.add(model);
                            sliderAdapter.notifyDataSetChanged();

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

    private void shareProduct() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Check this out on " + getResources().getString(R.string.app_name) + ": \n" + product.getTitle() + "\n\nhttp://fortcity.com/product/" + product.getTitle().replace(" ", "-") + "/" + product.getId());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        startActivity(Intent.createChooser(shareIntent, "Share  via.."));
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
            final Button btn = new Button(SellerViewProduct.this);
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
                final Button btn = new Button(SellerViewProduct.this);
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

        super.onResume();

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
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        if (id == R.id.action_edit) {
            if (product.getVendor().getUsername() != null) {
                if (!product.getVendor().getUsername().equalsIgnoreCase(SharedPrefs.getVendor().getUsername())) {
//                relativeLayout1.setVisibility(View.GONE);
                    CommonUtils.showToast("Not your product");

                } else {
                    Intent i = new Intent(SellerViewProduct.this, EditProduct.class);
                    i.putExtra("productId", product.getId());
                    startActivity(i);
                }
            } else {
                CommonUtils.showToast("Not your product");

            }


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_seller_view_product, menu);


        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {


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