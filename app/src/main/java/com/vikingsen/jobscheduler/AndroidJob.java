package com.vikingsen.jobscheduler;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;

import java.util.concurrent.TimeUnit;

public class AndroidJob extends Job {
    public static final String TAG = "AndroidJob";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.d(TAG, "Running Job " + params.getId() + " On Thread: " + Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ignore) {
            // Ignore exception
        }
        return Result.SUCCESS;
    }
}
