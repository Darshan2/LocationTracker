package com.android.darshan.locationtracker.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.android.darshan.locationtracker.database.AppDatabase;
import com.android.darshan.locationtracker.database.UserRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    AppDatabase providesAppDatabase(Application application) {
       return Room.databaseBuilder(application,
                AppDatabase.class, AppDatabase.DATABASE_NAME)
                .build();
    }

    @Provides
    @Singleton
    UserRepository providesUserRepository(AppDatabase appDatabase) {
        return new UserRepository(appDatabase);
    }
}
