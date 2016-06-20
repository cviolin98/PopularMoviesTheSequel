package com.chrisblackledge.popularmoviesthesequel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chrisblackledge.popularmoviesthesequel.R;
import com.chrisblackledge.popularmoviesthesequel.model.VideoParcel_Content;

import java.util.List;

import butterknife.ButterKnife;

public class TrailerListAdapter extends ArrayAdapter<VideoParcel_Content> {
    private LayoutInflater mInflater;

    public TrailerListAdapter(Context context, List<VideoParcel_Content> trailerTitles) {
        super(context, R.layout.trailer_list_item, trailerTitles);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get the parcel data
        VideoParcel_Content videoParcel = getItem(position);

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.trailer_list_item, parent, false);
        }

        // get the video name
        String videoName = videoParcel.getName();

        // use butterknife to bind to the view field
        TextView trailerTitleView = ButterKnife.findById(convertView, R.id.trailer_name);

        // set the data
        trailerTitleView.setText(videoName);

        return convertView;
    }
}
