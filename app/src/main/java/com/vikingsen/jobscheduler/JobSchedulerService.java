package com.vikingsen.jobscheduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    private static final String TAG = "JOBSchedulerService";

    private Map<Integer, JobTask> jobMap = new LinkedHashMap<>();

    /**
     * This runs on the main thread.
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        int jobId = params.getJobId();
        Log.d(TAG, "onStartJob: " + jobId);
        JobTask task = new JobTask(params);
        jobMap.put(jobId, task);
        task.execute(null, null);
        return true;
    }

    /**
     * This runs on the main thread.
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        int jobId = params.getJobId();
        Log.d(TAG, "onStopJob: " + jobId);
        AsyncTask task = jobMap.remove(jobId);
        if (task != null) {
            task.cancel(true);
        }
        return false; // Return true to retry
    }

    private class JobTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "JobTask";
        private JobParameters params;

        public JobTask(JobParameters params) {
            this.params = params;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "Running Job " + this.params.getJobId() + " On Thread: " + Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ignore) {
                // Ignore exception
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            jobFinished(params, !success);
            jobMap.remove(params.getJobId());
        }
    }
}
