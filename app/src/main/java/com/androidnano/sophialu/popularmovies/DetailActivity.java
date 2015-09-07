package com.androidnano.sophialu.popularmovies;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.ShareActionProvider;



public class DetailActivity extends ActionBarActivity {
    MovieDetailFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new MovieDetailFragment())
                    .commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.moviedetailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        FragmentManager fm = getSupportFragmentManager();
        fragment = (MovieDetailFragment) fm.findFragmentById(R.id.fragment_container);

        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null ) {
             mShareActionProvider.setShareIntent(fragment.createShareMovieIntent());
        } else {
              Log.d(fragment.LOG_TAG, "Share Action Provider is null?");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
