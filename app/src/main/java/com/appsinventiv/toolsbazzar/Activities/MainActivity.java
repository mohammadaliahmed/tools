package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.toolsbazzar.Adapters.DealsFragmentAdapter;
import com.appsinventiv.toolsbazzar.Adapters.FragmentAdapter;
import com.appsinventiv.toolsbazzar.Adapters.MainSliderAdapter;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.PrefManager;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    ScrollView scrollView;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scrollView);
        setSupportActionBar(toolbar);
        this.setTitle("Tools Bazzar");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (SharedPrefs.getIsLoggedIn().equals("yes")) {
            mDatabase.child("Customers").child(SharedPrefs.getUsername()).child("fcmKey").setValue(SharedPrefs.getFcmKey());
        }


        initDealView();
        initCategoryView();
        initDrawer();
        initViewPager();
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

        mDatabase.child("Settings").child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    categoryList.add("All");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String categoryTitle = snapshot.getKey();
                        categoryList.add(categoryTitle);
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
        pics.add("https://static.daraz.pk/cms/2017/W43/lipton/Lipton_06.jpg");
        pics.add("https://i.pinimg.com/originals/c8/ea/1c/c8ea1c22157b50a8e455e17128afe497.png");


        banner = findViewById(R.id.slider);
        mViewPagerAdapter = new MainSliderAdapter(this, pics);
        banner.setAdapter(mViewPagerAdapter);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Magic here
                if (currentPic >= pics.size()) {
                    currentPic = 0;
                    banner.setCurrentItem(currentPic);
                } else {
                    banner.setCurrentItem(currentPic);
                    currentPic++;
                }
                new Handler().postDelayed(this, 2000);
            }
        }, 2000); // Millisecond 1000 = 1 sec
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();
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
        TextView navSubtitle = (TextView) headerView.findViewById(R.id.phone_drawer);
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
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
                Intent i = new Intent(MainActivity.this, Cart.class);
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

        } else if (id == R.id.chat) {
            Intent i = new Intent(MainActivity.this, LiveChat.class);
            startActivity(i);


        } else if (id == R.id.contactUs) {
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+94 775292313"));
            startActivity(i);
        } else if (id == R.id.terms) {
            Intent i = new Intent(MainActivity.this, TermsAndConditions.class);
            startActivity(i);
        }
        else if (id == R.id.cart) {
            Intent i = new Intent(MainActivity.this, Cart.class);
            startActivity(i);
        }
        else if (id == R.id.aboutUs) {
            Intent i = new Intent(MainActivity.this, AboutUs.class);
            startActivity(i);
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
