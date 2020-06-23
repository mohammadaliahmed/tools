package com.appsinventiv.toolsbazzar.Seller;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement.ChooseAttributes;
import com.appsinventiv.toolsbazzar.ProductManagement.AttributesManagement.ChooseAttributesAgain;
import com.appsinventiv.toolsbazzar.ProductManagement.CategoryPackage.ChooseOtherMainCategory;
import com.appsinventiv.toolsbazzar.ProductManagement.ChooseProductVariation;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Seller.SellerChat.SellerChats;
import com.appsinventiv.toolsbazzar.Seller.SellerOrders.BottomDialogModel;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.CompressImage;
import com.appsinventiv.toolsbazzar.Utils.CompressImageToThumnail;
import com.appsinventiv.toolsbazzar.Utils.Constants;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static String productWeight, dimens;
    public static int fromWhere = 0;

    EditText productModel;

    RadioButton both, wholesale, retail;
    LinearLayout retailArea, wholesaleArea;
    int sellingTo = 1;
    public static HashMap<String, Object> productAttributesMap = new HashMap<>();

    private ArrayList<BottomDialogModel> vendrs = new ArrayList<>();
    private String localThumbnail;
    EditText warrantyPolicy;
    TextView dangerousGoodsTv;
    TextView productVariation;

    TextView productVariationSubtitle;
    public static SellerAddProduct activity;

    public static String whichWarranty, warrantyPeriod, dangerousGoods;
    TextView warrantyPeriodTv;


    CardView cardAttr;

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


        productVariationSubtitle = findViewById(R.id.productVariationSubtitle);
        productVariation = findViewById(R.id.productVariation);
        both = findViewById(R.id.both);
        productModel = findViewById(R.id.productModel);
        dangerousGoodsTv = findViewById(R.id.dangerousGoods);
        wholesale = findViewById(R.id.wholesale);
        retail = findViewById(R.id.retail);
        retailArea = findViewById(R.id.retailArea);
        wholesaleArea = findViewById(R.id.wholesaleArea);
        pick = findViewById(R.id.pick);
        upload = findViewById(R.id.upload);
        e_title = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerview);
        e_subtitle = findViewById(R.id.subtitle);
        e_costPrice = findViewById(R.id.costPrice);
        warrantyPolicy = findViewById(R.id.warrantyPolicy);
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
        warrantyPeriodTv = findViewById(R.id.warrantyPeriod);
        cardAttr = findViewById(R.id.cardAttr);

        productVariation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerAddProduct.this, ChooseProductVariation.class));
            }
        });


        both.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sellingTo = 1;
                    retailArea.setVisibility(View.VISIBLE);
                    wholesaleArea.setVisibility(View.VISIBLE);
                }
            }
        });
        wholesale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sellingTo = 2;
                    retailArea.setVisibility(View.GONE);
                    wholesaleArea.setVisibility(View.VISIBLE);
                }
            }
        });
        retail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sellingTo = 3;
                    retailArea.setVisibility(View.VISIBLE);
                    wholesaleArea.setVisibility(View.GONE);
                }
            }
        });

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

        cardAttr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.RE_ATTRIBUTES = true;
                ChooseOtherMainCategory.activity.finish();
                Intent i = new Intent(SellerAddProduct.this, ChooseAttributes.class);
                startActivity(i);

                finish();
            }
        });


        e_title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    /* Write your logic here that will be executed when user taps next button */
                    e_subtitle.requestFocus();

                    handled = true;
                }

                return handled;
            }
        });
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
                    e_minOrderQty.requestFocus();

                    handled = true;
                }

                return handled;
            }
        });
        e_minOrderQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
//        e_quantityAvailable.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_NEXT) {
//                    /* Write your logic here that will be executed when user taps next button */
//                    e_retailPrice.requestFocus();
//
//                    handled = true;
//                }
//
//                return handled;
//            }
//        });


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
                    CommonUtils.showToast("Enter title");
                    e_title.requestFocus();
                } else if (e_subtitle.getText().length() == 0) {
                    e_subtitle.setError("Enter subtitle");
                    CommonUtils.showToast("Enter subtitle");
                    e_subtitle.requestFocus();
                } else if (e_description.getText().length() == 0) {
                    e_description.setError("Enter description");
                    CommonUtils.showToast("Enter description");
                    e_description.requestFocus();
                } else if (e_costPrice.getText().length() == 0) {
                    e_costPrice.setError("Enter cost price");
                    CommonUtils.showToast("Enter cost price");
                    e_costPrice.requestFocus();
                } else if (e_quantityAvailable.getText().length() == 0) {
                    e_quantityAvailable.setError("Enter quantity");
                    CommonUtils.showToast("Enter quantity");
                    e_quantityAvailable.requestFocus();
                } else if (whichWarranty == null) {
                    CommonUtils.showToast("Please select warranty type");

                } else if (productWeight == null) {
                    CommonUtils.showToast("Please select product weight");
                } else if (mSelected.size() == 0) {
                    CommonUtils.showToast("Please select image");
                } else {

                    showUploadAlert();

                }

            }
        });


    }

    private void showUploadAlert() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_title_curved, null);

        dialog.setContentView(layout);

        TextView message = layout.findViewById(R.id.message);
        TextView title = layout.findViewById(R.id.title);
        TextView no = layout.findViewById(R.id.no);
        TextView yes = layout.findViewById(R.id.yes);

        message.setText("Upload Product?");
        title.setText("Alert");

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                uploadNow();
            }
        });


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void uploadNow() {
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
                true,
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
                dimens, "admin",
                "Approved", warrantyPeriod,
                warrantyPolicy.getText().toString(),
                dangerousGoods,
                productModel.getText().toString()



        )).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child("Sellers").child(SharedPrefs.getUsername()).child("products").child(productId).setValue(productId);

                int count = 0;
                putThumbnail(localThumbnail, productId);
                mDatabase.child("Products").child(productId).child("productAttributes").updateChildren(productAttributesMap);
                mDatabase.child("Products").child(productId).child("attributesWithPics").updateChildren(ChooseProductVariation.uploadedMap);
                mDatabase.child("Products").child(productId).child("newAttributes").updateChildren(ChooseProductVariation.hashMapHashMap);
                categoryList.clear();
                productAttributesMap.clear();


                NotificationAsync notificationAsync = new NotificationAsync(SellerAddProduct.this);
                String NotificationTitle = "New product uploaded by " + SharedPrefs.getVendor().getStoreName();
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

    public void putThumbnail(String path, final String key) {
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
                        mDatabase.child("Products").child(productId).child("thumbnailUrl").setValue("" + downloadUrl);


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

    private boolean checkk() {


        int whole = Integer.parseInt(e_wholesalePrice.getText().toString());
        int retail = Integer.parseInt(e_retailPrice.getText().toString());
        int cost = Integer.parseInt(e_costPrice.getText().toString());
        if (sellingTo == 1) {
            if (retail > whole && retail > cost && whole > cost) {
                return true;
            } else {
                e_retailPrice.setError("Error");
                e_wholesalePrice.setError("Error");
                e_retailPrice.requestFocus();
                CommonUtils.showToast("Selling price should be greater than cost price");
                return false;
            }
        } else if (sellingTo == 2) {
            if (whole > cost) {
                return true;
            } else {
                e_wholesalePrice.setError("Error");
                e_wholesalePrice.requestFocus();
                CommonUtils.showToast("Selling price should be greater than cost price");
                return false;
            }
        } else if (sellingTo == 3) {
            if (retail > cost) {
                return true;
            } else {
                e_retailPrice.setError("Error");
                e_retailPrice.requestFocus();
                CommonUtils.showToast("Selling price should be greater than cost price");
                return false;
            }
        }
        return false;


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
        Constants.RE_ATTRIBUTES = false;
        if (productWeight != null) {
            weightChosen.setText("Weight: " + productWeight + "Kg");
        }
        if (productAttributesMap != null && productAttributesMap.size() > 0) {
            final LinearLayout options_layout = (LinearLayout) findViewById(R.id.layout);
            options_layout.removeAllViews();
            for (final Map.Entry<String, Object> entry : productAttributesMap.entrySet()) {
                final String key = entry.getKey();
                String value = entry.getValue().toString();
                LayoutInflater inflater = LayoutInflater.from(this);
                final View to_add = inflater.inflate(R.layout.product_attributes_layout,
                        options_layout, false);
//                ImageView  delete = to_add.findViewById(R.id.delete);
                TextInputEditText subtitle = to_add.findViewById(R.id.subtitle);
                TextInputLayout keu = to_add.findViewById(R.id.TextInputLayout);
                ImageView delete = to_add.findViewById(R.id.delete);
                ImageView edit = to_add.findViewById(R.id.edit);
                keu.setHint(key);
                subtitle.setText(value);
                options_layout.addView(to_add);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        options_layout.removeView(to_add);
                        productAttributesMap.remove(entry.getKey());
                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(SellerAddProduct.this, ChooseAttributesAgain.class);
                        i.putExtra("attribute", key);
                        startActivity(i);
                    }
                });
            }

        }
        if (categoryList.size() > 0) {
            categoryChoosen.setText("Category: " + categoryList);

        }
        if (whichWarranty != null) {
            warrantyChosen.setText("Warranty: " + whichWarranty);
        }
        if (warrantyPeriod != null) {
            warrantyPeriodTv.setText("Period:" + warrantyPeriod);
        }
        if (dangerousGoods != null) {
            dangerousGoodsTv.setText("Dangerous:" + dangerousGoods);
        }

        if (categoryList != null && categoryList.size() > 0) {
            Constants.ADDING_PRODUCT = false;
        } else {
            if (!Constants.ADDING_PRODUCT_BACK) {
                SellerAddProduct.fromWhere=1;
                Constants.ADDING_PRODUCT = true;
                sellingTo = 1;
                Intent i = new Intent(SellerAddProduct.this, ChooseMainCategory.class);
                categoryList.clear();
                startActivityForResult(i, 1);
            } else {
                finish();
            }
        }
        if (ChooseProductVariation.hashMapHashMap != null && ChooseProductVariation.hashMapHashMap.size() > 0) {
            productVariationSubtitle.setText("Color and size selected");
        } else {

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
                CompressImageToThumnail compressImageToThumnail = new CompressImageToThumnail(SellerAddProduct.this);
                localThumbnail = compressImageToThumnail.compressImage("" + mSelected.get(0));
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
            categoryList.clear();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        categoryList.clear();
        finish();
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
