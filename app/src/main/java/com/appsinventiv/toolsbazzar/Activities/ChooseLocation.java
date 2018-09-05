package com.appsinventiv.toolsbazzar.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.appsinventiv.toolsbazzar.Adapters.CustomExpandableListAdapter;
import com.appsinventiv.toolsbazzar.Adapters.ExpandableListDataPump;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseLocation extends AppCompatActivity {
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        this.setTitle("Choose Country and City");
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {


                Intent returnIntent = new Intent();
                returnIntent.putExtra("country", expandableListTitle.get(groupPosition));
                returnIntent.putExtra("city",
                        expandableListDetail.get(expandableListTitle.get(groupPosition))
                                .get(childPosition));

                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        CommonUtils.showToast("Please select your country and city");
    }
}
