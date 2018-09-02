package ovlesser.webmovies.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ovlesser.webmovies.MovieBrowserActivity;
import ovlesser.webmovies.R;
import ovlesser.webmovies.model.FetchingJobService;
import ovlesser.webmovies.util.Http;

import static ovlesser.webmovies.util.Constant.ARG_MOVIE;
import static ovlesser.webmovies.util.Constant.KEY_MEOVIES;

/**
 * the fragment for displaying a movie
 */
public class ViewFragment extends Fragment {
    private static final String TAG = FetchingJobService.class.getSimpleName();

    private JSONObject mMovie;
    private String mGenre;
    public OtherMovieAdapter mOtherMovieAdapter;
    private RecyclerView mRecyclerView;

    public static ViewFragment newInstance(String movie) {
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE, movie);
        ViewFragment fragment = new ViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences(MovieBrowserActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String movies = prefs.getString(KEY_MEOVIES, "");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(movies);
        } catch (JSONException e) {
//            e.printStackTrace();
        }

        Bundle args = getArguments();
        try {
            if (args != null) {
                JSONObject movie = new JSONObject(args.getString(ARG_MOVIE));
                mGenre = movie.getString("Genre");
            } else {
                mGenre = mMovie.getString("Genre");
            }
        }
        catch (JSONException e) {
        }
        mOtherMovieAdapter = new OtherMovieAdapter( jsonArray);
        mOtherMovieAdapter.getFilter().filter(mGenre);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().findViewById(R.id.overflow_back).setVisibility(View.VISIBLE);

        View contentView = inflater.inflate(R.layout.fragment_view, container, false);
        mRecyclerView = contentView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mOtherMovieAdapter);

        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            try {
                mMovie = new JSONObject(args.getString(ARG_MOVIE));
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            try {
                JSONObject movie = new JSONObject(args.getString(ARG_MOVIE));
                updateMovieView(movie);
            }
            catch (JSONException e) {
            }
        } else {
            updateMovieView(mMovie);
        }
    }

    public void updateMovieView(JSONObject movie) {
        String title = null;
        try {
            title = movie.getString("Title");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        TextView titleView = (TextView) getActivity().findViewById(R.id.title);
        titleView.setText(title);
        String rating = null;
        try {
            rating = movie.getString("Rating");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        String director = null;
        try {
            director = movie.getString("Director");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        String year = null;
        try {
            year = movie.getString("Year");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        TextView rdyView = (TextView) getActivity().findViewById(R.id.rating_director_year);
        rdyView.setText( String.format("%s - %s - %s", rating, director, year));

        String poster = null;
        try {
            poster = movie.getString("Poster");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        final String posterUrl = poster;
        final ImageView posterView = (ImageView) getActivity().findViewById(R.id.poster);
        posterView.post(new Runnable() {
            @Override
            public void run() {
                int requestedWidth = posterView.getWidth();
                if (requestedWidth > 0) {
                    Http.mPicasso
                            .load(posterUrl)
                            .fit().centerInside()
                            .into(posterView);
                }
            }
        });

        String plot = null;
        try {
            plot = movie.getString("Plot");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        TextView plotView = (TextView) getActivity().findViewById(R.id.plot);
        plotView.setText( plot);

        String writer = null;
        try {
            writer = movie.getString("Writer");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        String actors = null;
        try {
            actors = movie.getString("Actors");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        String language = null;
        try {
            language = movie.getString("Language");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        String country = null;
        try {
            country = movie.getString("Country");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        String awards = null;
        try {
            awards = movie.getString("Awards");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        String boxOffice = null;
        try {
            boxOffice = movie.getString("BoxOffice");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        TextView detailView = (TextView) getActivity().findViewById(R.id.detail);
        detailView.setText( String.format("%s, %s, %s, %s, %s, %s, %s, %s", writer, actors, plot, language, country, awards, rating, boxOffice));

        mMovie = movie;
    }

    public void update(JSONArray jsonArray) {
        mOtherMovieAdapter.update(jsonArray);
        mOtherMovieAdapter.getFilter().filter(mGenre);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ARG_MOVIE, mMovie.toString());
    }
}