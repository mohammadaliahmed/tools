package com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement;

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
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.ProductManagement.ChooseSiz;

import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.CompressImageToThumnail;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
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
import java.util.Map;

public class EditProductVariation extends AppCompatActivity {
    public static HashMap<String, ArrayList<String>> edithashMap = new HashMap<>();
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
    public static HashMap<String, String> uploadedMap = new HashMap<>();
    HorizontalScrollView hori;
    HashMap<String, NewProductModel> newProductModelHashMap = new HashMap<>();
    public static HashMap<String, Object> hashMapHashMap = new HashMap<>();

    Button ok;

    String colorss, sizess;
    Product product = SharedPrefs.getProduct();
    private ArrayList<String> colorArray = new ArrayList<>();
    private ArrayList<String> sizeArray = new ArrayList<>();
    HashMap<String, NewProductModel> anotherMap = new HashMap<>();


    @Override
    protected void onResume() {
        super.onResume();
        if (edithashMap.get("size") != null && edithashMap.get("size").size() > 0
                && edithashMap.get("color") != null && edithashMap.get("color").size() > 0) {
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

        uploadedMap = product.getAttributesWithPics();
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
                startActivity(new Intent(EditProductVariation.this, ChooseSiz.class));
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hashMapHashMap.clear();
                for (Map.Entry<String, NewProductModel> entry : anotherMap.entrySet()) {
                    hashMapHashMap.put(entry.getValue().getColor(), anotherMap);
                }
                finish();
            }
        });

        getSizeFromDB();
        edithashMap.clear();

        if (product.getProductCountHashmap() != null && product.getProductCountHashmap().size() > 0) {
            sizeArray = new ArrayList<>();
            colorArray = new ArrayList<>();
            for (Map.Entry<String, ArrayList<NewProductModel>> entry : product.getProductCountHashmap().entrySet()) {

                for (NewProductModel model : entry.getValue()) {
//                    if(model.get)
                    if (!sizeArray.contains(model.getSize())) {
                        sizeArray.add(model.getSize());

                    }
                    if (!colorArray.contains(model.getColor())) {
                        colorArray.add(model.getColor());
                    }
                    newProductModelHashMap.put(model.getColor() + model.getSize(), model);
                }


            }
            edithashMap.put("size", sizeArray);
            edithashMap.put("color", colorArray);
            addSKULayout();
        }


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


    private void addSKULayout() {
        addLayouts();
        final LinearLayout options_layout = (LinearLayout) findViewById(R.id.skuInfo);
        options_layout.removeAllViews();
        if (edithashMap != null) {
            for (int j = 0; j < edithashMap.get("color").size(); j++) {
                for (int k = 0; k < edithashMap.get("size").size(); k++) {


                    final LayoutInflater inflater = LayoutInflater.from(this);
                    final View to_add = inflater.inflate(R.layout.product_sku_layout,
                            options_layout, false);
                    ImageView delete = to_add.findViewById(R.id.delete);
                    TextView color = to_add.findViewById(R.id.color);
                    TextView size = to_add.findViewById(R.id.size);
                    color.setText(edithashMap.get("color").get(j));
                    size.setText(edithashMap.get("size").get(k));


                    Button setValues = to_add.findViewById(R.id.setValues);
                    final EditText sku = to_add.findViewById(R.id.sku);
                    final EditText quantityAvailable = to_add.findViewById(R.id.quantityAvailable);
                    final EditText wholesalePrice = to_add.findViewById(R.id.wholesalePrice);
                    final EditText oldWholeSalePrice = to_add.findViewById(R.id.oldWholeSalePrice);
                    final EditText minOrder = to_add.findViewById(R.id.minOrder);
                    final EditText retailPrice = to_add.findViewById(R.id.retailPrice);
                    final EditText oldRetailPrice = to_add.findViewById(R.id.oldRetailPrice);

                    final int finalK = k;
                    final int finalJ = j;
                    if (product.getProductCountHashmap() != null && product.getProductCountHashmap().size() > 0) {
                        String colo = edithashMap.get("color").get(finalJ);
                        String si = edithashMap.get("size").get(finalK);
                        if (newProductModelHashMap != null) {
                            if (newProductModelHashMap.get(colo + si) != null) {
                                sku.setText("" + newProductModelHashMap.get(colo + si).getSku());
                                quantityAvailable.setText("" + newProductModelHashMap.get(colo + si).getQty());
                                wholesalePrice.setText("" + newProductModelHashMap.get(colo + si).getWholesalePrice());
                                oldWholeSalePrice.setText("" + newProductModelHashMap.get(colo + si).getOldWholesalePrice());
                                minOrder.setText("" + newProductModelHashMap.get(colo + si).getMinOrderQuantity());
                                retailPrice.setText("" + newProductModelHashMap.get(colo + si).getRetailPrice());
                                oldRetailPrice.setText("" + newProductModelHashMap.get(colo + si).getOldRetailPrice());


                                NewProductModel newProductsModel = new NewProductModel();
                                newProductsModel.setSku(newProductModelHashMap.get(colo + si).getSku());
                                newProductsModel.setQty(newProductModelHashMap.get(colo + si).getQty());
                                newProductsModel.setWholesalePrice(newProductModelHashMap.get(colo + si).getWholesalePrice());
                                newProductsModel.setOldWholesalePrice(newProductModelHashMap.get(colo + si).getOldWholesalePrice());
                                newProductsModel.setMinOrderQuantity(newProductModelHashMap.get(colo + si).getMinOrderQuantity());
                                newProductsModel.setRetailPrice(newProductModelHashMap.get(colo + si).getRetailPrice());
                                newProductsModel.setOldRetailPrice(newProductModelHashMap.get(colo + si).getOldRetailPrice());
                                newProductsModel.setColor(edithashMap.get("color").get(finalJ));
                                newProductsModel.setSize(edithashMap.get("size").get(finalK));

                                newProductModelHashMap.put(edithashMap.get("size").get(finalK), newProductsModel);
                                anotherMap.put(edithashMap.get("color").get(finalJ) + edithashMap.get("size").get(finalK), newProductsModel);
//                        hashMapHashMap.put(hashMap.get("color").get(finalJ), newProductModelHashMap);
//                            CommonUtils.showToast("Value set");
                            }
                        }

                    }
                    setValues.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            NewProductModel newProductsModel = new NewProductModel();
                            newProductsModel.setSku(sku.getText().toString());
                            newProductsModel.setQty(Integer.parseInt(quantityAvailable.getText().length() == 0 ? "1" : quantityAvailable.getText().toString()));
                            newProductsModel.setWholesalePrice(Integer.parseInt(wholesalePrice.getText().length() == 0 ? "1" : wholesalePrice.getText().toString()));
                            newProductsModel.setOldWholesalePrice(Integer.parseInt(oldWholeSalePrice.getText().length() == 0 ? "1" : oldWholeSalePrice.getText().toString()));
                            newProductsModel.setMinOrderQuantity(Integer.parseInt(minOrder.getText().length() == 0 ? "1" : minOrder.getText().toString()));
                            newProductsModel.setRetailPrice(Integer.parseInt(retailPrice.getText().length() == 0 ? "1" : retailPrice.getText().toString()));
                            newProductsModel.setOldRetailPrice(Integer.parseInt(oldRetailPrice.getText().length() == 0 ? "1" : oldRetailPrice.getText().toString()));
                            newProductsModel.setColor(edithashMap.get("color").get(finalJ));
                            newProductsModel.setSize(edithashMap.get("size").get(finalK));


                            newProductModelHashMap.put(edithashMap.get("size").get(finalK), newProductsModel);
                            anotherMap.put(edithashMap.get("color").get(finalJ) + edithashMap.get("size").get(finalK), newProductsModel);
                            CommonUtils.showToast("Value set");
                        }
                    });


                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            options_layout.removeView(to_add);
                            newProductModelHashMap.remove(edithashMap.get("color"));
                        }
                    });

                    options_layout.addView(to_add);


                }
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
                edithashMap.put("color", checked);

            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
//                CommonUtils.showToast("" + hashMap);
                colorSubtitle.setText("" + edithashMap.get("color"));
                dialog.dismiss();
                addLayouts();
                if (edithashMap.get("size") != null && edithashMap.get("size").size() > 0
                        && edithashMap.get("color") != null && edithashMap.get("color").size() > 0) {
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
        for (final String color : edithashMap.get("color")) {


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
            Glide.with(EditProductVariation.this).load(product.getAttributesWithPics().get(color)).into(image);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadedMap.remove(color);
                    Glide.with(EditProductVariation.this).load(R.drawable.placeholder).into(image);
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
