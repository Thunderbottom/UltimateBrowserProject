package io.github.UltimateBrowserProject.Application;

import android.app.Application;

import com.google.firebase.crash.FirebaseCrash;

public class UltimateBrowserProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                FirebaseCrash.report(ex);
            }
        });
    }

}
