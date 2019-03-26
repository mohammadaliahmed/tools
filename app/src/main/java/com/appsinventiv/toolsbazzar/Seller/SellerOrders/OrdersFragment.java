package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

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

import com.appsinventiv.toolsbazzar.Models.OrderModel;

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
import java.util.Collections;
import java.util.Comparator;


public class OrdersFragment extends Fragment {

    Context context;
    RecyclerView recycler_orders;
    ArrayList<OrderModel> arrayList = new ArrayList<>();
    String orderStatus;
    OrdersAdapter adapter;
    ProgressBar progress;
    DatabaseReference mDatabase;
    String by;


    public OrdersFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public OrdersFragment(String orderStatus, String by) {
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
        adapter = new OrdersAdapter(context, arrayList, new OrdersAdapter.UpdateOrderStatus() {
            @Override
            public void markAsProcessing(String orderId) {
                updateOrderStatus(orderId);
            }

            @Override
            public void markAsCancelled(String orderId) {
                updateOrderStatusAsCencelled(orderId);
            }

            @Override
            public void markAsDeleted(String orderId) {
                markOrderAsDeleted(orderId);
            }
        });
        recyclerView.setAdapter(adapter);


        return rootView;

    }

    private void updateOrderStatusAsCencelled(final String orderId) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Mark order " + orderId + " as cancelled?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.child("Orders").child(orderId).child("orderStatus").setValue("Cancelled").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Order status updated");
                                getDataFromServer();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                CommonUtils.showToast("Error: " + e.getMessage());

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

    private void markOrderAsDeleted(final String orderId) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Delete Order " + orderId + "?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.child("Orders").child(orderId).child("orderStatus").setValue("Deleted").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Order status updated");
                                getDataFromServer();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                CommonUtils.showToast("Error: " + e.getMessage());

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

    private void updateOrderStatus(final String orderId) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Mark order " + orderId + " as under process?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.child("Orders").child(orderId).child("orderStatus").setValue("Under Process").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Order status updated");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                CommonUtils.showToast("Error: " + e.getMessage());

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

    @Override
    public void onResume() {
        super.onResume();
        getDataFromServer();
    }

    private void getDataFromServer() {
        mDatabase.child("Sellers").child(SharedPrefs.getVendor().getUsername()).child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    arrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        getOrdersFromDB(snapshot.getKey());
                    }
//                    adapter.notifyDataSetChanged();
                } else {
                    arrayList.clear();
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        mDatabase.child("Orders").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                arrayList.clear();
//                if (dataSnapshot.getValue() != null) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        OrderModel model = snapshot.getValue(OrderModel.class);
//                        if (model != null) {
//                            if (model.getOrderStatus().equalsIgnoreCase(orderStatus)) {
//                                if(model.getOrderFor()!=null){
//                                    if(model.getOrderFor().equalsIgnoreCase("seller")){
//                                        arrayList.add(model);
//                                    }
//                                }
//
//
//                            }
//                        }
//                    }
//                    Collections.sort(arrayList, new Comparator<OrderModel>() {
//                        @Override
//                        public int compare(OrderModel listData, OrderModel t1) {
//                            Long ob1 = listData.getTime();
//                            Long ob2 = t1.getTime();
//
//                            return ob2.compareTo(ob1);
//
//                        }
//                    });
//                    adapter.notifyDataSetChanged();
//                    progress.setVisibility(View.GONE);
//                } else {
////                    CommonUtils.showToast("Nothing to show");
//                    progress.setVisibility(View.GONE);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void getOrdersFromDB(String key) {
        mDatabase.child("Orders").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OrderModel model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {
                        if (model.getOrderStatus().equalsIgnoreCase(orderStatus)) {
                            arrayList.add(model);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }


}