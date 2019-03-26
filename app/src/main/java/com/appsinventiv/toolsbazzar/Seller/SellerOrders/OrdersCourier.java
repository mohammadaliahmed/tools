package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.SimpleFragmentPagerAdapter;

import java.util.ArrayList;

public class OrdersCourier extends AppCompatActivity {
    ArrayList<String> orderStatusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_courier);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Courier");
        ViewPager viewPager = findViewById(R.id.viewpager);
        orderStatusList.add("Shipped");
        orderStatusList.add("Delivered");
        orderStatusList.add("Refused");
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, orderStatusList, getSupportFragmentManager(),"courier");
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
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
