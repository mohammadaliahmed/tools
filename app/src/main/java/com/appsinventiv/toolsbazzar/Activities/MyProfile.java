package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.LocationAndChargesModel;
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

    EditText e_name, e_phone, e_address, e_username, e_password;
    Button update;
    DatabaseReference mDatabase;
    LocationAndChargesModel chargesModel;
    TextView chooseLocation, createAccountText;
    String city = "", country = "", locationId = "";
    int locationPosition;
    TextView nameof;

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
        e_password = findViewById(R.id.password);
        e_username = findViewById(R.id.username);
        chooseLocation = findViewById(R.id.chooseLocation);
        nameof = findViewById(R.id.nameof);


        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyProfile.this, ChooseLocation.class);
                startActivityForResult(i, 1);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("address").setValue(e_address.getText().toString());
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("phone").setValue(e_phone.getText().toString());
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("name").setValue(e_name.getText().toString());
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("password").setValue(e_password.getText().toString());
                if (!city.equalsIgnoreCase("")) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("city").setValue(city);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("country").setValue(country);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("locationId").setValue(chargesModel.getId());
                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("currencySymbol").setValue(chargesModel.getCurrency());
                    SharedPrefs.setExchangeRate("" + chargesModel.getCurrencyRate());
                    SharedPrefs.setLocationId("" + locationId);
                    SharedPrefs.setCurrencySymbol("" + chargesModel.getCurrency());

                    CommonUtils.showToast("Profile updated");
                    finish();

                } else {
                    CommonUtils.showToast("Profile updated");
                    finish();
                }

            }
        });
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    if (customer != null) {
                        e_name.setText(customer.getName());
                        e_address.setText(customer.getAddress());
                        e_phone.setText(customer.getPhone());
                        e_username.setText(customer.getUsername());
                        e_password.setText(customer.getPassword());
                        nameof.setText(customer.getName() + "\n" + customer.getCustomerType());
                        chooseLocation.setText(customer.getCountry() + " > " + customer.getCity());

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {
                Bundle extras;
                extras = data.getExtras();
                if (extras.getString("city") != null) {
                    city = extras.getString("city");
                    country = extras.getString("country");
                    locationId = extras.getString("locationId");
                    locationPosition = extras.getInt("locationPosition");
                    chooseLocation.setText("Location: " + extras.getString("country") + " > " + extras.get("city"));
                    getListOfCountriesFromDb(locationId);

                }
            }
        }
    }

    private void getListOfCountriesFromDb(String id) {
        mDatabase.child("Settings").child("DeliveryCharges").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    chargesModel = dataSnapshot.getValue(LocationAndChargesModel.class);
//                    CommonUtils.showToast(chargesModel.getCurrency());


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
