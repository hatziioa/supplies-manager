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

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


public class ProductViewModel extends AndroidViewModel {

    private ProductRepository repository;
    private LiveData<List<Product>> allProducts;
    private LiveData<List<Product>> allProductsByGroup;
    private LiveData<List<Product>> allProductsByUserInput;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application);
        allProducts = repository.getAllProducts();
        allProductsByGroup = repository.getAllProductsByGroupResults();
        allProductsByUserInput = repository.getAllProductsByUserInputResults();
    }

    public void insert(Product product) {
        repository.insert(product);
    }

    public void update(Product product) {
        repository.update(product);
    }

    public void delete(Product product) {
        repository.delete(product);
    }

    public void getAllProductsByGroup(int groupId) {
        repository.getAllProductsByGroup(groupId);
    }

    public void getAllProductsByUserInput(String userInput) {
        repository.getAllProductsByUserInput(userInput);
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<List<Product>> getAllProductsByGroupResults() {
        return allProductsByGroup;
    }

    public LiveData<List<Product>> getAllProductsByUserInputResults() {
        return allProductsByUserInput;
    }

}
