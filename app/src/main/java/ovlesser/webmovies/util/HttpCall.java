package ovlesser.webmovies.util;

import android.util.Log;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * wrapper of calback of retrofit
 */
abstract class HttpCall implements Callback< ResponseBody > {
    private static final String TAG = HttpCall.class.getSimpleName();

    protected final String mUrl;
    private final boolean mExpectResponseBody;

    HttpCall(final String url, final boolean expectResponseBody) {
        mUrl = url;
        mExpectResponseBody = expectResponseBody;
    }

    @Override
    public final void onFailure(Call<ResponseBody> call, Throwable t) {
        onNetworkingProblem(call.request().body(), t );
    }

    @Override
    public final void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
        okhttp3.Response rawResponse = response.raw();
        boolean isFromNet = (rawResponse.networkResponse() != null);
        boolean isFromCache = (rawResponse.cacheResponse() != null);
        Log.d(TAG, String.format("onResponse: cache=%s, net=%s %s now=%d",
                isFromCache, isFromNet, call.request().method(), System.currentTimeMillis()));

        if( !response.isSuccessful()) {
            // HTTP status is outside of [200..300)
            onFailure( call, response );
            return;
        }

        String body = "";
        if( mExpectResponseBody ) {
            try {
                body = response.body().string();
                final int len = body.length();
                Log.d( TAG, String.format( "Read %d bytes %s", len, mUrl ));
                if( body.isEmpty() ) {
                    onFailure( call, response );
                    return;
                }
            } catch ( final Throwable t ) {
                onFailure( call, t );
                return;
            }
        }
        onSuccess( response, body );
    }

    protected void onNetworkingProblem(RequestBody body, Throwable t){
        Log.e(TAG, String.format("onNetworkingProblem: %s\n%s", mUrl, body), t);
    }

    protected void onFailure(final Call<ResponseBody> call, final Response<ResponseBody> response) {
        Log.e(TAG, String.format("onFailure: Unable to %s %s", call.request().method(), mUrl));
    }

    protected void onSuccess(final Response<ResponseBody> response, final String body) {
        Log.i(TAG, String.format("onSuccess: code=%d size=%d", response.code(), body.length()));
    }
}
