package com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerAddProduct;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChooseSKU extends AppCompatActivity {
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    ChooseAttributeOptionAdapter adapter;
    ArrayList<String> itemList = new ArrayList<>();

    TextView back, next, skip;
    String category;

    ArrayList<SubAttributeModel> attributeModelArrayList = new ArrayList<>();
    int count = 0;
    RelativeLayout wholeLayout;
    LinearLayout userInput;
    EditText manualAttribute;
    Button add;
    TextInputLayout aaa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        setContentView(R.layout.activity_choose_attributes);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        this.setTitle("Choose Attribute");
        add = findViewById(R.id.add);
        manualAttribute = findViewById(R.id.manualAttribute);
        back = findViewById(R.id.back);
        aaa = findViewById(R.id.aaa);
        userInput = findViewById(R.id.userInput);
        next = findViewById(R.id.next);
        skip = findViewById(R.id.skip);
        wholeLayout = findViewById(R.id.wholeLayout);
        category = SellerAddProduct.categoryList.get(SellerAddProduct.categoryList.size() - 1);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count < (attributeModelArrayList.size() - 1)) {
                    SellerAddProduct.productAttributesMap.remove(attributeModelArrayList.get(count).getMainCategory());
                    count++;
                    getSubSubAttributesFromDB(attributeModelArrayList.get(count));
                } else {
                    if (Constants.RE_ATTRIBUTES) {
                        ChooseAttributes.chooseAttributes.finish();

                        finish();
                    } else {
                        startActivity(new Intent(ChooseSKU.this, ChooseWarrenty.class));
                    }
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count < (attributeModelArrayList.size() - 1)) {
                    count++;
                    getSubSubAttributesFromDB(attributeModelArrayList.get(count));
                } else {
                    if (Constants.RE_ATTRIBUTES) {
                        ChooseAttributes.chooseAttributes.finish();
                        finish();
                    } else {
                        startActivity(new Intent(ChooseSKU.this, ChooseWarrenty.class));
                    }
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count > 0) {
                    count--;
                    getSubSubAttributesFromDB(attributeModelArrayList.get(count));
                } else {
                    finish();
                }

//                getSubSubAttributesFromDB(attributeModelArrayList.get(count));
            }
        });
        recyclerView = findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new ChooseAttributeOptionAdapter(this, itemList,"sku", new ChooseAttributeOptionAdapter.ChooseOptionCallback() {
            @Override
            public void onOptionSelected(String value) {
                SellerAddProduct.productAttributesMap.put(attributeModelArrayList.get(count).getMainCategory(), value);

            }
        });

        recyclerView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aaa.setHint(attributeModelArrayList.get(count).getMainCategory());
                SellerAddProduct.productAttributesMap.put(attributeModelArrayList.get(count).getMainCategory(),
                        manualAttribute.getText().toString());
                CommonUtils.showToast("Added\nGo to next");
            }
        });


        getAttributesList();

    }

    private void getAttributesList() {
        mDatabase.child("Settings").child("Attributes/AssignedAttributes").child(category).child("SKU").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SubAttributeModel model = snapshot.getValue(SubAttributeModel.class);
                        if (model != null) {
                            attributeModelArrayList.add(model);
                        }
                    }
                    if (attributeModelArrayList.size() > 0) {
                        getSubSubAttributesFromDB(attributeModelArrayList.get(count));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getSubSubAttributesFromDB(final SubAttributeModel subAttributeModel) {
        this.setTitle(subAttributeModel.getMainCategory());
        itemList.clear();

        if (subAttributeModel.getSelection().equalsIgnoreCase("userInput")) {

            userInput.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        } else {
            userInput.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            wholeLayout.setVisibility(View.VISIBLE);
            mDatabase.child("Settings/Attributes/SubSubAttributes")
                    .child(subAttributeModel.getMainCategory()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String item = snapshot.getValue(String.class);
                            if (item != null) {
                                itemList.add(item);

                            }
                        }
                        if (subAttributeModel.getSelection().equalsIgnoreCase("single")) {
                            adapter.setMultiSelect(false);
                        } else if (subAttributeModel.getSelection().equalsIgnoreCase("multiple")) {
                            adapter.setMultiSelect(true);
                        }
                        adapter.selectedText = "";
                        adapter.setSelected(-1);
                        Collections.sort(itemList, new Comparator<String>() {
                            @Override
                            public int compare(String listData, String t1) {


                                return listData.compareTo(t1);

                            }
                        });
                        adapter.updateList(itemList);
                        adapter.notifyDataSetChanged();
                        wholeLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


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
