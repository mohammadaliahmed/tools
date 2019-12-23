package com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement.AssignAttributeOptionAdapter;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssignAttributes extends AppCompatActivity {
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    AssignAttributeOptionAdapter adapter;
    ArrayList<SubAttributeModel> itemList = new ArrayList<>();
    HashMap<String, SubAttributeModel> checkMap = new HashMap<>();

    String type, category;
    Button assign;


//    ArrayList<SubAttributeModel> attributeModelArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        setContentView(R.layout.activity_assign_attributes);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        type = getIntent().getStringExtra("type");
        category = getIntent().getStringExtra("category");
        setTitle(type);


        recyclerView = findViewById(R.id.recyclerView);
        assign = findViewById(R.id.assign);

        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkMap.size() > 0) {
                    showAlert();
                } else {
                    CommonUtils.showToast("Select some attributes");
                }

            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new AssignAttributeOptionAdapter(this, itemList, new AssignAttributeOptionAdapter.ChooseAssignOptionCallback() {
            @Override
            public void onOptionSelected(SubAttributeModel value, boolean checked, int position) {
                if (checked) {
                    checkMap.put(value.getMainCategory(), value);
                } else {
                    checkMap.remove(value.getMainCategory());
                }
            }
        });

        recyclerView.setAdapter(adapter);


        getAttributesList();
        getCheckedAttributesList();

    }

    private void getCheckedAttributesList() {
        mDatabase.child("Settings/Attributes").child("AssignedAttributes").child(category).child(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SubAttributeModel val = snapshot.getValue(SubAttributeModel.class);
                        if (val != null) {
                            checkMap.put(val.getMainCategory(), val);
                        }
                    }
                    ArrayList<SubAttributeModel> abc = new ArrayList<>();

                    for (Map.Entry<String, SubAttributeModel> entry : checkMap.entrySet()) {
                        abc.add(entry.getValue());
                    }
                    adapter.setSelectedList(abc);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Assign " + type + " ?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Settings/Attributes").child("AssignedAttributes").child(category).child(type).setValue(checkMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Assigned");
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void getAttributesList() {
        mDatabase.child("Settings").child("Attributes").child("SubAttributes").child(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SubAttributeModel model = snapshot.getValue(SubAttributeModel.class);
                        if (model != null) {
                            itemList.add(model);
                        }
                    }
                    adapter.updateList(itemList);
                    adapter.notifyDataSetChanged();
//                    if (attributeModelArrayList.size() > 0) {
//                        getSubSubAttributesFromDB(attributeModelArrayList.get(count));
//                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void getSubSubAttributesFromDB(final SubAttributeModel subAttributeModel) {
//        this.setTitle(subAttributeModel.getMainCategory());
//        itemList.clear();
//
//        if (subAttributeModel.getSelection().equalsIgnoreCase("userInput")) {
//
//            userInput.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//
//        } else {
//            userInput.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//            wholeLayout.setVisibility(View.VISIBLE);
//            mDatabase.child("Settings/Attributes/SubSubAttributes")
//                    .child(subAttributeModel.getMainCategory()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.getValue() != null) {
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            String item = snapshot.getValue(String.class);
//                            if (item != null) {
//                                itemList.add(item);
//
//                            }
//                        }
//                        if (subAttributeModel.getSelection().equalsIgnoreCase("single")) {
//                            adapter.setMultiSelect(false);
//                        } else if (subAttributeModel.getSelection().equalsIgnoreCase("multiple")) {
//                            adapter.setMultiSelect(true);
//                        }
//                        adapter.setSelected(-1);
//                        adapter.updateList(itemList);
//                        adapter.notifyDataSetChanged();
//                        wholeLayout.setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.only_search_menu, menu);
        final MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.filter(newText);

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
