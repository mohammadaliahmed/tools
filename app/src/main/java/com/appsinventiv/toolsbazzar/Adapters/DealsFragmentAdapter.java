package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appsinventiv.toolsbazzar.Fragments.DealsFragment;
import com.appsinventiv.toolsbazzar.Fragments.MostLikedFragment;
import com.appsinventiv.toolsbazzar.Fragments.TopSellerFragment;

import java.util.ArrayList;

/**
 * Created by AliAh on 27/08/2018.
 */

public class DealsFragmentAdapter extends FragmentPagerAdapter {
    Context context;
    ArrayList<String> categoryTitle;

    public DealsFragmentAdapter(FragmentManager fm, Context context, ArrayList<String> categoryTitle) {
        super(fm);
        this.context = context;
        this.categoryTitle = categoryTitle;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TopSellerFragment();
        } else if (position == 1) {
            return new DealsFragment();
        } else {
            return new MostLikedFragment();
        }
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
