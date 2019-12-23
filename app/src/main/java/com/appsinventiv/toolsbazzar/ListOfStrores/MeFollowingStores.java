package com.appsinventiv.toolsbazzar.ListOfStrores;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
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
import java.util.HashMap;
import java.util.Map;

public class MeFollowingStores extends Fragment {
    DatabaseReference mDatabase;
    RecyclerView recyclerview;
    ArrayList<StoreListModel> itemList = new ArrayList<>();
    ListOfStroesAdapter adapter;
    private Context context;
    HashMap<String, ArrayList<String>> map = new HashMap<>();
    ArrayList<VendorModel> vendors = new ArrayList<>();
    ArrayList<String> userFollowingStoreList = new ArrayList<>();
    ArrayList<Product> productArrayList = new ArrayList<>();

    public MeFollowingStores() {
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

        return rootView;

    }

    private void setUpRecycler() {
        recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        adapter = new ListOfStroesAdapter(context, itemList, userFollowingStoreList, true);
        recyclerview.setAdapter(adapter);
        adapter.setCallbacks(new StoreCallbacks() {
            @Override
            public void followStore(VendorModel vendorModel) {

            }

            @Override
            public void unFollowStore(final VendorModel vendorModel) {
                showAlert(vendorModel);

            }
        });


//        getUserFollowingStores();
//

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
                        getUserFollowingStores();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAlert(final VendorModel model) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Unfollow " + model.getStoreName() + "?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("storeFollowing").child(model.getUsername()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDatabase.child("Sellers").child(model.getUsername()).child("followersCount").setValue(
                                                model.getFollowersCount() - 1
                                        );
                                        CommonUtils.showToast("Unfollowed " + model.getStoreName());
                                    }
                                });

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    private void getUserFollowingStores() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("storeFollowing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            itemList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                userFollowingStoreList.add(snapshot.getKey());
                                getStoreListFromDB(snapshot.getKey());

                            }
                            getFortCityProducts();

                        } else {
                            itemList.clear();
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getStoreListFromDB(String storeId) {
        mDatabase.child("Sellers").child(storeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    VendorModel model = dataSnapshot.getValue(VendorModel.class);
                    if (model != null) {
                        int size = vendors.size();
                        vendors.add(model);
                        getProductsFromDB(model, size);
                    }


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
            if (product != null) {

                if (product.getId() != null && vendor.getProducts() != null) {
                    for (Map.Entry<String, String> entry : vendor.getProducts().entrySet()) {
                        String key = entry.getKey();
                        if (key.equalsIgnoreCase(product.getId())) {
                            image.add(product.getThumbnailUrl());
                        }
                    }


                }
            }

        }
        vendors.get(size).setProductsimages(image);
        itemList.add(new StoreListModel(vendor, vendors.get(size).getProductsimages()));
        adapter.notifyDataSetChanged();


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
