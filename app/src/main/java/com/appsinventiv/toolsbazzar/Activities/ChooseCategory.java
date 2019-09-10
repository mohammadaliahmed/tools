package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.appsinventiv.toolsbazzar.Adapters.CategoryAdapter;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.EditProduct;
import com.appsinventiv.toolsbazzar.Seller.SellerAddProduct;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
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
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new CategoryAdapter(this, itemList, new CategoryAdapter.GetNewData() {
            @Override
            public void whichCategory(String title) {
                getCategoryDataFromDB(title);
            }
        });

        recyclerView.setAdapter(adapter);


        parentCategory = getIntent().getStringExtra("parentCategory");

        if (parentCategory == null) {
            this.setTitle("Choose category");
//            getDataFromDB();
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
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String value = snapshot.getValue(String.class);
                        itemList.add(value);
                    }
                    adapter.updateList(itemList);
                    progress.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();
                } else {
                    if (SellerAddProduct.fromWhere == 1) {
//                        CommonUtils.showToast("sdfsdf");
                        ChooseMainCategory.activity.finish();
                        finish();
                    } else {
                        Intent i = new Intent(ChooseCategory.this, ListOfProducts.class);
                        i.putExtra("parentCategory", parentCategory);
                        startActivity(i);
                        finish();
//                        CommonUtils.showToast("jgjhgj");
                    }
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
                } else {

                    ChooseMainCategory.activity.finish();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        try {
            if (SellerAddProduct.categoryList.size() > 0 || EditProduct.categoryList.size() > 0) {
                SellerAddProduct.categoryList.remove(SellerAddProduct.categoryList.size() - 1);
                EditProduct.categoryList.remove(EditProduct.categoryList.size() - 1);
                if (SellerAddProduct.fromWhere == 1) {

                    getCategoryDataFromDB(SellerAddProduct.categoryList.get(SellerAddProduct.categoryList.size() - 1));
                } else if (EditProduct.fromWhere == 1) {
                    getCategoryDataFromDB(EditProduct.categoryList.get(EditProduct.categoryList.size() - 1));
                }

            } else {
                finish();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            finish();
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            try {


                if (SellerAddProduct.categoryList.size() > 0 || EditProduct.categoryList.size() > 0) {
                    SellerAddProduct.categoryList.remove(SellerAddProduct.categoryList.size() - 1);
                    EditProduct.categoryList.remove(EditProduct.categoryList.size() - 1);
                    if (SellerAddProduct.fromWhere == 1) {
                        getCategoryDataFromDB(SellerAddProduct.categoryList.get(SellerAddProduct.categoryList.size() - 1));
                    } else if (EditProduct.fromWhere == 1) {
                        getCategoryDataFromDB(EditProduct.categoryList.get(EditProduct.categoryList.size() - 1));
                    }

                } else {
                    finish();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                finish();
            }
//            super.onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }
}
