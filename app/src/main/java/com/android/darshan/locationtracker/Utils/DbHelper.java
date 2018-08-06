package com.android.darshan.locationtracker.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.darshan.locationtracker.Utils.Consts.*;
import com.android.darshan.locationtracker.models.Location;
import com.android.darshan.locationtracker.models.User;

import java.util.ArrayList;


/**
 * Created by Darshan B.S on 05-08-2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";

    private static final String DATABASE_NAME = "LocationTracker.db";
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase db;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        Log.d(TAG, "onCreate: ");

        final String SQL_CREATE_USER_DETAILS_TABLE = "CREATE TABLE " +
                Consts.UserDetailsTable.TABLE_NAME + " ( " +
                UserDetailsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserDetailsTable.COLUMN_USER_NAME + " TEXT, " +
                UserDetailsTable.COLUMN_PHONE_NUMBER + " TEXT, " +
                UserDetailsTable.COLUMN_PASSWORD + " TEXT, " +
                UserDetailsTable.COLUMN_LATITUDE + " TEXT, " +
                UserDetailsTable.COLUMN_LONGITUDE + " TEXT " +
                ")";

        db.execSQL(SQL_CREATE_USER_DETAILS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: ");
        //Delete old table.
        db.execSQL("DROP TABLE IF EXISTS " + UserDetailsTable.TABLE_NAME);
        //Create new table with updated info
        onCreate(db);
    }


    public void addNewUserToDB(User user) {
        Log.d(TAG, "addNewUserToDB: ");
        SQLiteDatabase writableDB = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDetailsTable.COLUMN_USER_NAME, user.getUserName());
        contentValues.put(UserDetailsTable.COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        contentValues.put(UserDetailsTable.COLUMN_PASSWORD, user.getPassWord());
        contentValues.put(UserDetailsTable.COLUMN_LATITUDE, "");
        contentValues.put(UserDetailsTable.COLUMN_LONGITUDE, "");

        writableDB.insert(UserDetailsTable.TABLE_NAME, null, contentValues);
    }


    public User getUserWith(String userName, String passWord) {
        Log.d(TAG, "getUserWithUserName: ");
        SQLiteDatabase readableDb = getReadableDatabase();

        String query = "SELECT * FROM "+ UserDetailsTable.TABLE_NAME + " WHERE "
                + UserDetailsTable.COLUMN_USER_NAME + " = '" + userName + "' AND "
                + UserDetailsTable.COLUMN_PASSWORD + " = '" + passWord +  "'";

        Cursor cursor = readableDb.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            User user = new User();
            user.setUserID(cursor.getInt(cursor.getColumnIndex(UserDetailsTable._ID)));
            user.setUserName(cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_USER_NAME)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_PHONE_NUMBER)));
            user.setPassWord(cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_PASSWORD)));

            String lastLatitude = cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_LATITUDE));
            String lastLongitude = cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_LONGITUDE));
            user.setLastLocation(new Location(lastLatitude, lastLongitude));

            cursor.close();
            return user;

        } else {
           return null;
        }
    }


    public ArrayList<User> getAllUsersWithUserName(String userName) {
        Log.d(TAG, "getAllUsersWithUserName: ");
        SQLiteDatabase readableDb = getReadableDatabase();
        ArrayList<User> usersArrayList = new ArrayList<>();

        String query = "SELECT * FROM "+ UserDetailsTable.TABLE_NAME + " WHERE "
                + UserDetailsTable.COLUMN_USER_NAME + " = '" + userName + "'";

        Cursor cursor = readableDb.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUserID(cursor.getInt(cursor.getColumnIndex(UserDetailsTable._ID)));
                user.setUserName(cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_USER_NAME)));
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_PHONE_NUMBER)));
                user.setPassWord(cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_PASSWORD)));

                String lastLatitude = cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_LATITUDE));
                String lastLongitude = cursor.getString(cursor.getColumnIndex(UserDetailsTable.COLUMN_LONGITUDE));
                user.setLastLocation(new Location(lastLatitude, lastLongitude));

                usersArrayList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return usersArrayList;
    }


    public int updateLastLocation(Location location, String userName) {
        Log.d(TAG, "updateLastLocation: " + userName);
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDetailsTable.COLUMN_LATITUDE, location.getLatitude());
        contentValues.put(UserDetailsTable.COLUMN_LONGITUDE, location.getLongitude());

        String selection = UserDetailsTable.COLUMN_USER_NAME + " = ?";
        String[] selectionArgs = { userName };

        int updatedRowsCount = db.update(
                UserDetailsTable.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs
        );
        
        if(updatedRowsCount > 0) {
            Log.d(TAG, "updateLastLocation: update success");
        }
        
        return updatedRowsCount;

    }
}
