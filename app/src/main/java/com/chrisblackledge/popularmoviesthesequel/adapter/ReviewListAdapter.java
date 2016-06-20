package com.chrisblackledge.popularmoviesthesequel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chrisblackledge.popularmoviesthesequel.R;
import com.chrisblackledge.popularmoviesthesequel.model.ReviewParcel_Content;

import java.util.List;

import butterknife.ButterKnife;

public class ReviewListAdapter extends ArrayAdapter<ReviewParcel_Content> {
    private LayoutInflater mInflater;

    public ReviewListAdapter(Context context, List<ReviewParcel_Content> movieReviews) {
        super(context, R.layout.review_list_item, movieReviews);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get the parcel data
        ReviewParcel_Content reviewParcel = getItem(position);

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.review_list_item, parent, false);
        }

        // get the author
        String author = reviewParcel.getAuthor();

        // get the content
        String content = reviewParcel.getContent();

        // use butterknife to bind to the view fields
        TextView authorView = ButterKnife.findById(convertView, R.id.review_author);
        TextView contentView = ButterKnife.findById(convertView, R.id.review_content);

        // display the data
        authorView.setText("Review by: " + author);
        contentView.setText(content);

        return convertView;
    }
}
