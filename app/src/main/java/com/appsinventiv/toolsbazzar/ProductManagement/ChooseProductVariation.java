package com.appsinventiv.toolsbazzar.ProductManagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Models.NewProductModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.CompressImageToThumnail;
import com.bumptech.glide.Glide;
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
import java.util.HashMap;
import java.util.List;

public class ChooseProductVariation extends AppCompatActivity {
    public static HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
    RelativeLayout color, size;
    DatabaseReference mDatabase;
    ArrayList<String> colorList = new ArrayList<>();
    ArrayList<String> sizeList = new ArrayList<>();
    public static TextView colorSubtitle, sizeSubtitle;
    LinearLayout layout, skuInfo;
    HashMap<String, View> viewMap = new HashMap<>();
    private static final int REQUEST_CODE_CHOOSE = 23;
    private List<Uri> mSelected = new ArrayList<>();
    String colorSelecred = "";
    public static HashMap<String, Object> uploadedMap = new HashMap<>();
    HorizontalScrollView hori;
    HashMap<String, NewProductModel> newProductModelHashMap = new HashMap<>();
    public static HashMap<String, Object> hashMapHashMap = new HashMap<>();

    Button ok;

    String colorss, sizess;

    @Override
    protected void onResume() {
        super.onResume();
        if (hashMap.get("size") != null && hashMap.get("size").size() > 0
                && hashMap.get("color") != null && hashMap.get("color").size() > 0) {
            addSKULayout();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_product_variation);
        this.setTitle("Choose Product Variation");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        hori = findViewById(R.id.hori);
        ok = findViewById(R.id.ok);
        sizeSubtitle = findViewById(R.id.sizeSubtitle);
        colorSubtitle = findViewById(R.id.colorSubtitle);
        skuInfo = findViewById(R.id.skuInfo);
        layout = findViewById(R.id.layout);
        size = findViewById(R.id.size);
        color = findViewById(R.id.color);
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorAlert();
            }
        });

        size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showSizeAlert();
                startActivity(new Intent(ChooseProductVariation.this, ChooseSiz.class));
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                CommonUtils.showToast("" + hashMapHashMap);
//                hashMapHashMap.put(hashMap.get("color").get(finalJ), newProductModelHashMap);
                finish();
            }
        });

        getSizeFromDB();


    }

    private void getSizeFromDB() {
        mDatabase.child("Settings").child("Attributes").child("SubSubAttributes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.child("Color").getChildren()) {
                        String abc = snapshot.getValue(String.class);
                        colorList.add(abc);
                    }
                    for (DataSnapshot snapshot : dataSnapshot.child("Sizes").getChildren()) {
                        String abc = snapshot.getValue(String.class);
                        sizeList.add(abc);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showSizeAlert() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose sizes");

// add a checkbox list
        final String[] animals = sizeList.toArray(new String[0]);

        boolean[] checkedItems = new boolean[sizeList.size()];
        final ArrayList<String> checked = new ArrayList<>();
        builder.setMultiChoiceItems(animals, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box
                if (isChecked) {
                    checked.add(animals[which]);
                } else {
                    checked.remove(animals[which]);
                }
                hashMap.put("size", checked);
            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
//                CommonUtils.showToast("" + hashMap);
                sizeSubtitle.setText("" + hashMap.get("size"));
                dialog.dismiss();
                if (hashMap.get("size") != null && hashMap.get("size").size() > 0
                        && hashMap.get("color") != null && hashMap.get("color").size() > 0) {
                    addSKULayout();
                }

            }
        });
        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addSKULayout() {
        final LinearLayout options_layout = (LinearLayout) findViewById(R.id.skuInfo);
        options_layout.removeAllViews();
        for (int j = 0; j < hashMap.get("color").size(); j++) {
            for (int k = 0; k < hashMap.get("size").size(); k++) {


                final LayoutInflater inflater = LayoutInflater.from(this);
                final View to_add = inflater.inflate(R.layout.product_sku_layout,
                        options_layout, false);
                ImageView delete = to_add.findViewById(R.id.delete);
                TextView color = to_add.findViewById(R.id.color);
                TextView size = to_add.findViewById(R.id.size);
                color.setText(hashMap.get("color").get(j));
                size.setText(hashMap.get("size").get(k));


                Button setValues = to_add.findViewById(R.id.setValues);
                EditText sku = to_add.findViewById(R.id.sku);
                EditText quantityAvailable = to_add.findViewById(R.id.quantityAvailable);
                EditText wholesalePrice = to_add.findViewById(R.id.wholesalePrice);
                EditText oldWholeSalePrice = to_add.findViewById(R.id.oldWholeSalePrice);
                EditText minOrder = to_add.findViewById(R.id.minOrder);
                EditText retailPrice = to_add.findViewById(R.id.retailPrice);
                EditText oldRetailPrice = to_add.findViewById(R.id.oldRetailPrice);

                final NewProductModel newProductsModel = new NewProductModel();
                newProductsModel.setColor(hashMap.get("color").get(j));
                newProductsModel.setSize(hashMap.get("size").get(k));

                sku.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        newProductsModel.setSku(editable.toString());
                    }
                });
                quantityAvailable.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() > 0) {
                            newProductsModel.setQty(Integer.parseInt(editable.toString()));
                        }
                    }
                });
                wholesalePrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() > 0) {
                            newProductsModel.setWholesalePrice(Integer.parseInt(editable.toString()));
                        }
                    }
                });
                oldWholeSalePrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() > 0) {
                            newProductsModel.setOldWholesalePrice(Integer.parseInt(editable.toString()));
                        }
                    }
                });
                minOrder.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() > 0) {
                            newProductsModel.setMinOrderQuantity(Integer.parseInt(editable.toString()));
                        }
                    }
                });
                retailPrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() > 0) {
                            newProductsModel.setRetailPrice(Integer.parseInt(editable.toString()));
                        }
                    }
                });
                oldRetailPrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() > 0) {
                            newProductsModel.setOldRetailPrice(Integer.parseInt(editable.toString()));
                        }
//

                    }
                });

                final int finalK = k;
                final int finalJ = j;
                setValues.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NewProductModel newProductsModel1 = new NewProductModel(newProductsModel);
                        newProductModelHashMap.put(hashMap.get("size").get(finalK), newProductsModel1);
                        hashMapHashMap.put(hashMap.get("color").get(finalJ), newProductModelHashMap);
                        CommonUtils.showToast("Value set");
                    }
                });


                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        options_layout.removeView(to_add);
                        newProductModelHashMap.remove(hashMap.get("color"));
                    }
                });

                options_layout.addView(to_add);


            }
        }
        hori.setVisibility(View.VISIBLE);

    }

    private void showColorAlert() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose color");

// add a checkbox list
        final String[] animals = colorList.toArray(new String[0]);

        boolean[] checkedItems = new boolean[colorList.size()];


        final ArrayList<String> checked = new ArrayList<>();
        builder.setMultiChoiceItems(animals, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box
                if (isChecked) {
                    checked.add(animals[which]);
                } else {
                    checked.remove(animals[which]);
                }
                hashMap.put("color", checked);

            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
//                CommonUtils.showToast("" + hashMap);
                colorSubtitle.setText("" + hashMap.get("color"));
                dialog.dismiss();
                addLayouts();
                if (hashMap.get("size") != null && hashMap.get("size").size() > 0
                        && hashMap.get("color") != null && hashMap.get("color").size() > 0) {
                    addSKULayout();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addLayouts() {
        LinearLayout options_layout = (LinearLayout) findViewById(R.id.layout);
        options_layout.removeAllViews();
        for (final String color : hashMap.get("color")) {


            final LayoutInflater inflater = LayoutInflater.from(this);
            View to_add = inflater.inflate(R.layout.product_variation_layout_pics,
                    options_layout, false);
            TextView colorName = to_add.findViewById(R.id.colorName);
            Button selectImage = to_add.findViewById(R.id.selectImage);
            final ImageView image = to_add.findViewById(R.id.image);
            ImageView delete = to_add.findViewById(R.id.delete);
            colorName.setText(color);
            options_layout.addView(to_add);
            viewMap.put(color, to_add);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadedMap.remove(color);
                    Glide.with(ChooseProductVariation.this).load(R.drawable.fort_city_without_green).into(image);
                }
            });
            selectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    CommonUtils.showToast(color);
                    colorSelecred = color;
                    mSelected.clear();
                    initMatisse();
                }
            });


        }

    }

    public void putPictures(String imgUrl, final String key) {
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        Uri file = Uri.fromFile(new File(imgUrl));
        StorageReference mStorageRef;

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        uploadedMap.put(key, "" + downloadUrl);
                        CommonUtils.showToast("Uploaded" + key);


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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE) {
            if (data != null) {
                if (Matisse.obtainResult(data) != null) {
                    mSelected = Matisse.obtainResult(data);
                    View abc = viewMap.get(colorSelecred);
                    ImageView abaa = abc.findViewById(R.id.image);
                    Glide.with(this).load(mSelected.get(0)).into(abaa);
                    CompressImageToThumnail compressImageToThumnail = new CompressImageToThumnail(this);
                    String url = compressImageToThumnail.compressImage("" + mSelected.get(0));
                    putPictures(url, colorSelecred);
                }
            }

        }
    }

    private void initMatisse() {
        Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
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


}
