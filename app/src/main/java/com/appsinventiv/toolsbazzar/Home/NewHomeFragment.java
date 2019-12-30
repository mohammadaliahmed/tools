package com.appsinventiv.toolsbazzar.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ChooseMainCategory;
import com.appsinventiv.toolsbazzar.Activities.LiveChat;
import com.appsinventiv.toolsbazzar.Activities.MainActivity;
import com.appsinventiv.toolsbazzar.Activities.NewCart;
import com.appsinventiv.toolsbazzar.Activities.SizeChart;
import com.appsinventiv.toolsbazzar.Activities.Whishlist;
import com.appsinventiv.toolsbazzar.Activities.WholesaleLiveChat;
import com.appsinventiv.toolsbazzar.Adapters.DealsFragmentAdapter;
import com.appsinventiv.toolsbazzar.Adapters.MainCategoryAdapter;
import com.appsinventiv.toolsbazzar.Adapters.RelatedProductsAdapter;
import com.appsinventiv.toolsbazzar.Customer.CustomerScreen;
import com.appsinventiv.toolsbazzar.Interface.AddToCartInterface;
import com.appsinventiv.toolsbazzar.ListOfStrores.StoreListModel;
import com.appsinventiv.toolsbazzar.Models.MainCategoryModel;
import com.appsinventiv.toolsbazzar.Models.NewProductModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
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
import java.util.Map;

/**
 * Created by AliAh on 27/08/2018.
 */

public class NewHomeFragment extends Fragment {
    Context context;
    RecyclerView categoryRecycler;
    NewMainCategoryAdapter categoryAdapter;
    HomeListOfStoresAdapter storesAdapter;
    private View rootView;
    ArrayList<MainCategoryModel> categoryList = new ArrayList<>();
    DatabaseReference mDatabase;
    RecyclerView sellersRecycler;
    private ArrayList<StoreListModel> storeProductList = new ArrayList<>();
    ArrayList<Product> productArrayList = new ArrayList<>();
    ViewPager viewpager1;
    TabLayout sliding_tabs1;
    LinearLayout ic_settings, ic_chat, ic_mycart, ic_orders, ic_wishlist;

    ArrayList<VendorModel> vendors = new ArrayList<>();

    TextView viewAll;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        rootView = inflater.inflate(R.layout.new_home_fragment, container, false);

        initCategoriesAdapter();
        initSellersProducts();
        initDealView();
        initMenu();
        return rootView;
    }

    private void initMenu() {
        ic_settings = rootView.findViewById(R.id.ic_settings);
        ic_chat = rootView.findViewById(R.id.ic_chat);
        ic_mycart = rootView.findViewById(R.id.ic_mycart);
        ic_orders = rootView.findViewById(R.id.ic_orders);
        ic_wishlist = rootView.findViewById(R.id.ic_wishlist);
        viewAll = rootView.findViewById(R.id.viewAll);


        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ChooseMainCategory.class));

            }
        });

        ic_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                    Intent i = new Intent(context, WholesaleLiveChat.class);
                    startActivity(i);
                } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                    Intent i = new Intent(context, LiveChat.class);
                    startActivity(i);
                }

            }
        });
        ic_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, CustomerScreen.class);
                startActivity(i);
            }
        });
        ic_mycart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, NewCart.class);
                startActivity(i);
            }
        });
        ic_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChooseMainCategory.class);
                startActivity(i);
            }
        });
        ic_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Whishlist.class);
                startActivity(i);
            }
        });
    }

    private void initDealView() {
        ViewPager viewPager = rootView.findViewById(R.id.viewpager1);
        ArrayList<String> categoryList = new ArrayList<>();

        categoryList.add("Top Sellers");
        categoryList.add("Deals");
        categoryList.add("Most Liked");


        DealsFragmentAdapter adapter = new DealsFragmentAdapter(getChildFragmentManager(), context, categoryList);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = rootView.findViewById(R.id.sliding_tabs1);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void initCategoriesAdapter() {
        int gridCount = 4;
        int returnSize = 8;
        if (CommonUtils.screenSize() > 7) {
            gridCount = 6;
            returnSize = 12;
        }
        if (CommonUtils.screenSize() > 9) {
            gridCount = 8;
            returnSize = 16;
        }

        categoryRecycler = rootView.findViewById(R.id.categoryRecycler);
        categoryRecycler.setLayoutManager(new GridLayoutManager(context, gridCount));
        categoryAdapter = new NewMainCategoryAdapter(context, categoryList, returnSize);
        categoryRecycler.setAdapter(categoryAdapter);
        getMainCategoriesFromDb();

    }

    private void getMainCategoriesFromDb() {
        mDatabase.child("Settings").child("Categories").child("MainCategories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MainCategoryModel categoryTitle = snapshot.getValue(MainCategoryModel.class);
                        categoryList.add(categoryTitle);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initSellersProducts() {
        sellersRecycler = rootView.findViewById(R.id.sellersRecycler);
        sellersRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        storesAdapter = new HomeListOfStoresAdapter(context, storeProductList);
        sellersRecycler.setAdapter(storesAdapter);
        getProductss();
    }

    private void getProductss() {
        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
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

                            productArrayList.add(product);
                        }
                    }
                    if (productArrayList.size() > 0) {
                        getStoreListFromDB();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getStoreListFromDB() {
        mDatabase.child("Sellers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    storeProductList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        VendorModel model = snapshot.getValue(VendorModel.class);
                        if (model != null) {
                            int size = vendors.size();
                            vendors.add(model);
                            getProductsFromDB(model, size);

                        }
                    }
                    getFortCityProducts();

                    storesAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFortCityProducts() {
        ArrayList<String> image = new ArrayList<>();
        for (Product product : productArrayList) {
            if (product != null) {
                if (product.getUploadedBy() != null && product.getSellerProductStatus() != null) {
                    if (product.getUploadedBy().equalsIgnoreCase("admin") && product.getSellerProductStatus().equalsIgnoreCase("Approved")) {
                        image.add(product.getThumbnailUrl());
                    }
                }
            }

        }
        VendorModel vendor = new VendorModel();
        vendor.setUsername("Fort City");
        vendor.setStoreName("Fort City");
        vendor.setPicUrl("https://firebasestorage.googleapis.com/v0/b/toolsbazzar.appspot.com/o/logos%2Fapp_icon.png?alt=media&token=1a7074e3-0d71-4508-9292-412b0af2046c");
        vendor.setStoreCover(SharedPrefs.getCompanyDetails().getCoverPicUrl());
        storeProductList.add(new StoreListModel(vendor, image));

        storesAdapter.notifyDataSetChanged();
    }


    private void getProductsFromDB(final VendorModel vendor, final int size) {

        ArrayList<String> image = new ArrayList<>();
        for (Product product : productArrayList) {
            if (product.getId() != null && vendor.getProducts() != null) {
                for (Map.Entry<String, String> entry : vendor.getProducts().entrySet()) {
                    String key = entry.getKey();
                    if (key.equalsIgnoreCase(product.getId())) {
                        image.add(product.getThumbnailUrl());
                    }
                }
            }

        }
        vendors.get(size).setProductsimages(image);
        if (vendors.get(size).getProductsimages().size() > 5) {
            storeProductList.add(new StoreListModel(vendor, vendors.get(size).getProductsimages()));
        } else {
//                        CommonUtils.showToast("less than 6");
        }
        storesAdapter.notifyDataSetChanged();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
