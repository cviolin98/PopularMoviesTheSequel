package com.chrisblackledge.popularmoviesthesequel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisblackledge.popularmoviesthesequel.adapter.TrailerListAdapter;
import com.chrisblackledge.popularmoviesthesequel.data.MovieContract;
import com.chrisblackledge.popularmoviesthesequel.interfaces.MovieInterface;
import com.chrisblackledge.popularmoviesthesequel.model.MovieParcel_Content;
import com.chrisblackledge.popularmoviesthesequel.model.VideoParcel_Content;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    // butterknife view binders
    @Nullable @BindView(R.id.detail_original_title) TextView mTitle = null;
    @Nullable @BindView(R.id.detail_rating) TextView mUserRating = null;
    @Nullable @BindView(R.id.detail_release_date) TextView mReleaseDate = null;
    @Nullable @BindView(R.id.detail_overview) TextView mOverview = null;
    @Nullable @BindView(R.id.detail_thumbnail) ImageView mPoster = null;
    @Nullable @BindView(R.id.set_favorite_button) Button mFavoriteButton = null;
    @Nullable @BindView(R.id.view_trailers_button) Button mTrailerButton = null;
    @Nullable @BindView(R.id.view_reviews_button) Button mReviewsButton = null;

    // butterknife unbinder
    private Unbinder mUnbinder = null;

    // formatters for the date
    private SimpleDateFormat mFormatFromAPI = null;
    private SimpleDateFormat mFormatOutput = null;
    private DecimalFormat mFormatDecimal = null;

    // string to support image display via picasso
    private String mBaseImgStr = "";

    // current movie id
    private int mMovieId = 0;

    // current movie detail data
    private MovieParcel_Content mMovieDetail = null;

    // flag for 2pane layout
    private boolean mIsTwoPane = false;

    // cursor loader id
    private static final int CURSOR_LOADER_ID = 123;

    // bitmap data array
    byte[] mImgByteArray = null;

    // content value data holder for database
    ContentValues mContentValues = new ContentValues();

    // argument string for 2pane layout
    private String mTwoPaneStr = "";

    // argument and intent string for movie id
    private String mMovieIdStr = "";

    // cursor loader
    private Loader mCursorLoader = null;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the xml strings
        mBaseImgStr = getString(R.string.api_base_image_url);
        mTwoPaneStr = getString(R.string.arguments_two_pane);
        mMovieIdStr = getString(R.string.arguments_movie_id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // initialize the formatters
        mFormatFromAPI = new SimpleDateFormat(getString(R.string.format_date_api));
        mFormatOutput = new SimpleDateFormat(getString(R.string.format_date_output));
        mFormatDecimal = new DecimalFormat(getString(R.string.format_rating_output));
    }

    @Optional @OnClick(R.id.set_favorite_button)
    public void setFavorite() {

        String setFavorite = getString(R.string.button_set_favorite);

        if(mFavoriteButton.getText().equals(setFavorite)) {
            String movieId = MovieContract.MovieEntry.COLUMN_MOVIE_ID;
            String releaseDate = MovieContract.MovieEntry.COLUMN_RELEASE_DATE;
            String userRating = MovieContract.MovieEntry.COLUMN_USER_RATING;
            String originalTitle = MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE;
            String overview = MovieContract.MovieEntry.COLUMN_OVERVIEW;

            // add the movie data to display and set into the database
            mContentValues.put(movieId, mMovieId);
            mContentValues.put(originalTitle, mMovieDetail.getOriginalTitle());
            mContentValues.put(overview, mMovieDetail.getOverview());
            mContentValues.put(releaseDate, mMovieDetail.getReleaseDate());
            mContentValues.put(userRating, mMovieDetail.getUserRating());

            // if removing the favorite, and then setting it, we have the poster data;
            // fill in, and readd to database
            if(mMovieDetail.getImagePosterURL() == null) {
                mImgByteArray = mMovieDetail.getImagePoster();

                // save to the database
                setFavoriteToDatabase();
            }
            else {
                // display the poster image
                Picasso
                        .with(getActivity())
                        .load(mBaseImgStr + mMovieDetail.getImagePosterURL())
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                // now that the bitmap has been loaded, compares and extract the byte data
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);

                                mImgByteArray = outputStream.toByteArray();

                                // save to the database
                                setFavoriteToDatabase();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                // display toast message due to image poster failing to download
                                Toast.makeText(getActivity(),
                                        "Failed to download image and set favorite!",
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        });
            }
        }
        else {

            // get a content resolver
            ContentResolver contentResolver = getActivity().getContentResolver();

            // create a uri based on a specific movie id
            Uri URI_WITH_ID = MovieContract.MovieEntry.buildMovieUri(mMovieId);

            // delete the movie from the database;  this returns how many entries were deleted
            int numDeleted = contentResolver.delete(URI_WITH_ID, null, null);

            // if we have deleted any database entries, update the button label to be able to
            // set the movie as a favorite
            if(numDeleted > 0) {
                mFavoriteButton.setText(setFavorite);
            }

            // get the shared preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String sortByParamKey = getString(R.string.sort_param);
            String sortByParamDefault = getString(R.string.sort_param_default);
            String sortByParamFavorites = getString(R.string.sort_param_favorites);

            // get the sort method
            String sortByParam = sharedPref.getString(sortByParamKey, sortByParamDefault);

            // only refresh the grid if we are currently displaying the favorites
            if(sortByParam.equals(sortByParamFavorites)) {
                // refresh the grid view and this fragment via this call since a favorite
                // has been removed when viewing the favorites
                ((MovieInterface) getActivity()).refreshGrid();
            }
        }
    }

    public void setFavoriteToDatabase() {

        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri CONTENT_URI = MovieContract.MovieEntry.CONTENT_URI;
        String POSTER = MovieContract.MovieEntry.COLUMN_POSTER;

        String removeFavorite = getString(R.string.button_remove_favorite);

        // add the poster data to the contentValues now that we have the byte data
        mContentValues.put(POSTER, mImgByteArray);

        // insert the data into the database
        contentResolver.insert(CONTENT_URI, mContentValues);

        // since we have added the movie to the database, update the button text to remove it
        mFavoriteButton.setText(removeFavorite);
    }

    @Optional @OnClick(R.id.view_trailers_button)
    public void viewTrailersActivity() {

        // either replace the fragment or launch a new activity
        if (mIsTwoPane) {
            Bundle args = new Bundle();
            args.putInt(mTwoPaneStr, 1);
            args.putInt(mMovieIdStr, mMovieId);

            TrailerActivityFragment fragment = new TrailerActivityFragment();
            fragment.setArguments(args);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, getString(R.string.fragment_tag_trailers))
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), TrailerActivity.class);
            intent.putExtra(mMovieIdStr, mMovieId);
            startActivity(intent);
        }
    }

    @Optional @OnClick(R.id.view_reviews_button)
    public void viewReviewsActivity() {

        // either replace the fragment or launch a new activity
        if (mIsTwoPane) {
            Bundle args = new Bundle();
            args.putInt(mTwoPaneStr, 1);
            args.putInt(mMovieIdStr, mMovieId);

            ReviewActivityFragment fragment = new ReviewActivityFragment();
            fragment.setArguments(args);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, getString(R.string.fragment_tag_reviews))
                .commit();
        } else {
            Intent intent = new Intent(getActivity(), ReviewActivity.class);
            intent.putExtra(mMovieIdStr, mMovieId);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = null;

        Bundle arguments = getArguments();

        // extract the arguments if they exist
        if (arguments != null) {
            int twoPane = arguments.getInt(mTwoPaneStr);
            mIsTwoPane = ((twoPane == 1) ? true : false);
        }

        // inflate the view
        returnView = inflater.inflate(R.layout.fragment_detail, container, false);

        // initialize the unbinder
        mUnbinder = ButterKnife.bind(this, returnView);

        return returnView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // butterknife unbinder
        mUnbinder.unbind();
    }

    /*
        This method determines if there is internet connectivity;  If so, the buttons to list
        reviews and trailers for favorites are not enabled.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get the shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String sortByParamKey = getString(R.string.sort_param);
        String sortByParamDefault = getString(R.string.sort_param_default);
        String sortParamFavorites = getString(R.string.sort_param_favorites);

        // get the sort method
        String sortByParam = sharedPref.getString(sortByParamKey, sortByParamDefault);

        String movieDetailStr = getString(R.string.parcel_data);

        Bundle arguments = getArguments();
        Intent myIntent = getActivity().getIntent();

        // reset member variable
        mMovieDetail = null;

        if (arguments != null) {
            int twoPane = arguments.getInt(mTwoPaneStr);
            mIsTwoPane = ((twoPane == 1) ? true : false);

            // get the movie detail (2pane mode)
            mMovieDetail = (MovieParcel_Content)(arguments.getParcelable(movieDetailStr));
        }

        // if the movieDetail is not set yet, then pull from intent data to support a 1pane device
        if((mMovieDetail == null) && (myIntent != null)) {
            // get the movie detail (1pane mode)
            mMovieDetail = (MovieParcel_Content)(myIntent.getParcelableExtra(movieDetailStr));
        }

        // extract the movie data
        String releaseDateStr = mMovieDetail.getReleaseDate();
        String userRating = String.valueOf(mMovieDetail.getUserRating());
        String imagePosterURL = mMovieDetail.getImagePosterURL();
        String originalTitle = mMovieDetail.getOriginalTitle();
        String overview = mMovieDetail.getOverview();
        int id = mMovieDetail.getId();

        // get the release date to reformat
        String outputDateStr = getString(R.string.date_unknown);

        // format the date
        try {
            outputDateStr = mFormatOutput.format(mFormatFromAPI.parse(releaseDateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // get and format the user rating
        double userRatingDbl = Double.valueOf(userRating);
        String outputRatingStr = mFormatDecimal.format(userRatingDbl);
        outputRatingStr += getString(R.string.rating_divisor);

        // update the fields
        mTitle.setText(originalTitle);
        mUserRating.setText(outputRatingStr);
        mReleaseDate.setText(outputDateStr);
        mOverview.setText(overview);

        // keep track of the movie id
        mMovieId = id;

        // if favorites, display the bitmap;  else, display via picasso
        if(sortByParam.equals(sortParamFavorites)) {
            Bitmap tempBitmap = null;
            byte[] tempImgData = mMovieDetail.getImagePoster();
            tempBitmap = BitmapFactory.decodeByteArray(tempImgData, 0, tempImgData.length);

            mPoster.setImageBitmap(tempBitmap);

            // remove the trailer and review buttons when in favorite mode to support offline
            // functionality
            if(!isNetworkAvailable()) {
                mTrailerButton.setEnabled(false);
                mReviewsButton.setEnabled(false);
            }
        }
        else {
            Picasso.with(getActivity())
                    .load(mBaseImgStr + imagePosterURL)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.no_image_thumb_detail)
                    .into(mPoster);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri URI_WITH_ID = MovieContract.MovieEntry.buildMovieUri(mMovieId);

        // return a new cursor loader
        return new CursorLoader(getActivity(),
                URI_WITH_ID,
                new String[]{MovieContract.MovieEntry._ID},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        String removeFavoriteStr = getString(R.string.button_remove_favorite);
        String setFavoriteStr = getString(R.string.button_set_favorite);

        // if favorite data is displayed for a movie, set the button text
        if (data != null && data.moveToFirst()) {
            mFavoriteButton.setText(removeFavoriteStr);
        }
        else {
            mFavoriteButton.setText(setFavoriteStr);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}