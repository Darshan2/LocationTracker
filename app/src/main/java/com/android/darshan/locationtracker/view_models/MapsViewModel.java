package com.android.darshan.locationtracker.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.darshan.locationtracker.database.UserEntry;
import com.android.darshan.locationtracker.database.UserRepository;
import com.android.darshan.locationtracker.di.MyApp;

import javax.inject.Inject;


public class MapsViewModel extends AndroidViewModel {
    private static final String TAG = "MapsViewModel";

    @Inject UserRepository userRepository;

    private LiveData<UserEntry> mUserWithName;

    public MapsViewModel(@NonNull Application application, String userName) {
        super(application);
        Log.d(TAG, "MapsViewModel: created ");

//        //Tight coupling, Use dependency injection(using Dagger2) to inject this object later.
//        UserRepository userRepository = new UserRepository(application);
        ((MyApp)application).getAppComponent().inject(this);

        mUserWithName = userRepository.loadUsersWithUserName(userName);
    }

    public LiveData<UserEntry> getUserWithName() {
        return mUserWithName;
    }



    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private Application application;
        private String userName;

        public Factory(@NonNull Application application, String userName) {
            this.application = application;
            this.userName = userName;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //unchecked Cast
            return (T) new MapsViewModel(application, userName);
        }
    }
}
