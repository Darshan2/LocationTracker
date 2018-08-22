package com.android.darshan.locationtracker.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;



@Dao
interface UserDao {

    @Query("SELECT * FROM user_details ORDER BY _id")
    LiveData<List<UserEntry>> loadAllUsers();

    @Insert
    void insertUser(UserEntry userEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(UserEntry userEntry);

//    @Delete
//    void deleteUser(UserEntry userEntry);

    @Query("SELECT * FROM user_details WHERE _id = :id")
    LiveData<UserEntry> loadUserWithId(int id);

    @Query("SELECT * FROM user_details WHERE user_name = :userName")
    LiveData<UserEntry> loadUsersWithUserName(String userName);
}
