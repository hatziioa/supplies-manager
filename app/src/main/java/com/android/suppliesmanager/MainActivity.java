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

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.google.zxing.integration.android.IntentIntegrator.ALL_CODE_TYPES;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int ADD_GROUP_REQUEST = 1;
    public static final int EDIT_GROUP_REQUEST = 2;

    private GroupViewModel groupViewModel;
    private GroupAdapter adapter = new GroupAdapter();
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        final FloatingActionButton floatingActionButtonAddProduct = findViewById(R.id.fab_add_group);
        floatingActionButtonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddEditGroupActivity.class);

                startActivityForResult(intent, ADD_GROUP_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_group);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        groupViewModel.getAllGroups().observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable List<Group> groups) {
                adapter.submitList(groups);
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
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                final Group groupForDeletion = adapter.getGroupAtPosition(viewHolder.getBindingAdapterPosition());

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.dialog_message_delete_group).setTitle(R.string.dialog_title);
                builder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        groupViewModel.delete(groupForDeletion);
                        Toast.makeText(MainActivity.this, R.string.toast_list_deleted, Toast.LENGTH_SHORT).show();
                    }

                });
                builder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                notifyGroupChanged(groupForDeletion);
            }
        }).attachToRecyclerView(recyclerView);


        adapter.setOnItemClickListener(new GroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Group group) {

                Intent intent = new Intent(MainActivity.this, ProductsListActivity.class);
                intent.putExtra("EXTRA_PRODUCT_GROUP_ID", group.getId());
                intent.putExtra(ProductsListActivity.EXTRA_PRODUCT_GROUP, group.getGroupName());
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(Group group) {
                final int groupId = group.getId();
                final String groupName = group.getGroupName();

                Intent intent = new Intent(MainActivity.this, AddEditGroupActivity.class);
                intent.putExtra(AddEditGroupActivity.EXTRA_GROUP_ID, groupId);
                intent.putExtra(AddEditGroupActivity.EXTRA_GROUP_NAME, groupName);
                startActivityForResult(intent, EDIT_GROUP_REQUEST);
                return false;
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {

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

        } else if (requestCode == ADD_GROUP_REQUEST && resultCode == RESULT_OK) {
            String groupName = data.getStringExtra(AddEditGroupActivity.EXTRA_GROUP_NAME);

            Group group = new Group(groupName);
            groupViewModel.insert(group);

            Toast.makeText(this, R.string.toast_list_saved, Toast.LENGTH_SHORT).show();

        } else if (requestCode == EDIT_GROUP_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditGroupActivity.EXTRA_GROUP_ID, -1);

            if (id == -1) {
                Toast.makeText(this, R.string.toast_list_not_updated, Toast.LENGTH_SHORT).show();
                return;
            }

            String groupName = data.getStringExtra(AddEditGroupActivity.EXTRA_GROUP_NAME);

            Group group = new Group(groupName);
            group.setId(id);
            groupViewModel.update(group);

            Toast.makeText(this, R.string.toast_list_updated, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, R.string.toast_list_not_saved, Toast.LENGTH_SHORT).show();
        }
    }


    private void notifyGroupChanged(Group group) {
        int position = adapter.getPositionOfGroup(adapter.getCurrentList(), group.getId());
        if (position == -1) {
            Toast.makeText(MainActivity.this, R.string.toast_something_wrong, Toast.LENGTH_SHORT).show();
            finish();
        }
        adapter.notifyItemChanged(position);
    }


    public void handleScanResults(final String result) {

        Intent intent = new Intent(MainActivity.this, SearchableActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, result);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(MainActivity.this.getString(R.string.search_hint));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_barcode_scan_search:
                new IntentIntegrator(MainActivity.this)
                        .setDesiredBarcodeFormats(ALL_CODE_TYPES)
                        .setCameraId(0)
                        .setBeepEnabled(false)
                        .setOrientationLocked(true)
                        .setCaptureActivity(BarcodeScannerHelperActivity.class)
                        .initiateScan();
                return true;

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about) {
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

