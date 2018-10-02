package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


import com.appsinventiv.toolsbazzar.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by AliAh on 20/06/2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter{

    CategoryChoosen categoryChoosen;
    private Context _context;
    private List<String> _listDataHeader; // header titles
    private List<String> countryList; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,List<String> countryList,
            HashMap<String, List<String>> listChildData,CategoryChoosen categoryChoosen) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this.countryList = countryList;
        this._listDataChild = listChildData;
        this.categoryChoosen=categoryChoosen;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        final String locationId=(String) getGroup(groupPosition);
        final int locationPosition= groupPosition;
        final String  parentText= countryList.get(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        final TextView text = convertView.findViewById(R.id.lblListItem);

        text.setText(childText);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryChoosen.whichCategory(parentText,childText,locationId,locationPosition);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) countryList.get(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public interface CategoryChoosen{
        public void whichCategory(String main,String sub,String locationId,int position);

    }
}