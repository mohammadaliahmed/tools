package com.appsinventiv.toolsbazzar.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Adapters.FragmentAdapter;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfProducts extends AppCompatActivity {
    public static TextView textCartItemCount;
    DatabaseReference mDatabase;
    ArrayList<String> categoryList = new ArrayList<>();
    long cartItemCountFromDb;
    int pos;
    String category;
    int flag;

    boolean tabScrollable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_products);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent i = getIntent();
        pos = i.getIntExtra("position", 0);
        category = i.getStringExtra("category");
        flag = i.getIntExtra("flag", 0);

        this.setTitle(category);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ViewPager viewPager = findViewById(R.id.viewpager);
        if (category != null) {
            if (category.equalsIgnoreCase("Oil & Ghee")) {
                categoryList.add("Sunflower Oil");
                categoryList.add("Cooking Oil");
                categoryList.add("Other Edible Oil");
                categoryList.add("Canola Oil");
                categoryList.add("Olive Oil");
                categoryList.add("Corn Oil");
                categoryList.add("Ghee");
                categoryList.add("Desi Ghee");
            } else if (category.equalsIgnoreCase("Spices, Salt & Sugar")) {
                categoryList.add("Herbs & Spices");
                categoryList.add("Salt");
                categoryList.add("Sugar");
                categoryList.add("National Masala");
                categoryList.add("Shan Masala");
                categoryList.add("Seasoning Cubes");
                categoryList.add("Vinegar");
            } else if (category.equalsIgnoreCase("Daalain, Rice & Flour")) {
                categoryList.add("Daalian");
                categoryList.add("Rice");
                categoryList.add("Flour");
                categoryList.add("Dry Fruit");
                categoryList.add("Other");

            } else if (category.equalsIgnoreCase("Sauces, Olives & Pickles")) {
                categoryList.add("Ketchup");
                categoryList.add("Chilli Sauce");
                categoryList.add("Mayonise");
                categoryList.add("Olives");
                categoryList.add("Pickles");

            } else if (category.equalsIgnoreCase("Jam, Honey & Spread")) {
                categoryList.add("Jam");
                categoryList.add("Honey");
                categoryList.add("Spread");
                categoryList.add("Syrup");

            } else if (category.equalsIgnoreCase("Baking & Desert")) {
                categoryList.add("Baking Mix");
                categoryList.add("Jelly");
                categoryList.add("Laziza Deserts");
                categoryList.add("Other");
            } else if (category.equalsIgnoreCase("Women Care")) {
                categoryList.add("W Body Spray");
                categoryList.add("W Roll on");
                categoryList.add("Pads");
                categoryList.add("Hair Remover");
                categoryList.add("Nail Polish Remover");

            } else if (category.equalsIgnoreCase("Men Care")) {
                categoryList.add("M Roll on");
                categoryList.add("Body Spray");
                categoryList.add("Razors");
                categoryList.add("Shaving Foams");
                categoryList.add("After Shave");

            } else if (category.equalsIgnoreCase("Hair Care")) {
                categoryList.add("Hair Color");
                categoryList.add("Shampoo");
                categoryList.add("Conditioner");
                categoryList.add("Gel");
                categoryList.add("Hair Cream");
            } else if (category.equalsIgnoreCase("Skin Care")) {
                categoryList.add("Scrubs");
                categoryList.add("Lotion & Cream");
                categoryList.add("Face Wash");
                categoryList.add("Sun Block");
            } else if (category.equalsIgnoreCase("Dental Care")) {
                categoryList.add("Tooth Brush");
                categoryList.add("Tooth paste");
                categoryList.add("Mouth Wash");
                tabScrollable = true;
            } else if (category.equalsIgnoreCase("Soap, Hand Wash & Sanitizer")) {
                categoryList.add("Soap");
                categoryList.add("Hand Wash");
                categoryList.add("Shower Gel");
                tabScrollable = true;
            } else if (category.equalsIgnoreCase("Shoes Ploish & Brush")) {
                categoryList.add("Polish");
                categoryList.add("Brush");
                tabScrollable = true;
            } else if (category.equalsIgnoreCase("Fruits")) {
                categoryList.add("Vegetables");
                categoryList.add("Fruits");
                pos = 1;
                tabScrollable = true;

            } else if (category.equalsIgnoreCase("Vegetables")) {
                categoryList.add("Vegetables");
                categoryList.add("Fruits");
                pos = 0;
                tabScrollable = true;
            } else if (category.equalsIgnoreCase("Floor & Bath Cleaning")) {
                categoryList.add("Floor & Bath Cleaning");
                categoryList.add("Laundry");
                categoryList.add("Kitchen Cleaning");
                categoryList.add("Repellents");
                categoryList.add("Air Refreshners");
                categoryList.add("Cleaning Accessories");

                pos = 0;

            } else if (category.equalsIgnoreCase("Laundry")) {
                categoryList.add("Floor & Bath Cleaning");
                categoryList.add("Laundry");
                categoryList.add("Kitchen Cleaning");
                categoryList.add("Repellents");
                categoryList.add("Air Refreshners");
                categoryList.add("Cleaning Accessories");

                pos = 1;

            } else if (category.equalsIgnoreCase("Kitchen Cleaning")) {
                categoryList.add("Floor & Bath Cleaning");
                categoryList.add("Laundry");
                categoryList.add("Kitchen Cleaning");
                categoryList.add("Repellents");
                categoryList.add("Air Refreshners");
                categoryList.add("Cleaning Accessories");

                pos = 2;

            } else if (category.equalsIgnoreCase("Repellents")) {
                categoryList.add("Floor & Bath Cleaning");
                categoryList.add("Laundry");
                categoryList.add("Kitchen Cleaning");
                categoryList.add("Repellents");
                categoryList.add("Air Refreshners");
                categoryList.add("Cleaning Accessories");

                pos = 3;

            } else if (category.equalsIgnoreCase("Air Refreshners")) {
                categoryList.add("Floor & Bath Cleaning");
                categoryList.add("Laundry");
                categoryList.add("Kitchen Cleaning");
                categoryList.add("Repellents");
                categoryList.add("Air Refreshners");
                categoryList.add("Cleaning Accessories");

                pos = 4;

            } else if (category.equalsIgnoreCase("Cleaning Accessories")) {
                categoryList.add("Floor & Bath Cleaning");
                categoryList.add("Laundry");
                categoryList.add("Kitchen Cleaning");
                categoryList.add("Repellents");
                categoryList.add("Air Refreshners");
                categoryList.add("Cleaning Accessories");

                pos = 5;

            } else if (category.equalsIgnoreCase("Cold Drinks")) {
                categoryList.add("Cold Drinks");
                categoryList.add("Juices");
                categoryList.add("Tea");
                categoryList.add("Mineral Water");
                categoryList.add("Sharbat");
                categoryList.add("Coffee");

                pos = 0;

            } else if (category.equalsIgnoreCase("Juices")) {
                categoryList.add("Cold Drinks");
                categoryList.add("Juices");
                categoryList.add("Tea");
                categoryList.add("Mineral Water");
                categoryList.add("Sharbat");
                categoryList.add("Coffee");

                pos = 1;

            } else if (category.equalsIgnoreCase("Tea")) {
                categoryList.add("Cold Drinks");
                categoryList.add("Juices");
                categoryList.add("Tea");
                categoryList.add("Mineral Water");
                categoryList.add("Sharbat");
                categoryList.add("Coffee");

                pos = 2;

            } else if (category.equalsIgnoreCase("Mineral Water")) {
                categoryList.add("Cold Drinks");
                categoryList.add("Juices");
                categoryList.add("Tea");
                categoryList.add("Mineral Water");
                categoryList.add("Sharbat");
                categoryList.add("Coffee");
                pos = 3;

            } else if (category.equalsIgnoreCase("Sharbat")) {
                categoryList.add("Cold Drinks");
                categoryList.add("Juices");
                categoryList.add("Tea");
                categoryList.add("Mineral Water");
                categoryList.add("Sharbat");
                categoryList.add("Coffee");

                pos = 4;

            } else if (category.equalsIgnoreCase("Coffee")) {
                categoryList.add("Cold Drinks");
                categoryList.add("Juices");
                categoryList.add("Tea");
                categoryList.add("Mineral Water");
                categoryList.add("Sharbat");
                categoryList.add("Coffee");

                pos = 5;

            }

        }

        FragmentAdapter adapter = new FragmentAdapter(this, getSupportFragmentManager(), categoryList,"0");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        if (tabScrollable) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {

        }
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

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
        if (item.getItemId() == android.R.id.home) {

            finish();
        }
        if (id == R.id.action_search) {
            Intent i = new Intent(ListOfProducts.this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_cart) {
//            if (SharedPrefs.getIsLoggedIn().equals("yes")) {
            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
                CommonUtils.showToast("Your Cart is empty");
            } else {
                Intent i = new Intent(ListOfProducts.this, Cart.class);
                startActivity(i);
            }
//            } else {
//                Intent i = new Intent(ListOfProducts.this, Login.class);
//                i.putExtra("takeUserToActivity", Constants.CART_ACTIVITY);
//                startActivity(i);
//            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
