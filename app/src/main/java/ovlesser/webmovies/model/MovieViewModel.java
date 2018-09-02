package ovlesser.webmovies.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

/**
 * view model for saving the movie data
 */
public class MovieViewModel extends AndroidViewModel {
    private static final String TAG = MovieViewModel.class.getSimpleName();

    public MovieViewModel(Application application) {
        super(application);

    }
}
