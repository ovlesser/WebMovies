package ovlesser.webmovies.view;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * item of the list of movie
 */
public class Item {
    private JSONObject json;
//    private String Title;
//    private String Year;
//    private String Rated;
//    private String Released;
//    private String Runtime;
//    private String Genre;
//    private String Director;
//    private String Writer;
//    private String Actors;
//    private String Plot;
//    private String Language;
//    private String Country;
//    private String Awards;
//    private String Poster;
//    private String Metascore;
//    private String Rating;
//    private String Votes;
//    private String Id;
//    private String Type;
//    private String DVD;
//    private String BoxOffice;
//    private String Production;
//    private String Website;

    public Item(JSONObject json) {
        this.json = json;
    }

    public Item(Item item) {
        this.json = item.json;
    }

    public JSONObject get() {
        return json;
    }

    public String getValue(String key) {
        String value = null;
        try {
            value = json.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public void setValue(String key, String value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
