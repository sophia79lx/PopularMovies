package com.androidnano.sophialu.popularmovies;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sophia.lu on 7/28/15.
 */
public class MovieListAdapter extends ArrayAdapter<MovieData> {

    public MovieListAdapter(Context context, ArrayList<MovieData> resource) {
        super(context, R.layout.list_item_movie,  resource);
    }

    private static class ViewHolder {
        ImageView moviePosterView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the position
        MovieData resultm = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder =  new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_movie, parent, false);

            viewHolder.moviePosterView = (ImageView) convertView.findViewById(R.id.list_item_movie_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String posterPath =  "http://image.tmdb.org/t/p/w185" + resultm.moviePosterPath;

        Picasso.with(getContext())
                .load(posterPath)
                .into(viewHolder.moviePosterView);

        return convertView;
    }
}
