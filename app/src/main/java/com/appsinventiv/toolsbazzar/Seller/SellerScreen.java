package com.appsinventiv.toolsbazzar.Seller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.AboutUs;
import com.appsinventiv.toolsbazzar.Activities.AccountIsDisabled;
import com.appsinventiv.toolsbazzar.Activities.ChooseMainCategory;
import com.appsinventiv.toolsbazzar.Activities.Login;
import com.appsinventiv.toolsbazzar.Activities.NewCart;
import com.appsinventiv.toolsbazzar.Activities.Search;
import com.appsinventiv.toolsbazzar.Activities.Splash;
import com.appsinventiv.toolsbazzar.Activities.Welcome;
import com.appsinventiv.toolsbazzar.Activities.Whishlist;
import com.appsinventiv.toolsbazzar.Adapters.MainSliderAdapter;
import com.appsinventiv.toolsbazzar.Adapters.SellerFragmentAdapter;
import com.appsinventiv.toolsbazzar.Models.AdminModel;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.Sales.SellerSales;
import com.appsinventiv.toolsbazzar.Seller.SellerChat.SellerChats;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.Orders;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.OrdersCourier;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.CompressImage;
import com.appsinventiv.toolsbazzar.Utils.PrefManager;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class SellerScreen extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 24;
    private static final int REQUEST_CODE_CHOOSE_COVER = 25;
    Toolbar toolbar;
    ImageView back;
    ImageView storeCover;
    CircleImageView profilePic;
    ImageView pickProfilePic;
    Button changeCover;
    StorageReference mStorageRef;
    List<Uri> mSelected1 = new ArrayList<>();
    List<Uri> mSelected2 = new ArrayList<>();

    ArrayList<String> imageUrl1 = new ArrayList<>();
    ArrayList<String> imageUrl2 = new ArrayList<>();
    Button update;
    TextView name;

    DatabaseReference mDatabase;
    RelativeLayout wholeLayout,orders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_screen);

        back = findViewById(R.id.back);
        changeCover = findViewById(R.id.changeCover);
        pickProfilePic = findViewById(R.id.pickProfilePic);
        profilePic = findViewById(R.id.profilePic);
        storeCover = findViewById(R.id.storeCover);
        wholeLayout = findViewById(R.id.wholeLayout);
        name = findViewById(R.id.name);
        orders = findViewById(R.id.orders);
        update = findViewById(R.id.update);
        transparentToolbar();
        getPermissions();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUrl1.size() > 0) {
                    putProfilePic(imageUrl1.get(0));
                }
                if (imageUrl2.size() > 0) {
                    putCoverPic(imageUrl2.get(0));
                }
            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerScreen.this,Orders.class));
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        changeCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initCoverMatisse();

                } catch (Exception e) {
                    CommonUtils.showToast(e.getMessage());
                }
            }
        });
        pickProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initProfileMatisse();

                } catch (Exception e) {
                    CommonUtils.showToast(e.getMessage());
                }
            }
        });

        getVendorDataFromDB();

    }

    private void getVendorDataFromDB() {
        mDatabase.child("Sellers").child(SharedPrefs.getVendor().getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    wholeLayout.setVisibility(View.GONE);
                    VendorModel model = dataSnapshot.getValue(VendorModel.class);
                    if (model != null) {
                        name.setText(model.getStoreName());
                        if (model.getPicUrl() != null) {
                            Glide.with(SellerScreen.this).load(model.getPicUrl()).into(profilePic);

                        }
                        if (model.getStoreCover() != null) {
                            Glide.with(SellerScreen.this).load(model.getStoreCover()).into(new SimpleTarget<GlideDrawable>() {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        storeCover.setBackground(resource);
                                    }
                                }
                            });
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initProfileMatisse() {
        Matisse.from(SellerScreen.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private void initCoverMatisse() {
        Matisse.from(SellerScreen.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE_COVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == REQUEST_CODE_CHOOSE) {
                mSelected1 = Matisse.obtainResult(data);
                Glide.with(SellerScreen.this).load(mSelected1.get(0)).into(profilePic);
                for (Uri img : mSelected1) {
                    CompressImage compressImage = new CompressImage(SellerScreen.this);
                    imageUrl1.add(compressImage.compressImage("" + img));
                }

            }
            if (requestCode == REQUEST_CODE_CHOOSE_COVER) {
                mSelected2 = Matisse.obtainResult(data);

                Glide.with(SellerScreen.this).load(mSelected2.get(0)).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            storeCover.setBackground(resource);
                        }
                    }
                });
                mSelected2 = Matisse.obtainResult(data);
                for (Uri img : mSelected2) {
                    CompressImage compressImage = new CompressImage(SellerScreen.this);
                    imageUrl2.add(compressImage.compressImage("" + img));
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void putProfilePic(String path) {
        wholeLayout.setVisibility(View.VISIBLE);
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        Uri file = Uri.fromFile(new File(path));

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mDatabase.child("Sellers").child(SharedPrefs.getVendor().getUsername()).child("picUrl").setValue("" + downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Profile pic changed successfully");
                                wholeLayout.setVisibility(View.GONE);
                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        CommonUtils.showToast("There was some error uploading pic");

                    }
                });


    }


    public void putCoverPic(String path) {
        wholeLayout.setVisibility(View.VISIBLE);
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        Uri file = Uri.fromFile(new File(path));

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mDatabase.child("Sellers").child(SharedPrefs.getVendor().getUsername()).child("storeCover").setValue("" + downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Cover changed successfully");
                                wholeLayout.setVisibility(View.GONE);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        CommonUtils.showToast("There was some error uploading pic");

                    }
                });


    }

    private void transparentToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
