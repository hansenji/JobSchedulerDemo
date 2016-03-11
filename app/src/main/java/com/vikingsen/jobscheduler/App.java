package com.vikingsen.jobscheduler;

import android.app.Application;

import com.evernote.android.job.JobManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager jobManager = JobManager.create(this);
        jobManager.addJobCreator(new AndroidJobCreator());
    }
}
