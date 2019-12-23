package com.appsinventiv.toolsbazzar.Activities.CustomerOrders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersAdapter;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.appsinventiv.toolsbazzar.Utils.SwipeControllerActions;
import com.appsinventiv.toolsbazzar.Utils.SwipeToDeleteCallback;
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


public class CustomerOrdersFragment extends Fragment {

    Context context;
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    com.appsinventiv.toolsbazzar.Adapters.OrdersAdapter adapter;
    ArrayList<OrderModel> orderModelArrayList = new ArrayList<>();
    ProgressBar progress;
    Button shopping;
    RelativeLayout wholeLayout;
    String orderStatus;


    public CustomerOrdersFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public CustomerOrdersFragment(String orderStatus) {
        this.orderStatus = orderStatus;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        progress = rootView.findViewById(R.id.progress);
        shopping = rootView.findViewById(R.id.shopping);
        wholeLayout = rootView.findViewById(R.id.wholeLayout);
        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i=new Intent(MyOrders.this)
//                finish();
            }
        });

//        this.setTitle((orderStatus != null ? orderStatus + " Orders" : "My Orders"));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = rootView.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new com.appsinventiv.toolsbazzar.Adapters.OrdersAdapter(context, orderModelArrayList, new com.appsinventiv.toolsbazzar.Adapters.OrdersAdapter.ChangeOrderStatus() {
            @Override
            public void onCancelOrder(final OrderModel product) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Cancel Order?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDatabase.child("Orders").child(product.getOrderId()).child("orderStatus").setValue("Cancelled").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        CommonUtils.showToast("Order Canceled");
                                        orderModelArrayList.clear();
                                        getOrders();
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
        });
        recyclerView.setAdapter(adapter);


        getOrders();
        return rootView;
    }

    private void getOrders() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    orderModelArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        getOrdersFromDb(snapshot.getKey());
//                        if (orderModelArrayList.size() > 0) {
//                            wholeLayout.setVisibility(View.GONE);
//                        } else {
//                            wholeLayout.setVisibility(View.VISIBLE);
//                        }
                    }

//                    adapter.notifyDataSetChanged();
                } else {
                    progress.setVisibility(View.GONE);
                    orderModelArrayList.clear();
                    if (orderModelArrayList.size() > 0) {
                        wholeLayout.setVisibility(View.GONE);
                    } else {
                        wholeLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getOrdersFromDb(String key) {
        mDatabase.child("Orders").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OrderModel model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {
                        wholeLayout.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        if (orderStatus.equalsIgnoreCase("all")) {
                            orderModelArrayList.add(model);
                        } else if (model.getOrderStatus().toLowerCase().contains(orderStatus.toLowerCase())) {
                            orderModelArrayList.add(model);
                        }


                        Collections.sort(orderModelArrayList, new Comparator<OrderModel>() {
                            @Override
                            public int compare(OrderModel listData, OrderModel t1) {
                                Long ob1 = listData.getTime();
                                Long ob2 = t1.getTime();

                                return ob2.compareTo(ob1);

                            }
                        });
                        adapter.notifyDataSetChanged();


                    }

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
