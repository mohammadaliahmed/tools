package com.appsinventiv.toolsbazzar.BottomSheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.Activities.SizeChart;
import com.appsinventiv.toolsbazzar.Adapters.AttributesAdapter;
import com.appsinventiv.toolsbazzar.Adapters.NewAttributesAdapter;
import com.appsinventiv.toolsbazzar.Interface.AttributesOptionCallbacks;
import com.appsinventiv.toolsbazzar.Models.ColorAttributeModel;
import com.appsinventiv.toolsbazzar.Models.NewProductModel;
import com.appsinventiv.toolsbazzar.Models.Product;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;
import com.appsinventiv.toolsbazzar.Utils.Constants;
import com.appsinventiv.toolsbazzar.Utils.PrefManager;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowBottomDialog {

    private static String sizeSelected = "";
    private static String colorSelected = "";
    private static int selected = -1;
    private static int selectedColor;

    public static void showSizeAndColorDialogNew(final Context context, final Product product, final int quantity, final DatabaseReference mDatabase, final AttributesListener listener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_size_color_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(context)
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        final TextView title = customView.findViewById(R.id.title);
        final TextView price = customView.findViewById(R.id.price);
        final ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        final Button confirm = customView.findViewById(R.id.confirm);
        TextView sizeChart = customView.findViewById(R.id.sizeChart);
        final TextView percentageOff = customView.findViewById(R.id.percentageOff);
        final TextView oldPrice = customView.findViewById(R.id.oldPrice);
        final TextView quantityText = customView.findViewById(R.id.quantityText);
        final ImageView whichArrow = customView.findViewById(R.id.whichArrow);

        RecyclerView recyclerColors = customView.findViewById(R.id.recyclerColors);
        final RecyclerView recyclerSize = customView.findViewById(R.id.recyclerSize);

        if (product.getQuantityAvailable() == 0) {

            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
//            confirm.setVisibility(View.INVISIBLE);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");


        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());


        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        title.setText(product.getTitle());
        Glide.with(context).load(product.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(image);

        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SizeChart.class);
                context.startActivity(i);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancelled();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sizeSelected.equalsIgnoreCase("") && !colorSelected.equalsIgnoreCase("")) {

                    Product p1 =product;
                    p1.setProductCountHashmap(null);
                    p1.setThumbnailUrl(product.getAttributesWithPics().get(colorSelected));
                    p1.setAttributesWithPics(null);

                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(p1);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("size").setValue(sizeSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sizeSelected = "";
                            selected = -1;
                        }
                    });
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("color").setValue(colorSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            colorSelected = "";
                            selectedColor = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select size and color");

                }
            }
        });
        final String[] colorChosen = {""};
        initializeColorPictures(context, product, recyclerColors, new AttributesOptionCallbacks() {
            @Override
            public void onSelected(String color) {
                colorChosen[0] = color;
                colorSelected = color;
                Glide.with(context).load(product.getAttributesWithPics().get(colorSelected)).into(image);

                initiliazeNewSizeButtons(context, product, recyclerSize, colorChosen, new AttributesOptionCallbacks() {
                    @Override
                    public void onSelected(String size) {
//                        CommonUtils.showToast(size);
                        sizeSelected = product.getProductCountHashmap().get(colorChosen[0]).get(Integer.parseInt(size)).getSize();
                        product.setQuantityAvailable(Integer.parseInt("" + (product.getProductCountHashmap().get(colorChosen[0]).get(Integer.parseInt(size)).getQty())));
                        product.setRetailPrice(Float.parseFloat("" + (product.getProductCountHashmap().get(colorChosen[0]).get(Integer.parseInt(size)).getRetailPrice())));
                        product.setWholeSalePrice(Float.parseFloat("" + (product.getProductCountHashmap().get(colorChosen[0]).get(Integer.parseInt(size)).getWholesalePrice())));
                        setNewPrices(product.getProductCountHashmap().get(colorChosen[0]).get(Integer.parseInt(size)),
                                price, oldPrice, percentageOff, quantityText, whichArrow, confirm
                        );


                    }
                });

            }
        });

        bottomDialog.show();

    }

    private static void setNewPrices(NewProductModel product, TextView price, TextView oldPrice, TextView percentageOff,
                                     TextView quantityText,
                                     ImageView whichArrow, Button confirm) {
        if (product.getQty() == 0) {

            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
//            confirm.setVisibility(View.INVISIBLE);
        } else if (product.getQty() > 0 && product.getQty() <= 5) {
            quantityText.setText("Only " + product.getQty() + " left in stock, Hurry up & grab yours");


        } else {
            quantityText.setText("Available quantity in stock " + product.getQty());


        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            if (product.getOldWholesalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholesalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldWholesalePrice() - product.getWholesalePrice()) / product.getWholesalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((float) (product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }


    public static void showSizeAndColorDialog(final Context context, final Product product, final int quantity, final DatabaseReference mDatabase, final AttributesListener listener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_size_color_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(context)
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        TextView title = customView.findViewById(R.id.title);
        final TextView price = customView.findViewById(R.id.price);
        ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        Button confirm = customView.findViewById(R.id.confirm);
        TextView sizeChart = customView.findViewById(R.id.sizeChart);
        TextView percentageOff = customView.findViewById(R.id.percentageOff);
        TextView oldPrice = customView.findViewById(R.id.oldPrice);
        TextView quantityText = customView.findViewById(R.id.quantityText);
        ImageView whichArrow = customView.findViewById(R.id.whichArrow);

        RecyclerView recyclerColors = customView.findViewById(R.id.recyclerColors);
        RecyclerView recyclerSize = customView.findViewById(R.id.recyclerSize);

        if (product.getQuantityAvailable() == 0) {

            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
//            confirm.setVisibility(View.INVISIBLE);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");


        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());


        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        title.setText(product.getTitle());
        Glide.with(context).load(product.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(image);

        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SizeChart.class);
                context.startActivity(i);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancelled();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sizeSelected.equalsIgnoreCase("") && !colorSelected.equalsIgnoreCase("")) {

//                    product.setRetailPrice();
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("size").setValue(sizeSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sizeSelected = "";
                            selected = -1;
                        }
                    });
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("color").setValue(colorSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            colorSelected = "";
                            selectedColor = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select size and color");

                }
            }
        });
        initializeColorButtons(context, product, recyclerColors);
        initiliazeSizeButtons(context, product, recyclerSize);

        bottomDialog.show();

    }


    public static void showSizeBottomDialog(final Context context, final Product product, final int quantity, final DatabaseReference mDatabase, final AttributesListener listener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_size_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(context)
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        TextView title = customView.findViewById(R.id.title);
        TextView price = customView.findViewById(R.id.price);
        TextView percentageOff = customView.findViewById(R.id.percentageOff);
        TextView oldPrice = customView.findViewById(R.id.oldPrice);
        TextView quantityText = customView.findViewById(R.id.quantityText);
        ImageView whichArrow = customView.findViewById(R.id.whichArrow);
        ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        final Button confirm = customView.findViewById(R.id.confirm);
        RecyclerView recyclerSize = customView.findViewById(R.id.recyclerSize);

        if (product.getQuantityAvailable() == 0) {
            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
//            confirm.setVisibility(View.INVISIBLE);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");
        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());
        }

        if (product.getQuantityAvailable() == 0) {

            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
//            confirm.setVisibility(View.INVISIBLE);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");


        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());


        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        TextView sizeChart = customView.findViewById(R.id.sizeChart);

        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SizeChart.class);
                context.startActivity(i);
            }
        });

        title.setText(product.getTitle());

        Glide.with(context).load(product.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(image);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancelled();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sizeSelected.equalsIgnoreCase("")) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());

                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("size").setValue(sizeSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sizeSelected = "";
                            selected = -1;
                            colorSelected = "";
                            selectedColor = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select size ");

                }
            }
        });
        initiliazeSizeButtons(context, product, recyclerSize);

        bottomDialog.show();
    }

    public static void showColorBottomDialog(final Context context, final Product product, final int quantity, final DatabaseReference mDatabase, final AttributesListener listener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.attributes_color_layout, null);
//
        final BottomDialog bottomDialog = new BottomDialog.Builder(context)
                .setContent(null)
                .setCustomView(customView)

                .setCancelable(false)
                .build();

        TextView title = customView.findViewById(R.id.title);
        TextView price = customView.findViewById(R.id.price);
        ImageView image = customView.findViewById(R.id.image);
        ImageView close = customView.findViewById(R.id.close);
        Button confirm = customView.findViewById(R.id.confirm);
        RecyclerView recyclerColors = customView.findViewById(R.id.recyclerColors);
        TextView percentageOff = customView.findViewById(R.id.percentageOff);
        TextView oldPrice = customView.findViewById(R.id.oldPrice);
        TextView quantityText = customView.findViewById(R.id.quantityText);
        ImageView whichArrow = customView.findViewById(R.id.whichArrow);

        if (product.getQuantityAvailable() == 0) {

            quantityText.setText("Sorry item is currently out of stock");
            whichArrow.setImageResource(R.drawable.ic_arrow_red);
//            confirm.setVisibility(View.INVISIBLE);
        } else if (product.getQuantityAvailable() > 0 && product.getQuantityAvailable() <= 5) {
            quantityText.setText("Only " + product.getQuantityAvailable() + " left in stock, Hurry up & grab yours");


        } else {
            quantityText.setText("Available quantity in stock " + product.getQuantityAvailable());


        }

        if (SharedPrefs.getCustomerType().equalsIgnoreCase("wholesale")) {
            if (product.getOldWholeSalePrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldWholeSalePrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getWholeSalePrice() - product.getWholeSalePrice()) / product.getWholeSalePrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        } else if (SharedPrefs.getCustomerType().equalsIgnoreCase("retail")) {
            price.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
            if (product.getOldRetailPrice() != 0) {
                oldPrice.setText(SharedPrefs.getCurrencySymbol() + " " + String.format("%.2f", product.getOldRetailPrice() * Float.parseFloat(SharedPrefs.getExchangeRate())));
                String percent = "" + String.format("%.0f", ((product.getOldRetailPrice() - product.getRetailPrice()) / product.getOldRetailPrice()) * 100);
                percentageOff.setVisibility(View.VISIBLE);
                percentageOff.setText(percent + "% Off");
            } else {
                oldPrice.setText("");
                percentageOff.setVisibility(View.GONE);

            }
        }
        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        title.setText(product.getTitle());
        Glide.with(context).load(product.getThumbnailUrl()).placeholder(R.drawable.placeholder).into(image);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancelled();
                bottomDialog.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!colorSelected.equalsIgnoreCase("")) {
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("product").setValue(product);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("quantity").setValue(quantity);
                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("time").setValue(System.currentTimeMillis());


                    mDatabase.child("Customers").child(SharedPrefs.getUsername())
                            .child("cart").child(product.getId()).child("color").setValue(colorSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            colorSelected = "";
                            selectedColor = -1;
                            sizeSelected = "";
                            selected = -1;
                        }
                    });


                    bottomDialog.dismiss();
                } else {
                    CommonUtils.showToast("Please select color");

                }
            }
        });
        initializeColorButtons(context, product, recyclerColors);

        bottomDialog.show();
    }

    public static void initializeColorButtons(Context context, final Product product, RecyclerView recyclerView) {


        AttributesAdapter adapter = new AttributesAdapter(context, product.getColorList(), new AttributesAdapter.OnItemSelected() {
            @Override
            public void onOptionSelected(String value) {
                colorSelected = value;

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

    }

    public static void initializeColorPictures(Context context, final Product product, RecyclerView recyclerView, final AttributesOptionCallbacks colorCallBacks) {

        List<ColorAttributeModel> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : product.getAttributesWithPics().entrySet()) {

            items.add(new ColorAttributeModel(entry.getKey(), entry.getValue()));
        }

        NewAttributesAdapter adapter = new NewAttributesAdapter(context, items, new NewAttributesAdapter.OnItemSelected() {
            @Override
            public void onOptionSelected(String value) {
                colorSelected = value;
//                CommonUtils.showToast(value);
                colorCallBacks.onSelected(value);

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

    }

    public static void initiliazeSizeButtons(Context context, final Product product, RecyclerView recyclerSize) {
        AttributesAdapter adapter = new AttributesAdapter(context, product.getSizeList(), new AttributesAdapter.OnItemSelected() {
            @Override
            public void onOptionSelected(String value) {
                sizeSelected = value;

            }
        });
        recyclerSize.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerSize.setAdapter(adapter);
    }

    public static void initiliazeNewSizeButtons(Context context, final Product product, RecyclerView recyclerSize, String[] colorChosen, final AttributesOptionCallbacks callBacks) {
        List<String> sizeList = new ArrayList<>();
        for (NewProductModel size : product.getProductCountHashmap().get(colorChosen[0])) {
            if (size.getColor().equalsIgnoreCase(colorChosen[0])) {
                sizeList.add(size.getSize());
            }
        }
        AttributesAdapter adapter = new AttributesAdapter(context, sizeList, new AttributesAdapter.OnItemSelected() {
            @Override
            public void onOptionSelected(String value) {
                sizeSelected = value;
                callBacks.onSelected(value);

            }
        });
        recyclerSize.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerSize.setAdapter(adapter);
    }


    public interface AttributesListener {
        public void onCancelled();
    }

}
