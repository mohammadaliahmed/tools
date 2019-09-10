package com.appsinventiv.toolsbazzar.Seller;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.AboutUs;
import com.appsinventiv.toolsbazzar.Activities.AccountIsDisabled;
import com.appsinventiv.toolsbazzar.Activities.Cart;
import com.appsinventiv.toolsbazzar.Activities.ChooseMainCategory;
import com.appsinventiv.toolsbazzar.Activities.ClientProhibted;
import com.appsinventiv.toolsbazzar.Activities.LiveChat;
import com.appsinventiv.toolsbazzar.Activities.Login;
import com.appsinventiv.toolsbazzar.Activities.MainActivity;
import com.appsinventiv.toolsbazzar.Activities.MyOrders;
import com.appsinventiv.toolsbazzar.Activities.MyProfile;
import com.appsinventiv.toolsbazzar.Activities.NewCart;
import com.appsinventiv.toolsbazzar.Activities.NewSales;
import com.appsinventiv.toolsbazzar.Activities.Search;
import com.appsinventiv.toolsbazzar.Activities.SellerProhibted;
import com.appsinventiv.toolsbazzar.Activities.Splash;
import com.appsinventiv.toolsbazzar.Activities.TermsAndConditions;
import com.appsinventiv.toolsbazzar.Activities.Welcome;
import com.appsinventiv.toolsbazzar.Activities.Whishlist;
import com.appsinventiv.toolsbazzar.Activities.WholesaleLiveChat;
import com.appsinventiv.toolsbazzar.Adapters.DealsFragmentAdapter;
import com.appsinventiv.toolsbazzar.Adapters.FragmentAdapter;
import com.appsinventiv.toolsbazzar.Adapters.MainSliderAdapter;
import com.appsinventiv.toolsbazzar.Adapters.SellerFragmentAdapter;
import com.appsinventiv.toolsbazzar.Models.AdminModel;
import com.appsinventiv.toolsbazzar.Models.Customer;
import com.appsinventiv.toolsbazzar.Models.MainCategoryModel;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.Reviews.SellerProductReviews;
import com.appsinventiv.toolsbazzar.Seller.Sales.SellerSales;
import com.appsinventiv.toolsbazzar.Seller.SellerChat.SellerChats;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.Orders;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersCourier;
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

public class SellerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DatabaseReference mDatabase;
    MainSliderAdapter mViewPagerAdapter;
    private static final int TIME_INTERVAL = 2000;
    ArrayList<String> pics = new ArrayList<>();
    ViewPager banner;
    int currentPic = 0;
    DotsIndicator dots_indicator;
    private TextView chat;

    FloatingActionButton fab;
    private AppBarLayout mAppBarLayout;
    CollapsingToolbarLayout collapsing_toolbar;

    LinearLayout ic_settings, ic_chat, reviews, myProducts, myOrders;
    NestedScrollView nested;
    ImageView nav;
    TextView toolbarTitle;
    private DrawerLayout drawer;
    LinearLayout rela;
    private ViewPager viewPager;
    private ViewPager viewPager2;
//    NestedScrollView scrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("viewPagerHeight"));
        nested = findViewById(R.id.nested);
        fab = findViewById(R.id.fab);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        nav = findViewById(R.id.nav);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        rela = findViewById(R.id.rela);
//        scrollview = findViewById(R.id.scrollview);
//        scrollview.setFillViewport (true);


        this.setTitle("My Store View");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerMainActivity.this, SellerAddProduct.class);
                startActivity(i);
            }
        });

        setSupportActionBar(toolbar);
        setupSubmenu();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nested.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (i1 > 1) {
                        toolbarTitle.setText("My Store View");
                    } else {
                        toolbarTitle.setText("");
                    }
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


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


        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == -collapsing_toolbar.getHeight() + toolbar.getHeight()) {
                    //toolbar is collapsed here
                    //write your code here
                    collapsing_toolbar.setTitle("My Store View");
                    collapsing_toolbar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
                    collapsing_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.default_back));


                } else {
                    collapsing_toolbar.setTitle("");
                }

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        dots_indicator = findViewById(R.id.dots_indicator);
        if (SharedPrefs.getVendor().getUsername() != null) {
            mDatabase.child("Sellers").child(SharedPrefs.getVendor().getUsername()).child("fcmKey").setValue(FirebaseInstanceId.getInstance().getToken());
        }
        this.setTitle("My Store View");
        initTabsView();
        initTabsView2();
        getBannerImagesFromDb();
        initViewPager();
        initDrawer();
        getAdminDetails();
        getUserAccountStatusFromDB();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            int count = intent.getIntExtra("count", 0);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rela.getLayoutParams();
            float abc = 500 * count;
            params.height = (int) abc;
            rela.setLayoutParams(params);


        }
    };

    private void setupSubmenu() {
        ic_settings = findViewById(R.id.ic_settings);
        ic_chat = findViewById(R.id.ic_chat);
        myOrders = findViewById(R.id.myOrders);
        reviews = findViewById(R.id.reviews);
        myProducts = findViewById(R.id.myProducts);

        ic_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SellerMainActivity.this, SellerChats.class);
                startActivity(i);


            }
        });
        ic_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerMainActivity.this, SellerScreen.class);
                startActivity(i);
            }
        });

        myProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerMainActivity.this, SellerListOfProducts.class);
                startActivity(i);
            }
        });
        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerMainActivity.this, Orders.class);
                startActivity(i);
            }
        });
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerMainActivity.this, SellerProductReviews.class);
                startActivity(i);
            }
        });


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

    private void getUserAccountStatusFromDB() {
        mDatabase.child("Sellers").child(SharedPrefs.getVendor().getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    VendorModel vendor = dataSnapshot.getValue(VendorModel.class);
                    if (vendor != null) {
                        SharedPrefs.setVendorModel(vendor);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initTabsView() {
        viewPager = findViewById(R.id.viewpager1);
        ArrayList<String> categoryList = new ArrayList<>();

        categoryList.add("Approved");
        categoryList.add("Pending");
        categoryList.add("Rejected");


        final SellerFragmentAdapter adapter = new SellerFragmentAdapter(this, getSupportFragmentManager(), categoryList, "1");
        viewPager.setAdapter(adapter);


        TabLayout tabLayout = findViewById(R.id.sliding_tabs1);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.setupWithViewPager(viewPager);
//        viewPager.getLayoutParams().height=20000;


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initTabsView2() {
        viewPager2 = findViewById(R.id.viewpager2);
        final ArrayList<String> categoryList2 = new ArrayList<>();
        final SellerCategoryFragmentAdapter adapter2 = new SellerCategoryFragmentAdapter(this, getSupportFragmentManager(), categoryList2, "1");


        mDatabase.child("Settings").child("Categories").child("MainCategories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    categoryList2.add("All");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MainCategoryModel categoryTitle = snapshot.getValue(MainCategoryModel.class);
                        categoryList2.add(categoryTitle.getMainCategory());
                    }
                    adapter2.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        viewPager2.setAdapter(adapter2);


        TabLayout tabLayout2 = findViewById(R.id.sliding_tabs2);
        tabLayout2.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout2.setupWithViewPager(viewPager2);

    }


    private void initViewPager() {
//        pics = SharedPrefs.getArrayList("banners");
//        CommonUtils.showToast(""+pics);

//        pics.add("https://static.daraz.pk/cms/2017/W43/lipton/Lipton_06.jpg");
//        pics.add("https://i.pinimg.com/originals/c8/ea/1c/c8ea1c22157b50a8e455e17128afe497.png");


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

    private void getBannerImagesFromDb() {
        mDatabase.child("Settings").child("SellerBanners").addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void initDrawer() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.name_drawer);
        TextView navSubtitle = (TextView) headerView.findViewById(R.id.customerType);
        CircleImageView imageView = headerView.findViewById(R.id.profileimageView);
        if (SharedPrefs.getVendor().getPicUrl() != null) {
            Glide.with(this).load(SharedPrefs.getVendor().getPicUrl()).into(imageView);

        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerMainActivity.this, SellerProfile.class));
            }
        });

        chat = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.chat));

        chat.setTextColor(getResources().getColor(R.color.colorGreen));
        if (SharedPrefs.getNewMsg().equalsIgnoreCase("1")) {
            chat.setText("(New msg)");

        } else {
            chat.setText("");

        }
        chat.setGravity(Gravity.CENTER_VERTICAL);
        chat.setTypeface(null, Typeface.BOLD);
        if (SharedPrefs.getUsername().equalsIgnoreCase("")) {
            navSubtitle.setText("Welcome to Tools Bazzar");

            navUsername.setText("Login or Signup");
            navUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(SellerMainActivity.this, Login.class);
                    startActivity(i);
                }
            });
        } else {
            navSubtitle.setText(SharedPrefs.getCity());

            navUsername.setText("Hi, " +SharedPrefs.getVendor().getStoreName());
            navSubtitle.setText("Vendor");
//            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
//                navSubtitle.setText("Wholesale");
//            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
//                navSubtitle.setText("Retail");
//            }
        }
        getVendorDetailsFromDB();


    }

    private void getVendorDetailsFromDB() {
        mDatabase.child("Sellers").child(SharedPrefs.getVendor().getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    VendorModel model = dataSnapshot.getValue(VendorModel.class);
                    if (model != null) {
                        SharedPrefs.setVendorModel(model);
                        if (model.getPicUrl() != null) {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seller_main, menu);
//        final MenuItem menuItem = menu.findItem(R.id.action_cart);
//
//        View actionView = MenuItemCompat.getActionView(menuItem);
//        final TextView textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

//        mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                cartItemCountFromDb = dataSnapshot.getChildrenCount();
//                textCartItemCount.setText("" + cartItemCountFromDb);
//                SharedPrefs.setCartCount("" + cartItemCountFromDb);
//                if (dataSnapshot.getChildrenCount() == 0) {
//                    SharedPrefs.setCartCount("0");
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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
        if (id == R.id.action_search) {
            Intent i = new Intent(SellerMainActivity.this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_cart) {
//            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
//                CommonUtils.showToast("Your Cart is empty");
//            } else {
            Intent i = new Intent(SellerMainActivity.this, NewCart.class);
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

        if (id == R.id.addProduct) {
            Intent i = new Intent(SellerMainActivity.this, SellerAddProduct.class);
            startActivity(i);
        } else if (id == R.id.myProducts) {
            Intent i = new Intent(SellerMainActivity.this, SellerListOfProducts.class);
            startActivity(i);

        } else if (id == R.id.orders) {
            Intent i = new Intent(SellerMainActivity.this, Orders.class);
            startActivity(i);

        } else if (id == R.id.shipping) {
            Intent i = new Intent(SellerMainActivity.this, SellerProductReviews.class);
            startActivity(i);

        } else if (id == R.id.sales) {
            Intent i = new Intent(SellerMainActivity.this, SellerSales.class);
            startActivity(i);

        } else if (id == R.id.chat) {

            Intent i = new Intent(SellerMainActivity.this, SellerChats.class);
            startActivity(i);


        } else if (id == R.id.profile) {

            Intent i = new Intent(SellerMainActivity.this, SellerScreen.class);
            startActivity(i);


        } else if (id == R.id.clientApp) {
            PrefManager prefManager;
            prefManager = new PrefManager(this);
            prefManager.setFirstTimeLaunch(true);
            Intent i = new Intent(SellerMainActivity.this, Login.class);
            startActivity(i);


        } else if (id == R.id.whishlist) {
            Intent i = new Intent(SellerMainActivity.this, Whishlist.class);
            startActivity(i);


        } else if (id == R.id.prohibted) {
            Intent i = new Intent(SellerMainActivity.this, SellerProhibted.class);
            startActivity(i);


        } else if (id == R.id.contactUs) {
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+94 775292313"));
            startActivity(i);
        } else if (id == R.id.terms) {
            Intent i = new Intent(SellerMainActivity.this, SellerTermsAndConditions.class);
            startActivity(i);
        } else if (id == R.id.cart) {
            Intent i = new Intent(SellerMainActivity.this, NewCart.class);
            startActivity(i);
        } else if (id == R.id.aboutUs) {
            Intent i = new Intent(SellerMainActivity.this, AboutUs.class);
            startActivity(i);
        } else if (id == R.id.rateUs) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + SellerMainActivity.this.getPackageName())));
        } else if (id == R.id.openSlider) {
            PrefManager prefManager = new PrefManager(this);
            prefManager.setIsFirstTimeLaunchWelcome(true);
            Intent i = new Intent(SellerMainActivity.this, Welcome.class);
            i.putExtra("flag", 1);

            startActivity(i);
        } else if (id == R.id.signout) {
            PrefManager prefManager = new PrefManager(SellerMainActivity.this);
            prefManager.setFirstTimeLaunch(true);
            SharedPrefs.setIsLoggedIn("no");
            Intent i = new Intent(SellerMainActivity.this, Splash.class);
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
