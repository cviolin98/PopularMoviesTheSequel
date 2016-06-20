package com.chrisblackledge.popularmoviesthesequel.interfaces;

import com.chrisblackledge.popularmoviesthesequel.model.MovieParcel_Content;

/**
 * A callback interface that all activities containing this fragment must
 * implement. This mechanism allows activities to be notified of item
 * selections and to refresh the movie grid.
 */
public interface MovieInterface {

    /**
     * Callback for when an item has been selected.
     */
    public void onItemSelected(MovieParcel_Content movieParcel, int position);

    /**
     * Callback to refresh the movie grid.
     */
    public void refreshGrid();
}