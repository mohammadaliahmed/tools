package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.toolsbazzar.Models.ChatModel;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.LocationAndChargesModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.PrefManager;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Register extends AppCompatActivity {
    Button signup;
    TextView login;
    DatabaseReference mDatabase;
    private PrefManager prefManager;
    ArrayList<String> userslist = new ArrayList<String>();
    EditText e_fullname, e_username, e_email, e_password, e_phone, e_address, e_storeName, e_businessNumber, e_telPhone;
    String fullname, username, email, password, phone, address, businessNumber, storeName, telPhone = "";
    long time;
    TextView chooseLocation, createAccountText;
    String city = "", country = "", locationId = "";
    int locationPosition;
    TextInputLayout abc1, abc2;
    LocationAndChargesModel chargesModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("Customers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userslist.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        abc1 = findViewById(R.id.abc1);
        abc2 = findViewById(R.id.abc2);


        createAccountText = findViewById(R.id.createAccountText);
        e_fullname = findViewById(R.id.name);
        e_username = findViewById(R.id.username);
        e_email = findViewById(R.id.email);
        e_password = findViewById(R.id.password);
        e_phone = findViewById(R.id.phone);
        e_address = findViewById(R.id.address);
        e_storeName = findViewById(R.id.storeName);
        e_businessNumber = findViewById(R.id.businessRegNumber);
        e_telPhone = findViewById(R.id.telPhone);
        chooseLocation = findViewById(R.id.chooseLocation);


        if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            createAccountText.setText("Create Retail Account");
            e_businessNumber.setVisibility(View.GONE);
            e_storeName.setVisibility(View.GONE);
            abc1.setVisibility(View.GONE);
            abc2.setVisibility(View.GONE);

        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            createAccountText.setText("Create Wholesale Account");

        }


        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, ChooseLocation.class);
                startActivityForResult(i, 1);
            }
        });

        signup = findViewById(R.id.signup);
        login = findViewById(R.id.signin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (e_fullname.getText().toString().length() == 0) {
                    e_fullname.setError("Cannot be null");
                } else if (e_username.getText().toString().length() == 0) {
                    e_username.setError("Cannot be null");
                } else if (e_password.getText().toString().length() == 0) {
                    e_password.setError("Cannot be null");
                } else if (e_phone.getText().toString().length() == 0) {
                    e_phone.setError("Cannot be null");
                } else if (e_address.getText().toString().length() == 0) {
                    e_address.setError("Cannot be null");
                } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                    if (e_businessNumber.getText().toString().length() == 0) {
                        e_businessNumber.setError("Cannot be null");
                    }
                } else if (city.equalsIgnoreCase("")) {
                    CommonUtils.showToast("Please choose country and city");
                    Intent i = new Intent(Register.this, ChooseLocation.class);
                    startActivityForResult(i, 1);
                } else {
                    fullname = e_fullname.getText().toString();
                    username = e_username.getText().toString();
                    email = e_email.getText().toString();
                    password = e_password.getText().toString();
                    phone = e_phone.getText().toString();
                    telPhone = e_telPhone.getText().toString();
                    businessNumber = e_businessNumber.getText().toString();
                    storeName = e_storeName.getText().toString();
                    address = e_address.getText().toString();

                    if (userslist.contains("" + username)) {
                        Toast.makeText(Register.this, "Username is already taken\nPlease choose another", Toast.LENGTH_SHORT).show();
                    } else {
//                        int randomPIN = (int) (Math.random() * 900000) + 100000;
                        time = System.currentTimeMillis();
                        final String userId = "" + time;
                        ArrayList<String> ratedProducts = new ArrayList<>();
                        ArrayList<String> wishList = new ArrayList<>();
                        ArrayList<String> recentlyViewed = new ArrayList<>();
                        mDatabase.child("Customers")
                                .child(username)
                                .setValue(new Customer(
                                        username,
                                        fullname,
                                        username,
                                        email,
                                        password,
                                        "" + phone,
                                        "" + telPhone,
                                        address,
                                        city,
                                        country,
                                        SharedPrefs.getFcmKey(),
                                        time,
                                        SharedPrefs.getCustomerType(),
                                        storeName,
                                        "" + businessNumber,
                                        locationId,
                                        chargesModel.getCurrency()

                                ))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Register.this, "Thankyou for registering", Toast.LENGTH_SHORT).show();
                                        SharedPrefs.setUsername(username);
                                        SharedPrefs.setName(fullname);
                                        SharedPrefs.setCity(city);
                                        SharedPrefs.setIsLoggedIn("yes");
                                        startChatWithAdmin(username);
                                        SharedPrefs.setCurrencySymbol(chargesModel.getCurrency());
                                        SharedPrefs.setExchangeRate("" + chargesModel.getCurrencyRate());
                                        SharedPrefs.setLocationId(locationId);
                                        launchHomeScreen();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "There was some error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
            }
        });


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

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        prefManager.setIsFirstTimeLaunchWelcome(false);

        startActivity(new Intent(Register.this, MainActivity.class));

        finish();
    }

    private void startChatWithAdmin(String user) {
        final String key = mDatabase.push().getKey();
        mDatabase.child("Chats").child(user).child(key)
                .setValue(new ChatModel(key, "I just registered on your app", user
                        , System.currentTimeMillis(), "sent", user));
    }

}
