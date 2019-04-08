package com.appsinventiv.toolsbazzar.Seller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.AboutUs;
import com.appsinventiv.toolsbazzar.Activities.AccountIsDisabled;
import com.appsinventiv.toolsbazzar.Activities.Cart;
import com.appsinventiv.toolsbazzar.Activities.ChooseMainCategory;
import com.appsinventiv.toolsbazzar.Activities.LiveChat;
import com.appsinventiv.toolsbazzar.Activities.Login;
import com.appsinventiv.toolsbazzar.Activities.MainActivity;
import com.appsinventiv.toolsbazzar.Activities.MyOrders;
import com.appsinventiv.toolsbazzar.Activities.MyProfile;
import com.appsinventiv.toolsbazzar.Activities.NewCart;
import com.appsinventiv.toolsbazzar.Activities.NewSales;
import com.appsinventiv.toolsbazzar.Activities.Search;
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
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.Sales.SellerSales;
import com.appsinventiv.toolsbazzar.Seller.SellerChat.SellerChats;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.Orders;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersCourier;
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
    LinearLayout ic_settings, ic_chat, ic_orders, ic_wishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerMainActivity.this, SellerAddProduct.class);
                startActivity(i);
            }
        });

        setSupportActionBar(toolbar);
        setupSubmenu();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        dots_indicator = findViewById(R.id.dots_indicator);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (SharedPrefs.getIsLoggedIn().equals("yes")) {
            mDatabase.child("Sellers").child(SharedPrefs.getUsername()).child("fcmKey").setValue(SharedPrefs.getFcmKey());
        }
        this.setTitle("My Store View");
//        getUserAccountStatusFromDB();
        initTabsView();
        getBannerImagesFromDb();
        initViewPager();
        initDrawer();
        getAdminDetails();
    }

    private void setupSubmenu() {
        ic_settings = findViewById(R.id.ic_settings);
        ic_chat = findViewById(R.id.ic_chat);
        ic_orders = findViewById(R.id.ic_orders);
        ic_wishlist = findViewById(R.id.ic_wishlist);

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
                Intent i = new Intent(SellerMainActivity.this, SellerProfile.class);
                startActivity(i);
            }
        });

        ic_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerMainActivity.this, ChooseMainCategory.class);
                startActivity(i);
            }
        });
        ic_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerMainActivity.this, Whishlist.class);
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
        mDatabase.child("Sellers").child(SharedPrefs.getVendor().getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    VendorModel customer = dataSnapshot.getValue(VendorModel.class);
                    if (customer != null) {
                        if (!customer.isActive()) {
//                            SharedPrefs.setAccountStatus("false");
                            CommonUtils.showToast("Your account is disabled");
                            Intent i = new Intent(SellerMainActivity.this, AccountIsDisabled.class);
                            startActivity(i);
//                            System.exit(0);
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

    private void initTabsView() {
        ViewPager viewPager = findViewById(R.id.viewpager1);
        ArrayList<String> categoryList = new ArrayList<>();

        categoryList.add("Approved");
        categoryList.add("Pending");
        categoryList.add("Rejected");


        final SellerFragmentAdapter adapter = new SellerFragmentAdapter(this, getSupportFragmentManager(), categoryList, "1");
        viewPager.setAdapter(adapter);


        TabLayout tabLayout = findViewById(R.id.sliding_tabs1);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.setupWithViewPager(viewPager);
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

    private void initDrawer() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);


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

            navUsername.setText(SharedPrefs.getVendor().getStoreName());
            navSubtitle.setText("Vendor");
//            if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
//                navSubtitle.setText("Wholesale");
//            } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
//                navSubtitle.setText("Retail");
//            }
        }


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
            Intent i = new Intent(SellerMainActivity.this, OrdersCourier.class);
            startActivity(i);

        } else if (id == R.id.sales) {
            Intent i = new Intent(SellerMainActivity.this, SellerSales.class);
            startActivity(i);

        } else if (id == R.id.chat) {

            Intent i = new Intent(SellerMainActivity.this, SellerChats.class);
            startActivity(i);


        } else if (id == R.id.profile) {

            Intent i = new Intent(SellerMainActivity.this, SellerProfile.class);
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
