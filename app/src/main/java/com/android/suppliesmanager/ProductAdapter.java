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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductHolder> {

    private OnItemClickListener listener;
    private OnItemCheckBoxClickListener checkBoxListener;


    protected ProductAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getGroupId() == newItem.getGroupId() &&
                    oldItem.getBarcode().equals(newItem.getBarcode()) &&
                    oldItem.getAmount() == newItem.getAmount() &&
                    oldItem.getUnit().equals(newItem.getUnit()) &&
                    oldItem.getPrice().equals(newItem.getPrice()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getCheckBox() == newItem.getCheckBox();

        }
    };


    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())    // Get context of parent activity
                .inflate(R.layout.product_item, parent, false); // Use the displayed layout
        return new ProductHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, final int position) {
        final Product currentProduct = getItem(position);
        holder.textViewTitle.setText(currentProduct.getTitle());
        holder.textViewBarcode.setText(currentProduct.getBarcode());

        float amount = currentProduct.getAmount();

        DecimalFormat commaFormat = new DecimalFormat("0.##");

        holder.textViewAmount.setText(String.valueOf(commaFormat.format(amount)));
        holder.textViewUnit.setText(currentProduct.getUnit());

        if (currentProduct.getPrice().isEmpty()) {
            holder.textViewPrice.setText(String.valueOf(currentProduct.getPrice()));
            holder.textViewPricePerItem.setText(String.valueOf(currentProduct.getPrice()));

        } else {
            float productsPrice = Float.parseFloat(currentProduct.getPrice()) * currentProduct.getAmount();
            String productByAmountPrice = String.valueOf(commaFormat.format(productsPrice) + "€");
            holder.textViewPrice.setText(productByAmountPrice);
            holder.textViewPricePerItem.setText(String.valueOf(currentProduct.getPrice()) + "€/" + currentProduct.getUnit());

        }

        holder.checkBox.setChecked(currentProduct.getCheckBox());
    }


    public Product getProductAtPosition(int position) {
        return getItem(position);
    }


    public int getPositionOfProduct(List<Product> products, int id) {

        for (int position = 0; position < products.size(); position++) {
            if (products.get(position).getId() == id) {
                return position;
            }
        }
        return -1;
    }


    class ProductHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewBarcode;
        private TextView textViewAmount;
        private TextView textViewUnit;
        private TextView textViewPrice;
        private TextView textViewPricePerItem;
        private CheckBox checkBox;

        public ProductHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewBarcode = itemView.findViewById(R.id.text_view_barcode);
            textViewAmount = itemView.findViewById(R.id.text_view_amount);
            textViewUnit = itemView.findViewById(R.id.text_view_unit);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            textViewPricePerItem = itemView.findViewById(R.id.text_view_price_per_item);
            checkBox = itemView.findViewById(R.id.checkBox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getBindingAdapterPosition();

                    if (checkBoxListener != null && position != RecyclerView.NO_POSITION) {

                        checkBoxListener.onItemCheckBoxClick(getItem(position));
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getBindingAdapterPosition();

                    if (listener != null && position != RecyclerView.NO_POSITION) {

                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(Product product);

    }


    public interface OnItemCheckBoxClickListener {
        void onItemCheckBoxClick(Product product);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public void setOnItemCheckBoxClick(OnItemCheckBoxClickListener checkBoxListener) {
        this.checkBoxListener = checkBoxListener;
    }

}