package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.R;

public class TrackOrder extends AppCompatActivity {
    String orderId;
    String orderStatus;
    Button back;
    ImageView img;
    LinearLayout trackingInfo;
    TextView deliveredTo;
    private String deliveredToWhom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        back = findViewById(R.id.goBack);
        img = findViewById(R.id.img);
        deliveredTo = findViewById(R.id.deliveredTo);
        trackingInfo = findViewById(R.id.trackingInfo);


        Intent i = getIntent();
        orderId = i.getStringExtra("orderId");
        orderStatus = i.getStringExtra("orderStatus");
        deliveredToWhom = i.getStringExtra("deliveredToWhom");

        if (orderStatus.equalsIgnoreCase("pending")) {
            img.setImageResource(R.drawable.order_pending);
        }
        if (orderStatus.equalsIgnoreCase("under process")) {
            img.setImageResource(R.drawable.order_under_review);
        }
        if (orderStatus.equalsIgnoreCase("shipped")) {
            img.setImageResource(R.drawable.order_shipped);
        }
        if (orderStatus.equalsIgnoreCase("delivered")) {
            img.setImageResource(R.drawable.order_delivered);
            deliveredTo.setText("Delivered to: " + deliveredToWhom);
        }

        if (orderStatus.equalsIgnoreCase("Delivered By Courier")) {
            img.setImageResource(R.drawable.order_delivered);
            trackingInfo.setVisibility(View.VISIBLE);
        }
        if (orderStatus.equalsIgnoreCase("cancelled")) {
            img.setImageResource(R.drawable.order_canceled);
        }
        if (orderStatus.equalsIgnoreCase("Out Of Stock")) {
            img.setImageResource(R.drawable.order_out_of_stock);
        }


        this.setTitle("Tracking Order #: " + orderId);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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
