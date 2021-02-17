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

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;


@Entity(tableName = "product_table",
        foreignKeys = @ForeignKey(
                entity = Group.class,
                parentColumns = "id",
                childColumns = "groupId",
                onDelete = CASCADE,
                onUpdate = CASCADE),
        indices = {@Index("groupId")})
public class Product {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo()
    private String title;

    private int groupId;

    private String barcode;

    private float amount;

    private String unit;

    private String price;

    private String description;

    private boolean checkBox;


    public Product(String title, int groupId, String barcode, float amount, String unit, String price, String description, boolean checkBox) {
        this.title = title;
        this.groupId = groupId;
        this.barcode = barcode;
        this.amount = amount;
        this.unit = unit;
        this.price = price;
        this.description = description;
        this.checkBox = checkBox;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    // Getter methods for Room to persist values in the database, meaning that the data survives
    // the process that created them.


    public int getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public int getGroupId() {
        return groupId;
    }


    public String getBarcode() {
        return barcode;
    }


    public float getAmount() {
        return amount;
    }


    public String getUnit() {
        return unit;
    }


    public String getPrice() {
        return price;
    }


    public String getDescription() {
        return description;
    }


    public boolean getCheckBox() {
        return checkBox;
    }

}