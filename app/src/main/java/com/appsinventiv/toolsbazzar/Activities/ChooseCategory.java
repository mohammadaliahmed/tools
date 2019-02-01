package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.appsinventiv.toolsbazzar.Adapters.CategoryAdapter;
import com.appsinventiv.toolsbazzar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseCategory extends AppCompatActivity {
    DatabaseReference mDatabase;
    CategoryAdapter adapter;
    RecyclerView recyclerView;
    String parentCategory;
    ArrayList<String> itemList = new ArrayList<>();
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new CategoryAdapter(this, itemList);

        recyclerView.setAdapter(adapter);


        parentCategory = getIntent().getStringExtra("parentCategory");

        if (parentCategory == null) {
            this.setTitle("Choose category");
            getDataFromDB();
        } else {
            this.setTitle("" + parentCategory);
            getCategoryDataFromDB(parentCategory);
        }


    }

    private void getCategoryDataFromDB(String cat) {
        progress.setVisibility(View.VISIBLE);
        mDatabase.child("Settings").child("Categories").child(cat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String value = snapshot.getValue(String.class);
                        itemList.add(value);
                    }
                    progress.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();
                } else {
                    Intent i = new Intent(ChooseCategory.this, ListOfProducts.class);
                    i.putExtra("parentCategory", parentCategory);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromDB() {
        progress.setVisibility(View.VISIBLE);
        mDatabase.child("Settings").child("Categories").child("MainCategory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String value = snapshot.getValue(String.class);
                        itemList.add(value);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
