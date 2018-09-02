package ovlesser.webmovies.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ovlesser.webmovies.R;
import ovlesser.webmovies.util.Http;

/**
 * the adapter of thr recycler view for displaying the movies with same genre in movie fragment
 */
public class OtherMovieAdapter extends RecyclerView.Adapter<OtherMovieAdapter.ItemHolder> implements Filterable {
    private String TAG = OtherMovieAdapter.class.getSimpleName();
    private List<Item> items;
    private List<Item> filteredItems;
    private OtherMovieAdapter.ValueFilter valueFilter;

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new OtherMovieAdapter.ValueFilter();
        }
        return valueFilter;
    }

    public class ValueFilter extends Filter {
        @Override
        public FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.toString().compareTo("All Genres") != 0 && constraint.length() > 0) {
                List<Item> filterList = new ArrayList<Item>();
                for (int i = 0; i < items.size(); i++) {
                    String genre = items.get(i).getValue("Genre");
                    if (genre.compareTo(constraint.toString()) == 0) {
                        Item item = new Item(items.get(i));
                        filterList.add(item);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = items.size();
                results.values = items;
            }
            return results;

        }

        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (ArrayList<Item>) results.values;
            notifyDataSetChanged();
        }
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView posterView;

        public ItemHolder(View view) {
            super(view);
            posterView = view.findViewById(R.id.poster);
        }
    }

    public OtherMovieAdapter(JSONArray jsonArray) {
        this.items = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                this.items.add( new Item(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.filteredItems = new ArrayList<>(this.items);
    }

    public void update(JSONArray jsonArray) {
        this.items.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                this.items.add( new Item(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public OtherMovieAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_other, parent, false);

        return new OtherMovieAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OtherMovieAdapter.ItemHolder holder, int position) {
        final Item item = filteredItems.get(position);
        holder.posterView.post(new Runnable() {
            @Override
            public void run() {
                int requestedWidth = holder.posterView.getWidth();
                if (requestedWidth > 0) {
                    Http.mPicasso
                            .load(item.getValue("Poster"))
                            .fit().centerInside()
                            .into(holder.posterView);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }
}
