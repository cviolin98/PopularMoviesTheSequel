package com.chrisblackledge.popularmoviesthesequel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.chrisblackledge.popularmoviesthesequel.adapter.TrailerListAdapter;
import com.chrisblackledge.popularmoviesthesequel.model.MovieParcel_Content;
import com.chrisblackledge.popularmoviesthesequel.model.VideoParcel_Content;
import com.chrisblackledge.popularmoviesthesequel.model.VideoParcel_List;
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
 * Fragment to display the list of trailers for a selected movie
 */
public class TrailerActivityFragment extends Fragment {

    // butterknife binding for views
    @BindView(R.id.listView) ListView mTrailerListView = null;
    @BindView(R.id.listViewEmpty) TextView mTrailerListEmpty = null;

    // butterknife unbinder
    private Unbinder mUnbinder = null;

    // list adapter to display the list of trailers
    private TrailerListAdapter mTrailerListAdapter = null;

    // arrayList of VideoParcel_Content data of all trailers
    private List<VideoParcel_Content> mTrailerList = new ArrayList<VideoParcel_Content>();

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

    public TrailerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_trailer, container, false);

        // get xml strings
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

        // get the trailers
        getTrailers();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set the context here...  getActivity() is often null when restored
        mContext = getActivity();
    }

    public void displayTrailerList() {
        if(mTrailerListView != null) {

            // set up the adapter
            mTrailerListAdapter = new TrailerListAdapter(mContext, mTrailerList);

            // add the adapter
            mTrailerListView.setAdapter(mTrailerListAdapter);

            // add a click listener to be able to watch the trailer videos
            mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // get the movie key
                String movieKey = mTrailerList.get(position).getKey();

                // create implicit intent
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + movieKey)));
                }
            });
        }
    }

    public void getTrailers() {

        GetAdapter ga = GetAdapter.retrofit.create(GetAdapter.class);
        Call<VideoParcel_List> call = ga.getMovieTrailers(mMovieId, mAPIKey);

        call.enqueue(new Callback<VideoParcel_List>() {
            @Override
            public void onResponse(Call<VideoParcel_List> call, Response<VideoParcel_List> response) {

                // get the list of trailers
                mTrailerList = response.body().getVideoParcels();

                // if no trailers, then show the message indicating there are no trailers as opposed
                // to just a blank list
                if(mTrailerList.size() == 0) {
                    mTrailerListEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    // now that we have the trailers, display them
                    displayTrailerList();
                }
            }

            @Override
            public void onFailure(Call<VideoParcel_List> call, Throwable t) {
            }
        });
    }
}
