package com.chrisblackledge.popularmoviesthesequel;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.chrisblackledge.popularmoviesthesequel.adapter.ReviewListAdapter;
import com.chrisblackledge.popularmoviesthesequel.model.ReviewParcel_Content;
import com.chrisblackledge.popularmoviesthesequel.model.ReviewParcel_List;
import com.chrisblackledge.popularmoviesthesequel.interfaces.GetAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment to display the reviews for a selected movie
 */
public class ReviewActivityFragment extends Fragment {

    // butterknife binding for views
    @BindView(R.id.reviewListView) ListView mReviewListView = null;
    @BindView(R.id.reviewListEmpty) TextView mReviewListEmpty = null;

    // butterknife unbinder
    private Unbinder mUnbinder = null;

    // list adapter to display the list of reviews
    private ReviewListAdapter mReviewListAdapter = null;

    // arrayList of ReviewParcel_Content data of all reviews
    private List<ReviewParcel_Content> mReviewList = new ArrayList<ReviewParcel_Content>();

    // if 2pane
    private boolean mIsTwoPane = false;

    // activity member variable
    private Context mContext = null;

    // movie id
    private int mMovieId = 0;

    // api key
    private String mAPIKey = "";

    // argument string for 2pane layout
    private String mTwoPaneStr = "";

    // argument and intent string for movie id
    private String mMovieIdStr = "";

    public ReviewActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_review, container, false);

        // get the xml strings
        mAPIKey = getString(R.string.api_key);
        mTwoPaneStr = getString(R.string.arguments_two_pane);
        mMovieIdStr = getString(R.string.arguments_movie_id);

        // set the butterknife unbinder
        mUnbinder = ButterKnife.bind(this, returnView);

        // get the arguments
        Bundle arguments = getArguments();

        // get the movie id and if 2pane or not
        if (arguments != null) {
            int twoPane = arguments.getInt(mTwoPaneStr);
            mIsTwoPane = ((twoPane == 1) ? true : false);

            // get the movie id from the arguments passed in
            mMovieId = arguments.getInt(mMovieIdStr);
        }

        return returnView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // unbind butterknife
        mUnbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();

        // if in a single pane layout, get the movie id from the intent data
        if(!mIsTwoPane) {
            mMovieId = getActivity().getIntent().getIntExtra(mMovieIdStr, 0);
        }

        // get the reviews
        getReviews();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set the context here...  getActivity() is often null when restored
        mContext = getActivity();
    }

    public void displayReviewList() {
        if(mReviewListView != null) {

            // set up the adapter
            mReviewListAdapter = new ReviewListAdapter(mContext, mReviewList);

            // add the adapter
            mReviewListView.setAdapter(mReviewListAdapter);
        }
    }

    public void getReviews() {

        GetAdapter ga = GetAdapter.retrofit.create(GetAdapter.class);
        Call<ReviewParcel_List> call = ga.getMovieReviews(mMovieId, mAPIKey);

        call.enqueue(new Callback<ReviewParcel_List>() {
            @Override
            public void onResponse(Call<ReviewParcel_List> call, Response<ReviewParcel_List> response) {
                mReviewList = response.body().getReviewParcels();

                // if no reviews, then show the message indicating there are no reviews as opposed
                // to just a blank list
                if(mReviewList.size() == 0) {
                    mReviewListEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    // now that we have the reviews, display them
                    displayReviewList();
                }
            }

            @Override
            public void onFailure(Call<ReviewParcel_List> call, Throwable t) {
            }
        });
    }
}
