package com.appsinventiv.toolsbazzar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appsinventiv.toolsbazzar.Activities.ViewPictures;
import com.appsinventiv.toolsbazzar.R;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 25/12/2017.
 */

public class ViewPicturesSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public ArrayList<String> pictures;
    int flag;


    public ViewPicturesSliderAdapter(Context context, ArrayList<String> pictures, int flag) {
        this.context = context;
        this.pictures = pictures;
        this.flag = flag;
    }


    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pic_product_slider, container, false);
        ImageView imageView = view.findViewById(R.id.slider_image);
        Glide.with(context)
                .load(pictures.get(position))
                .placeholder(R.drawable.placeholder)
                .into(imageView);


        container.addView(view);
        if (flag == 1) {

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ViewPictures.class);
                    context.startActivity(i);
                }
            });
        } else {
            imageView.setOnTouchListener(new ImageMatrixTouchHandler(context));

        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
