package com.appsinventiv.toolsbazzar.Utils;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.appsinventiv.toolsbazzar.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomBottomDialog {
    private CustomBottomDialog() {
    }

    public static void showFeatureDialog(Context context, String text, int id) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.fort_city_features_bottom_sheet, null);
        final Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen
        );
        dialog.setContentView(customView);

        Button ok = customView.findViewById(R.id.ok);
        TextView textData = customView.findViewById(R.id.textData);
        TextView subtitle = customView.findViewById(R.id.subtitle);
        ImageView picture = customView.findViewById(R.id.picture);
        textData.setText(text);

        if (id == 1) {
            Glide.with(context).load(R.drawable.ic_promo_big).into(picture);
            subtitle.setText("Promo Codes");
        } else if (id == 2) {
            Glide.with(context).load(R.drawable.ic_easy_big).into(picture);
            subtitle.setText("Easy Checkout");
        } else if (id == 3) {
            Glide.with(context).load(R.drawable.ic_cash_big).into(picture);
            subtitle.setText("Cash on dlivery");
        } else if (id == 4) {
            Glide.with(context).load(R.drawable.ic_online_big).into(picture);
            subtitle.setText("Online payments");
        } else if (id == 5) {
            Glide.with(context).load(R.drawable.ic_paypal_big).into(picture);
            subtitle.setText("Paypal Option");

        }


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

}
