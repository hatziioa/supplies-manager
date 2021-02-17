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

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {Product.class, Group.class}, version = 1)
public abstract class InventoryDatabase extends RoomDatabase {

    private static InventoryDatabase instance;


    public abstract ProductDao productDao();


    public abstract GroupDao groupDao();


    public static synchronized InventoryDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    InventoryDatabase.class, "inventory_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }


    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDatabaseAsyncTask(instance).execute();
        }
    };


    private static class PopulateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        private GroupDao groupDao;
        private ProductDao productDao;

        private PopulateDatabaseAsyncTask(InventoryDatabase db) {
            groupDao = db.groupDao();
            productDao = db.productDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            groupDao.insert(new Group("Shopping List"));
            groupDao.insert(new Group("Fridge"));
            groupDao.insert(new Group("Beverages"));
            groupDao.insert(new Group("Bathroom"));
            groupDao.insert(new Group("Pantry"));

            productDao.insert(new Product("Ice Cream",          1, "546020137962", 1,   "pack",   "3",      "", true));
            productDao.insert(new Product("Orange Juice",       1, "700757691486", 4,   "bottle", "1.10",   "", false));
            productDao.insert(new Product("Bread",              1, "163046283037", 2,   "loaf",   "0.80",   "", false));
            productDao.insert(new Product("Detergent",          1, "687194882780", 1,   "bottle", "1",      "", true));
            productDao.insert(new Product("Soap",               1, "397435117413", 1,   "piece",  "1.5",    "", false));
            productDao.insert(new Product("Batteries",          1, "967656961044", 1,   "pack",   "5",      "", true));
            productDao.insert(new Product("Peppers",            1, "753915290633", 1,   "kg",     "1.3",    "", false));
            productDao.insert(new Product("Tomatoes",           1, "069502678140", 2,   "kg",     "1.6",    "", false));
            productDao.insert(new Product("Clothes Detergent",  1, "488787355370", 1,   "box",    "7",      "", false));
            productDao.insert(new Product("Honey",              1, "369812079387", 2,   "jar",    "5",      "", true));
            productDao.insert(new Product("Oranges",            1, "868986380163", 2,   "kg",     "2",      "", false));
            productDao.insert(new Product("Napkins",            1, "632111969715", 1,   "pack",   "3",      "", false));
            productDao.insert(new Product("Bread",              1, "317156641679", 1,   "loaf",   "0.8",    "", false));
            productDao.insert(new Product("Milk",               2, "088262660548", 3,   "pack",   "1.2",    "", false));
            productDao.insert(new Product("Eggs",               2, "168114364070", 1,   "pack",   "2.2",    "", false));
            productDao.insert(new Product("Chocolate",          2, "164130270630", 3,   "piece",  "1.5",    "", true));
            productDao.insert(new Product("Yogurt",             2, "440990940092", 4,   "piece",  "1.3",    "", false));
            productDao.insert(new Product("Cheese",             2, "692429928134", 1,   "kg",     "5",      "", false));
            productDao.insert(new Product("Mayonnaise",         2, "368460897339", 1,   "piece",  "1.7",    "", false));
            productDao.insert(new Product("Mustard",            2, "556061456468", 1,   "piece",  "1.5",    "", false));
            productDao.insert(new Product("Ketchup",            2, "571216739409", 1,   "piece",  "1.3",    "", false));
            productDao.insert(new Product("Butter",             2, "289715707183", 1,   "piece",  "1.5",    "", false));
            productDao.insert(new Product("Wine",               3, "875871546646", 1,   "bottle", "10",     "", false));
            productDao.insert(new Product("Coffee",             3, "533430080490", 1,   "pack",   "3",      "", true));
            productDao.insert(new Product("Lemonade",           3, "766918374612", 1,   "bottle", "3",      "", false));
            productDao.insert(new Product("Iced tea",           3, "840851900757", 1,   "can",    "4",      "", true));
            productDao.insert(new Product("Beer",               3, "133031224050", 1,   "bottle", "2",      "", true));
            productDao.insert(new Product("Dental Floss",       4, "052544304383", 2,   "pack",   "3",      "", false));
            productDao.insert(new Product("Toothbrush",         4, "200705081704", 3,   "piece",  "2",      "", false));
            productDao.insert(new Product("Toothpaste",         4, "956493190055", 5,   "piece",  "4",      "", false));
            productDao.insert(new Product("Cotton Buds",        4, "956493190055", 1,   "pack",   "1.5",    "", true));
            productDao.insert(new Product("Olive oil",          5, "200813815130", 2,   "bottle", "8",      "", false));
            productDao.insert(new Product("Rice",               5, "776197223857", 3,   "pack",   "1.23",   "", true));
            productDao.insert(new Product("Olive Oil",          5, "331963242267", 5,   "bottle", "5.6",    "", false));
            productDao.insert(new Product("Flour",              5, "911141986125", 7,   "kg",     "1.3",    "", false));
            productDao.insert(new Product("Potatoes",           5, "639879489699", 2,   "kg",     "2",      "", false));
            productDao.insert(new Product("Coffee",             5, "259700466794", 2,   "piece",  "5",      "", false));
            productDao.insert(new Product("Sugar",              5, "126522616257", 1,   "pack",   "2.6",    "", false));

            return null;
        }
    }
}
