package com.appsinventiv.toolsbazzar.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.appsinventiv.toolsbazzar.Adapters.CustomExpandableListAdapter;
import com.appsinventiv.toolsbazzar.Adapters.ExpandableListAdapter;
import com.appsinventiv.toolsbazzar.Adapters.ExpandableListDataPump;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseLocation extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<String>();
    List<String> countryList = new ArrayList<String>();
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        this.setTitle("Choose Country and City");

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Settings").child("DeliveryCharges");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    listDataHeader.clear();
                    for (DataSnapshot header : dataSnapshot.getChildren()) {
                        listDataHeader.add(header.getKey());
                        countryList.add(header.child("countryName").getValue(String.class));
                    }
                    setChildList();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listAdapter = new ExpandableListAdapter(this, listDataHeader,countryList,
                listDataChild,
                new ExpandableListAdapter.CategoryChoosen() {
                    @Override
                    public void whichCategory(String parent, String text,String locationId,int locationPosition) {
//                        category.setText("Category: "+text);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("country", parent);
                        returnIntent.putExtra("city", text);
                        returnIntent.putExtra("locationId", locationId);
                        returnIntent.putExtra("locationPosition", locationPosition);

                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                });

        // setting list adapter
        expListView.setAdapter(listAdapter);


    }

    private void setChildList() {
        int ijk = 0;
        for (String head : listDataHeader) {

            final int finalIjk = ijk;
            mDatabase.child(head).child("cities").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                        };
                        List<String> abc = dataSnapshot.getValue(t);
                        listDataChild.put(listDataHeader.get(finalIjk), abc);
                        listAdapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            ijk++;
        }

    }

    @Override
    public void onBackPressed() {
        CommonUtils.showToast("Please select your country and city");
    }
}
