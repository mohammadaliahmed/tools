package com.appsinventiv.toolsbazzar.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.CityDeliveryChargesModel;
import com.appsinventiv.toolsbazzar.Models.CountryModel;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.LocationAndChargesModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.CompressImage;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 23;
    EditText e_name, e_phone, e_address, e_username, e_password, secondAddress;
    Button update;
    DatabaseReference mDatabase;
    TextView chooseLocation;
    int locationPosition;
    TextView textName, textPhone;
    CountryModel chargesModel;
    public static String province = "", district = "", city = "", country = "", locationId = "";
    public static boolean countryType;
    CityDeliveryChargesModel perKgRates;
    StorageReference mStorageRef;
    private List<Uri> mSelected = new ArrayList<>();
    private ArrayList<String> imageUrl = new ArrayList<>();
    CircleImageView userImage;
    ImageView picPicture;

    @Override
    protected void onResume() {
        super.onResume();
        if (country.equalsIgnoreCase("")) {
            chooseLocation.setText("Choose address");
        } else {
            chooseLocation.setText("Location: " + country + " > " + province + " > " + district + " > " + city);
            getShippingDetailsFromDB(country);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        this.setTitle("My account settings");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        getPermissions();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        secondAddress = findViewById(R.id.secondAddress);
        e_name = findViewById(R.id.name);
        e_phone = findViewById(R.id.phone);
        e_address = findViewById(R.id.address);
        update = findViewById(R.id.update);
        e_password = findViewById(R.id.password);
        e_username = findViewById(R.id.username);
        chooseLocation = findViewById(R.id.chooseLocation);
        textName = findViewById(R.id.textName);
        textPhone = findViewById(R.id.textPhone);
        picPicture = findViewById(R.id.picPicture);
        userImage = findViewById(R.id.userImage);




        picPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelected.clear();
                imageUrl.clear();
                initMatise();
            }
        });


        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyProfile.this, ChooseCountry.class);
                startActivityForResult(i, 1);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SharedPrefs.setExchangeRate("" + chargesModel.getCurrencyRate());
//                SharedPrefs.setLocationId("" + locationId);
//                SharedPrefs.setCurrencySymbol("" + chargesModel.getCurrencySymbol());
//                SharedPrefs.setCountry("" + country);

                HashMap<String, Object> map = new HashMap<>();
                map.put("address", e_address.getText().toString());
                map.put("phone", e_phone.getText().toString());
                map.put("name", e_name.getText().toString());
                map.put("password", e_password.getText().toString());
                map.put("secondAddress", secondAddress.getText().toString());
                if (!city.equalsIgnoreCase("")) {
                    map.put("city", city);
                    map.put("country", country);
                    map.put("province", province);
                    map.put("district", district);
                    map.put("oneKg", (perKgRates.getOneKg() == null ? 1 : perKgRates.getOneKg()));
                    map.put("halfKg", (perKgRates.getHalfKg() == null ? 1 : perKgRates.getHalfKg()));
                    map.put("currencySymbol", chargesModel.getCurrencySymbol());
                    map.put("currencyRate", chargesModel.getCurrencyRate());

                }
                mDatabase.child("Customers").child(SharedPrefs.getUsername()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Profile updated");
                        if(mSelected.size()>0){
                            putPictures(imageUrl.get(0));
                        }

                    }
                });
//                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("address").setValue(e_address.getText().toString());
//                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("phone").setValue(e_phone.getText().toString());
//                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("name").setValue(e_name.getText().toString());
//                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("password").setValue(e_password.getText().toString());
//                mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("secondAddress").setValue(secondAddress.getText().toString());
//                if (!city.equalsIgnoreCase("")) {
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("city").setValue(city);
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("country").setValue(country);
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("province").setValue(province);
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("district").setValue(district);
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("oneKg").setValue((perKgRates.getOneKg() == null ? 1 : perKgRates.getOneKg()));
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("halfKg").setValue((perKgRates.getHalfKg() == null ? 1 : perKgRates.getHalfKg()));
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("currencySymbol").setValue(chargesModel.getCurrencySymbol());
//                    mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("currencyRate").setValue(chargesModel.getCurrencyRate());
//
//
//                    CommonUtils.showToast("Profile updated");
//                    finish();
//
//                } else {
//                    CommonUtils.showToast("Profile updated");
//                    finish();
//                }

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
//                        customertype.setText(customer.getCustomerType());
                        e_username.setText(customer.getUsername());
                        e_password.setText(customer.getPassword());
                        secondAddress.setText(customer.getSecondAddress());
//                        nameof.setText(customer.getName());
                        textName.setText(customer.getName());
                        textPhone.setText(customer.getPhone());
                        chooseLocation.setText("Location: " + customer.getCountry() + " > " + customer.getProvince() + " > " + customer.getDistrict() + " > " + customer.getCity());
                       customer.setFcmKey(FirebaseInstanceId.getInstance().getToken());
                        SharedPrefs.setCustomerModel(customer);
                        try {
                            Glide.with(MyProfile.this).load(customer.getPicUrl()).into(userImage);
                        }
                        catch (Exception e){

                        }
                        
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void initMatise() {
        mSelected.clear();
        imageUrl.clear();
        Matisse.from(MyProfile.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            if (requestCode == REQUEST_CODE_CHOOSE) {

                mSelected = Matisse.obtainResult(data);
                Glide.with(MyProfile.this).load(mSelected.get(0)).into(userImage);
                for (Uri img : mSelected) {
                    CompressImage compressImage = new CompressImage(MyProfile.this);
                    imageUrl.add(compressImage.compressImage("" + img));
                }

            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void putPictures(String path) {
        CommonUtils.showToast("Uploading picture...");
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        Uri file = Uri.fromFile(new File(path));


        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("picUrl").setValue("" + downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Picture Uploaded");
                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        CommonUtils.showToast("There was some error uploading pic");
                    }
                });
    }
    private void getShippingDetailsFromDB(String country) {
        mDatabase.child("Settings").child("Locations").child("Countries").child(countryType ? "international" : "local")
                .child(country).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    chargesModel = dataSnapshot.getValue(CountryModel.class);
                    if (district != null || city != null) {
                        getPerKgRatesFrom();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPerKgRatesFrom() {
        mDatabase.child("Settings").child("Locations").child("Cities").child(district).child(city).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    perKgRates = dataSnapshot.getValue(CityDeliveryChargesModel.class);
                    if (perKgRates != null) {
                        SharedPrefs.setOneKgRate(perKgRates.getOneKg());
                        SharedPrefs.setHalfKgRate(perKgRates.getHalfKg());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getListOfCountriesFromDb(String id) {
        mDatabase.child("Settings").child("DeliveryCharges").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

//                    chargesModel = dataSnapshot.getValue(LocationAndChargesModel.class);
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
