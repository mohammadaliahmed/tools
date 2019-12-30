package com.appsinventiv.toolsbazzar.Seller.SellerStore;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersFragment;

import java.util.ArrayList;


/**
 * Created by AliAh on 02/03/2018.
 */

public class SellerStoreFragmentAdapter extends FragmentPagerAdapter {

    private final String sellerId;
    Context mContext;
    ArrayList<String> arrayList;

    public SellerStoreFragmentAdapter(Context context, ArrayList<String> arrayList, FragmentManager fm, String sellerId) {
        super(fm);
        this.mContext = context;
        this.arrayList = arrayList;
        this.sellerId = sellerId;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

//        if (position == 0) {
//            return new SellerStoreNewProductsFragment(arrayList.get(position), sellerId);
//        } else {
            return new SellerStoreProductsFragment(arrayList.get(position), sellerId);
//        }


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
