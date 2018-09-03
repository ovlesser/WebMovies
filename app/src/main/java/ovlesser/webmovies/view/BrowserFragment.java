package ovlesser.webmovies.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ovlesser.webmovies.MovieBrowserActivity;
import ovlesser.webmovies.R;

import static ovlesser.webmovies.util.Constant.KEY_MEOVIES;

/**
 * the fragment for displaying the list of the movie
 */
public class BrowserFragment extends Fragment {
    private String TAG = BrowserFragment.class.getSimpleName();

    private String mGenre;
    public BrowserAdapter mBrowserAdapter;
    public ArrayAdapter<String> mGenreAdapter;
    private RecyclerView mRecyclerView;

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

        mBrowserAdapter = new BrowserAdapter(jsonArray);
        mBrowserAdapter.getFilter().filter(mGenre);
        mGenreAdapter =  new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, getGenreList(jsonArray));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().findViewById(R.id.overflow_back).setVisibility(View.INVISIBLE);

        View contentView = inflater.inflate(R.layout.fragment_browser, container, false);

        contentView.findViewById(R.id.tips).setVisibility(View.INVISIBLE);
        mRecyclerView = contentView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mBrowserAdapter);

        mGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = contentView.findViewById(R.id.filter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGenre = mGenreAdapter.getItem(position);
                mBrowserAdapter.getFilter().filter(mGenre);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(mGenreAdapter);

        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void update(JSONArray jsonArray) {
        if (jsonArray != null) {
            mBrowserAdapter.update(jsonArray);
            mBrowserAdapter.notifyDataSetChanged();
            mBrowserAdapter.getFilter().filter(mGenre);
            if (getContext() != null) {
                mGenreAdapter.clear();
                for (String genre : getGenreList(jsonArray)) {
                    mGenreAdapter.insert(genre, mGenreAdapter.getCount());
                }
                mGenreAdapter.notifyDataSetChanged();
            }
        }
    }

    private List<String> getGenreList(JSONArray jsonArray) {
        List<String> genres = new ArrayList<>();
        genres.add("All Genres");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                String genre = null;
                try {
                    genre = jsonArray.getJSONObject(i).getString("Genre");
                    genres.add(genre);
                } catch (JSONException e) {
//                e.printStackTrace();
                }
            }
        }
        return genres;
    }

}
