package com.appsinventiv.toolsbazzar.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.appsinventiv.toolsbazzar.Adapters.LanguageListAdapter;
import com.appsinventiv.toolsbazzar.Adapters.OrdersAdapter;
import com.appsinventiv.toolsbazzar.Models.LanguageModel;
import com.appsinventiv.toolsbazzar.Models.OrderModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerScreen;
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

public class ChooseLanguage extends AppCompatActivity {

    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    ArrayList<LanguageModel> arrayList = new ArrayList<>();
    LanguageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        this.setTitle("Choose language");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new LanguageListAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        adapter.setListener(new LanguageListAdapter.Listener() {
            @Override
            public void countryChosen(LanguageModel model) {
                Welcome.language = model.getLanguageName();
                SellerScreen.language = model.getLanguageName();
                finish();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();


        getLanguages();
    }

    private void getLanguages() {
        mDatabase.child("Settings").child("Languages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String lan = snapshot.getValue(String.class);

                        arrayList.add(new LanguageModel(lan));
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
    public void onBackPressed() {

        finish();
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

