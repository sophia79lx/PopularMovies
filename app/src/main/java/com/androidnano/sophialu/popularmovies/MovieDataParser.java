package com.androidnano.sophialu.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by sophia.lu on 7/28/15.
 */
public class MovieDataParser {

    public static ArrayList<MovieData> getMovieDataFromJson(String moviesJsonStr)
            throws JSONException {

        JSONObject jsonObj = new JSONObject(moviesJsonStr);
        JSONArray movies = jsonObj.getJSONArray("results");

        ArrayList<MovieData> list = new ArrayList<MovieData>();

        for(int i = 0; i < movies.length(); i++) {

            JSONObject m = movies.getJSONObject(i);

            int id = m.getInt("id");
            String title = m.getString("title");
            String poster_path = m.getString("poster_path");
            String release_date = m.getString("release_date");
            String overview = m.getString("overview");
            String vote_average = m.getString("vote_average");

            MovieData map = new MovieData(id, title, poster_path, release_date, overview, vote_average);

            list.add(map);

        }

        return list;
    }
}
