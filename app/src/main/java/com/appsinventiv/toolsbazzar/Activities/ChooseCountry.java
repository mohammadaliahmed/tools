package com.appsinventiv.toolsbazzar.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Adapters.ListOfCountriesAdapter;
import com.appsinventiv.toolsbazzar.CountryCountry;
import com.appsinventiv.toolsbazzar.Models.AboutUsModel;
import com.appsinventiv.toolsbazzar.Models.CountryModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerChat.SellerChats;
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

public class ChooseCountry extends AppCompatActivity {
    ListOfCountriesAdapter adapter;
    RecyclerView recyclerView;
    DatabaseReference mDatabase;
    ArrayList<CountryModel> countriesList = new ArrayList<>();
    public static Activity activity;
    int from;
    Button cliclk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_country);
        this.setTitle("Choose country");
        activity = this;
        from = getIntent().getIntExtra("from", 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        recyclerView = findViewById(R.id.recyclerView);
        cliclk = findViewById(R.id.cliclk);

        mDatabase = FirebaseDatabase.getInstance().getReference();

//        cliclk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ArrayList<CountryCountry> item=new ArrayList<>();
//                for(CountryModel model:countriesList){
//                    item.add(new CountryCountry(model.getCountryName(),model.getImageUrl()));
//                }
//                mDatabase.child("newCountry").setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//                    }
//                });
//            }
//        });

        adapter = new ListOfCountriesAdapter(this, countriesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        if (from == 1) {
            adapter.setListener(true, new ListOfCountriesAdapter.Listener() {
                @Override
                public void countryObject(CountryModel model) {
                    Welcome.country = model.getCountryName();
                    SellerChats.country = model.getCountryName();
                    SharedPrefs.setCountryModel(model);
                    finish();
                }
            });
        }


        getCountriesFromDB();

    }

    private void getCountriesFromDB() {
        mDatabase.child("Settings").child("Locations").child("Countries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            CountryModel country = snapshot1.getValue(CountryModel.class);
                            if (country != null) {
                                countriesList.add(country);

                            }
                        }
                    }
                    Collections.sort(countriesList, new Comparator<CountryModel>() {
                        @Override
                        public int compare(CountryModel listData, CountryModel t1) {
                            String ob1 = listData.getCountryName();
                            String ob2 = t1.getCountryName();

                            return ob1.compareTo(ob2);

                        }
                    });
                    adapter.updateList(countriesList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.only_search_menu, menu);
        final MenuItem mSearch = menu.findItem(R.id.action_search);
//        mSearch.expandActionView();
        SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.filter(newText);
//                    getUserCartProductsFromDB();

                return false;
            }
        });
        return true;
    }


}
