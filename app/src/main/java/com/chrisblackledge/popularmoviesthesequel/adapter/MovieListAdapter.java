package com.chrisblackledge.popularmoviesthesequel.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.chrisblackledge.popularmoviesthesequel.MainActivityFragment;
import com.chrisblackledge.popularmoviesthesequel.R;
import com.chrisblackledge.popularmoviesthesequel.model.MovieParcel_Content;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;

public class MovieListAdapter extends ArrayAdapter<MovieParcel_Content> {
    private Context mContext;
    private LayoutInflater mInflater;
    private MainActivityFragment mActivityFragment;
    private String mBaseImgStr = "";
    private boolean mDisplayFavorites = false;

    public MovieListAdapter(Context context, MainActivityFragment activityFragment, List<MovieParcel_Content> imageUrls) {
        super(context, R.layout.movie_grid_image, imageUrls);

        this.mContext = context;
        this.mActivityFragment = activityFragment;
        this.mBaseImgStr = mActivityFragment.getString(R.string.api_base_image_url);

        // get the preferences to determine if the favorites should be displayed or not
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivityFragment.getActivity());

        String sortByParamKey = mActivityFragment.getActivity().getString(R.string.sort_param);
        String sortByParamDefault = mActivityFragment.getActivity().getString(R.string.sort_param_default);
        String sortByParamFavorites = mActivityFragment.getActivity().getString(R.string.sort_param_favorites);

        // get the sort method
        String sortByParam = sharedPref.getString(sortByParamKey, sortByParamDefault);

        if(sortByParam.equals(sortByParamFavorites)) {
            mDisplayFavorites = true;
        }

        mInflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            // get the parcel data
            MovieParcel_Content movieParcel = getItem(position);

            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.movie_grid_image, parent, false);
            }

            // get the fragment width
            int screenWidth = mActivityFragment.getGridWidth();

            // eventual width of each image
            int newImageWidth = (screenWidth / mActivityFragment.getNumGridViewCols());

            // original dimensions : mImageWidth and mImageHeight
            int newImageHeight = ((newImageWidth * mActivityFragment.getImageHeight()) / mActivityFragment.getImageWidth());

            // get the grid image
            ImageView gridImage = ButterKnife.findById(convertView, R.id.gridImage);

            // if true, display the favorites from the database
            if(mDisplayFavorites) {
                Bitmap tempBitmap = null;
                byte[] tempImgData = movieParcel.getImagePoster();
                tempBitmap = BitmapFactory.decodeByteArray(tempImgData, 0, tempImgData.length);

                // calculate the image height based upon the bitmap data
                newImageHeight = ((newImageWidth * tempBitmap.getHeight()) / tempBitmap.getWidth());

                // display the resized bitmap
                gridImage.setImageBitmap(Bitmap.createScaledBitmap(tempBitmap, newImageWidth, newImageHeight, false));
            }
            else {
                // get the poster URL
                String posterURL = movieParcel.getImagePosterURL();

                // display in picasso
                Picasso
                        .with(mContext)
                        .load(mBaseImgStr + posterURL)
                        .resize(newImageWidth, newImageHeight)
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.no_image_thumb_detail)
                        .into(gridImage);
            }
        }
        catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
