package com.appsinventiv.toolsbazzar.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfile extends AppCompatActivity {

    EditText e_name, e_phone, e_address;
    Button update;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        this.setTitle("My Profile");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        e_name = findViewById(R.id.name);
        e_phone = findViewById(R.id.phone);
        e_address = findViewById(R.id.address);
        update = findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("address").setValue(e_address.getText().toString());
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("phone").setValue(e_phone.getText().toString());
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("name").setValue(e_name.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Updated");
                        finish();
                    }
                });


            }
        });
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    if (customer != null) {
                        e_name.setText(customer.getName());
                        e_address.setText(customer.getAddress());
                        e_phone.setText(customer.getPhone());

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
