package com.androidnano.sophialu.popularmovies;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by sophia.lu on 10/1/15.
 */
public class FavoriteMovieSharePreference {

    public static final String PREFS_NAME = "MOVIE_APP";
    public static final String FAVORITES = "Movie_Favorite";

    public FavoriteMovieSharePreference() {
        super();
    }


    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<String> favorites) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, String movieId) {
        List<String> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<String>();
        favorites.add(movieId);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, String movieId) {
        ArrayList<String> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(movieId);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<String> getFavorites(Context context) {
        SharedPreferences settings;
        List<String> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            String[] favoriteItems = gson.fromJson(jsonFavorites,
                    String[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<String>(favorites);
        } else
            return null;

        return (ArrayList<String>) favorites;
    }
}
