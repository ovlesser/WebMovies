package ovlesser.webmovies;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;

import ovlesser.webmovies.model.FetchingJobService;
import ovlesser.webmovies.util.Http;
import ovlesser.webmovies.view.BrowserFragment;
import ovlesser.webmovies.view.ViewFragment;

import static ovlesser.webmovies.util.Constant.KEY_MEOVIES;
import static ovlesser.webmovies.util.Constant.MESSENGER_INTENT_KEY;
import static ovlesser.webmovies.util.Constant.MSG_UPDATE;

public class MovieBrowserActivity extends FragmentActivity {
    private static final String TAG = MovieBrowserActivity.class.getSimpleName();

    private int mJobId;
    private ComponentName mServiceComponent;
    private IncomingMessageHandler mHandler;
    private BrowserFragment mBrowserFragment;
    public ViewFragment mViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_browser);

        Http.init(this);
        mServiceComponent = new ComponentName(getApplication(), FetchingJobService.class);
        Intent startServiceIntent = new Intent(getApplication(), FetchingJobService.class);

        AppCompatImageView backImage = findViewById(R.id.overflow_back);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            mBrowserFragment = new BrowserFragment();
            mBrowserFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mBrowserFragment).commit();
        }
        mHandler = new IncomingMessageHandler(this);
        Messenger messengerIncoming = new Messenger(mHandler);
        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming);
        getApplication().startService(startServiceIntent);
//        mHandler.update(Constant.Status.RUNNING, "update");
    }

    @Override
    protected void onStart() {
        super.onStart();

        scheduleJob();
    }

    private void scheduleJob() {
        JobInfo.Builder builder = new JobInfo.Builder( mJobId++, mServiceComponent);
        builder.setRequiredNetworkType( JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(1)
                .setOverrideDeadline(1);
        SharedPreferences prefs = getApplication().getSharedPreferences(MovieBrowserActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        PersistableBundle extras = new PersistableBundle();
        builder.setExtras(extras);
        Log.d(TAG, "Scheduling job");
        JobScheduler tm = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());
    }

    private static class IncomingMessageHandler extends Handler {
        private WeakReference<Context> mContext;

        IncomingMessageHandler(Context context) {
            super();
            this.mContext = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            Context context = mContext.get();
            if (context == null) {
                return;
            }
            Message m;
            switch (msg.what) {
                case MSG_UPDATE:
                    update(msg.obj, "update");
                    break;
            }
        }

        private void update(@Nullable Object obj, String action) {
            Log.d(TAG, "update: ");
            MovieBrowserActivity context = (MovieBrowserActivity) mContext.get();
            SharedPreferences prefs =  context.getSharedPreferences(MovieBrowserActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            switch ( action) {
                case "update":
                    String movies = prefs.getString(KEY_MEOVIES, "");
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(movies);
                        TextView tipsView = context.findViewById(R.id.tips);
                        if (tipsView != null) {
                            tipsView.setVisibility(View.INVISIBLE);
                        }
                        if (context.mBrowserFragment != null) {
                            context.mBrowserFragment.update(jsonArray);
                        }
                        if (context.mViewFragment != null) {
                            context.mViewFragment.update(jsonArray);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
