package com.chrisblackledge.popularmoviesthesequel.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.chrisblackledge.popularmoviesthesequel.MainActivityFragment;
import com.chrisblackledge.popularmoviesthesequel.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class GetImageSizeTask extends AsyncTask<Void, Void, Void> {

    private MainActivityFragment mActivityFragment = null;
    private String mBaseImgStr = "";

    public GetImageSizeTask(MainActivityFragment activityFragment) {
        this.mActivityFragment = activityFragment;
        mBaseImgStr = mActivityFragment.getString(R.string.api_base_image_url);
    }

    protected Void doInBackground(Void... voids) {

        try {
            // now that we have the data via Retrofit2, determine the dimensions of an image to be able to
            // resize the images to display within the gridView without any gaps
            final Bitmap image;

            try {
                // find the first posterURL that is not null
                for(int i = 0; i < mActivityFragment.getMovieListSize(); i++) {

                    String posterURL = mActivityFragment.getPosterURL(i);

                    if((posterURL != null) && (!posterURL.isEmpty()) && (!posterURL.equals("null"))) {
                        // load an image to retrieve the dimensions
                        image = Picasso
                            .with(mActivityFragment.getActivity())
                            .load(mBaseImgStr + mActivityFragment.getPosterURL(i)).get();

                        mActivityFragment.setImageDimensions(image.getWidth(), image.getHeight());

                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        mActivityFragment.displayMovies();
    }
}