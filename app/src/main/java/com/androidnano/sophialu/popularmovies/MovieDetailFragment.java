package com.androidnano.sophialu.popularmovies;

/**
 * Created by sophia.lu on 7/29/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    protected static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String MOVIE_SHARE_HASHTAG = " #MovieApp";

    private String mMovieStr;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        String movieTitle = getActivity().getIntent().getStringExtra("movieTitle");
        mMovieStr = getActivity().getIntent().getStringExtra("movieTitle");
        String overview = getActivity().getIntent().getStringExtra("overview");
        String poster_path = getActivity().getIntent().getStringExtra("poster_path");
        String vote_average = getActivity().getIntent().getStringExtra("vote_average");
        String release_date = getActivity().getIntent().getStringExtra("release_date");

        String[] parts= release_date.split("-");

        String release_year_diaplay = parts[0];

        String vote_average_diaplay = vote_average + "/10";


        TextView movieNameTextView = (TextView) rootView.findViewById(R.id.movie_title);
        TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.movie_release_date);
        TextView voteAverageTextView = (TextView) rootView.findViewById(R.id.movie_vote_average);
        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.movie_image);


        movieNameTextView.setText(movieTitle);

        releaseDateTextView.setText(release_year_diaplay);
        voteAverageTextView.setText(vote_average_diaplay);

        //app displays "null" if there is no review
        if (!overview.equals("null")) {
            TextView overviewTextView = (TextView) rootView.findViewById(R.id.movie_overview);
            overviewTextView.setText(overview);
        }

        String posterPath =  "http://image.tmdb.org/t/p/w185" + poster_path;

        Picasso.with(rootView.getContext())
                .load(posterPath)
                .into(posterImageView);

        return rootView;
    }

    protected Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieStr + MOVIE_SHARE_HASHTAG);

        return shareIntent;
    }
}