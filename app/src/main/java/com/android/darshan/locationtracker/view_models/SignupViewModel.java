package com.android.darshan.locationtracker.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.darshan.locationtracker.database.UserEntry;
import com.android.darshan.locationtracker.database.UserRepository;

import java.util.List;


public class SignupViewModel extends AndroidViewModel {
    private LiveData<List<UserEntry>> mAllUsers;

    public SignupViewModel(@NonNull Application application) {
        super(application);

        //Not good practice(Tight coupling). Inject userRepository object using Dagger2
        UserRepository userRepository = new UserRepository(application);
        mAllUsers = userRepository.loadAllUsers();
    }


    public LiveData<List<UserEntry>> loadAllUsers() {
        return mAllUsers;
    }
}
