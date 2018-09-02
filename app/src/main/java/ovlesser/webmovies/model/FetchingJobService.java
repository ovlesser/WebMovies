package ovlesser.webmovies.model;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import ovlesser.webmovies.MovieBrowserActivity;
import ovlesser.webmovies.R;
import ovlesser.webmovies.util.HttpGet;
import retrofit2.Response;

import static ovlesser.webmovies.util.Constant.KEY_MEOVIES;
import static ovlesser.webmovies.util.Constant.MESSENGER_INTENT_KEY;
import static ovlesser.webmovies.util.Constant.MSG_JOB_START;
import static ovlesser.webmovies.util.Constant.MSG_JOB_STOP;
import static ovlesser.webmovies.util.Constant.MSG_UPDATE;

/**
 * Job service running in background to fetch the data
 */
public class FetchingJobService extends JobService {
    private static final String TAG = FetchingJobService.class.getSimpleName();

    private Messenger mActivityMessenger;
    private JobParameters mParams;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY);
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        sendMessage(MSG_JOB_START, params.getJobId());
        mParams = params;
        new MyAsyncTask( this).execute(this);
        Log.i(TAG, "on start job: " + params.getJobId());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        sendMessage(MSG_JOB_STOP, params.getJobId());
        Log.i(TAG, "on stop job: " + params.getJobId());
        return false;
    }

    private void sendMessage(int messageID, @Nullable Object params) {
        if (mActivityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.");
            return;
        }
        Message m = Message.obtain();
        m.what = messageID;
        m.obj = params;
        try {
            mActivityMessenger.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object back to activity.");
        }
    }

    public static class MyAsyncTask extends AsyncTask< Object, Void, Object> {
        private final WeakReference<Context> weakContext;

        MyAsyncTask(Context context) {
            this.weakContext = new WeakReference<>(context);
        }

        @Override
        protected Object doInBackground( Object... args) {
            final FetchingJobService fetchingJobService = (FetchingJobService) args[0];
            final Context context = weakContext.get();
            if (context != null) {
                new HttpGet(context.getString(R.string.url)) {
                    @Override
                    public void onSuccess(final Response<ResponseBody> response, final String body) {
                        Log.d(TAG, "onSuccess: " + this.mUrl);
                        SharedPreferences prefs =  context.getSharedPreferences(MovieBrowserActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                        prefs.edit().remove(KEY_MEOVIES).commit();
                        boolean ret = prefs.edit().putString(KEY_MEOVIES, body).commit();
                        fetchingJobService.sendMessage(MSG_UPDATE, Status.RUNNING);
                    }

                    @Override
                    protected void onNetworkingProblem(RequestBody body, Throwable t){
                        Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
                    }
                };
            }
            return args[0];
        }

        @Override
        protected void onPostExecute(Object params) {
            FetchingJobService fetchingJobService = (FetchingJobService) params;
            fetchingJobService.jobFinished(fetchingJobService.mParams, false);
//            fetchingJobService.sendMessage(MSG_JOB_STOP, fetchingJobService.mParams.getJobId());
        }
    }
}
