package com.appsinventiv.toolsbazzar.Seller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.CommonUtils;

public class AddProductWeight extends AppCompatActivity {
    EditText pweight, plength, pwidth, pheight;
    TextView packageWeight, dimensionWeight;
    Button update, calculate;
    String dimenWeight;
    boolean calculated = false;
    String dimens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_weight);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Add product weight");

        dimensionWeight = findViewById(R.id.dimensionWeight);
        packageWeight = findViewById(R.id.packageWeight);
        pweight = findViewById(R.id.pweight);
        plength = findViewById(R.id.plength);
        pwidth = findViewById(R.id.pwidth);
        pheight = findViewById(R.id.pheight);
        update = findViewById(R.id.update);
        calculate = findViewById(R.id.calculate);


        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pweight.getText().length() == 0) {
                    pweight.setError("Enter Value");
                } else if (plength.getText().length() == 0) {
                    plength.setError("Enter Value");
                } else if (pwidth.getText().length() == 0) {
                    pwidth.setError("Enter Value");
                } else if (pheight.getText().length() == 0) {
                    pheight.setError("Enter Value");
                } else {

                    calculateValues();
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calculated) {
                    float w1=Float.parseFloat(pweight.getText().toString());
                    float w2=Float.parseFloat(dimenWeight);
                    SellerAddProduct.productWeight = ""+(w1>w2?w1:w2);
                    EditProduct.productWeight = "" +(w1>w2?w1:w2);
                    SellerAddProduct.dimens = "" + dimens;
                    EditProduct.dimens = "" + dimens;
                    finish();
                } else {
                    CommonUtils.showToast("Please calculate weight");
                }
            }
        });


    }

    private void calculateValues() {
        calculated = true;
        packageWeight.setText("Package Weight: " + pweight.getText().toString() + "Kg");
        String dimeneight = "" + String.format("%.1f", ((Double.parseDouble(plength.getText().toString()) *
                Double.parseDouble(pwidth.getText().toString()) *
                Double.parseDouble(pheight.getText().toString())) / 5000));

        dimenWeight = dimeneight;
        dimensionWeight.setText("Dimension Weight: " + dimeneight + "Kg");
        float w1=Float.parseFloat(pweight.getText().toString());
        float w2=Float.parseFloat(dimenWeight);
        dimens = ""+(w1>w2?w1:w2) +" Kg." + " Dimensions: " + plength.getText().toString() + "*" + pweight.getText().toString() + "*" + pheight.getText();

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
