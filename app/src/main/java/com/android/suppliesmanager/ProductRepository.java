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
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class ProductRepository {

    private ProductDao productDao;
    private LiveData<List<Product>> allProducts;
    private MutableLiveData<List<Product>> allProductsByGroup = new MutableLiveData<>();
    private MutableLiveData<List<Product>> allProductsByUserInput = new MutableLiveData<>();


    public ProductRepository(Application application) {
        InventoryDatabase database = InventoryDatabase.getInstance(application);
        productDao = database.productDao();
        allProducts = productDao.getAllProducts();
    }


    public void insert(Product product) {
        new InsertProductAsyncTask(productDao).execute(product);
    }


    public void update(Product product) {
        new UpdateProductAsyncTask(productDao).execute(product);
    }


    public void delete(Product product) {
        new DeleteProductAsyncTask(productDao).execute(product);
    }


    public void getAllProductsByGroup(int groupId) {
        GetProductsByGroupAsyncTask task = new GetProductsByGroupAsyncTask(productDao);
        task.delegate = this;
        task.execute(groupId);
    }


    public void getAllProductsByUserInput(String userInput) {
        GetProductsByUserInputAsyncTask task = new GetProductsByUserInputAsyncTask(productDao);
        task.delegate = this;
        task.execute(userInput);
    }


    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }


    public MutableLiveData<List<Product>> getAllProductsByGroupResults() {
        return allProductsByGroup;
    }


    public MutableLiveData<List<Product>> getAllProductsByUserInputResults() {
        return allProductsByUserInput;
    }


    private static class InsertProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        private InsertProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.insert(products[0]);
            return null;
        }
    }

    private static class UpdateProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        private UpdateProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.update(products[0]);
            return null;
        }
    }

    private static class DeleteProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        private DeleteProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.delete(products[0]);
            return null;
        }
    }

    private static class GetProductsByGroupAsyncTask extends AsyncTask<Integer, Void, List<Product>> {

        private ProductDao productDao;
        private ProductRepository delegate = null;

        private GetProductsByGroupAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected List<Product> doInBackground(Integer... params) {
            return productDao.getAllProductsByGroup(params[0]);
        }

        @Override
        protected void onPostExecute(List<Product> result) {
            delegate.allProductsByGroup.setValue(result);
        }
    }

    private void asyncByGroupFinished(List<Product> results) {
        allProductsByGroup.setValue(results);
    }

    private static class GetProductsByUserInputAsyncTask extends AsyncTask<String, Void, List<Product>> {

        private ProductDao productDao;
        private ProductRepository delegate = null;

        private GetProductsByUserInputAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected List<Product> doInBackground(String... params) {
            return productDao.getAllProductsByUserInput(params[0]);
        }

        @Override
        protected void onPostExecute(List<Product> result) {
            delegate.allProductsByUserInput.setValue(result);
        }
    }

    private void asyncByUserInputFinished(List<Product> results) {
        allProductsByUserInput.setValue(results);
    }

}
