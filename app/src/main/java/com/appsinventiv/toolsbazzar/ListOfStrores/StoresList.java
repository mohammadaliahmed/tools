package com.appsinventiv.toolsbazzar.ListOfStrores;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.appsinventiv.toolsbazzar.Adapters.SellerFragmentAdapter;
import com.appsinventiv.toolsbazzar.R;

import java.util.ArrayList;

public class StoresList extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        this.setTitle("Stores");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        ViewPager viewPager = findViewById(R.id.viewpager1);
        ArrayList<String> categoryList = new ArrayList<>();

        categoryList.add("Recommended");
        categoryList.add("Following");


        final StoreListFragmentAdapter adapter = new StoreListFragmentAdapter(this, getSupportFragmentManager(), categoryList );
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);


        TabLayout tabLayout = findViewById(R.id.sliding_tabs1);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.setupWithViewPager(viewPager);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
