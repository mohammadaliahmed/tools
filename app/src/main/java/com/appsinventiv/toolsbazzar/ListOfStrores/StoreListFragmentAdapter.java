package com.appsinventiv.toolsbazzar.ListOfStrores;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appsinventiv.toolsbazzar.Fragments.SellerProductsFragment;

import java.util.ArrayList;

class StoreListFragmentAdapter extends FragmentPagerAdapter {
    Context context;
    ArrayList<String> categoryTitle;


    public StoreListFragmentAdapter(Context context, FragmentManager fm, ArrayList<String> categoryTitle) {
        super(fm);
        this.context = context;
        this.categoryTitle = categoryTitle;
    }

    @Override
    public Fragment getItem(int position) {

       if(position==0){
           return new ListOfStoreFragment();
       }else if(position==1){
           return new MeFollowingStores();
       }else{
           return null;
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
