package com.appsinventiv.toolsbazzar.Utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersCourier;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersFragment;

import java.util.ArrayList;


/**
 * Created by AliAh on 02/03/2018.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    Context mContext;
    ArrayList<String> arrayList;
    String by;

    public SimpleFragmentPagerAdapter(Context context, ArrayList<String> arrayList, FragmentManager fm, String by) {
        super(fm);
        this.mContext = context;
        this.arrayList = arrayList;
        this.by = by;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {


        return new OrdersFragment(arrayList.get(position), by);


    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return arrayList.size();
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position

        return arrayList.get(position);
    }

}
