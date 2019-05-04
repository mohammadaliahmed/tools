package com.appsinventiv.toolsbazzar.Seller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.CustomerVerficiation;
import com.appsinventiv.toolsbazzar.Activities.Login;
import com.appsinventiv.toolsbazzar.Activities.MainActivity;
import com.appsinventiv.toolsbazzar.Activities.Register;
import com.appsinventiv.toolsbazzar.Models.CityDeliveryChargesModel;
import com.appsinventiv.toolsbazzar.Models.CountryModel;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.PrefManager;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerLogin extends AppCompatActivity {

    DatabaseReference mDatabase;
    EditText e_username, e_password;
    private PrefManager prefManager;
    ArrayList<String> userlist = new ArrayList<String>();
    String username, password;
    Button login;
    TextView register, back;

    //    String productId;
//    String takeUserToActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        e_username = findViewById(R.id.username);
        e_password = findViewById(R.id.password);
        back = findViewById(R.id.back);

        Intent i = getIntent();
//        takeUserToActivity = i.getStringExtra("takeUserToActivity");
//        productId = i.getStringExtra("productId");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Sellers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userlist.add(dataSnapshot.getKey());
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

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunchSeller()) {
            launchHomeScreen();
            finish();
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerLogin.this, SellerRegister.class);
                startActivity(i);
                finish();

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isNetworkConnected()) {
                    userLogin();
                } else {
                    CommonUtils.showToast("No internet");
                }
            }
        });
    }

    private void userLogin() {

        if (e_username.getText().toString().length() == 0) {
            e_username.setError("Please enter username");
        } else if (e_password.getText().toString().length() == 0) {
            e_password.setError("Please enter your password");
        } else {
            username = e_username.getText().toString();
            password = e_password.getText().toString();
            if (userlist.contains(username)) {
                mDatabase.child("Sellers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            VendorModel user = dataSnapshot.child("" + username).getValue(VendorModel.class);
                            if (user != null) {
                                if (user.getPassword().equals(password)) {
                                    SharedPrefs.setVendorModel(user);
                                    SharedPrefs.setUsername(user.getUsername());
                                    SharedPrefs.setName(user.getVendorName());
                                    SharedPrefs.setCity(user.getCity());
                                    SharedPrefs.setIsLoggedIn("yes");
                                    SharedPrefs.setCustomerType(user.getCustomerType());
                                    SharedPrefs.setCurrencySymbol(user.getCurrencySymbol());
                                    SharedPrefs.setExchangeRate(user.getCurrencyRate() + "");
                                    SharedPrefs.setLocationId(user.getLocationId());
                                    SharedPrefs.setCountry(user.getCountry());
                                    setUserData(user.getProvince(), user.getCity());
                                    SharedPrefs.setUserType("sell");
                                    launchHomeScreen();
//                                    if (user.isCodeVerified()) {
//
//
//                                        launchHomeScreen();
//
//                                    } else {
//                                        startActivity(new Intent(SellerLogin.this, SellerVerficiation.class));
//
//                                    }


                                } else {
                                    CommonUtils.showToast("Wrong password\nPlease try again");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                CommonUtils.showToast("Username does not exist\nPlease Sign up");

            }
        }

    }

    private void setUserData(String province, String city) {
//        itemList.clear();
        mDatabase.child("Settings").child("Locations").child("Cities").child(province).child(city).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    CityDeliveryChargesModel model = dataSnapshot.getValue(CityDeliveryChargesModel.class);
                    if (model != null) {
                        SharedPrefs.setHalfKgRate(model.getHalfKg());
                        SharedPrefs.setOneKgRate(model.getOneKg());
//                        launchHomeScreen();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void launchHomeScreen() {
        prefManager.setIsFirstTimeLaunchWelcome(false);
        prefManager.setFirstTimeLaunchSeller(false);
        startActivity(new Intent(SellerLogin.this, SellerMainActivity.class));


        finish();
    }
}
