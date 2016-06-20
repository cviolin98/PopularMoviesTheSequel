package com.chrisblackledge.popularmoviesthesequel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chrisblackledge.popularmoviesthesequel.interfaces.MovieInterface;
import com.chrisblackledge.popularmoviesthesequel.model.MovieParcel_Content;

public class MainActivity extends AppCompatActivity implements MovieInterface, SharedPreferences.OnSharedPreferenceChangeListener {

    // flag for 2pane layout
    private boolean mTwoPane = false;

    // detail fragment tag
    private String mDetailTag = "";

    // selected movie parcel data
    private MovieParcel_Content mSelectedMovieParcel = null;

    // 2pane xml string
    private String mTwoPaneStr = "";

    // argument string for the movie parcel
    private String mMovieParcelStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // get xml strings
        mDetailTag = getString(R.string.fragment_tag_details);
        mTwoPaneStr = getString(R.string.arguments_two_pane);
        mMovieParcelStr = getString(R.string.parcel_data);

        // this will be valid in a 2 pane layout since this id belongs to the FrameLayout
        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.registerOnSharedPreferenceChangeListener(this);

            // In two-pane mode, show the detail view in this activity by adding or replacing the
            // detail fragment using a fragment transaction.
            if (savedInstanceState == null) {

                // display the default detail fragment prior to a movie selection
                DetailActivityFragment_Default fragment = new DetailActivityFragment_Default();

                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, mDetailTag)
                    .commit();
            }
            else {
                mSelectedMovieParcel = savedInstanceState.getParcelable(mMovieParcelStr);
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save the selected movie parcel data
        outState.putParcelable(mMovieParcelStr, mSelectedMovieParcel);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mTwoPane) {
            // if there is no selected movie, replace the detail fragment with a default fragment
            if(mSelectedMovieParcel == null) {
                DetailActivityFragment_Default fragment = new DetailActivityFragment_Default();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, mDetailTag)
                        .commit();
            }
        }
    }

    @Override
    public void refreshGrid() {
        // only needed in a tablet layout
        if (mTwoPane) {
            // refresh the grid since a favorite was removed
            ((MainActivityFragment)(getSupportFragmentManager().findFragmentById(R.id.fragment))).refreshGrid();

            // next, replace the detail fragment here with the default fragment
            DetailActivityFragment_Default fragment = new DetailActivityFragment_Default();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, mDetailTag)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(MovieParcel_Content movieParcel, int position) {

        // save the selected movie parcel data
        mSelectedMovieParcel = movieParcel;

        String movieDetailStr = getString(R.string.parcel_data);

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putInt(mTwoPaneStr, 1);
            args.putParcelable(movieDetailStr, movieParcel);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, mDetailTag)
                    .commit();
        } else {
            // create the intent to display the DetailActivity in 1pane mode
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(movieDetailStr, movieParcel);
            startActivity(intent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // preference change, so the selected movie should be set to null as it may not exist
        // under the new movie list
        mSelectedMovieParcel = null;
    }
}
