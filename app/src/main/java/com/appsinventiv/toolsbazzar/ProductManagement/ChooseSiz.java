package com.appsinventiv.toolsbazzar.ProductManagement;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;


import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChooseSiz extends AppCompatActivity {
    ChooseSiz activity;
    ArrayList<String> sizeList = new ArrayList<>();
    ArrayList<String> newSizeList = new ArrayList<>();
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    ChooseSizeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_siz);
        activity = this;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Choose Size");
        getSizeFromDB();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new ChooseSizeAdapter(this, sizeList, new ChooseSizeAdapter.ChooseSizeOptionCallback() {
            @Override
            public void onOptionSelected(String value) {
                getSubSizesFromDB(value);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    private void getSubSizesFromDB(String value) {
        mDatabase.child("Settings").child("Attributes").child("SubSubAttributes").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String abc = snapshot.getValue(String.class);
                        newSizeList.add(abc);
                    }
                    showSizeAlert();

                } else {
                    CommonUtils.showToast("No Data");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getSizeFromDB() {
        mDatabase.child("Settings").child("Attributes").child("SubSubAttributes").child("Sizes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String abc = snapshot.getValue(String.class);
                        sizeList.add(abc);
                    }
                    adapter.updateList(sizeList);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showSizeAlert() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose sizes");

// add a checkbox list
        final String[] animals = newSizeList.toArray(new String[0]);

        boolean[] checkedItems = new boolean[newSizeList.size()];
        final ArrayList<String> checked = new ArrayList<>();
        builder.setMultiChoiceItems(animals, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box
                if (isChecked) {
                    checked.add(animals[which]);
                } else {
                    checked.remove(animals[which]);
                }
                ChooseProductVariation.hashMap.put("size", checked);
            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
                ChooseProductVariation.sizeSubtitle.setText("" + ChooseProductVariation.hashMap.get("size"));
                dialog.dismiss();
                finish();
//                if ( ChooseProductVariation.hashMap.get("size") != null &&  ChooseProductVariation.hashMap.get("size").size() > 0
//                        &&  ChooseProductVariation.hashMap.get("color") != null &&  ChooseProductVariation.hashMap.get("color").size() > 0) {
//                    addSKULayout();
//                }

            }
        });
        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
