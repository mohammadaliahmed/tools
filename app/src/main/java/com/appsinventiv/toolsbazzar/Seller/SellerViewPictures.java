package com.appsinventiv.toolsbazzar.Seller;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.appsinventiv.toolsbazzar.Activities.ViewProduct;
import com.appsinventiv.toolsbazzar.Adapters.ViewPicturesSliderAdapter;
import com.appsinventiv.toolsbazzar.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class SellerViewPictures extends AppCompatActivity {
    ViewPager mViewPager;
    ViewPicturesSliderAdapter sliderAdapter;
    DotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_view_pictures);

        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);

        mViewPager = findViewById(R.id.viewpager);

        sliderAdapter = new ViewPicturesSliderAdapter(SellerViewPictures.this, SellerViewProduct.picUrls, 0);


        mViewPager.setAdapter(sliderAdapter);
        dotsIndicator.setViewPager(mViewPager);
    }
}
