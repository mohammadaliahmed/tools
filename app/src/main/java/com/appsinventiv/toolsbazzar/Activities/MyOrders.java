package com.appsinventiv.toolsbazzar.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.appsinventiv.toolsbazzar.Adapters.OrdersAdapter;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
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
import java.util.Collections;
import java.util.Comparator;

public class MyOrders extends AppCompatActivity {

    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    OrdersAdapter adapter;
    ArrayList<OrderModel> orderModelArrayList = new ArrayList<>();
    ProgressBar progress;
    Button shopping;
    RelativeLayout wholeLayout;
    String orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        this.setTitle("My Orders");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        progress = findViewById(R.id.progress);
        shopping = findViewById(R.id.shopping);
        wholeLayout = findViewById(R.id.wholeLayout);
        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i=new Intent(MyOrders.this)
                finish();
            }
        });

        orderStatus = getIntent().getStringExtra("orderStatus");
        this.setTitle((orderStatus != null ? orderStatus + " Orders" : "My Orders"));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OrdersAdapter(this, orderModelArrayList, new OrdersAdapter.ChangeOrderStatus() {
            @Override
            public void onCancelOrder(final OrderModel product) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MyOrders.this);
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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getOrders();
    }

    private void getOrders() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    orderModelArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        getOrdersFromDb(snapshot.getKey());
                        if (orderModelArrayList.size() > 0) {
                            wholeLayout.setVisibility(View.GONE);
                        } else {
                            wholeLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    adapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {

        finish();
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
                        if (orderStatus == null) {
                            orderModelArrayList.add(model);
                        } else if (model.getOrderStatus().contains(orderStatus)) {
                            orderModelArrayList.add(model);
                        }


                        adapter.notifyDataSetChanged();
                        Collections.sort(orderModelArrayList, new Comparator<OrderModel>() {
                            @Override
                            public int compare(OrderModel listData, OrderModel t1) {
                                Long ob1 = listData.getTime();
                                Long ob2 = t1.getTime();

                                return ob2.compareTo(ob1);

                            }
                        });


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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}

