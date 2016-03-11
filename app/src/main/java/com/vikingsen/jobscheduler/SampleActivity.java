package com.vikingsen.jobscheduler;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

public class SampleActivity extends Activity implements View.OnClickListener {

    private static final long INITIAL_DELAY = TimeUnit.SECONDS.toMillis(5);
    private static final long DEADLINE = TimeUnit.SECONDS.toMillis(10);
    private static final long JOB_SCHEDULER_INTERVAL = TimeUnit.SECONDS.toMillis(5);
    private static final long ANDROID_JOB_INTERVAL = TimeUnit.MINUTES.toMillis(1); // Minimum for periodic jobs

    private RadioButton androidJobRadioBtn;
    private RadioButton jobSchedulerRadioBtn;

    private enum Scheduler {JOB_SCHEDULER, ANDROID_JOB}

    private static int jobId = 0;

    private Scheduler scheduler;
    private JobScheduler jobScheduler;
    private ComponentName serviceComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        serviceComponent = new ComponentName(this, JobSchedulerService.class);

        jobSchedulerRadioBtn = (RadioButton) findViewById(R.id.jobSchedulerRadioBtn);
        androidJobRadioBtn = (RadioButton) findViewById(R.id.androidJobRadioBtn);
        jobSchedulerRadioBtn.setOnClickListener(this);
        androidJobRadioBtn.setOnClickListener(this);
        setupScheduler();

        Button scheduleOnceBtn = (Button) findViewById(R.id.scheduleOnceBtn);
        Button scheduleRepeatBtn = (Button) findViewById(R.id.scheduleRepeatBtn);
        scheduleOnceBtn.setOnClickListener(this);
        scheduleRepeatBtn.setOnClickListener(this);

        Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setupScheduler();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jobSchedulerRadioBtn:
                scheduler = Scheduler.JOB_SCHEDULER;
                break;
            case R.id.androidJobRadioBtn:
                scheduler = Scheduler.ANDROID_JOB;
                break;
            case R.id.scheduleOnceBtn:
                scheduleOnce();
                break;
            case R.id.scheduleRepeatBtn:
                scheduleRepeating();
                break;
            case R.id.cancelBtn:
                cancel();
                break;
        }
    }

    private void setupScheduler() {
        if (jobSchedulerRadioBtn.isChecked()) {
            scheduler = Scheduler.JOB_SCHEDULER;
        } else if (androidJobRadioBtn.isChecked()) {
            scheduler = Scheduler.ANDROID_JOB;
        }
    }

    private void scheduleOnce() {
        Toast.makeText(this, "JOB SCHEDULED ONCE: " + scheduler, Toast.LENGTH_SHORT).show();
        switch (scheduler) {
            case JOB_SCHEDULER:
                scheduleOnceJobScheduler();
                break;
            case ANDROID_JOB:
                scheduleOnceAndroidJob();
                break;
            default:
                throw new IllegalStateException("Invalid scheduler: " + scheduler);
        }
    }

    private void scheduleOnceJobScheduler() {
        JobInfo jobInfo = new JobInfo.Builder(jobId++, serviceComponent)
                .setMinimumLatency(INITIAL_DELAY)
                .setOverrideDeadline(DEADLINE) // If this is set verify requirements are met.
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    private void scheduleOnceAndroidJob() {
        JobRequest jobRequest = new JobRequest.Builder(AndroidJob.TAG)
                .setExecutionWindow(INITIAL_DELAY, DEADLINE)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .setRequirementsEnforced(true)
                .build();
        jobRequest.schedule();
    }

    private void scheduleRepeating() {
        Toast.makeText(this, "JOB SCHEDULED REPEAT: " + scheduler, Toast.LENGTH_SHORT).show();
        switch (scheduler) {
            case JOB_SCHEDULER:
                scheduleRepeatingJobScheduler();
                break;
            case ANDROID_JOB:
                scheduleRepeatingAndroidJob();
                break;
            default:
                throw new IllegalStateException("Invalid scheduler: " + scheduler);
        }

    }

    private void scheduleRepeatingJobScheduler() {
        JobInfo jobInfo = new JobInfo.Builder(jobId++, serviceComponent)
                .setPeriodic(JOB_SCHEDULER_INTERVAL)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    private void scheduleRepeatingAndroidJob() {
        JobRequest jobRequest = new JobRequest.Builder(AndroidJob.TAG)
                .setPeriodic(ANDROID_JOB_INTERVAL)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .setRequirementsEnforced(true)
                .build();
        jobRequest.schedule();
    }

    private void cancel() {
        Toast.makeText(this, "CANCEL: " + scheduler, Toast.LENGTH_SHORT).show();
        switch (scheduler) {
            case JOB_SCHEDULER:
                cancelJobScheduler();
                break;
            case ANDROID_JOB:
                cancelAndroidJob();
                break;
            default:
                throw new IllegalStateException("Invalid scheduler: " + scheduler);
        }
    }

    private void cancelJobScheduler() {
        jobScheduler.cancelAll(); // Can cancel specific id
    }

    private void cancelAndroidJob() {
        JobManager.instance().cancelAllForTag(AndroidJob.TAG);
    }
}
