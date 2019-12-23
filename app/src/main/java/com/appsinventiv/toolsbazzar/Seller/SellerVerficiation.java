package com.appsinventiv.toolsbazzar.Seller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.PrefManager;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerVerficiation extends AppCompatActivity {

    DatabaseReference mDatabase;
    EditText code;
    Button verify;
    TextView resend;
    int codeFromDB;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_verification);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getCodeFromDB();
        verify = findViewById(R.id.verify);
        code = findViewById(R.id.code);
        resend = findViewById(R.id.resend);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendCode();
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code.getText().length() == 0) {
                    code.setError("Enter code");
                } else {
                    verifyCode();
                }
            }
        });

    }

    private void resendCode() {
        int randomPIN = (int) (Math.random() * 900000) + 100000;
        mDatabase.child("Sellers").child(SharedPrefs.getUsername()).child("code").setValue(randomPIN).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //TODO
//                sendCodeViaSMS();
                CommonUtils.showToast("Code sent");
            }
        });
    }

    private void verifyCode() {
        if (Integer.parseInt(code.getText().toString()) == codeFromDB) {
            CommonUtils.showToast("Success");
            updateStatusInDB();


        } else {
            CommonUtils.showToast("Wrong code entered\nPlease check and enter again");
        }
    }

    private void updateStatusInDB() {
        mDatabase.child("Sellers").child(SharedPrefs.getUsername()).child("codeVerified").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                launchHomeScreen();

            }
        });
    }


    private void getCodeFromDB() {
        mDatabase.child("Sellers").child(SharedPrefs.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    VendorModel vendorModel = dataSnapshot.getValue(VendorModel.class);
                    if (vendorModel != null) {
                        if (vendorModel.getCode() != 0) {
                            codeFromDB = vendorModel.getCode();
                        }
                    }
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

        startActivity(new Intent(SellerVerficiation.this, SellerMainActivity.class));

        finish();
    }
}
