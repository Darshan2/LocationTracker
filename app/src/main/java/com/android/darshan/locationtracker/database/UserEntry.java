package com.android.darshan.locationtracker.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "user_details")
public class UserEntry {
    @ColumnInfo(name = "user_name")
    private String userName;
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    @ColumnInfo(name = "pass_word")
    private String passWord;

    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String latitude;
    private String longitude;


    @Ignore
    public UserEntry(String userName, String phoneNumber, String passWord, String latitude, String longitude) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.passWord = passWord;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UserEntry(String userName, String phoneNumber, String passWord, int id, String latitude, String longitude) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.passWord = passWord;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "UserEntry{" +
                "userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", passWord='" + passWord + '\'' +
                ", id=" + id +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
