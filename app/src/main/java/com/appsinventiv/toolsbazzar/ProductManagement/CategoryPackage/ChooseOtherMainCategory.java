package com.appsinventiv.toolsbazzar.ProductManagement.CategoryPackage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.MainCategoryModel;

import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.EditProduct;
import com.appsinventiv.toolsbazzar.Seller.SellerAddProduct;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.Constants;
import com.appsinventiv.toolsbazzar.Utils.SwipeControllerActions;
import com.appsinventiv.toolsbazzar.Utils.SwipeToDeleteCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseOtherMainCategory extends AppCompatActivity {

    RecyclerView recycler;
    DatabaseReference mDatabase;
    ArrayList<MainCategoryModel> itemList = new ArrayList<>();
    OtherMainCategoryAdapter adapter;
    public static Activity activity;
    String mainCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_main_category);
        activity = this;
        this.setTitle("Choose Categories");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mainCategory = getIntent().getStringExtra("mainCategory");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recycler = findViewById(R.id.recycler);


        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new OtherMainCategoryAdapter(this, itemList, 0, new OtherMainCategoryAdapter.MainCategoryCallBacks() {
            @Override
            public void deleteCategory(MainCategoryModel model) {
//                showAlert(model);
            }
        });


        recycler.setAdapter(adapter);
        getMainCategoriesFromDB();


    }

    private void showAlert(final MainCategoryModel model) {


        final Dialog dialog = new Dialog(ChooseOtherMainCategory.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_curved, null);

        dialog.setContentView(layout);

        TextView message = layout.findViewById(R.id.message);
        TextView no = layout.findViewById(R.id.no);
        TextView yes = layout.findViewById(R.id.yes);

        message.setText("Do you want to delete this category?");

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                deleteCategory(model);
            }
        });


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();


    }

    private void deleteCategory(MainCategoryModel model) {
        mDatabase.child("Settings/Categories/OtherMainCategories/" + mainCategory).child(model.getMainCategory()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Category Deleted");
            }
        });
    }

    private void getMainCategoriesFromDB() {

        mDatabase.child("Settings/Categories/OtherMainCategories/" + mainCategory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MainCategoryModel model = snapshot.getValue(MainCategoryModel.class);
                        if (model != null) {
                            itemList.add(model);
                        }
                    }
                    adapter.updateList(itemList);
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
            Constants.ADDING_PRODUCT_BACK = true;

            SellerAddProduct.categoryList.clear();
            EditProduct.categoryList.clear();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SellerAddProduct.categoryList.clear();
        EditProduct.categoryList.clear();
        Constants.ADDING_PRODUCT_BACK = true;

        finish();
    }

}
