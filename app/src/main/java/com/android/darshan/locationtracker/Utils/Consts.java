package com.android.darshan.locationtracker.Utils;

import android.provider.BaseColumns;

/**
 * Created by Darshan B.S on 05-08-2018.
 */

public class Consts {
    public static String STARTFOREGROUND_ACTION = "action.startforeground";
    public static String STOPFOREGROUND_ACTION = "action.stopforeground";

    public static class UserDetailsTable implements BaseColumns {
        public static final String TABLE_NAME = "user_details";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_PASSWORD = "pass_word";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

    }
}
