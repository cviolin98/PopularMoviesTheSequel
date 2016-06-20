package com.chrisblackledge.popularmoviesthesequel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.chrisblackledge.popularmoviesthesequel.adapter.MovieListAdapter;
import com.chrisblackledge.popularmoviesthesequel.data.MovieContract;
import com.chrisblackledge.popularmoviesthesequel.data.MovieDBHelper;
import com.chrisblackledge.popularmoviesthesequel.interfaces.MovieInterface;
import com.chrisblackledge.popularmoviesthesequel.model.MovieParcel_Content;
import com.chrisblackledge.popularmoviesthesequel.model.MovieParcel_List;
import com.chrisblackledge.popularmoviesthesequel.interfaces.GetAdapter;
import com.chrisblackledge.popularmoviesthesequel.task.GetImageSizeTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // butterknife for gridView to display the grid of movie posters
    public @BindView(R.id.gridView) GridView mGridView = null;

    // butterknife unbinder
    private Unbinder mUnbinder = null;

    // list adapter to support the gridView
    private MovieListAdapter mMovieListAdapter = null;

    // arrayList of MovieParcel_Content data to support the gridView
    private List<MovieParcel_Content> mMovieList = new ArrayList<MovieParcel_Content>();

    // asyncTask to get the image size via picasso
    private GetImageSizeTask mGetImageSizeTask = null;

    // calculated width of an image in the gridView (assuming all are same dimensions)
    // default to the queried width
    private int mImageWidth = 185;

    // calculated height of an image in the gridView (assuming all are same dimensions)
    // default to a height
    private int mImageHeight = 278;

    // reference to this fragment
    private MainActivityFragment mThisFragment = null;

    // grid width
    public int mGridWidth = 0;

    // cursor loader id
    private static final int CURSOR_LOADER_ID = 456;

    // scroll position
    private int mSavedSelectedPosition = 0;

    // scroll state
    private int mScrollState = -1;

    // cursor loader
    private Loader mCursorLoader = null;

    // string to reference data property
    private String mSelectedPosStr = "";

    // api key
    private String mAPIKey = "";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the xml strings
        mSelectedPosStr = getString(R.string.state_data_selected_position);
        mAPIKey = getString(R.string.api_key);

        // reset the selected position
        mSavedSelectedPosition = 0;

        // if there is state data, get the selected position
        if(savedInstanceState != null) {
            mSavedSelectedPosition = savedInstanceState.getInt(mSelectedPosStr);
        }

        // enable the menu option to select the settings menu
        setHasOptionsMenu(true);
   }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // get the scroll position based upon the first visible position
        int selectedPosition = mGridView.getFirstVisiblePosition();

        // save it for restore (i.e. device rotation)
        savedInstanceState.putInt(mSelectedPosStr, selectedPosition);
    }

    @Override
    public void onResume () {
        super.onResume();

        // set a reference to this fragment
        mThisFragment = this;

        // initialize the task to determine an image size
        mGetImageSizeTask = new GetImageSizeTask(this);

        // if setting the grid width to the fragment width, it is often not known until later in
        // the processing causing null pointer exceptions;  as a result, the layoutChangeListener
        // is utilized and when it no longer equals 0, we can continue processing as we should
        // be able to otherwise;  this causes a problem when we want to restore and set the scrolled
        // position later on because the layoutChangeListener is called when the user scrolls;
        // as a result, a scroll listener needs to be created;  this is called when the user starts
        // scrolling;  when this happens, it is set to either 0 or 1, which we can then not call
        // the method to for the scroll position on restoration

        // if the grid width is 0...
        if(mGridWidth == 0) {
            mGridView.setOnScrollListener( new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // initialized to -1, but this sets to either 0 or 1
                    mScrollState = scrollState;
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });

            mGridView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                           int oldTop, int oldRight, int oldBottom) {
                if ((v.getWidth() > 0) && (v.getWidth() != mGridWidth)) {
                    mGridWidth = v.getWidth();

                    // now that we have a valid grid with, get the movie list
                    getMovieList();
                }

                // only call if -1 to not fight with the user scrolling and causing a
                // layoutChangeListener call
                if(mScrollState == -1) {
                    setGridViewSelection();
                }
                }
            });
        }
        else {
            // get the movie list
            getMovieList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_main, container, false);

        // butterknife binder
        mUnbinder = ButterKnife.bind(this, returnView);

        return returnView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // butterknife unbinder
        mUnbinder.unbind();
    }

    @Override
    public void onStop() {
        super.onStop();

        // reset the scroll position
        mSavedSelectedPosition = 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // launch the settings activity
        if (id == R.id.action_settings) {
            Intent i = new Intent(getActivity(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setGridViewSelection() {
        // set the scroll position for the grid view
        mGridView.setSelection(mSavedSelectedPosition);
    }

    public void clearMovieList() {
        // clear the movie list to rebuild it
        mMovieList.clear();
    }

    public void addToMovieList(MovieParcel_Content tempParcel) {
        // add to the movie list to later display them
        mMovieList.add(tempParcel);
    }

    public void setImageDimensions(int width, int height) {
        // set the image with and height to proportionally resize them based on grid width
        mImageWidth = width;
        mImageHeight = height;
    }

    public int getImageWidth() {
        // get the image width
        return mImageWidth;
    }

    public int getImageHeight() {
        // get the image height
        return mImageHeight;
    }

    public int getNumGridViewCols() {
        // get the number of columns in the grid
        return mGridView.getNumColumns();
    }

    public String getPosterURL(int position) {
        // get the poster url
        return mMovieList.get(position).getImagePosterURL();
    }

    public int getMovieListSize() {
        // get the number of movies
        return mMovieList.size();
    }

    public void openDetailActivity(MovieParcel_Content movieParcel, int position) {
        // call to the MainActivity which allows support for either 1 or 2pane layouts
        ((MovieInterface) getActivity()).onItemSelected(movieParcel, position);
    }

    public void displayMovies() {

        // initialize the movie list adapter
        mMovieListAdapter = new MovieListAdapter(getActivity(), mThisFragment, mMovieList);

        // set the adapter
        mGridView.setAdapter(mMovieListAdapter);

        // add a click listener to display movie details
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieParcel_Content tempParcel = mMovieListAdapter.getItem(position);
                openDetailActivity(tempParcel, position);
            }
        });
    }

    public int getGridWidth() {
        // get the grid width
        return mGridWidth;
    }

    public void getMovieList() {

        // clear the movie list since we are about to get a new set of movie data
        clearMovieList();

        String sortByParamKey = getString(R.string.sort_param);
        String sortByParamDefault = getString(R.string.sort_param_default);
        String sortByParamFavorites = getString(R.string.sort_param_favorites);

        // get the shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // get the sort method
        String sortByParam = sharedPref.getString(sortByParamKey, sortByParamDefault);

        // if displaying favorites, pull from the database
        if(sortByParam.equals(sortByParamFavorites)) {

            boolean initLoader = true;

            // initialize or restart the loader
            if(mCursorLoader == null) {
                if(getLoaderManager().getLoader(CURSOR_LOADER_ID) != null) {
                    if(getLoaderManager().getLoader(CURSOR_LOADER_ID).isStarted()) {
                        // restart the loader
                        initLoader = false;
                        mCursorLoader = getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
                    }
                }

                if(initLoader) {
                    // initialize the loader
                    mCursorLoader = getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
                }
            }
            else {
                // restart the loader
                mCursorLoader = getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
            }
        }
        else {
            GetAdapter ga = GetAdapter.retrofit.create(GetAdapter.class);
            Call<MovieParcel_List> call = ga.getMovieList(sortByParam, mAPIKey);

            call.enqueue(new Callback<MovieParcel_List>() {
                @Override
                public void onResponse(Call<MovieParcel_List> call, Response<MovieParcel_List> response) {
                    if(response.body() == null) {
                        // likely no api provided...  display toast
                        String badAPI = getString(R.string.no_movies_error);
                        Toast.makeText(getActivity(), badAPI, Toast.LENGTH_LONG).show();
                    }
                    else {
                        // get the movie list
                        mMovieList = response.body().getMovieParcels();

                        // determine an image size;  this will call displayMovies method once calculated
                        mGetImageSizeTask.execute();
                    }
                }

                @Override
                public void onFailure(Call<MovieParcel_List> call, Throwable t) {
                }
            });
        }
    }

    /*
        This method is called when a favorite has been removed in 2pane mode;  If we are displaying
        the favorites, we want to refresh the list and redisplay the updated list
     */
    public void refreshGrid() {
        // clear the list
        clearMovieList();

        // restart the loader to get the new movie list
        mCursorLoader = getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // return a new instance of the cursor loader
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                        MovieContract.MovieEntry.COLUMN_USER_RATING,
                        MovieContract.MovieEntry.COLUMN_POSTER,
                        MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
                        MovieContract.MovieEntry.COLUMN_OVERVIEW
                        },
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // if we have data returned via the cursor loader and there is data to display...
        if (data != null && data.moveToFirst()) {
            do {

                // get and extract the data
                String movieTitle = data.getString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE_INDEX);
                byte[] posterImg = data.getBlob(MovieContract.MovieEntry.COLUMN_POSTER_INDEX);
                int movieId = data.getInt(MovieContract.MovieEntry.COLUMN_MOVIE_ID_INDEX);
                String movieOverview = data.getString(MovieContract.MovieEntry.COLUMN_OVERVIEW_INDEX);
                String movieReleaseDate = data.getString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE_INDEX);
                String movieUserRating = data.getString(MovieContract.MovieEntry.COLUMN_USER_RATING_INDEX);

                // create content data to support the display of the detail data
                MovieParcel_Content tempMovieParcel = new MovieParcel_Content(
                        movieTitle,
                        null,
                        movieOverview,
                        Double.valueOf(movieUserRating),
                        movieReleaseDate,
                        posterImg,
                        movieId);

                // add to the movie list
                addToMovieList(tempMovieParcel);
            }
            while (data.moveToNext());
        }

        // favorites is designed to work without an internet connection since bitmap
        // data is stored in the database...  we can just make the call here to display them
        displayMovies();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}