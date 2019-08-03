package com.appsinventiv.toolsbazzar.Seller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.ChooseMainCategory;
import com.appsinventiv.toolsbazzar.Activities.LiveChat;
import com.appsinventiv.toolsbazzar.Interface.NotificationObserver;
import com.appsinventiv.toolsbazzar.Interfaces.ProductObserver;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerChat.SellerChats;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.CompressImage;
import com.appsinventiv.toolsbazzar.Utils.NotificationAsync;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
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
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SellerAddProduct extends AppCompatActivity implements ProductObserver, NotificationObserver {
    TextView categoryChoosen;
    StorageReference mStorageRef;
    DatabaseReference mDatabase;
    Button pick, upload;
    private static final int REQUEST_CODE_CHOOSE = 23;
    List<Uri> mSelected = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    SelectedImagesAdapter adapter;
    Bundle extras;
    ArrayList<SelectedAdImages> selectedAdImages = new ArrayList<>();
    ArrayList<String> imageUrl = new ArrayList<>();

    String imagePath;
    EditText e_title, e_sku, e_subtitle, e_costPrice, e_wholesalePrice,
            e_retailPrice, e_minOrderQty, e_measurement, e_sizes, e_colors, e_description,
            e_oldRetailPrice, e_oldWholesalePrice, e_quantityAvailable;
    String productId;
    ProgressBar progressBar;
    ArrayList<VendorModel> vendorModelArrayList = new ArrayList<>();
    VendorModel vendor;
    ProductObserver observer;
    ArrayList<Integer> skuList = new ArrayList<>();
    long newSku = 10001;
    RadioGroup radioGroup;
    RadioButton selected;
    public static ArrayList<String> categoryList = new ArrayList<>();
    EditText brandName, productContents;
    TextView warrantyChosen, weightChosen;
    private String whichWarranty;
    public static String productWeight, dimens;
    public static int fromWhere = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_product);
        this.setTitle("Add Product");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        observer = SellerAddProduct.this;
//       CommonUtils.showToast( SharedPrefs.getVendor().getName()+"");
        getPermissions();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        categoryChoosen = findViewById(R.id.categoryChoosen);
        categoryChoosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromWhere = 1;
                Intent i = new Intent(SellerAddProduct.this, ChooseMainCategory.class);
                categoryList.clear();
                startActivityForResult(i, 1);

            }
        });
        pick = findViewById(R.id.pick);
        upload = findViewById(R.id.upload);
        e_title = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerview);
        e_subtitle = findViewById(R.id.subtitle);
        e_costPrice = findViewById(R.id.costPrice);
        e_wholesalePrice = findViewById(R.id.wholeSalePrice);
        e_retailPrice = findViewById(R.id.retailPrice);
        e_minOrderQty = findViewById(R.id.minOrder);
        e_measurement = findViewById(R.id.measurement);
        e_sku = findViewById(R.id.productSku);
        progressBar = findViewById(R.id.prgress);
        radioGroup = findViewById(R.id.radioGroup);
        e_description = findViewById(R.id.description);
        e_sizes = findViewById(R.id.size);
        e_colors = findViewById(R.id.color);
        e_oldWholesalePrice = findViewById(R.id.oldWholeSalePrice);
        e_oldRetailPrice = findViewById(R.id.oldRetailPrice);
        e_quantityAvailable = findViewById(R.id.quantityAvailable);
        brandName = findViewById(R.id.brandName);
        productContents = findViewById(R.id.productContents);
        weightChosen = findViewById(R.id.weightChosen);
        warrantyChosen = findViewById(R.id.warrantyChosen);

        warrantyChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarrantyAlert();
            }
        });

        weightChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellerAddProduct.this, AddProductWeight.class);
                startActivity(i);
            }
        });

        showPickedPictures();

        getSKUFromDb();


        e_wholesalePrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    /* Write your logic here that will be executed when user taps next button */
                    e_oldWholesalePrice.requestFocus();

                    handled = true;
                }

                return handled;
            }
        });
        e_oldWholesalePrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    /* Write your logic here that will be executed when user taps next button */
                    e_retailPrice.requestFocus();

                    handled = true;
                }

                return handled;
            }
        });
        e_retailPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    /* Write your logic here that will be executed when user taps next button */
                    e_oldRetailPrice.requestFocus();

                    handled = true;
                }

                return handled;
            }
        });


        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelected != null) {
                    mSelected.clear();
                    selectedAdImages.clear();
                    imageUrl.clear();
                }
                Matisse.from(SellerAddProduct.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(5)
                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (categoryList.size() == 0) {
                    CommonUtils.showToast("Please select category");
                } else if (e_title.getText().length() == 0) {
                    e_title.setError("Enter title");
                } else if (e_subtitle.getText().length() == 0) {
                    e_subtitle.setError("Enter subtitle");
                } else if (e_costPrice.getText().length() == 0) {
                    e_costPrice.setError("Enter price");
                } else if (e_wholesalePrice.getText().length() == 0) {
                    e_wholesalePrice.setError("Enter whole sale price");
                } else if (e_retailPrice.getText().length() == 0) {
                    e_retailPrice.setError("Enter price");
                } else if (e_quantityAvailable.getText().length() == 0) {
                    e_quantityAvailable.setError("Enter quantity");
                } else if (whichWarranty == null) {
                    CommonUtils.showToast("Please select warranty type");
                } else if (productWeight == null) {
                    CommonUtils.showToast("Please select product weight");
                } else if (mSelected.size() == 0) {
                    CommonUtils.showToast("Please select image");
                } else {
                    List<String> container = new ArrayList<>();
                    if (e_sizes.getText().length() > 0) {
                        String[] sizes = e_sizes.getText().toString().split(",");
                        container = Arrays.asList(sizes);

                    }
                    List<String> container1 = new ArrayList<>();

                    if (e_colors.getText().length() > 0) {
                        String[] colors = e_colors.getText().toString().split(",");
                        container1 = Arrays.asList(colors);

                    }


                    progressBar.setVisibility(View.VISIBLE);
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    selected = findViewById(selectedId);
                    productId = mDatabase.push().getKey();
                    mDatabase.child("Products").child(productId).setValue(new Product(
                            productId,
                            e_title.getText().toString(),
                            e_subtitle.getText().toString(),
                            "true",
                            Integer.parseInt("" + newSku),
                            "",
                            "",
                            "",
                            System.currentTimeMillis(),
                            Float.parseFloat(e_costPrice.getText().toString()),
                            Float.parseFloat(e_wholesalePrice.getText().toString()),
                            Float.parseFloat(e_retailPrice.getText().toString()),
                            Integer.parseInt(e_minOrderQty.getText().length() > 0 ? e_minOrderQty.getText().toString() : "" + 1),
                            e_measurement.getText().toString(),
                            SharedPrefs.getVendor(),
                            selected.getText().toString(),
                            e_description.getText().toString(),
                            container,
                            container1,
                            Float.parseFloat(e_oldWholesalePrice.getText().length() > 0 ? e_oldWholesalePrice.getText().toString() : "" + 0),
                            Float.parseFloat(e_oldRetailPrice.getText().length() > 0 ? e_oldRetailPrice.getText().toString() : "" + 0),
                            0,
                            categoryList, Integer.parseInt(e_quantityAvailable.getText().toString()),
                            brandName.getText().toString(),
                            productContents.getText().toString(),
                            whichWarranty,
                            productWeight,
                            dimens, "Pending",
                            "seller",
                            0


                    )).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabase.child("Sellers").child(SharedPrefs.getUsername()).child("products").child(productId).setValue(productId);
                            int count = 0;
                            NotificationAsync notificationAsync = new NotificationAsync(SellerAddProduct.this);
                            String NotificationTitle = "New product uploaded by " + SharedPrefs.getUsername();
                            String NotificationMessage = "Product: " + e_title.getText().toString();
                            notificationAsync.execute("ali", SharedPrefs.getAdminFcmKey(), NotificationTitle, NotificationMessage, "NewSellerProduct", "");
                            for (String img : imageUrl) {

                                putPictures(img, "" + productId, count);
                                count++;
                                observer.onUploaded(count, imageUrl.size());

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

            }
        });


    }

    private void showWarrantyAlert() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(SellerAddProduct.this);
        builderSingle.setTitle("Select warranty type");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SellerAddProduct.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("International Manufacture Warranty");
        arrayAdapter.add("International Seller Warranty");
        arrayAdapter.add("Local Seller Warranty");
        arrayAdapter.add("No Warranty");
        arrayAdapter.add("Non-local Warranty");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                warrantyChosen.setText("Warranty chosen: " + arrayAdapter.getItem(which));
                whichWarranty = arrayAdapter.getItem(which);

            }
        });
        builderSingle.show();
    }


    private void getSKUFromDb() {
        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long abc = Integer.parseInt("" + dataSnapshot.getChildrenCount());

                    newSku = newSku + abc;

                    e_sku.setText("" + newSku);

                } else {
                    e_sku.setText("" + newSku);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void showPickedPictures() {
        selectedAdImages = new ArrayList<>();
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(SellerAddProduct.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        adapter = new SelectedImagesAdapter(SellerAddProduct.this, selectedAdImages, new SelectedImagesAdapter.ChooseOption() {
            @Override
            public void onDeleteClicked(SelectedAdImages images, int position) {
                selectedAdImages.remove(position - 1);
                imageUrl.remove(position - 1);
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);
    }


    public void putPictures(String path, final String key, final int count) {
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
                        observer.putThumbnailUrl(count, "" + downloadUrl);
                        mDatabase.child("Products").child(productId).child("pictures").child("" + count).setValue("" + downloadUrl);


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

    @Override
    protected void onResume() {
        super.onResume();
        if (productWeight != null) {
            weightChosen.setText("Weight: " + productWeight + "Kg");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        CommonUtils.showToast(categoryList + "");
        categoryChoosen.setText("Category: " + categoryList);
        selectedAdImages.clear();
        if (data != null) {
            if (requestCode == REQUEST_CODE_CHOOSE) {
                recyclerView.setVisibility(View.VISIBLE);

                mSelected = Matisse.obtainResult(data);
                for (Uri img :
                        mSelected) {
                    selectedAdImages.add(new SelectedAdImages("" + img));
                    adapter.notifyDataSetChanged();
                    CompressImage compressImage = new CompressImage(SellerAddProduct.this);
                    imageUrl.add(compressImage.compressImage("" + img));
                }
//                CompressImage compressImage = new CompressImage(this);
//                imagePath = compressImage.compressImage("" + mSelected.get(0));
            }
            if (requestCode == 1) {
                extras = data.getExtras();

//                if (extras.getString("subCategory") != null) {
////                    categoryChoosen.setText("Category: " + extras.getString("subCategory"));
//
//
//                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onUploaded(int count, int arraySize) {
        if (count == arraySize) {
            Intent i = new Intent(SellerAddProduct.this, ProductUploaded.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void putThumbnailUrl(int count, String url) {
        if (count == 0) {
            mDatabase.child("Products").child(productId).child("thumbnailUrl").setValue(url);
        }
    }

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}
