package com.androidnano.sophialu.popularmovies;

/**
 * Created by sophia.lu on 7/28/15.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
public class MovieListFragment extends Fragment {


    ArrayList<MovieData> movieL;
    GridView gridView;
    String sortSequence;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    MovieListAdapter movieListAdapter;
    String sortBy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movieL = new ArrayList<MovieData>();
        } else {
            movieL = savedInstanceState.getParcelableArrayList("movies");
        }
        setHasOptionsMenu(true);
    }

    public MovieListFragment() {

    }

    public void updateMovies(String sortSequence) {
        new DownloadDataTask().execute(sortSequence);
    }

    public void showSortByDialog(){

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builderS = new AlertDialog.Builder(getActivity());

        String[] options = new String[] {"Sort By Highest-rated", "Sort By Popularity"};
        int selected = 0;
        String sortByPref = prefs.getString("sortBy", "popularity.desc");

        if (sortByPref.equals("popularity.desc")) {
            selected = 1;
        }

        // 2. Chain together various setter methods to set the dialog characteristics
        builderS.setTitle(R.string.sort_by_dialog_title)
                .setSingleChoiceItems(options, selected,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which)
                        {
                            case 0:
                                sortBy = "vote_average.desc";
                                updateMovies(sortBy);
                                break;
                            case 1:
                                sortBy = "popularity.desc";
                                updateMovies(sortBy);
                                break;
                            default:
                                break;
                        }
                    }
                });

        builderS.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                editor.putString("sortBy", sortBy)
                        .commit();

            }
        });
        builderS.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        //Get the Dialog from create()
        AlertDialog dialog = builderS.create();
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movielistfragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_sort_by) {
            showSortByDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieL != null) {
            outState.putParcelableArrayList("movies", movieL);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        editor = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE).edit();
        prefs = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String restoredText = prefs.getString("sortBy", null);

        if (restoredText == null) {
            sortSequence = "popularity.desc";
            editor.putString("sortBy", sortSequence)
                    .commit();
        } else {
            sortSequence = prefs.getString("sortBy", "popularity.desc");
        }

        if(savedInstanceState != null) {
            // read the person list from the saved state
            movieL = savedInstanceState.getParcelableArrayList("movies");
        } else {
            // load the person list
            movieL = new ArrayList<MovieData>();

            if (isNetworkAvailable()){

                updateMovies(sortSequence);

            } else {
                Context context = getActivity().getApplicationContext();
                String text = "No internet connection";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
                updateMovies(sortSequence);
        }


        gridView = (GridView) rootView.findViewById(R.id.movieList);



        movieListAdapter = new MovieListAdapter(getActivity(), movieL);

        gridView.setAdapter(movieListAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieData item = (MovieData) movieListAdapter.getItem(position);

                Intent intent = new Intent(parent.getContext(), DetailActivity.class);
                intent.putExtra("movieTitle", item.movieTitle);
                intent.putExtra("overview", item.movieOverview);
                intent.putExtra("poster_path", item.moviePosterPath);
                intent.putExtra("vote_average", item.movieVoteAverage);
                intent.putExtra("release_date", item.movieReleaseDate);

                startActivity(intent);
            }
        });


        return rootView;
    }



    private class DownloadDataTask extends AsyncTask<String, Void, ArrayList<MovieData>> {

        @Override
        protected ArrayList<MovieData> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String apiKey = "ed6798c81aeed8c73e546af867778d28";
            String page = "1";
            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String MOVIE_DATA_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";
                final String PAGE = "page";

                Uri buildUri = Uri.parse(MOVIE_DATA_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(PAGE, page)
                        .appendQueryParameter(API_KEY, apiKey)
                        .build();

                URL url = new URL(buildUri.toString());

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


                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
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
                return MovieDataParser.getMovieDataFromJson(moviesJsonStr);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> result) {
            super.onPostExecute(result);

            if (result != null) {

                movieListAdapter.clear();

                for (int i = 0; i < result.size(); i++) {
                    MovieData item = result.get(i);
                    movieListAdapter.add(item);
                }

            }
            movieListAdapter.notifyDataSetChanged();
        }


    }
}
