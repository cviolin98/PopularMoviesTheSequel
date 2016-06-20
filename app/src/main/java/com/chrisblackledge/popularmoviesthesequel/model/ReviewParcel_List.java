package com.chrisblackledge.popularmoviesthesequel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"page", "total_pages", "total_results"})
public class ReviewParcel_List implements Parcelable {

    @JsonProperty("id")
    private int mId;

    @JsonProperty("results")
    private List<ReviewParcel_Content> mReviewParcels = new ArrayList<ReviewParcel_Content>();;

    public ReviewParcel_List() {
    }

    public ReviewParcel_List(int id,
                             List<ReviewParcel_Content> reviewParcels) {
        this.mId = id;
        this.mReviewParcels = reviewParcels;
    }

    private ReviewParcel_List(Parcel in) {
        this.mId = in.readInt();
        in.readTypedList(mReviewParcels, ReviewParcel_Content.CREATOR);
    }

    public int getId() {
        return mId;
    }

    public List<ReviewParcel_Content> getReviewParcels() {
        return mReviewParcels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return mId + "--" +
                mReviewParcels.size();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeTypedList(mReviewParcels);
    }

    public static final Creator<ReviewParcel_List> CREATOR = new Creator<ReviewParcel_List>() {
        @Override
        public ReviewParcel_List createFromParcel(Parcel parcel) {
            return new ReviewParcel_List(parcel);
        }

        @Override
        public ReviewParcel_List[] newArray(int i) {
            return new ReviewParcel_List[i];
        }
   };
}