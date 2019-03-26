package com.appsinventiv.toolsbazzar.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.TermsModel;
import com.appsinventiv.toolsbazzar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TermsAndConditions extends AppCompatActivity {
    DatabaseReference mDatabase;
    TextView terms, cookies, license, hyperlink, iframes, contentLiability,
            reservation, removal, disclaimer, replacement, other,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Terms & Conditions");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        terms = findViewById(R.id.terms);
        cookies = findViewById(R.id.cookies);
        license = findViewById(R.id.license);
        hyperlink = findViewById(R.id.hyperlink);
        iframes = findViewById(R.id.iframes);
        contentLiability = findViewById(R.id.contentLiability);
        reservation = findViewById(R.id.reservation);
        removal = findViewById(R.id.removal);
        disclaimer = findViewById(R.id.disclaimer);
        replacement = findViewById(R.id.replacement);
        other = findViewById(R.id.other);
        address = findViewById(R.id.address);

        mDatabase.child("Settings").child("Terms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    TermsModel model = dataSnapshot.getValue(TermsModel.class);
                    terms.setText(model.getTerms());
                    cookies.setText(model.getCookies());
                    license.setText(model.getLicense());
                    hyperlink.setText(model.getHyperlink());
                    iframes.setText(model.getIframes());
                    contentLiability.setText(model.getContentLiability());
                    reservation.setText(model.getReservation());
                    removal.setText(model.getRemoval());
                    disclaimer.setText(model.getDisclaimer());
                    replacement.setText(model.getReplacement());
                    other.setText(model.getOther());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        getAddressFromDb();
    }

    private void getAddressFromDb() {
        mDatabase.child("Settings").child("AboutUs").child("contact").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String contact = dataSnapshot.getValue(String.class);
                    address.setText(contact);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
