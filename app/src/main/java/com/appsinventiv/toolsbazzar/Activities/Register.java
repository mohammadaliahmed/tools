package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.toolsbazzar.Models.CountryModel;
import com.appsinventiv.toolsbazzar.Models.Customer;
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
import java.util.HashMap;

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

    int locationPosition;
    RelativeLayout abc1, abc2;
    CountryModel chargesModel;
    public static String province = "", district = "", city = "", country = "", locationId = "";
    TextView terms;
    Customer customer;
    HashMap<String, Customer> emailMap = new HashMap<>();
    HashMap<String, Customer> usernameMap = new HashMap<>();
    HashMap<String, Customer> phoneMap = new HashMap<>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (country.equalsIgnoreCase("")) {
            chooseLocation.setText("Choose address");
        } else {
            chooseLocation.setText("Location: " + country + " > " + province + " > " + district + " > " + city);
            if (country.contains("lanka")) {
                getLocalShippingDetailsFromDB(country);
            } else {
                getShippingDetailsFromDB(country);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();


        getCustomersFromDB();
        terms = findViewById(R.id.terms);
        terms.setPaintFlags(terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, TermsAndConditions.class);
                startActivity(i);
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


        e_phone.setText(SharedPrefs.getCountryModel().getMobileCode());
        e_telPhone.setText(SharedPrefs.getCountryModel().getMobileCode());

        e_phone.setSelection(3);
        e_telPhone.setSelection(3);

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
                Intent i = new Intent(Register.this, ChooseCountry.class);
                startActivity(i);
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
                } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale") && e_businessNumber.getText().length() == 0) {

                    e_businessNumber.setError("Cannot be null");

                } else if (country.equalsIgnoreCase("")) {
                    Intent i = new Intent(Register.this, ChooseCountry.class);
                    startActivity(i);
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

                    if (phoneMap.containsKey(phone)) {
                        CommonUtils.showToast("We found an Existing Account under same phone number. Try to Sign in using your Password");

                    } else if (emailMap.containsKey(email)) {
                        CommonUtils.showToast("We found an Existing Account under same Email.Try to Sign in using your Password");
                    } else if (usernameMap.containsKey(username)) {
                        CommonUtils.showToast("Username is already taken\nPlease choose another");
                    } else {


                        int randomPIN = (int) (Math.random() * 900000) + 100000;
                        time = System.currentTimeMillis();
                        final String userId = "" + time;
                        ArrayList<String> ratedProducts = new ArrayList<>();
                        ArrayList<String> wishList = new ArrayList<>();
                        ArrayList<String> recentlyViewed = new ArrayList<>();
                        customer = new Customer(username,
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
                                chargesModel.getCurrencySymbol(),
                                chargesModel.getCurrencyRate(),
                                province,
                                district,
                                true,
                                randomPIN,
                                false);
                        mDatabase.child("Customers")
                                .child(username)
                                .setValue(customer)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Register.this, "Thankyou for registering", Toast.LENGTH_SHORT).show();
                                        SharedPrefs.setCustomerModel(customer);
                                        SharedPrefs.setUsername(username);
                                        SharedPrefs.setName(fullname);
                                        SharedPrefs.setCountry(country);
                                        SharedPrefs.setCity(city);
                                        SharedPrefs.setIsLoggedIn("yes");
                                        startChatWithAdmin(username);
                                        SharedPrefs.setCurrencySymbol(chargesModel.getCurrencySymbol());
                                        SharedPrefs.setExchangeRate("" + chargesModel.getCurrencyRate());
                                        SharedPrefs.setLocationId(locationId);
//                                        startActivity(new Intent(Register.this, CustomerVerficiation.class));

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

    private void getCustomersFromDB() {
        mDatabase.child("Customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Customer customer = snapshot.getValue(Customer.class);
                        if (customer != null && customer.getUsername() != null) {
                            emailMap.put(customer.getEmail(), customer);
                            usernameMap.put(customer.getUsername(), customer);
                            phoneMap.put(customer.getPhone(), customer);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getLocalShippingDetailsFromDB(String country) {
        mDatabase.child("Settings").child("Locations").child("Countries").child("local").child(country).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    chargesModel = dataSnapshot.getValue(CountryModel.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getShippingDetailsFromDB(String country) {
        mDatabase.child("Settings").child("Locations").child("Countries").child("international").child(country).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    chargesModel = dataSnapshot.getValue(CountryModel.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        prefManager.setIsFirstTimeLaunchWelcome(false);

        startActivity(new Intent(Register.this, MainActivity.class));

        finish();
    }

    private void startChatWithAdmin(String user) {
//        final String key = mDatabase.push().getKey();
//        mDatabase.child("Chats").child(user).child(key)
//                .setValue(new ChatModel(key, "I just registered on your app", user
//                        , System.currentTimeMillis(), "sent", user,SharedPrefs.getName()));
    }

}
