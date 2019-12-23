package com.appsinventiv.toolsbazzar.Seller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.appsinventiv.toolsbazzar.Activities.LiveChat;
import com.appsinventiv.toolsbazzar.Activities.MainActivity;
import com.appsinventiv.toolsbazzar.Activities.WholesaleLiveChat;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerChat.SellerChats;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SupportCenter extends AppCompatActivity {
    Button back;

    RelativeLayout dialCall, liveChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_center);
        this.setTitle("Support center");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }


        back = findViewById(R.id.back);
        liveChat = findViewById(R.id.liveChat);
        dialCall = findViewById(R.id.dialCall);


        liveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getCustomerModel() != null) {
                    if (SharedPrefs.getCustomerModel().getCustomerType().equalsIgnoreCase("retail")) {
                        Intent i = new Intent(SupportCenter.this, LiveChat.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(SupportCenter.this, WholesaleLiveChat.class);
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(SupportCenter.this, SellerChats.class);
                    startActivity(i);
                }
            }
        });


        dialCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + SharedPrefs.getCompanyDetails().getTelephone()));
                startActivity(i);
            }
        });


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
