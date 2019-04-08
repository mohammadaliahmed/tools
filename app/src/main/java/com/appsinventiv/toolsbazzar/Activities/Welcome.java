package com.appsinventiv.toolsbazzar.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.appsinventiv.toolsbazzar.ApplicationClass;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerLogin;
import com.appsinventiv.toolsbazzar.Seller.SellerMainActivity;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.PrefManager;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class Welcome extends AppCompatActivity {
    private Button btnBack, btnNext;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;
    DotsIndicator dotsIndicator;
    Switch sw_wholesale, sw_retail;
    RelativeLayout sell_layout, buy_layout;
    Switch sw_buy, sw_sell;
    String customerType = "";
    String userType = "";
    private PrefManager prefManager;
    int flag;

    int pageNumber = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        prefManager = new PrefManager(this);

        if (!prefManager.isFirstTimeLaunchWelcome()) {
            launchHomeScreen();
            finish();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        Intent i = getIntent();
        flag = i.getIntExtra("flag", 0);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btn_next);
        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);

        if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
            customerType = SharedPrefs.getCustomerType();
            layouts = new int[]{
                    R.layout.welcome_slide1,
                    R.layout.welcome_slide2,
                    R.layout.welcome_slide3,
            };
        } else if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("")) {
            layouts = new int[]{
                    R.layout.welcome_slide1,
                    R.layout.welcome_slide2,
                    R.layout.welcome_slide3,
                    R.layout.welcome_buy_sell,
                    R.layout.welcome_slide4
            };
        }
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        dotsIndicator.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(10);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                launchHomeScreen();
                int current = getItem(-1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
//                    launchHomeScreen();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                if (flag == 1) {
                    int current = getItem(+1);
                    viewPager.setCurrentItem(current);
                    if (current == layouts.length) {
                        // move to next screen

                        launchHomeScreen();

                    }
                } else {
                    if (pageNumber == 3) {
                        if (userType.equalsIgnoreCase("")) {
                            CommonUtils.showToast("Please select your type");
                        } else {
                            int current = getItem(+1);
                            if (current < layouts.length) {
                                // move to next screen
                                viewPager.setCurrentItem(current);
                            }
                        }
                    } else if (pageNumber == 4) {
                        if (userType.equalsIgnoreCase("sell")) {
                            launchSellerHomeScreen();
                        } else if (userType.equalsIgnoreCase("buy")) {
                            if (!customerType.equalsIgnoreCase("")) {
                                SharedPrefs.setUserType("buy");
                                SharedPrefs.setCustomerType(customerType);
                                launchHomeScreen();
                            } else {
                                CommonUtils.showToast("Please choose your type");
                            }
                        }
                    } else {
                        int current = getItem(+1);
                        if (current < layouts.length) {
                            // move to next screen
                            viewPager.setCurrentItem(current);
//                            launchHomeScreen();
                        }
                    }
                }

            }
        });
    }

    private void launchSellerHomeScreen() {
        Intent i = new Intent(Welcome.this, SellerLogin.class);
        startActivity(i);
    }

    private void launchHomeScreen() {
        if (SharedPrefs.getUserType().equalsIgnoreCase("sell")) {
            startActivity(new Intent(Welcome.this, SellerLogin.class));

        } else if (SharedPrefs.getUserType().equalsIgnoreCase("buy")) {
            startActivity(new Intent(Welcome.this, Login.class));

        }
//        Intent i = new Intent(Welcome.this, Login.class);
//        startActivity(i);

    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            pageNumber = position;
            // changing the next button text 'NEXT' / 'GOT IT'
            pageNumber = position;
            if (position == 3) {

                // last page. make button text to GOT IT
                btnNext.setText("GOT IT");
            } else if (position == 4) {
                // still pages are left
                if (userType.equalsIgnoreCase("buy")) {
                    if (buy_layout != null) {
                        buy_layout.setVisibility(View.VISIBLE);

                    }
                    if (sell_layout != null) {
                        sell_layout.setVisibility(View.GONE);

                    }
                } else if (userType.equalsIgnoreCase("sell")) {
                    if (buy_layout != null) {
                        buy_layout.setVisibility(View.GONE);

                    }
                    if (sell_layout != null) {
                        sell_layout.setVisibility(View.VISIBLE);

                    }
                }

                btnNext.setText("START");


            } else {
                // still pages are left
                btnNext.setText("NEXT");
                btnBack.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int pos) {
            if (pageNumber == 3) {
                if (userType.equalsIgnoreCase("")) {
                    CommonUtils.showToast("Please select your type");
                    viewPager.setOnTouchListener(new View.OnTouchListener() {

                        public boolean onTouch(View arg0, MotionEvent arg1) {
                            return true;
                        }
                    });
                } else {
                    viewPager.setOnTouchListener(null);
                }
            } else {
            }
        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            buy_layout = view.findViewById(R.id.buy_layout);
            sell_layout = view.findViewById(R.id.sell_layout);
            if (position == 3) {
                buy_layout = view.findViewById(R.id.buy_layout);
                sell_layout = view.findViewById(R.id.sell_layout);
                sw_buy = view.findViewById(R.id.buy);
                sw_buy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        viewPager.setOnTouchListener(null);

                        if (b) {

                            sw_sell.setChecked(false);
                            userType = "buy";

                        } else {
                            sw_sell.setChecked(true);

                        }
                    }
                });
                sw_sell = view.findViewById(R.id.sell);
                sw_sell.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        if (b) {
                            sw_buy.setChecked(false);
                            userType = "sell";
                            customerType = "wholesale";
                            SharedPrefs.setCustomerType(customerType);


                        } else {
                            sw_buy.setChecked(true);
                        }
                    }
                });
            } else if (position == 4) {
                buy_layout = view.findViewById(R.id.buy_layout);
                sell_layout = view.findViewById(R.id.sell_layout);
//                if (userType.equalsIgnoreCase("buy")) {


                    sw_retail = view.findViewById(R.id.retail);
                    sw_retail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                sw_wholesale.setChecked(false);
                                customerType = "retail";
                                SharedPrefs.setCustomerType(customerType);
                            } else {
                                sw_wholesale.setChecked(true);

                            }
                        }
                    });
                    sw_wholesale = view.findViewById(R.id.wholesale);
                    sw_wholesale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                sw_retail.setChecked(false);
                                customerType = "wholesale";
                                SharedPrefs.setCustomerType(customerType);
                            } else {
                                sw_retail.setChecked(true);
                            }
                        }
                    });
                } else if (userType.equalsIgnoreCase("sell")) {

                }
//                if (userType.equalsIgnoreCase("buy")) {
//                    if(buy_layout!=null){
//                        buy_layout.setVisibility(View.VISIBLE);
//
//                    }
//                    if(sell_layout!=null){
//                        sell_layout.setVisibility(View.GONE);
//
//                    }
//                } else if (userType.equalsIgnoreCase("sell")) {
//                    if(buy_layout!=null){
//                        buy_layout.setVisibility(View.GONE);
//
//                    }
//                    if(sell_layout!=null){
//                        sell_layout.setVisibility(View.VISIBLE);
//
//                    }
//                }

//            }

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {

            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
