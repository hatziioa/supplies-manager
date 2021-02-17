/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.suppliesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.google.zxing.integration.android.IntentIntegrator.ALL_CODE_TYPES;


/**
 * Does X and Y and provides an abstraction for Z.
 */

public class AddEditProductActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "com.android.suppliesmanager.EXTRA_PRODUCT_ID";
    public static final String EXTRA_PRODUCT_TITLE = "com.android.suppliesmanager.EXTRA_PRODUCT_TITLE";
    public static final String EXTRA_PRODUCT_BARCODE = "com.android.suppliesmanager.EXTRA_PRODUCT_BARCODE";
    public static final String EXTRA_PRODUCT_AMOUNT = "com.android.suppliesmanager.EXTRA_PRODUCT_AMOUNT";
    public static final String EXTRA_PRODUCT_UNIT = "com.android.suppliesmanager.EXTRA_PRODUCT_UNIT";
    public static final String EXTRA_PRODUCT_PRICE = "com.android.suppliesmanager.EXTRA_PRODUCT_PRICE";
    public static final String EXTRA_PRODUCT_DESCRIPTION = "com.android.suppliesmanager.EXTRA_PRODUCT_DESCRIPTION";

    private EditText editTextTitle;
    private EditText editTextBarcode;
    private EditText editTextAmount;
    private EditText editTextUnit;
    private EditText editTextPrice;
    private EditText editTextDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextBarcode = findViewById(R.id.edit_text_barcode);
        editTextAmount = findViewById(R.id.edit_text_amount);
        editTextUnit = findViewById(R.id.edit_text_unit);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextDescription = findViewById(R.id.edit_text_description);

        FloatingActionButton floatingActionButtonSaveProduct = findViewById(R.id.fab_scan_barcode);
        floatingActionButtonSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(AddEditProductActivity.this)
                        .setDesiredBarcodeFormats(ALL_CODE_TYPES)
                        .setCameraId(0)
                        .setBeepEnabled(false)
                        .setOrientationLocked(true)
                        .setCaptureActivity(BarcodeScannerHelperActivity.class)
                        .initiateScan();
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        final Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_PRODUCT_ID)) {
            setTitle(R.string.label_edit_product);

            editTextTitle.setText(intent.getStringExtra(EXTRA_PRODUCT_TITLE));
            editTextBarcode.setText(intent.getStringExtra(EXTRA_PRODUCT_BARCODE));
            editTextAmount.setText(String.valueOf(intent.getFloatExtra(EXTRA_PRODUCT_AMOUNT, 1)));
            editTextUnit.setText(intent.getStringExtra(EXTRA_PRODUCT_UNIT));
            editTextPrice.setText(intent.getStringExtra(EXTRA_PRODUCT_PRICE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION));

            ImageButton imageButtonIncrease = findViewById(R.id.image_button_increase);
            increaseAmount(intent, imageButtonIncrease);

            ImageButton imageButtonDecrease = findViewById(R.id.image_button_decrease);
            decreaseAmount(intent, imageButtonDecrease);

        } else {
            editTextAmount.setText(String.valueOf(intent.getFloatExtra(EXTRA_PRODUCT_AMOUNT, 1)));

            ImageButton imageButtonIncrease = findViewById(R.id.image_button_increase);
            increaseAmount(intent, imageButtonIncrease);

            ImageButton imageButtonDecrease = findViewById(R.id.image_button_decrease);
            decreaseAmount(intent, imageButtonDecrease);

            setTitle(R.string.label_add_product);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, R.string.toast_scan_cancel, Toast.LENGTH_LONG).show();
            } else {
                handleScanResults(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void handleScanResults(final String result) {
        Intent intent = getIntent();
        intent.putExtra(EXTRA_PRODUCT_BARCODE, result);
        editTextBarcode.setText(intent.getStringExtra(EXTRA_PRODUCT_BARCODE));
        Toast.makeText(this, R.string.toast_scan_success,
                Toast.LENGTH_SHORT).show();
    }


    public void saveProduct() {

        String title = editTextTitle.getText().toString();
        String barcode = editTextBarcode.getText().toString();
        float amount = Float.parseFloat(editTextAmount.getText().toString());
        int groupId = getIntent().getIntExtra("EXTRA_PRODUCT_GROUP_ID", -1);
        String unit = editTextUnit.getText().toString();
        String price = editTextPrice.getText().toString();
        String description = editTextDescription.getText().toString();

        if (title.trim().isEmpty() && barcode.trim().isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_name_barcode,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra("EXTRA_PRODUCT_GROUP_ID", groupId);
        data.putExtra(EXTRA_PRODUCT_TITLE, title);
        data.putExtra(EXTRA_PRODUCT_BARCODE, barcode);
        data.putExtra(EXTRA_PRODUCT_AMOUNT, amount);
        data.putExtra(EXTRA_PRODUCT_UNIT, unit);
        data.putExtra(EXTRA_PRODUCT_PRICE, price);
        data.putExtra(EXTRA_PRODUCT_DESCRIPTION, description);

        int id = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);

        if (id != -1) {
            data.putExtra(EXTRA_PRODUCT_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }


    private void increaseAmount(final Intent intent, ImageButton imageButtonIncrease) {

        imageButtonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float amount;
                String amountString = editTextAmount.getText().toString();

                if (amountString.isEmpty()) {
                    amount = 0;
                } else {
                    amount = Float.parseFloat(amountString);
                }
                amount++;
                intent.putExtra(EXTRA_PRODUCT_AMOUNT, amount);
                editTextAmount.setText(String.valueOf(intent.getFloatExtra(EXTRA_PRODUCT_AMOUNT, 1)));
            }
        });
    }


    private void decreaseAmount(final Intent intent, ImageButton imageButtonDecrease) {

        imageButtonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float amount;
                String amountString = editTextAmount.getText().toString();

                if (amountString.isEmpty()) {
                    amount = 0;
                } else {
                    amount = Float.parseFloat(amountString);
                }
                if (amount - 1 < 0) {
                    Toast.makeText(AddEditProductActivity.this, R.string.toast_negative_quantity, Toast.LENGTH_SHORT).show();
                } else {
                    amount--;
                    intent.putExtra(EXTRA_PRODUCT_AMOUNT, amount);
                    editTextAmount.setText(String.valueOf(intent.getFloatExtra(EXTRA_PRODUCT_AMOUNT, 1)));
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_edit_item_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:


                try {
                    saveProduct();
                } catch (NumberFormatException exception) {
                    Toast.makeText(AddEditProductActivity.this, R.string.toast_enter_quantity,
                            Toast.LENGTH_SHORT).show();
                }
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

