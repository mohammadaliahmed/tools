package com.appsinventiv.toolsbazzar.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ChooseCountry;
import com.appsinventiv.toolsbazzar.Models.CountryModel;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerAddBankAccount extends AppCompatActivity {
    Button update;
    EditText accountNumber, bankName, beneficiaryName;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_bank_account);
        this.setTitle("Add Bank account");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        update = findViewById(R.id.update);
        accountNumber = findViewById(R.id.accountNumber);
        bankName = findViewById(R.id.bankName);
        beneficiaryName = findViewById(R.id.beneficiaryName);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beneficiaryName.getText().length() == 0) {
                    beneficiaryName.setError("Enter name");
                } else if (bankName.getText().length() == 0) {
                    bankName.setError("Enter name");
                } else if (accountNumber.getText().length() == 0) {
                    accountNumber.setError("Enter name");
                } else {
                    sendDataToDB();
                }
            }
        });
        getDataFromDB();

    }

    private void getDataFromDB() {
        mDatabase.child("BankAccountDetails").child("Seller").child(SharedPrefs.getVendor().getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    BankDetailsModel model = dataSnapshot.getValue(BankDetailsModel.class);
                    if (model != null) {
                        beneficiaryName.setText(model.getBeneficiaryName());
                        bankName.setText(model.getBankName());
                        accountNumber.setText("" + model.getAccountNumber());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendDataToDB() {
        BankDetailsModel model = new BankDetailsModel(
                SharedPrefs.getVendor().getUsername(),
                beneficiaryName.getText().toString(),
                bankName.getText().toString(),
                accountNumber.getText().toString()

        );
        mDatabase.child("BankAccountDetails").child("Seller").child(SharedPrefs.getVendor().getUsername()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Updated");
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
