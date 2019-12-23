package com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerAddProduct;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class WarrentyPeriod extends AppCompatActivity {
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    ChooseOptionAdapter adapter;
    ArrayList<String> itemList = new ArrayList<>();

    TextView back, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        setContentView(R.layout.activity_choose_warrenty);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        this.setTitle("Choose Warranty");
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WarrentyPeriod.this,DangerousGoods.class));

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);

        itemList.add("None");
        itemList.add("1 Month");
        itemList.add("6 Months");
        itemList.add("1 year");
        itemList.add("2 year");
        itemList.add("3 year");
        itemList.add("4 year");
        itemList.add("Lifetime");

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new ChooseOptionAdapter(this, itemList,"warrentyPeriod", new ChooseOptionAdapter.ChooseOptionCallback() {
            @Override
            public void onOptionSelected(String value) {
                SellerAddProduct.warrantyPeriod=value;

            }
        });

        recyclerView.setAdapter(adapter);


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

    @Override
    public void onBackPressed() {
        finish();
    }


}
