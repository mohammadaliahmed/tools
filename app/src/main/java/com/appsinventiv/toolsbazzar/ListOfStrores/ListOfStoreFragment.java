package com.appsinventiv.toolsbazzar.ListOfStrores;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.ProductCountModel;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListOfStoreFragment extends Fragment {
    DatabaseReference mDatabase;
    RecyclerView recyclerview;
    ArrayList<StoreListModel> itemList = new ArrayList<>();
    ListOfStroesAdapter adapter;
    private Context context;
    HashMap<String, ArrayList<String>> map = new HashMap<>();
    ArrayList<VendorModel> vendors = new ArrayList<>();
    ArrayList<String> userFollowingStoreList = new ArrayList<>();
    ArrayList<Product> productArrayList = new ArrayList<>();

    public ListOfStoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.store_list_fragment, container, false);
        recyclerview = rootView.findViewById(R.id.recyclerview);


        setUpRecycler();
        getUserFollowingStores();
//        getStoreListFromDB();
        getProductss();
        return rootView;

    }

    private void getProductss() {
        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
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

    private void setUpRecycler() {
        recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        adapter = new ListOfStroesAdapter(context, itemList, userFollowingStoreList, false);
        recyclerview.setAdapter(adapter);
        adapter.setCallbacks(new StoreCallbacks() {
            @Override
            public void followStore(final VendorModel vendorModel) {
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("storeFollowing").child(vendorModel.getUsername()).setValue(vendorModel.getUsername()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabase.child("Sellers").child(vendorModel.getUsername()).child("followersCount").setValue(
                                vendorModel.getFollowersCount() + 1
                        );
                        CommonUtils.showToast("You are following " + vendorModel.getVendorName());
                    }
                });
            }


            @Override
            public void unFollowStore(VendorModel vendorModel) {

            }
        });


//

    }

    private void getUserFollowingStores() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("storeFollowing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userFollowingStoreList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String abc = snapshot.getKey();
                        userFollowingStoreList.add(abc);


                    }
                    adapter.notifyDataSetChanged();
                } else {
                    userFollowingStoreList.clear();
                    adapter.notifyDataSetChanged();

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
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        VendorModel model = snapshot.getValue(VendorModel.class);
                        if (model != null) {
                            int size = vendors.size();
                            vendors.add(model);
                            getProductsFromDB(model, size);

                        }
                    }
                    getFortCityProducts();

                    adapter.notifyDataSetChanged();

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
        itemList.add(new StoreListModel(vendor, image));

        adapter.notifyDataSetChanged();
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
            itemList.add(new StoreListModel(vendor, vendors.get(size).getProductsimages()));
        } else {
//                        CommonUtils.showToast("less than 6");
        }
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
