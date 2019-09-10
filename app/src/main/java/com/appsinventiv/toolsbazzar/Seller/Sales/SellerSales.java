package com.appsinventiv.toolsbazzar.Seller.Sales;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerMainActivity;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.Orders;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersFragmentAdapter;

import java.util.ArrayList;

public class SellerSales extends AppCompatActivity {
    ArrayList<String> orderStatusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_sales);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        this.setTitle("My Sales");
        ViewPager viewPager = findViewById(R.id.viewpager);
        orderStatusList.add("Completed Sales");
//        orderStatusList.add("Under Process");
//        orderStatusList.add("Cancelled");
        SalesFragmentAdapter adapter = new SalesFragmentAdapter(this, orderStatusList, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SellerSales.this, SellerMainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(SellerSales.this, SellerMainActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
