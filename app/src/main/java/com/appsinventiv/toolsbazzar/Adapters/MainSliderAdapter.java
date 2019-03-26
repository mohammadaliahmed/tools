package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsinventiv.toolsbazzar.Activities.ListOfProducts;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 21/02/2018.
 */

public class MainSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> picturesList;

    public MainSliderAdapter(Context context, ArrayList<String> picturesList) {

        this.context = context;
        this.picturesList = picturesList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.main_product_slider, container, false);
        ImageView imageView = view.findViewById(R.id.slider_image);
        Glide.with(context).load(picturesList.get(position)).into(imageView);
        container.addView(view);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context, SellerListOfProducts.class);
//                if(position==0) {
//                    i.putExtra("category", "Tea");
//                    i.putExtra("position", 2);
//                }else if(position==1){
//                    i.putExtra("category", "Laundry");
//                    i.putExtra("position", 1);
//                }
//                context.startActivity(i);
//            }
//        });

        return view;
    }

    @Override
    public int getCount() {

        if (picturesList == null) {
            return 0;
        } else if (picturesList.size() > 0) {
            return picturesList.size();
        } else {
            return picturesList.size();
        }

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);

    }

}
