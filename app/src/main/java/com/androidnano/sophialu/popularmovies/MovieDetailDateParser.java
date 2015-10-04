package com.androidnano.sophialu.popularmovies;

        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

/**
 * Created by sophia.lu on 9/25/15.
 */
public class MovieDetailDateParser {

    public static  HashMap<String, Object> getMovieDetailDataFromJson(String moviesJsonStr)
            throws JSONException {

        JSONObject jsonObj = new JSONObject(moviesJsonStr);
        JSONArray trailers = jsonObj.getJSONObject("trailers").getJSONArray("youtube");
        JSONArray reviews = jsonObj.getJSONObject("reviews").getJSONArray("results");

        ArrayList<MovieTrailerData> trailerList = new ArrayList<MovieTrailerData>();
        ArrayList<MovieReviewData> reviewList = new ArrayList<MovieReviewData>();

        for(int i = 0; i < trailers.length(); i++) {

            JSONObject m = trailers.getJSONObject(i);

            String name = m.getString("name");
            String source = m.getString("source");

            MovieTrailerData map = new MovieTrailerData(name, source);

            trailerList.add(map);
        }

        for(int i = 0; i < reviews.length(); i++) {

            JSONObject m = reviews.getJSONObject(i);

            String author = m.getString("author");
            String content = m.getString("content");

            MovieReviewData map = new MovieReviewData(author, content);

            reviewList.add(map);
        }


        HashMap<String, Object> map = new HashMap<>();
        map.put("trailerList", trailerList);
        map.put("reviewList", reviewList);

        return map;

    }
}
