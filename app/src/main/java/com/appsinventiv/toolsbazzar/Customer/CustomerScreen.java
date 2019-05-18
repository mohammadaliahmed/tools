package com.appsinventiv.toolsbazzar.Customer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.MyOrders;
import com.appsinventiv.toolsbazzar.Activities.MyProfile;
import com.appsinventiv.toolsbazzar.Activities.RecentlyViewed.RecentViewedActivity;
import com.appsinventiv.toolsbazzar.ListOfStrores.StoresList;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;

public class CustomerScreen extends AppCompatActivity {
    ImageView back, settings;
    RelativeLayout recentViewed, faq, phone, reviews, address, wallet, stores, myOrders;
    TextView name;
    LinearLayout pending, processing, completed, shipped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_screen);

        stores = findViewById(R.id.stores);
        wallet = findViewById(R.id.wallet);
        address = findViewById(R.id.address);
        reviews = findViewById(R.id.reviews);
        phone = findViewById(R.id.phone);
        faq = findViewById(R.id.faq);
        recentViewed = findViewById(R.id.recentViewed);
        settings = findViewById(R.id.settings);
        name = findViewById(R.id.name);
        name.setText(SharedPrefs.getName());
        myOrders = findViewById(R.id.myOrders);
        back = findViewById(R.id.back);
        pending = findViewById(R.id.pending);
        processing = findViewById(R.id.processing);
        completed = findViewById(R.id.completed);
        shipped = findViewById(R.id.shipped);

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CustomerScreen.this, MyOrders.class);
                i.putExtra("orderStatus", "Pending");
                startActivity(i);
            }
        });
        processing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CustomerScreen.this, MyOrders.class);
                i.putExtra("orderStatus", "Under Process");
                startActivity(i);
            }
        });
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CustomerScreen.this, MyOrders.class);
                i.putExtra("orderStatus", "Completed");
                startActivity(i);
            }
        });
        shipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CustomerScreen.this, MyOrders.class);
                i.putExtra("orderStatus", "Shipped");
                startActivity(i);
            }
        });


        transparentToolbar();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerScreen.this, MyProfile.class));
            }
        });
        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerScreen.this, MyOrders.class));
            }
        });

        recentViewed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerScreen.this, RecentViewedActivity.class));
            }
        });


        stores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerScreen.this, StoresList.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void transparentToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
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
