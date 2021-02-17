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


public class GroupRepository {

    private GroupDao groupDao;
    private LiveData<List<Group>> allGroups;


    public GroupRepository(Application application) {
        InventoryDatabase database = InventoryDatabase.getInstance(application);
        groupDao = database.groupDao();
        allGroups = groupDao.getAllGroups();
    }


    public void insert(Group group) {
        new InsertGroupAsyncTask(groupDao).execute(group);
    }


    public void update(Group group) {
        new UpdateGroupAsyncTask(groupDao).execute(group);
    }


    public void delete(Group group) {
        new DeleteGroupAsyncTask(groupDao).execute(group);
    }


    public LiveData<List<Group>> getAllGroups() {
        return allGroups;
    }


    private static class InsertGroupAsyncTask extends AsyncTask<Group, Void, Void> {
        private GroupDao groupDao;

        private InsertGroupAsyncTask(GroupDao groupDao) {
            this.groupDao = groupDao;
        }

        @Override
        protected Void doInBackground(Group... groups) {
            groupDao.insert(groups[0]);
            return null;
        }
    }

    private static class UpdateGroupAsyncTask extends AsyncTask<Group, Void, Void> {
        private GroupDao groupDao;

        private UpdateGroupAsyncTask(GroupDao groupDao) {
            this.groupDao = groupDao;
        }

        @Override
        protected Void doInBackground(Group... groups) {
            groupDao.update(groups[0]);
            return null;
        }
    }

    private static class DeleteGroupAsyncTask extends AsyncTask<Group, Void, Void> {
        private GroupDao groupDao;

        private DeleteGroupAsyncTask(GroupDao groupDao) {
            this.groupDao = groupDao;
        }

        @Override
        protected Void doInBackground(Group... groups) {
            groupDao.delete(groups[0]);
            return null;
        }
    }
}
