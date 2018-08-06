package com.android.darshan.locationtracker.models;

/**
 * Created by Darshan B.S on 05-08-2018.
 */

public class User {
    private String userName, phoneNumber, passWord;
    private int userID;
    private Location lastLocation;

    public User() {
    }

    public User(String userName, String phoneNumber, String passWord) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.passWord = passWord;
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", passWord='" + passWord + '\'' +
                ", userID=" + userID +
                ", lastLocation=" + lastLocation +
                '}';
    }
}
