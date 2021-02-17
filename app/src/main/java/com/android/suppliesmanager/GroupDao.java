package com.android.suppliesmanager;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface GroupDao {

    @Insert
    void insert(Group group);

    @Update
    void update(Group group);

    @Delete
    void delete(Group group);

    @Query("SELECT * FROM group_table")
    LiveData<List<Group>> getAllGroups();

}
