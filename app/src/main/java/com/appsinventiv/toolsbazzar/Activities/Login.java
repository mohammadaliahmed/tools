package com.appsinventiv.toolsbazzar.Activities;

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

import com.appsinventiv.toolsbazzar.Models.Customer;
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

public class Login extends AppCompatActivity {

    DatabaseReference mDatabase;
    EditText e_username, e_password;
    private PrefManager prefManager;
    ArrayList<String> userlist = new ArrayList<String>();
    String username, password;
    Button login;
    TextView register, back;
    String productId;
    String takeUserToActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        e_username = findViewById(R.id.username);
        e_password = findViewById(R.id.password);
        back = findViewById(R.id.back);

        Intent i = getIntent();
        takeUserToActivity = i.getStringExtra("takeUserToActivity");
        productId = i.getStringExtra("productId");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Customers");
        mDatabase.addChildEventListener(new ChildEventListener() {
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
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
                finish();

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommonUtils.isNetworkConnected()) {
                    userLogin();
                }else{
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
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            Customer user = dataSnapshot.child("" + username).getValue(Customer.class);
                            if (user != null) {
                                if (user.getPassword().equals(password)) {
                                    SharedPrefs.setUsername(user.getUsername());
                                    SharedPrefs.setName(user.getName());
                                    SharedPrefs.setCity(user.getCity());
                                    SharedPrefs.setIsLoggedIn("yes");
                                    SharedPrefs.setCustomerType(user.getCustomerType());
                                    launchHomeScreen();
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

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Login.this, MainActivity.class));
        prefManager.setIsFirstTimeLaunchWelcome(false);

//        if(takeUserToActivity.equalsIgnoreCase(Constants.HOME_ACTIVITY)){
//            startActivity(new Intent(Login.this, ListOfProducts.class));
//
//        }else if(takeUserToActivity.equalsIgnoreCase(Constants.CART_ACTIVITY)){
//            startActivity(new Intent(Login.this, Cart.class));
//        }
//        else if(takeUserToActivity.equalsIgnoreCase(Constants.MY_ORDERS_ACTIVITY)){
//            startActivity(new Intent(Login.this, MyOrders.class));
//        }else if(takeUserToActivity.equalsIgnoreCase(Constants.PRODUCT_DETAIL_ACTIVITY)){
//            Intent i=new Intent(Login.this,ProductDescription.class);
//            i.putExtra("productId",productId);
//            startActivity(i);
//
//        }

        finish();
    }
}
