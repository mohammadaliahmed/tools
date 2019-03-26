package com.appsinventiv.toolsbazzar.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.appsinventiv.toolsbazzar.Adapters.AddressChooseAdapter;
import com.appsinventiv.toolsbazzar.Models.CityDeliveryChargesModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseAddress extends AppCompatActivity {

    String country, province, district, city;
    DatabaseReference mDatabase;
    RecyclerView recyclerview;
    ArrayList<String> itemList = new ArrayList<>();
    AddressChooseAdapter adapter;
    String key = "country";
    ProgressBar progress;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }


        progress = findViewById(R.id.progress);
        recyclerview = findViewById(R.id.recyclerview);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new AddressChooseAdapter(this, itemList, key, new AddressChooseAdapter.WhichOption() {
            @Override
            public void option(String val, String value) {
                count++;
                progress.setVisibility(View.VISIBLE);
                ChooseAddress.this.setTitle(value);
                if (count == 1) {
                    country = value;
                    getProvincesFromDb(value);
                } else if (count == 2) {
                    getDistrictsFromDB(value);
                } else if (count == 3) {
                    province = value;
                    getCitiesFromDB(value);
                } else if (count == 4) {
                    city = value;
                    setUserData(value);
                }
            }
        });
        recyclerview.setAdapter(adapter);

        getCountriesFromDb();


    }

    private void setUserData(String value) {
        itemList.clear();
        mDatabase.child("Settings").child("Locations").child("Cities").child(province).child(value).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    CityDeliveryChargesModel model = dataSnapshot.getValue(CityDeliveryChargesModel.class);
                    if (model != null) {
                        SharedPrefs.setHalfKgRate(model.getHalfKg());
                        SharedPrefs.setOneKgRate(model.getOneKg());
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("country", country);
                        returnIntent.putExtra("city", city);
                        returnIntent.putExtra("province", province);
                        returnIntent.putExtra("locationId", "sdfsfsd");
                        returnIntent.putExtra("locationPosition", "dsf");

                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCitiesFromDB(String value) {
        mDatabase.child("Settings").child("Locations").child("Cities").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CityDeliveryChargesModel model = snapshot.getValue(CityDeliveryChargesModel.class);
                        if (model != null) {
                            itemList.add(model.getCityName());
                        }

                    }
                    progress.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDistrictsFromDB(String value) {
        mDatabase.child("Settings").child("Locations").child("Districts").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getValue(String.class);
                        itemList.add(key);
                    }
                    progress.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getProvincesFromDb(String value) {
        mDatabase.child("Settings").child("Locations").child("Countries").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    SharedPrefs.setCurrencySymbol(String.format("%.2f", dataSnapshot.child("currencyRate").getValue(Float.class)));
                    SharedPrefs.setCurrencySymbol(dataSnapshot.child("currencySymbol").getValue(String.class));
                    for (DataSnapshot snapshot : dataSnapshot.child("provinces").getChildren()) {
                        String key = snapshot.getValue(String.class);
                        itemList.add(key);
                    }
                    progress.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getCountriesFromDb() {
        mDatabase.child("Settings").child("Locations").child("Countries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        itemList.add(key);
                    }
                    key = "province";
                    progress.setVisibility(View.GONE);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
