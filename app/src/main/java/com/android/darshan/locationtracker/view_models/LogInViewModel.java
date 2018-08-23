package com.android.darshan.locationtracker.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.darshan.locationtracker.database.UserEntry;
import com.android.darshan.locationtracker.database.UserRepository;
import com.android.darshan.locationtracker.di.MyApp;

import java.util.List;

import javax.inject.Inject;


public class LogInViewModel extends AndroidViewModel {
    private LiveData<List<UserEntry>> mAllUsers;

    @Inject UserRepository userRepository;

    public LogInViewModel(@NonNull Application application) {
        super(application);

        ((MyApp)application).getAppComponent().inject(this);

        mAllUsers = userRepository.loadAllUsers();
    }

    public LiveData<List<UserEntry>> loadAllUsers() {
        return mAllUsers;
    }
}
