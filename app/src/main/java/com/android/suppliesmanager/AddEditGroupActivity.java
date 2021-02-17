package com.android.suppliesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditGroupActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "com.android.suppliesmanager.EXTRA_GROUP_ID";
    public static final String EXTRA_GROUP_NAME = "com.android.suppliesmanager.EXTRA_GROUP_NAME";

    private EditText editTextGroupName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_group);

        editTextGroupName = findViewById(R.id.edit_text_group_name);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_GROUP_ID)) {
            setTitle(R.string.label_edit_list);

            editTextGroupName.setText(intent.getStringExtra(EXTRA_GROUP_NAME));

        } else {
            setTitle(R.string.label_add_list);

        }
    }


    public void saveGroup() {
        String groupName = editTextGroupName.getText().toString();

        if (groupName.trim().isEmpty()) {
            Toast.makeText(this, R.string.toast_insert_valid,
                    Toast.LENGTH_SHORT).show();
            return;

        }

        Intent data = new Intent();
        data.putExtra(EXTRA_GROUP_NAME, groupName);

        int id = getIntent().getIntExtra(EXTRA_GROUP_ID, -1);

        if (id != -1) {
            data.putExtra(EXTRA_GROUP_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
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

                saveGroup();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
