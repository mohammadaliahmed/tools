package com.appsinventiv.toolsbazzar.Seller;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ChooseAddress;
import com.appsinventiv.toolsbazzar.Activities.ChooseCountry;
import com.appsinventiv.toolsbazzar.Models.CityDeliveryChargesModel;
import com.appsinventiv.toolsbazzar.Models.CountryModel;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
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

public class SellerProfile extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 23;
    EditText e_name, e_phone, e_address, e_username, e_password, businessRegNumber, storeName, email, telephone;
    Button update;
    DatabaseReference mDatabase;
    TextView chooseLocation;
    TextView textName, textPhone;
    CountryModel chargesModel;
    public static String province = "", district = "", city = "", country = "", locationId = "";

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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
        this.setTitle("My Profile");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        businessRegNumber = findViewById(R.id.businessRegNumber);
        storeName = findViewById(R.id.storeName);
        email = findViewById(R.id.email);
        telephone = findViewById(R.id.telephone);
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
                Intent i = new Intent(SellerProfile.this, ChooseCountry.class);
                startActivityForResult(i, 1);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                HashMap<String, Object> map = new HashMap<>();
                map.put("address", e_address.getText().toString());
                map.put("phone", e_phone.getText().toString());
                map.put("name", e_name.getText().toString());
                map.put("password", e_password.getText().toString());
                map.put("businessRegNumber", businessRegNumber.getText().toString());
                map.put("storeName", storeName.getText().toString());
                map.put("email", email.getText().toString());
                map.put("telephone", telephone.getText().toString());
                if (!city.equalsIgnoreCase("")) {
                    map.put("city", city);
                    map.put("country", country);
                    map.put("province", province);
                    map.put("district", district);
                    map.put("currencySymbol", chargesModel.getCurrencySymbol());
                    map.put("currencyRate", chargesModel.getCurrencyRate());

                }
                mDatabase.child("Sellers").child(SharedPrefs.getUsername()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Profile updated");
                        if (mSelected.size() > 0) {
                            putPictures(imageUrl.get(0));
                        }

                    }
                });

            }
        });
        mDatabase.child("Sellers").child(SharedPrefs.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    VendorModel seller = dataSnapshot.getValue(VendorModel.class);
                    if (seller != null) {
                        e_name.setText(seller.getVendorName());
                        e_address.setText(seller.getAddress());
                        e_phone.setText(seller.getPhone());
                        businessRegNumber.setText(seller.getBusinessRegistrationNumber());
                        storeName.setText(seller.getStoreName());
                        email.setText(seller.getEmail());
                        telephone.setText(seller.getTelNumber());
                        e_username.setText(seller.getUsername());
                        e_password.setText(seller.getPassword());
                        textName.setText(seller.getStoreName());
                        textPhone.setText(seller.getPhone());
                        chooseLocation.setText("Location: " + seller.getCountry() + " > " + seller.getProvince() + " > " + seller.getDistrict() + " > " + seller.getCity());
                        seller.setFcmKey(FirebaseInstanceId.getInstance().getToken());
                        SharedPrefs.setVendorModel(seller);
                        try {
                            Glide.with(SellerProfile.this).load(seller.getPicUrl()).into(userImage);
                        } catch (Exception e) {

                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void initMatise() {
        mSelected.clear();
        imageUrl.clear();
        Matisse.from(SellerProfile.this)
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
                Glide.with(SellerProfile.this).load(mSelected.get(0)).into(userImage);
                for (Uri img : mSelected) {
                    CompressImage compressImage = new CompressImage(SellerProfile.this);
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
                        mDatabase.child("Sellers").child(SharedPrefs.getUsername()).child("picUrl").setValue("" + downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
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