package com.appsinventiv.toolsbazzar.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.AboutUsModel;
import com.appsinventiv.toolsbazzar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class AboutUs extends AppCompatActivity {
    TextView about, vision, mission, values, contact;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        this.setTitle("About Us");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        about = findViewById(R.id.about);
        vision = findViewById(R.id.vision);
        mission = findViewById(R.id.mission);
        values = findViewById(R.id.values);
        contact = findViewById(R.id.contact);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Settings").child("AboutUs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    AboutUsModel model = dataSnapshot.getValue(AboutUsModel.class);
                    if (model != null) {
                        about.setText(model.getAbout());
                        vision.setText(model.getVision());
                        mission.setText(model.getMission());
                        values.setText(model.getValues());
                        contact.setText(model.getContact());

                    }
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
