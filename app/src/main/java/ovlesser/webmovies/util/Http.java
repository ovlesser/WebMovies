package ovlesser.webmovies.util;

import android.content.Context;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * a wrapper of retrofit / picasso
 */
public class Http {
    private static String TAG = Http.class.getSimpleName();
    public static Retrofit mRetrofit = null;
    public static Picasso mPicasso = null;

    public static void init(Context context) {
        if (mRetrofit != null && mPicasso != null)
            return;
        final OkHttpClient clnt = makeOkHttpClient(context);
        if (clnt == null) {
            Log.w(TAG, "Retrofit and Piccaso are not sharing a OK HTTP client");
            mRetrofit = new Retrofit.Builder().build();
            mPicasso = Picasso.with(context);
        } else {
            Log.i(TAG, "Retrofit and Piccaso share a OK HTTP client");
            mRetrofit = new Retrofit.Builder().baseUrl("http://www.mocky.io").client(clnt).build();
            final Picasso.Builder picassoBuilder = new Picasso.Builder(context);
            picassoBuilder.downloader(new OkHttp3Downloader(clnt));
            mPicasso = picassoBuilder.build();
        }
    }

    /**
     * Whether the Http client is valid
     * @return true if initialised
     */
    public static boolean isInitialized() {
        return (mRetrofit != null && mPicasso != null);
    }

    private static OkHttpClient makeOkHttpClient(final Context context) {
        final File baseDir = context.getCacheDir();
        if( baseDir == null ) {
            Log.w( TAG, "Unable to create a shared OK HTTP client - no cache dir" );
            return null;
        }
        final CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(10, TimeUnit.SECONDS)
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .retryOnConnectionFailure(true);

        return clientBuilder.build();
    }
}
