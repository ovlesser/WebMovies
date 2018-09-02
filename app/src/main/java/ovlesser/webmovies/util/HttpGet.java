package ovlesser.webmovies.util;

import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * wrapper of get method of retrofit callback
 */
public class HttpGet extends HttpCall {
    private static final String TAG = HttpGet.class.getSimpleName();

    interface Service {
        @GET
        Call<ResponseBody> get(
                @Url final String url,
                @QueryMap() final Map<String, String> options);
    }

    public HttpGet(final String url) {
        super( url, true );

        if (!Http.isInitialized()) return;
        Log.i( TAG, url );
        Uri uri = Uri.parse(url).normalizeScheme();
        HashMap<String, String> data = new HashMap<>();
        for (String name: uri.getQueryParameterNames()) {
            data.put(name, uri.getQueryParameter(name));
        }

        Http.mRetrofit
                .create( Service.class )
                .get( url, data )
                .enqueue( this );
    }
}
