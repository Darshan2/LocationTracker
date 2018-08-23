package com.android.darshan.locationtracker.database;

import android.app.Application;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.util.Log;

import javax.inject.Inject;


@Database(entities = {UserEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    public static final String DATABASE_NAME = "Location_tracker";

    Application application;








//    //In order to avoid multiple AppDatabase object creation, in MultiThread execution.
//    private static final Object LOCK = new Object();
//    public static final String DATABASE_NAME = "Location_tracker";
//
//    private static AppDatabase sInstance;
//
//    public static AppDatabase getInstance(Application application) {
//        if (sInstance == null) {
//            synchronized (LOCK) {
//                Log.d(LOG_TAG, "Creating new database instance");
//                sInstance = Room.databaseBuilder(application,
//                        AppDatabase.class, AppDatabase.DATABASE_NAME)
//                        .build();
//            }
//        }
//        Log.d(LOG_TAG, "Getting the database instance");
//        return sInstance;
//    }

    public abstract UserDao userDao();
}
