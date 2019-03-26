package com.appsinventiv.toolsbazzar.Seller.Sales;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;

import com.appsinventiv.toolsbazzar.Models.InvoiceModel;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersAdapter;
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


public class SalesFragment extends Fragment {

    Context context;
    RecyclerView recycler_orders;
    ArrayList<InvoiceModel> itemList = new ArrayList<>();
    String orderStatus;
    InvoiceListAdapter adapter;
    ProgressBar progress;
    DatabaseReference mDatabase;
    String by;


    public SalesFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SalesFragment(String orderStatus, String by) {
        this.orderStatus = orderStatus;
        this.by = by;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (by.equalsIgnoreCase("courier")) {
            orderStatus = orderStatus + " by courier";
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_orders);
        progress = rootView.findViewById(R.id.progress);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new InvoiceListAdapter(context, itemList, 0, "", new InvoiceListAdapter.SelectInvoices() {
            @Override
            public void addToArray(long id, int position) {

            }

            @Override
            public void removeFromArray(long id, int position) {

            }
        });
        recyclerView.setAdapter(adapter);


        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();
        getDataFromServer();
    }

    private void getDataFromServer() {
        mDatabase.child("SellerInvoice").child(SharedPrefs.getVendor().getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equalsIgnoreCase("InvoiceCount")) {
                            continue;
                        } else {
                            InvoiceModel model = snapshot.getValue(InvoiceModel.class);
                            if (model != null) {
                                itemList.add(model);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    itemList.clear();
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//
    }

//    private void getOrdersFromDB(String key) {
//        mDatabase.child("Orders").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    OrderModel model = dataSnapshot.getValue(OrderModel.class);
//                    if (model != null) {
//                        if (model.getOrderStatus().equalsIgnoreCase(orderStatus)) {
//                            arrayList.add(model);
//                        }
//                    }
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
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }


}
