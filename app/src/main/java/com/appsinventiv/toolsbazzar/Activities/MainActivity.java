package com.appsinventiv.toolsbazzar.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.CustomerOrders.MyOrders;
import com.appsinventiv.toolsbazzar.Adapters.FragmentAdapter;
import com.appsinventiv.toolsbazzar.Adapters.MainSliderAdapter;
import com.appsinventiv.toolsbazzar.Customer.CustomerScreen;
import com.appsinventiv.toolsbazzar.Models.AdminModel;
import com.appsinventiv.toolsbazzar.Models.CompanyDetailsModel;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.MainCategoryModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SupportCenter;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.PrefManager;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DatabaseReference mDatabase;
    long cartItemCountFromDb;
    Toolbar toolbar;
    MainSliderAdapter mViewPagerAdapter;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    ArrayList<String> pics = new ArrayList<>();
    ViewPager banner;
    int currentPic = 0;
    int pos;
    LinearLayout ic_settings, ic_chat, ic_mycart, ic_orders, ic_wishlist;

    CollapsingToolbarLayout collapsing_toolbar;
    TextView toolbarTitle;

    //    ScrollView scrollView;
    DotsIndicator dots_indicator;
    TextView chat;
    private AppBarLayout mAppBarLayout;
    TextView cart_count;
    ImageView cartIcon, search, nav;
    private DrawerLayout drawer;
//    LinearLayout rela;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        nav = findViewById(R.id.nav);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        search = findViewById(R.id.search);
        cartIcon = findViewById(R.id.cartIcon);
        cart_count = findViewById(R.id.cart_count);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));


        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewCart.class);
                startActivity(i);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Search.class);
                startActivity(i);
            }
        });

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        collapsing_toolbar = findViewById(R.id.collapsing_toolbar);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
//
//        Window window = getWindow();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(Color.TRANSPARENT); }
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getWindow().addFlags(WindowManager.LayoutParams.NAV);
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset == (-collapsing_toolbar.getHeight() + toolbar.getHeight() + 75)) {
                    //toolbar is collapsed here
                    //write your code here
                    collapsing_toolbar.setTitle("Fort City");
                    collapsing_toolbar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
                    collapsing_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.default_back));


                } else {
                    collapsing_toolbar.setTitle("");
                }

            }
        });

        this.setTitle("Fort City");
        mDatabase = FirebaseDatabase.getInstance().getReference();


        if (SharedPrefs.getIsLoggedIn().equals("yes")) {
            mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("fcmKey").setValue(FirebaseInstanceId.getInstance().getToken());
        }


        getBannerImagesFromDb();
        initViewPager();
        initCategoryView();
        initDrawer();
        getAdminDetails();
        getCompanyDetails();
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItemCountFromDb = dataSnapshot.getChildrenCount();
                cart_count.setText("" + cartItemCountFromDb);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void initCategoryView() {

        ViewPager viewPager = findViewById(R.id.viewpager);
        final ArrayList<String> categoryList = new ArrayList<>();
        final FragmentAdapter adapter = new FragmentAdapter(this, getSupportFragmentManager(), categoryList, "1");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);

        tabLayout.setupWithViewPager(viewPager);

        mDatabase.child("Settings").child("Categories").child("MainCategories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    categoryList.add("Home");
                    categoryList.add("All");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MainCategoryModel categoryTitle = snapshot.getValue(MainCategoryModel.class);
                        categoryList.add(categoryTitle.getMainCategory());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCompanyDetails() {

        mDatabase.child("Settings").child("CompanyDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    CompanyDetailsModel model = dataSnapshot.getValue(CompanyDetailsModel.class);
                    if (model != null) {
                        SharedPrefs.setCompanyDetails(model);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getLogoDetails() {
        mDatabase.child("Settings").child("appIcon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String name = dataSnapshot.getValue(String.class);
                    if (name.equalsIgnoreCase("eidUlFitr")) {
                        changeIcon("com.appsinventiv.toolsbazzar.SplashAliasActivity");
                    } else {
                        changeIcon("com.appsinventiv.toolsbazzar.Activities.Splash");

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void changeIcon(String activityPath) {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(getComponentName(),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, activityPath),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


        //重启桌面 加速显示
//        restartSystemLauncher(pm);
    }

    private void getAdminDetails() {
        mDatabase.child("Admin").child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    AdminModel model = dataSnapshot.getValue(AdminModel.class);
                    if (model != null) {
//                        LiveChat.this.setTitle(model.getId());
                        String adminFcmKey = model.getFcmKey();
                        SharedPrefs.setAdminFcmKey(adminFcmKey);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setStatusBarColor(boolean abc) {
        if (abc) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
    }

    private void getUserAccountStatusFromDB() {
        mDatabase.child("Customers").child(SharedPrefs.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    if (customer != null) {
                        if (!customer.isActive()) {
//                            SharedPrefs.setAccountStatus("false");
                            CommonUtils.showToast("Your account is disabled");
                            Intent i = new Intent(MainActivity.this, AccountIsDisabled.class);
                            startActivity(i);
                        } else {
//                            SharedPrefs.setAccountStatus("true");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
//            int count = intent.getIntExtra("count", 0);
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rela.getLayoutParams();
//            float abc = 500 * 175;
//            params.height = (int) abc;
//            rela.setLayoutParams(params);


        }
    };


    private void getBannerImagesFromDb() {
        mDatabase.child("Settings").child("Banners").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> imgList = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String url = snapshot.child("url").getValue(String.class);
                        pics.add(url);
                    }
                    mViewPagerAdapter.notifyDataSetChanged();
//                    SharedPrefs.saveArrayList(imgList, "banners");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void initViewPager() {


        banner = findViewById(R.id.slider);
        dots_indicator = findViewById(R.id.dots_indicator);
        mViewPagerAdapter = new MainSliderAdapter(this, pics);
        banner.setAdapter(mViewPagerAdapter);
        mViewPagerAdapter.notifyDataSetChanged();
        dots_indicator.setViewPager(banner);
        banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPic = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Magic here
                if (pics != null) {
                    if (currentPic >= pics.size()) {
                        currentPic = 0;
                        banner.setCurrentItem(currentPic);
                    } else {
                        banner.setCurrentItem(currentPic);
                        currentPic++;
                    }
                }
                new Handler().postDelayed(this, 4000);
            }
        }, 4000); // Millisecond 1000 = 1 sec

    }


    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Exiting")
                    .setContentText("Are you sure you want to exit?")
                    .setCancelText("No, cancel!")
                    .setConfirmText("Yes, exit!")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();

                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();

        }

    }

    private void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.name_drawer);
        CircleImageView imageView = headerView.findViewById(R.id.imageView);
        TextView navSubtitle = (TextView) headerView.findViewById(R.id.customerType);

        if (SharedPrefs.getCustomerModel().getPicUrl() != null) {
            Glide.with(MainActivity.this).load(SharedPrefs.getCustomerModel().getPicUrl()).into(imageView);
        } else {
            Glide.with(MainActivity.this).load(R.drawable.logo).into(imageView);

        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyProfile.class));
            }
        });

        chat = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.chat));

//        chat.setGravity(Gravity.CENTER);
//
//        chat.setBackgroundResource(R.drawable.red_count);
//        chat.setTextColor(getResources().getColor(R.color.colorRed));
//        if(SharedPrefs.getNewMsg().equalsIgnoreCase("") || SharedPrefs.getNewMsg().equalsIgnoreCase("0")){
//
//        }else{
        chat.setText(SharedPrefs.getNewMsg());

//        }
//        chat.setTextSize(15);

        if (SharedPrefs.getUsername().equalsIgnoreCase("")) {
            navSubtitle.setText("Welcome to Tools Bazzar");

            navUsername.setText("Login or Signup");
            navUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                }
            });
        } else {
            navSubtitle.setText(SharedPrefs.getCity());

            navUsername.setText("Hi, " + SharedPrefs.getName());
            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                navSubtitle.setText("Wholesale");
            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                navSubtitle.setText("Retail");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItemCountFromDb = dataSnapshot.getChildrenCount();
//                textCartItemCount.setText("" + cartItemCountFromDb);
                cart_count.setText("" + cartItemCountFromDb);
                SharedPrefs.setCartCount("" + cartItemCountFromDb);
                if (dataSnapshot.getChildrenCount() == 0) {
                    SharedPrefs.setCartCount("0");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        actionView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onOptionsItemSelected(menuItem);
//            }
//        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            Intent i = new Intent(MainActivity.this, Search.class);
//            startActivity(i);
//        } else if (id == R.id.action_cart) {
////            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
////                CommonUtils.showToast("Your Cart is empty");
////            } else {
//            Intent i = new Intent(MainActivity.this, NewCart.class);
//            startActivity(i);
////            }
//
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent i = new Intent(MainActivity.this, CustomerScreen.class);
            startActivity(i);
        } else if (id == R.id.orders) {
            Intent i = new Intent(MainActivity.this, MyOrders.class);
            startActivity(i);

        } else if (id == R.id.categoryListing) {
            Intent i = new Intent(MainActivity.this, ChooseMainCategory.class);
            startActivity(i);

        } else if (id == R.id.newListing) {
            Intent i = new Intent(MainActivity.this, NewSales.class);
            startActivity(i);

        } else if (id == R.id.chat) {

            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                Intent i = new Intent(MainActivity.this, WholesaleLiveChat.class);
                startActivity(i);
            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                Intent i = new Intent(MainActivity.this, LiveChat.class);
                startActivity(i);
            }


        } else if (id == R.id.whishlist) {
            Intent i = new Intent(MainActivity.this, Whishlist.class);
            startActivity(i);


        } else if (id == R.id.contactUs) {
//            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + SharedPrefs.getCompanyDetails().getTelephone()));
//            startActivity(i);
            Intent i = new Intent(MainActivity.this, SupportCenter.class);
            startActivity(i);
        } else if (id == R.id.terms) {
            Intent i = new Intent(MainActivity.this, TermsAndConditions.class);
            startActivity(i);
        } else if (id == R.id.cart) {
            Intent i = new Intent(MainActivity.this, NewCart.class);
            startActivity(i);
        } else if (id == R.id.prohibted) {
            Intent i = new Intent(MainActivity.this, ClientProhibted.class);
            startActivity(i);
        } else if (id == R.id.aboutUs) {
            Intent i = new Intent(MainActivity.this, AboutUs.class);
            startActivity(i);
        } else if (id == R.id.rateUs) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
        } else if (id == R.id.openSlider) {
            PrefManager prefManager = new PrefManager(this);
            prefManager.setIsFirstTimeLaunchWelcome(true);
            Intent i = new Intent(MainActivity.this, Welcome.class);
            i.putExtra("flag", 1);

            startActivity(i);
        } else if (id == R.id.signout) {
            PrefManager prefManager = new PrefManager(MainActivity.this);
            prefManager.setFirstTimeLaunch(true);
            SharedPrefs.setIsLoggedIn("no");
            Intent i = new Intent(MainActivity.this, Splash.class);
            startActivity(i);

            deleteAppData();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deleteAppData() {
        try {
            // clearing app data
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + packageName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
