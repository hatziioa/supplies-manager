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


public class GroupViewModel extends AndroidViewModel {

    private GroupRepository repository;
    private LiveData<List<Group>> allGroups;

    public GroupViewModel(@NonNull Application application) {
        super(application);
        repository = new GroupRepository(application);
        allGroups = repository.getAllGroups();
    }

    public void insert(Group group) {
        repository.insert(group);
    }

    public void update(Group group) {
        repository.update(group);
    }

    public void delete(Group group) {
        repository.delete(group);
    }

    public LiveData<List<Group>> getAllGroups() {
        return allGroups;
    }
}
