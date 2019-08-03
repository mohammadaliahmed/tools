package com.appsinventiv.toolsbazzar.Seller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.appsinventiv.toolsbazzar.Interface.NotificationObserver;
import com.appsinventiv.toolsbazzar.Interfaces.ProductObserver;

import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.Models.VendorModel;
import com.appsinventiv.toolsbazzar.R;
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

public class EditProduct extends AppCompatActivity implements ProductObserver ,NotificationObserver {
    TextView categoryChoosen;
    StorageReference mStorageRef;
    DatabaseReference mDatabase;
    Button pick, upload;
    private static final int REQUEST_CODE_CHOOSE = 23;
    List<Uri> mSelected;
    RecyclerView recyclerView;
    SelectedImagesAdapter adapter;
    Bundle extras;
    ArrayList<SelectedAdImages> selectedAdImages = new ArrayList<>();
    ArrayList<String> imageUrl = new ArrayList<>();
    EditText e_title, e_sku, e_subtitle, e_costPrice, e_wholesalePrice,
            e_retailPrice, e_minOrderQty, e_measurement, e_sizes, e_colors, e_description,
            e_oldRetailPrice, e_oldWholesalePrice, quantityAvailable;
    String productId;
    ProgressBar progressBar;
    //    Spinner spinner;
    ArrayList<VendorModel> vendorModelArrayList = new ArrayList<>();
    VendorModel vendor;
    ProductObserver observer;
    int newSku = 10001;
    RadioGroup radioGroup;
    RadioButton selected;
    Product product;
    public static ArrayList<String> categoryList = new ArrayList<>();
    EditText brandName, productContents;
    TextView warrantyChosen, weightChosen;
    private String whichWarranty;
    public static String productWeight, dimens;
    TextView productIdd;
    public static int fromWhere = 0;

    @Override
    protected void onResume() {
        super.onResume();
        if (productWeight != null) {
            weightChosen.setText("Weight: " + productWeight + "Kg");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Edit Product");
        Intent i = getIntent();
        productId = i.getStringExtra("productId");

        observer = EditProduct.this;

        getPermissions();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        categoryChoosen = findViewById(R.id.categoryChoosen);
        categoryChoosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromWhere = 1;
                Intent i = new Intent(EditProduct.this, ChooseMainCategory.class);
                categoryList.clear();
                startActivityForResult(i, 1);

            }
        });
        pick = findViewById(R.id.pick);
        productIdd = findViewById(R.id.productId);
        upload = findViewById(R.id.upload);
        recyclerView = findViewById(R.id.recyclerview);
        e_title = findViewById(R.id.title);
        e_subtitle = findViewById(R.id.subtitle);
        e_costPrice = findViewById(R.id.costPrice);
        e_wholesalePrice = findViewById(R.id.wholeSalePrice);
        e_retailPrice = findViewById(R.id.retailPrice);
        e_minOrderQty = findViewById(R.id.minOrder);
        e_measurement = findViewById(R.id.measurement);
        e_sku = findViewById(R.id.productSku);
        e_description = findViewById(R.id.description);
        e_sizes = findViewById(R.id.size);
        e_colors = findViewById(R.id.color);
        e_oldWholesalePrice = findViewById(R.id.oldWholeSalePrice);
        e_oldRetailPrice = findViewById(R.id.oldRetailPrice);
        progressBar = findViewById(R.id.prgress);
//        spinner = findViewById(R.id.chooseVendor);
        radioGroup = findViewById(R.id.radioGroup);
        quantityAvailable = findViewById(R.id.quantityAvailable);
        brandName = findViewById(R.id.brandName);
        productContents = findViewById(R.id.productContents);
        weightChosen = findViewById(R.id.weightChosen);
        warrantyChosen = findViewById(R.id.warrantyChosen);
        productIdd.setText("Product Id: " + productId);

        warrantyChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarrantyAlert();
            }
        });

        weightChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditProduct.this, AddProductWeight.class);
                startActivity(i);
            }
        });


        showPickedPictures();

        getDataFromServer();
        getVendorsFromDb();
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
                Matisse.from(EditProduct.this)
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
                if (product.getCategory() == null && categoryList.size() == 0) {
                    CommonUtils.showToast("Please select category");
                } else if (e_title.getText().length() == 0) {
                    e_title.setError("Enter title");
                } else if (e_subtitle.getText().length() == 0) {
                    e_subtitle.setError("Enter subtitle");
                } else if (e_costPrice.getText().length() == 0) {
                    e_costPrice.setError("Enter price");
                } else {
                    List<String> container = new ArrayList<>();
                    if (e_sizes.getText().length() > 0) {
                        String[] sizes = e_sizes.getText().toString().replace("[", "").replace("]", "").replace(" ", "").split(",");
                        container = Arrays.asList(sizes);

                    }
                    List<String> container1 = new ArrayList<>();

                    if (e_colors.getText().length() > 0) {
                        String[] colors = e_colors.getText().toString().replace("[", "").replace("]", "").replace(" ", "").split(",");
                        container1 = Arrays.asList(colors);

                    }


                    progressBar.setVisibility(View.VISIBLE);
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    selected = findViewById(selectedId);
                    mDatabase.child("Products").child(productId).setValue(new Product(
                            productId,
                            e_title.getText().toString(),
                            e_subtitle.getText().toString(),
                            "true",
                            newSku,
                            product.getThumbnailUrl(),
                            "",
                            "",
                            product.getTime(),
                            Float.parseFloat(e_costPrice.getText().toString()),
                            Float.parseFloat(e_wholesalePrice.getText().toString()),
                            Float.parseFloat(e_retailPrice.getText().toString()),
                            Integer.parseInt(e_minOrderQty.getText().toString()),
                            e_measurement.getText().toString(),
                            SharedPrefs.getVendor(),
                            selected.getText().toString(),
                            e_description.getText().toString(),
                            container,
                            container1,
                            Float.parseFloat(e_oldWholesalePrice.getText().toString()),
                            Float.parseFloat(e_oldRetailPrice.getText().toString()),
                            product.getRating(),
                            categoryList,
                            Integer.parseInt(quantityAvailable.getText().toString()),
                            brandName.getText().toString(),
                            productContents.getText().toString(),

                            whichWarranty,
                            productWeight,
                            dimens,
                            "Pending",
                            product.getUploadedBy() == null ? "Admin" : product.getUploadedBy(),
                            product.getLikesCount()

                    )).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            int count = 0;
                            NotificationAsync notificationAsync = new NotificationAsync(EditProduct.this);
                            String NotificationTitle = "Product was edited " + SharedPrefs.getUsername();
                            String NotificationMessage = "Click to view";
                            notificationAsync.execute("ali", SharedPrefs.getAdminFcmKey(), NotificationTitle, NotificationMessage, "NewSellerProduct", "");

                            if (imageUrl.size() != 0) {

                                for (String img : imageUrl) {

                                    putPictures(img, "" + productId, count);
                                    count++;
                                    observer.onUploaded(count, imageUrl.size());

                                }
                            } else {

                                putPicturesBack();
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
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(EditProduct.this);
        builderSingle.setTitle("Select warranty type");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditProduct.this, android.R.layout.simple_list_item_1);
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

    private void putPicturesBack() {

        mDatabase.child("Products").child(productId).child("pictures")
                .setValue(product.getPictures()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Product updated");
                finish();
            }
        });
    }

    private void getDataFromServer() {
        mDatabase.child("Products").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        e_title.setText(product.getTitle());
                        e_subtitle.setText(product.getSubtitle() + "");
                        e_costPrice.setText(product.getCostPrice() + "");
                        e_wholesalePrice.setText(product.getWholeSalePrice() + "");
                        e_retailPrice.setText(product.getRetailPrice() + "");
                        e_minOrderQty.setText("" + product.getMinOrderQuantity());
                        e_measurement.setText(product.getMeasurement());
                        e_sku.setText("" + product.getSku());
                        e_description.setText(product.getDescription());
                        quantityAvailable.setText("" + product.getQuantityAvailable());
                        if (product.getSizeList() != null) {
                            e_sizes.setText("" + product.getSizeList());
                        }
                        if (product.getColorList() != null) {
                            e_colors.setText("" + product.getColorList());
                        }

                        e_oldWholesalePrice.setText("" + product.getOldWholeSalePrice());
                        e_oldRetailPrice.setText("" + product.getOldRetailPrice());
                        newSku = product.getSku();
                        if (product.getPictures() != null) {
                            for (int i = 0; i < product.getPictures().size(); i++) {
                                selectedAdImages.add(new SelectedAdImages(product.getPictures().get(i)));
                            }
                        }
                        recyclerView.setVisibility(View.VISIBLE);

                        adapter.notifyDataSetChanged();


                        warrantyChosen.setText("Warranty chosen: " + product.getWarrantyType() == null ? "" : product.getWarrantyType());
                        whichWarranty = product.getWarrantyType();
                        productContents.setText(product.getProductContents());
                        brandName.setText(product.getBrandName());
                        weightChosen.setText(product.getProductWeight() == null ? "Choose weight" : "Product Weight: " + product.getProductWeight());
                        if (product.getCategory() != null) {
                            categoryList = product.getCategory();
                            categoryChoosen.setText("Category: " + product.getCategory());
                        }

                    }
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
                = new LinearLayoutManager(EditProduct.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        adapter = new SelectedImagesAdapter(EditProduct.this, selectedAdImages, new SelectedImagesAdapter.ChooseOption() {
            @Override
            public void onDeleteClicked(SelectedAdImages images, int position) {
                selectedAdImages.remove(position - 1);
                product.getPictures().remove(position - 1);
                adapter.notifyDataSetChanged();

            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getVendorsFromDb() {
        mDatabase.child("Vendors").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot != null) {
                            VendorModel model = snapshot.getValue(VendorModel.class);
                            if (model != null) {
                                vendorModelArrayList.add(model);
//                                setUpSpinner();
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonUtils.showToast(categoryList + "");
        categoryChoosen.setText("Category: " + categoryList);
        selectedAdImages.clear();
        if (data != null) {
            if (requestCode == REQUEST_CODE_CHOOSE) {
                recyclerView.setVisibility(View.VISIBLE);

                mSelected = Matisse.obtainResult(data);
                for (Uri img : mSelected) {
                    selectedAdImages.add(new SelectedAdImages("" + img));
                    adapter.notifyDataSetChanged();
                    CompressImage compressImage = new CompressImage(EditProduct.this);
                    imageUrl.add(compressImage.compressImage("" + img));
                }

            }
            if (requestCode == 1) {
                extras = data.getExtras();
                if (extras.getString("subCategory") != null) {
                    categoryChoosen.setText("Category: " + extras.getString("subCategory"));

                }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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

    @Override
    public void onUploaded(int count, int arraySize) {
        if (count == arraySize) {
            Intent i = new Intent(EditProduct.this, ProductUploaded.class);
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
