package com.android.darshan.locationtracker.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.darshan.locationtracker.database.UserEntry;
import com.android.darshan.locationtracker.database.UserRepository;

import java.util.List;


public class LogInViewModel extends AndroidViewModel {
    private LiveData<List<UserEntry>> mAllUsers;

    public LogInViewModel(@NonNull Application application) {
        super(application);

        UserRepository userRepository = new UserRepository(application);
        mAllUsers = userRepository.loadAllUsers();
    }

    public LiveData<List<UserEntry>> loadAllUsers() {
        return mAllUsers;
    }
}
