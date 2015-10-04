package com.androidnano.sophialu.popularmovies;

/**
 * Created by sophia.lu on 7/29/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    protected static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String MOVIE_SHARE_HASHTAG = " #MovieApp";
    static final String MOVIE_URI = "URI";
    static final String MOVIE_TRAILER_URI = "TRAILER_URL";
    static final String MOVIE_REVIEW_URI = "REVIEW_URI";

    private String mMovieStr;
    public Context context;
    View rootView;
    String movieId;
    private MovieData mMovieData;
    ArrayList<MovieTrailerData> movieTrailerData;
    ArrayList<MovieReviewData> movieReviewData;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    public void updateMovieDetail() {
        new DownloadMovieDetailTask().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = getActivity();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_TRAILER_URI, movieTrailerData);
        outState.putParcelableArrayList(MOVIE_REVIEW_URI, movieReviewData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(MOVIE_URI)) {
            mMovieData = intent.getParcelableExtra(MOVIE_URI);
        } else if (getArguments() != null && getArguments().containsKey(MOVIE_URI)) {
            mMovieData = getArguments().getParcelable(MOVIE_URI);
        }

        String movieTitle = mMovieData.getMovieTitle();
        mMovieStr = mMovieData.getMovieTitle();
        String overview = mMovieData.getMovieOverview();
        String poster_path = mMovieData.getMoviePosterPath();
        String vote_average = mMovieData.getMovieVoteAverage();
        String release_date = mMovieData.getMovieReleaseDate();
        movieId = mMovieData.getMovieId() + "";


        if (savedInstanceState != null) {
            movieTrailerData = savedInstanceState.getParcelableArrayList(MOVIE_TRAILER_URI);
            movieReviewData = savedInstanceState.getParcelableArrayList(MOVIE_REVIEW_URI);
            showTrailers(movieTrailerData);
            showReviews(movieReviewData);
        } else {
            movieTrailerData = new ArrayList<MovieTrailerData>();
            movieReviewData = new ArrayList<MovieReviewData>();
            updateMovieDetail();
        }

        String[] parts = release_date.split("-");
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

        String posterPath = "http://image.tmdb.org/t/p/w185" + poster_path;

        Picasso.with(rootView.getContext())
                .load(posterPath)
                .into(posterImageView);


        setFavoriteButton();
        return rootView;
    }

    protected void setFavoriteButton(){

        Button likeBtn = (Button) rootView.findViewById(R.id.like_button);

        final FavoriteMovieSharePreference mSharePreference = new FavoriteMovieSharePreference();
        ArrayList<String> favorites = mSharePreference.getFavorites(context);

        if (favorites != null) {
            if(favorites.contains(movieId)) {
                likeBtn.setText("REMOVE FROM FAVORITE");
                likeBtn.setBackgroundResource(R.color.grey);
            } else {
                likeBtn.setText("MARK AS FAVORITE");
                likeBtn.setBackgroundResource(R.color.green);
            }
        }


        likeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Button likeBtn = (Button) v;
                String tag = likeBtn.getText().toString();
                if (tag.equalsIgnoreCase("MARK AS FAVORITE")) {
                    mSharePreference.addFavorite(getActivity(), movieId);
                    Toast.makeText(getActivity(),
                            "MARKED AS FAVORITE",
                            Toast.LENGTH_SHORT).show();

                    likeBtn.setText("REMOVE FROM FAVORITE");
                    likeBtn.setBackgroundResource(R.color.grey);
                } else {
                    mSharePreference.removeFavorite(getActivity(), movieId);
                    Toast.makeText(getActivity(),
                            "REMOVED FROM FAVORITE",
                            Toast.LENGTH_SHORT).show();
                    likeBtn.setText("MARK AS FAVORITE");
                    likeBtn.setBackgroundResource(R.color.green);
                }
            }
        });

    }

    protected Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieStr + MOVIE_SHARE_HASHTAG);

        return shareIntent;
    }

    public void showTrailers(final ArrayList<MovieTrailerData> movieTrailerData) {

        if (movieTrailerData.size() > 0) {
            TextView trailerTitle = (TextView) rootView.findViewById(R.id.movie_trailer_title);
            trailerTitle.setVisibility(View.VISIBLE);
            LinearLayout trailerList =  (LinearLayout) rootView.findViewById(R.id.trailer_list);
            trailerList.setVisibility(View.VISIBLE);

            for(int i = 0; i < movieTrailerData.size(); i++) {
                TextView textView = new TextView(context);
                String text = movieTrailerData.get(i).trailerName;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                textView.setPadding(20, 40, 20, 0);
                SpannableString content = new SpannableString(text);
                content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
                textView.setTextColor(Color.parseColor("#747474"));
                textView.setText(content);

                ImageView posterView = new ImageView(context);
                String YOUTUBE_IMAGE_URL_PREFIX = "http://img.youtube.com/vi/";
                String YOUTUBE_IMAGE_URL_SUFFIX = "/0.jpg";

                String posterPath = YOUTUBE_IMAGE_URL_PREFIX + movieTrailerData.get(i).trailerSource + YOUTUBE_IMAGE_URL_SUFFIX;
                Picasso.with(getActivity())
                        .load(posterPath)
                        .resize(200, 150)
                        .into(posterView);


                LinearLayout.LayoutParams list1;
                list1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                LinearLayout list = new LinearLayout(context);
                list.setLayoutParams(list1);
                list.setOrientation(LinearLayout.HORIZONTAL);


                list.addView(posterView);

                if(textView.getParent()!=null)
                    ((ViewGroup)textView.getParent()).removeView(textView);
                list.addView(textView);
                list.setPadding(0,25,0,0);

                if(list.getParent()!=null)
                    ((ViewGroup)list.getParent()).removeView(list);
                trailerList.addView(list);

                final int finalI = i;
                list.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + movieTrailerData.get(finalI).trailerSource)));
                    }
                });

            }
        }

    }

    public void showReviews(ArrayList<MovieReviewData> movieReviewData) {

        if (movieReviewData.size() > 0) {

            TextView reviewTitle = (TextView) rootView.findViewById(R.id.movie_review_title);
            reviewTitle.setVisibility(View.VISIBLE);

            LinearLayout reviewList =  (LinearLayout) rootView.findViewById(R.id.review_list);
            reviewList.setVisibility(View.VISIBLE);

            for(int i = 0; i <movieReviewData.size(); i++) {
                TextView textViewName = new TextView(context);

                String text = "From: " + movieReviewData.get(i).reviewAuthor;
                textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textViewName.setPadding(20, 40, 20, 0);
                SpannableString content = new SpannableString(text);
                content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
                textViewName.setText(content);
                textViewName.setTextColor(Color.parseColor("#747474"));

                reviewList.addView(textViewName);

                TextView textViewContent = new TextView(context);
                String reviewContent = movieReviewData.get(i).reviewContent;
                textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                textViewContent.setPadding(50, 15, 20, 0);
                textViewContent.setText(reviewContent);
                textViewContent.setTextColor(Color.parseColor("#747474"));
                reviewList.addView(textViewContent);
            }
        }
    }


    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    private class DownloadMovieDetailTask extends AsyncTask<String, Void, HashMap<String, Object>> {

        @Override
        protected HashMap<String, Object> doInBackground(String... params) {
            //load movie reviews and trailers
            //TODO: NEED TO REPLACE WITH THE REAL APIKEY IN README
            String apiKey = "00000000000000000000000000000";
            String trailerReviewURL = "http://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey + "&append_to_response=trailers,reviews";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieReviewJsonStr = null;

            try {
                URL url = new URL(trailerReviewURL);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }


                movieReviewJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return MovieDetailDateParser.getMovieDetailDataFromJson(movieReviewJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final HashMap<String, Object> result) {
            super.onPostExecute(result);

            movieTrailerData = (ArrayList<MovieTrailerData>) result.get("trailerList");
            movieReviewData = (ArrayList<MovieReviewData>) result.get("reviewList");

            showTrailers(movieTrailerData);
            showReviews(movieReviewData);
        }
    }
}