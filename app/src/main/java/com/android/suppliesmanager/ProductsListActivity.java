package com.android.suppliesmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsListActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_GROUP = "com.android.suppliesmanager.EXTRA_PRODUCT_GROUP";

    public static final int ADD_PRODUCT_REQUEST = 1;
    public static final int EDIT_PRODUCT_REQUEST = 2;

    private ProductViewModel productViewModel;
    private final ProductAdapter adapter = new ProductAdapter();
    private static boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);
        setTitle(getIntent().getStringExtra(EXTRA_PRODUCT_GROUP));

        final int groupId = getIntent().getIntExtra("EXTRA_PRODUCT_GROUP_ID", -1);
        final String groupName = getIntent().getStringExtra(EXTRA_PRODUCT_GROUP);

        final FloatingActionButton floatingActionButtonAddProduct = findViewById(R.id.fab_add_product);
        floatingActionButtonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProductsListActivity.this, AddEditProductActivity.class);

                startActivityForResult(intent, ADD_PRODUCT_REQUEST);
            }
        });


        RecyclerView recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getAllProductsByGroup(groupId);
        productViewModel.getAllProductsByGroupResults().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable List<Product> products) {
                productViewModel.getAllProductsByGroup(groupId);
                adapter.submitList(products);
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && floatingActionButtonAddProduct.getVisibility() == View.VISIBLE) {
                    floatingActionButtonAddProduct.hide();
                } else if (dy < 0 && floatingActionButtonAddProduct.getVisibility() != View.VISIBLE) {
                    floatingActionButtonAddProduct.show();
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final Product productForDeletion = adapter.getProductAtPosition(viewHolder.getBindingAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductsListActivity.this);
                builder.setMessage(R.string.dialog_message_delete_product).setTitle(R.string.dialog_title);
                builder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        productViewModel.delete(productForDeletion);
                        productViewModel.getAllProductsByGroup(groupId);
                        Toast.makeText(ProductsListActivity.this, R.string.toast_product_deleted, Toast.LENGTH_SHORT).show();
                    }

                });
                builder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                notifyProductChanged(productForDeletion);
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(ProductsListActivity.this, AddEditProductActivity.class);
                intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_ID, product.getId());
                intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_TITLE, product.getTitle());
                intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_BARCODE, product.getBarcode());
                intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_AMOUNT, product.getAmount());
                intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_UNIT, product.getUnit());
                intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_PRICE, product.getPrice());
                intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_DESCRIPTION, product.getDescription());
                isChecked = product.getCheckBox();

                intent.putExtra("EXTRA_PRODUCT_GROUP_ID", groupId);
                startActivityForResult(intent, EDIT_PRODUCT_REQUEST);
            }
        });

        adapter.setOnItemCheckBoxClick(new ProductAdapter.OnItemCheckBoxClickListener() {
            @Override
            public void onItemCheckBoxClick(Product product) {
                if (product.getCheckBox()) {
                    product.setCheckBox(false);
                    productViewModel.update(product);

                } else {
                    product.setCheckBox(true);
                    productViewModel.update(product);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            int groupId = getIntent().getIntExtra("EXTRA_PRODUCT_GROUP_ID", -1);

            if (groupId == -1) {
                Toast.makeText(this, R.string.toast_product_not_saved, Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_TITLE);
            String barcode = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_BARCODE);
            float amount = data.getFloatExtra(AddEditProductActivity.EXTRA_PRODUCT_AMOUNT, 1);
            String unit = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_UNIT);
            String price = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_PRICE);
            String description = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_DESCRIPTION);

            Product product = new Product(title, groupId, barcode, amount, unit, price, description, false);
            productViewModel.insert(product);

            Toast.makeText(this, R.string.toast_product_saved, Toast.LENGTH_SHORT).show();

        } else if (requestCode == EDIT_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditProductActivity.EXTRA_PRODUCT_ID, -1);
            int groupId = getIntent().getIntExtra("EXTRA_PRODUCT_GROUP_ID", -1);

            if (id == -1 || groupId == -1) {
                Toast.makeText(this, R.string.toast_product_not_updated, Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_TITLE);
            String barcode = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_BARCODE);
            float amount = data.getFloatExtra(AddEditProductActivity.EXTRA_PRODUCT_AMOUNT, 1);
            String unit = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_UNIT);
            String price = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_PRICE);
            String description = data.getStringExtra(AddEditProductActivity.EXTRA_PRODUCT_DESCRIPTION);

            Product product = new Product(title, groupId, barcode, amount, unit, price, description, isChecked);
            product.setId(id);
            productViewModel.update(product);

            Toast.makeText(this, R.string.toast_product_updated, Toast.LENGTH_SHORT).show();

        } else {
            productViewModel.getAllProductsByGroup(getIntent().getIntExtra("EXTRA_PRODUCT_GROUP_ID", -1));
            Toast.makeText(this, R.string.toast_product_not_saved, Toast.LENGTH_SHORT).show();
        }
    }


    public void modifyProductQuantity(final Product product, final int modifier) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductsListActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.modify_quantity_dialog, null);
        builder.setCancelable(false);
        builder.setView(dialogView);

        final EditText editTextModifyQuantity = dialogView.findViewById(R.id.edit_text_modify_quantity);
        editTextModifyQuantity.setText("1");
        editTextModifyQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextModifyQuantity.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editTextModifyQuantity, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editTextModifyQuantity.requestFocus();

        if (modifier == 1) {
            builder.setTitle(R.string.dialog_title_increase_quantity);
        } else if (modifier == 0) {
            builder.setTitle(R.string.dialog_title_decrease_quantity);
        }


        if (!product.getTitle().trim().isEmpty()) {
            builder.setMessage(product.getTitle());
        } else {
            builder.setMessage(product.getBarcode());
        }

        builder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                float extraAmount;
                try {
                    extraAmount = Float.parseFloat(editTextModifyQuantity.getText().toString());
                } catch (NumberFormatException exception) {
                    Toast.makeText(ProductsListActivity.this, R.string.toast_dialog_no_product_quantity,
                            Toast.LENGTH_SHORT).show();
                    extraAmount = 0;
                    product.setCheckBox(false);
                    productViewModel.update(product);
                    notifyProductChanged(product);

                }

                if (modifier == 1) {
                    product.setAmount(product.getAmount() + extraAmount);
                    productViewModel.update(product);
                    notifyProductChanged(product);
                } else if (modifier == 0) {
                    if (product.getAmount() - extraAmount < 0) {
                        Toast.makeText(ProductsListActivity.this, R.string.toast_negative_quantity,
                                Toast.LENGTH_SHORT).show();
                        extraAmount = 0;
                    }
                    product.setAmount(product.getAmount() - extraAmount);
                    productViewModel.update(product);
                    notifyProductChanged(product);
                }
            }
        });

        builder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                productViewModel.update(product);
                notifyProductChanged(product);
            }
        });

        builder.create().show();
    }


    private void notifyProductChanged(Product product) {
        int position = adapter.getPositionOfProduct(adapter.getCurrentList(), product.getId());
        if (position == -1) {
            Toast.makeText(ProductsListActivity.this, R.string.toast_something_wrong, Toast.LENGTH_SHORT).show();
            finish();
        }
        adapter.notifyItemChanged(position);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.product_list_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final List<Product> products = adapter.getCurrentList();

        switch (item.getItemId()) {


            case R.id.action_increase_checked:

                for (Product product : products) {
                    if (product.getCheckBox()) {
                        product.setAmount(product.getAmount() + 1);
                        notifyProductChanged(product);
                        productViewModel.update(product);
                    }
                }
                return true;

            case R.id.action_increase_checked_interactive:
                AlertDialog.Builder increaseBuilder = new AlertDialog.Builder(ProductsListActivity.this);
                increaseBuilder.setTitle(R.string.dialog_title_increase_selected);
                increaseBuilder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        for (Product product : products) {
                            if (product.getCheckBox()) {
                                product.setAmount(product.getAmount() + 1);
                                notifyProductChanged(product);
                                productViewModel.update(product);
                            }
                        }

                        Toast.makeText(ProductsListActivity.this, R.string.toast_products_updated, Toast.LENGTH_SHORT).show();
                    }

                });

                increaseBuilder.setNeutralButton(R.string.dialog_neutral, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        for (Product product : products) {
                            if (product.getCheckBox()) {
                                modifyProductQuantity(product, 1);
                            }
                        }
                    }
                });

                increaseBuilder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                increaseBuilder.create().show();
                return true;


            case R.id.action_decrease_checked:

                for (Product product : products) {
                    if (product.getCheckBox()) {

                        if (product.getAmount() - 1 >= 0) {
                            product.setAmount(product.getAmount() - 1);
                        }
                    }
                    productViewModel.update(product);
                    notifyProductChanged(product);
                }

                return true;


            case R.id.action_decrease_checked_interactive:
                AlertDialog.Builder decreaseBuilder = new AlertDialog.Builder(ProductsListActivity.this);
                decreaseBuilder.setTitle(R.string.dialog_title_decrease_selected);
                decreaseBuilder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        for (Product product : products) {

                            if (product.getCheckBox() && (product.getAmount() - 1) < 0) {
                                Toast.makeText(ProductsListActivity.this, R.string.toast_negative_quantity,
                                        Toast.LENGTH_SHORT).show();
                            } else if (product.getCheckBox() && (product.getAmount() - 1) >= 0) {
                                product.setAmount(product.getAmount() - 1);
                                notifyProductChanged(product);
                                productViewModel.update(product);
                            }
                        }

                        Toast.makeText(ProductsListActivity.this, R.string.toast_products_updated, Toast.LENGTH_SHORT).show();
                    }

                });

                decreaseBuilder.setNeutralButton(R.string.dialog_neutral, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        for (Product product : products) {
                            if (product.getCheckBox()) {
                                modifyProductQuantity(product, 0);
                            }
                        }
                    }
                });

                decreaseBuilder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                decreaseBuilder.create().show();
                return true;

            case R.id.action_reset_checkbox:
                for (Product product : products) {
                    product.setCheckBox(false);
                    notifyProductChanged(product);
                    productViewModel.update(product);
                }
                return true;

            case R.id.action_select_all:
                for (Product product : products) {
                    product.setCheckBox(true);
                    notifyProductChanged(product);
                    productViewModel.update(product);
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
