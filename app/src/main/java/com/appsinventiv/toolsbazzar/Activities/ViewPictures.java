package com.appsinventiv.toolsbazzar.Activities;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.appsinventiv.toolsbazzar.Adapters.SliderAdapter;
import com.appsinventiv.toolsbazzar.Adapters.ViewPicturesSliderAdapter;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerViewProduct;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class ViewPictures extends AppCompatActivity {
    ViewPager mViewPager;
    ViewPicturesSliderAdapter sliderAdapter;
    DotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pictures);

        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);

        mViewPager = findViewById(R.id.viewpager);
        sliderAdapter = new ViewPicturesSliderAdapter(ViewPictures.this, ViewProduct.picUrls, 0);


        mViewPager.setAdapter(sliderAdapter);
        dotsIndicator.setViewPager(mViewPager);
    }
}
