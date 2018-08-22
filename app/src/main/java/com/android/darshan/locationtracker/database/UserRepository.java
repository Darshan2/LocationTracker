package com.android.darshan.locationtracker.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;


public class UserRepository {
    private static final String TAG = "UserRepository";
    private UserDao mUserDao;

    public UserRepository(Application application) {
        mUserDao = AppDatabase.getInstance(application).userDao();
    }


    public LiveData<List<UserEntry>> loadAllUsers() {
        return mUserDao.loadAllUsers();
    }

    public LiveData<UserEntry> loadUserWithId(int id) {
        return mUserDao.loadUserWithId(id);
    }

    public LiveData<UserEntry> loadUsersWithUserName(String userName) {
        return mUserDao.loadUsersWithUserName(userName);
    }

    public void insertUser(UserEntry userEntry) {
        new DbAsyncTask(mUserDao, DbAsyncTask.INSERT_TASK).execute(userEntry);
    }

    public void updateUser(UserEntry userEntry) {
        Log.d(TAG, "updateUser: ");
        new DbAsyncTask(mUserDao, DbAsyncTask.UPDATE_TASK).execute(userEntry);
    }


    private static class DbAsyncTask extends AsyncTask<UserEntry, Void, Void> {

        private static final String INSERT_TASK = "insert";
        private static final String UPDATE_TASK = "update";

        private UserDao asyncUserDao;
        private String performTask;

        public DbAsyncTask(UserDao asyncUserDao, String performTask) {
            this.asyncUserDao = asyncUserDao;
            this.performTask = performTask;
        }

        @Override
        protected Void doInBackground(UserEntry... userEntries) {
            if(performTask.equals(INSERT_TASK)) {
                asyncUserDao.insertUser(userEntries[0]);
            } else if(performTask.equals(UPDATE_TASK)) {
                asyncUserDao.updateUser(userEntries[0]);
            }

            return null;
        }

    }

}
