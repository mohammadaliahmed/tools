package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Adapters.DealsFragmentAdapter;
import com.appsinventiv.toolsbazzar.Adapters.FragmentAdapter;
import com.appsinventiv.toolsbazzar.Adapters.MainSliderAdapter;
import com.appsinventiv.toolsbazzar.Adapters.WithoutPic2ProductAdapter;
import com.appsinventiv.toolsbazzar.Adapters.WithoutPic3ProductAdapter;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.MainCategoryModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.PrefManager;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

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


    ScrollView scrollView;
    DotsIndicator dots_indicator;
    TextView chat;
    private AppBarLayout mAppBarLayout;


    @Override
    protected void onResume() {
        super.onResume();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getUserAccountStatusFromDB();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

        scrollView = findViewById(R.id.scrollView);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
//        mAppBarLayout.setElevation(0);

//        getSupportActionBar().setElevation(0);
//        getSupportActionBar().setElevation(0);
//        toolbar.getBackground().setAlpha(0);
        this.setTitle("Fort City");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        dots_indicator = findViewById(R.id.dots_indicator);
        ic_settings = findViewById(R.id.ic_settings);
        ic_chat = findViewById(R.id.ic_chat);
        ic_mycart = findViewById(R.id.ic_mycart);
        ic_orders = findViewById(R.id.ic_orders);
        ic_wishlist = findViewById(R.id.ic_wishlist);


        ic_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                    Intent i = new Intent(MainActivity.this, WholesaleLiveChat.class);
                    startActivity(i);
                } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                    Intent i = new Intent(MainActivity.this, LiveChat.class);
                    startActivity(i);
                }

            }
        });
        ic_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyProfile.class);
                startActivity(i);
            }
        });
        ic_mycart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewCart.class);
                startActivity(i);
            }
        });
        ic_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ChooseMainCategory.class);
                startActivity(i);
            }
        });
        ic_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Whishlist.class);
                startActivity(i);
            }
        });


        if (SharedPrefs.getIsLoggedIn().equals("yes")) {
            mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("fcmKey").setValue(SharedPrefs.getFcmKey());
        }


        getBannerImagesFromDb();
        initViewPager();
//        initFirstGrid();
        initDealView();
        initCategoryView();

        initDrawer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                    if (i1 > 1200) {
//                        setStatusBarColor(false);
//                    } else {
//                        setStatusBarColor(true);
//                    }
                }
            });
        }
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
                            SharedPrefs.setAccountStatus("false");
                            CommonUtils.showToast("Your account is disabled");
                            System.exit(0);
                        } else {
                            SharedPrefs.setAccountStatus("true");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initFirstGrid() {
        final ArrayList<Product> itemList = new ArrayList<>();

        mDatabase.child("Products").limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            itemList.add(product);

                        }
                    }
                    ArrayList<Product> abc = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        abc.add(itemList.get(i));
                    }
                    final RecyclerView recyclerGrid = findViewById(R.id.recyclerGrid1);
                    final WithoutPic3ProductAdapter adapter = new WithoutPic3ProductAdapter(MainActivity.this, abc);
                    recyclerGrid.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));//
                    recyclerGrid.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    ArrayList<Product> def = new ArrayList<>();
                    for (int i = 3; i < 5; i++) {
                        def.add(itemList.get(i));
                    }
                    final RecyclerView recyclerGrid2 = findViewById(R.id.recyclerGrid2);
                    final WithoutPic2ProductAdapter adapter2 = new WithoutPic2ProductAdapter(MainActivity.this, def);
                    recyclerGrid2.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));//
                    recyclerGrid2.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();


                } else {

                    itemList.clear();
//                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


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

    private void initDealView() {
        ViewPager viewPager = findViewById(R.id.viewpager1);
        ArrayList<String> categoryList = new ArrayList<>();

        categoryList.add("Top Sellers");
        categoryList.add("Deals");
        categoryList.add("Most Liked");


        DealsFragmentAdapter adapter = new DealsFragmentAdapter(getSupportFragmentManager(), this, categoryList);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs1);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.setupWithViewPager(viewPager);
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


    private void initViewPager() {
//        pics = SharedPrefs.getArrayList("banners");
//        CommonUtils.showToast(""+pics);

//        pics.add("https://static.daraz.pk/cms/2017/W43/lipton/Lipton_06.jpg");
//        pics.add("https://i.pinimg.com/originals/c8/ea/1c/c8ea1c22157b50a8e455e17128afe497.png");


        banner = findViewById(R.id.slider);
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
                new Handler().postDelayed(this, 3000);
            }
        }, 3000); // Millisecond 1000 = 1 sec

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);

                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();
//            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
//                super.onBackPressed();
//                return;
//            } else {
//                Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
//            }
//
//            mBackPressed = System.currentTimeMillis();
        }

    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.name_drawer);
        TextView navSubtitle = (TextView) headerView.findViewById(R.id.customerType);


        chat = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.chat));

        chat.setTextColor(getResources().getColor(R.color.colorAccent));
        chat.setText("(New msg)");
        chat.setGravity(Gravity.CENTER_VERTICAL);
        chat.setTypeface(null, Typeface.BOLD);
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

            navUsername.setText(SharedPrefs.getName());
            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
                navSubtitle.setText("Wholesale");
            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
                navSubtitle.setText("Retail");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        final TextView textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItemCountFromDb = dataSnapshot.getChildrenCount();
                textCartItemCount.setText("" + cartItemCountFromDb);
                SharedPrefs.setCartCount("" + cartItemCountFromDb);
                if (dataSnapshot.getChildrenCount() == 0) {
                    SharedPrefs.setCartCount("0");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent i = new Intent(MainActivity.this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_cart) {
//            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
//                CommonUtils.showToast("Your Cart is empty");
//            } else {
            Intent i = new Intent(MainActivity.this, NewCart.class);
            startActivity(i);
//            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent i = new Intent(MainActivity.this, MyProfile.class);
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
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+94 775292313"));
            startActivity(i);
        } else if (id == R.id.terms) {
            Intent i = new Intent(MainActivity.this, TermsAndConditions.class);
            startActivity(i);
        } else if (id == R.id.cart) {
            Intent i = new Intent(MainActivity.this, NewCart.class);
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
