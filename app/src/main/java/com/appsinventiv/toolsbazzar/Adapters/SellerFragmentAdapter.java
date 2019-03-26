package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appsinventiv.toolsbazzar.Fragments.SellerProductsFragment;

import java.util.ArrayList;

/**
 * Created by AliAh on 20/06/2018.
 */

public class SellerFragmentAdapter extends FragmentPagerAdapter {
    Context context;
    ArrayList<String> categoryTitle;
    String flag;

    public SellerFragmentAdapter(Context context, FragmentManager fm, ArrayList<String> categoryTitle, String flag) {
        super(fm);
        this.context = context;
        this.categoryTitle = categoryTitle;
        this.flag=flag;
    }

    @Override
    public Fragment getItem(int position) {

        return new SellerProductsFragment(categoryTitle.get(position),flag);

    }

    @Override
    public int getCount() {
        return categoryTitle.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return categoryTitle.get(position);

    }

}
