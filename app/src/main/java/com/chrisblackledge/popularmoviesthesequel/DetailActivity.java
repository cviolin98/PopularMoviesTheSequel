package com.chrisblackledge.popularmoviesthesequel;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chrisblackledge.popularmoviesthesequel.interfaces.MovieInterface;
import com.chrisblackledge.popularmoviesthesequel.model.MovieParcel_Content;

public class DetailActivity extends AppCompatActivity implements MovieInterface {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
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

        // added to support two-pane layout;
        // however, this is only applicable to a single-pane layout
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                   .add(R.id.movie_detail_container, new DetailActivityFragment())
                    .commit();
        }
    }

    @Override
    public void onItemSelected(MovieParcel_Content movieParcel, int position) {
        // added to support implements functionality
    }

    @Override
    public void refreshGrid() {
        // added to support implements functionality
    }
}
