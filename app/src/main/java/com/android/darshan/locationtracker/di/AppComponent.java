package com.android.darshan.locationtracker.di;

import com.android.darshan.locationtracker.MapsActivity;
import com.android.darshan.locationtracker.SignupActivity;
import com.android.darshan.locationtracker.view_models.LogInViewModel;
import com.android.darshan.locationtracker.view_models.MapsViewModel;
import com.android.darshan.locationtracker.view_models.SignupViewModel;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    //places where we are going to inject objects
    void inject(LogInViewModel logInViewModel);
    void inject(MapsViewModel mapsViewModel);
    void inject(SignupViewModel signupViewModel);
    void inject(MapsActivity mapsActivity);
    void inject(SignupActivity signupActivity);

}
